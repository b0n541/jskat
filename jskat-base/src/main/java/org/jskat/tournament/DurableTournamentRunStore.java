package org.jskat.tournament;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.*;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.*;

/**
 * Locked, durable local ext4 run store. Every acknowledged publication includes directory fsync.
 */
public final class DurableTournamentRunStore implements TournamentRunStore {
    private final Path root;
    private final FileChannel channel;
    private final FileLock lock;
    private final UUID runId;
    private ResolvedTournamentConfiguration configuration;
    private List<ScheduledTournamentGame> schedule;
    private Map<String, ScheduledTournamentGame> scheduleById;
    private boolean closed, owner;
    private com.fasterxml.jackson.databind.node.ObjectNode activeOwner;
    private TournamentPublicationIO io = TournamentPublicationIO.LOCAL;
    private final Map<String, TournamentGameResult> committed = new LinkedHashMap<>();
    private final Map<String, String> checksums = new LinkedHashMap<>();
    private final Map<UUID, JsonNode> sessions = new HashMap<>();
    private final Set<UUID> attempts = new HashSet<>();
    private final Set<UUID> endedSessions = new HashSet<>();

    private DurableTournamentRunStore(Path root, FileChannel channel, FileLock lock, UUID runId) {
        this.root = root;
        this.channel = channel;
        this.lock = lock;
        this.runId = runId;
    }

    public static DurableTournamentRunStore create(
            TournamentConfiguration requested,
            long seed,
            TournamentBuildIdentity build,
            TournamentInputResolver resolver,
            CancellationToken cancellation)
            throws IOException {
        return create(
                requested, seed, build, resolver, cancellation, TournamentPublicationIO.LOCAL);
    }

    public static DurableTournamentRunStore create(
            TournamentConfiguration requested,
            long seed,
            TournamentBuildIdentity build,
            TournamentInputResolver resolver,
            CancellationToken cancellation,
            TournamentPublicationIO io)
            throws IOException {
        Objects.requireNonNull(io);
        var reread =
                new TournamentConfigurationReader()
                        .read(requested.originalJson(), requested.configurationFile());
        if (!reread.valid()
                || !TournamentIdentities.requestedDigest(reread.value())
                        .equals(TournamentIdentities.requestedDigest(requested)))
            throw new IOException("Original configuration conflicts with requested identity");
        Path root = requested.output().absolute();
        if (attributes(root) != null) throw new FileAlreadyExistsException(root.toString());
        if (requested.seed().isPresent() && requested.seed().orElseThrow() != seed)
            throw new IOException("Effective seed conflicts with explicit root seed");
        checkAncestors(root.getParent());
        Path existingParent = root.getParent();
        while (attributes(existingParent) == null) existingParent = existingParent.getParent();
        checkFilesystem(existingParent);
        // Full read-only source preflight is repeated under the new run's lock during prepare.
        var preflight = resolver.validate(requested, cancellation);
        if (!preflight.valid())
            throw new IOException("Tournament source preflight failed: " + preflight.issues());
        if (cancellation.isCancellationRequested())
            throw new TournamentCancellationException("Preparation cancelled");

        createParents(root.getParent());
        Files.createDirectory(root);
        forceDirectory(root.getParent());
        FileChannel channel = FileChannel.open(root.resolve(".lock"), CREATE_NEW, WRITE);
        FileLock lock = null;
        DurableTournamentRunStore store = null;
        try {
            lock = acquire(channel);
            checkFilesystem(root);
            store = new DurableTournamentRunStore(root, channel, lock, UUID.randomUUID());
            store.io = io;
            for (String dir :
                    List.of(
                            "inputs",
                            "inputs/models",
                            "games",
                            "failed-attempts",
                            "sessions",
                            "diagnostics",
                            "lock-history",
                            "reports")) store.mkdir(dir);
            store.takeOwnership(false);
            if (cancellation.isCancellationRequested())
                throw new TournamentCancellationException("Preparation cancelled");
            var resolved =
                    resolver.prepare(
                            requested, seed, build, root.resolve("inputs/models"), cancellation);
            if (!resolved.valid())
                throw new IOException("Input resolution failed: " + resolved.issues());
            store.configuration = resolved.value();
            if (!store.configuration.requested().equals(requested)
                    || store.configuration.effectiveSeed() != seed
                    || !store.configuration.build().equals(build))
                throw new IOException("Resolver returned conflicting inputs");
            store.validateSnapshots();
            store.installSchedule(
                    TournamentSchedules.generate(
                            requested.competitors().stream().map(TournamentCompetitor::id).toList(),
                            requested.epochs(),
                            seed));
            store.publish(
                    "inputs/original-config.json",
                    requested.originalJson().getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    false);
            store.record(
                    "inputs/requested.json",
                    "requested",
                    CanonicalJson.parse(TournamentIdentities.requestedCanonicalJson(requested)));
            store.record(
                    "inputs/resolved.json",
                    "resolved",
                    CanonicalJson.parse(
                            TournamentIdentities.resolvedCanonicalJson(store.configuration)));
            var provenance = CanonicalJson.object();
            provenance.put("configurationFile", requested.configurationFile().toString());
            store.record("inputs/provenance.json", "provenance", provenance);
            var input = CanonicalJson.object();
            input.put("scheduleVersion", TournamentSchedules.VERSION);
            input.put("deckVersion", TournamentSchedules.DECK_VERSION);
            input.set("games", TournamentRecords.tree(store.schedule));
            store.record("inputs/schedule.json", "schedule", input);
            var verified = resolver.verify(store.configuration, cancellation);
            if (!verified.valid())
                throw new IOException("Snapshot verification failed: " + verified.issues());
            store.validateSnapshots();
            try (var dirs = Files.walk(root.resolve("inputs"))) {
                for (Path p : dirs.sorted(Comparator.reverseOrder()).toList())
                    if (attributes(p).isDirectory()) forceDirectory(p);
            }
            var ready = CanonicalJson.object();
            ready.put("runFormat", 1);
            ready.set("algorithms", algorithmVersions());
            ready.set("inventory", store.inventory(true));
            store.record("ready.json", "ready", ready);
            return store;
        } catch (Throwable e) {
            if (store != null) {
                try {
                    store.close();
                } catch (Throwable close) {
                    e.addSuppressed(close);
                }
            } else {
                if (lock != null) lock.release();
                channel.close();
            }
            throw rethrow(e);
        }
    }

