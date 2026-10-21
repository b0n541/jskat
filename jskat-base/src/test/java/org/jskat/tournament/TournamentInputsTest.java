package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;

class TournamentInputsTest {
    @TempDir Path dir;

    TournamentCompetitor competitor(
            String id, PlayerImplementation implementation, ModelSource source) {
        return new TournamentCompetitor(
                UUID.fromString(id), implementation.id() + id, implementation, source);
    }

    TournamentConfiguration config(List<TournamentCompetitor> roster) {
        return new TournamentConfiguration(
                1,
                "standard-skat-v1",
                Optional.of(1L),
                1,
                new ConfiguredPath("out", dir.resolve("out")),
                roster,
                dir.resolve("config.json"),
                "{}");
    }

    @Test
    void modelFreeRosterDoesNotNeedModelStore() {
        var roster =
                List.of(
                        competitor(
                                "00000000-0000-4000-8000-000000000001",
                                PlayerImplementation.RANDOM,
                                null),
                        competitor(
                                "00000000-0000-4000-8000-000000000002",
                                PlayerImplementation.ALGORITHMIC,
                                null));
        var result =
                new DefaultTournamentInputResolver(dir.resolve("absent-store"))
                        .prepare(
                                config(roster),
                                1,
                                new TournamentBuildIdentity("a".repeat(40), Optional.empty()),
                                dir.resolve("snapshots"),
                                CancellationToken.NONE);
        assertThat(result.valid()).isTrue();
        assertThat(result.value().competitors()).hasSize(2);
        assertThat(dir.resolve("snapshots")).doesNotExist();
    }

    @Test
    void aggregatesIndependentRosterFailuresWithoutCreatingSnapshots() {
        var roster =
                List.of(
                        competitor(
                                "00000000-0000-4000-8000-000000000001",
                                PlayerImplementation.ML,
                                new ModelSource.Active()),
                        competitor(
                                "00000000-0000-4000-8000-000000000002",
                                PlayerImplementation.ML_PRO,
                                new ModelSource.Installed("missing")));
        var result =
                new DefaultTournamentInputResolver(dir.resolve("absent-store"))
                        .prepare(
                                config(roster),
                                1,
                                new TournamentBuildIdentity("a".repeat(40), Optional.empty()),
                                dir.resolve("snapshots"),
                                CancellationToken.NONE);
        assertThat(result.valid()).isFalse();
        assertThat(result.issues())
                .extracting(ValidationIssue::path)
                .containsExactly("/competitors/0", "/competitors/1");
        assertThat(dir.resolve("snapshots")).doesNotExist();
    }

