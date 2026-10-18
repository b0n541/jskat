package org.jskat.ai.ml;

import java.net.URI;
import java.util.Objects;

/**
 * One file declared by a model release manifest.
 *
 * @param name      asset file name relative to the release base URL
 * @param sizeBytes expected asset size in bytes
 * @param sha256    expected SHA-256 digest in hexadecimal form
 */
public record ModelAsset(String name, long sizeBytes, String sha256) {

    /**
     * Validates the asset name and digest.
     */
    public ModelAsset {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(sha256, "sha256");
    }

    /**
     * Resolves this asset against a release base URI.
     *
     * @param baseUrl base URI of the release
     * @return asset download URI
     */
    public URI resolveAgainst(final URI baseUrl) {
        final String prefix = baseUrl.toString().endsWith("/") ? baseUrl.toString() : baseUrl + "/";
        return URI.create(prefix + name);
    }
}
