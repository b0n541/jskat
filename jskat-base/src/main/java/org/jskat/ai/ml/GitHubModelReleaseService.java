package org.jskat.ai.ml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * GitHub implementation of the model-release service boundary.
 */
public final class GitHubModelReleaseService implements ModelReleaseService {

    private static final URI LATEST_RELEASE = URI.create(
            "https://api.github.com/repos/avaskys/skat-ml-models/releases/latest");
    private static final ObjectMapper JSON = new ObjectMapper();

    private final HttpClient client;

    /**
     * Performs git hub model release service.
     */
    public GitHubModelReleaseService() {
        this(HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build());
    }

    GitHubModelReleaseService(final HttpClient client) {
        this.client = client;
    }

    @Override
    public RemoteRelease latestPublishedRelease() throws IOException {
        final JsonNode root = getJson(LATEST_RELEASE);
        final List<ReleaseAsset> assets = new ArrayList<>();
        for (final JsonNode asset : root.path("assets")) {
            final String digest = asset.path("digest").asText("");
            assets.add(new ReleaseAsset(
                    asset.path("name").asText(),
                    URI.create(asset.path("browser_download_url").asText()),
                    asset.path("size").asLong(-1),
                    digest.startsWith("sha256:") ? digest.substring("sha256:".length()) : null));
        }
        return new RemoteRelease(
                root.path("tag_name").asText(),
                root.path("draft").asBoolean(),
                root.path("prerelease").asBoolean(),
                assets);
    }

    @Override
    public String fetchManifest(final RemoteRelease release) throws IOException {
        final ReleaseAsset asset = release.manifestAsset();
        if (asset == null) {
            throw new IOException("The model release does not publish models.json");
        }
        final byte[] bytes = getBytes(asset.downloadUri());
        if (asset.size() >= 0 && bytes.length != asset.size()) {
            throw new IOException("models.json size does not match GitHub release metadata");
        }
        if (asset.sha256() != null && !asset.sha256().isBlank()
                && !asset.sha256().equalsIgnoreCase(sha256(bytes))) {
            throw new IOException("models.json SHA-256 does not match GitHub release metadata");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public InputStream open(final URI uri) throws IOException {
        final HttpRequest request = request(uri).build();
        final HttpResponse<InputStream> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while opening model asset", e);
        }
        if (response.statusCode() / 100 != 2) {
            response.body().close();
            throw new IOException("Model asset request failed with HTTP " + response.statusCode());
        }
        return response.body();
    }

    private JsonNode getJson(final URI uri) throws IOException {
        return JSON.readTree(new String(getBytes(uri), StandardCharsets.UTF_8));
    }

    private byte[] getBytes(final URI uri) throws IOException {
        final HttpRequest request = request(uri).build();
        try {
            final HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() / 100 != 2) {
                throw new IOException("Model release request failed with HTTP " + response.statusCode());
            }
            return response.body();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while contacting GitHub", e);
        }
    }

    private HttpRequest.Builder request(final URI uri) {
        return HttpRequest.newBuilder(uri)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "JSkat-model-manager");
    }

    private static String sha256(final byte[] bytes) throws IOException {
        try {
            final byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
            final StringBuilder result = new StringBuilder(64);
            for (final byte value : digest) {
                result.append(String.format("%02x", value));
            }
            return result.toString();
        } catch (final NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 is unavailable", e);
        }
    }
}
