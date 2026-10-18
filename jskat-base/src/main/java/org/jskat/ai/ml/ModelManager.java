package org.jskat.ai.ml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * JavaFX-free facade for local model inspection, release discovery, and installation.
 */
public final class ModelManager {

    /**
     * Current model manifest version supported by the application.
     */
    public static final String SUPPORTED_VERSION = "1.4.0";
    /**
     * S u p p o r t e d  v e r s i o n s value.
     */
    public static final List<String> SUPPORTED_VERSIONS = List.of(SUPPORTED_VERSION);
    /**
     * R e q u i r e d  m o d e l  f i l e s value.
     */
    public static final List<String> REQUIRED_MODEL_FILES = List.of(
            "bidding_dense.onnx",
            "bidding_dense.onnx.data",
            "bidding_transformer.onnx",
            "bidding_transformer.onnx.data",
            "card_play_transformer.onnx",
            "game_eval_dense.onnx",
            "game_eval_dense.onnx.data",
            "game_eval_transformer.onnx",
            "game_eval_transformer.onnx.data"
    );

    private final Path storeDirectory;
    private final ModelReleaseService releaseService;

    /**
     * Performs model manager.
     */
    public ModelManager() {
        this(Path.of(System.getProperty("user.home"), ".jskat", "models"),
                new GitHubModelReleaseService());
    }

    /**
     * Performs model manager.
     * @param storeDirectory store directory value.
     * @param releaseService release service value.
     */
    public ModelManager(final Path storeDirectory, final ModelReleaseService releaseService) {
        this.storeDirectory = storeDirectory.toAbsolutePath().normalize();
        this.releaseService = releaseService;
    }

    /**
     * Performs store directory.
     * @return store directory result.
     */
    public Path storeDirectory() {
        return storeDirectory;
    }

    /**
     * Inspects only the local Active marker and files. It never creates directories or contacts the network.
     * @return current local model availability
     */
    public ModelInspection inspectLocalAvailability() {
        final Path activeMarker = storeDirectory.resolve("active");
        if (!Files.isRegularFile(activeMarker)) {
            return unavailable(ModelUnavailableReason.NO_ACTIVE_RELEASE, "No Active model release is installed", null);
        }

        final String marker;
        try {
            marker = Files.readString(activeMarker).trim();
        } catch (final IOException e) {
            return unavailable(ModelUnavailableReason.ACTIVE_MARKER_INVALID,
                    "The Active model marker cannot be read", null);
        }
        if (!marker.matches("v[0-9]+\\.[0-9]+\\.[0-9]+")) {
            return unavailable(ModelUnavailableReason.ACTIVE_MARKER_INVALID,
                    "The Active model marker is invalid", null);
        }

        final String version = marker.substring(1);
        final Path releaseDirectory = storeDirectory.resolve("releases").resolve(marker);
        if (!Files.isDirectory(releaseDirectory)) {
            return unavailable(ModelUnavailableReason.ACTIVE_RELEASE_MISSING,
                    "The Active model release directory is missing", version);
        }
        if (!SUPPORTED_VERSIONS.contains(version)) {
            return unavailable(ModelUnavailableReason.UNSUPPORTED_VERSION,
                    "The Active model release version " + version + " is not supported", version);
        }

        final Path manifestPath = releaseDirectory.resolve("models.json");
        if (!Files.isRegularFile(manifestPath)) {
            return unavailable(ModelUnavailableReason.MANIFEST_MISSING,
                    "The Active model release has no models.json manifest", version);
        }

        final ModelReleaseManifest manifest;
        try {
            manifest = ModelReleaseManifest.parse(Files.readString(manifestPath));
        } catch (final RuntimeException | IOException e) {
            return unavailable(ModelUnavailableReason.MANIFEST_INVALID,
                    "The Active model manifest is invalid: " + e.getMessage(), version);
        }
        if (!manifest.version().equals(version)) {
            return unavailable(ModelUnavailableReason.RELEASE_TAG_MISMATCH,
                    "The Active marker and manifest versions disagree", version);
        }
        if (!SUPPORTED_VERSIONS.contains(manifest.version())) {
            return unavailable(ModelUnavailableReason.UNSUPPORTED_VERSION,
                    "The Active model manifest version " + manifest.version() + " is not supported", version);
        }
        final ModelUnavailable fileFailure = validateManifestAndFiles(manifest, releaseDirectory, version);
        if (fileFailure != null) {
            return ModelInspection.unavailable(fileFailure);
        }

        final Map<String, Path> paths = new HashMap<>();
        for (final String fileName : REQUIRED_MODEL_FILES) {
            paths.put(fileName, releaseDirectory.resolve(fileName));
        }
        return ModelInspection.available(new ResolvedModelSet(version, paths));
    }

