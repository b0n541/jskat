package org.jskat.ai.ml;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for the Game Evaluation Transformer ONNX model.
 * <p>
 * This attention-based model evaluates a hand + skat combination to predict
 * win probability. It can learn card relationships like singleton detection
 * and suit protection patterns.
 * <p>
 * Input tensors:
 * - hand_cards: (batch, 10) - card indices 0-31
 * - skat_cards: (batch, 2) - card indices 0-31 or 32 for padding
 * - skat_len: (batch,) - 0, 1, or 2
 * - game_type: (batch,) - 0-5 (DIAMONDS, HEARTS, SPADES, CLUBS, GRAND, NULL)
 * - position: (batch,) - 0-2 (forehand, middlehand, rearhand)
 * - is_hand: (batch,) - 0 or 1
 * - bid: (batch,) - normalized bid value 0.0-1.0
 * <p>
 * Output:
 * - win_prob: (batch,) - win probability 0.0-1.0
 */
public class CardSetEvaluatorWrapper implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(CardSetEvaluatorWrapper.class);

    private static final int MAX_HAND = 10;
    private static final int MAX_SKAT = 2;
    private static final int CARD_PAD_IDX = 32;

    // Bid normalization constants (matching Python)
    private static final int MIN_BID = 18;
    private static final int MAX_BID = 264;

    private final OrtEnvironment env;
    private final OrtSession session;

    /**
     * Creates a CardSetEvaluator wrapper.
     *
     * @param modelPath Path to the .onnx model file
     * @throws OrtException If model loading fails
     * @throws IOException  If file cannot be read
     */
    public CardSetEvaluatorWrapper(String modelPath) throws OrtException, IOException {
        this.env = OrtEnvironment.getEnvironment();

        Path path = Paths.get(modelPath);
        if (!Files.exists(path)) {
            String absolutePath = path.toAbsolutePath().toString();
            String cwd = System.getProperty("user.dir");
            throw new IOException(String.format(
                    "CardSetEvaluator model not found: %s\n" +
                    "  Absolute path tried: %s\n" +
                    "  Current working directory: %s",
                    modelPath, absolutePath, cwd));
        }

        logger.info("Loading CardSetEvaluator from {}", modelPath);
        this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
        logger.info("CardSetEvaluator loaded successfully");
    }

    /**
     * Runs inference on the CardSetEvaluator model.
     *
     * @param handCardIndices  10 card indices (0-31), sorted
     * @param skatCardIndices  0-2 card indices (0-31), padded with 32
     * @param skatLen          Number of actual skat cards (0, 1, or 2)
     * @param gameTypeIdx      Game type index (0-5)
     * @param positionIdx      Position index (0-2)
     * @param isHand           Whether this is a hand game
     * @param bidValue         Raw bid value (will be normalized)
     * @return Win probability (0.0 to 1.0)
     * @throws OrtException If inference fails
     */
    public float predict(int[] handCardIndices, int[] skatCardIndices, int skatLen,
                         int gameTypeIdx, int positionIdx, boolean isHand, int bidValue) throws OrtException {

        // Validate inputs
        if (handCardIndices.length != MAX_HAND) {
            throw new IllegalArgumentException("Expected " + MAX_HAND + " hand cards, got " + handCardIndices.length);
        }

        // Prepare tensors (batch size 1)
        long[][] handCards = new long[1][MAX_HAND];
        for (int i = 0; i < MAX_HAND; i++) {
            handCards[0][i] = handCardIndices[i];
        }

        long[][] skatCards = new long[1][MAX_SKAT];
        for (int i = 0; i < MAX_SKAT; i++) {
            skatCards[0][i] = (i < skatCardIndices.length) ? skatCardIndices[i] : CARD_PAD_IDX;
        }

        long[] skatLenArr = {skatLen};
        long[] gameType = {gameTypeIdx};
        long[] position = {positionIdx};
        long[] isHandArr = {isHand ? 1 : 0};

        // Normalize bid
        float normalizedBid = (float) (bidValue - MIN_BID) / (MAX_BID - MIN_BID);
        float[] bid = {normalizedBid};

        // Create tensors
        try (OnnxTensor handTensor = OnnxTensor.createTensor(env, handCards);
             OnnxTensor skatTensor = OnnxTensor.createTensor(env, skatCards);
             OnnxTensor skatLenTensor = OnnxTensor.createTensor(env, skatLenArr);
             OnnxTensor gameTypeTensor = OnnxTensor.createTensor(env, gameType);
             OnnxTensor positionTensor = OnnxTensor.createTensor(env, position);
             OnnxTensor isHandTensor = OnnxTensor.createTensor(env, isHandArr);
             OnnxTensor bidTensor = OnnxTensor.createTensor(env, bid)) {

            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("hand_cards", handTensor);
            inputs.put("skat_cards", skatTensor);
            inputs.put("skat_len", skatLenTensor);
            inputs.put("game_type", gameTypeTensor);
            inputs.put("position", positionTensor);
            inputs.put("is_hand", isHandTensor);
            inputs.put("bid", bidTensor);

            var results = session.run(inputs);

            // Extract output
            Object value = results.get("win_prob").get().getValue();
            if (value instanceof float[]) {
                return ((float[]) value)[0];
            } else if (value instanceof float[][]) {
                return ((float[][]) value)[0][0];
            } else {
                throw new OrtException("Unexpected output type: " + value.getClass().getName());
            }
        }
    }

    @Override
    public void close() {
        if (session != null) {
            try {
                session.close();
            } catch (OrtException e) {
                logger.error("Error closing CardSetEvaluator session", e);
            }
        }
    }
}
