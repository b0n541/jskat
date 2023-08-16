package org.jskat.ai.sascha.solo;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Suit;
import org.jskat.util.Rank;
import org.jskat.ai.sascha.Util;
import org.jskat.ai.sascha.util.CardWithInt;

public class SuitHelper extends AbstractSuitHelper {

    private int startingSize;

    public int getStartingSize() {
        return startingSize;
    }

    public SuitHelper(Suit s, CardList own) {
        this.s = s;
        this.out = new CardList();
        this.own = Util.filterSuite(own, s);

        this.own.sort(g);

        this.opp = new CardList();
        for (Rank r : Rank.values()) {
            if (r != Rank.JACK && !isOwn(r))
                opp.add(Card.getCard(s, r));
        }
        this.opp.sort(g);

        startingSize = this.own.size();

    }

    public int getThrowPriority() {
        if (isUnbeatable() || has2ndHighest())
            return 0;

        switch (size()) {
            case 1:
                return 10;
            case 2:
                if (hasHighest())
                    return 5;
                return 3;
            case 3:
                return 1;
            default:
                return 0;
        }

    }

    @Override
    public int comebacks() {
        if (hasHighest() && opp.size() > 2) {
            if (isHighest(1) && opp.size() > 3)
                return 2;
            return 1;
        }
        return 0;
    }


    // public int neededClears() {

    //     boolean hh = hasHighest();
    //     if (size() == 1) {
    //         return 0;
    //     }
    //     if (opp.size() == 0)
    //         return 0;

    //     if (opp.size() == 1) {
    //         if (hh)
    //             return 0;

    //         return size() > 1 ? 1 : 0;
    //     }

    //     if (size() < 5) {
    //         if (hh) {
    //             if (isHighest(1))
    //                 return 0;
    //             if (opp.size() < 3)
    //                 return 0;
    //             return 1;
    //         } else {
    //             return has2ndHighest() ? 1 : 0;
    //         }
    //     }

    //     return isUnbeatable() ? 0 : 1;
    // }

    @Override
    public int estimateLostTricks() {
        if (isUnbeatable())
            return 0;
        boolean hh = hasHighest();

        switch (size()) {
            case 0:
                return 0;
            case 1:
                return hh ? 0 : 1;
            case 2:
                return hh || has2ndHighest() ? 1 : 2;
            case 3:
                return hh && has2ndHighest() ? 1 : 2;
            default:
                return hh || has2ndHighest() ? 1 : 2;
        }
    }

    @Override
    public CardWithInt getDiscardPriority() {
        if (size() == 0) {
            return new CardWithInt(-1000, null);
        }

        Card c0 = own.get(0);
        boolean hasAce = (c0.getRank() == Rank.ACE);
        int discardedPoints = getDiscardedPoints();
        if (size() == 1) {
            if (hasAce) {
                return new CardWithInt(-20, c0);
            }
            return new CardWithInt(15 + c0.getPoints() - discardedPoints, c0);
        }
        Card c1 = own.get(1);
        if (size() == 2) {
            if (hasAce) {
                if (c1.getRank() == Rank.TEN)
                    return new CardWithInt(-10, c1);
                return new CardWithInt(3, c1);
            }
            if (c0.getRank() == Rank.TEN) {
                if (c1.getRank() == Rank.KING)
                    return new CardWithInt(-5, c0);
                return new CardWithInt(8, c0);
            }
            return new CardWithInt(5 + c0.getPoints() - discardedPoints, c0);
        }

        return new CardWithInt(0, own.get(own.size() - 1));

    }

}