    /**
     * Checks the published stable release without changing local model state.
     * @return update availability and release information
     */
    public ModelUpdateResult checkForUpdates() {
        final ModelInspection local = inspectLocalAvailability();
        final ModelReleaseService.RemoteRelease release;
        try {
            release = releaseService.latestPublishedRelease();
            if (release == null || release.draft() || release.prerelease()) {
                return ModelUpdateResult.failed(ModelUnavailableReason.MANIFEST_INVALID,
                        "No published stable model release is available");
            }
            final String manifestJson = releaseService.fetchManifest(release);
            final ModelReleaseManifest manifest = ModelReleaseManifest.parse(manifestJson);
            if (!("v" + manifest.version()).equals(release.tagName())) {
                return ModelUpdateResult.failed(ModelUnavailableReason.RELEASE_TAG_MISMATCH,
                        "The model manifest version does not match the release tag");
            }
            if (!SUPPORTED_VERSIONS.contains(manifest.version())) {
                return ModelUpdateResult.unsupported(
                        "Model release " + manifest.version() + " is not supported by this JSkat version");
            }
            final ModelUnavailable shapeFailure = validateManifestShape(manifest);
            if (shapeFailure != null) {
                return ModelUpdateResult.failed(shapeFailure.reason(), shapeFailure.message());
            }

            if (!local.isAvailable()) {
                return ModelUpdateResult.available(new ModelReleaseCandidate(release, manifest));
            }
            final int comparison = compareVersions(manifest.version(), local.installedVersion());
            if (comparison > 0) {
                return ModelUpdateResult.available(new ModelReleaseCandidate(release, manifest));
            }
            return ModelUpdateResult.upToDate("Model release " + local.installedVersion() + " is current");
        } catch (final IllegalArgumentException e) {
            return ModelUpdateResult.failed(ModelUnavailableReason.MANIFEST_INVALID, e.getMessage());
        } catch (final IOException e) {
            return ModelUpdateResult.failed(ModelUnavailableReason.MANIFEST_INVALID,
                    "Could not check for model updates: " + e.getMessage());
        }
    }