    /** Tolerant read-only inspection. Diagnostics never grant execution or completion authority. */
    public record Inspection(
            Path directory,
            Optional<UUID> runId,
            Optional<TournamentConfiguration> requested,
            List<String> diagnostics) {
        public Inspection {
            diagnostics = List.copyOf(diagnostics);
        }
    }

    public static Inspection inspect(Path directory) {
        Path root = directory.toAbsolutePath().normalize();
        var diagnostics = new ArrayList<String>();
        UUID run = null;
        TournamentConfiguration requested = null;
        try {
            checkAncestors(root);
            var ready = CanonicalJson.parse(decodeUtf8(readRegular(root.resolve("ready.json"))));
            run = UUID.fromString(ready.path("runId").asText());
            TournamentRecords.decode(readRegular(root.resolve("ready.json")), run, "ready");
            var provenance =
                    TournamentRecords.decode(
                            readRegular(root.resolve("inputs/provenance.json")), run, "provenance");
            TournamentRecords.fields(provenance, "configurationFile");
            var config =
                    new TournamentConfigurationReader()
                            .read(
                                    decodeUtf8(
                                            readRegular(
                                                    root.resolve("inputs/original-config.json"))),
                                    Path.of(provenance.path("configurationFile").asText()));
            if (!config.valid())
                throw new IOException(
                        "Invalid retained original configuration: " + config.issues());
            requested = config.value();
            if (!requested.output().absolute().equals(root))
                throw new IOException(
                        "Run directory differs from retained output; moving runs is unsupported");
        } catch (Exception e) {
            diagnostics.add(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
        return new Inspection(
                root, Optional.ofNullable(run), Optional.ofNullable(requested), diagnostics);
    }

    public static Verification verify(
            Path directory,
            TournamentBuildIdentity build,
            TournamentInputResolver resolver,
            CancellationToken cancellation)
            throws IOException {
        var inspection = inspect(directory);
        if (!inspection.diagnostics().isEmpty() || inspection.requested().isEmpty())
            throw new IOException("Retained run is not verifiable: " + inspection.diagnostics());
        return verify(inspection.requested().orElseThrow(), build, resolver, cancellation);
    }

    private static String decodeUtf8(byte[] bytes) throws IOException {
        return java.nio.charset.StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
                .onUnmappableCharacter(java.nio.charset.CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(bytes))
                .toString();
    }

    public static DurableTournamentRunStore resume(
            TournamentConfiguration requested,
            TournamentBuildIdentity build,
            TournamentInputResolver resolver,
            boolean recover,
            CancellationToken cancellation)
            throws IOException {
        var store = openExisting(requested, build, resolver, cancellation);
        try {
            store.takeOwnership(recover);
            return store;
        } catch (Throwable e) {
            try {
                store.close();
            } catch (Throwable c) {
                e.addSuppressed(c);
            }
            throw rethrow(e);
        }
    }

    private static DurableTournamentRunStore openExisting(
            TournamentConfiguration requested,
            TournamentBuildIdentity build,
            TournamentInputResolver resolver,
            CancellationToken cancellation)
            throws IOException {
        Path root = requested.output().absolute();
        checkAncestors(root);
        checkFilesystem(root);
        FileChannel channel = FileChannel.open(root.resolve(".lock"), WRITE, NOFOLLOW_LINKS);
        FileLock lock = null;
        DurableTournamentRunStore store = null;
        try {
            lock = acquire(channel);
            var envelope =
                    CanonicalJson.parse(
                            new String(
                                    readRegular(root.resolve("ready.json")),
                                    java.nio.charset.StandardCharsets.UTF_8));
            UUID id = UUID.fromString(envelope.path("runId").asText());
            store = new DurableTournamentRunStore(root, channel, lock, id);
            var ready = store.read("ready.json", "ready");
            TournamentRecords.fields(ready, "runFormat", "algorithms", "inventory");
            if (!ready.get("algorithms").equals(algorithmVersions()))
                throw new IOException("Unsupported retained algorithm versions");
            if (!ready.path("runFormat").isIntegralNumber()
                    || !ready.path("runFormat").canConvertToInt()
                    || ready.path("runFormat").intValue() != 1)
                throw new IOException("Unsupported run format");
            if (!ready.get("inventory").equals(store.inventory(false)))
                throw new IOException("Retained input inventory mismatch");
            var provenance = store.read("inputs/provenance.json", "provenance");
            TournamentRecords.fields(provenance, "configurationFile");
            var original =
                    new TournamentConfigurationReader()
                            .read(
                                    decodeUtf8(
                                            readRegular(
                                                    root.resolve("inputs/original-config.json"))),
                                    Path.of(provenance.path("configurationFile").asText()));
            if (!original.valid()
                    || !TournamentIdentities.requestedDigest(original.value())
                            .equals(TournamentIdentities.requestedDigest(requested)))
                throw new IOException(
                        "Original configuration conflicts with retained requested identity");
            var retainedRequest = store.read("inputs/requested.json", "requested");
            if (!retainedRequest.equals(
                    CanonicalJson.parse(TournamentIdentities.requestedCanonicalJson(requested))))
                throw new IOException("Requested configuration digest/output/seed conflict");
            var resolved = store.read("inputs/resolved.json", "resolved");
            TournamentRecords.fields(
                    resolved,
                    "encoding",
                    "requestedDigest",
                    "effectiveSeed",
                    "build",
                    "gameCount",
                    "competitors");
            if (!resolved.path("encoding").asText().equals("jskat-resolved-v1")
                    || !resolved.path("build").asText().equals(build.identity()))
                throw new IOException("Unsupported resolved encoding or exact build mismatch");
            var roster = new ArrayList<ResolvedTournamentCompetitor>();
            if (!resolved.path("competitors").isArray())
                throw new IOException("Invalid resolved roster");
            for (var entry : resolved.get("competitors")) {
                var competitor =
                        requested.competitors().stream()
                                .filter(c -> c.id().toString().equals(entry.path("id").asText()))
                                .findFirst()
                                .orElseThrow(() -> new IOException("Unknown retained competitor"));
                var artifacts = new ArrayList<TournamentArtifact>();
                Path snapshot = null;
                if (competitor.implementation().modelRequired()) {
                    TournamentRecords.fields(
                            entry,
                            "id",
                            "implementation",
                            "class",
                            "fingerprint",
                            "releaseLabel",
                            "snapshot",
                            "artifacts");
                    snapshot =
                            Path.of(entry.path("snapshot").asText()).toAbsolutePath().normalize();
                    if (!snapshot.equals(
                            root.resolve("inputs/models")
                                    .resolve(entry.path("fingerprint").asText())))
                        throw new IOException("Snapshot location escapes retained inputs");
                    for (var artifact : entry.path("artifacts"))
                        artifacts.add(TournamentRecords.typed(artifact, TournamentArtifact.class));
                } else TournamentRecords.fields(entry, "id", "implementation", "class");
                if (!entry.path("class").asText().equals(competitor.implementation().className()))
                    throw new IOException("Concrete implementation class mismatch");
                roster.add(
                        new ResolvedTournamentCompetitor(
                                competitor,
                                entry.path("class").asText(),
                                entry.has("releaseLabel")
                                        ? entry.get("releaseLabel").asText()
                                        : null,
                                entry.has("fingerprint") ? entry.get("fingerprint").asText() : null,
                                snapshot,
                                artifacts));
            }
            store.configuration =
                    new ResolvedTournamentConfiguration(
                            requested,
                            Long.parseUnsignedLong(resolved.path("effectiveSeed").asText()),
                            build,
                            roster);
            if (!resolved.equals(
                    CanonicalJson.parse(
                            TournamentIdentities.resolvedCanonicalJson(store.configuration))))
                throw new IOException("Resolved identity is inconsistent");
            var checked = resolver.verify(store.configuration, cancellation);
            if (!checked.valid())
                throw new IOException("Retained snapshot verification failed: " + checked.issues());
            store.installSchedule(
                    TournamentSchedules.generate(
                            requested.competitors().stream().map(TournamentCompetitor::id).toList(),
                            requested.epochs(),
                            store.configuration.effectiveSeed()));
            var retained = store.read("inputs/schedule.json", "schedule");
            TournamentRecords.fields(retained, "scheduleVersion", "deckVersion", "games");
            if (!retained.path("scheduleVersion").asText().equals(TournamentSchedules.VERSION)
                    || !retained.path("deckVersion")
                            .asText()
                            .equals(TournamentSchedules.DECK_VERSION)
                    || !CanonicalJson.encode(retained.get("games"))
                            .equals(CanonicalJson.encode(TournamentRecords.tree(store.schedule))))
                throw new IOException("Unsupported or inconsistent exact schedule/decks/seeds");
            store.validateSnapshots();
            store.validateLayout();
            store.loadHistory();
            return store;
        } catch (Throwable e) {
            if (e instanceof RuntimeException)
                e = new IOException("Invalid retained run " + root + ": " + e.getMessage(), e);
            if (store != null) {
                try {
                    store.close();
                } catch (Throwable c) {
                    e.addSuppressed(c);
                }
            } else {
                if (lock != null) lock.release();
                channel.close();
            }
            throw rethrow(e);
        }
    }

    private void validateSnapshots() throws IOException {
        var expected =
                new HashSet<String>(
                        List.of(
                                "inputs/original-config.json",
                                "inputs/requested.json",
                                "inputs/resolved.json",
                                "inputs/provenance.json",
                                "inputs/schedule.json"));
        for (var c : configuration.competitors())
            if (c.snapshot() != null) {
                if (!c.snapshot().equals(root.resolve("inputs/models").resolve(c.fingerprint())))
                    throw new IOException("Snapshot path conflicts with run layout");
                expected.add("inputs/models/" + c.fingerprint() + "/models.json");
                for (var a : c.artifacts())
                    expected.add("inputs/models/" + c.fingerprint() + "/" + a.name());
            }
        try (var paths = Files.walk(root.resolve("inputs"))) {
            for (Path p : paths.toList())
                if (!attributes(p).isDirectory()) {
                    String name = root.relativize(p).toString().replace('\\', '/');
                    if (!expected.contains(name))
                        throw new IOException("Unexpected retained input: " + name);
                }
        }
    }

    private void validateLayout() throws IOException {
        Set<String> top =
                Set.of(
                        ".lock",
                        "lock-owner.json",
                        "ready.json",
                        "inputs",
                        "games",
                        "failed-attempts",
                        "sessions",
                        "diagnostics",
                        "lock-history",
                        "reports");
        try (var paths = Files.walk(root)) {
            for (Path p : paths.toList()) {
                if (p.equals(root)) continue;
                String relative = root.relativize(p).toString().replace('\\', '/');
                var a = attributes(p);
                if (!a.isDirectory() && !a.isRegularFile())
                    throw new IOException("Unsafe run entry: " + relative);
                if (!top.contains(relative.split("/")[0])
                        && !p.getFileName().toString().matches("\\.tmp-[0-9a-f-]{36}-.+"))
                    throw new IOException("Unexpected authoritative entry: " + relative);
                if (relative.startsWith("inputs/")
                        && p.getFileName().toString().startsWith(".tmp-"))
                    throw new IOException("Uninventoried retained temporary input: " + relative);
            }
        }
    }

    private List<Path> files(String directory) throws IOException {
        try (var paths = Files.list(safe(directory))) {
            return paths.sorted().toList();
        }
    }

    private boolean temporary(Path path) {
        return path.getFileName().toString().matches("\\.tmp-[0-9a-f-]{36}-.+");
    }

    private void loadHistory() throws IOException {
        var ends = new HashSet<UUID>();
        for (Path p : files("sessions")) {
            if (temporary(p)) continue;
            String name = p.getFileName().toString();
            boolean start = name.endsWith(".start.json");
            if (!start && !name.endsWith(".end.json"))
                throw new IOException("Unexpected session record: " + p);
            String idText = name.substring(0, name.length() - (start ? 11 : 9));
            UUID id = parseUuid(idText, p.toString());
            var value = read("sessions/" + name, start ? "session-start" : "session-end");
            TournamentRecords.fields(
                    value,
                    start
                            ? new String[] {"sessionId", "startedAt", "metadata"}
                            : new String[] {"sessionId", "outcome", "endedAt", "metadata"});
            if (!value.path("sessionId").asText().equals(id.toString()))
                throw new IOException("Session filename/payload mismatch: " + p);
            Instant.parse(value.path(start ? "startedAt" : "endedAt").asText());
            if (start) sessions.put(id, value);
            else {
                if (!Set.of("COMPLETED", "CANCELLED", "FAILED")
                        .contains(value.path("outcome").asText()))
                    throw new IOException("Invalid session outcome");
                ends.add(id);
                endedSessions.add(id);
            }
        }
        for (UUID ended : ends) {
            var terminal = read("sessions/" + ended + ".end.json", "session-end");
            if (sessions.containsKey(ended)
                    && Instant.parse(terminal.path("endedAt").asText())
                            .isBefore(
                                    Instant.parse(sessions.get(ended).path("startedAt").asText())))
                throw new IOException("Session end precedes start: " + ended);
        }
        if (!sessions.keySet().containsAll(ends))
            throw new IOException("Session end without start");
        for (Path p : files("failed-attempts")) {
            if (temporary(p)) continue;
            String name = p.getFileName().toString();
            var failure =
                    TournamentRecords.typed(
                            read("failed-attempts/" + name, "failed-attempt"),
                            TournamentFailedAttempt.class);
            if (!name.equals(failure.attemptId() + ".json"))
                throw new IOException("Attempt filename/payload mismatch: " + p);
            requireAttempt(failure.gameId(), failure.attemptId(), failure.sessionId());
            attempts.add(failure.attemptId());
        }
        for (Path p : files("games")) {
            if (temporary(p)) continue;
            String name = p.getFileName().toString();
            try {
                var payload = read("games/" + name, "game");
                var result = decodeGame(payload);
                if (!name.equals(result.gameId() + ".json"))
                    throw new IOException("Game filename/payload mismatch: " + p);
                validateGame(result, payload);
                committed.put(result.gameId(), result);
                checksums.put(result.gameId(), CanonicalJson.sha256(CanonicalJson.bytes(payload)));
                attempts.add(result.attemptId());
            } catch (IOException | RuntimeException e) {
                throw new IOException("games/" + name + ": " + e.getMessage(), e);
            }
        }
        for (String directory : List.of("diagnostics", "lock-history"))
            for (Path p : files(directory))
                if (!temporary(p)) {
                    String name = p.getFileName().toString();
                    if (!name.matches("[0-9a-f-]{36}\\.json"))
                        throw new IOException("Unexpected history record: " + p);
                    parseUuid(name.substring(0, name.length() - 5), p.toString());
                    var payload =
                            read(
                                    directory + "/" + name,
                                    directory.equals("diagnostics")
                                            ? "diagnostic"
                                            : "lock-recovery");
                    if (directory.equals("diagnostics")) {
                        TournamentRecords.fields(payload, "recordedAt", "detail");
                        Instant.parse(payload.path("recordedAt").asText());
                    } else {
                        TournamentRecords.fields(
                                payload, "previousBase64", "previousSha256", "recoveredAt");
                        byte[] prior =
                                Base64.getDecoder().decode(payload.path("previousBase64").asText());
                        if (!CanonicalJson.sha256(prior)
                                .equals(payload.path("previousSha256").asText()))
                            throw new IOException("Recovery audit checksum mismatch: " + p);
                        Instant.parse(payload.path("recoveredAt").asText());
                    }
                }
    }

    private static UUID parseUuid(String text, String path) throws IOException {
        try {
            var id = UUID.fromString(text);
            if (!id.toString().equals(text)) throw new IllegalArgumentException();
            return id;
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid UUID filename: " + path, e);
        }
    }

    private JsonNode gamePayload(TournamentGameResult result) throws IOException {
        var game = scheduleById.get(result.gameId());
        if (game == null) throw new IOException("Unknown game ID");
        var payload = CanonicalJson.object();
        payload.set("schedule", TournamentRecords.tree(game));
        payload.set("result", TournamentRecords.tree(result));
        return payload;
    }

    private TournamentGameResult decodeGame(JsonNode payload) throws IOException {
        TournamentRecords.fields(payload, "schedule", "result");
        return TournamentRecords.typed(payload.get("result"), TournamentGameResult.class);
    }

    private void validateGame(TournamentGameResult result, JsonNode payload) throws IOException {
        requireAttempt(result.gameId(), result.attemptId(), result.sessionId());
        if (committed.containsKey(result.gameId()))
            throw new IOException("Duplicate committed game");
        if (!result.inputDigest().equals(TournamentIdentities.resolvedDigest(configuration))
                || !CanonicalJson.encode(payload).equals(CanonicalJson.encode(gamePayload(result))))
            throw new IOException("Game input/schedule identity conflict");
        for (var fallback : result.fallbacks()) {
            if (!fallback.gameId().equals(result.gameId())
                    || fallback.error().isBlank()
                    || configuration.competitors().stream()
                            .noneMatch(
                                    c ->
                                            c.requested().id().equals(fallback.competitorId())
                                                    && c.requested()
                                                            .implementation()
                                                            .modelRequired())
                    || scheduleById.get(result.gameId()).seats().stream()
                            .noneMatch(seat -> seat.competitorId().equals(fallback.competitorId())))
                throw new IOException("Fallback game/competitor linkage is invalid");
            if (result.events().stream()
                    .noneMatch(
                            event ->
                                    event.type().equals("phase")
                                            && CanonicalJson.parse(event.payloadJson())
                                                    .path("value")
                                                    .asText()
                                                    .equals(fallback.phase())))
                throw new IOException("Fallback references an absent game phase");
        }
        // Pure replay through the same game core; checksums alone never establish legality.
        verifyLegalResult(scheduleById.get(result.gameId()), result);
    }

    private static void verifyLegalResult(ScheduledTournamentGame game, TournamentGameResult result)
            throws IOException {
        try {
            new TournamentGameCore().verifyResult(game, result);
        } catch (RuntimeException e) {
            throw new IOException(
                    "Illegal committed game " + game.gameId() + ": " + e.getMessage(), e);
        }
    }

    private void installSchedule(List<ScheduledTournamentGame> games) {
        schedule = games;
        var index = new HashMap<String, ScheduledTournamentGame>();
        for (var game : games)
            if (index.put(game.gameId(), game) != null)
                throw new IllegalArgumentException("Duplicate schedule ID");
        scheduleById = Map.copyOf(index);
    }

    public Path runDirectory() {
        return root;
    }

    public synchronized List<ScheduledTournamentGame> pendingGames() throws IOException {
        ensureOpen();
        return schedule.stream().filter(game -> !committed.containsKey(game.gameId())).toList();
    }

    /** Canonical copied history for audit UI and summary writers. */
    public synchronized String historyJson() throws IOException {
        ensureOpen();
        var history = CanonicalJson.object();
        for (String directory :
                List.of("sessions", "failed-attempts", "diagnostics", "lock-history")) {
            var records = history.putArray(directory);
            for (Path path : files(directory))
                if (!temporary(path)) {
                    String name = path.getFileName().toString();
                    String type =
                            switch (directory) {
                                case "sessions" ->
                                        name.endsWith(".start.json")
                                                ? "session-start"
                                                : "session-end";
                                case "failed-attempts" -> "failed-attempt";
                                case "diagnostics" -> "diagnostic";
                                default -> "lock-recovery";
                            };
                    var entry = records.addObject();
                    entry.put("file", directory + "/" + name);
                    entry.set("payload", read(directory + "/" + name, type));
                }
        }
        return CanonicalJson.encode(history);
    }

    private static JsonNode algorithmVersions() {
        var versions = CanonicalJson.object();
        versions.put("canonicalEncoding", 1);
        versions.put("artifactFingerprint", TournamentIdentities.ARTIFACT_ENCODING);
        versions.put("seedDerivation", TournamentIdentities.SEED_ENCODING);
        versions.put("schedule", TournamentSchedules.VERSION);
        versions.put("deckRng", TournamentSchedules.DECK_VERSION);
        versions.put("gameAudit", "jskat-game-audit-v1");
        versions.put("resultRules", "standard-skat-seeger-fabian-v1");
        versions.put("ratingExport", TournamentReports.VERSION);
        versions.put("rating", TournamentRatings.VERSION);
        versions.put("bootstrap", TournamentRatings.BOOTSTRAP_VERSION);
        versions.put("predictiveDiagnostics", TournamentReports.DIAGNOSTICS_VERSION);
        return versions;
    }

    private static IOException rethrow(Throwable e) throws IOException {
        if (e instanceof IOException i) return i;
        if (e instanceof RuntimeException r) throw r;
        if (e instanceof Error error) throw error;
        return new IOException(e);
    }

    private static FileLock acquire(FileChannel channel) throws IOException {
        try {
            var lock = channel.tryLock();
            if (lock == null) throw new IOException("Run is busy: live OS lock");
            return lock;
        } catch (OverlappingFileLockException e) {
            throw new IOException("Run is busy: live OS lock", e);
        }
    }

    /** Read-only profile and ancestor suitability; does not create or force any file. */
    public static void validateOutputFilesystem(Path output) throws IOException {
        checkAncestors(output.getParent());
        Path ancestor = output.getParent();
        while (attributes(ancestor) == null) ancestor = ancestor.getParent();
        checkFilesystemProfile(ancestor);
    }

    private static void checkFilesystem(Path root) throws IOException {
        checkFilesystemProfile(root);
        forceDirectory(root);
    }

    private static void checkFilesystemProfile(Path root) throws IOException {
        var store = Files.getFileStore(root);
        if (!System.getProperty("os.name").equals("Linux")
                || !root.getFileSystem().provider().getScheme().equals("file")
                || !store.type().equals("ext4"))
            throw new IOException(
                    "Unsupported filesystem for durable tournaments: " + store.type());
    }

    private static void checkAncestors(Path path) throws IOException {
        for (Path p = path; p != null; p = p.getParent()) {
            var a = attributes(p);
            if (a != null && !a.isDirectory()) throw new IOException("Unsafe directory: " + p);
        }
    }

    private static BasicFileAttributes attributes(Path path) throws IOException {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class, NOFOLLOW_LINKS);
        } catch (NoSuchFileException e) {
            return null;
        }
    }

