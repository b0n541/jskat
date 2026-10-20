package org.jskat.tournament;

import org.jskat.ai.ml.*;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

/**
 * Read-only source discovery and private verified snapshots. No ordinary model lifecycle is used.
 */
public final class DefaultTournamentInputResolver implements TournamentInputResolver {
    private final Path modelStore;
    private final TournamentPlayerRegistry registry = new TournamentPlayerRegistry();

    public DefaultTournamentInputResolver(Path modelStore) {
        this.modelStore = modelStore.toAbsolutePath().normalize();
    }

    public DefaultTournamentInputResolver() {
        this(Path.of(System.getProperty("user.home"), ".jskat", "models"));
    }

    public record Candidate(
            ModelSource source, Path directory, String label, List<ValidationIssue> issues) {
        public Candidate {
            issues = List.copyOf(issues);
        }
    }

    /** Invalid installed candidates remain discoverable with their reasons. */
    public List<Candidate> discover() {
        var candidates = new ArrayList<Candidate>();
        candidates.add(candidate(new ModelSource.Active()));
        var releases = modelStore.resolve("releases");
        if (Files.isDirectory(releases))
            try (var entries = Files.list(releases)) {
                entries.filter(Files::isDirectory)
                        .sorted()
                        .filter(p -> !p.equals(candidates.getFirst().directory()))
                        .forEach(
                                p ->
                                        candidates.add(
                                                candidate(
                                                        new ModelSource.Installed(
                                                                p.getFileName().toString()))));
            } catch (IOException e) {
                candidates.add(
                        new Candidate(
                                null,
                                releases,
                                "Installed",
                                List.of(issue("/models", "discovery", e))));
            }
        return List.copyOf(candidates);
    }

    public Candidate candidate(ModelSource source) {
        Path directory = null;
        String label = "Unknown";
        try {
            directory = sourceDirectory(source);
            var manifest = readManifest(directory);
            label = manifest.version();
            inventory(directory, manifest, CancellationToken.NONE);
            TournamentModelSignatures.verify(directory, PlayerImplementation.ML);
            TournamentModelSignatures.verify(directory, PlayerImplementation.ML_PRO);
            return new Candidate(source, directory, label, List.of());
        } catch (Exception e) {
            return new Candidate(source, directory, label, List.of(issue("/models", "model", e)));
        }
    }

    private Path sourceDirectory(ModelSource source) throws IOException {
        if (source instanceof ModelSource.Development d) return d.path().absolute();
        String label =
                source instanceof ModelSource.Installed i
                        ? i.version()
                        : Files.readString(modelStore.resolve("active")).strip();
        // Reuse the direct-child validation even for the mutable Active marker.
        new ModelSource.Installed(label);
        Path releases = modelStore.resolve("releases");
        Path release = releases.resolve(label);
        if (Files.isSymbolicLink(release))
            throw new IOException("Release directory must not be a symbolic link");
        return release;
    }

    private ModelReleaseManifest readManifest(Path directory) throws IOException {
        return ModelReleaseManifest.parse(Files.readString(directory.resolve("models.json")));
    }

