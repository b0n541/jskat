package org.jskat.util.rule;

import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.*;

/**
 * Interface for all skat rules objects
 */
public interface SkatRule {

    /**
     * Checks, whether the given card is allowed to be played, also considering
     * the rest of the hand
     *
     * @param gameType    Game type
     * @param initialCard First card in the trick
     * @param hand        All cards on the hand
     * @param card        Card to be checked
     * @return TRUE if the card is allowed to be played
     */
    public boolean isCardAllowed(GameType gameType, Card initialCard, CardList hand, Card card);

    /**
     * Checks whether a card beats another card
     *
     * @param gameType   Game type
     * @param cardToBeat Card to be beaten
     * @param card       Card to be checked
     * @return TRUE if the card beats the other card
     */
    public boolean isCardBeatsCard(GameType gameType, Card cardToBeat, Card card);

    /**
     * Checks whether a game is won
     *
     * @param gameData Game data
     * @return TRUE if the game was won
     */
    public boolean isGameWon(SkatGameData gameData);

    /**
     * Calculates the value for a game if won
     *
     * @param gameData Game data
     * @return Value of the game if won
     */
    public int getGameValueForWonGame(SkatGameData gameData);

    /**
     * Computes the value for a game
     *
     * @param gameData Game data
     * @return Game result
     */
    public int calcGameResult(SkatGameData gameData);

    /**
     * Checks whether one or more cards of a given suit are on the hand
     *
     * @param gameType Game type
     * @param hand     Cards on the players hand
     * @param suit     Suit color to search for
     * @return TRUE if one or more cards are on the hand
     */
    public boolean hasSuit(GameType gameType, CardList hand, Suit suit);

    /**
     * Calculates the trick winner
     *
     * @param gameType Game type
     * @param trick    Trick
     * @return Trick winner
     */
    public Player calculateTrickWinner(GameType gameType, Trick trick);

    /**
     * calculates the multiplier of a card list with regard to the ruleset
     *
     * @param gameData Game data
     * @return the multiplier for bidding and game announcement
     */
    public int getMultiplier(SkatGameData gameData);

    /**
     * Checks whether the game is played with jacks or without
     *
     * @param gameData Game data
     * @return TRUE if the game is played with jacks
     */
    public boolean isPlayWithJacks(SkatGameData gameData);

    /**
     * Checks whether a game was overbid
     *
     * @param gameData Game data
     * @return TRUE if the game was overbid
     */
    public boolean isOverbid(final SkatGameData gameData);
}
