package org.jskat.tournament;

import java.text.Normalizer;
import java.util.Objects;

/** Complete release artifact inventory entry; lowercase SHA-256 is mandatory. */
public record TournamentArtifact(String name, long size, String sha256) {
    public TournamentArtifact {
        name = Normalizer.normalize(Objects.requireNonNull(name), Normalizer.Form.NFC);
        if (name.isBlank()
                || name.equals(".")
                || name.equals("..")
                || name.contains("/")
                || name.contains("\\")
                || size < 0
                || sha256 == null
                || !sha256.matches("[0-9a-f]{64}"))
            throw new IllegalArgumentException("Invalid artifact inventory entry");
    }
}
