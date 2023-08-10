package org.jskat.ai.sascha.solo;

import java.util.Arrays;

import org.jskat.ai.sascha.Util;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

public class TrumpHelper extends AbstractSuitHelper {

    public TrumpHelper(Suit s, CardList cards) {
        this.s = s;
        this.out = new CardList();
        this.own = Util.filterSuite(cards, s);
        this.own.addAll(Util.getJacks(cards));

        this.own.sort(g);

        this.opp = new CardList();
        if (s != null) {
            for (Rank r : Rank.values()) {
                if (r != Rank.JACK && !isOwn(r))
                    opp.add(Card.getCard(s, r));
            }
        }

        for (Card jack : new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ))) {
            if (!own.contains(jack))
                opp.add(jack);
        }

        this.opp.sort(g);
    }

    @Override
    public int comebacks() {
        double x = (double) own.size();
        double y = (double) opp.size();
        return (int) Math.floor(x - y / 2.0);
    }

    @Override
    public int getThrowPriority() {
        return 0;
    }

    @Override
    public int neededClears() {
        int clears = (int) Math.ceil((double) opp.size() / 2.0);

        for (int i = 0; i < size() && clears > 0; i++) {
            if (isHighest(i))
                clears--;
        }

        return clears;
    }

    @Override
    public int estimateLostTricks() {
        return neededClears();
    }

}