    /**
     * Performs install.
     * @param candidate candidate value.
     * @param progress progress value.
     * @param cancellation cancellation value.
     * @return install result.
     */
    public ModelInstallResult install(final ModelReleaseCandidate candidate,
                                      final Consumer<ModelDownloadProgress> progress,
                                      final ModelCancellation cancellation) {
        final Consumer<ModelDownloadProgress> listener = progress == null ? ignored -> { } : progress;
        final ModelCancellation token = cancellation == null ? ModelCancellation.never() : cancellation;
        try {
            final ModelUnavailable candidateFailure = validateCandidate(candidate);
            if (candidateFailure != null) {
                return ModelInstallResult.failed(inspectLocalAvailability(), candidateFailure.message());
            }
            Files.createDirectories(storeDirectory);
            try (FileChannel channel = FileChannel.open(storeDirectory.resolve("install.lock"),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 FileLock ignored = channel.lock()) {
                cleanupStagedReleases();
                final Path staged = storeDirectory.resolve("staged")
                        .resolve(UUID.randomUUID() + "-v" + candidate.manifest().version());
                Files.createDirectories(staged);
                try {
                    final ModelReleaseManifest manifest = candidate.manifest();
                    listener.accept(new ModelDownloadProgress(null, 0, 0, 0, 0, true));
                    listener.accept(new ModelDownloadProgress(null, 0, manifest.assets().size(),
                            0, manifest.totalBytes(), false));
                    long downloaded = 0;
                    int completed = 0;
                    for (final ModelAsset asset : manifest.assets()) {
                        if (cancelled(token)) {
                            return ModelInstallResult.cancelled(inspectLocalAvailability());
                        }
                        listener.accept(new ModelDownloadProgress(asset.name(), completed,
                                manifest.assets().size(), downloaded, manifest.totalBytes(), false));
                        final Path target = staged.resolve(asset.name());
                        try (InputStream input = releaseService.open(asset.resolveAgainst(manifest.baseUrl()));
                             var output = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)) {
                            final byte[] buffer = new byte[32 * 1024];
                            int read;
                            while ((read = input.read(buffer)) >= 0) {
                                if (cancelled(token)) {
                                    return ModelInstallResult.cancelled(inspectLocalAvailability());
                                }
                                output.write(buffer, 0, read);
                                downloaded += read;
                                listener.accept(new ModelDownloadProgress(asset.name(), completed,
                                        manifest.assets().size(), downloaded, manifest.totalBytes(), false));
                            }
                        }
                        completed++;
                        listener.accept(new ModelDownloadProgress(asset.name(), completed,
                                manifest.assets().size(), downloaded, manifest.totalBytes(), false));
                    }
                    Files.writeString(staged.resolve("models.json"), manifest.sourceJson(),
                            StandardOpenOption.CREATE_NEW);
                    final ModelUnavailable validationFailure = validateManifestAndFiles(manifest, staged,
                            candidate.manifest().version());
                    if (validationFailure != null) {
                        return ModelInstallResult.failed(inspectLocalAvailability(), validationFailure.message());
                    }
                    activate(staged, manifest.version());
                    return ModelInstallResult.installed(inspectLocalAvailability());
                } finally {
                    deleteRecursively(staged);
                }
            }
        } catch (final IOException | RuntimeException e) {
            return ModelInstallResult.failed(inspectLocalAvailability(),
                    "Model installation failed: " + e.getMessage());
        }
    }

    /**
     * Performs install.
     * @param candidate candidate value.
     * @param progress progress value.
     * @return install result.
     */
    public ModelInstallResult install(final ModelReleaseCandidate candidate,
                                      final Consumer<ModelDownloadProgress> progress) {
        return install(candidate, progress, ModelCancellation.never());
    }

    private ModelUnavailable validateCandidate(final ModelReleaseCandidate candidate) {
        if (candidate == null || candidate.release() == null || candidate.manifest() == null) {
            return unavailableValue(ModelUnavailableReason.MANIFEST_INVALID, "No model release candidate was supplied", null);
        }
        if (candidate.release().draft() || candidate.release().prerelease()) {
            return unavailableValue(ModelUnavailableReason.MANIFEST_INVALID, "Draft and prerelease models cannot be installed", null);
        }
        if (!SUPPORTED_VERSIONS.contains(candidate.manifest().version())) {
            return unavailableValue(ModelUnavailableReason.UNSUPPORTED_VERSION,
                    "Model release " + candidate.manifest().version() + " is not supported", candidate.manifest().version());
        }
        if (!("v" + candidate.manifest().version()).equals(candidate.release().tagName())) {
            return unavailableValue(ModelUnavailableReason.RELEASE_TAG_MISMATCH,
                    "The model manifest version does not match the release tag", candidate.manifest().version());
        }
        return validateManifestShape(candidate.manifest());
    }

    private ModelUnavailable validateManifestShape(final ModelReleaseManifest manifest) {
        final Set<String> actual = new HashSet<>();
        for (final ModelAsset asset : manifest.assets()) {
            actual.add(asset.name());
        }
        final Set<String> expected = Set.copyOf(REQUIRED_MODEL_FILES);
        if (!actual.equals(expected)) {
            return unavailableValue(ModelUnavailableReason.MANIFEST_INVALID,
                    "Manifest file set does not match the supported model contract", manifest.version());
        }
        return null;
    }

