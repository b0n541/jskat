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
    boolean isCardAllowed(GameType gameType, Card initialCard, CardList hand, Card card);

    /**
     * Checks whether a card beats another card
     *
     * @param gameType   Game type
     * @param cardToBeat Card to be beaten
     * @param card       Card to be checked
     * @return TRUE if the card beats the other card
     */
    boolean isCardBeatsCard(GameType gameType, Card cardToBeat, Card card);

    /**
     * Checks whether a game is won
     *
     * @param gameData Game data
     * @return TRUE if the game was won
     */
    boolean isGameWon(SkatGameData gameData);

    /**
     * Calculates the value for a game if won
     *
     * @param gameData Game data
     * @return Value of the game if won
     */
    int getGameValueForWonGame(SkatGameData gameData);

    /**
     * Computes the value for a game
     *
     * @param gameData Game data
     * @return Game result
     */
    int calcGameResult(SkatGameData gameData);

    /**
     * Computes the value for an overbid game
     *
     * @param gameData Game data
     * @return Game result
     */
    int calcOverbidGameResult(SkatGameData gameData);

    /**
     * Checks whether one or more cards of a given suit are on the hand
     *
     * @param gameType Game type
     * @param hand     Cards on the players hand
     * @param suit     Suit color to search for
     * @return TRUE if one or more cards are on the hand
     */
    boolean hasSuit(GameType gameType, CardList hand, Suit suit);

    /**
     * Calculates the trick winner
     *
     * @param gameType Game type
     * @param trick    Trick
     * @return Trick winner
     */
    Player calculateTrickWinner(GameType gameType, Trick trick);

    /**
     * Calculates the number of matadors (Jacks and trump cards in a row without gaps).
     * <p>
     * return Number of matadors
     */
    int getMatadors(SkatGameData gameData);

    /**
     * Checks whether the game is played with jacks or without
     *
     * @param gameData Game data
     * @return TRUE if the game is played with jacks
     */
    boolean isPlayWithJacks(SkatGameData gameData);

    /**
     * Checks whether a game was overbid
     *
     * @param gameData Game data
     * @return TRUE if the game was overbid
     */
    boolean isOverbid(final SkatGameData gameData);
}
