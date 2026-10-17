package org.jskat.ai.ml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

/** Network boundary used by {@link ModelManager}; tests can provide a deterministic implementation. */
public interface ModelReleaseService {

    RemoteRelease latestPublishedRelease() throws IOException;

    /** Downloads and validates models.json, including its release asset metadata when available. */
    String fetchManifest(RemoteRelease release) throws IOException;

    InputStream open(URI uri) throws IOException;

    record RemoteRelease(String tagName, boolean draft, boolean prerelease, List<ReleaseAsset> assets) {
        public RemoteRelease {
            assets = List.copyOf(assets);
        }

        public ReleaseAsset manifestAsset() {
            return assets.stream()
                    .filter(asset -> asset.name().equals("models.json"))
                    .findFirst()
                    .orElse(null);
        }
    }

    record ReleaseAsset(String name, URI downloadUri, long size, String sha256) {
    }
}
