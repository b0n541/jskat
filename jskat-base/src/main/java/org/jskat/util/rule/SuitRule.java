package org.jskat.util.rule;

import org.jskat.util.*;

/**
 * Implementation of skat rules for Suit games
 */
public class SuitRule extends SuitGrandRule {

    /**
     * {@inheritDoc}
     */
    public int getBaseMultiplier(final CardList cards, final GameType gameType) {
        if (gameType == GameType.GRAND || gameType == GameType.RAMSCH || gameType == GameType.NULL) {
            throw new IllegalArgumentException("Wrong ruleset - " + gameType);
        }

        return getMatadors(cards, gameType) + 1;
    }

    public int getMatadors(final CardList cards, final GameType gameType) {
        int result = 0;

        if (cards.contains(Card.CJ)) {
            result++;
            if (cards.contains(Card.SJ)) {
                result++;
                if (cards.contains(Card.HJ)) {
                    result++;
                    if (cards.contains(Card.DJ)) {
                        result++;
                        for (final Rank r : Rank.getRankList()) {
                            if (cards.contains(Card.getCard(Suit.valueOf(gameType.toString()), r))) {
                                result++;
                            } else {
                                break;
                            }
                        }
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
                        for (final Rank r : Rank.getRankList()) {
                            if (!cards.contains(Card.getCard(Suit.valueOf(gameType.toString()), r))) {
                                result++;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}
