package org.jskat.ai.sascha.solo;

import java.util.HashMap;

import org.jskat.ai.sascha.AbstractPlayer;
import org.jskat.ai.sascha.util.CardListWithInt;
import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.Suit;

public class NullPlayer extends AbstractPlayer {
    protected HashMap<Suit, NullSuitHelper> suits = new HashMap<Suit, NullSuitHelper>();

    public NullPlayer(ImmutablePlayerKnowledge k) {
        super(k);

        for (Suit s : Suit.values()) {
            if (s != k.getTrumpSuit()) {
                suits.put(s, new NullSuitHelper(s, k.getOwnCards()));
            }
        }
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Card foreHand() {
        for (NullSuitHelper sh : this.suits.values()) {
            Card c = sh.getPullCard();
            if (c != null)
                return c;
        }
        for (NullSuitHelper sh : this.suits.values()) {
            if (sh.size() > 0)
                return sh.lowest();
        }

        return getPlayableCard();
    }

    private Card discardCard() {

        CardListWithInt w = new CardListWithInt();

        for (NullSuitHelper sh : this.suits.values()) {
            if (sh.size() > 0) {
                CardListWithInt w2 = sh.getWeakness();
                if (w2.i > w.i) {
                    w = w2;
                } else if (w2.i == w.i && w.cl != null && w2.cl.size() < w.cl.size()) {
                    w = w2;
                }
            }
        }

        if (w.cl.size() > 0) {
            return w.cl.get(w.cl.size() - 1);
        } else {
            return getPlayableCard();
        }

    }

    @Override
    protected Card midHand(Card firstCard) {
        NullSuitHelper sh = suits.get(firstCard.getSuit());
        if (sh.size() > 0) {
            return sh.getUnderCard(firstCard);
        } else {
            return discardCard();
        }
    }

    @Override
    protected Card rearHand(Card firstCard, Card secondCard) {
        NullSuitHelper sh = suits.get(firstCard.getSuit());
        if (sh.size() > 0) {
            return sh.getUnderCard(firstCard, secondCard);
        } else {
            return discardCard();
        }
    }

    @Override
    protected void beforeCard() {
    }

    @Override
    protected void afterTrick(Trick t) {
        for (NullSuitHelper sh : this.suits.values()) {
            sh.registerTrick(t);
        }
    }

}