    private static void createParents(Path path) throws IOException {
        if (attributes(path) != null) return;
        createParents(path.getParent());
        Files.createDirectory(path);
        forceDirectory(path.getParent());
    }

    private static void forceDirectory(Path path) throws IOException {
        try (var c = FileChannel.open(path, READ)) {
            c.force(true);
        }
    }

    private void mkdir(String relative) throws IOException {
        var p = root.resolve(relative);
        Files.createDirectory(p);
        forceDirectory(p.getParent());
    }

    private Path safe(String relative) throws IOException {
        Path p = root.resolve(relative).normalize();
        if (!p.startsWith(root)
                || p.equals(root)
                || relative.contains("\\")
                || Path.of(relative).isAbsolute())
            throw new IOException("Unsafe run path: " + relative);
        checkAncestors(p.getParent());
        return p;
    }

    private synchronized void publish(String relative, byte[] bytes, boolean replace)
            throws IOException {
        ensureOpen();
        Path target = safe(relative);
        if (!replace && attributes(target) != null)
            throw new FileAlreadyExistsException(target.toString());
        Path temp = target.resolveSibling(".tmp-" + UUID.randomUUID() + "-" + target.getFileName());
        try (var c = FileChannel.open(temp, CREATE_NEW, WRITE)) {
            var b = ByteBuffer.wrap(bytes);
            while (b.hasRemaining()) c.write(b);
            io.forceFile(c);
        }
        if (!Arrays.equals(Files.readAllBytes(temp), bytes))
            throw new IOException("Temporary publication verification failed: " + temp);
        if (!replace && attributes(target) != null)
            throw new FileAlreadyExistsException(target.toString());
        io.atomicMove(temp, target, replace);
        io.forceDirectory(target.getParent());
    }

