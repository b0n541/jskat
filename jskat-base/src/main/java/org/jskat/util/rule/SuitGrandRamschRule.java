package org.jskat.util.rule;

import org.jskat.util.*;

/**
 * Rules that are in common for suit, grand and ramsch games
 */
public abstract class SuitGrandRamschRule extends AbstractSkatRule {

    /**
     * @see SkatRule#isCardAllowed(GameType, Card, CardList, Card)
     */
    @Override
    public boolean isCardAllowed(GameType gameType, Card initialCard,
                                 CardList hand, Card card) {

        boolean result = false;

        if (initialCard == null) {
            // no initial card given --> every card is allowed
            result = true;
        } else if (initialCard.isTrump(gameType)) {

            if (card.isTrump(gameType)) {
                // card must serve trump
                result = true;
            } else if (!hand.hasTrump(gameType)) {
                // no trump on hand --> every card is allowed
                result = true;
            }
        } else if (initialCard.getSuit() == card.getSuit()
                && !card.isTrump(gameType)) {
            // card must serve suit
            result = true;
        } else if (!hand.hasSuit(gameType, initialCard.getSuit())) {
            // no suit on hand --> every card is allowed
            result = true;
        }

        return result;
    }

    /**
     * @see SkatRule#isCardBeatsCard(GameType, Card, Card)
     */
    @Override
    public boolean isCardBeatsCard(GameType gameType, Card cardToBeat, Card card) {

        boolean result = false;

        if (cardToBeat.isTrump(gameType)) {
            // card to beat is a trump card
            if (card.isTrump(gameType)) {

                if (cardToBeat.getSuitGrandOrder() < card.getSuitGrandOrder()) {
                    // card is a trump card too and has higher suit order
                    result = true;
                } else if (cardToBeat.getSuitGrandOrder() == card
                        .getSuitGrandOrder()) {
                    // cards have same suit grand order
                    // only possible if two jacks are checked
                    if (cardToBeat.getSuit().getSortOrder() < card.getSuit()
                            .getSortOrder()) {

                        result = true;
                    }
                }
            }
        } else {
            // card to beat is not a trump card
            if (card.isTrump(gameType)) {
                // card is a trump card
                result = true;
            } else if (cardToBeat.getSuit() == card.getSuit()
                    && cardToBeat.getSuitGrandOrder() < card
                    .getSuitGrandOrder()) {
                // cards have the same suit and card has higher order in
                // suit/grand games
                result = true;
            }
        }

        return result;
    }

    /**
     * @see SkatRule#hasSuit(GameType, CardList, Suit)
     */
    @Override
    public boolean hasSuit(GameType gameType, CardList hand, Suit suit) {
        for (Card card : hand) {
            if (card.getSuit() == suit && !card.isTrump(gameType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a card is a trump card
     *
     * @param gameType Game type in which the Card is checked
     * @param card     Card to be checked
     * @return TRUE if the card is a trump card
     */
    public boolean isTrump(GameType gameType, Card card) {

        boolean result = false;

        if (gameType == GameType.GRAND || gameType == GameType.RAMSCH) {

            result = card.getRank() == Rank.JACK;

        } else {

            boolean isSuitTrump = false;

            // first check for a Jack
            boolean isJack = card.getRank() == Rank.JACK;

            if (!isJack) {
                // then check the suit
                Suit suit = card.getSuit();

                if (gameType == GameType.CLUBS && suit == Suit.CLUBS
                        || gameType == GameType.SPADES && suit == Suit.SPADES
                        || gameType == GameType.HEARTS && suit == Suit.HEARTS
                        || gameType == GameType.DIAMONDS
                        && suit == Suit.DIAMONDS) {

                    isSuitTrump = true;
                }
            }

            result = isJack || isSuitTrump;
        }

        return result;
    }
}
