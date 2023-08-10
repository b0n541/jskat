package org.jskat.ai.sascha.solo;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Suit;
import org.jskat.util.Rank;
import org.jskat.ai.sascha.Util;

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
        int comebacks = 0;
        if (hasHighest()) {
            comebacks++;
            if (opp.size() > 2 && has(Rank.TEN)) {
                comebacks++;
            }
        }
        return comebacks;
    }

    @Override
    public int neededClears() {

        boolean hh = hasHighest();
        if (size() < 3) {
            return 0;
        }
        if (opp.size() == 0)
            return 0;

        if (opp.size() == 1) {
            if (hh)
                return 0;

            return size() > 1 ? 1 : 0;
        }

        if (size() < 5) {
            if (hh) {
                if (isHighest(1))
                    return 0;
                if (opp.size() < 3)
                    return 0;
                return 1;
            } else {
                return has2ndHighest() ? 1 : 0;
            }
        }

        return isUnbeatable() ? 0 : 1;
    }

    @Override
    public int estimateLostTricks() {
        int r = 0;
        for (int i = 0; i * 2 < opp.size() && i < own.size(); i++) {
            if (!own.get(i).beats(g, opp.get(i * 2)))
                r++;
        }
        return r;
    }

}
