package org.jskat.ai.sascha;

import org.jskat.util.Suit;
import org.jskat.ai.sascha.solo.SuitHelper;
import org.jskat.ai.sascha.solo.TrumpHelper;
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
    protected List<TrumpHelper> trumps = new ArrayList<TrumpHelper>();

    private GameType gameType;
    private boolean isBiddable;
    private int gameValue;
    private Map<Suit, Integer> weaknesses;

    public Bidder(final CardList c, Player postion) {
        this.c = c;
        this.postion = postion;

        var longestSuitSize = 0;

        for (Suit s : Suit.values()) {
            var n = new SuitHelper(s, c);
            suits.put(s, n);
            longestSuitSize = Math.max(longestSuitSize, n.size());
        }

        initGameType(longestSuitSize);
        if (isBiddable) {
            gameValue = SkatConstants.getGameBaseValue(gameType, false, false) * jacksMultiplier();
        }

    }

    private void initGameType(int longestSuitSize) {
        if (isGrand()) {
            gameType = GameType.GRAND;
            isBiddable = true;
            return;
        }
        if (isNull()) {
            gameType = GameType.GRAND;
            isBiddable = true;
            return;
        }

        var suit = bestTrumpSuit(longestSuitSize);
        switch (suit) {
            case CLUBS:
                gameType = GameType.CLUBS;
            case DIAMONDS:
                gameType = GameType.DIAMONDS;
            case HEARTS:
                gameType = GameType.HEARTS;
            case SPADES:
                gameType = GameType.SPADES;
        }

        isBiddable = checkSuit(suit);
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

    private int weaknessSumNull() {
        int wSum = 0;
        for (Suit s : Suit.values()) {
            wSum += Util.suiteNullWeakness(c, s);
        }

        return wSum;
    }

    public boolean isNull() {
        return weaknessSumNull() < 4;
    }

    public CardList getCardsToDiscard() {
        Card c1 = c.get(0), c2 = c.get(1);

        return new CardList(c1, c2);

    }

    private List<Suit> longestSuits(int longestSuitSize) {
        List<Suit> r = new ArrayList<Suit>();
        for (var sh : suits.values()) {
            if (sh.size() == longestSuitSize)
                r.add(sh.getS());
        }
        return r;
    }

    private Suit bestTrumpSuit(int longestSuitSize) {

        Suit r = null;
        int lostTricksBest = 10;
        for (var s : longestSuits(longestSuitSize)) {
            int comebacks = 0, lostTricks = 0, clears = 0;
            for (SuitHelper sh : suits.values()) {
                if (sh.getS() != s) {
                    comebacks += sh.comebacks();
                    lostTricks += sh.estimateLostTricks();
                    clears += sh.neededClears();
                }
            }
            if (lostTricks < lostTricksBest) {
                r = s;
                lostTricksBest = lostTricks;
            }
        }
        return r;
    }

    private boolean checkSuit(Suit s) {
        int comebacks = 0, lostTricks = 0, clears = 0;
        if (postion == Player.FOREHAND)
            comebacks++;
        for (SuitHelper sh : suits.values()) {
            if (sh.getS() != s) {
                comebacks += sh.comebacks();
                lostTricks += sh.estimateLostTricks();
                clears += sh.neededClears();
            }
        }
        var th = new TrumpHelper(s, c);
        comebacks += th.comebacks();
        lostTricks += th.estimateLostTricks();
        clears += th.neededClears();

        return (comebacks > clears && lostTricks < 5);
    }

    public boolean isGrand() {
        return checkSuit(null);
    }

}