    private List<TournamentArtifact> inventory(
            Path directory, ModelReleaseManifest manifest, CancellationToken cancellation)
            throws Exception {
        var names =
                manifest.assets().stream()
                        .map(ModelAsset::name)
                        .collect(java.util.stream.Collectors.toSet());
        if (manifest.assets().size() != ModelManager.REQUIRED_MODEL_FILES.size()
                || !names.equals(new HashSet<>(ModelManager.REQUIRED_MODEL_FILES)))
            throw new IOException(
                    "Manifest must contain exactly the nine required model artifacts");
        var result = new ArrayList<TournamentArtifact>();
        for (var asset :
                manifest.assets().stream()
                        .sorted(Comparator.comparing(ModelAsset::name))
                        .toList()) {
            checkCancellation(cancellation);
            Path path = directory.resolve(asset.name());
            if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)
                    || Files.size(path) != asset.sizeBytes())
                throw new IOException("Missing or incorrect size: " + asset.name());
            var digest = MessageDigest.getInstance("SHA-256");
            try (var input = Files.newInputStream(path)) {
                byte[] buffer = new byte[65536];
                for (int n; (n = input.read(buffer)) != -1; ) {
                    checkCancellation(cancellation);
                    digest.update(buffer, 0, n);
                }
            }
            var hash = HexFormat.of().formatHex(digest.digest());
            if (!hash.equals(asset.sha256()))
                throw new IOException("SHA-256 mismatch: " + asset.name());
            result.add(new TournamentArtifact(asset.name(), asset.sizeBytes(), hash));
        }
        return List.copyOf(result);
    }

    private static void checkCancellation(CancellationToken token) {
        if (token.isCancellationRequested())
            throw new java.util.concurrent.CancellationException("Input preparation cancelled");
    }

    private static ValidationIssue issue(String path, String code, Exception e) {
        return new ValidationIssue(
                ValidationIssue.Severity.ERROR,
                code,
                path,
                e.getMessage() == null ? e.toString() : e.getMessage());
    }

    /** Validate before acquiring a new run directory. All source inspection is read-only. */
    @Override
    public ValidationResult<TournamentConfiguration> validate(
            TournamentConfiguration requested, CancellationToken token) {
        var issues = new ArrayList<ValidationIssue>();
        var identities = new HashSet<String>();
        int index = 0;
        for (var competitor : requested.competitors()) {
            String pointer = "/competitors/" + index++;
            try {
                checkCancellation(token);
                ResolvedTournamentCompetitor entry;
                if (competitor.implementation().modelRequired()) {
                    Path source = sourceDirectory(competitor.model());
                    var manifest = readManifest(source);
                    var artifacts = inventory(source, manifest, token);
                    TournamentModelSignatures.verify(source, competitor.implementation());
                    entry =
                            new ResolvedTournamentCompetitor(
                                    competitor,
                                    competitor.implementation().className(),
                                    manifest.version(),
                                    TournamentIdentities.artifactFingerprint(artifacts),
                                    source,
                                    artifacts);
                } else
                    entry =
                            new ResolvedTournamentCompetitor(
                                    competitor,
                                    competitor.implementation().className(),
                                    null,
                                    null,
                                    null,
                                    List.of());
                if (!identities.add(
                        competitor.implementation().id()
                                + ":"
                                + Objects.toString(entry.fingerprint(), "")))
                    issues.add(
                            new ValidationIssue(
                                    ValidationIssue.Severity.ERROR,
                                    "duplicate-inputs",
                                    pointer,
                                    "Duplicate implementation and effective model inputs"));
                probe(entry);
            } catch (Exception e) {
                issues.add(issue(pointer, "input", e));
            }
        }
        return new ValidationResult<>(issues.isEmpty() ? requested : null, issues);
    }

    @Override
    public ValidationResult<ResolvedTournamentConfiguration> prepare(
            TournamentConfiguration requested,
            long seed,
            TournamentBuildIdentity build,
            Path snapshots,
            CancellationToken token) {
        var issues = new ArrayList<ValidationIssue>();
        var resolved = new ArrayList<ResolvedTournamentCompetitor>();
        var identities = new HashSet<String>();
        var created = new ArrayList<Path>();
        int index = 0;
        for (var competitor : requested.competitors()) {
            String pointer = "/competitors/" + index++;
            try {
                checkCancellation(token);
                ResolvedTournamentCompetitor entry;
                if (!competitor.implementation().modelRequired())
                    entry =
                            new ResolvedTournamentCompetitor(
                                    competitor,
                                    competitor.implementation().className(),
                                    null,
                                    null,
                                    null,
                                    List.of());
                else {
                    Path source = sourceDirectory(competitor.model());
                    var manifest = readManifest(source);
                    var artifacts = inventory(source, manifest, token);
                    String fingerprint = TournamentIdentities.artifactFingerprint(artifacts);
                    Path snapshot = snapshots.toAbsolutePath().normalize().resolve(fingerprint);
                    if (!Files.exists(snapshot)) {
                        Files.createDirectories(snapshots);
                        Path staging = Files.createTempDirectory(snapshots, ".snapshot-");
                        try {
                            Files.copy(
                                    source.resolve("models.json"), staging.resolve("models.json"));
                            for (var asset : artifacts) {
                                checkCancellation(token);
                                Files.copy(
                                        source.resolve(asset.name()),
                                        staging.resolve(asset.name()));
                            }
                            var copied = inventory(staging, readManifest(staging), token);
                            if (!copied.equals(artifacts))
                                throw new IOException("Source changed during snapshot copy");
                            Files.move(staging, snapshot, StandardCopyOption.ATOMIC_MOVE);
                            created.add(snapshot);
                        } finally {
                            deleteTree(staging);
                        }
                    }
                    entry =
                            new ResolvedTournamentCompetitor(
                                    competitor,
                                    competitor.implementation().className(),
                                    manifest.version(),
                                    fingerprint,
                                    snapshot,
                                    artifacts);
                    verifyModel(entry, token);
                }
                String identity =
                        competitor.implementation().id()
                                + ":"
                                + Objects.toString(entry.fingerprint(), "");
                if (!identities.add(identity))
                    issues.add(
                            new ValidationIssue(
                                    ValidationIssue.Severity.ERROR,
                                    "duplicate-inputs",
                                    pointer,
                                    "Duplicate implementation and effective model inputs"));
                probe(entry);
                resolved.add(entry);
            } catch (Exception e) {
                issues.add(issue(pointer, "input", e));
            }
        }
        if (!issues.isEmpty()) {
            for (var path : created)
                try {
                    deleteTree(path);
                } catch (IOException e) {
                    issues.add(issue("/snapshots", "cleanup", e));
                }
            return new ValidationResult<>(null, issues);
        }
        try {
            return new ValidationResult<>(
                    new ResolvedTournamentConfiguration(
                            requested,
                            seed,
                            build,
                            resolved.stream()
                                    .sorted(
                                            Comparator.comparing(
                                                    c -> c.requested().id().toString()))
                                    .toList()),
                    issues);
        } catch (Exception e) {
            issues.add(issue("/", "inputs", e));
            return new ValidationResult<>(null, issues);
        }
    }

    private void verifyModel(ResolvedTournamentCompetitor entry, CancellationToken token)
            throws Exception {
        var actual = inventory(entry.snapshot(), readManifest(entry.snapshot()), token);
        if (!actual.equals(entry.artifacts())
                || !TournamentIdentities.artifactFingerprint(actual).equals(entry.fingerprint()))
            throw new IOException("Retained snapshot identity mismatch");
        TournamentModelSignatures.verify(entry.snapshot(), entry.requested().implementation());
    }

    private void probe(ResolvedTournamentCompetitor entry) {
        var player = registry.create(entry);
        registry.close(player);
    }

    @Override
    public ValidationResult<ResolvedTournamentConfiguration> verify(
            ResolvedTournamentConfiguration retained, CancellationToken token) {
        var issues = new ArrayList<ValidationIssue>();
        int index = 0;
        for (var entry : retained.competitors())
            try {
                checkCancellation(token);
                if (!entry.concreteClass().equals(entry.requested().implementation().className()))
                    throw new IOException("Player implementation changed");
                if (entry.requested().implementation().modelRequired()) verifyModel(entry, token);
            } catch (Exception e) {
                issues.add(issue("/competitors/" + index, "snapshot", e));
            } finally {
                index++;
            }
        return new ValidationResult<>(issues.isEmpty() ? retained : null, issues);
    }

    private static void deleteTree(Path directory) throws IOException {
        if (!Files.exists(directory)) return;
        try (var paths = Files.walk(directory)) {
            for (var path : paths.sorted(Comparator.reverseOrder()).toList()) Files.delete(path);
        }
    }
}
