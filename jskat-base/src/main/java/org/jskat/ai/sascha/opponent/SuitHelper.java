package org.jskat.ai.sascha.opponent;

import org.jskat.ai.sascha.Util;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

public class SuitHelper {
    private Suit s;
    private CardList own, out;
    private GameType g;
    private Player partner, opp;

    private boolean partnerEmpty, oppEmpty, oppTouched;

    private int startingSize;

    public int getStartingSize() {
        return startingSize;
    }

    public boolean isOppTouched() {
        return oppTouched;
    }

    public boolean isOppEmpty() {
        return oppEmpty || own.size() + out.size() == 7;
    }

    public boolean isPartnerEmpty() {
        return partnerEmpty || own.size() + out.size() == 7;
    }

    public SuitHelper(Suit s, CardList own, GameType g, Player partner, Player opp) {
        this.g = g;
        this.s = s;
        this.out = new CardList();
        this.own = Util.filterSuite(own, s);
        this.own.sort(g);

        this.partner = partner;
        this.opp = opp;

        partnerEmpty = false;
        oppEmpty = false;
        oppTouched = false;
        startingSize = this.own.size();
    }

    public int size() {
        return own.size();
    }

    public Card highest() {
        return own.get(0);
    }

    public Card lowest() {
        return own.get(own.size() - 1);
    }

    public boolean isUnbeatable(Card c) {
        Card nh = c.getNextHighest(g);
        while (nh != null) {
            if (!out.contains(nh))
                return false;
            nh = nh.getNextHighest(g);
        }
        return true;
    }

    public boolean hasHighest() {

        Card nextHighest = own.get(0).getNextHighest(g);
        while (nextHighest != null) {
            if (!out.contains(nextHighest))
                return false;
            nextHighest = nextHighest.getNextHighest(g);
        }

        return true;
    }

    public boolean isOwn(Rank r) {
        return own.contains(Card.getCard(s, r));
    }

    public Card lowestOwnBeatingCard(Card c) {
        for (int i = size() - 1; i >= 0; i--) {
            if (own.get(i).beats(g, c))
                return own.get(i);
        }
        return null;
    }

    public int higherCardsIn(Card c) {
        int r = 0;
        Card nextHighest = c.getNextHighest(g);
        while (nextHighest != null) {
            if (!out.contains(nextHighest) && !own.contains(nextHighest))
                r++;
            nextHighest = nextHighest.getNextHighest(g);
        }
        return r;
    }

    public void registerTrick(Trick t) {
        CardList tc = Util.filterSuite(t.getCardList(), s);
        own.removeAll(tc);
        out.addAll(tc);

        if (t.getFirstCard().getSuit() != s || t.getFirstCard().getRank() == Rank.JACK)
            return;

        if (t.getForeHand() == opp)
            oppTouched = true;

        if (t.getSecondCard().getSuit() != s || t.getSecondCard().getRank() == Rank.JACK) {
            if (t.getMiddleHand() == partner) {
                partnerEmpty = true;
            }
            if (t.getMiddleHand() == opp) {
                oppEmpty = true;
            }
        }

        if (t.getThirdCard().getSuit() != s || t.getThirdCard().getRank() == Rank.JACK) {
            if (t.getRearHand() == partner) {
                partnerEmpty = true;
            }
            if (t.getRearHand() == opp) {
                oppEmpty = true;
            }
        }
    }
}