    private void record(String relative, String type, JsonNode payload) throws IOException {
        byte[] bytes = TournamentRecords.encode(runId, type, payload);
        TournamentRecords.decode(bytes, runId, type);
        publish(relative, bytes, false);
    }

    private JsonNode read(String relative, String type) throws IOException {
        try {
            return TournamentRecords.decode(readRegular(safe(relative)), runId, type);
        } catch (IOException e) {
            throw new IOException(relative + ": " + e.getMessage(), e);
        }
    }

    private static byte[] readRegular(Path p) throws IOException {
        var a = attributes(p);
        if (a == null || !a.isRegularFile()) throw new IOException("Expected regular file: " + p);
        return Files.readAllBytes(p);
    }

    private void ensureOpen() throws IOException {
        if (closed || !lock.isValid()) throw new IOException("Run store is closed");
    }

    private JsonNode inventory(boolean force) throws IOException {
        var values = CanonicalJson.object().putArray("files");
        try (var paths = Files.walk(root.resolve("inputs"))) {
            for (Path p : paths.sorted().toList()) {
                var a = attributes(p);
                if (a.isDirectory()) {
                    if (force) forceDirectory(p);
                    continue;
                }
                if (!a.isRegularFile()) throw new IOException("Unsafe retained input: " + p);
                if (force)
                    try (var c = FileChannel.open(p, WRITE)) {
                        c.force(true);
                    }
                byte[] bytes = readRegular(p);
                var entry = values.addObject();
                entry.put("path", root.relativize(p).toString().replace('\\', '/'));
                entry.put("size", bytes.length);
                entry.put("sha256", CanonicalJson.sha256(bytes));
            }
        }
        return values;
    }

