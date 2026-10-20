package org.jskat.tournament;

import java.nio.file.Path;
import java.util.*;

/** A retained model identity never depends on its mutable source after preparation. */
public record ResolvedTournamentCompetitor(
        TournamentCompetitor requested,
        String concreteClass,
        String releaseLabel,
        String fingerprint,
        Path snapshot,
        List<TournamentArtifact> artifacts) {
    public ResolvedTournamentCompetitor {
        Objects.requireNonNull(requested);
        Objects.requireNonNull(concreteClass);
        artifacts = List.copyOf(artifacts);
        if (requested.implementation().modelRequired()) {
            Objects.requireNonNull(releaseLabel);
            Objects.requireNonNull(snapshot);
            snapshot = snapshot.toAbsolutePath().normalize();
            if (fingerprint == null
                    || !fingerprint.matches("[0-9a-f]{64}")
                    || artifacts.isEmpty()
                    || !TournamentIdentities.artifactFingerprint(artifacts).equals(fingerprint))
                throw new IllegalArgumentException("Invalid resolved model identity");
        } else if (releaseLabel != null
                || fingerprint != null
                || snapshot != null
                || !artifacts.isEmpty())
            throw new IllegalArgumentException(
                    "Model-free competitor must not contain model inputs");
    }
}
