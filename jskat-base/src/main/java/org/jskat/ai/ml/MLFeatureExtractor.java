package org.jskat.ai.ml;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Extracts features from card hands for ML model inference.
 * Implements feature engineering matching the skat-ml-models interface.
 */
public class MLFeatureExtractor {

    /**
     * Card ordering for ML models (jacks first, then suits).
     * Matches the skat-ml-models CARDS array.
     */
    private static final Card[] ML_CARD_ORDER = {
            // Jacks (indices 0-3)
            Card.CJ, Card.SJ, Card.HJ, Card.DJ,
            // Clubs (indices 4-10)
            Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8, Card.C7,
            // Spades (indices 11-17)
            Card.SA, Card.ST, Card.SK, Card.SQ, Card.S9, Card.S8, Card.S7,
            // Hearts (indices 18-24)
            Card.HA, Card.HT, Card.HK, Card.HQ, Card.H9, Card.H8, Card.H7,
            // Diamonds (indices 25-31)
            Card.DA, Card.DT, Card.DK, Card.DQ, Card.D9, Card.D8, Card.D7,
    };

    /**
     * Reverse mapping: Card -> ML index
     */
    private static final Map<Card, Integer> CARD_TO_ML_INDEX = new HashMap<>();

    static {
        for (int i = 0; i < ML_CARD_ORDER.length; i++) {
            CARD_TO_ML_INDEX.put(ML_CARD_ORDER[i], i);
        }
    }

    /**
     * Extracts features for the Bidding Dense model (35 dimensions).
     * Format: [32 one-hot cards] + [3 one-hot position]
     *
     * @param cards    The card hand (typically 10 cards)
     * @param position Player position
     * @return Feature vector of length 35
     */
    public static float[] extractBiddingFeatures(CardList cards, Player position) {
        float[] features = new float[MLConstants.BIDDING_DENSE_INPUT_DIM];

        // One-hot card encoding (32 dimensions)
        for (Card card : cards) {
            int mlIndex = CARD_TO_ML_INDEX.get(card);
            features[mlIndex] = 1.0f;
        }

        // Player position one-hot (3 dims, starting at index 32)
        int posIndex = position.ordinal();
        if (posIndex >= 0 && posIndex <= 2) {
            features[32 + posIndex] = 1.0f;
        }

        return features;
    }

    /**
     * Extracts features for the Game Evaluation Dense model (75 dimensions).
     * Format: [32 hand] + [32 skat] + [6 game type] + [3 position] + [1 hand flag] + [1 bid]
     *
     * @param cards      The final card hand (10 cards after discarding)
     * @param position   Player position
     * @param gameType   The game type (DIAMONDS, HEARTS, SPADES, CLUBS, GRAND, NULL)
     * @param isHandGame Whether this is a hand game
     * @param bidLevel   The bid level that must be met
     * @param skatCards  The cards in the skat (2 cards for pickup, empty for hand games). Can be null.
     * @return Feature vector of length 75
     */
    public static float[] extractGameEvalFeatures(CardList cards, Player position, GameType gameType,
                                                   boolean isHandGame, int bidLevel, CardList skatCards) {
        float[] features = new float[MLConstants.GAME_EVAL_DENSE_INPUT_DIM];

        // 1. Hand cards (32 dims, indices 0-31)
        for (Card card : cards) {
            int mlIndex = CARD_TO_ML_INDEX.get(card);
            features[mlIndex] = 1.0f;
        }

        // 2. Skat cards (32 dims, indices 32-63)
        if (skatCards != null) {
            for (Card card : skatCards) {
                int mlIndex = CARD_TO_ML_INDEX.get(card);
                features[32 + mlIndex] = 1.0f;
            }
        }

        int offset = 64;

        // 3. Game type one-hot (6 dimensions, indices 64-69)
        features[offset] = (gameType == GameType.DIAMONDS) ? 1.0f : 0.0f;
        features[offset + 1] = (gameType == GameType.HEARTS) ? 1.0f : 0.0f;
        features[offset + 2] = (gameType == GameType.SPADES) ? 1.0f : 0.0f;
        features[offset + 3] = (gameType == GameType.CLUBS) ? 1.0f : 0.0f;
        features[offset + 4] = (gameType == GameType.GRAND) ? 1.0f : 0.0f;
        features[offset + 5] = (gameType == GameType.NULL) ? 1.0f : 0.0f;
        offset += 6;

        // 4. Position one-hot (3 dimensions, indices 70-72)
        int posIndex = position.ordinal();
        if (posIndex >= 0 && posIndex <= 2) {
            features[offset + posIndex] = 1.0f;
        }
        offset += 3;

        // 5. Is hand game (1 dimension, index 73)
        features[offset++] = isHandGame ? 1.0f : 0.0f;

        // 6. Bid level normalized (1 dimension, index 74)
        features[offset] = bidLevel / (float) MLConstants.MAX_BID;

        return features;
    }

    /**
     * Gets the ML index for a card (0-31).
     * This is the canonical card encoding used by skat-ml-models.
     */
    public static int getMLIndex(Card card) {
        return CARD_TO_ML_INDEX.get(card);
    }

    /**
     * Gets the game type index (0-5).
     * Order: DIAMONDS, HEARTS, SPADES, CLUBS, GRAND, NULL
     */
    public static int getGameTypeIndex(GameType gameType) {
        return switch (gameType) {
            case DIAMONDS -> 0;
            case HEARTS -> 1;
            case SPADES -> 2;
            case CLUBS -> 3;
            case GRAND -> 4;
            case NULL -> 5;
            default -> throw new IllegalArgumentException("Unsupported game type: " + gameType);
        };
    }

    /**
     * Gets the position index (0-2).
     * Order: FOREHAND, MIDDLEHAND, REARHAND
     */
    public static int getPositionIndex(Player position) {
        return position.ordinal();
    }
}
