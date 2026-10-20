package org.jskat.tournament;

import java.nio.file.Path;
import java.util.*;

/** Requested configuration; seed bits use Java's unsigned-long convention. */
public record TournamentConfiguration(
        int schemaVersion,
        String ruleset,
        Optional<Long> seed,
        long epochs,
        ConfiguredPath output,
        List<TournamentCompetitor> competitors,
        Path configurationFile,
        String originalJson) {
    public TournamentConfiguration {
        Objects.requireNonNull(seed);
        Objects.requireNonNull(output);
        Objects.requireNonNull(originalJson);
        competitors = List.copyOf(competitors);
        configurationFile = configurationFile.toAbsolutePath().normalize();
        if (schemaVersion != 1 || !"standard-skat-v1".equals(ruleset))
            throw new IllegalArgumentException("Unsupported tournament configuration");
        TournamentScheduleSize.gameCount(competitors.size(), epochs);
        var ids = new HashSet<UUID>();
        var names = new HashSet<String>();
        var emptyInputs = new HashSet<PlayerImplementation>();
        for (var c : competitors) {
            if (!ids.add(c.id())
                    || !names.add(c.name())
                    || (!c.implementation().modelRequired()
                            && !emptyInputs.add(c.implementation())))
                throw new IllegalArgumentException(
                        "Duplicate competitor identity/name/effective inputs");
        }
    }

    public long gameCount() {
        return TournamentScheduleSize.gameCount(competitors.size(), epochs);
    }
}
