package org.jskat.tournament;

import java.nio.file.Path;

/** Preparation validates the whole roster and publishes tournament-owned verified snapshots. */
public interface TournamentInputResolver {
    ValidationResult<ResolvedTournamentConfiguration> prepare(
            TournamentConfiguration requested,
            long effectiveSeed,
            TournamentBuildIdentity build,
            Path snapshotDirectory,
            CancellationToken cancellation);

    /** Verify retained snapshots only; never resolve original model sources on resume. */
    ValidationResult<ResolvedTournamentConfiguration> verify(
            ResolvedTournamentConfiguration retained, CancellationToken cancellation);
}
