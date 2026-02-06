package org.jskat.ai.ml;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * ONNX wrapper for the Card Play Transformer model.
 * Handles the multi-input transformer architecture for card play decisions.
 * <p>
 * This model processes the game state as a sequence and learns patterns like
 * trump tracking, void detection, and signaling.
 */
public class TransformerModelWrapper implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(TransformerModelWrapper.class);

    // Fixed sizes matching FastTransformerDataset
    public static final int MAX_HAND = 10;
    public static final int MAX_OUVERT = 10;
    public static final int MAX_HISTORY = 27;
    public static final int MAX_TRICK = 2;

    private final OrtEnvironment env;
    private final OrtSession session;

    /**
     * Creates a transformer model wrapper.
     *
     * @param modelPath Path to the transformer.onnx file
     * @throws OrtException If model loading fails
     * @throws IOException  If file cannot be read
     */
    public TransformerModelWrapper(String modelPath) throws OrtException, IOException {
        this.env = OrtEnvironment.getEnvironment();

        Path path = Paths.get(modelPath);
        if (!Files.exists(path)) {
            throw new IOException("Transformer model not found: " + modelPath);
        }

        logger.info("Loading transformer model from {}", modelPath);
        this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
        logger.info("Transformer model loaded successfully");
    }

    /**
     * Runs transformer inference for gameplay decisions.
     *
     * @param gameTypeIdx    Game type index (0-5: DIAMONDS, HEARTS, SPADES, CLUBS, GRAND, NULL)
     * @param declarerIdx    Relative declarer position (0=me, 1=left, 2=right)
     * @param isOuvert       1 if ouvert game, 0 otherwise
     * @param hand           Card indices in hand (0-31), padded to MAX_HAND
     * @param handLen        Actual number of cards in hand
     * @param ouvertHand     Declarer's visible cards in ouvert games (padded to MAX_OUVERT)
     * @param ouvertHandLen  Actual number of visible declarer cards (0 if not ouvert or I'm declarer)
     * @param history        History of played cards as (player, card) pairs, padded to MAX_HISTORY
     * @param historyLen     Actual number of moves in history
     * @param trick          Current trick as (player, card) pairs, padded to MAX_TRICK
     * @param trickLen       Actual number of cards in current trick
     * @param legalMask      Boolean mask of legal cards (length 32)
     * @return Logits array of length 32 (scores for each card)
     * @throws OrtException If inference fails
     */
    public float[] predict(
            int gameTypeIdx,
            int declarerIdx,
            int isOuvert,
            long[] hand,
            int handLen,
            long[] ouvertHand,
            int ouvertHandLen,
            long[][] history,
            int historyLen,
            long[][] trick,
            int trickLen,
            boolean[] legalMask
    ) throws OrtException {

        // Validate inputs
        if (hand.length != MAX_HAND) {
            throw new IllegalArgumentException("Hand must be padded to " + MAX_HAND + " cards");
        }
        if (ouvertHand.length != MAX_OUVERT) {
            throw new IllegalArgumentException("Ouvert hand must be padded to " + MAX_OUVERT + " cards");
        }
        if (history.length != MAX_HISTORY || (history.length > 0 && history[0].length != 2)) {
            throw new IllegalArgumentException("History must be " + MAX_HISTORY + " x 2");
        }
        if (trick.length != MAX_TRICK || (trick.length > 0 && trick[0].length != 2)) {
            throw new IllegalArgumentException("Trick must be " + MAX_TRICK + " x 2");
        }
        if (legalMask.length != 32) {
            throw new IllegalArgumentException("Legal mask must be length 32");
        }

        Map<String, OnnxTensor> inputs = new HashMap<>();

        try {
            // Create tensors - all with batch dimension of 1
            // game_type: [1]
            long[] gameTypeArr = {gameTypeIdx};
            inputs.put("game_type", OnnxTensor.createTensor(env, LongBuffer.wrap(gameTypeArr), new long[]{1}));

            // declarer: [1]
            long[] declarerArr = {declarerIdx};
            inputs.put("declarer", OnnxTensor.createTensor(env, LongBuffer.wrap(declarerArr), new long[]{1}));

            // is_ouvert: [1]
            long[] isOuvertArr = {isOuvert};
            inputs.put("is_ouvert", OnnxTensor.createTensor(env, LongBuffer.wrap(isOuvertArr), new long[]{1}));

            // hand: [1, MAX_HAND]
            long[][] hand2D = {hand};
            inputs.put("hand", OnnxTensor.createTensor(env, hand2D));

            // hand_len: [1]
            long[] handLenArr = {handLen};
            inputs.put("hand_len", OnnxTensor.createTensor(env, LongBuffer.wrap(handLenArr), new long[]{1}));

            // ouvert_hand: [1, MAX_OUVERT]
            long[][] ouvertHand2D = {ouvertHand};
            inputs.put("ouvert_hand", OnnxTensor.createTensor(env, ouvertHand2D));

            // ouvert_hand_len: [1]
            long[] ouvertHandLenArr = {ouvertHandLen};
            inputs.put("ouvert_hand_len", OnnxTensor.createTensor(env, LongBuffer.wrap(ouvertHandLenArr), new long[]{1}));

            // history: [1, MAX_HISTORY, 2]
            long[][][] history3D = {history};
            inputs.put("history", OnnxTensor.createTensor(env, history3D));

            // history_len: [1]
            long[] historyLenArr = {historyLen};
            inputs.put("history_len", OnnxTensor.createTensor(env, LongBuffer.wrap(historyLenArr), new long[]{1}));

            // trick: [1, MAX_TRICK, 2]
            long[][][] trick3D = {trick};
            inputs.put("trick", OnnxTensor.createTensor(env, trick3D));

            // trick_len: [1]
            long[] trickLenArr = {trickLen};
            inputs.put("trick_len", OnnxTensor.createTensor(env, LongBuffer.wrap(trickLenArr), new long[]{1}));

            // legal_mask: [1, 32]
            boolean[][] legalMask2D = {legalMask};
            inputs.put("legal_mask", OnnxTensor.createTensor(env, legalMask2D));

            // Run inference
            try (var results = session.run(inputs)) {
                // Extract logits [1, 32] -> [32]
                float[][] logits = (float[][]) results.get("logits").get().getValue();
                return logits[0];
            }

        } finally {
            // Clean up tensors
            for (OnnxTensor tensor : inputs.values()) {
                tensor.close();
            }
        }
    }

    @Override
    public void close() {
        if (session != null) {
            try {
                session.close();
            } catch (OrtException e) {
                logger.error("Error closing transformer session", e);
            }
        }
    }
}
