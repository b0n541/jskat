package org.jskat.ai.sascha.bidder;

import org.jskat.util.Suit;
import org.jskat.ai.sascha.Util;
import org.jskat.ai.sascha.solo.AbstractSuitHelper;
import org.jskat.ai.sascha.solo.SuitHelper;
import org.jskat.ai.sascha.solo.TrumpHelper;
import org.jskat.ai.sascha.util.CardWithInt;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.CardList;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bidder {
    private final Player postion;
    private CardList c;

    protected HashMap<Suit, SuitHelper> suits = new HashMap<Suit, SuitHelper>();
    private TrumpHelper trump;

    private GameType gameType;
    private Suit trumpSuit;

    public Suit getTrumpSuit() {
        return trumpSuit;
    }

    private boolean isBiddable;
    private int gameValue, longestSuitSize = 0;

    public Bidder(final CardList c, Player postion) {
        this.c = c;
        this.postion = postion;

        for (Suit s : Suit.values()) {
            var n = new SuitHelper(s, c);
            suits.put(s, n);
            longestSuitSize = Math.max(longestSuitSize, n.size());
        }

        initGameType();
        if (isBiddable) {
            if (gameType == GameType.NULL) {
                gameValue = SkatConstants.getGameBaseValue(gameType, false, false);
            } else {
                gameValue = SkatConstants.getGameBaseValue(gameType, false, false) * jacksMultiplier();
            }

        }

    }

    private void initGameType() {
        if (isGrand()) {
            gameType = GameType.GRAND;
            isBiddable = true;
            trump = new TrumpHelper(null, c);
            return;
        }
        // if (isNull()) {
        // gameType = GameType.NULL;
        // isBiddable = true;
        // return;
        // }

        trumpSuit = bestTrumpSuit();
        trump = new TrumpHelper(trumpSuit, c);
        gameType = gameTypeOfSuit(trumpSuit);

        isBiddable = checkSuit(trumpSuit);
    }

    private GameType gameTypeOfSuit(Suit suit) {
        switch (suit) {
            case CLUBS:
                return GameType.CLUBS;
            case DIAMONDS:
                return GameType.DIAMONDS;
            case HEARTS:
                return GameType.HEARTS;
            case SPADES:
                return GameType.SPADES;
            default:
                return GameType.GRAND;
        }
    }

    private int jacksMultiplier() {
        var mit = c.hasJack(Suit.CLUBS);
        if (mit) {
            if (!c.hasJack(Suit.SPADES))
                return 2;
            if (!c.hasJack(Suit.HEARTS))
                return 3;
            if (!c.hasJack(Suit.DIAMONDS))
                return 4;
            return 5;
        } else {
            if (c.hasJack(Suit.SPADES))
                return 2;
            if (c.hasJack(Suit.HEARTS))
                return 3;
            if (c.hasJack(Suit.DIAMONDS))
                return 4;
            return 5;
        }
    }

    public GameAnnouncement gameAnnouncement() {
        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
        factory.setGameType(gameType);
        return factory.getAnnouncement();
    }

    public int getGameValue() {
        return this.gameValue;
    }

    private Card discardCard() {

        AbstractSuitHelper h = trump;
        CardWithInt r = h.getDiscardPriority();

        for (SuitHelper sh : suits.values()) {
            if (sh.getS() == trumpSuit) {
                continue;
            }
            CardWithInt r2 = sh.getDiscardPriority();
            if (r2.i > r.i) {
                r = r2;
                h = sh;
            }
        }

        h.discardCard(r.c);
        return r.c;
    }

    public CardList getCardsToDiscard() {

        return new CardList(discardCard(), discardCard());

    }

    private List<Suit> longestSuits(int longestSuitSize) {
        List<Suit> r = new ArrayList<Suit>();
        for (var sh : suits.values()) {
            if (sh.size() == longestSuitSize)
                r.add(sh.getS());
        }
        return r;
    }

    public Suit bestTrumpSuit() {

        Suit r = null;
        int lostTricksBest = 10;
        for (var s : longestSuits(longestSuitSize)) {
            int comebacks = 0, lostTricks = 0, clears = 0;
            for (SuitHelper sh : suits.values()) {
                if (sh.getS() != s) {
                    comebacks += sh.comebacks();
                    lostTricks += sh.estimateLostTricks();
                    clears += sh.getNeededClears();
                }
            }
            if (lostTricks < lostTricksBest) {
                r = s;
                lostTricksBest = lostTricks;
            }
        }
        return r;
    }

    public boolean checkSuit(Suit s) {
        var th = new TrumpHelper(s, c);
        if (th.size() > 6)
            return true;

        int comebacks = 0, lostTricks = 0, clears = 0, bigOnes = 0, aces = 0;
        if (postion == Player.FOREHAND)
            comebacks++;
        for (SuitHelper sh : suits.values()) {
            if (sh.getS() != s) {
                aces += sh.getAcesCount();
                bigOnes += sh.getBigOnesCount();
                comebacks += sh.comebacks();
                lostTricks += sh.estimateLostTricks();
                clears += sh.getNeededClears();
            }
        }

        comebacks += th.comebacks();
        lostTricks += th.estimateLostTricks();
        clears += th.getNeededClears();
        bigOnes += th.getBigOnesCount();

        int jacksCount = Util.countJacks(c);

        // can't clear trump so we will loose some tricks
        if (th.oppSize() > th.size())
            clears += th.oppSize() - th.size() - 1;

        if (th.size() > 3 && bigOnes > 3 && aces > 1)
            return true;

        if (jacksCount > 1 && th.size() > 4 && bigOnes > 2 && aces > 0)
            return true;

        if (comebacks > clears && lostTricks < 6)
            return true;

        if (th.size() > 5 && th.getNeededClears() < 3)
            return true;

        return false;
    }

    public boolean isGrand() {
        return checkSuit(null);
    }

}
