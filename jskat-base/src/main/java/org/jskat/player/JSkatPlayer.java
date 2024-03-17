package org.jskat.player;

import org.jskat.data.GameContract;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Player interface for JSkat players
 */
public interface JSkatPlayer {

    /**
     * This function can be used to do some clean up
     */
    void prepareForNewGame();

    /**
     * This function can be used to do some finalizing calculations on the
     * finished game
     */
    void finalizeGame();

    /**
     * Informs the player about the start of a new game
     *
     * @param position Initial sitting position of the player
     */
    void newGame(Player position);

    /**
     * Notifies the player of the start of bidding for a new game
     */
    void setUpBidding();

    /**
     * Asks the player whether it wants to bid higher or not
     *
     * @param nextBidValue Next bid value
     * @return A bid value equal or higher as the next bid value 0 if the
     * player passes
     */
    int bidMore(int nextBidValue);

    /**
     * Asks the player whether it wants to hold a bid from the announcer
     *
     * @param currBidValue Current bid value
     * @return TRUE if the player holds the bid
     */
    boolean holdBid(int currBidValue);

    /**
     * Informs the player about a bid value that was announced or hold
     *
     * @param player   Player who announced or hold the bid
     * @param bidValue Bid value
     */
    void bidByPlayer(Player player, int bidValue);

    /**
     * Takes cards from the dealer
     *
     * @param cards New cards
     */
    void takeCards(CardList cards);

    /**
     * @return <code>true</code>, if the player wants to play a grand hand in a
     * BockRamsch game
     */
    boolean playGrandHand();

    /**
     * Asks the player to call Contra as opponent player
     *
     * @return <code>true</code>, if the player wants to call Contra
     */
    boolean callContra();

    /**
     * Asks the player to call Re a declarer player, after an opponent did call
     * Contra
     *
     * @return <code>true</code>, if the player wants to call Re
     */
    boolean callRe();

    /**
     * Checks whether the player wants to look into the skat
     *
     * @return TRUE if the player wants to look into the skat
     */
    boolean pickUpSkat();

    /**
     * Take the skat as a single player.<br>
     * When the method is done, the skat CardList must still contain exactly two
     * cards. Otherwise, a SkatHandlingException might be thrown.
     *
     * @param skat The skat Cards
     */
    void takeSkat(CardList skat);

    /**
     * Asks the player for the game he wants to play
     *
     * @return Game announcement
     */
    GameContract announceGame();

    /**
     * Start the game: inform player of game type, trumpf and special options
     *
     * @param singlePlayer Single player position
     * @param contract     Game announcement containing all relevant information about
     *                     the new game
     */
    void startGame(Player singlePlayer, GameContract contract);

    /**
     * Sets the current game state.
     *
     * @param gameState Game state
     */
    void setGameState(SkatGameData.GameState gameState);

    /**
     * Shows the cards of the single player to the opponents in Ouvert games
     *
     * @param ouvertCards Cards of the single player in an Ouvert game
     */
    void lookAtOuvertCards(CardList ouvertCards);

    /**
     * Get next Card to play
     *
     * @return Card to be played
     */
    Card playCard();

    /**
     * Informs the player about a card that was played
     *
     * @param player Player who played the card
     * @param card   Card that was played
     */
    void cardPlayed(Player player, Card card);

    /**
     * Makes the current trick known to the players when it is initiated
     *
     * @param trickNo       Trick no in game
     * @param trickForehand Forehand for the trick
     */
    void newTrick(int trickNo, Player trickForehand);

    /**
     * Makes the current trick known to the players when it is complete
     *
     * @param trick Trick information
     */
    void showTrick(Trick trick);

    /**
     * Gets the name of the skat player
     *
     * @return Player name
     */
    String getPlayerName();

    /**
     * Sets the name of the skat player
     *
     * @param newPlayerName Player name to be set
     */
    void setPlayerName(String newPlayerName);

    /**
     * Checks whether the player is a human player
     *
     * @return TRUE if the player is a human player
     */
    boolean isHumanPlayer();

    /**
     * Checks whether the player is an AI player
     *
     * @return TRUE if the player is an AI player
     */
    boolean isAIPlayer();

    /**
     * Checks whether the player is the declaring player
     *
     * @return TRUE if the player is the declaring player
     */
    boolean isDeclarer();

    /**
     * Asks for the new skat cards during discarding
     *
     * @return CardList The new cards for the skat
     */
    CardList discardSkat();

    /**
     * Informs the player about the game
     *
     * @param gameSummary Game summary
     */
    void setGameSummary(GameSummary gameSummary);

    /**
     * Holds all player states
     */
    enum PlayerState {
        /**
         * Player waits
         */
        WAITING,
        /**
         * Player receives cards during dealing
         */
        DEALING,
        /**
         * Player takes part in bidding
         */
        BIDDING,
        /**
         * Player looks into skat and discards two cards
         */
        DISCARDING,
        /**
         * Player announces a game
         */
        GAME_ANNOUNCING,
        /**
         * Player plays the tricks
         */
        PLAYING
    }
}