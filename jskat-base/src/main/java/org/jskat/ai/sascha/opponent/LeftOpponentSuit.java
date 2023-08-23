package org.jskat.ai.sascha.opponent;

import java.util.ArrayList;
import java.util.HashMap;

import org.jskat.ai.sascha.AbstractPlayer;
import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

public class LeftOpponentSuit extends AbstractPlayer {

    private HashMap<Suit, SuitHelper> suits;

    private SuitHelper prefPlaySuit;

    private TrumpHelper th;

    private Player partner, opp;

    private ArrayList<SuitHelper> shortestSuits;

    public LeftOpponentSuit(ImmutablePlayerKnowledge k) {
        super(k);
        suits = new HashMap<Suit, SuitHelper>();
        shortestSuits = new ArrayList<SuitHelper>(4);
        opp = k.getDeclarer();
        partner = opp.getRightNeighbor();
        int shortestLength = 8;
        for (Suit s : Suit.values()) {
            if (s != k.getTrumpSuit()) {
                var sh = new SuitHelper(s, k.getOwnCards(), k.getGameType(), partner, opp);
                if (sh.size() > 0 && sh.size() < shortestLength)
                    shortestLength = sh.size();
                suits.put(s, sh);
            }
        }
        for (SuitHelper sh : suits.values()) {
            if (sh.size() > 0 && sh.size() == shortestLength)
                shortestSuits.add(sh);
        }

        th = new TrumpHelper(k.getOwnCards(), k.getGameType(), partner, opp);
        prefPlaySuit = null;
    }

    private ArrayList<SuitHelper> nextShortestSuit() {
        int shortestLength = 8;
        for (SuitHelper sh : suits.values()) {
            if (sh.size() > 0 && sh.getStartingSize() < shortestLength)
                shortestLength = sh.getStartingSize();
        }
        ArrayList<SuitHelper> r = new ArrayList<SuitHelper>(4);
        for (SuitHelper sh : suits.values()) {
            if (sh.size() > 0 && sh.getStartingSize() == shortestLength)
                r.add(sh);
        }
        return r;

    }

    private SuitHelper bestPlaySuit() {
        if (shortestSuits.size() == 0) {
            shortestSuits = nextShortestSuit();
        }

        for (var sh : shortestSuits) {
            if (sh.hasHighest())
                return sh;
        }
        for (var sh : shortestSuits) {
            if (!sh.isOwn(Rank.TEN))
                return sh;
        }

        for (var sh : shortestSuits) {
            return sh;
        }

        return null;

    }

    @Override
    protected Card foreHand() {
        if (prefPlaySuit == null) {
            prefPlaySuit = bestPlaySuit();
            if (prefPlaySuit == null) // todo find something better
                return getPlayableCard();
        }
        return prefPlaySuit.highest();

    }

    private Card schmotzCard() {
        // blanke zehner
        for (var sh : suits.values()) {
            if (!sh.hasHighest() && sh.size() == 1 && sh.isOwn(Rank.TEN))
                return sh.highest();
        }
        // dicke, die bei gegner blank sitzen
        for (var sh : suits.values()) {
            if (sh.isOppEmpty() && (sh.isOwn(Rank.ACE) || sh.isOwn(Rank.TEN)))
                return sh.highest();
        }
        // ass und zehn
        for (var sh : suits.values()) {
            if (sh.isOwn(Rank.ACE) && sh.isOwn(Rank.TEN))
                return sh.highest();
        }
        // trumpf, falls schwach
        if (th.size() > 0 && th.getStartingSize() < 3 && th.highestPointCard().getPoints() > 4)
            return th.highestPointCard();
        // schlecht gedeckte zehner
        for (var sh : suits.values()) {
            if (!sh.hasHighest() && sh.isOwn(Rank.TEN) && !sh.isOwn(Rank.KING))
                return sh.highest();
        }
        // lange farbe
        for (var sh : suits.values()) {
            if (sh.getStartingSize() > 3 && (sh.isOwn(Rank.ACE) || sh.isOwn(Rank.TEN)))
                return sh.highest();
        }
        // blanke könige
        for (var sh : suits.values()) {
            if (!sh.hasHighest() && sh.size() == 1 && sh.isOwn(Rank.KING))
                return sh.highest();
        }
        // gut gedeckte zehner
        for (var sh : suits.values()) {
            if (!sh.hasHighest() && sh.isOwn(Rank.TEN))
                return sh.highest();
        }
        // nichts zum buttern => abwerfen
        return throwCard();
    }