    private ModelUnavailable validateManifestAndFiles(final ModelReleaseManifest manifest,
                                                      final Path releaseDirectory,
                                                      final String version) {
        final ModelUnavailable shapeFailure = validateManifestShape(manifest);
        if (shapeFailure != null) {
            return shapeFailure;
        }
        for (final String fileName : REQUIRED_MODEL_FILES) {
            final ModelAsset asset = manifest.asset(fileName);
            final Path path = releaseDirectory.resolve(fileName);
            if (!Files.isRegularFile(path)) {
                return unavailableValue(ModelUnavailableReason.REQUIRED_FILE_MISSING,
                        "Required model artifact is missing: " + fileName, version);
            }
            try {
                if (Files.size(path) != asset.sizeBytes()) {
                    return unavailableValue(ModelUnavailableReason.FILE_SIZE_MISMATCH,
                            "Model artifact size does not match the manifest: " + fileName, version);
                }
                if (!sha256(path).equalsIgnoreCase(asset.sha256())) {
                    return unavailableValue(ModelUnavailableReason.FILE_HASH_MISMATCH,
                            "Model artifact SHA-256 does not match the manifest: " + fileName, version);
                }
            } catch (final IOException e) {
                return unavailableValue(ModelUnavailableReason.FILE_HASH_MISMATCH,
                        "Could not verify model artifact " + fileName + ": " + e.getMessage(), version);
            }
        }
        return null;
    }

    private void activate(final Path staged, final String version) throws IOException {
        final Path releases = storeDirectory.resolve("releases");
        Files.createDirectories(releases);
        final String previousTag = Files.isRegularFile(storeDirectory.resolve("active"))
                ? Files.readString(storeDirectory.resolve("active")).trim()
                : "";
        final String tag = "v" + version;
        final Path target = releases.resolve(tag);
        if (Files.exists(target)) {
            deleteRecursively(target);
        }
        moveAtomically(staged, target);

        final Path activeTemp = storeDirectory.resolve("active.tmp-" + UUID.randomUUID());
        Files.writeString(activeTemp, tag, StandardOpenOption.CREATE_NEW);
        moveAtomically(activeTemp, storeDirectory.resolve("active"));

        final List<Path> completedReleases;
        try (var paths = Files.list(releases)) {
            completedReleases = paths.filter(Files::isDirectory)
                    .sorted(Comparator.comparing(Path::toString).reversed())
                    .toList();
        }
        for (final Path release : completedReleases) {
            if (!release.getFileName().toString().equals(tag)
                    && !release.getFileName().toString().equals(previousTag)) {
                deleteRecursively(release);
            }
        }
    }

    private void cleanupStagedReleases() throws IOException {
        final Path staged = storeDirectory.resolve("staged");
        if (!Files.isDirectory(staged)) {
            return;
        }
        try (var paths = Files.list(staged)) {
            for (final Path path : paths.toList()) {
                deleteRecursively(path);
            }
        }
    }

    private static boolean cancelled(final ModelCancellation token) {
        return token.isCancelled() || Thread.currentThread().isInterrupted();
    }

    private static ModelInspection unavailable(final ModelUnavailableReason reason, final String message,
                                               final String version) {
        return ModelInspection.unavailable(unavailableValue(reason, message, version));
    }

    private static ModelUnavailable unavailableValue(final ModelUnavailableReason reason, final String message,
                                                     final String version) {
        return new ModelUnavailable(reason, message, version);
    }

    private static void moveAtomically(final Path source, final Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String sha256(final Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] buffer = new byte[32 * 1024];
            int read;
            while ((read = input.read(buffer)) >= 0) {
                digest.update(buffer, 0, read);
            }
            final StringBuilder result = new StringBuilder(64);
            for (final byte value : digest.digest()) {
                result.append(String.format("%02x", value));
            }
            return result.toString();
        } catch (final NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 is unavailable", e);
        }
    }

    private static void deleteRecursively(final Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var paths = Files.walk(path)) {
            for (final Path child : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(child);
            }
        }
    }

    private static int compareVersions(final String left, final String right) {
        final String[] leftParts = left.split("\\.");
        final String[] rightParts = right.split("\\.");
        for (int i = 0; i < 3; i++) {
            final int comparison = Integer.compare(Integer.parseInt(leftParts[i]), Integer.parseInt(rightParts[i]));
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

}