    private void takeOwnership(boolean recover) throws IOException {
        Path pointer = root.resolve("lock-owner.json");
        if (attributes(pointer) != null) {
            byte[] previous = readRegular(pointer);
            boolean released = false;
            try {
                var value = TournamentRecords.decode(previous, runId, "lock-owner");
                validateOwner(value);
                released = value.path("released").booleanValue();
            } catch (IOException ignored) {
            }
            if (!released) {
                if (!recover)
                    throw new TournamentRecoveryRequiredException("Stale lock metadata requires explicit recovery");
                var audit = CanonicalJson.object();
                audit.put("previousBase64", Base64.getEncoder().encodeToString(previous));
                audit.put("previousSha256", CanonicalJson.sha256(previous));
                audit.put("recoveredAt", Instant.now().toString());
                record("lock-history/" + UUID.randomUUID() + ".json", "lock-recovery", audit);
            }
        }
        var value = CanonicalJson.object();
        value.put("ownerId", UUID.randomUUID().toString());
        value.put("pid", ProcessHandle.current().pid());
        value.put("hostname", System.getenv().getOrDefault("HOSTNAME", "unknown"));
        value.put(
                "processStartedAt",
                ProcessHandle.current().info().startInstant().map(Instant::toString).orElse(""));
        value.put("acquiredAt", Instant.now().toString());
        value.put("releasedAt", "");
        value.put("released", false);
        publish("lock-owner.json", TournamentRecords.encode(runId, "lock-owner", value), true);
        owner = true;
        activeOwner = value;
    }

