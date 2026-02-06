package org.jskat.ai.ml;

import ai.onnxruntime.OrtException;
import org.jskat.data.GameContract;
import org.jskat.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ML-based AI player using dense (MLP) ONNX models.
 * <p>
 * Uses the dense architecture for bidding and game evaluation:
 * - Bidding Dense: Evaluates initial 10-card hand for bidding decisions
 * - Game Eval Dense: Evaluates final hand + game type for optimal game selection
 * - Card Play Transformer: Attention-based card play decisions
 */
public class MLPlayer extends AbstractMLPlayer {

    private static final Logger logger = LoggerFactory.getLogger(MLPlayer.class);

    // Game evaluation dense model
    private ONNXModelWrapper gameEvalDenseModel;

    /**
     * Creates a new ML Player with default model paths.
     */
    public MLPlayer() {
        this(getDefaultModelPath("bidding_dense.onnx"),
             getDefaultModelPath("game_eval_dense.onnx"),
             getDefaultModelPath("card_play_transformer.onnx"));
    }

    /**
     * Creates a new ML Player with custom model paths.
     */
    public MLPlayer(String biddingDensePath, String gameEvalDensePath, String cardPlayTransformerPath) {
        setPlayerName("MLPlayer");

        // Initialize shared models (bidding dense, card play transformer)
        initializeSharedModels(biddingDensePath, cardPlayTransformerPath);

        // Initialize game evaluation dense model
        try {
            this.gameEvalDenseModel = new ONNXModelWrapper(gameEvalDensePath, ONNXModelWrapper.ModelType.GAME_EVAL_DENSE);
            logger.info("MLPlayer initialized successfully");
        } catch (OrtException | IOException e) {
            logger.error("Failed to load game evaluation dense model", e);
            throw new RuntimeException("Cannot initialize MLPlayer without game evaluation model", e);
        }
    }

    @Override
    public void close() {
        super.close();
        if (gameEvalDenseModel != null) {
            gameEvalDenseModel.close();
        }
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

            // Extract game eval features using the playing hand
            float[] features = MLFeatureExtractor.extractGameEvalFeatures(
                    playingHand, position, contract.gameType(), isHandGame, bidConstraint, skatCards);

            // Run inference
            float winProb = gameEvalDenseModel.predictGameEval(features);

            // Calculate expected value
            // EV = P(win) * gameValue - P(loss) * 2 * gameValue
            double expectedValue = winProb * gameValue - (1 - winProb) * 2 * gameValue;

            logger.debug("GAME EVAL DENSE: {} (hand={}) -> Value: {}, WinProb: {}, EV: {}",
                    contract.gameType(), contract.hand(), gameValue, winProb, expectedValue);

            return new GameSearchResult(contract, winProb, expectedValue, gameValue);

        } catch (OrtException e) {
            logger.error("Game eval inference failed for contract: {}", contract, e);
            return null;
        }
    }
}
