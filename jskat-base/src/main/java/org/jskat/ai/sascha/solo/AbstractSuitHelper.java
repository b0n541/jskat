package org.jskat.ai.sascha.solo;

import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

public abstract class AbstractSuitHelper {
    protected CardList own, out, opp;
    protected GameType g = GameType.GRAND;
    protected Suit s;

    public Suit getS() {
        return s;
    }

    abstract public int comebacks();

    abstract public int getThrowPriority();

    abstract public int neededClears();

    abstract public int estimateLostTricks();

    protected boolean has2ndHighest() {
        if (size() < 2)
            return false;
        if (opp.size() < 2)
            return false;
        return own.get(0).beats(g, opp.get(1));
    }

    protected boolean has(Rank r) {
        return own.contains(Card.getCard(s, r));
    }

    public Card getClearCard() {
        return own.get(own.size() - 1);
    }

    public Card getThrowCard() {
        return own.get(own.size() - 1);
    }

    public Card getPullCard() {
        return own.get(0);
    }

    public boolean isOwn(Rank r) {
        return own.contains(Card.getCard(s, r));
    }

    public boolean isUnbeatable() {
        for (int i = 0; i < opp.size() && i < own.size(); i++) {
            if (!isHighest(i))
                return false;
        }
        return true;
    }

    public boolean hasHighest() {
        return (own.size() > 0 && isHighest(0));
    }

    public int size() {
        return own.size();
    }

    public boolean isEmpty() {
        return (own.size() == 0);
    }

    protected boolean isHighest(int index) {
        if (opp.size() == 0)
            return true;
        return own.get(index).beats(g, opp.get(0));
    }

    public void registerTrick(Trick trick) {
        out.addAll(trick.getCardList());
        opp.removeAll(trick.getCardList());
        own.removeAll(trick.getCardList());
    };

}
