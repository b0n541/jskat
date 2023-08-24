package org.jskat.ai.sascha.solo;

import java.util.HashMap;

import org.jskat.ai.sascha.AbstractPlayer;
import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

public class SuitPlayer extends AbstractPlayer {

    protected HashMap<Suit, SuitHelper> suits = new HashMap<Suit, SuitHelper>();
    private TrumpHelper th;

    public SuitPlayer(final ImmutablePlayerKnowledge k) {
        super(k);
        for (Suit s : Suit.values()) {
            if (s != k.getTrumpSuit()) {
                suits.put(s, new SuitHelper(s, k.getOwnCards()));
            }
        }
        th = new TrumpHelper(k.getTrumpSuit(), k.getOwnCards());
    }

    @Override
    protected Card foreHand() {

        if (comebacks() < 1) {
            return pullSuit();
        }
        if (th.opp.size() > 0) {
            if (th.isUnbeatable() && th.size() > 0)
                return th.getPullCard();
            if (th.hasHighest() && th.opp.size() < 3)
                return th.getPullCard();

            Card c = th.getClearCard();
            if (c != null)
                return c;
            return pullSuit();
        } else {
            return clearSuite();
        }

    }

    private Card clearSuite() {
        Card c = null;
        int neededClears = 100;
        int size = 0;

        for (SuitHelper sh : this.suits.values()) {
            int nc = sh.getNeededClears();
            int sz = sh.size();
            if (nc < 1 || sz < 1)
                continue;
            if (nc <= neededClears && sz >= size) {
                c = sh.getClearCard();
                neededClears = nc;
                size = sz;
            }
        }
        if (c != null)
            return c;

        return pullSuit();
    }

    private Card pullSuit() {
        Card c = getPlayableCard();
        int points = -1;

        for (SuitHelper sh : this.suits.values()) {
            if (sh.hasHighest() && sh.size() > 0 && sh.getPullCard().getPoints() >= points) {
                c = sh.getPullCard();
                points = c.getPoints();
            }
        }
        for (SuitHelper sh : this.suits.values()) {
            if (sh.isUnbeatable() && sh.size() > 0 && sh.getPullCard().getPoints() >= points) {
                c = sh.getPullCard();
                points = c.getPoints();
            }
        }
        return c;
    }

    private int comebacks() {
        int i = th.comebacks();
        for (SuitHelper sh : suits.values()) {
            i += sh.comebacks();
        }
        return i;
    }

    @Override
    protected Card midHand(Card firstCard) {
        if (isTrump(firstCard))
            return reactTrump();
        var sh = suits.get(firstCard.getSuit());
        if (sh.isEmpty()) {
            if (th.isEmpty())
                return throwSuit();
            if (firstCard.getPoints() > 7)
                return th.stab();
            if (shouldThrow()) {
                Card toThrow = throwSuit();
                if (toThrow.getPoints() > 4)
                    return th.stab();
                return toThrow;
            }
            return th.stab();
        } else {
            return midSuitCard(firstCard);
        }
    }

    private Card reactTrump() {
        if (k.getOwnCards().hasTrump(k.getGameType())) {
            Card c = th.getClearCard();
            if (c == null)
                c = th.getPullCard();
            return c;
        } else {
            return throwSuit();
        }
    }

    private Card midSuitCard(Card firstCard) {
        SuitHelper sh = suits.get(firstCard.getSuit());
        if (sh.hasHighest())
            return sh.getPullCard();

        return sh.getThrowCard();
    }

    private Card rearSuitCard(Card firstCard, Card secondCard) {
        SuitHelper sh = suits.get(firstCard.getSuit());
        if (sh.hasHighest())
            return sh.getPullCard();

        return sh.getThrowCard();
    }

    private boolean shouldThrow() {
        int discardPriority = 0;
        for (SuitHelper sh : this.suits.values()) {
            if (sh.getThrowPriority() > discardPriority)
                discardPriority = sh.getThrowPriority();
        }
        return (discardPriority > 3 || discardPriority > 0 && comebacks() < 2);
    }

    private Card throwSuit() {
        Card r = getPlayableCard();
        int discardPriority = 0;
        for (SuitHelper sh : this.suits.values()) {
            if (sh.getThrowPriority() > discardPriority) {
                discardPriority = sh.getThrowPriority();
                r = sh.getThrowCard();
            }
        }
        return r;
    }

    @Override
    protected Card rearHand(Card firstCard, Card secondCard) {
        if (isTrump(firstCard))
            return reactTrump();
        var sh = suits.get(firstCard.getSuit());
        if (sh.isEmpty()) {
            if (th.isEmpty())
                return throwSuit();
            if (firstCard.getPoints() + secondCard.getPoints() > 7)
                return th.stab();
            if (shouldThrow())
                return throwSuit();
            return th.stab();
        } else {
            return rearSuitCard(firstCard, secondCard);
        }
    }

    @Override
    protected void afterTrick(Trick t) {
        for (SuitHelper sh : this.suits.values()) {
            sh.registerTrick(t);
        }
        th.registerTrick(t);
    }

    @Override
    protected void beforeCard() {
    }
}