    @Test
    void snapshotsSurviveSourceRemovalAndRejectRetainedTampering() throws Exception {
        Path installed = Path.of(System.getProperty("user.home"), ".jskat/models/releases/v1.4.0");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                java.nio.file.Files.exists(installed.resolve("models.json")),
                "Local ONNX integration fixture not installed");
        Path development = dir.resolve("development");
        java.nio.file.Files.createDirectory(development);
        try (var files = java.nio.file.Files.list(installed)) {
            for (var file : files.toList())
                if (java.nio.file.Files.isRegularFile(file))
                    java.nio.file.Files.copy(file, development.resolve(file.getFileName()));
        }
        var manifest =
                org.jskat.ai.ml.ModelReleaseManifest.parse(
                        java.nio.file.Files.readString(development.resolve("models.json")));
        java.nio.file.Files.writeString(
                development.resolve("models.json"),
                new org.jskat.ai.ml.ModelReleaseManifest(
                                "local-workbench", manifest.baseUrl(), manifest.assets())
                        .sourceJson());
        var roster =
                List.of(
                        competitor(
                                "00000000-0000-4000-8000-000000000001",
                                PlayerImplementation.ML_PRO,
                                new ModelSource.Development(
                                        new ConfiguredPath("development", development))),
                        competitor(
                                "00000000-0000-4000-8000-000000000002",
                                PlayerImplementation.RANDOM,
                                null));
        var resolver = new DefaultTournamentInputResolver(dir.resolve("absent"));
        var preflight =
                new TournamentService(resolver)
                        .preflightNew(config(roster), CancellationToken.NONE);
        assertThat(preflight.competitors()).hasSize(2);
        assertThat(preflight.competitors().getFirst().releaseLabel()).isEqualTo("local-workbench");
        assertThat(preflight.competitors().getFirst().fingerprint()).matches("[0-9a-f]{64}");
        assertThat(dir.resolve("out")).doesNotExist();
        assertThat(dir.resolve("snapshots")).doesNotExist();
        var result =
                resolver.prepare(
                        config(roster),
                        1,
                        new TournamentBuildIdentity("a".repeat(40), Optional.empty()),
                        dir.resolve("snapshots"),
                        CancellationToken.NONE);
        assertThat(result.issues()).isEmpty();
        assertThat(result.valid()).isTrue();
        assertThat(result.value().competitors().getFirst().fingerprint())
                .isEqualTo(preflight.competitors().getFirst().fingerprint());
        assertThat(result.value().competitors().getFirst().releaseLabel())
                .isEqualTo("local-workbench");
        try (var files = java.nio.file.Files.list(development)) {
            for (var file : files.toList()) java.nio.file.Files.delete(file);
        }
        java.nio.file.Files.delete(development);
        assertThat(resolver.verify(result.value(), CancellationToken.NONE).valid()).isTrue();
        var snapshot = result.value().competitors().getFirst().snapshot();
        java.nio.file.Files.write(snapshot.resolve("bidding_dense.onnx"), new byte[] {1});
        assertThat(resolver.verify(result.value(), CancellationToken.NONE).issues())
                .extracting(ValidationIssue::message)
                .anyMatch(m -> m.contains("size"));
    }

    @Test
    void sourcePreflightDoesNotCreateOutputOrSnapshots() {
        var roster =
                List.of(
                        competitor(
                                "00000000-0000-4000-8000-000000000001",
                                PlayerImplementation.ML,
                                new ModelSource.Active()),
                        competitor(
                                "00000000-0000-4000-8000-000000000002",
                                PlayerImplementation.ML_PRO,
                                new ModelSource.Installed("missing")));
        var result =
                new DefaultTournamentInputResolver(dir.resolve("absent-store"))
                        .validate(config(roster), CancellationToken.NONE);
        assertThat(result.valid()).isFalse();
        assertThat(result.issues()).hasSize(2);
        assertThat(dir.resolve("out")).doesNotExist();
        assertThat(dir.resolve("absent-store")).doesNotExist();
    }

    @Test
    void activeAndInstalledAliasesRejectDuplicateEffectiveCompetitors() throws Exception {
        Path store = Path.of(System.getProperty("user.home"), ".jskat/models");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                java.nio.file.Files.exists(store.resolve("active")),
                "Local model integration fixture not installed");
        String active = java.nio.file.Files.readString(store.resolve("active")).strip();
        var roster =
                List.of(
                        competitor(
                                "00000000-0000-4000-8000-000000000001",
                                PlayerImplementation.ML,
                                new ModelSource.Active()),
                        competitor(
                                "00000000-0000-4000-8000-000000000002",
                                PlayerImplementation.ML,
                                new ModelSource.Installed(active.replaceFirst("^v", ""))));
        var result =
                new DefaultTournamentInputResolver(store)
                        .validate(config(roster), CancellationToken.NONE);
        assertThat(result.issues())
                .extracting(ValidationIssue::code)
                .containsExactly("duplicate-inputs");
        assertThat(java.nio.file.Files.readString(store.resolve("active")).strip())
                .isEqualTo(active);
        assertThat(dir.resolve("out")).doesNotExist();
    }

    @Test
    void hashValidArtifactsWithWrongTensorSignatureAreRejected() throws Exception {
        Path installed = Path.of(System.getProperty("user.home"), ".jskat/models/releases/v1.4.0");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                java.nio.file.Files.exists(installed.resolve("models.json")),
                "Local ONNX integration fixture not installed");
        Path development = dir.resolve("development");
        java.nio.file.Files.createDirectory(development);
        try (var files = java.nio.file.Files.list(installed)) {
            for (var file : files.toList())
                if (java.nio.file.Files.isRegularFile(file))
                    java.nio.file.Files.copy(file, development.resolve(file.getFileName()));
        }
        var original =
                org.jskat.ai.ml.ModelReleaseManifest.parse(
                        java.nio.file.Files.readString(installed.resolve("models.json")));
        var replacement = original.asset("game_eval_dense.onnx");
        java.nio.file.Files.copy(
                development.resolve("game_eval_dense.onnx"),
                development.resolve("bidding_dense.onnx"),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        var inventory =
                original.assets().stream()
                        .map(
                                a ->
                                        a.name().equals("bidding_dense.onnx")
                                                ? new org.jskat.ai.ml.ModelAsset(
                                                        a.name(),
                                                        replacement.sizeBytes(),
                                                        replacement.sha256())
                                                : a)
                        .toList();
        java.nio.file.Files.writeString(
                development.resolve("models.json"),
                new org.jskat.ai.ml.ModelReleaseManifest(
                                "different-label", original.baseUrl(), inventory)
                        .sourceJson());
        var roster =
                List.of(
                        competitor(
                                "00000000-0000-4000-8000-000000000001",
                                PlayerImplementation.ML,
                                new ModelSource.Development(
                                        new ConfiguredPath("development", development))),
                        competitor(
                                "00000000-0000-4000-8000-000000000002",
                                PlayerImplementation.RANDOM,
                                null));
        var result =
                new DefaultTournamentInputResolver(dir.resolve("absent"))
                        .validate(config(roster), CancellationToken.NONE);
        assertThat(result.issues()).hasSize(1);
        assertThat(result.issues().getFirst().message()).contains("incompatible");
        assertThat(dir.resolve("out")).doesNotExist();
    }
}
