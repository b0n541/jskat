package org.jskat.ai.ml;

import java.net.URI;
import java.util.Objects;

/** One file declared by a model release manifest. */
public record ModelAsset(String name, long sizeBytes, String sha256) {

    public ModelAsset {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(sha256, "sha256");
    }

    public URI resolveAgainst(final URI baseUrl) {
        final String prefix = baseUrl.toString().endsWith("/") ? baseUrl.toString() : baseUrl + "/";
        return URI.create(prefix + name);
    }
}
