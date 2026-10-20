package org.jskat.tournament;

import java.util.Objects;

public sealed interface ModelSource {
    record Active() implements ModelSource {}

    record Installed(String version) implements ModelSource {
        public Installed {
            Objects.requireNonNull(version);
            if (version.isBlank()
                    || version.equals(".")
                    || version.equals("..")
                    || version.contains("/")
                    || version.contains("\\")
                    || version.indexOf(0) >= 0)
                throw new IllegalArgumentException(
                        "Installed version must be a direct-child release label");
        }
    }

    record Development(ConfiguredPath path) implements ModelSource {
        public Development {
            Objects.requireNonNull(path);
        }
    }
}
