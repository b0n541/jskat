package org.jskat.ai.ml;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for the Bidding Transformer ONNX model.
 * <p>
 * This attention-based model evaluates the initial 10-card hand for bidding
 * decisions. It learns card relationships like Jack combinations and suit patterns.
 * <p>
 * Input tensors:
 * - hand_cards: (batch, 10) - card indices 0-31, sorted
 * - position: (batch,) - player position 0-2 (forehand, middlehand, rearhand)
 * <p>
 * Output tensors:
 * - pickup_probs: (batch, 63) - win probability at each bid level for pickup game
 * - hand_probs: (batch, 63) - win probability at each bid level for hand game
 */
public class PreSkatTransformerWrapper implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(PreSkatTransformerWrapper.class);

    private static final int NUM_CARDS = 10;
    private static final int NUM_BID_LEVELS = 63;

    private final OrtEnvironment env;
    private final OrtSession session;

    /**
     * Creates a PreSkatTransformer wrapper.
     *
     * @param modelPath Path to the .onnx model file
     * @throws OrtException If model loading fails
     * @throws IOException  If file cannot be read
     */
    public PreSkatTransformerWrapper(String modelPath) throws OrtException, IOException {
        this.env = OrtEnvironment.getEnvironment();

        Path path = Paths.get(modelPath);
        if (!Files.exists(path)) {
            String absolutePath = path.toAbsolutePath().toString();
            String cwd = System.getProperty("user.dir");
            throw new IOException(String.format(
                    "PreSkatTransformer model not found: %s\n" +
                    "  Absolute path tried: %s\n" +
                    "  Current working directory: %s",
                    modelPath, absolutePath, cwd));
        }

        logger.info("Loading PreSkatTransformer from {}", modelPath);
        this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
        logger.info("PreSkatTransformer loaded successfully");
    }

    /**
     * Runs inference on the PreSkatTransformer model.
     *
     * @param handCardIndices 10 card indices (0-31), sorted
     * @param positionIdx     Position index (0-2: forehand, middlehand, rearhand)
     * @return Result containing pickup and hand probabilities at each bid level
     * @throws OrtException If inference fails
     */
    public Result predict(int[] handCardIndices, int positionIdx) throws OrtException {
        if (handCardIndices.length != NUM_CARDS) {
            throw new IllegalArgumentException("Expected " + NUM_CARDS + " hand cards, got " + handCardIndices.length);
        }

        // Prepare tensors (batch size 1)
        long[][] handCards = new long[1][NUM_CARDS];
        for (int i = 0; i < NUM_CARDS; i++) {
            handCards[0][i] = handCardIndices[i];
        }

        long[] position = {positionIdx};

        // Create tensors and run inference
        try (OnnxTensor handTensor = OnnxTensor.createTensor(env, handCards);
             OnnxTensor positionTensor = OnnxTensor.createTensor(env, position)) {

            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("hand_cards", handTensor);
            inputs.put("position", positionTensor);

            var results = session.run(inputs);

            // Extract outputs
            float[][] pickupProbs = (float[][]) results.get("pickup_probs").get().getValue();
            float[][] handProbs = (float[][]) results.get("hand_probs").get().getValue();

            return new Result(pickupProbs[0], handProbs[0]);
        }
    }

    @Override
    public void close() {
        if (session != null) {
            try {
                session.close();
            } catch (OrtException e) {
                logger.error("Error closing PreSkatTransformer session", e);
            }
        }
    }

    /**
     * Result container for PreSkatTransformer predictions.
     */
    public static class Result {
        public final float[] pickupProbs;  // Length 63
        public final float[] handProbs;    // Length 63

        public Result(float[] pickupProbs, float[] handProbs) {
            this.pickupProbs = pickupProbs;
            this.handProbs = handProbs;
        }

        /**
         * Finds the maximum bid value where probability exceeds threshold.
         *
         * @param bidValues Array of valid bid values (length 63)
         * @param forHand   Whether to use hand probabilities
         * @param threshold Minimum probability threshold
         * @return Maximum bid value, or 0 to pass
         */
        public int getMaxBid(int[] bidValues, boolean forHand, float threshold) {
            float[] probs = forHand ? handProbs : pickupProbs;

            for (int i = probs.length - 1; i >= 0; i--) {
                if (probs[i] >= threshold) {
                    return bidValues[i];
                }
            }
            return 0;  // Pass
        }
    }
}
