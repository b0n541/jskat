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

public class RightOpponentSuit extends AbstractPlayer {

    private HashMap<Suit, SuitHelper> suits;

    private SuitHelper prefPlaySuit;

    private TrumpHelper th;

    private Player partner, opp;

    private ArrayList<SuitHelper> longestSuits;

    public RightOpponentSuit(ImmutablePlayerKnowledge k) {
        super(k);
        suits = new HashMap<Suit, SuitHelper>();
        longestSuits = new ArrayList<SuitHelper>(4);
        opp = k.getDeclarer();
        partner = opp.getLeftNeighbor();
        int longestLength = 0;
        for (Suit s : Suit.values()) {
            if (s != k.getTrumpSuit()) {
                var sh = new SuitHelper(s, k.getOwnCards(), k.getGameType(), partner, opp);
                if (sh.size() > longestLength)
                    longestLength = sh.size();
                suits.put(s, sh);
            }
        }
        for (SuitHelper sh : suits.values()) {
            if (sh.size() == longestLength)
                longestSuits.add(sh);
        }

        th = new TrumpHelper(k.getOwnCards(), k.getGameType(), partner, opp);

        prefPlaySuit = null;
    }

    private SuitHelper bestPlaySuit() {
        if (longestSuits.size() == 0) {
            for (SuitHelper sh : suits.values()) {
                if (sh.size() > 0) {
                    longestSuits.add(sh);
                    break;
                }
            }
        }
        for (var sh : longestSuits) {
            if (sh.isPartnerEmpty())
                return sh;
        }
        for (var sh : longestSuits) {
            if (sh.isOppEmpty())
                return sh;
        }
        for (var sh : longestSuits) {
            if (!sh.hasHighest())
                return sh;
        }
        for (var sh : longestSuits) {
            return sh;
        }

        return null;

    }

    @Override
    protected Card foreHand() {
        if (prefPlaySuit == null) {
            prefPlaySuit = bestPlaySuit();
            if (prefPlaySuit == null)
                return th.LowestPointCard();
        }

        if (prefPlaySuit.isPartnerEmpty())
            return prefPlaySuit.highest();
        if (prefPlaySuit.isOppEmpty())
            return prefPlaySuit.lowest();
        if (prefPlaySuit.hasHighest())
            return prefPlaySuit.highest();

        return prefPlaySuit.lowest();
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

    private Card throwCard() {
        // gespielte farben blank werfen
        if (prefPlaySuit.size() > 0)
            return prefPlaySuit.lowest();

        var shortestSuits = nextShortestSuit();

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
            int chances = th.higherCardsIn(firstCard);
            if (th.size() == 0) {
                // schmotzen nur auf den höchsten trumpf im spiel
                if (chances < 1)
                    return schmotzCard();
                return throwCard();
            }
            if (chances < 1)
                return th.highestPointCard();
            return th.LowestPointCard();
        }

        SuitHelper sh = suits.get(firstCard.getSuit());
        if (sh.size() == 0) {
            if (th.getStartingSize() < 3 && th.size() > 0)
                return th.LowestPointCard();
            if (sh.isOppEmpty())
                return throwCard();
            if (sh.isUnbeatable(firstCard))
                return schmotzCard();
            return throwCard();
        }
        // übernehmen, wenn möglich
        if (sh.hasHighest())
            return sh.highest();

        Card c = sh.lowestOwnBeatingCard(firstCard);
        if (c != null)
            return c;
        // letze wahl, wegbleiben
        return sh.lowest();
    }

    @Override
    protected Card rearHand(Card firstCard, Card secondCard) {
        if (firstCard.isTrump(k.getGameType())) {
            if (th.size() > 0) {
                // partner higher
                if (secondCard.beats(k.getGameType(), firstCard)) {
                    return th.highestPointCard();
                }
                var bc = th.lowestHighPointBeatingCard(firstCard);
                if (bc != null)
                    return bc;
                return th.LowestPointCard();
            }
            // partner higher
            if (secondCard.beats(k.getGameType(), firstCard)) {
                return schmotzCard();
            }
            return throwCard();
        }

        var sh = suits.get(firstCard.getSuit());
        if (sh.size() < 0) {
            // partner higher
            if (secondCard.beats(k.getGameType(), firstCard)) {
                return schmotzCard();
            }
            return throwCard();
        }
        if (secondCard.beats(k.getGameType(), firstCard))
            return sh.highest();

        if (sh.highest().beats(k.getGameType(), firstCard))
            return sh.highest();
        return sh.lowest();
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
            if (sh.size() == 0) {
                longestSuits.remove(sh);
            }
        }

        th.registerTrick(t);


        if (prefPlaySuit != null && prefPlaySuit.size() == 0) {
            prefPlaySuit = null;
        }

    }

}
