package org.jskat.tournament;

import java.nio.file.Path;
import java.util.Objects;

public record ConfiguredPath(String configured, Path absolute) {
    public ConfiguredPath {
        Objects.requireNonNull(configured);
        absolute =
                Path.of(
                        java.text.Normalizer.normalize(
                                absolute.toAbsolutePath().normalize().toString(),
                                java.text.Normalizer.Form.NFC));
    }

    public static ConfiguredPath resolve(String value, Path configurationFile) {
        return new ConfiguredPath(
                value, configurationFile.toAbsolutePath().normalize().getParent().resolve(value));
    }
}
