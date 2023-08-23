package org.jskat.util;

import java.util.Arrays;
import java.util.List;

/**
 * Contains all ranks.
 */
public enum Rank {
    /**
     * Seven
     */
    SEVEN("7", "Seven", 0, 0, 0),
    /**
     * Eight
     */
    EIGHT("8", "Eight", 1, 1, 0),
    /**
     * Nine
     */
    NINE("9", "Nine", 2, 2, 0),
    /**
     * Queen or Ober
     */
    QUEEN("Q", "Queen", 3, 5, 3),
    /**
     * King or König
     */
    KING("K", "King", 4, 6, 4),
    /**
     * Ten
     */
    TEN("T", "Ten", 5, 3, 10),
    /**
     * Ace or Daus
     */
    ACE("A", "Ace", 6, 7, 11),
    /**
     * Jack or Unter
     */
    JACK("J", "Jack", 7, 4, 2);

    private final String shortString;
    private final String longString;
    private final int suitGrandOrder;
    private final int nullOrder;
    private final int points;

    /**
     * Constructor
     */
    Rank(final String shortString,
            final String longString,
            final int suitGrandOrder,
            final int nullOrder,
            final int points) {
        this.shortString = shortString;
        this.longString = longString;
        this.suitGrandOrder = suitGrandOrder;
        this.nullOrder = nullOrder;
        this.points = points;
    }

    /**
     * Gets a short string representation of the rank.
     *
     * @return Short string representation of the rank
     */
    public String getShortString() {
        return shortString;
    }

    /**
     * Gets a long string representation of the rank.
     *
     * @return Long string representation of the rank
     */
    public String getLongString() {
        return longString;
    }

    /**
     * Gets the order of the rank in suit and grand games.
     *
     * @return Order in suit and grand games
     */
    public int getSuitGrandOrder() {
        return suitGrandOrder;
    }

    /**
     * Gets the order of the rank in null games.
     *
     * @return Order in null games
     */
    public int getNullOrder() {
        return nullOrder;
    }

    /**
     * Gets the points of the rank for game value calculation.
     *
     * @return Points of the card
     */
    public int getPoints() {
        return points;
    }

    /**
     * Gets the rank of a card given as string.
     *
     * @param cardAsString Card as string
     * @return Rank of card
     */
    public static Rank getRankFromString(final String cardAsString) {

        Rank rank = null;

        if (cardAsString.length() == 2) {
            // parse only, iff the string is two characters long
            if (cardAsString.endsWith("A")) {

                rank = ACE;
            } else if (cardAsString.endsWith("T")) {

                rank = TEN;
            } else if (cardAsString.endsWith("K")) {

                rank = KING;
            } else if (cardAsString.endsWith("Q")) {

                rank = QUEEN;
            } else if (cardAsString.endsWith("J")) {

                rank = JACK;
            } else if (cardAsString.endsWith("9")) {

                rank = NINE;
            } else if (cardAsString.endsWith("8")) {

                rank = EIGHT;
            } else if (cardAsString.endsWith("7")) {

                rank = SEVEN;
            }
        }

        return rank;
    }

    /**
     * Builds an array of the ranks (e.g. to compute the multipliers)
     *
     * @return an array containing the ranks without the jack, starting with ace,
     *         ending with 7
     */
    public static List<Rank> getRankList() {

        return Arrays.asList(Rank.ACE, Rank.TEN, Rank.KING, Rank.QUEEN,
                Rank.NINE, Rank.EIGHT, Rank.SEVEN);
    }

    public static List<Rank> getNullRankList() {

        return Arrays.asList(Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK, Rank.TEN,
                Rank.NINE, Rank.EIGHT, Rank.SEVEN);
    }

    /**
     * converts the rank of a card to a specific int value (7=1, 8=2, 9=4, ... A=64,
     * J=128)
     *
     * @return an int representation of the card's rank
     */
    @Deprecated
    public int toBinaryFlag() {
        return (int) Math.pow(2, ordinal());
    }

    public int toNullBinaryFlag() {
        return (int) Math.pow(2, getNullOrder());
    }

    public int toSuitBinaryFlag() {
        return (int) Math.pow(2, getSuitGrandOrder());
    }

    public int toGrandBinaryFlag() {
        return toSuitBinaryFlag();
    }
}
