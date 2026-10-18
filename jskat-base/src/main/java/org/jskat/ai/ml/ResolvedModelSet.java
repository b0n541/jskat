package org.jskat.ai.ml;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable, validated model artifacts passed to an ML player.
 */
public final class ResolvedModelSet {

    private final String version;
    private final Map<String, Path> artifacts;

    ResolvedModelSet(final String version, final Map<String, Path> artifacts) {
        this.version = Objects.requireNonNull(version, "version");
        this.artifacts = Map.copyOf(artifacts);
    }

    /**
     * Creates a test or embedding model set with immutable artifact paths.
     * @param version model release version
     * @param artifacts model artifact paths keyed by file name
     * @return immutable resolved model set
     */
    public static ResolvedModelSet of(final String version, final Map<String, Path> artifacts) {
        Objects.requireNonNull(artifacts, "artifacts");
        if (version == null || version.isBlank() || artifacts.isEmpty()) {
            throw new IllegalArgumentException("A resolved model set needs a version and at least one artifact");
        }
        return new ResolvedModelSet(version, artifacts);
    }

    /**
     * Performs version.
     * @return version result.
     */
    public String version() {
        return version;
    }

    /**
     * Performs path.
     * @param fileName file name value.
     * @return path result.
     */
    public Path path(final String fileName) {
        final Path path = artifacts.get(fileName);
        if (path == null) {
            throw new IllegalArgumentException("Resolved model set does not contain " + fileName);
        }
        return path;
    }

    /**
     * Performs artifacts.
     * @return artifacts result.
     */
    public Map<String, Path> artifacts() {
        return artifacts;
    }

    /**
     * Performs bidding dense.
     * @return bidding dense result.
     */
    public Path biddingDense() {
        return path("bidding_dense.onnx");
    }

    /**
     * Performs game eval dense.
     * @return game eval dense result.
     */
    public Path gameEvalDense() {
        return path("game_eval_dense.onnx");
    }

    /**
     * Performs game eval transformer.
     * @return game eval transformer result.
     */
    public Path gameEvalTransformer() {
        return path("game_eval_transformer.onnx");
    }

    /**
     * Performs card play transformer.
     * @return card play transformer result.
     */
    public Path cardPlayTransformer() {
        return path("card_play_transformer.onnx");
    }

    /**
     * Performs bidding transformer.
     * @return bidding transformer result.
     */
    public Path biddingTransformer() {
        return path("bidding_transformer.onnx");
    }
}
