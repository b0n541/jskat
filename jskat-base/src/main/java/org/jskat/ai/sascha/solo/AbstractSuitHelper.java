package org.jskat.ai.sascha.solo;

import org.jskat.ai.sascha.Util;
import org.jskat.util.CardList;
import org.jskat.util.Suit;

public class AbstractSuitHelper {
    protected CardList own, out, opp;

    public AbstractSuitHelper(Suit s, CardList own) {
        this.out = new CardList();
        this.own = Util.filterSuite(own, s);

    }
}
