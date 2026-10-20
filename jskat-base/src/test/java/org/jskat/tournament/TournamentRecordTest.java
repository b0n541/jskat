package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.UUID;

class TournamentRecordTest {
    @Test
    void recordsBindRunTypeVersionAndChecksum() throws Exception {
        var run = UUID.randomUUID();
        var payload = CanonicalJson.parse("{\"answer\":42}");
        byte[] encoded = TournamentRecords.encode(run, "test", payload);
        assertThat(TournamentRecords.decode(encoded, run, "test")).isEqualTo(payload);
        assertThatThrownBy(() -> TournamentRecords.decode(encoded, UUID.randomUUID(), "test"))
                .hasMessageContaining("runId");
        assertThatThrownBy(
                        () ->
                                TournamentRecords.decode(
                                        new String(encoded, java.nio.charset.StandardCharsets.UTF_8)
                                                .replace("\"answer\":42", "\"answer\":43")
                                                .getBytes(java.nio.charset.StandardCharsets.UTF_8),
                                        run,
                                        "test"))
                .hasMessageContaining("checksum");
    }
}
