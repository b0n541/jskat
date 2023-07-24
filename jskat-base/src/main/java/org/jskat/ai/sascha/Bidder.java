package org.jskat.ai.sascha;

import org.jskat.util.Player;
import org.jskat.util.Suit;
import org.jskat.util.CardList;

public class Bidder {
    private final int postion;
    private CardList c;

    private int jacksMultiplier = 0;
    private int jacksCount = 0;

    public Bidder(final CardList c, int postion) {
        this.c = c;
        this.postion = postion;

        jacksMultiplier = jacksMultiplier();
        jacksCount = Util.countJacks(c);

    }

    private int jacksMultiplier() {
        var mit = c.hasJack(Suit.CLUBS);
        if (mit) {
            if (!c.hasJack(Suit.SPADES))
                return 2;
            if (!c.hasJack(Suit.HEARTS))
                return 3;
            if (!c.hasJack(Suit.DIAMONDS))
                return 4;
            return 5;
        } else {
            if (c.hasJack(Suit.SPADES))
                return 2;
            if (c.hasJack(Suit.HEARTS))
                return 3;
            if (c.hasJack(Suit.DIAMONDS))
                return 4;
            return 5;
        }
    }

    public boolean isGrand() {

        var strength = Util.suiteStrengh(c, Suit.CLUBS);
        strength += Util.suiteStrengh(c, Suit.SPADES);
        strength += Util.suiteStrengh(c, Suit.HEARTS);
        strength += Util.suiteStrengh(c, Suit.DIAMONDS);

        if (postion == 0) {
            if (c.hasJack(Suit.CLUBS) && jacksCount > 1) {

                if (strength > 7) {
                    return true;
                }
            } else {
                return (strength > 9);
            }
        } else {
            if (c.hasJack(Suit.CLUBS) && jacksCount > 1) {

                if (strength > 8) {
                    return true;
                }
            }
            if (jacksCount > 2) {
                if (strength > 7) {
                    return true;
                }
            }
        }

        return false;

    }

}
