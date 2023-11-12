package org.jskat.data;


import org.jskat.control.event.skatgame.*;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SkatGameDataTest {

    SkatGameData gameData;

    @BeforeEach
    public void createGameData() {
        gameData = new SkatGameData();
    }

    @Test
    void hand_noAnnouncement() {

        assertThatThrownBy(() -> gameData.isHand())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void hand_Announcement() {

        gameData.setAnnouncement(new GameAnnouncement(new GameContract(GameType.GRAND, true)));

        assertTrue(gameData.isHand());
    }

    @Test
    void ouvert_noAnnouncement() {

        assertThatThrownBy(() -> gameData.isOuvert())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void ouvert_Announcement() {

        gameData.setAnnouncement(new GameAnnouncement(
                new GameContract(
                        GameType.GRAND,
                        true,
                        CardList.of(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CK, Card.CQ, Card.C9, Card.C8))));

        assertTrue(gameData.isOuvert());
    }

    @Test
    void schneiderSchwarz() {

        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(0);

        assertTrue(gameData.isSchneider());
        assertTrue(gameData.isSchwarz());

        gameData.setDeclarerScore(15);

        assertTrue(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(30);

        assertTrue(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(31);

        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(60);

        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(89);

        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(90);

        assertTrue(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());

        gameData.setDeclarerScore(120);

        assertTrue(gameData.isSchneider());
        assertTrue(gameData.isSchwarz());
    }

    @Test
    void overbid() {

        final List<SkatGameEvent> gameEvents = List.of(
                new CardDealEvent(
                        Map.of(
                                Player.FOREHAND, new CardList(Card.S9, Card.HA, Card.HK, Card.H9, Card.H8, Card.SA, Card.DT, Card.DK, Card.DQ, Card.D8),
                                Player.MIDDLEHAND, new CardList(Card.SJ, Card.DJ, Card.HT, Card.HQ, Card.H7, Card.CA, Card.CK, Card.C8, Card.SK, Card.S8),
                                Player.REARHAND, new CardList(Card.HJ, Card.CT, Card.CQ, Card.C7, Card.ST, Card.SQ, Card.S7, Card.DA, Card.D9, Card.D7)),
                        new CardList(Card.CJ, Card.C9)),
                new BidEvent(Player.MIDDLEHAND, 24),
                new HoldBidEvent(Player.FOREHAND, 24),
                new PassBidEvent(Player.FOREHAND, 24),
                new PassBidEvent(Player.REARHAND, 24),
                new PickUpSkatEvent(Player.FOREHAND),
                new DiscardSkatEvent(Player.FOREHAND, new CardList(Card.C9, Card.S9)),
                new GameAnnouncementEvent(Player.FOREHAND, new GameAnnouncement(new GameContract(GameType.HEARTS), CardList.of(Card.C9, Card.S9))),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.SA),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.S8),
                new TrickCardPlayedEvent(Player.REARHAND, Card.S7),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.D8),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.HT),
                new TrickCardPlayedEvent(Player.REARHAND, Card.D9),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.CA),
                new TrickCardPlayedEvent(Player.REARHAND, Card.C7),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.HA),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.DQ),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.HQ),
                new TrickCardPlayedEvent(Player.REARHAND, Card.D7),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.SK),
                new TrickCardPlayedEvent(Player.REARHAND, Card.SQ),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.H8),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.DK),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.DJ),
                new TrickCardPlayedEvent(Player.REARHAND, Card.DA),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.C8),
                new TrickCardPlayedEvent(Player.REARHAND, Card.CT),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.H9),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.SJ),
                new TrickCardPlayedEvent(Player.REARHAND, Card.HJ),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.CK),
                new TrickCardPlayedEvent(Player.REARHAND, Card.CQ),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.CJ),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.D8),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.H7),
                new TrickCardPlayedEvent(Player.REARHAND, Card.ST)
        );

        gameEvents.forEach(event -> event.processForward(gameData));

        gameData.calcResult();

        //assertThat(gameData.isGameFinished()).isTrue();
        assertThat(gameData.isGameLost()).isTrue();
        assertThat(gameData.getGameResult().getGameValue()).isEqualTo(-60);
    }

    @Test
    void getDeclarerCardsAfterDiscarding() {

        var factory = GameAnnouncement.getFactory();
        factory.setGameType(GameType.HEARTS);
        factory.setDiscardedCards(new CardList(Card.C9, Card.S9));

        List.of(
                new CardDealEvent(
                        Map.of(
                                Player.FOREHAND, new CardList(Card.S9, Card.HA, Card.HK, Card.H9, Card.H8, Card.SA, Card.DT, Card.DK, Card.DQ, Card.D8),
                                Player.MIDDLEHAND, new CardList(Card.SJ, Card.DJ, Card.HT, Card.HQ, Card.H7, Card.CA, Card.CK, Card.C8, Card.SK, Card.S8),
                                Player.REARHAND, new CardList(Card.HJ, Card.CT, Card.CQ, Card.C7, Card.ST, Card.SQ, Card.S7, Card.DA, Card.D9, Card.D7)),
                        new CardList(Card.CJ, Card.C9)),
                new BidEvent(Player.MIDDLEHAND, 24),
                new HoldBidEvent(Player.FOREHAND, 24),
                new PassBidEvent(Player.FOREHAND, 24),
                new PassBidEvent(Player.REARHAND, 24),
                new PickUpSkatEvent(Player.FOREHAND)
        ).forEach(event -> event.processForward(gameData));

        assertThat(gameData.getDeclarerCardsBeforeFirstTrick().isEmpty());

        var discardEvent = new DiscardSkatEvent(Player.FOREHAND, new CardList(Card.C9, Card.S9));
        discardEvent.processForward(gameData);

        List.of(
                new GameAnnouncementEvent(Player.FOREHAND, factory.getAnnouncement()),
                new TrickCardPlayedEvent(Player.FOREHAND, Card.SA),
                new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.S8),
                new TrickCardPlayedEvent(Player.REARHAND, Card.S7)
        ).forEach(event -> event.processForward(gameData));

        assertThat(gameData.getSkat()).containsExactlyInAnyOrder(Card.C9, Card.S9);
        assertThat(gameData.getDeclarerCardsBeforeFirstTrick())
                .containsExactlyInAnyOrder(Card.HA, Card.HK, Card.H9, Card.H8, Card.SA, Card.DT, Card.DK, Card.DQ, Card.D8, Card.CJ);
    }
}
