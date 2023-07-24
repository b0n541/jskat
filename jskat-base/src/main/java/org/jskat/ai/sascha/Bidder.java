package org.jskat.ai.sascha;

import org.jskat.util.Suit;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.CardList;
import org.jskat.util.GameType;

import java.util.HashMap;
import java.util.Map;

public class Bidder {
    private final int postion;
    private CardList c;
    private int aggroLevel;

    private int jacksMultiplier = 0;
    private int jacksCount = 0;

    private GameType gameType;
    private int gameValue;
    private Map<Suit, Integer> weaknesses;

    public Bidder(final CardList c, int postion, int aggroLevel) {
        this.aggroLevel = aggroLevel;
        this.c = c;
        this.postion = postion;

        jacksMultiplier = jacksMultiplier();
        jacksCount = Util.countJacks(c);
        weaknesses = new HashMap<>();

        for (Suit s : Suit.values()) {
            weaknesses.put(s, Util.suiteWeakness(c, s));
        }

        if (isGrand()) {
            gameType = GameType.GRAND;
            gameValue = 24 * jacksMultiplier;
            return;
        }
        if (isPlayable(Suit.CLUBS)) {
            gameType = GameType.CLUBS;
            gameValue = 12 * jacksMultiplier;
            return;
        }
        if (isPlayable(Suit.SPADES)) {
            gameType = GameType.SPADES;
            gameValue = 11 * jacksMultiplier;
            return;
        }
        if (isPlayable(Suit.HEARTS)) {
            gameType = GameType.HEARTS;
            gameValue = 10 * jacksMultiplier;
            return;
        }
        if (isPlayable(Suit.DIAMONDS)) {
            gameType = GameType.DIAMONDS;
            gameValue = 9 * jacksMultiplier;
            return;
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

    public boolean isPlayable(Suit tsuite) {
        int wSum = -aggroLevel;
        ;
        int tSum = 0;

        for (Suit s : Suit.values()) {
            if (s != tsuite)
                wSum += weaknesses.get(s);
        }

        tSum = jacksCount + c.getSuitCount(tsuite, false);

        if (tSum == 4)
            return wSum < 7;

        if (tSum == 5)
            return wSum < 10;
        if (tSum == 6)
            return wSum < 12;
        return tSum > 6;
    }

    public boolean isNull() {
        int wSum = 0;
        for (Suit s : Suit.values()) {
            wSum += Util.suiteNullWeakness(c, s);
        }

        return wSum < 4;
    }

    public boolean isGrand() {

        int wSum = -aggroLevel;

        for (Suit s : Suit.values()) {
            wSum += weaknesses.get(s);
        }
        if (jacksCount == 3) {
            return wSum < 12;
        }
        if (jacksCount == 4) {
            return wSum < 16;
        }

        if (postion == 0) {
            if (c.hasJack(Suit.CLUBS) && jacksCount > 1) {
                return (wSum < 8);
            } else {
                return (wSum < 4);
            }
        } else {
            if (c.hasJack(Suit.CLUBS) && jacksCount > 1)
                return (wSum < 5);

            return (wSum < 9);
        }
    }

}
