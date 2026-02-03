package org.jskat.ai.ml;

import ai.onnxruntime.OrtException;
import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameContract;
import org.jskat.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for ML-based AI players.
 * <p>
 * Provides shared functionality for:
 * - Bidding: Evaluates initial 10-card hand for bidding decisions
 * - Card Play: Uses transformer or dense model for card play decisions
 * <p>
 * Subclasses implement their own game evaluation (for discard/announce).
 */
public abstract class AbstractMLPlayer extends AbstractAIPlayer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMLPlayer.class);
    protected final Random random = new Random();

    /**
     * Valid bid values in Skat (from SkatConstants)
     */
    protected static final int[] BID_VALUES = {
            18, 20, 22, 23, 24, 27, 30, 33, 35, 36, 40, 44, 45, 46, 48,
            50, 54, 55, 59, 60, 63, 66, 70, 72, 77, 80, 81, 84, 88, 90,
            96, 99, 100, 108, 110, 117, 120, 121, 126, 130, 132, 135,
            140, 143, 144, 150, 153, 154, 156, 160, 162, 165, 168, 170,
            176, 180, 187, 192, 198, 204, 216, 240, 264
    };

    /**
     * Confidence threshold for bidding (only bid if win probability >= threshold)
     */
    protected static final float BID_CONFIDENCE_THRESHOLD = 0.70f;

    // ONNX model wrappers
    protected ONNXModelWrapper biddingDenseModel;
    protected TransformerModelWrapper cardPlayTransformerModel;

    // Cached decisions (since getCardsToDiscard and announceGame are called separately)
    protected CardList cachedDiscard;
    protected GameContract cachedGameContract;

    // ML-based bidding state
    protected int mlMaxBidValue = 0;
    protected boolean mlShouldPickupSkat = true;
    protected boolean bidCalculated = false;

    /**
     * Resolves the default model path, checking multiple locations.
     * Models are downloaded from skat-ml-models releases via Gradle task.
     */
    protected static String getDefaultModelPath(String modelFileName) {
        // Search paths in order of preference:
        // 1. Project-local .jskat/models/ (downloaded by Gradle)
        // 2-4. Parent directories (for running from submodules)
        // 5. User home (shared across projects)
        String[] searchPaths = {
                ".jskat/models/" + modelFileName,
                "../.jskat/models/" + modelFileName,
                "../../.jskat/models/" + modelFileName,
                "../../../.jskat/models/" + modelFileName,
                System.getProperty("user.home") + "/.jskat/models/" + modelFileName,
        };

        for (String path : searchPaths) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                logger.info("Found model at: {}", file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        }

        // Return project-local path as default (Gradle should have downloaded here)
        String defaultPath = ".jskat/models/" + modelFileName;
        logger.warn("Model not found. Run './gradlew downloadMlModels' to download. Trying: {}", defaultPath);
        return defaultPath;
    }

    /**
     * Initializes shared models (bidding dense, card play transformer).
     * Subclasses should call this and then initialize their own game evaluation model.
     */
    protected void initializeSharedModels(String biddingDensePath, String cardPlayTransformerPath) {
        try {
            this.biddingDenseModel = new ONNXModelWrapper(biddingDensePath, ONNXModelWrapper.ModelType.BIDDING_DENSE);
            this.cardPlayTransformerModel = new TransformerModelWrapper(cardPlayTransformerPath);
            logger.info("Card play transformer loaded");
        } catch (OrtException | IOException e) {
            logger.error("Failed to load ML models", e);
            throw new RuntimeException("Cannot initialize ML player without models", e);
        }
    }

    @Override
    public void prepareForNewGame() {
        mlMaxBidValue = 0;
        mlShouldPickupSkat = true;
        cachedDiscard = null;
        cachedGameContract = null;
        bidCalculated = false;
    }

    @Override
    public void finalizeGame() {
    }

    @Override
    public void startGame() {
    }

    @Override
    public boolean callContra() {
        return false;
    }

    @Override
    public boolean callRe() {
        return false;
    }

    @Override
    public boolean playGrandHand() {
        return false;
    }

    // ==================== Bidding ====================

    /**
     * Uses bidding model to determine maximum bid value and whether to pick up skat.
     */
    protected void calculateMLMaxBid() {
        try {
            CardList hand = knowledge.getOwnCards();
            Player position = knowledge.getPlayerPosition();

            logger.debug("=== BIDDING INFERENCE ===");
            logger.debug("Hand: {}", hand);
            logger.debug("Position: {}", position);

            float[] features = MLFeatureExtractor.extractBiddingFeatures(hand, position);
            ONNXModelWrapper.BiddingResult result = biddingDenseModel.predictBidding(features);

            int maxPickupBid = findMaxBid(result.pickupProbs, BID_CONFIDENCE_THRESHOLD);
            int maxHandBid = findMaxBid(result.handProbs, BID_CONFIDENCE_THRESHOLD);

            logger.debug("Max pickup bid at threshold {}: {}", BID_CONFIDENCE_THRESHOLD, maxPickupBid);
            logger.debug("Max hand bid at threshold {}: {}", BID_CONFIDENCE_THRESHOLD, maxHandBid);

            if (maxHandBid > maxPickupBid) {
                mlMaxBidValue = maxHandBid;
                mlShouldPickupSkat = false;
                logger.info("DECISION: Hand game with max bid: {}", mlMaxBidValue);
            } else {
                mlMaxBidValue = maxPickupBid;
                mlShouldPickupSkat = true;
                logger.info("DECISION: Pickup game with max bid: {}", mlMaxBidValue);
            }

        } catch (OrtException e) {
            logger.error("Bidding inference failed, falling back to conservative bid", e);
            mlMaxBidValue = 18;
            mlShouldPickupSkat = true;
        }
    }

    protected int findMaxBid(float[] probabilities, float threshold) {
        for (int i = probabilities.length - 1; i >= 0; i--) {
            if (probabilities[i] >= threshold) {
                return BID_VALUES[i];
            }
        }
        return 0;
    }

    @Override
    public int bidMore(int nextBidValue) {
        if (!bidCalculated) {
            calculateMLMaxBid();
            bidCalculated = true;
        }

        if (nextBidValue <= mlMaxBidValue) {
            logger.info("BIDDING: {} (max: {})", nextBidValue, mlMaxBidValue);
            return nextBidValue;
        } else {
            logger.info("PASSING at {} (max: {})", nextBidValue, mlMaxBidValue);
            return 0;
        }
    }

    @Override
    public boolean holdBid(int currBidValue) {
        if (!bidCalculated) {
            calculateMLMaxBid();
            bidCalculated = true;
        }

        boolean hold = currBidValue <= mlMaxBidValue;
        if (hold) {
            logger.info("HOLDING bid {} (max: {})", currBidValue, mlMaxBidValue);
        } else {
            logger.info("NOT HOLDING bid {} (max: {})", currBidValue, mlMaxBidValue);
        }
        return hold;
    }

    @Override
    public boolean pickUpSkat() {
        if (mlShouldPickupSkat) {
            logger.info("PICKING UP SKAT");
        } else {
            logger.info("DECLARING HAND GAME (not picking up skat)");
        }
        return mlShouldPickupSkat;
    }

    // ==================== Discard & Game Announcement ====================

    @Override
    public CardList getCardsToDiscard() {
        if (cachedDiscard != null) {
            logger.info("Returning cached discard: {}", cachedDiscard);
            return cachedDiscard;
        }

        CardList currentHand = knowledge.getOwnCards();
        Player position = knowledge.getPlayerPosition();

        logger.info("Searching discard space with {} cards", currentHand.size());

        DiscardSearchResult bestResult = searchDiscardSpace(currentHand, position);

        if (bestResult != null) {
            cachedDiscard = bestResult.discardPair;
            cachedGameContract = bestResult.gameContract;

            logger.info("Optimal discard: {} -> Game: {} (EV: {})",
                    cachedDiscard, cachedGameContract, bestResult.expectedValue);

            return cachedDiscard;
        } else {
            logger.warn("Discard search failed, using fallback");
            CardList sorted = new CardList(currentHand);
            sorted.sort(GameType.GRAND);
            cachedDiscard = new CardList();
            cachedDiscard.add(sorted.get(0));
            cachedDiscard.add(sorted.get(1));
            return cachedDiscard;
        }
    }

    protected DiscardSearchResult searchDiscardSpace(CardList currentHand, Player position) {
        logger.debug("=== DISCARD SEARCH ===");
        logger.debug("Current hand (12 cards): {}", currentHand);

        DiscardSearchResult best = null;
        double bestEV = Double.NEGATIVE_INFINITY;

        List<Card> cards = new ArrayList<>();
        for (Card card : currentHand) {
            cards.add(card);
        }
        int totalSearches = 0;
        int topResultsToLog = 5;
        List<DiscardSearchResult> topResults = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                Card card1 = cards.get(i);
                Card card2 = cards.get(j);

                CardList finalHand = new CardList(currentHand);
                finalHand.remove(card1);
                finalHand.remove(card2);

                CardList discardPair = new CardList();
                discardPair.add(card1);
                discardPair.add(card2);

                GameSearchResult gameResult = searchGameAnnouncements(finalHand, currentHand, position, false, discardPair);

                if (gameResult != null) {
                    DiscardSearchResult result = new DiscardSearchResult(discardPair, gameResult.gameContract, gameResult.expectedValue, gameResult.winProbability, gameResult.gameValue);

                    topResults.add(result);
                    topResults.sort((a, b) -> Double.compare(b.expectedValue, a.expectedValue));
                    if (topResults.size() > topResultsToLog) {
                        topResults.remove(topResults.size() - 1);
                    }

                    if (gameResult.expectedValue > bestEV) {
                        bestEV = gameResult.expectedValue;
                        best = result;
                    }
                }

                totalSearches++;
            }
        }

        logger.info("Evaluated {} discard combinations", totalSearches);

        logger.debug("Top {} discard options:", topResultsToLog);
        for (int i = 0; i < topResults.size(); i++) {
            DiscardSearchResult result = topResults.get(i);
            logger.debug("  {}. Discard {} -> {} (EV: {}, WinProb: {}, Value: {})",
                    i + 1, result.discardPair, result.gameContract.gameType(), result.expectedValue, result.winProbability, result.gameValue);
        }

        return best;
    }

    @Override
    public GameContract announceGame() {
        logger.info("=== ANNOUNCING GAME ===");

        if (cachedGameContract != null) {
            logger.info("ANNOUNCING: {} (from cached search during discard)", cachedGameContract);
            return cachedGameContract;
        }

        CardList hand = knowledge.getOwnCards();
        Player position = knowledge.getPlayerPosition();

        logger.info("Searching game announcements for hand game...");

        GameSearchResult result = searchGameAnnouncements(hand, hand, position, true, new CardList());

        if (result != null) {
            logger.info("ANNOUNCING: {}", result.gameContract);
            logger.info("   Win probability: {}%", result.winProbability * 100);
            logger.info("   Expected value: {}", result.expectedValue);
            return result.gameContract;
        } else {
            logger.warn("Game search failed, using fallback (Diamonds)");
            return new GameContract(GameType.DIAMONDS);
        }
    }

    protected GameSearchResult searchGameAnnouncements(CardList playingHand, CardList matadorHand, Player position, boolean isHandGame, CardList skatCards) {
        GameSearchResult best = null;
        double bestEV = Double.NEGATIVE_INFINITY;

        GameType[] gameTypes = {GameType.DIAMONDS, GameType.HEARTS, GameType.SPADES,
                GameType.CLUBS, GameType.GRAND, GameType.NULL};

        int highestBid = knowledge.getHighestBid(position);
        int bidConstraint = Math.max(highestBid, 18);

        try {
            for (GameType gameType : gameTypes) {
                // Base game (only for non-hand games)
                if (!isHandGame) {
                    GameContract baseGame = new GameContract(gameType);
                    GameSearchResult baseResult = evaluateGameContract(playingHand, matadorHand, position, baseGame, false, bidConstraint, skatCards);
                    if (baseResult != null && baseResult.expectedValue > bestEV) {
                        bestEV = baseResult.expectedValue;
                        best = baseResult;
                    }
                }

                // Hand game
                if (isHandGame) {
                    GameContract handGame = new GameContract(gameType).withHand();
                    GameSearchResult handResult = evaluateGameContract(playingHand, matadorHand, position, handGame, true, bidConstraint, skatCards);
                    if (handResult != null && handResult.expectedValue > bestEV) {
                        bestEV = handResult.expectedValue;
                        best = handResult;
                    }
                }

                // Null Ouvert checks
                if (gameType == GameType.NULL) {
                    if (!isHandGame) {
                        GameContract nullOuvert = new GameContract(GameType.NULL).withOuvert(playingHand);
                        GameSearchResult ouvertResult = evaluateGameContract(playingHand, matadorHand, position, nullOuvert, false, bidConstraint, skatCards);
                        if (ouvertResult != null && ouvertResult.expectedValue > bestEV) {
                            bestEV = ouvertResult.expectedValue;
                            best = ouvertResult;
                        }
                    }

                    if (isHandGame) {
                        GameContract nullHandOuvert = new GameContract(GameType.NULL).withHand().withOuvert(playingHand);
                        GameSearchResult handOuvertResult = evaluateGameContract(playingHand, matadorHand, position, nullHandOuvert, true, bidConstraint, skatCards);
                        if (handOuvertResult != null && handOuvertResult.expectedValue > bestEV) {
                            bestEV = handOuvertResult.expectedValue;
                            best = handOuvertResult;
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error during game announcement search", e);
        }

        return best;
    }

    /**
     * Evaluates a specific game contract. Subclasses implement this with their game evaluation model.
     * Returns null if the game doesn't meet the bid constraint.
     */
    protected abstract GameSearchResult evaluateGameContract(CardList playingHand, CardList matadorHand, Player position, GameContract contract,
                                                              boolean isHandGame, int bidConstraint, CardList skatCards);

    // ==================== Card Play ====================

    @Override
    public Card playCard() {
        CardList playable = getPlayableCards(knowledge.getTrickCards());

        if (playable.size() == 1) {
            return playable.get(0);
        }

        try {
            float[] logits = playCardWithTransformer(playable);
            float[] probs = softmax(logits);

            List<CardCandidate> candidates = new ArrayList<>();
            for (Card c : playable) {
                int idx = MLFeatureExtractor.getMLIndex(c);
                candidates.add(new CardCandidate(c, probs[idx]));
            }

            candidates.sort((a, b) -> Float.compare(b.prob, a.prob));

            logger.debug("=== CARD PLAY TRANSFORMER INFERENCE ===");
            for (int i = 0; i < Math.min(5, candidates.size()); i++) {
                CardCandidate cand = candidates.get(i);
                logger.debug("  {}. {} - Prob: {}%", i + 1, cand.card, String.format("%.2f", cand.prob * 100));
            }

            if (!candidates.isEmpty()) {
                return candidates.get(0).card;
            }

        } catch (Exception e) {
            logger.error("Card play transformer error, falling back to random", e);
        }

        logger.warn("Using random fallback for playCard");
        return playable.get(random.nextInt(playable.size()));
    }

    protected float[] playCardWithTransformer(CardList playable) throws OrtException {
        Player me = knowledge.getPlayerPosition();
        Player left = me.getLeftNeighbor();
        Player right = me.getRightNeighbor();

        int gameTypeIdx = MLFeatureExtractor.getGameTypeIndex(knowledge.getGameType());

        int declarerIdx;
        Player declarer = knowledge.getDeclarer();
        if (declarer == me) {
            declarerIdx = 0;
        } else if (declarer == left) {
            declarerIdx = 1;
        } else {
            declarerIdx = 2;
        }

        long[] hand = new long[TransformerModelWrapper.MAX_HAND];
        CardList myCards = knowledge.getOwnCards();
        int handLen = Math.min(myCards.size(), TransformerModelWrapper.MAX_HAND);

        int[] handIndices = new int[handLen];
        for (int i = 0; i < handLen; i++) {
            handIndices[i] = MLFeatureExtractor.getMLIndex(myCards.get(i));
        }
        java.util.Arrays.sort(handIndices);
        for (int i = 0; i < handLen; i++) {
            hand[i] = handIndices[i];
        }

        long[][] history = new long[TransformerModelWrapper.MAX_HISTORY][2];
        int historyLen = 0;
        for (org.jskat.data.Trick t : knowledge.getCompletedTricks()) {
            for (Player p : new Player[]{Player.FOREHAND, Player.MIDDLEHAND, Player.REARHAND}) {
                Card c = t.getCard(p);
                if (c != null && historyLen < TransformerModelWrapper.MAX_HISTORY) {
                    int relPlayer;
                    if (p == me) relPlayer = 0;
                    else if (p == left) relPlayer = 1;
                    else relPlayer = 2;

                    history[historyLen][0] = relPlayer;
                    history[historyLen][1] = MLFeatureExtractor.getMLIndex(c);
                    historyLen++;
                }
            }
        }

        long[][] trick = new long[TransformerModelWrapper.MAX_TRICK][2];
        int trickLen = 0;
        CardList trickCards = knowledge.getTrickCards();
        Player trickLeader = knowledge.getCurrentTrick().getForeHand();
        Player currentPlayer = trickLeader;
        for (int i = 0; i < trickCards.size() && trickLen < TransformerModelWrapper.MAX_TRICK; i++) {
            Card c = trickCards.get(i);
            int relPlayer;
            if (currentPlayer == me) relPlayer = 0;
            else if (currentPlayer == left) relPlayer = 1;
            else relPlayer = 2;

            trick[trickLen][0] = relPlayer;
            trick[trickLen][1] = MLFeatureExtractor.getMLIndex(c);
            trickLen++;

            currentPlayer = currentPlayer.getLeftNeighbor();
        }

        boolean[] legalMask = new boolean[32];
        for (Card c : playable) {
            legalMask[MLFeatureExtractor.getMLIndex(c)] = true;
        }

        int isOuvert = knowledge.isOuvertGame() ? 1 : 0;
        long[] ouvertHand = new long[TransformerModelWrapper.MAX_OUVERT];
        int ouvertHandLen = 0;

        if (knowledge.isOuvertGame() && declarer != me) {
            CardList declarerCards = new CardList();
            for (Card c : knowledge.getDeclarerPlayerCards()) {
                if (!knowledge.isCardPlayed(c)) {
                    declarerCards.add(c);
                }
            }

            int[] ouvertIndices = new int[declarerCards.size()];
            for (int i = 0; i < declarerCards.size(); i++) {
                ouvertIndices[i] = MLFeatureExtractor.getMLIndex(declarerCards.get(i));
            }
            java.util.Arrays.sort(ouvertIndices);

            ouvertHandLen = Math.min(ouvertIndices.length, TransformerModelWrapper.MAX_OUVERT);
            for (int i = 0; i < ouvertHandLen; i++) {
                ouvertHand[i] = ouvertIndices[i];
            }
        }

        return cardPlayTransformerModel.predict(
                gameTypeIdx,
                declarerIdx,
                isOuvert,
                hand,
                handLen,
                ouvertHand,
                ouvertHandLen,
                history,
                historyLen,
                trick,
                trickLen,
                legalMask
        );
    }

    // ==================== Utility Methods ====================

    protected float[] softmax(float[] logits) {
        float[] probs = new float[logits.length];
        float maxLogit = Float.NEGATIVE_INFINITY;
        for (float logit : logits) {
            if (logit > maxLogit) maxLogit = logit;
        }

        float sum = 0.0f;
        for (int i = 0; i < logits.length; i++) {
            probs[i] = (float) Math.exp(logits[i] - maxLogit);
            sum += probs[i];
        }

        for (int i = 0; i < probs.length; i++) {
            probs[i] /= sum;
        }
        return probs;
    }

    protected int calculateGameValue(GameContract contract, CardList hand) {
        GameType gameType = contract.gameType();

        if (gameType == GameType.NULL) {
            if (contract.hand() && contract.ouvert()) return 59;
            if (contract.hand()) return 35;
            if (contract.ouvert()) return 46;
            return 23;
        }

        int baseValue = SkatConstants.getGameBaseValue(gameType, contract.hand(), contract.ouvert());
        int multiplier = getGameMultiplier(contract, hand);

        return baseValue * multiplier;
    }

    protected int getGameMultiplier(GameContract contract, CardList hand) {
        GameType gameType = contract.gameType();
        List<Card> trumps = getTrumpsInOrder(gameType);

        int matadors = 0;
        if (trumps.isEmpty()) {
            return 1;
        }

        boolean hasFirst = hand.contains(trumps.get(0));

        if (hasFirst) {
            for (Card c : trumps) {
                if (hand.contains(c)) {
                    matadors++;
                } else {
                    break;
                }
            }
        } else {
            for (Card c : trumps) {
                if (!hand.contains(c)) {
                    matadors++;
                } else {
                    break;
                }
            }
        }

        int multiplier = 1;
        multiplier += matadors;

        if (contract.hand()) {
            multiplier++;
        }
        if (contract.schneider()) {
            multiplier++;
        }
        if (contract.schwarz()) {
            multiplier++;
        }
        if (contract.ouvert()) {
            multiplier++;
        }

        return multiplier;
    }

    protected List<Card> getTrumpsInOrder(GameType gameType) {
        List<Card> trumps = new ArrayList<>();

        trumps.add(Card.CJ);
        trumps.add(Card.SJ);
        trumps.add(Card.HJ);
        trumps.add(Card.DJ);

        if (gameType == GameType.GRAND) {
            return trumps;
        }

        if (gameType != GameType.NULL && gameType != GameType.RAMSCH) {
            Suit suit = gameType.getTrumpSuit();
            trumps.add(Card.getCard(suit, Rank.ACE));
            trumps.add(Card.getCard(suit, Rank.TEN));
            trumps.add(Card.getCard(suit, Rank.KING));
            trumps.add(Card.getCard(suit, Rank.QUEEN));
            trumps.add(Card.getCard(suit, Rank.NINE));
            trumps.add(Card.getCard(suit, Rank.EIGHT));
            trumps.add(Card.getCard(suit, Rank.SEVEN));
        }

        return trumps;
    }

    // ==================== Helper Classes ====================

    protected static class DiscardSearchResult {
        final CardList discardPair;
        final GameContract gameContract;
        final double expectedValue;
        final float winProbability;
        final int gameValue;

        DiscardSearchResult(CardList discardPair, GameContract gameContract, double expectedValue, float winProbability, int gameValue) {
            this.discardPair = discardPair;
            this.gameContract = gameContract;
            this.expectedValue = expectedValue;
            this.winProbability = winProbability;
            this.gameValue = gameValue;
        }
    }

    protected static class GameSearchResult {
        final GameContract gameContract;
        final float winProbability;
        final double expectedValue;
        final int gameValue;

        GameSearchResult(GameContract gameContract, float winProbability, double expectedValue, int gameValue) {
            this.gameContract = gameContract;
            this.winProbability = winProbability;
            this.expectedValue = expectedValue;
            this.gameValue = gameValue;
        }
    }

    protected static class CardCandidate {
        final Card card;
        final float prob;

        CardCandidate(Card card, float prob) {
            this.card = card;
            this.prob = prob;
        }
    }
}
