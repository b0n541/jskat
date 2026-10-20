package org.jskat.ai.ml;

import ai.onnxruntime.OrtException;
import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameContract;
import org.jskat.util.*;
import org.jskat.util.rule.GrandRule;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.jskat.util.rule.SuitRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public abstract class AbstractMLPlayer extends AbstractAIPlayer implements AutoCloseable {

    /**
     * Creates an ML player before its model assets are initialized.
     */
    protected AbstractMLPlayer() {
    }

    /**
     * Logger for model inference and decision messages.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractMLPlayer.class);
    /**
     * Random source used when model inference cannot produce a decision.
     */
    protected final Random random = new Random();

    /**
     * Confidence threshold for bidding (only bid if win probability >= threshold)
     */
    protected static final float BID_CONFIDENCE_THRESHOLD = 0.70f;

    /**
     * Shared dense model used to estimate bids.
     */
    protected ONNXModelWrapper biddingDenseModel;
    /**
     * Shared transformer model used to select cards during play.
     */
    protected TransformerModelWrapper cardPlayTransformerModel;

    /**
     * Discard choice cached between discard and game announcement callbacks.
     */
    protected CardList cachedDiscard;
    /**
     * Contract cached between discard and game announcement callbacks.
     */
    protected GameContract cachedGameContract;

    /**
     * Highest bid accepted by the bidding model for the current hand.
     */
    protected int mlMaxBidValue = 0;
    /**
     * Whether the model prefers picking up the skat.
     */
    protected boolean mlShouldPickupSkat = true;
    /**
     * Whether bidding inference has run for the current hand.
     */
    protected boolean bidCalculated = false;

    /**
     * Initializes shared models (bidding dense, card play transformer).
     * Subclasses should call this and then initialize their own game evaluation model.
     */
    /**
     * Loads the shared bidding and card-play models.
     * Subclasses should call this before running inference.
     * @param models resolved paths to required model assets
     */
    protected ONNXConstructionPolicy constructionPolicy = ONNXConstructionPolicy.INTERACTIVE;
    private java.util.function.BiConsumer<String, Throwable> fallbackSink = (phase, error) -> {};

    /** Per-lease diagnostics; fallback randomness deliberately remains unseeded. */
    public void setFallbackSink(java.util.function.BiConsumer<String, Throwable> sink) {
        fallbackSink = java.util.Objects.requireNonNull(sink);
    }
    protected void reportFallback(String phase, Throwable error) {
        fallbackSink.accept(knowledge.getGameState() == null ? phase : knowledge.getGameState().name(), error);
    }

    protected void initializeSharedModels(final ResolvedModelSet models) {
        try {
            this.biddingDenseModel = new ONNXModelWrapper(models.biddingDense().toString(),
                    ONNXModelWrapper.ModelType.BIDDING_DENSE, constructionPolicy);
            this.cardPlayTransformerModel = new TransformerModelWrapper(models.cardPlayTransformer().toString(), constructionPolicy);
            logger.info("Card play transformer loaded");
        } catch (OrtException | IOException | RuntimeException e) {
            cleanupAfterFailure(e);
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

    protected void cleanupAfterFailure(Throwable failure) {
        try { close(); } catch (RuntimeException cleanup) { failure.addSuppressed(cleanup); }
    }

    protected void closeModels(AbstractONNXWrapper... models) {
        RuntimeException failure = null;
        for (var model : models) if (model != null) {
            try { model.close(); } catch (RuntimeException e) {
                if (failure == null) failure = e; else failure.addSuppressed(e);
            }
        }
        if (failure != null) throw failure;
    }

    @Override
    public void close() { closeModels(biddingDenseModel, cardPlayTransformerModel); }

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
            reportFallback("BIDDING", e);
            logger.error("Bidding inference failed, falling back to conservative bid", e);
            mlMaxBidValue = 18;
            mlShouldPickupSkat = true;
        }
    }

    /**
     * Finds the highest bid whose predicted success probability reaches a threshold.
     * @param probabilities success probability for each supported bid
     * @param threshold minimum probability required to bid
     * @return highest qualifying bid value, or zero when none qualifies
     */
    protected int findMaxBid(float[] probabilities, float threshold) {
        for (int i = probabilities.length - 1; i >= 0; i--) {
            if (probabilities[i] >= threshold) {
                return MLConstants.BID_VALUES[i];
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

    /**
     * Searches possible discards and contracts for the strongest expected outcome.
     * @param currentHand hand including the picked up skat
     * @param position player's seat
     * @return best discard and contract found
     */
    protected DiscardSearchResult searchDiscardSpace(CardList currentHand, Player position) {
        logger.debug("=== DISCARD SEARCH ===");
        logger.debug("Current hand (12 cards): {}", currentHand);

        DiscardSearchResult best = null;
        double bestEV = Double.NEGATIVE_INFINITY;

        List<Card> cards = new ArrayList<>();
        currentHand.forEach(cards::add);
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
            return new GameContract(GameType.DIAMONDS, !mlShouldPickupSkat);
        }
    }

    /**
     * Searches contracts that satisfy the current bid constraint.
     * @param playingHand cards retained for play
     * @param matadorHand hand used to count top cards
     * @param position player's seat
     * @param isHandGame whether the skat remains unseen
     * @param skatCards cards in the skat
     * @return best candidate contract found
     */
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
            reportFallback("DECLARING", e);
            logger.error("Error during game announcement search", e);
        }

        return best;
    }

    /**
     * Evaluates a specific game contract. Subclasses implement this with their game evaluation model.
     * Returns null if the game doesn't meet the bid constraint.
     */
    /**
     * Evaluates a candidate contract using the subclass's game model.
     * @param playingHand cards held while playing
     * @param matadorHand hand used to determine top cards
     * @param position player's seat
     * @param contract candidate contract
     * @param isHandGame whether the skat remains unseen
     * @param bidConstraint minimum value required by the bid
     * @param skatCards cards in the skat
     * @return result for the contract, or {@code null} when it fails the bid constraint
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
            reportFallback("TRICK_PLAYING", e);
            logger.error("Card play transformer error, falling back to random", e);
        }

        if (playable.isEmpty()) reportFallback("TRICK_PLAYING", new IllegalStateException("Inference returned no legal candidates"));
        logger.warn("Using random fallback for playCard");
        return playable.get(random.nextInt(playable.size()));
    }

    /**
     * Runs transformer inference for the currently playable cards.
     * @param playable legal cards
     * @return model logits indexed by card
     * @throws OrtException if ONNX inference fails
     */
    protected float[] playCardWithTransformer(CardList playable) throws OrtException {
        Player me = knowledge.getPlayerPosition();
        Player left = me.getLeftNeighbor();

        int gameTypeIdx = MLFeatureExtractor.getGameTypeIndex(knowledge.getGameType());

        Player declarer = knowledge.getDeclarer();
        int declarerIdx = relativePosition(declarer, me, left);

        long[] hand = new long[TransformerModelWrapper.MAX_HAND];
        CardList myCards = knowledge.getOwnCards();
        int handLen = Math.min(myCards.size(), TransformerModelWrapper.MAX_HAND);

        int[] handIndices = new int[handLen];
        for (int i = 0; i < handLen; i++) {
            handIndices[i] = MLFeatureExtractor.getMLIndex(myCards.get(i));
        }
        Arrays.sort(handIndices);
        for (int i = 0; i < handLen; i++) {
            hand[i] = handIndices[i];
        }

        long[][] history = new long[TransformerModelWrapper.MAX_HISTORY][2];
        int historyLen = 0;
        for (org.jskat.data.Trick t : knowledge.getCompletedTricks()) {
            for (Player p : new Player[]{Player.FOREHAND, Player.MIDDLEHAND, Player.REARHAND}) {
                Card c = t.getCard(p);
                if (c != null && historyLen < TransformerModelWrapper.MAX_HISTORY) {
                    history[historyLen][0] = relativePosition(p, me, left);
                    history[historyLen][1] = MLFeatureExtractor.getMLIndex(c);
                    historyLen++;
                }
            }
        }

        long[][] trick = new long[TransformerModelWrapper.MAX_TRICK][2];
        int trickLen = 0;
        CardList trickCards = knowledge.getTrickCards();
        Player currentPlayer = knowledge.getCurrentTrick().getForeHand();
        for (int i = 0; i < trickCards.size() && trickLen < TransformerModelWrapper.MAX_TRICK; i++) {
            Card c = trickCards.get(i);
            trick[trickLen][0] = relativePosition(currentPlayer, me, left);
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
            Arrays.sort(ouvertIndices);

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

    /**
     * Maps a player to a relative position index (0=me, 1=left neighbor, 2=right neighbor).
     */
    private static int relativePosition(Player player, Player me, Player left) {
        if (player == me) return 0;
        if (player == left) return 1;
        return 2;
    }

    // ==================== Utility Methods ====================

    /**
     * Normalizes logits into probabilities using the softmax function.
     * @param logits model output scores
     * @return normalized probabilities
     */
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

    /**
     * Calculates the announced contract's value for a hand.
     * @param contract announced contract
     * @param hand cards used to count top cards
     * @return game value
     */
    protected int calculateGameValue(GameContract contract, CardList hand) {
        int baseValue = SkatConstants.getGameBaseValue(contract.gameType(), contract.hand(), contract.ouvert());
        int multiplier = getGameMultiplier(contract, hand);
        return baseValue * multiplier;
    }

    /**
     * Calculates the multiplier contributed by top cards and announcements.
     * @param contract announced contract
     * @param hand cards held by the declarer
     * @return game multiplier
     */
    protected int getGameMultiplier(GameContract contract, CardList hand) {
        GameType gameType = contract.gameType();

        SkatRule rule = SkatRuleFactory.getSkatRules(gameType);
        int multiplier;
        if (rule instanceof SuitRule suitRule) {
            multiplier = suitRule.getBaseMultiplier(hand, gameType);
        } else if (rule instanceof GrandRule grandRule) {
            multiplier = grandRule.getBaseMultiplier(hand, gameType);
        } else {
            // NULL and RAMSCH have fixed values, multiplier is 1
            return 1;
        }

        if (contract.hand()) multiplier++;
        if (contract.schneider()) multiplier++;
        if (contract.schwarz()) multiplier++;
        if (contract.ouvert()) multiplier++;

        return multiplier;
    }

    // ==================== Helper Classes ====================

    /**
     * Candidate discard and the estimated outcome of the resulting game.
     */
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

    /**
     * Estimated outcome for a candidate game contract.
     */
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

    /**
     * Playable card paired with its model probability.
     */
    protected static class CardCandidate {
        final Card card;
        final float prob;

        CardCandidate(Card card, float prob) {
            this.card = card;
            this.prob = prob;
        }
    }
}
