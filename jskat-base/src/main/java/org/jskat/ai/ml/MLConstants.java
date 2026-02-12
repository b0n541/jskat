package org.jskat.ai.ml;

import org.jskat.util.SkatConstants;

/**
 * Shared constants for Machine Learning player components.
 * Ensures parity between Python training (skat-ml-models) and Java inference.
 */
public class MLConstants {
    /**
     * Bidding Dense Model Input (35 dimensions)
     * - 32: Hand cards (one-hot)
     * - 3: Player position (one-hot)
     */
    public static final int BIDDING_DENSE_INPUT_DIM = 35;

    /**
     * Game Evaluation Dense Model Input (75 dimensions)
     * - 32: Hand cards (one-hot)
     * - 32: Skat cards (one-hot)
     * - 6: Game type (one-hot)
     * - 3: Player position (one-hot)
     * - 1: Is Hand game (0/1 flag)
     * - 1: Bid level (normalized 0-1)
     */
    public static final int GAME_EVAL_DENSE_INPUT_DIM = 75;

    /**
     * Number of cards in a Skat deck.
     */
    public static final int NUM_CARDS = 32;

    /**
     * Padding index for variable-length card sequences.
     */
    public static final int PAD_INDEX = 32;

    /**
     * Valid bid values in Skat, ordered from lowest to highest.
     * Derived from {@link SkatConstants#bidOrder}.
     */
    public static final int[] BID_VALUES = SkatConstants.bidOrder.stream()
            .mapToInt(Integer::intValue).toArray();

    /**
     * Number of valid bid levels in Skat.
     */
    public static final int NUM_BID_LEVELS = BID_VALUES.length;

    /**
     * Minimum bid value.
     */
    public static final int MIN_BID = BID_VALUES[0];

    /**
     * Maximum bid value for normalization.
     */
    public static final int MAX_BID = BID_VALUES[BID_VALUES.length - 1];

    private MLConstants() {
        // Prevent instantiation
    }
}
