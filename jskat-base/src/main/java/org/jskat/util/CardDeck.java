package org.jskat.util;

import java.util.*;

/**
 * Represents a complete card deck for Skat
 */
public class CardDeck extends CardList {

    private final int MAX_CARDS = 32;

    public final static Map<Suit, List<Card>> SUIT_CARDS;
    public final static Map<Rank, List<Card>> RANK_CARDS;

    static {
        SUIT_CARDS = Map.of(
                Suit.CLUBS, List.of(Card.CJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8, Card.C7),
                Suit.SPADES, List.of(Card.SJ, Card.SA, Card.ST, Card.SK, Card.SQ, Card.S9, Card.S8, Card.S7),
                Suit.HEARTS, List.of(Card.HJ, Card.HA, Card.HT, Card.HK, Card.HQ, Card.H9, Card.H8, Card.H7),
                Suit.DIAMONDS, List.of(Card.DJ, Card.DA, Card.DT, Card.DK, Card.DQ, Card.D9, Card.D8, Card.D7));

        RANK_CARDS = Map.of(
                Rank.JACK, List.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ),
                Rank.ACE, List.of(Card.CA, Card.SA, Card.HA, Card.DA),
                Rank.TEN, List.of(Card.CT, Card.ST, Card.HT, Card.DT),
                Rank.KING, List.of(Card.CK, Card.SK, Card.HK, Card.DK),
                Rank.QUEEN, List.of(Card.CQ, Card.SQ, Card.HQ, Card.DQ),
                Rank.NINE, List.of(Card.C9, Card.S9, Card.H9, Card.D9),
                Rank.EIGHT, List.of(Card.C8, Card.S8, Card.H8, Card.D8),
                Rank.SEVEN, List.of(Card.C7, Card.S7, Card.H7, Card.D7));
    }

    /**
     * Creates a new instance of CardDeck
     */
    public CardDeck() {

        super();

        // Adds a card for every suit and value
        for (final Card card : Card.values()) {
            add(card);
        }
    }

    /**
     * Constructor
     *
     * @param foreHandCards   Cards of fore hand
     * @param middleHandCards Cards of middle hand
     * @param rearHandCards   Cards of rear hand
     * @param skatCards       Cards of skat
     */
    public CardDeck(final List<Card> foreHandCards,
                    final List<Card> middleHandCards,
                    final List<Card> rearHandCards,
                    final List<Card> skatCards) {
        addAll(foreHandCards.subList(0, 3));
        addAll(middleHandCards.subList(0, 3));
        addAll(rearHandCards.subList(0, 3));
        addAll(skatCards);
        addAll(foreHandCards.subList(3, 7));
        addAll(middleHandCards.subList(3, 7));
        addAll(rearHandCards.subList(3, 7));
        addAll(foreHandCards.subList(7, 10));
        addAll(middleHandCards.subList(7, 10));
        addAll(rearHandCards.subList(7, 10));
    }

    /**
     * Constructor
     *
     * @param foreHandCards   Cards of fore hand
     * @param middleHandCards Cards of middle hand
     * @param rearHandCards   Cards of rear hand
     * @param skatCards       Cards of skat
     */
    public CardDeck(final String foreHandCards,
                    final String middleHandCards,
                    final String rearHandCards,
                    final String skatCards) {
        this(getCardsFromString(foreHandCards),
                getCardsFromString(middleHandCards),
                getCardsFromString(rearHandCards),
                getCardsFromString(skatCards));
    }

    /**
     * Constructor
     *
     * @param cards Card distribution
     */
    public CardDeck(final String cards) {

        addAll(getCardsFromString(cards));
    }

    private static List<Card> getCardsFromString(final String cards) {

        final List<Card> result = new ArrayList<>();
        final StringTokenizer token = new StringTokenizer(cards);

        while (token.hasMoreTokens()) {
            result.add(Card.getCardFromString(token.nextToken()));
        }

        return result;
    }

    /**
     * Constructor
     *
     * @param cards Card distribution
     */
    public CardDeck(final CardList cards) {

        addAll(cards);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(final Card card) {

        if (size() == MAX_CARDS) {
            throw new IllegalStateException("Card deck is already filled with " + MAX_CARDS + " cards.");
        }
        if (contains(card)) {
            throw new IllegalArgumentException("Card " + card + " is already contained in card deck.");
        }

        return super.add(card);
    }

    /**
     * Gets a complete card deck
     *
     * @return A complete card deck
     */
    public static EnumSet<Card> getAllCards() {

        final EnumSet<Card> allCards = EnumSet.allOf(Card.class);

        return allCards;
    }

    /**
     * Get the perfect card distribution where forehand wins all types of games.
     *
     * @return Perfect card distribution
     */
    public static CardDeck getPerfectDistribution() {
        return new CardDeck(
                "CJ SJ HJ CK CQ SK C7 C8 S7 H7 D7 DJ CA CT C9 SQ HA HK HQ S8 H8 H9 HT SA ST S9 D8 D9 DT DA DK DQ");
    }

    /**
     * Shuffles the CardDeck
     */
    public void shuffle() {
        // Simple random shuffling
        Collections.shuffle(cards);
    }
}
