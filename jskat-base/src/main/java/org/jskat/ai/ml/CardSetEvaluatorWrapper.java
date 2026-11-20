package org.jskat.ai.ml;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.TensorInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
public class CardSetEvaluatorWrapper extends AbstractONNXWrapper {

    /** Maximum native batch size; callers cannot allocate an unbounded candidate tensor. */
    public static final int MAX_BATCH_SIZE = 32;
    private Boolean batchingSupported;
    private static final int MAX_HAND = 10;
    private static final int MAX_SKAT = 2;
    private static final int CARD_PAD_IDX = MLConstants.PAD_INDEX;

    /**
     * Creates a CardSetEvaluator wrapper.
     *
     * @param modelPath Path to the .onnx model file
     * @throws OrtException If model loading fails
     * @throws IOException  If file cannot be read
     */
    public CardSetEvaluatorWrapper(String modelPath) throws OrtException, IOException {
        this(modelPath, ONNXConstructionPolicy.INTERACTIVE);
    }

    public CardSetEvaluatorWrapper(String modelPath, ONNXConstructionPolicy policy) throws OrtException, IOException {
        super(modelPath, "CardSetEvaluator", policy);
    }

    /** Immutable inference request; array inputs are copied to retain the requested encoding. */
    public record Candidate(int[] handCards, int[] skatCards, int skatLen, int gameType,
                            int position, boolean hand, int bid) {
        public Candidate {
            if (handCards.length != MAX_HAND) throw new IllegalArgumentException("Expected ten hand cards");
            handCards = handCards.clone();
            skatCards = skatCards.clone();
        }
        @Override public int[] handCards() { return handCards.clone(); }
        @Override public int[] skatCards() { return skatCards.clone(); }
    }

    /** Fixed-one releases deliberately retain scalar execution without a failed batch probe. */
    public boolean supportsBatching() throws OrtException {
        if (batchingSupported == null) {
            // Mark scalar first, so failed metadata inspection is never repeated.
            batchingSupported = false;
            var inputs = session.getInputInfo();
            var result = session.getOutputInfo().get("win_prob");
            batchingSupported = inputs.size() == 7 && inputs.values().stream().allMatch(info ->
                    info.getInfo() instanceof TensorInfo tensor && tensor.getShape().length > 0
                            && tensor.getShape()[0] < 0)
                    && result != null && result.getInfo() instanceof TensorInfo output
                    && output.getShape().length > 0 && output.getShape()[0] < 0;
        }
        return batchingSupported;
    }

    /**
     * Predicts candidates in request order in bounded native batches. A dynamic export that
     * fails execution is disabled for future batches; the caller receives the failure so it
     * can report diagnostics before scalar recovery.
     */
    public float[] predictBatch(List<Candidate> candidates) throws OrtException {
        float[] predictions = new float[candidates.size()];
        if (candidates.isEmpty()) return predictions;
        if (!supportsBatching()) {
            for (int i = 0; i < candidates.size(); i++) {
                Candidate c = candidates.get(i);
                predictions[i] = predict(c.handCards, c.skatCards, c.skatLen, c.gameType,
                        c.position, c.hand, c.bid);
            }
            return predictions;
        }
        try {
            for (int offset = 0; offset < candidates.size(); offset += MAX_BATCH_SIZE) {
                int count = Math.min(MAX_BATCH_SIZE, candidates.size() - offset);
                float[] batch = runBatch(candidates.subList(offset, offset + count));
                System.arraycopy(batch, 0, predictions, offset, count);
            }
            return predictions;
        } catch (OrtException | RuntimeException failure) {
            batchingSupported = false;
            throw failure;
        }
    }

    private float[] runBatch(List<Candidate> candidates) throws OrtException {
        int size = candidates.size();
        long[][] hands = new long[size][MAX_HAND];
        long[][] skats = new long[size][MAX_SKAT];
        long[] lengths = new long[size];
        long[] games = new long[size];
        long[] positions = new long[size];
        long[] handFlags = new long[size];
        float[] bids = new float[size];
        for (int row = 0; row < size; row++) {
            Candidate c = candidates.get(row);
            for (int i = 0; i < MAX_HAND; i++) hands[row][i] = c.handCards[i];
            for (int i = 0; i < MAX_SKAT; i++) skats[row][i] =
                    i < c.skatCards.length ? c.skatCards[i] : CARD_PAD_IDX;
            lengths[row] = c.skatLen;
            games[row] = c.gameType;
            positions[row] = c.position;
            handFlags[row] = c.hand ? 1 : 0;
            bids[row] = (float) (c.bid - MLConstants.MIN_BID) / (MLConstants.MAX_BID - MLConstants.MIN_BID);
        }
        try (OnnxTensor handTensor = OnnxTensor.createTensor(env, hands);
             OnnxTensor skatTensor = OnnxTensor.createTensor(env, skats);
             OnnxTensor lengthTensor = OnnxTensor.createTensor(env, lengths);
             OnnxTensor gameTensor = OnnxTensor.createTensor(env, games);
             OnnxTensor positionTensor = OnnxTensor.createTensor(env, positions);
             OnnxTensor handFlagTensor = OnnxTensor.createTensor(env, handFlags);
             OnnxTensor bidTensor = OnnxTensor.createTensor(env, bids)) {
            Map<String, OnnxTensor> inputs = Map.of("hand_cards", handTensor, "skat_cards", skatTensor,
                    "skat_len", lengthTensor, "game_type", gameTensor, "position", positionTensor,
                    "is_hand", handFlagTensor, "bid", bidTensor);
            try (var results = session.run(inputs)) {
                Object value = results.get("win_prob").orElseThrow(() ->
                        new IllegalStateException("Missing win_prob output")).getValue();
                if (value instanceof float[] probabilities && probabilities.length == size) {
                    return probabilities;
                }
                if (value instanceof float[][] probabilities && probabilities.length == size) {
                    float[] output = new float[size];
                    for (int i = 0; i < size; i++) {
                        if (probabilities[i].length != 1) throw new OrtException("Expected one probability per candidate");
                        output[i] = probabilities[i][0];
                    }
                    return output;
                }
                throw new OrtException("Expected " + size + " win probabilities from batched evaluator");
            }
        }
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
        float normalizedBid = (float) (bidValue - MLConstants.MIN_BID) / (MLConstants.MAX_BID - MLConstants.MIN_BID);
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

            try (var results = session.run(inputs)) {
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
    }
}
