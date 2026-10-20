package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TournamentBuildIdentityTest {
    @Test
    void packagedIdentityRetainsFullCommitAndOptionalDirtyInputDigest() {
        var identity = TournamentBuildIdentity.current();
        assertThat(identity.commit()).matches("[0-9a-f]{40}|[0-9a-f]{64}");
        var dirty =
                new TournamentBuildIdentity(
                        identity.commit(), java.util.Optional.of("a".repeat(64)));
        assertThat(dirty.identity()).isEqualTo(identity.commit() + "+dirty:" + "a".repeat(64));
        assertThat(
                        new TournamentBuildIdentity(identity.commit(), java.util.Optional.empty())
                                .identity())
                .isEqualTo(identity.commit());
        assertThatThrownBy(() -> new TournamentBuildIdentity("1.0", java.util.Optional.empty()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
