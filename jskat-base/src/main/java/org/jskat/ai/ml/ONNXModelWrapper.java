package org.jskat.ai.ml;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for ONNX dense (MLP) model loading and inference.
 * Handles bidding and game evaluation dense models.
 */
public class ONNXModelWrapper extends AbstractONNXWrapper {

    private final ModelType modelType;

    public enum ModelType {
        /** Bidding dense: 35 inputs -> pickup_probs[63], hand_probs[63] */
        BIDDING_DENSE,
        /** Game evaluation dense: 75 inputs -> win_prob[1] */
        GAME_EVAL_DENSE
    }

    /**
     * Creates an ONNX model wrapper.
     *
     * @param modelPath Path to the .onnx model file
     * @param modelType Type of model
     * @throws OrtException If model loading fails
     * @throws IOException  If file cannot be read
     */
    public ONNXModelWrapper(String modelPath, ModelType modelType) throws OrtException, IOException {
        super(modelPath, modelType.name());
        this.modelType = modelType;
    }

    /**
     * Runs bidding dense model inference.
     * Returns two probability arrays for pickup and hand games.
     *
     * @param features Feature vector of length 35
     * @return BiddingResult containing pickup and hand probabilities (each length 63)
     * @throws OrtException If inference fails
     */
    public BiddingResult predictBidding(float[] features) throws OrtException {
        if (modelType != ModelType.BIDDING_DENSE) {
            throw new IllegalStateException("This wrapper is for " + modelType + ", not BIDDING_DENSE");
        }
        if (features.length != MLConstants.BIDDING_DENSE_INPUT_DIM) {
            throw new IllegalArgumentException("Bidding model expects " + MLConstants.BIDDING_DENSE_INPUT_DIM +
                    " features, got " + features.length);
        }

        float[][] input2D = {features};

        try (OnnxTensor tensor = OnnxTensor.createTensor(env, input2D)) {
            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("features", tensor);

            try (var results = session.run(inputs)) {
                float[][] pickupProbs = (float[][]) results.get("pickup_probs").get().getValue();
                float[][] handProbs = (float[][]) results.get("hand_probs").get().getValue();

                return new BiddingResult(pickupProbs[0], handProbs[0]);
            }
        }
    }

    /**
     * Runs game evaluation dense model inference.
     * Returns win probability for a specific game configuration.
     *
     * @param features Feature vector of length 75
     * @return Win probability (0.0 to 1.0)
     * @throws OrtException If inference fails
     */
    public float predictGameEval(float[] features) throws OrtException {
        if (modelType != ModelType.GAME_EVAL_DENSE) {
            throw new IllegalStateException("This wrapper is for " + modelType + ", not GAME_EVAL_DENSE");
        }
        if (features.length != MLConstants.GAME_EVAL_DENSE_INPUT_DIM) {
            throw new IllegalArgumentException("Game eval model expects " + MLConstants.GAME_EVAL_DENSE_INPUT_DIM +
                    " features, got " + features.length);
        }

        float[][] input2D = {features};

        try (OnnxTensor tensor = OnnxTensor.createTensor(env, input2D)) {
            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("features", tensor);

            try (var results = session.run(inputs)) {
                // Handle both 1D [1] and 2D [1, 1] output shapes
                Object value = results.get("win_prob").get().getValue();
                if (value instanceof float[][]) {
                    float[][] winProb = (float[][]) value;
                    return winProb[0][0];
                } else if (value instanceof float[]) {
                    float[] winProb = (float[]) value;
                    return winProb[0];
                } else {
                    throw new OrtException("Unexpected output type: " + value.getClass().getName());
                }
            }
        }
    }

    /**
     * Result container for bidding predictions.
     */
    public static class BiddingResult {
        public final float[] pickupProbs;  // Length 63
        public final float[] handProbs;    // Length 63

        public BiddingResult(float[] pickupProbs, float[] handProbs) {
            this.pickupProbs = pickupProbs;
            this.handProbs = handProbs;
        }
    }
}
