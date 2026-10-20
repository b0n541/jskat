package org.jskat.tournament;

import java.text.Normalizer;
import java.util.*;

public record TournamentCompetitor(
        UUID id, String name, PlayerImplementation implementation, ModelSource model) {
    public TournamentCompetitor {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(implementation);
        name = Normalizer.normalize(name.strip(), Normalizer.Form.NFC);
        if (!java.nio.charset.StandardCharsets.UTF_8.newEncoder().canEncode(name)
                || id.variant() != 2
                || id.version() < 1
                || id.version() > 8
                || name.isEmpty()
                || name.codePointCount(0, name.length()) > 128
                || implementation.modelRequired() != (model != null))
            throw new IllegalArgumentException("Invalid tournament competitor");
    }
}
