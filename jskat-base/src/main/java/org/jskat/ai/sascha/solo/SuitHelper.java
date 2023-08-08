package org.jskat.ai.sascha.solo;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Suit;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.jskat.util.Rank;
import org.jskat.ai.sascha.Util;
import org.jskat.data.Trick;

public class SuitHelper {
    private Suit s;

    private CardList own, out, opp;
    private int discardPriority, startingSize;
    public int getStartingSize() {
        return startingSize;
    }

    private SkatRule rules;

    public SuitHelper(Suit s, CardList own) {
        this.rules = SkatRuleFactory.getSkatRules(GameType.GRAND);
        this.s = s;
        this.out = new CardList();
        this.own = Util.filterSuite(own, s);

        this.own.sort(GameType.GRAND);

        this.opp = new CardList();
        for (Rank r : Rank.values()) {
            if (r != Rank.JACK && !isOwn(r))
                opp.add(Card.getCard(s, r));
        }
        this.opp.sort(GameType.GRAND);

        startingSize = this.own.size();

        calculateDiscardPrio();
    }

    private void calculateDiscardPrio() {
        if (this.own.size() == 1) {
            if (isOwn(Rank.ACE)) {
                discardPriority = 0;
            } else {
                discardPriority = 10;
            }
        } else if (this.own.size() == 2) {
            if (isOwn(Rank.TEN)) {
                discardPriority = 0;
            } else if (isOwn(Rank.ACE)) {
                discardPriority = 5;
            } else {
                discardPriority = 3;
            }
        } else {
            discardPriority = 0;
        }
    }

    public int size() {
        return own.size();
    }

    public boolean isEmpty() {
        return (own.size() == 0);
    }

    public int getDiscardPriority() {
        return discardPriority;
    }

    private boolean isHighest(int index) {
        for (Card oc : opp) {
            if (rules.isCardBeatsCard(GameType.GRAND, own.get(index), oc))
                return false;
        }
        return true;
    }

    public boolean isUnbeatable() {
        for (int i = 0; i < opp.size() && i < own.size(); i++) {
            if (!isHighest(i))
                return false;
        }
        return true;
    }

    public boolean hasHighest() {
        return isHighest(0);
    }

    public Card getDiscardCard() {
        return own.get(own.size() - 1);
    }

    public Card getClearCard() {
        return own.get(own.size() - 1);
    }

    public Card getPullCard() {
        return own.get(0);
    }

    public boolean isOwn(Rank r) {
        return own.contains(Card.getCard(s, r));
    }

    public void registerTrick(Trick trick) {
        CardList sc = Util.filterSuite(trick.getCardList(), s);
        out.addAll(sc);
        opp.removeAll(sc);
        own.removeAll(sc);

        calculateDiscardPrio();
    }

}
