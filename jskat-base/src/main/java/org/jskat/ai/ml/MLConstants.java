package org.jskat.ai.ml;

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
     * Number of valid bid levels in Skat.
     */
    public static final int NUM_BID_LEVELS = 63;

    /**
     * Maximum bid value for normalization.
     */
    public static final int MAX_BID = 264;

    /**
     * Minimum bid value.
     */
    public static final int MIN_BID = 18;

    /**
     * Valid bid values in Skat, ordered from lowest to highest.
     * Length matches {@link #NUM_BID_LEVELS}.
     */
    public static final int[] BID_VALUES = {
            18, 20, 22, 23, 24, 27, 30, 33, 35, 36, 40, 44, 45, 46, 48,
            50, 54, 55, 59, 60, 63, 66, 70, 72, 77, 80, 81, 84, 88, 90,
            96, 99, 100, 108, 110, 117, 120, 121, 126, 130, 132, 135,
            140, 143, 144, 150, 153, 154, 156, 160, 162, 165, 168, 170,
            176, 180, 187, 192, 198, 204, 216, 240, 264
    };

    private MLConstants() {
        // Prevent instantiation
    }
}
