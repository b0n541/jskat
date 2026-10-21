package org.jskat.tournament;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/** Preparation validates the whole roster and publishes tournament-owned verified snapshots. */
public interface TournamentInputResolver {
    /** Complete read-only source preflight, including probes; never creates run/output storage. */
    ValidationResult<TournamentConfiguration> validate(
            TournamentConfiguration requested, CancellationToken cancellation);

    /** Read-only identities are observations of sources, never retained snapshot authority. */
    record InputIdentity(
            UUID competitorId, String concreteClass, String releaseLabel, String fingerprint) {}

    /** Custom resolvers may omit display identities while retaining their validation contract. */
    default ValidationResult<List<InputIdentity>> preflight(
            TournamentConfiguration requested, CancellationToken cancellation) {
        return new ValidationResult<>(List.of(), validate(requested, cancellation).issues());
    }

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