    private Card throwCard() {
        // gespielte farben blank werfen
        if (prefPlaySuit.size() > 0)
            return prefPlaySuit.lowest();

        if (shortestSuits.size() == 0) {
            shortestSuits = nextShortestSuit();
        }
        if (shortestSuits.size() == 0) // keine farbe mehr da
            return th.LowestPointCard();

        // nur ein schwacher trumpf und keine kurze farbe
        if (th.getStartingSize() < 3 && th.size() == 1 && shortestSuits.get(0).size() > 1)
            return th.LowestPointCard();

        // von anfang an blanke miese
        for (var sh : suits.values()) {
            if (sh.getStartingSize() == 1 && sh.highest().getPoints() == 0)
                return sh.highest();
        }
        // von anfang an blankes bild
        for (var sh : suits.values()) {
            if (sh.getStartingSize() == 1 && sh.highest().getPoints() < 10)
                return sh.highest();
        }
        // kurze miese
        for (var sh : suits.values()) {
            if (sh.lowest().getPoints() == 0)
                return sh.lowest();
        }
        // kurzes bild
        for (var sh : suits.values()) {
            if (sh.lowest().getPoints() < 10)
                return sh.lowest();
        }

        return getPlayableCard();
    }

    @Override
    protected Card midHand(Card firstCard) {

        if (firstCard.isTrump(k.getGameType())) {
            return reactTrump(firstCard);
        }

        SuitHelper sh = suits.get(firstCard.getSuit());
        if (sh.size() == 0) {
            if (th.size() > 0) {
                if (th.getStartingSize() < 3)
                    return th.highestPointCard();
                if (firstCard.getPoints() > 4)
                    return th.highestPointCard();
            }
            if (sh.higherCardsIn(firstCard) > 0 && firstCard.getPoints() < 4)
                return schmotzCard();
            if (sh.isPartnerEmpty() && !th.isPartnerEmpty() && firstCard.getPoints() > 2)
                return schmotzCard();
            return throwCard();
        }

        return getPlayableCard();
    }

    private Card reactTrump(Card firstCard) {
        int chances = th.higherCardsIn(firstCard);
        if (th.size() == 0) {
            if (th.isPartnerEmpty())
                return throwCard();
            if (chances < 1)
                return throwCard();
            if (th.getStartingSize() > 2)
                return throwCard();
            return schmotzCard();
        }
        var bc = th.beatingCards(firstCard);
        if (chances < 2)
            return bc.size() > 0 ? bc.get(bc.size() - 1) : th.LowestPointCard();
        return th.highestPointCard();
    }

    @Override
    protected Card rearHand(Card firstCard, Card secondCard) {
        boolean oppTrick = !(firstCard.beats(k.getGameType(), secondCard));

        if (firstCard.isTrump(k.getGameType())) {
            if (oppTrick) {
                if (th.size() == 0)
                    return throwCard();
                var bc = th.beatingCards(firstCard);
                if (bc.size() > 0)
                    return bc.get(bc.size() - 1);
                return th.LowestPointCard();
            }
            if (th.size() == 0) {
                return schmotzCard();
            }
            return th.highestPointCard();
        }
        var sh = suits.get(firstCard.getSuit());
        if (oppTrick) {
            if (sh.size() == 0) {
                var bc = th.beatingCards(firstCard);
                if (bc.size() > 0)
                    return bc.get(bc.size() - 1);
                return throwCard();
            }
            return sh.lowest();
        }

        if (sh.size() == 0) {
            return schmotzCard();
        }
        return sh.highest();
    }

    @Override
    protected void beforeCard() {

    }

    @Override
    protected void afterTrick(Trick t) {
        if (prefPlaySuit == null) {
            if (t.getForeHand() == partner && !t.getFirstCard().isTrump(k.getGameType())) {
                prefPlaySuit = suits.get(t.getFirstCard().getSuit());
            }
        }

        for (SuitHelper sh : suits.values()) {
            sh.registerTrick(t);
            if (sh.size() == 0)
                shortestSuits.remove(sh);
        }

        th.registerTrick(t);

        if (prefPlaySuit != null && prefPlaySuit.size() == 0) {
            prefPlaySuit = null;
        }

    }

}
