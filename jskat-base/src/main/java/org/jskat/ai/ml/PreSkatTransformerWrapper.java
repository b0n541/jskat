package org.jskat.ai.ml;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;

import java.io.IOException;
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
public class PreSkatTransformerWrapper extends AbstractONNXWrapper {

    private static final int NUM_CARDS = 10;

    /**
     * Creates a PreSkatTransformer wrapper.
     *
     * @param modelPath Path to the .onnx model file
     * @throws OrtException If model loading fails
     * @throws IOException  If file cannot be read
     */
    public PreSkatTransformerWrapper(String modelPath) throws OrtException, IOException {
        super(modelPath, "PreSkatTransformer");
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

            try (var results = session.run(inputs)) {
                // Extract outputs
                float[][] pickupProbs = (float[][]) results.get("pickup_probs").get().getValue();
                float[][] handProbs = (float[][]) results.get("hand_probs").get().getValue();

                return new Result(pickupProbs[0], handProbs[0]);
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
    }
}
