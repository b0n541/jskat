package org.jskat.ai.sascha.solo;

import org.jskat.ai.sascha.util.CardWithInt;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

public abstract class AbstractSuitHelper {
    protected CardList own, out, opp, discardedCards;
    protected GameType g = GameType.GRAND;
    protected Suit s;

    public AbstractSuitHelper() {
        discardedCards = new CardList();
    }

    public Suit getS() {
        return s;
    }

    abstract public int comebacks();

    abstract public int getThrowPriority();

    abstract public int estimateLostTricks();

    abstract public CardWithInt getDiscardPriority();

    public void discardCard(Card c) {
        own.remove(c);
        discardedCards.add(c);
    }

    protected int getDiscardedPoints() {
        int discardedPoints = 0;
        for (Card c : discardedCards) {
            discardedPoints += c.getPoints();
        }
        return discardedPoints;
    }

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

    protected Card getClearCard(CardList ownCards, CardList oppCards) {
        Card r = null;
        CardList opp = new CardList(oppCards);
        CardList own = new CardList(ownCards);

        for (int i = 0; i < own.size(); i++) {
            Card c = own.get(i);
            if (opp.size() == 0)
                return null;
            if (c.beats(g, opp.get(0))) {
                opp.remove(opp.size() - 1);
            } else {
                if (opp.size() == 1 || i > own.size() - 2) {
                    return own.get(own.size() - 1);
                } else {
                    r = own.get(own.size() - 1);
                    for (int j = i + 1; j < own.size(); j++) {
                        Card c2 = own.get(j);
                        if (c2.beats(g, opp.get(1)))
                            r = c2;
                    }
                    return r;
                }
            }
        }
        return r;
    }

    public int getSaveTricks() {
        return size() - getNeededClears();
    }

    public int getNeededClears() {
        int r = 0;
        int tops = 0;

        for (int i = 0; i < own.size(); i++) {
            if (isHighest(i)) {
                tops++;
            } else {
                break;
            }
        }

        CardList own = new CardList(this.own), opp = new CardList(this.opp);
        Card c = getClearCard(own, opp);
        while (c != null) {
            if (opp.size() > 2) {
                // most likely when there is more than 2 cards out, another card of this suit
                // will be played onto clear trick
                opp.remove(opp.size() - 1);
            }
            r++;
            own.remove(c);
            opp.remove(0);
            c = getClearCard(own, opp);
        }
        if (own.size() <= tops)
            return 0;
        return r;

    }

    public Card getClearCard() {
        return getClearCard(this.own, this.opp);
        // recommended Card for clearing (null if not recommended at all)

    }

    public int getAcesCount() {
        if (size() == 2 && has(Rank.ACE) && has(Rank.TEN))
            return 2;
        return has(Rank.ACE) ? 1 : 0;
    }

    public int getBigOnesCount() {
        int r = 0;
        for (Card c : own) {
            if (c.getRank() == Rank.ACE || c.getRank() == Rank.TEN) {
                r++;
            }
        }
        return r;
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

    public boolean couldBeUnbeatable(){
        // todo implement
        throw new UnsupportedOperationException("not implemented yet");
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

    public int oppSize() {
        return opp.size();
    }

    public boolean isEmpty() {
        return (own.size() == 0);
    }

    protected boolean isHighest(int index) {
        if (size() < index + 1)
            return false;
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