    private static void validateOwner(JsonNode value) throws IOException {
        TournamentRecords.fields(
                value,
                "ownerId",
                "pid",
                "hostname",
                "processStartedAt",
                "acquiredAt",
                "released",
                "releasedAt");
        parseUuid(value.path("ownerId").asText(), "lock-owner.json");
        if (!value.path("pid").isIntegralNumber()
                || !value.path("pid").canConvertToLong()
                || value.path("pid").longValue() <= 0
                || !value.path("released").isBoolean())
            throw new IOException("Invalid lock-owner fields");
        try {
            Instant.parse(value.path("acquiredAt").asText());
            if (!value.path("processStartedAt").asText().isEmpty())
                Instant.parse(value.path("processStartedAt").asText());
            if (value.path("released").booleanValue())
                Instant.parse(value.path("releasedAt").asText());
        } catch (java.time.DateTimeException e) {
            throw new IOException("Invalid lock-owner timestamp", e);
        }
    }

    @Override
    public UUID runId() {
        return runId;
    }

    @Override
    public ResolvedTournamentConfiguration configuration() {
        return configuration;
    }

    @Override
    public List<ScheduledTournamentGame> schedule() {
        return schedule;
    }

    @Override
    public synchronized List<TournamentGameResult> committedGames() throws IOException {
        ensureOpen();
        return schedule.stream()
                .map(g -> committed.get(g.gameId()))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public synchronized void commit(TournamentGameResult result) throws IOException {
        ensureOpen();
        if (endedSessions.contains(result.sessionId()))
            throw new IOException("Cannot commit into an ended session");
        var payload = gamePayload(result);
        validateGame(result, payload);
        record("games/" + result.gameId() + ".json", "game", payload);
        committed.put(result.gameId(), result);
        checksums.put(result.gameId(), CanonicalJson.sha256(CanonicalJson.bytes(payload)));
        attempts.add(result.attemptId());
    }

    @Override
    public synchronized void recordFailure(TournamentFailedAttempt failure) throws IOException {
        ensureOpen();
        requireAttempt(failure.gameId(), failure.attemptId(), failure.sessionId());
        if (endedSessions.contains(failure.sessionId()) || committed.containsKey(failure.gameId()))
            throw new IOException("Failure belongs to an ended session or committed game");
        for (int i = 0; i < failure.events().size(); i++)
            if (failure.events().get(i).sequence() != i)
                throw new IOException("Failed-attempt event sequence must be contiguous");
        record(
                "failed-attempts/" + failure.attemptId() + ".json",
                "failed-attempt",
                TournamentRecords.tree(failure));
        attempts.add(failure.attemptId());
    }

    private void requireAttempt(String game, UUID attempt, UUID session) throws IOException {
        if (!scheduleById.containsKey(game)) throw new IOException("Unknown game ID");
        if (!sessions.containsKey(session)) throw new IOException("Missing session start");
        if (attempts.contains(attempt)) throw new IOException("Duplicate attempt ID");
    }

    public synchronized void startSession(UUID id, JsonNode metadata) throws IOException {
        var p = CanonicalJson.object();
        p.put("sessionId", id.toString());
        p.put("startedAt", Instant.now().toString());
        p.set("metadata", Objects.requireNonNull(metadata));
        record("sessions/" + id + ".start.json", "session-start", p);
        sessions.put(id, p);
    }

    public synchronized void endSession(UUID id, String outcome, JsonNode metadata)
            throws IOException {
        if (!sessions.containsKey(id)) throw new IOException("Missing session start");
        if (outcome.equals("COMPLETED") && committed.size() != schedule.size())
            throw new IOException("A completed session requires every scheduled game committed");
        if (!Set.of("COMPLETED", "CANCELLED", "FAILED").contains(outcome))
            throw new IOException("Invalid terminal outcome");
        var p = CanonicalJson.object();
        p.put("sessionId", id.toString());
        p.put("outcome", outcome);
        p.put("endedAt", Instant.now().toString());
        p.set("metadata", Objects.requireNonNull(metadata));
        record("sessions/" + id + ".end.json", "session-end", p);
        endedSessions.add(id);
    }

    public synchronized void diagnostic(JsonNode diagnostic) throws IOException {
        var payload = CanonicalJson.object();
        payload.put("recordedAt", Instant.now().toString());
        payload.set("detail", Objects.requireNonNull(diagnostic));
        record("diagnostics/" + UUID.randomUUID() + ".json", "diagnostic", payload);
    }

    public record CommittedSet(
            UUID runId,
            String digest,
            List<TournamentGameResult> games,
            Map<String, String> gameChecksums,
            List<Long> completeEpochs) {
        public CommittedSet {
            games = List.copyOf(games);
            gameChecksums = Collections.unmodifiableMap(new TreeMap<>(gameChecksums));
            completeEpochs = List.copyOf(completeEpochs);
        }
    }

    public record ReportGeneration(
            String generationId,
            String setDigest,
            String metadataJson,
            Map<String, byte[]> exports) {
        public ReportGeneration {
            exports = copyBytes(exports);
        }

        @Override
        public Map<String, byte[]> exports() {
            return copyBytes(exports);
        }
    }

    private static Map<String, byte[]> copyBytes(Map<String, byte[]> values) {
        var copy = new TreeMap<String, byte[]>();
        values.forEach((k, v) -> copy.put(k, v.clone()));
        return Collections.unmodifiableMap(copy);
    }

    public synchronized CommittedSet captureCommittedSet() throws IOException {
        ensureOpen();
        var games = committedGames();
        var epochs = new ArrayList<Long>();
        var expected = new HashMap<Long, Integer>();
        var actual = new HashMap<Long, Integer>();
        for (var game : schedule) {
            expected.merge(game.epoch(), 1, Integer::sum);
            if (committed.containsKey(game.gameId())) actual.merge(game.epoch(), 1, Integer::sum);
        }
        for (long epoch = 0; epoch < configuration.requested().epochs(); epoch++)
            if (expected.get(epoch).equals(actual.get(epoch))) epochs.add(epoch);
        var inventory = CanonicalJson.object();
        inventory.set("games", TournamentRecords.tree(new TreeMap<>(checksums)));
        inventory.set("completeEpochs", TournamentRecords.tree(epochs));
        return new CommittedSet(
                runId,
                CanonicalJson.sha256(CanonicalJson.bytes(inventory)),
                games,
                checksums,
                epochs);
    }

    private static final Set<String> EXPORT_NAMES =
            Set.of("summary.json", "leaderboard.csv", "game-scores.csv");

    public synchronized ReportGeneration publishReports(
            CommittedSet fixed, Map<String, byte[]> exports, JsonNode metadata) throws IOException {
        ensureOpen();
        if (!captureCommittedSet().equals(fixed))
            throw new IOException("Report committed set is stale or foreign");
        if (!exports.keySet().equals(EXPORT_NAMES))
            throw new IOException(
                    "A report generation requires exactly summary.json, leaderboard.csv and"
                            + " game-scores.csv");
        String generation = UUID.randomUUID().toString();
        String temporary = "reports/.tmp-" + generation;
        mkdir(temporary);
        var manifest = CanonicalJson.object();
        manifest.put("generationId", generation);
        manifest.put("setDigest", fixed.digest());
        manifest.set("gameChecksums", TournamentRecords.tree(fixed.gameChecksums()));
        manifest.set("completeEpochs", TournamentRecords.tree(fixed.completeEpochs()));
        manifest.set("metadata", metadata);
        var hashes = manifest.putObject("exports");
        for (String name : exports.keySet().stream().sorted().toList()) {
            byte[] bytes = exports.get(name).clone();
            publish(temporary + "/" + name, bytes, false);
            hashes.put(name, CanonicalJson.sha256(bytes));
        }
        record(temporary + "/manifest.json", "report-manifest", manifest);
        forceDirectory(root.resolve(temporary));
        Path destination = safe("reports/" + generation);
        if (attributes(destination) != null)
            throw new FileAlreadyExistsException(destination.toString());
        io.atomicMove(root.resolve(temporary), destination, false);
        io.forceDirectory(root.resolve("reports"));
        var pointer = CanonicalJson.object();
        pointer.put("generationId", generation);
        pointer.put(
                "manifestSha256",
                CanonicalJson.sha256(readRegular(destination.resolve("manifest.json"))));
        pointer.put("setDigest", fixed.digest());
        publish(
                "reports/current.json",
                TournamentRecords.encode(runId, "report-current", pointer),
                true);
        return new ReportGeneration(
                generation, fixed.digest(), CanonicalJson.encode(metadata), exports);
    }

    /** Coherent immutable-generation observation, without execution authority or a writer lock. */
    public static ReportGeneration inspectReports(Path directory, UUID runId) throws IOException {
        Path root = directory.toAbsolutePath().normalize();
        checkAncestors(root);
        return new DurableTournamentRunStore(root, null, null, runId).readReports(false);
    }

    public synchronized ReportGeneration readCurrentReports() throws IOException {
        ensureOpen();
        return readReports(true);
    }

    private ReportGeneration readReports(boolean verifySet) throws IOException {
        var pointer = read("reports/current.json", "report-current");
        TournamentRecords.fields(pointer, "generationId", "manifestSha256", "setDigest");
        String id = pointer.path("generationId").asText();
        parseUuid(id, "reports/current.json");
        String relative = "reports/" + id;
        byte[] manifestBytes = readRegular(safe(relative + "/manifest.json"));
        if (!CanonicalJson.sha256(manifestBytes).equals(pointer.path("manifestSha256").asText()))
            throw new IOException("Report manifest checksum conflict");
        var manifest = TournamentRecords.decode(manifestBytes, runId, "report-manifest");
        TournamentRecords.fields(
                manifest,
                "generationId",
                "setDigest",
                "gameChecksums",
                "completeEpochs",
                "metadata",
                "exports");
        if (!manifest.path("generationId").asText().equals(id)
                || !manifest.path("setDigest").asText().equals(pointer.path("setDigest").asText()))
            throw new IOException("Report pointer/manifest conflict");
        TournamentRecords.fields(manifest.get("exports"), EXPORT_NAMES.toArray(String[]::new));
        var exports = new TreeMap<String, byte[]>();
        for (String name : EXPORT_NAMES) {
            byte[] bytes = readRegular(safe(relative + "/" + name));
            if (!CanonicalJson.sha256(bytes).equals(manifest.path("exports").path(name).asText()))
                throw new IOException("Report export checksum conflict: " + name);
            exports.put(name, bytes);
        }
        var fixed = verifySet ? captureCommittedSet() : null;
        if (verifySet
                && (!manifest.get("gameChecksums")
                                .equals(TournamentRecords.tree(fixed.gameChecksums()))
                        || !CanonicalJson.encode(manifest.get("completeEpochs"))
                                .equals(
                                        CanonicalJson.encode(
                                                TournamentRecords.tree(fixed.completeEpochs())))
                        || !fixed.digest().equals(manifest.path("setDigest").asText())))
            throw new IOException(
                    "Report generation does not cover the current verified committed set");
        try (var entries = Files.list(safe(relative))) {
            if (!entries.map(p -> p.getFileName().toString())
                    .collect(java.util.stream.Collectors.toSet())
                    .equals(
                            Set.of(
                                    "manifest.json",
                                    "summary.json",
                                    "leaderboard.csv",
                                    "game-scores.csv")))
                throw new IOException("Unexpected report generation entries");
        }
        return new ReportGeneration(
                id,
                manifest.path("setDigest").asText(),
                CanonicalJson.encode(manifest.get("metadata")),
                exports);
    }

    /**
     * Missing/corrupt derived exports require publication-only resume, without invalidating games.
     */
    public record Verification(
            UUID runId,
            ResolvedTournamentConfiguration configuration,
            List<ScheduledTournamentGame> schedule,
            CommittedSet committedSet,
            boolean successfullyCompleted,
            Optional<ReportGeneration> reports,
            List<String> diagnostics) {
        public Verification {
            schedule = List.copyOf(schedule);
            diagnostics = List.copyOf(diagnostics);
        }
    }

    public static Verification verify(
            TournamentConfiguration requested,
            TournamentBuildIdentity build,
            TournamentInputResolver resolver,
            CancellationToken cancellation)
            throws IOException {
        try (var store = openExisting(requested, build, resolver, cancellation)) {
            var diagnostics = new ArrayList<String>();
            ReportGeneration reports = null;
            try {
                reports = store.readCurrentReports();
            } catch (IOException e) {
                diagnostics.add(e.getMessage());
            }
            boolean complete =
                    reports != null
                            && store.committed.size() == store.schedule.size()
                            && store.successfulFinalSession(reports);
            try (var entries = Files.walk(store.root)) {
                for (Path p : entries.toList())
                    if (store.temporary(p)
                            || p.getFileName().toString().matches("\\.tmp-[0-9a-f-]{36}"))
                        diagnostics.add(
                                "Interrupted publication evidence: " + store.root.relativize(p));
            }
            if (!complete && store.committed.size() == store.schedule.size())
                diagnostics.add(
                        "All games are retained; final reports/session/owner release require"
                                + " publication recovery");
            return new Verification(
                    store.runId,
                    store.configuration,
                    store.schedule,
                    store.captureCommittedSet(),
                    complete,
                    Optional.ofNullable(reports),
                    diagnostics);
        }
    }

    private boolean successfulFinalSession(ReportGeneration report) throws IOException {
        var metadata = CanonicalJson.parse(report.metadataJson());
        if (!metadata.path("outcome").asText().equals("COMPLETED")) return false;
        UUID finalSession;
        try {
            finalSession = UUID.fromString(metadata.path("sessionId").asText());
        } catch (IllegalArgumentException e) {
            return false;
        }
        var finalStart = sessions.get(finalSession);
        if (finalStart == null) return false;
        if (attributes(root.resolve("sessions/" + finalSession + ".end.json")) == null)
            return false;
        var end = read("sessions/" + finalSession + ".end.json", "session-end");
        if (!end.path("outcome").asText().equals("COMPLETED")) return false;
        Instant ended = Instant.parse(end.path("endedAt").asText());
        for (var entry : sessions.entrySet()) {
            if (entry.getKey().equals(finalSession)) continue;
            if (!Instant.parse(entry.getValue().path("startedAt").asText()).isBefore(ended))
                return false;
            Path priorEnd = root.resolve("sessions/" + entry.getKey() + ".end.json");
            if (attributes(priorEnd) != null
                    && Instant.parse(
                                    read("sessions/" + entry.getKey() + ".end.json", "session-end")
                                            .path("endedAt")
                                            .asText())
                            .isAfter(ended)) return false;
        }
        for (Path p : files("diagnostics"))
            if (!temporary(p)) {
                var diagnostic = read("diagnostics/" + p.getFileName(), "diagnostic");
                if (!Instant.parse(diagnostic.path("recordedAt").asText()).isBefore(ended))
                    return false;
            }
        // An unacknowledged owner release never certifies a successful invocation.
        if (attributes(root.resolve("lock-owner.json")) == null) return false;
        try {
            var value = read("lock-owner.json", "lock-owner");
            validateOwner(value);
            return value.path("released").booleanValue();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (closed) return;
        Throwable problem = null;
        try {
            if (owner) {
                var p = activeOwner.deepCopy();
                p.put("released", true);
                p.put("releasedAt", Instant.now().toString());
                publish("lock-owner.json", TournamentRecords.encode(runId, "lock-owner", p), true);
            }
        } catch (Throwable e) {
            problem = e;
            try {
                diagnostic(
                        CanonicalJson.object()
                                .put("operation", "owner-release")
                                .put("error", e.toString()));
            } catch (Throwable diagnosticError) {
                if (problem != diagnosticError) problem.addSuppressed(diagnosticError);
            }
        }
        try {
            lock.release();
        } catch (Throwable e) {
            if (problem == null) problem = e;
            else if (problem != e) problem.addSuppressed(e);
            closeDiagnostic(e);
        }
        try {
            channel.close();
        } catch (Throwable e) {
            if (problem == null) problem = e;
            else if (problem != e) problem.addSuppressed(e);
        }
        closed = true;
        if (problem instanceof IOException io) throw io;
        if (problem instanceof RuntimeException runtime) throw runtime;
        if (problem instanceof Error error) throw error;
        if (problem != null) throw new IOException("Tournament resource cleanup failed", problem);
    }

    private void closeDiagnostic(Throwable problem) {
        // A reported release error does not prove that the OS lock is still owned.
        if (owner && lock.isValid())
            try {
                diagnostic(
                        CanonicalJson.object()
                                .put("operation", "lock-release")
                                .put("error", problem.toString()));
            } catch (Throwable diagnosticError) {
                if (problem != diagnosticError) problem.addSuppressed(diagnosticError);
            }
    }
}
