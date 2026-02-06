package org.jskat.ai.ml;

import ai.onnxruntime.OrtException;
import org.jskat.data.GameContract;
import org.jskat.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * ML-based AI player using attention-based transformer models.
 * <p>
 * This Pro player uses transformer architectures that can learn card relationships
 * like singleton detection, suit protection patterns, and Jack combinations.
 * <p>
 * Model architecture:
 * - Bidding Transformer: Attention-based pre-skat bidding (replaces dense bidding)
 * - Game Eval Transformer: Attention-based post-skat evaluation (replaces dense game eval)
 * - Card Play Transformer: Attention-based card play decisions
 */
public class MLPlayerPro extends AbstractMLPlayer {

    private static final Logger logger = LoggerFactory.getLogger(MLPlayerPro.class);

    // Game evaluation transformer model
    private CardSetEvaluatorWrapper gameEvalTransformerModel;

    // Bidding transformer model (optional - falls back to dense)
    private PreSkatTransformerWrapper biddingTransformerModel;

    /**
     * Creates a new ML Player Pro with default model paths.
     */
    public MLPlayerPro() {
        this(getDefaultModelPath("bidding_dense.onnx"),
             getDefaultModelPath("game_eval_transformer.onnx"),
             getDefaultModelPath("card_play_transformer.onnx"),
             getDefaultModelPath("bidding_transformer.onnx"));
    }

    /**
     * Creates a new ML Player Pro with custom model paths.
     */
    public MLPlayerPro(String biddingDensePath, String gameEvalTransformerPath, String cardPlayTransformerPath,
                       String biddingTransformerPath) {
        setPlayerName("MLPlayerPro");

        // Initialize shared models (bidding dense, card play transformer)
        initializeSharedModels(biddingDensePath, cardPlayTransformerPath);

        // Initialize game evaluation transformer model
        try {
            this.gameEvalTransformerModel = new CardSetEvaluatorWrapper(gameEvalTransformerPath);
        } catch (OrtException | IOException e) {
            logger.error("Failed to load game evaluation transformer model", e);
            throw new RuntimeException("Cannot initialize MLPlayerPro without game evaluation model", e);
        }

        // Initialize bidding transformer (optional - falls back to dense bidding model)
        if (biddingTransformerPath != null) {
            try {
                this.biddingTransformerModel = new PreSkatTransformerWrapper(biddingTransformerPath);
                logger.info("Bidding transformer loaded - using attention-based bidding");
            } catch (OrtException | IOException e) {
                logger.warn("Bidding transformer not found at {}, falling back to dense bidding model", biddingTransformerPath);
                this.biddingTransformerModel = null;
            }
        }

        logger.info("MLPlayerPro initialized (biddingTransformer={})", biddingTransformerModel != null);
    }

    @Override
    public void close() {
        super.close();
        if (gameEvalTransformerModel != null) {
            gameEvalTransformerModel.close();
        }
        if (biddingTransformerModel != null) {
            biddingTransformerModel.close();
        }
    }

    // ==================== Bidding (Transformer override) ====================

