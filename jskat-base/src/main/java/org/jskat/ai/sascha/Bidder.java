package org.jskat.ai.sascha;

import org.jskat.util.Suit;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.CardList;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;

import java.util.HashMap;
import java.util.Map;

public class Bidder {
    private final Player postion;
    private CardList c;

    private int jacksMultiplier = 0;
    private int jacksCount = 0;

    private GameType gameType;
    private int gameValue;
    private Map<Suit, Integer> weaknesses;

    public Bidder(final CardList c, Player postion) {
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

        if (isNull()) {
            gameType = GameType.NULL;
            gameValue = 23;
            return;
        }

        Suit best = null;

        int bestW = 10000000;

        for (Suit s : Suit.values()) {
            int thisW = weaknessSum(s);
            if (thisW < bestW) {
                best = s;
                bestW = thisW;
            }
        }
        gameType = Util.GameTypeOfSuit(best);

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

    private int weaknessSum(Suit tsuite) {
        int wSum = 0;
        for (Suit s : Suit.values()) {
            if (s != tsuite)
                wSum += weaknesses.get(s);
        }
        return wSum;
    }

    public boolean isPlayable(Suit tsuite) {
        int wSum = weaknessSum(tsuite);
        ;
        int tSum = 0;

        tSum = jacksCount + c.getSuitCount(tsuite, false);

        if (tSum == 4)
            return wSum < 7;

        if (tSum == 5)
            return wSum < 10;
        if (tSum == 6)
            return wSum < 12;
        return tSum > 6;
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
        int p1 = -1, p2 = -1;
        int v1 = -1, v2 = -1;

        int w;

        if (gameType == GameType.NULL) {
            w = weaknessSumNull();
        } else {
            w = weaknessSum(gameType.getTrumpSuit());
        }
        for (Card card : this.c) {
            CardList eval = new CardList(c);
            eval.remove(card);
            Bidder evalBidder = new Bidder(eval, postion);
            int v;
            if (gameType == GameType.NULL) {
                v = w - evalBidder.weaknessSumNull();
            } else {
                v = w - evalBidder.weaknessSum(gameType.getTrumpSuit());
            }
            if (v < v1 || ((v == v1) && card.getPoints() > p1)) {
                c1 = card;
                v1 = v;
                p1 = card.getPoints();
                continue;
            }
            if (v < v2 || ((v == v2) && card.getPoints() > p2)) {
                c2 = card;
                v2 = v;
                p2 = card.getPoints();
            }

        }

        return new CardList(c1, c2);

    }

    public boolean isGrand() {

        int wSum = weaknessSum(null);

        for (Suit s : Suit.values()) {
            wSum += weaknesses.get(s);
        }
        if (jacksCount == 3) {
            return wSum < 12;
        }
        if (jacksCount == 4) {
            return wSum < 16;
        }

        if (postion == Player.FOREHAND) {
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
