package org.jskat.ai.ml;

import org.jskat.AbstractJSkatTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ModelManagerTest extends AbstractJSkatTest {

    private static final String BASE_URL = "https://example.test/models/v1.4.0";

    @TempDir
    Path store;

    @Test
    void missingActiveReleaseIsTypedAndDoesNotContactTheService() {
        final RecordingReleaseService service = new RecordingReleaseService();
        final ModelInspection result = new ModelManager(store, service).inspectLocalAvailability();

        assertThat(result.isAvailable()).isFalse();
        assertThat(result.unavailable().reason()).isEqualTo(ModelUnavailableReason.NO_ACTIVE_RELEASE);
        assertThat(service.latestCalls).isZero();
    }

    @Test
    void completeActiveReleaseResolvesImmutableArtifacts() throws Exception {
        final Fixture fixture = new Fixture();
        fixture.writeActive(store, "1.4.0");

        final ModelInspection result = new ModelManager(store, new RecordingReleaseService()).inspectLocalAvailability();

        assertThat(result.isAvailable()).isTrue();
        assertThat(result.resolvedModelSet().version()).isEqualTo("1.4.0");
        assertThat(result.resolvedModelSet().artifacts()).hasSize(ModelManager.REQUIRED_MODEL_FILES.size());
    }

    @Test
    void anIntegrityMismatchCannotResolveTheActiveRelease() throws Exception {
        final Fixture fixture = new Fixture();
        fixture.writeActive(store, "1.4.0");
        Files.writeString(store.resolve("releases/v1.4.0/bidding_dense.onnx"), "corrupt");

        final ModelInspection result = new ModelManager(store, new RecordingReleaseService()).inspectLocalAvailability();

        assertThat(result.unavailable().reason()).isEqualTo(ModelUnavailableReason.FILE_SIZE_MISMATCH);
    }

    @Test
    void unsupportedAndMismatchedReleasesAreRejectedBeforeResolution() throws Exception {
        final Fixture fixture = new Fixture();
        fixture.writeActive(store, "2.0.0");
        ModelInspection unsupported = new ModelManager(store, new RecordingReleaseService()).inspectLocalAvailability();
        assertThat(unsupported.unavailable().reason()).isEqualTo(ModelUnavailableReason.UNSUPPORTED_VERSION);

        fixture.writeActive(store, "1.4.0");
        Files.writeString(store.resolve("releases/v1.4.0/models.json"), fixture.manifestJson("1.3.0"));
        ModelInspection mismatched = new ModelManager(store, new RecordingReleaseService()).inspectLocalAvailability();
        assertThat(mismatched.unavailable().reason()).isEqualTo(ModelUnavailableReason.RELEASE_TAG_MISMATCH);
    }

    @Test
    void updateDiscoveryReturnsACompatibleCandidateWithoutChangingLocalState() throws Exception {
        final Fixture fixture = new Fixture();
        fixture.writeActive(store, "1.4.0");
        final RecordingReleaseService service = new RecordingReleaseService(fixture.manifestJson("1.4.0"));

        final ModelUpdateResult result = new ModelManager(store, service).checkForUpdates();

        assertThat(result.status()).isEqualTo(ModelUpdateResult.Status.UP_TO_DATE);
        assertThat(Files.readString(store.resolve("active"))).isEqualTo("v1.4.0");
        assertThat(service.latestCalls).isEqualTo(1);
    }

    @Test
    void installationReportsIndeterminateContactAndCancellationLeavesActiveReleaseUntouched() throws Exception {
        final Fixture fixture = new Fixture();
        fixture.writeActive(store, "1.4.0");
        final RecordingReleaseService service = new RecordingReleaseService(fixture.manifestJson("1.4.0"));
        final ModelReleaseCandidate candidate = new ModelReleaseCandidate(service.release,
                ModelReleaseManifest.parse(service.manifestJson));
        final List<ModelDownloadProgress> progress = new ArrayList<>();

        final ModelInstallResult result = new ModelManager(store, service).install(candidate, progress::add, () -> true);

        assertThat(result.status()).isEqualTo(ModelInstallResult.Status.CANCELLED);
        assertThat(progress).first().extracting(ModelDownloadProgress::indeterminate).isEqualTo(true);
        assertThat(Files.readString(store.resolve("active"))).isEqualTo("v1.4.0");
    }

    @Test
    void aCompleteCandidateBecomesActiveOnlyAfterVerification() throws Exception {
        final RecordingReleaseService service = new RecordingReleaseService();
        final ModelReleaseCandidate candidate = new ModelReleaseCandidate(service.release,
                ModelReleaseManifest.parse(service.manifestJson));

        final ModelInstallResult result = new ModelManager(store, service).install(candidate, ignored -> { });

        assertThat(result.status()).isEqualTo(ModelInstallResult.Status.INSTALLED);
        assertThat(result.inspection().isAvailable()).isTrue();
        assertThat(Files.readString(store.resolve("active"))).isEqualTo("v1.4.0");
        assertThat(Files.isDirectory(store.resolve("staged"))).isTrue();
    }

    private static final class RecordingReleaseService implements ModelReleaseService {
        private final String manifestJson;
        private final RemoteRelease release;
        private final Map<URI, byte[]> assets = new HashMap<>();
        private int latestCalls;

        private RecordingReleaseService() {
            this(new Fixture().manifestJson("1.4.0"));
        }

        private RecordingReleaseService(final String manifestJson) {
            this.manifestJson = manifestJson;
            this.release = new RemoteRelease("v1.4.0", false, false,
                    List.of(new ReleaseAsset("models.json", URI.create(BASE_URL + "/models.json"),
                            manifestJson.getBytes(StandardCharsets.UTF_8).length, null)));
            final ModelReleaseManifest manifest = ModelReleaseManifest.parse(manifestJson);
            for (final ModelAsset asset : manifest.assets()) {
                assets.put(asset.resolveAgainst(manifest.baseUrl()),
                        Fixture.bytesFor(asset.name(), asset.sizeBytes()));
            }
        }

        @Override
        public RemoteRelease latestPublishedRelease() {
            latestCalls++;
            return release;
        }

        @Override
        public String fetchManifest(final RemoteRelease ignored) {
            return manifestJson;
        }

        @Override
        public InputStream open(final URI uri) throws IOException {
            final byte[] data = assets.get(uri);
            if (data == null) {
                throw new IOException("Unknown asset " + uri);
            }
            return new ByteArrayInputStream(data);
        }
    }

    private static final class Fixture {
        private final Map<String, byte[]> bytes = new HashMap<>();

        private void writeActive(final Path store, final String version) throws Exception {
            final Path release = store.resolve("releases/v" + version);
            Files.createDirectories(release);
            Files.writeString(release.resolve("models.json"), manifestJson(version));
            for (final ModelAsset asset : ModelReleaseManifest.parse(manifestJson(version)).assets()) {
                Files.write(release.resolve(asset.name()), bytesFor(asset.name(), asset.sizeBytes()));
            }
            Files.writeString(store.resolve("active"), "v" + version);
        }

        private String manifestJson(final String version) {
            final StringBuilder files = new StringBuilder();
            for (final String name : ModelManager.REQUIRED_MODEL_FILES) {
                final byte[] data = bytes.computeIfAbsent(name, key -> bytesFor(key, 16));
                if (!files.isEmpty()) {
                    files.append(',');
                }
                files.append("{\"name\":\"").append(name).append("\",\"size_bytes\":")
                        .append(data.length).append(",\"sha256\":\"").append(sha256(data)).append("\"}");
            }
            return "{\"version\":\"" + version + "\",\"base_url\":\"" + BASE_URL
                    + "\",\"models\":{\"skat\":{\"variants\":{\"all\":{\"files\":["
                    + files + "]}}}}}";
        }

        private static byte[] bytesFor(final String name, final long size) {
            final byte[] data = new byte[(int) size];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (name.hashCode() + i);
            }
            return data;
        }

        private static String sha256(final byte[] data) {
            try {
                final byte[] digest = MessageDigest.getInstance("SHA-256").digest(data);
                final StringBuilder result = new StringBuilder();
                for (final byte value : digest) {
                    result.append(String.format("%02x", value));
                }
                return result.toString();
            } catch (final Exception e) {
                throw new AssertionError(e);
            }
        }
    }
}
