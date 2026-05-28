package org.jskat.ai;

import org.jskat.data.GameContract;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Wrapper for AI players that adds delay before actions when running in UI context.
 * This centralizes the delay logic without requiring modifications to each AI player implementation.
 */
public class AIPlayerDelayWrapper implements JSkatPlayer {

    private final JSkatPlayer wrappedPlayer;

    /**
     * Creates a wrapper around an AI player.
     *
     * @param player The AI player to wrap
     */
    public AIPlayerDelayWrapper(final JSkatPlayer player) {
        this.wrappedPlayer = player;
    }

    /**
     * Adds delay if AI delay is enabled.
     */
    private void addDelay() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String getPlayerName() {
        return wrappedPlayer.getPlayerName();
    }

    @Override
    public void setPlayerName(String newPlayerName) {
        wrappedPlayer.setPlayerName(newPlayerName);
    }

    @Override
    public void prepareForNewGame() {
        wrappedPlayer.prepareForNewGame();
    }

    @Override
    public void finalizeGame() {
        wrappedPlayer.finalizeGame();
    }

    @Override
    public void newGame(final Player position) {
        wrappedPlayer.newGame(position);
    }

    @Override
    public void setUpBidding() {
        wrappedPlayer.setUpBidding();
    }

    @Override
    public int bidMore(final int nextBidValue) {
        addDelay();
        return wrappedPlayer.bidMore(nextBidValue);
    }

    @Override
    public boolean holdBid(final int currBidValue) {
        addDelay();
        return wrappedPlayer.holdBid(currBidValue);
    }

    @Override
    public void bidByPlayer(Player player, int bidValue) {
        wrappedPlayer.bidByPlayer(player, bidValue);
    }

    @Override
    public void takeCards(CardList cards) {
        wrappedPlayer.takeCards(cards);
    }

    @Override
    public boolean pickUpSkat() {
        addDelay();
        return wrappedPlayer.pickUpSkat();
    }

    @Override
    public void takeSkat(CardList skat) {
        wrappedPlayer.takeSkat(skat);
    }

    @Override
    public GameContract announceGame() {
        addDelay();
        return wrappedPlayer.announceGame();
    }

    @Override
    public CardList discardSkat() {
        addDelay();
        return wrappedPlayer.discardSkat();
    }

    @Override
    public void setGameSummary(GameSummary gameSummary) {
        wrappedPlayer.setGameSummary(gameSummary);
    }

    @Override
    public void startGame(final Player singlePlayer, final GameContract announcement) {
        wrappedPlayer.startGame(singlePlayer, announcement);
    }

    @Override
    public void setGameState(SkatGameData.GameState gameState) {
        wrappedPlayer.setGameState(gameState);
    }

    @Override
    public void lookAtOuvertCards(CardList ouvertCards) {
        wrappedPlayer.lookAtOuvertCards(ouvertCards);
    }

    @Override
    public Card playCard() {
        addDelay();
        return wrappedPlayer.playCard();
    }

    @Override
    public void cardPlayed(Player player, Card card) {
        wrappedPlayer.cardPlayed(player, card);
    }

    @Override
    public void newTrick(int trickNo, Player trickForehand) {
        wrappedPlayer.newTrick(trickNo, trickForehand);
    }

    @Override
    public void showTrick(Trick trick) {
        wrappedPlayer.showTrick(trick);
    }

    @Override
    public boolean isAIPlayer() {
        return wrappedPlayer.isAIPlayer();
    }

    @Override
    public boolean isHumanPlayer() {
        return wrappedPlayer.isHumanPlayer();
    }

    @Override
    public boolean callContra() {
        addDelay();
        return wrappedPlayer.callContra();
    }

    @Override
    public boolean callRe() {
        addDelay();
        return wrappedPlayer.callRe();
    }

    @Override
    public boolean playGrandHand() {
        addDelay();
        return wrappedPlayer.playGrandHand();
    }
}
