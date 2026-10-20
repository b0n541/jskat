package org.jskat.tournament;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class TournamentConfigurationTest {
    static final String VALID =
            """
            {"schemaVersion":1,"ruleset":"standard-skat-v1","epochs":2,"output":"runs/test","competitors":[
            {"id":"550e8400-e29b-41d4-a716-446655440000","name":" Random ","implementation":"random"},
            {"id":"ca207ce9-3ca4-4ce1-8443-81e081b8344e","name":"Algorithmic","implementation":"algorithmic"}]}
            """;

    @Test
    void resolvesConfigurationWithoutCreatingOutput() {
        var result =
                new TournamentConfigurationReader().read(VALID, Path.of("/tmp/config/config.json"));
        assertThat(result.issues()).isEmpty();
        assertThat(result.value().output().absolute()).isEqualTo(Path.of("/tmp/config/runs/test"));
        assertThat(result.value().competitors().getFirst().name()).isEqualTo("Random");
        assertThat(result.value().seed()).isEmpty();
        assertThat(result.value().gameCount()).isEqualTo(12);
    }

    @Test
    void aggregatesOrderedSchemaAndRosterIssues() {
        var invalid =
                VALID.replace("\"schemaVersion\":1", "\"schemaVersion\":2,\"extra\":true")
                        .replace("\"epochs\":2", "\"epochs\":1.5")
                        .replace("Algorithmic", "Random")
                        .replace(
                                "\"implementation\":\"algorithmic\"",
                                "\"implementation\":\"random\"");
        var result = new TournamentConfigurationReader().read(invalid, Path.of("/tmp/config.json"));
        assertThat(result.valid()).isFalse();
        assertThat(result.issues())
                .extracting(i -> i.path() + ":" + i.code())
                .containsExactly(
                        "/competitors/1/implementation:DUPLICATE_INPUTS",
                        "/competitors/1/name:DUPLICATE_NAME",
                        "/epochs:INVALID_INTEGER",
                        "/extra:UNKNOWN_FIELD",
                        "/schemaVersion:UNSUPPORTED_VERSION");
    }
}
