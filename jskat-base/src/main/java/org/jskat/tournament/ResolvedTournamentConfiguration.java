package org.jskat.tournament;

import java.util.*;

/**
 * Effective seed is retained once; originally omitted seeds remain omitted in requested identity.
 */
public record ResolvedTournamentConfiguration(
        TournamentConfiguration requested,
        long effectiveSeed,
        TournamentBuildIdentity build,
        List<ResolvedTournamentCompetitor> competitors) {
    public ResolvedTournamentConfiguration {
        Objects.requireNonNull(requested);
        Objects.requireNonNull(build);
        competitors = List.copyOf(competitors);
        if (requested.seed().isPresent() && requested.seed().get() != effectiveSeed)
            throw new IllegalArgumentException("Effective seed differs from explicit root seed");
        var expected = new HashSet<>(requested.competitors());
        var actual = new HashSet<TournamentCompetitor>();
        var inputs = new HashSet<String>();
        for (var c : competitors) {
            if (!actual.add(c.requested())
                    || !inputs.add(
                            c.requested().implementation().id()
                                    + ":"
                                    + Objects.toString(c.fingerprint(), "")))
                throw new IllegalArgumentException("Duplicate resolved competitor inputs");
        }
        if (!actual.equals(expected) || competitors.size() != expected.size())
            throw new IllegalArgumentException("Resolved roster differs from requested roster");
    }
}
