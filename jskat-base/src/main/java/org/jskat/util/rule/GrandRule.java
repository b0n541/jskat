package org.jskat.util.rule;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;

/**
 * Implementation of skat rules for Grand games
 */
public class GrandRule extends SuitGrandRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMultiplier(CardList cards, GameType gameType) {
        if (gameType != GameType.GRAND) {
            throw new IllegalArgumentException("Wrong ruleset - " + gameType);
        }
        int result = 1;
        if (cards.contains(Card.CJ)) {
            result++;
            if (cards.contains(Card.SJ)) {
                result++;
                if (cards.contains(Card.HJ)) {
                    result++;
                    if (cards.contains(Card.DJ)) {
                        result++;
                    }
                }
            }
        } else {
            result++;
            if (!cards.contains(Card.SJ)) {
                result++;
                if (!cards.contains(Card.HJ)) {
                    result++;
                    if (!cards.contains(Card.DJ)) {
                        result++;
                    }
                }
            }
        }
        return result;
    }
}
