package org.jskat.tournament;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Comparator;

/** Explicitly versioned identities. Changing encoding requires a new version. */
public final class TournamentIdentities {
    public static final String REQUEST_ENCODING = "jskat-request-v1";

    private TournamentIdentities() {}

    public static String requestedCanonicalJson(TournamentConfiguration configuration) {
        return CanonicalJson.encode(requested(configuration));
    }

    public static String requestedDigest(TournamentConfiguration configuration) {
        return CanonicalJson.sha256(CanonicalJson.bytes(requested(configuration)));
    }

    private static ObjectNode requested(TournamentConfiguration c) {
        var root = CanonicalJson.object();
        root.put("encoding", REQUEST_ENCODING);
        var value = root.putObject("configuration");
        value.put("schemaVersion", c.schemaVersion());
        value.put("ruleset", c.ruleset());
        value.put("epochs", c.epochs());
        value.put("output", c.output().absolute().toString());
        c.seed().ifPresent(s -> value.put("seed", Long.toUnsignedString(s)));
        var roster = value.putArray("competitors");
        for (var entry :
                c.competitors().stream()
                        .sorted(Comparator.comparing(e -> e.id().toString()))
                        .toList()) {
            var competitor = roster.addObject();
            competitor.put("id", entry.id().toString());
            competitor.put("name", entry.name());
            competitor.put("implementation", entry.implementation().id());
            if (entry.model() != null) {
                var model = competitor.putObject("model");
                switch (entry.model()) {
                    case ModelSource.Active ignored -> model.put("source", "active");
                    case ModelSource.Installed installed -> {
                        model.put("source", "installed");
                        model.put("version", installed.version());
                    }
                    case ModelSource.Development development -> {
                        model.put("source", "development");
                        model.put("path", development.path().absolute().toString());
                    }
                }
            }
        }
        return root;
    }

    public static final String ARTIFACT_ENCODING = "jskat-artifacts-v1";
    public static final String SEED_ENCODING = "jskat-seed-v1";

    public static String artifactFingerprint(java.util.List<TournamentArtifact> artifacts) {
        var root = CanonicalJson.object();
        root.put("encoding", ARTIFACT_ENCODING);
        var values = root.putArray("artifacts");
        var names = new java.util.HashSet<String>();
        for (var artifact :
                artifacts.stream()
                        .sorted(Comparator.comparing(TournamentArtifact::name))
                        .toList()) {
            if (!names.add(artifact.name()))
                throw new IllegalArgumentException("Duplicate artifact name");
            var value = values.addObject();
            value.put("name", artifact.name());
            value.put("size", artifact.size());
            value.put("sha256", artifact.sha256());
        }
        return CanonicalJson.sha256(CanonicalJson.bytes(root));
    }

    public static long deriveSeed(long rootSeed, String purpose, String... identifiers) {
        if (purpose == null || purpose.isBlank())
            throw new IllegalArgumentException("Seed purpose is required");
        var root = CanonicalJson.object();
        root.put("encoding", SEED_ENCODING);
        root.put("rootSeed", Long.toUnsignedString(rootSeed));
        root.put("purpose", purpose);
        var values = root.putArray("identifiers");
        for (var id : identifiers) values.add(java.util.Objects.requireNonNull(id));
        return java.nio.ByteBuffer.wrap(CanonicalJson.hash(CanonicalJson.bytes(root))).getLong();
    }

    public static String resolvedCanonicalJson(ResolvedTournamentConfiguration configuration) {
        return CanonicalJson.encode(resolved(configuration));
    }

    public static String resolvedDigest(ResolvedTournamentConfiguration configuration) {
        return CanonicalJson.sha256(CanonicalJson.bytes(resolved(configuration)));
    }

    private static ObjectNode resolved(ResolvedTournamentConfiguration c) {
        var root = CanonicalJson.object();
        root.put("encoding", "jskat-resolved-v1");
        root.put("requestedDigest", requestedDigest(c.requested()));
        root.put("effectiveSeed", Long.toUnsignedString(c.effectiveSeed()));
        root.put("build", c.build().identity());
        root.put("gameCount", c.requested().gameCount());
        var roster = root.putArray("competitors");
        for (var competitor :
                c.competitors().stream()
                        .sorted(Comparator.comparing(e -> e.requested().id().toString()))
                        .toList()) {
            var value = roster.addObject();
            value.put("id", competitor.requested().id().toString());
            value.put("implementation", competitor.requested().implementation().id());
            value.put("class", competitor.concreteClass());
            if (competitor.fingerprint() != null) {
                value.put("fingerprint", competitor.fingerprint());
                value.put("releaseLabel", competitor.releaseLabel());
                value.put("snapshot", competitor.snapshot().toString());
                var artifacts = value.putArray("artifacts");
                for (var artifact :
                        competitor.artifacts().stream()
                                .sorted(Comparator.comparing(TournamentArtifact::name))
                                .toList()) {
                    var a = artifacts.addObject();
                    a.put("name", artifact.name());
                    a.put("size", artifact.size());
                    a.put("sha256", artifact.sha256());
                }
            }
        }
        return root;
    }
}
