package org.jskat.ai.ml;

import ai.onnxruntime.OrtException;
import org.jskat.data.GameContract;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** ML Pro decisions with bounded contract batching and reuse of identical Null predictions. */
public class MLPlayerNext extends MLPlayerPro {
    private static final Logger logger = LoggerFactory.getLogger(MLPlayerNext.class);

    public MLPlayerNext(ResolvedModelSet models) {
        this(models, ONNXConstructionPolicy.INTERACTIVE);
    }

    public MLPlayerNext(ResolvedModelSet models, ONNXConstructionPolicy policy) {
        super(models, policy);
        setPlayerName("ML next");
    }

    @Override
    protected GameSearchResult searchGameAnnouncements(CardList playingHand, CardList matadorHand,
            Player position, boolean isHandGame, CardList skatCards) {
        try {
            if (!gameEvaluator().supportsBatching()) {
                return searchScalarContracts(playingHand, matadorHand, position, isHandGame, skatCards);
            }
            int bid = Math.max(knowledge.getHighestBid(position), 18);
            int[] hand = new int[10];
            int handIndex = 0;
            for (Card card : playingHand) {
                if (handIndex < hand.length) hand[handIndex++] = MLFeatureExtractor.getMLIndex(card);
            }
            Arrays.sort(hand);
            int[] skat = new int[]{MLConstants.PAD_INDEX, MLConstants.PAD_INDEX};
            int skatLength = 0;
            if (skatCards != null) {
                for (Card card : skatCards) {
                    if (skatLength < skat.length) skat[skatLength++] = MLFeatureExtractor.getMLIndex(card);
                }
            }
            if (skatLength == 2) Arrays.sort(skat);

            List<ValuedContract> contracts = new ArrayList<>();
            List<CardSetEvaluatorWrapper.Candidate> candidates = new ArrayList<>();
            Map<Integer, Integer> candidateByGame = new LinkedHashMap<>();
            for (GameContract contract : gameContracts(playingHand, isHandGame)) {
                int value = calculateGameValue(contract, matadorHand);
                if (value < bid) continue;
                int game = MLFeatureExtractor.getGameTypeIndex(contract.gameType());
                Integer candidateIndex = candidateByGame.get(game);
                if (candidateIndex == null) {
                    candidateIndex = candidates.size();
                    candidates.add(new CardSetEvaluatorWrapper.Candidate(hand, skat, skatLength, game,
                            MLFeatureExtractor.getPositionIndex(position), isHandGame, bid));
                    candidateByGame.put(game, candidateIndex);
                }
                // Null and Null Ouvert share model input, but keep their own values and order.
                contracts.add(new ValuedContract(contract, value, candidateIndex));
            }
            if (candidates.isEmpty()) return null;
            float[] probabilities = gameEvaluator().predictBatch(candidates);
            GameSearchResult best = null;
            double bestEV = Double.NEGATIVE_INFINITY;
            for (ValuedContract contract : contracts) {
                float winProbability = probabilities[contract.candidateIndex];
                double ev = winProbability * contract.value - (1 - winProbability) * 2 * contract.value;
                if (ev > bestEV) {
                    bestEV = ev;
                    best = new GameSearchResult(contract.contract, winProbability, ev, contract.value);
                }
            }
            return best;
        } catch (OrtException | RuntimeException failure) {
            reportFallback("DECLARING", failure);
            logger.warn("ML next batch evaluation failed; retrying candidates individually", failure);
        }
        return searchScalarContracts(playingHand, matadorHand, position, isHandGame, skatCards);
    }

    private GameSearchResult searchScalarContracts(CardList playingHand, CardList matadorHand,
            Player position, boolean isHandGame, CardList skatCards) {
        GameSearchResult best = null;
        double bestEV = Double.NEGATIVE_INFINITY;
        Map<Integer, Float> successfulPredictions = new LinkedHashMap<>();
        try {
            int bid = Math.max(knowledge.getHighestBid(position), 18);
            for (GameContract contract : gameContracts(playingHand, isHandGame)) {
                int value = calculateGameValue(contract, matadorHand);
                if (value < bid) continue;
                int game = MLFeatureExtractor.getGameTypeIndex(contract.gameType());
                Float probability = successfulPredictions.get(game);
                GameSearchResult result;
                if (probability != null) {
                    double ev = probability * value - (1 - probability) * 2 * value;
                    result = new GameSearchResult(contract, probability, ev, value);
                } else {
                    result = super.evaluateGameContract(playingHand, matadorHand, position,
                            contract, isHandGame, bid, skatCards);
                    // Cache successes only: a failed Null inference still permits an Ouvert retry.
                    if (result != null) successfulPredictions.put(game, result.winProbability);
                }
                if (result != null && result.expectedValue > bestEV) {
                    bestEV = result.expectedValue;
                    best = result;
                }
            }
        } catch (RuntimeException failure) {
            reportFallback("DECLARING", failure);
            logger.error("Error during ML next scalar contract search", failure);
        }
        return best;
    }

    private record ValuedContract(GameContract contract, int value, int candidateIndex) { }
}
