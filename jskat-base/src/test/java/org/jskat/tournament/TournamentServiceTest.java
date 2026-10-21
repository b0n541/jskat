package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.util.*;

class TournamentServiceTest {
    @TempDir Path directory;

    private TournamentConfiguration configuration(Path output) {
        return new TournamentConfigurationReader()
                .read(
                        TournamentConfigurationTest.VALID.replace("runs/test", output.toString()),
                        directory.resolve("config.json"))
                .value();
    }

    @Test
    void sharedPreflightReturnsBuildAndReadOnlyCompetitorIdentities() {
        var config = configuration(directory.resolve("preflight"));
        var result = new TournamentService().preflightNew(config, CancellationToken.NONE);
        assertThat(result.build()).isPresent();
        assertThat(result.competitors())
                .extracting(TournamentInputResolver.InputIdentity::competitorId)
                .containsExactlyElementsOf(
                        config.competitors().stream().map(TournamentCompetitor::id).toList());
        assertThat(result.competitors())
                .extracting(TournamentInputResolver.InputIdentity::concreteClass)
                .containsExactlyElementsOf(
                        config.competitors().stream()
                                .map(c -> c.implementation().className())
                                .toList());
        assertThat(result.competitors())
                .allSatisfy(
                        c -> {
                            assertThat(c.releaseLabel()).isNull();
                            assertThat(c.fingerprint()).isNull();
                        });
        assertThat(Files.exists(config.output().absolute())).isFalse();
    }

    @Test
    void sharedPreflightAggregatesUnavailableBuildAndOutputIssues() throws Exception {
        var output = Files.createDirectory(directory.resolve("existing"));
        var service =
                new TournamentService(
                        new DefaultTournamentInputResolver(),
                        () -> {
                            throw new IllegalStateException("Build unavailable");
                        },
                        (c, w) -> {
                            throw new AssertionError("Not running");
                        },
                        TournamentReports::calculate,
                        System::nanoTime,
                        TournamentPublicationIO.LOCAL);
        var result = service.preflightNew(configuration(output), CancellationToken.NONE);
        assertThat(result.valid()).isFalse();
        assertThat(result.build()).isEmpty();
        assertThat(result.issues())
                .extracting(ValidationIssue::path)
                .containsExactly("/build", "/output");
        assertThat(service.validateNew(configuration(output), CancellationToken.NONE).issues())
                .isEqualTo(result.issues());
    }

    @Test
    void workersRejectUnrepresentableSeatCapacityBeforePreparation() {
        assertThatThrownBy(
                        () ->
                                new TournamentSessionOptions(
                                        Integer.MAX_VALUE, false, java.time.Duration.ofSeconds(60)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancelledNewInvocationCleansUpBeforeTerminalAndReleasesProcessGate() {
        var observed = new ArrayList<TournamentProgress.State>();
        var result =
                new TournamentService()
                        .run(
                                configuration(directory.resolve("run")),
                                new TournamentInvocation(TournamentInvocation.Action.NEW, false),
                                TournamentSessionOptions.defaults(),
                                () -> true,
                                p -> observed.add(p.state()));
        assertThat(result.disposition()).isEqualTo(TournamentService.Disposition.CANCELLED);
        assertThat(observed)
                .endsWith(TournamentProgress.State.FINALIZING, TournamentProgress.State.CANCELLED);
        assertThat(TournamentService.isActive()).isFalse();
    }

    @Test
    void cancellationBeforePreparationIsTypedAndCreatesNothing() {
        Path output = directory.resolve("cancelled");
        assertThatThrownBy(
                        () ->
                                new TournamentService()
                                        .validateNew(configuration(output), () -> true))
                .isInstanceOf(java.util.concurrent.CancellationException.class);
        assertThat(Files.exists(output)).isFalse();
    }

    @Test
    void newValidationRejectsExistingOutputWithoutMutatingIt() throws Exception {
        Path output = Files.createDirectory(directory.resolve("run"));
        var result =
                new TournamentService().validateNew(configuration(output), CancellationToken.NONE);
        assertThat(result.valid()).isFalse();
        assertThat(result.issues())
                .anySatisfy(
                        i -> {
                            assertThat(i.path()).isEqualTo("/output");
                            assertThat(i.code()).isEqualTo("OUTPUT_EXISTS");
                        });
        try (var entries = Files.list(output)) {
            assertThat(entries.toList()).isEmpty();
        }
    }
}
