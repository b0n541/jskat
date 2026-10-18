package org.jskat.ai.ml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

/**
 * Network boundary used by {@link ModelManager}; tests can provide a deterministic implementation.
 */
public interface ModelReleaseService {

    /**
     * Returns metadata for the latest published release.
     * @return latest published release, or {@code null} when none exists
     * @throws IOException if release metadata cannot be retrieved
     */
    RemoteRelease latestPublishedRelease() throws IOException;

    /**
     * Downloads and validates models.json, including its release asset metadata when available.
     * @param release release whose manifest should be fetched
     * @return manifest contents
     * @throws IOException if the manifest cannot be retrieved
     */
    String fetchManifest(RemoteRelease release) throws IOException;

    /**
     * Opens a remote resource.
     * @param uri resource URI
     * @return input stream for the resource
     * @throws IOException if the resource cannot be opened
     */
    InputStream open(URI uri) throws IOException;

        /**
         * Metadata for a release returned by the remote service.
         * @param tagName release tag
         * @param draft whether the release is a draft
         * @param prerelease whether the release is a prerelease
         * @param assets release assets
         */
    record RemoteRelease(String tagName, boolean draft, boolean prerelease, List<ReleaseAsset> assets) {
        /**
         * Copies the supplied asset list to make release metadata immutable.
         */
        public RemoteRelease {
            assets = List.copyOf(assets);
        }

        /**
         * Returns the models.json asset when the release declares one.
         * @return manifest asset, or {@code null} when it is absent
         */
        public ReleaseAsset manifestAsset() {
            return assets.stream()
                    .filter(asset -> asset.name().equals("models.json"))
                    .findFirst()
                    .orElse(null);
        }
    }

    /**
     * Defines the release asset type.
     */
    /**
     * Asset attached to a remote model release.
     * @param name asset file name
     * @param downloadUri asset download URI
     * @param size asset size in bytes
     * @param sha256 expected SHA-256 digest
     */
    record ReleaseAsset(String name, URI downloadUri, long size, String sha256) {
    }
}