    /**
     * Uses bidding transformer (if available) to determine maximum bid value.
     * Also logs what dense bidding model would have bid for comparison.
     */
    @Override
    protected void calculateMLMaxBid() {
        // Use bidding transformer if available
        if (biddingTransformerModel != null) {
            try {
                CardList hand = knowledge.getOwnCards();
                Player position = knowledge.getPlayerPosition();

                logger.debug("=== BIDDING TRANSFORMER INFERENCE ===");
                logger.debug("Hand: {}", hand);
                logger.debug("Position: {}", position);

                // Convert hand to sorted ML indices
                int[] handIndices = new int[10];
                int idx = 0;
                for (Card card : hand) {
                    if (idx < 10) {
                        handIndices[idx++] = MLFeatureExtractor.getMLIndex(card);
                    }
                }
                Arrays.sort(handIndices);

                // Get position index
                int positionIdx = MLFeatureExtractor.getPositionIndex(position);

                PreSkatTransformerWrapper.Result result = biddingTransformerModel.predict(handIndices, positionIdx);

                int maxPickupBid = findMaxBid(result.pickupProbs, BID_CONFIDENCE_THRESHOLD);
                int maxHandBid = findMaxBid(result.handProbs, BID_CONFIDENCE_THRESHOLD);

                logger.debug("Transformer max pickup bid at {}: {}", BID_CONFIDENCE_THRESHOLD, maxPickupBid);
                logger.debug("Transformer max hand bid at {}: {}", BID_CONFIDENCE_THRESHOLD, maxHandBid);

                // Run dense model only for comparison logging when info logging is enabled
                if (logger.isInfoEnabled()) {
                    try {
                        float[] features = MLFeatureExtractor.extractBiddingFeatures(hand, position);
                        ONNXModelWrapper.BiddingResult denseResult = biddingDenseModel.predictBidding(features);
                        int denseMaxPickupBid = findMaxBid(denseResult.pickupProbs, BID_CONFIDENCE_THRESHOLD);
                        int denseMaxHandBid = findMaxBid(denseResult.handProbs, BID_CONFIDENCE_THRESHOLD);
                        logger.info("Dense bidding model would have bid: pickup={}, hand={} -> max={}",
                                denseMaxPickupBid, denseMaxHandBid, Math.max(denseMaxPickupBid, denseMaxHandBid));
                    } catch (OrtException e) {
                        logger.warn("Dense bidding inference failed for comparison logging", e);
                    }
                }

                if (maxHandBid > maxPickupBid) {
                    mlMaxBidValue = maxHandBid;
                    mlShouldPickupSkat = false;
                    logger.info("BIDDING TRANSFORMER DECISION: Hand game with max bid: {}", mlMaxBidValue);
                } else {
                    mlMaxBidValue = maxPickupBid;
                    mlShouldPickupSkat = true;
                    logger.info("BIDDING TRANSFORMER DECISION: Pickup game with max bid: {}", mlMaxBidValue);
                }

                return;

            } catch (OrtException e) {
                logger.warn("Bidding transformer inference failed, falling back to dense bidding model", e);
            }
        }

        // Fallback to parent implementation (dense bidding model)
        super.calculateMLMaxBid();
    }

    @Override
    protected GameSearchResult evaluateGameContract(CardList playingHand, CardList matadorHand, Player position, GameContract contract,
                                                     boolean isHandGame, int bidConstraint, CardList skatCards) {
        try {
            // Calculate game value using the matador hand (may include skat)
            int gameValue = calculateGameValue(contract, matadorHand);

            // Check if this game meets the bid constraint
            if (gameValue < bidConstraint) {
                logger.trace("Skipping {} - value {} < bid constraint {}",
                        contract.gameType(), gameValue, bidConstraint);
                return null;
            }

            // Convert hand cards to sorted ML indices
            int[] handIndices = new int[10];
            int idx = 0;
            for (Card card : playingHand) {
                if (idx < 10) {
                    handIndices[idx++] = MLFeatureExtractor.getMLIndex(card);
                }
            }
            Arrays.sort(handIndices);

            // Convert skat cards to ML indices (padded with PAD_INDEX)
            int[] skatIndices = new int[2];
            int skatLen = 0;
            if (skatCards != null && !skatCards.isEmpty()) {
                for (Card card : skatCards) {
                    if (skatLen < 2) {
                        skatIndices[skatLen++] = MLFeatureExtractor.getMLIndex(card);
                    }
                }
            }
            // Sort the actual skat cards
            if (skatLen == 2) {
                Arrays.sort(skatIndices);
            }
            // Pad remaining slots
            for (int i = skatLen; i < 2; i++) {
                skatIndices[i] = MLConstants.PAD_INDEX;
            }

            // Game type index
            int gameTypeIdx = MLFeatureExtractor.getGameTypeIndex(contract.gameType());

            // Position index
            int positionIdx = MLFeatureExtractor.getPositionIndex(position);

            // Run inference
            float winProb = gameEvalTransformerModel.predict(
                    handIndices, skatIndices, skatLen,
                    gameTypeIdx, positionIdx, isHandGame, bidConstraint);

            // Calculate expected value
            // EV = P(win) * gameValue - P(loss) * 2 * gameValue
            double expectedValue = winProb * gameValue - (1 - winProb) * 2 * gameValue;

            logger.debug("GAME EVAL TRANSFORMER: {} (hand={}) -> Value: {}, WinProb: {}, EV: {}",
                    contract.gameType(), contract.hand(), gameValue, winProb, expectedValue);

            return new GameSearchResult(contract, winProb, expectedValue, gameValue);

        } catch (OrtException e) {
            logger.error("Game eval transformer inference failed for contract: {}", contract, e);
            return null;
        }
    }
}
