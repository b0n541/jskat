package org.jskat.ai.sascha.solo;

import org.jskat.ai.sascha.Util;
import org.jskat.ai.sascha.util.CardListWithInt;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

public class NullSuitHelper {
    private CardList own, out, opp;
    private Suit s;
    private SkatRule rules;

    public NullSuitHelper(Suit s, CardList own) {
        this.rules = SkatRuleFactory.getSkatRules(GameType.NULL);
        this.s = s;
        this.out = new CardList();
        this.own = Util.filterSuiteNull(own, s);
        this.opp = new CardList();

        this.own.sort(GameType.NULL);
        for (Rank r : Rank.values()) {
            if (!isOwn(r))
                opp.add(Card.getCard(s, r));
        }

    }

    public void discardCard(Card c) {
        own.remove(c);
    }

    public int size() {
        return own.size();
    }

    public boolean isOwn(Rank r) {
        return own.contains(Card.getCard(s, r));
    }

    public Card get(Rank r) {
        return Card.getCard(s, r);
    }

    public void registerTrick(Trick trick) {
        CardList sc = Util.filterSuiteNull(trick.getCardList(), s);
        out.addAll(sc);
        opp.removeAll(sc);
        own.removeAll(sc);
    }

    public CardListWithInt getStartPointWeakness() {
        if (size() < 3 && isOwn(Rank.EIGHT)) {
            CardList mine = new CardList(own);
            mine.remove(Card.getCard(s, Rank.EIGHT));
            return getWeakness(mine, opp);
        }
        return getWeakness(own, opp);
    }

    public CardListWithInt getWeakness() {
        return getWeakness(own, opp);
    }

    private CardListWithInt getWeakness(CardList pmine, CardList theirs) {
        CardListWithInt r = new CardListWithInt();
        r.cl = new CardList();
        CardList mine = new CardList(pmine);
        for (Card o : theirs) {
            if (mine.size() == 0)
                break;
            Card played = null;
            for (Card m : mine) {
                if (rules.isCardBeatsCard(GameType.NULL, m, o)) {
                    played = m;
                    break;
                }
            }
            if (played == null) {
                r.i++;
                r.cl.add(mine.get(mine.size() - 1));
                mine.remove(mine.size() - 1);
            } else {
                mine.remove(played);
            }
        }
        if (r.i < 2 && theirs.size() > 3 && pmine.size() > 3) {
            r.i = 0;
            r.cl = new CardList();
        }

        return r;
    }

    private boolean isUnbeatable(CardList mine, CardList theirs) {
        return (getWeakness(mine, theirs).i == 0);
    }

    public boolean isUnbeatable() {
        return isUnbeatable(own, opp);
    }

    public Card lowest() {
        return own.get(own.size() - 1);
    }

    public Card highest() {
        return own.get(0);
    }

    public Card getUnderCard(Card o) {
        for (Card c : own) {
            if (rules.isCardBeatsCard(GameType.NULL, c, o)) {
                return c;
            }
        }
        return lowest();
    }

    public Card getUnderCard(Card o1, Card o2) {
        // if (o1.getSuit() != o2.getSuit())
        // return getUnderCard(o1);

        if (rules.isCardBeatsCard(GameType.NULL, o1, o2)) {
            return getUnderCard(o2);
        } else {
            return getUnderCard(o1);
        }
    }

    private boolean isUnbeatableAfterPulling() {

        CardList mine = new CardList(own);
        CardList theirs = new CardList(opp);

        mine.remove(lowest());
        theirs.remove(theirs.size() - 1);
        return isUnbeatable(mine, theirs);

    }

    public Card getPullCard() {
        if (own.size() == 1 || isUnbeatableAfterPulling()) {
            return lowest();
        }
        return null;
    }

}
