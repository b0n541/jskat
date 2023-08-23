package org.jskat.ai.sascha.opponent;

import org.jskat.ai.sascha.Util;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;

import org.jskat.util.Suit;

public class TrumpHelper {
    private Suit s;
    private CardList own, out;
    private GameType g;
    private Player partner, opp;

    private boolean partnerEmpty, oppEmpty;

    private int startingSize, timesPlayed;

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public TrumpHelper(CardList own, GameType g, Player partner, Player opp) {
        this.g = g;
        this.s = g.getTrumpSuit();
        this.out = new CardList();
        this.own = Util.filterSuite(own, s);
        this.own.addAll(Util.getJacks(own));
        this.own.sort(g);

        this.partner = partner;
        this.opp = opp;

        partnerEmpty = false;
        oppEmpty = false;
        startingSize = this.own.size();
    }

    public int getStartingSize() {
        return startingSize;
    }

    public boolean isOppEmpty() {
        return oppEmpty || own.size() + out.size() == 11;
    }

    public boolean isPartnerEmpty() {
        return partnerEmpty || own.size() + out.size() == 11;
    }

    public int size() {
        return own.size();
    }

    public Card LowestPointCard() {
        Card r = null;
        for (Card c : own) {
            if (r == null)
                r = c;
            if (c.getPoints() > r.getPoints()) {
                r = c;
            }
        }
        return r;
    }

    public Card highestPointCard() {
        Card r = null;
        for (Card c : own) {
            if (r == null)
                r = c;
            if (c.getPoints() > r.getPoints()) {
                r = c;
            }
        }
        return r;
    }

    public Card highest() {
        return own.get(0);
    }

    // public Card lowestLowPoints() {
    // Card r = own.get(size() - 1);
    // Card nh = r.getNextHighest(g);
    // while (nh != null) {
    // if (!own.contains(nh) && !out.contains(nh))
    // return r;
    // if (nh.getPoints() < r.getPoints())
    // r = nh;
    // nh = nh.getNextHighest(g);
    // }
    // return r;
    // }

    // public Card bestBeatingCard(Card c) {

    // for (int i = size() - 1; i >= 0; i--) {

    // }
    // Card r = highest();
    // if (c.beats(g, r))
    // return lowestLowPoints();
    // while (r.beats(g, c)) {

    // }
    // }

    public Card lowestHighPointBeatingCard(Card c) {
        var bc = beatingCards(c);
        if (bc.size() == 0)
            return null;
        Card b = bc.get(bc.size() - 1);
        Card bn = b.getNextHighest(g);
        while (bn != null) {
            if (out.contains(bn)) {
                bn = bn.getNextHighest(g);
                continue;
            }
            if (own.contains(bn)) {
                if (bn.getPoints() > b.getPoints())
                    b = bn;
                bn = bn.getNextHighest(g);
                continue;
            }
            return b;
        }
        return b;
    }

    public CardList beatingCards(Card c) {
        CardList r = new CardList();
        for (Card oc : own) {
            if (oc.beats(g, c))
                r.add(oc);
        }
        r.sort(g);
        return r;
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

    public boolean hasCard(Card c) {
        return own.contains(c);
    }

    public void registerTrick(Trick t) {
        CardList tc = Util.filterSuite(t.getCardList(), s);
        own.removeAll(tc);
        out.addAll(tc);

        if (!t.getFirstCard().isTrump(g))
            return;

        timesPlayed++;

        if (!t.getSecondCard().isTrump(g)) {
            if (t.getMiddleHand() == partner) {
                partnerEmpty = true;
            }
            if (t.getMiddleHand() == opp) {
                oppEmpty = true;
            }
        }

        if (!t.getThirdCard().isTrump(g)) {
            if (t.getRearHand() == partner) {
                partnerEmpty = true;
            }
            if (t.getRearHand() == opp) {
                oppEmpty = true;
            }
        }
    }

}
