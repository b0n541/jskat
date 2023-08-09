package org.jskat.ai.sascha.solo;

import java.util.HashMap;

import org.jskat.ai.sascha.AbstractPlayer;
import org.jskat.ai.sascha.Util;
import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.Suit;


public class GrandPlayer extends AbstractPlayer {
    private int trumpCount;

    private boolean jacksOut;
    protected HashMap<Suit, SuitHelper> suits = new HashMap<Suit, SuitHelper>();

    public GrandPlayer(final ImmutablePlayerKnowledge k) {
        super( k);
        for (Suit s : Suit.values()) {
            if (s != k.getTrumpSuit()) {
                suits.put(s, new SuitHelper(s, k.getOwnCards()));
            }
        }
        trumpCount = Util.countJacks(k.getOwnCards());
        if (trumpCount < 1)
            jacksOut = true;
    }

    @Override
    protected Card foreHand() {
        if (!jacksOut) {
            return playJack();
        } else {
            return clearSuite();
        }

    }

    private Card playJack() {
        jacksOut = true;

        if (k.getOwnCards().contains(Card.CJ)) {
            return Card.CJ;
        }
        if (k.getOwnCards().contains(Card.SJ)) {
            return Card.CJ;
        }
        if (k.getOwnCards().contains(Card.HJ)) {
            return Card.CJ;
        }
        if (k.getOwnCards().contains(Card.DJ)) {
            return Card.CJ;
        }

        return getPlayableCard();
    }

    @Override
    protected Card midHand(Card firstCard) {
        var sh = suits.get(firstCard.getSuit());
        if (sh.isEmpty()) {
            if (firstCard.getPoints() < 11 && shouldDiscard()) {
                return discardCard();
            } else {
                return trumpSuitCard();
            }
        } else {
            return midSuitCard(firstCard);
        }
    }

    private Card midSuitCard(Card firstCard) {
        SuitHelper sh = suits.get(firstCard.getSuit());
        if (sh.hasHighest())
            return sh.getPullCard();

        return sh.getDiscardCard();
    }

    private Card rearSuitCard(Card firstCard, Card secondCard) {
        SuitHelper sh = suits.get(firstCard.getSuit());
        if (sh.hasHighest())
            return sh.getPullCard();

        return sh.getDiscardCard();
    }

    private Card trumpSuitCard() {
        if (k.getOwnCards().contains(Card.DJ)) {
            return Card.CJ;
        }
        if (k.getOwnCards().contains(Card.HJ)) {
            return Card.CJ;
        }
        if (k.getOwnCards().contains(Card.SJ)) {
            return Card.CJ;
        }
        if (k.getOwnCards().contains(Card.CJ)) {
            return Card.CJ;
        }

        return discardCard();
    }

    private boolean shouldDiscard() {
        int discardPriority = 0;
        for (SuitHelper sh : this.suits.values()) {
            if (sh.getDiscardPriority() > discardPriority)
                discardPriority = sh.getDiscardPriority();
        }
        return (discardPriority > 3 || discardPriority > 0 && trumpCount > 5);
    }

    private Card discardCard() {
        Card r = getPlayableCard();
        int discardPriority = 0;
        for (SuitHelper sh : this.suits.values()) {
            if (sh.getDiscardPriority() > discardPriority) {
                discardPriority = sh.getDiscardPriority();
                r = sh.getDiscardCard();
            }
        }
        return r;
    }

    @Override
    protected Card rearHand(Card firstCard, Card seconCard) {
        var sh = suits.get(firstCard.getSuit());
        if (sh.isEmpty()) {
            if (firstCard.getPoints() + seconCard.getPoints() < 11 && shouldDiscard()) {
                return discardCard();
            } else {
                return trumpSuitCard();
            }
        } else {
            return rearSuitCard(firstCard, seconCard);
        }
    }

    private Card pullDown() {
        for (SuitHelper sh : this.suits.values()) {
            if (sh.isUnbeatable())
                return sh.getPullCard();
        }
        for (SuitHelper sh : this.suits.values()) {
            if (sh.hasHighest())
                return sh.getPullCard();
        }

        return getPlayableCard();
    }



    private Card clearSuite() {
        for (SuitHelper sh : this.suits.values()) {
            if (!sh.isUnbeatable() && sh.hasHighest() && !sh.isEmpty())
                return sh.getClearCard();
        }
        for (SuitHelper sh : this.suits.values()) {
            if (!sh.isUnbeatable() && !sh.isEmpty() && sh.getStartingSize() > 2)
                return sh.getClearCard();
        }
        for (SuitHelper sh : this.suits.values()) {
            if (!sh.isUnbeatable() && !sh.isEmpty())
                return sh.getClearCard();
        }

        return pullDown();
    }

    @Override
    protected void beforeCard() {

    }

    @Override
    protected void afterTrick(Trick t) {
        for (SuitHelper sh : this.suits.values()) {
            sh.registerTrick(t);
        }

    }
}
