package org.jskat.util;

import java.util.List;

/**
 * Skat constants
 */
public final class SkatConstants {

    /**
     * All possible bid values ordered from the lowest to the highest bid value
     */
    public static final List<Integer> bidOrder = List.of(18, 20, 22, 23,
            24, 27, 30, 33, 35, 36, 40, 44, 45, 46, 48, 50, 54, 55, 59, 60, 63,
            66, 70, 72, 77, 80, 81, 84, 88, 90, 96, 99, 100, 108, 110, 117,
            120, 121, 126, 130, 132, 135, 140, 143, 144, 150, 153, 154, 156,
            160, 162, 165, 168, 170, 176, 180, 187, 192, 198, 204, 216, 240,
            264);

    /**
     * Minimum points to win a game.
     */
    public static final Integer MIN_WINNING_POINTS = 61;
    /**
     * Minimum points to win a game with schneider.
     */
    public static final Integer MIN_SCHNEIDER_WINNING_POINTS = 90;
    /**
     * Minimum points to win a game with schwarz.
     */
    public static final Integer MIN_SCHWARZ_WINNING_POINTS = 120;

    /**
     * Gets the next valid bid value
     *
     * @param currBidValue Current bid value
     * @return Next valid bid value or the last possible bid value
     */
    public static int getNextBidValue(int currBidValue) {
        return bidOrder.stream()
                .filter(bidValue -> bidValue > currBidValue)
                .findFirst()
                .orElse(bidOrder.get(bidOrder.size() - 1));
    }

    /**
     * Returns the multiplier for the game
     *
     * @param gameType Game type
     * @param hand     TRUE if game is a hand game
     * @param ouvert   TRUE if game is an ouvert game
     * @return Multiplier
     */
    public static int getGameBaseValue(GameType gameType, boolean hand,
                                       boolean ouvert) {

        int multiplier = 0;

        switch (gameType) {
            case PASSED_IN:
                break;
            case CLUBS:
                multiplier = 12;
                break;
            case SPADES:
                multiplier = 11;
                break;
            case HEARTS:
                multiplier = 10;
                break;
            case DIAMONDS:
                multiplier = 9;
                break;
            case NULL:
                if (hand) {
                    if (ouvert) {
                        multiplier = 59;
                    } else {
                        multiplier = 35;
                    }
                } else {
                    if (ouvert) {
                        multiplier = 46;
                    } else {
                        multiplier = 23;
                    }
                }
                break;
            case GRAND:
                multiplier = 24;
                break;
            case RAMSCH:
                multiplier = 1;
                break;
        }

        return multiplier;
    }

    /**
     * Returns the game value after the Seeger-Fabian system
     *
     * @param declarer        TRUE, if calculation should be done for declarer of game
     * @param gameValue       Game value
     * @param numberOfPlayers Number of players on the skat table
     * @return Tournament value
     */
    public static int getTournamentGameValue(boolean declarer,
                                             int gameValue, int numberOfPlayers) {

        int result = 0;

        if (declarer) {
            // calculation for declarer of the game
            if (gameValue > 0) {

                result = gameValue + 50;
            } else {

                result = gameValue - 50;
            }
        } else {
            // calculation for opponents
            if (gameValue < 0) {

                if (numberOfPlayers == 3) {

                    result = 40;
                } else if (numberOfPlayers == 4) {

                    result = 30;
                }
            }
        }

        return result;
    }
}
