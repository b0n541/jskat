package org.jskat.player;

import org.jskat.data.GameContract;
import org.jskat.data.GameSummary;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract JSkat player implementation
 */
public abstract class AbstractJSkatPlayer implements JSkatPlayer {

    protected Logger log = LoggerFactory.getLogger(AbstractJSkatPlayer.class);

    /**
     * Player name
     */
    protected String playerName;
    /**
     * Player state
     */
    protected JSkatPlayer.PlayerState playerState;
    /**
     * Internal player knowledge
     */
    private final PlayerKnowledge internalKnowledge = new PlayerKnowledge();
    /**
     * Immutable Player knowledge
     */
    protected ImmutablePlayerKnowledge knowledge = internalKnowledge;
    /**
     * Skat rules for the current skat series
     */
    protected SkatRule rules;
    /**
     * Summary of the skat game
     */
    protected GameSummary gameSummary;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setPlayerName(final String newPlayerName) {

        playerName = newPlayerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getPlayerName() {

        return playerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setUpBidding() {

        setState(PlayerState.BIDDING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void newGame(final Player newPosition) {

        playerState = null;
        rules = null;
        gameSummary = null;
        internalKnowledge.resetCurrentGameData();
        internalKnowledge.setPlayerPosition(newPosition);

        prepareForNewGame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void takeCards(final CardList cards) {
        internalKnowledge.addOwnCards(cards);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void startGame(final Player declarer, final GameContract contract) {

        playerState = PlayerState.PLAYING;
        internalKnowledge.setDeclarer(declarer);
        internalKnowledge.setContract(contract);

        if (contract.ouvert()) {
            internalKnowledge.addDeclarerCards(contract.ouvertCards());
        }

        rules = SkatRuleFactory.getSkatRules(contract.gameType());
        if (!GameType.PASSED_IN.equals(contract.gameType())) {
            log.debug("Starting game for " + getPlayerName() + ": "
                    + contract.gameType() + " (rules="
                    + rules.getClass() + ")");
        }

        startGame();
    }

    /**
     * A method that is called by the abstract player to allow individual
     * players to implement certain start-up operations.
     */
    public abstract void startGame();

    /**
     * {@inheritDoc}
     */
    @Override
    public final void takeSkat(final CardList skatCards) {

        log.debug("Skat cards: " + skatCards);

        internalKnowledge.setSkat(skatCards);
        internalKnowledge.addOwnCards(skatCards);
    }

    /**
     * Sets the state of the player
     *
     * @param newState State to be set
     */
    protected final void setState(final JSkatPlayer.PlayerState newState) {

        playerState = newState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void bidByPlayer(final Player player, final int bidValue) {

        internalKnowledge.setHighestBid(player, bidValue);
    }

    /**
     * Gets all playable cards.
     *
     * @param trick Current trick
     * @return List of all playable cards
     */
    public final CardList getPlayableCards(final CardList trick) {

        boolean isCardAllowed;
        final CardList result = new CardList();

        log.debug("game type: " + internalKnowledge.getGameType());
        log.debug("player cards (" + internalKnowledge.getOwnCards().size() + "): " + internalKnowledge.getOwnCards());
        log.debug("trick size: " + trick.size());

        for (final Card card : internalKnowledge.getOwnCards()) {
            if (trick.size() > 0 &&
                    rules.isCardAllowed(internalKnowledge.getGameType(), trick.get(0), internalKnowledge.getOwnCards(), card)) {

                log.debug("Card: " + card + " is allowed after initial card: " + trick.get(0));
                isCardAllowed = true;
            } else {
                isCardAllowed = trick.size() == 0;
            }

            if (isCardAllowed) {
                result.add(card);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void cardPlayed(final Player player, final Card card) {
        internalKnowledge.setCardPlayed(player, card);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void newTrick(final int trickNo, final Player trickForehand) {
        internalKnowledge.setNextTrick(trickNo, trickForehand);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void showTrick(final Trick trick) {
        internalKnowledge.addCompletedTrick(trick);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isHumanPlayer() {
        return !isAIPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isDeclarer() {
        return GameType.RAMSCH.equals(internalKnowledge.getGameType())
                || internalKnowledge.getDeclarer().equals(internalKnowledge.getPlayerPosition());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void lookAtOuvertCards(final CardList ouvertCards) {
        internalKnowledge.addDeclarerCards(ouvertCards);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CardList discardSkat() {

        final CardList result = new CardList();

        log.debug("Player cards before discarding: " + internalKnowledge.getOwnCards());

        result.addAll(getCardsToDiscard());

        internalKnowledge.removeOwnCards(result.getImmutableCopy());

        log.debug("Player cards after discarding: " + internalKnowledge.getOwnCards());

        return result;
    }

    protected abstract CardList getCardsToDiscard();

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setGameSummary(final GameSummary gameSummary) {
        this.gameSummary = gameSummary;
    }

    /**
     * Sets a new logger for the abstract skat player
     *
     * @param newLogger New logger
     */
    public final void setLogger(final Logger newLogger) {
        log = newLogger;
    }
}