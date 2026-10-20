package org.jskat.tournament;

import java.io.IOException;
import java.util.*;

/** Build-supplied identity. Runtime never invokes Git or substitutes an application version. */
public record TournamentBuildIdentity(String commit, Optional<String> sourceDigest) {
    public TournamentBuildIdentity {
        Objects.requireNonNull(commit);
        Objects.requireNonNull(sourceDigest);
        if (!commit.matches("[0-9a-f]{40}|[0-9a-f]{64}")
                || sourceDigest.filter(s -> !s.matches("[0-9a-f]{64}")).isPresent())
            throw new IllegalArgumentException(
                    "Unsupported or unavailable tournament build identity");
    }

    public String identity() {
        return commit + sourceDigest.map(s -> "+dirty:" + s).orElse("");
    }

    public static TournamentBuildIdentity current() {
        try (var input =
                TournamentBuildIdentity.class.getResourceAsStream(
                        "/org/jskat/tournament/build-identity.properties")) {
            if (input == null)
                throw new IllegalStateException("Packaged tournament build identity unavailable");
            var properties = new Properties();
            properties.load(input);
            if (!"jskat-build-inputs-v1".equals(properties.getProperty("encoding")))
                throw new IllegalStateException("Unsupported build identity encoding");
            return new TournamentBuildIdentity(
                    properties.getProperty("commit"),
                    Optional.ofNullable(properties.getProperty("sourceDigest")));
        } catch (IOException | IllegalArgumentException | NullPointerException e) {
            throw new IllegalStateException("Invalid packaged tournament build identity", e);
        }
    }
}
