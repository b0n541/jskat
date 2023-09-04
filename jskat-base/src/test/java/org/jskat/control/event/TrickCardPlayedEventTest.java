package org.jskat.control.event;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.TrickCardPlayedEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TrickCardPlayedEventTest extends AbstractJSkatTest {

    private SkatGameData data;
    private List<TrickCardPlayedEvent> eventList;

    @BeforeEach
    public void setUp() {
        data = new SkatGameData();
        data.setAnnouncement(GameAnnouncement.builder(GameType.CLUBS).build());
        data.addPlayerCard(Player.FOREHAND, Card.CJ);
        data.addPlayerCard(Player.FOREHAND, Card.CA);
        data.addPlayerCard(Player.FOREHAND, Card.C7);
        data.addPlayerCard(Player.MIDDLEHAND, Card.SJ);
        data.addPlayerCard(Player.MIDDLEHAND, Card.SA);
        data.addPlayerCard(Player.MIDDLEHAND, Card.C8);
        data.addPlayerCard(Player.REARHAND, Card.HJ);
        data.addPlayerCard(Player.REARHAND, Card.HA);
        data.addPlayerCard(Player.REARHAND, Card.C9);

        eventList = new ArrayList<>();
        eventList.add(new TrickCardPlayedEvent(Player.FOREHAND, Card.CJ));
        eventList.add(new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.SJ));
        eventList.add(new TrickCardPlayedEvent(Player.REARHAND, Card.HJ));
        eventList.add(new TrickCardPlayedEvent(Player.FOREHAND, Card.C7));
        eventList.add(new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.C8));
        eventList.add(new TrickCardPlayedEvent(Player.REARHAND, Card.C9));
        eventList.add(new TrickCardPlayedEvent(Player.REARHAND, Card.HA));
        eventList.add(new TrickCardPlayedEvent(Player.FOREHAND, Card.CA));
        eventList.add(new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.SA));
    }

    @Test
    public void skatGameDataAfterEvent() {

        final Iterator<TrickCardPlayedEvent> events = eventList.iterator();

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CJ, Card.CA, Card.C7);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SJ, Card.SA, Card.C8);
        assertHand(data.getPlayerCards(Player.REARHAND), Card.HJ, Card.HA, Card.C9);
        assertNull(data.getCurrentTrick());

        events.next().processForward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA, Card.C7);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SJ, Card.SA, Card.C8);
        assertHand(data.getPlayerCards(Player.REARHAND), Card.HJ, Card.HA, Card.C9);
        assertTrick(data.getCurrentTrick(), Card.CJ, null, null, null);

        events.next().processForward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA, Card.C7);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA, Card.C8);
        assertHand(data.getPlayerCards(Player.REARHAND), Card.HJ, Card.HA, Card.C9);
        assertTrick(data.getCurrentTrick(), Card.CJ, Card.SJ, null, null);

        events.next().processForward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA, Card.C7);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA, Card.C8);
        assertHand(data.getPlayerCards(Player.REARHAND), Card.HA, Card.C9);
        assertTrick(data.getLastCompletedTrick(), Card.CJ, Card.SJ, Card.HJ, Player.FOREHAND);
        assertEmptyTrick(data.getCurrentTrick());
        assertThat(data.getScore(Player.FOREHAND)).isEqualTo(6);

        events.next().processForward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA, Card.C8);
        assertHand(data.getPlayerCards(Player.REARHAND), Card.HA, Card.C9);
        assertTrick(data.getCurrentTrick(), Card.C7, null, null, null);

        events.next().processForward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
        assertHand(data.getPlayerCards(Player.REARHAND), Card.HA, Card.C9);
        assertTrick(data.getCurrentTrick(), Card.C7, Card.C8, null, null);

        events.next().processForward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
        assertHand(data.getPlayerCards(Player.REARHAND), Card.HA);
        assertTrick(data.getLastCompletedTrick(), Card.C7, Card.C8, Card.C9, Player.REARHAND);
        assertEmptyTrick(data.getCurrentTrick());
        assertThat(data.getScore(Player.REARHAND)).isEqualTo(0);

        events.next().processForward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
        assertHand(data.getPlayerCards(Player.REARHAND));
        assertTrick(data.getCurrentTrick(), Card.HA, null, null, null);

        events.next().processForward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND));
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
        assertHand(data.getPlayerCards(Player.REARHAND));
        assertTrick(data.getCurrentTrick(), Card.HA, Card.CA, null, null);

        events.next().processForward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND));
        assertHand(data.getPlayerCards(Player.MIDDLEHAND));
        assertHand(data.getPlayerCards(Player.REARHAND));
        assertTrick(data.getLastCompletedTrick(), Card.HA, Card.CA, Card.SA, Player.FOREHAND);
        assertEmptyTrick(data.getCurrentTrick());
        assertThat(data.getScore(Player.FOREHAND)).isEqualTo(39);
    }

    private static void assertEmptyTrick(final Trick trick) {
        assertNull(trick.getFirstCard());
        assertNull(trick.getSecondCard());
        assertNull(trick.getThirdCard());
        assertNull(trick.getTrickWinner());
    }

    private static void assertHand(final CardList hand, final Card... cards) {
        assertThat(hand).containsExactlyInAnyOrder(cards);
    }

    @Test
    public void skatGameDataBeforeEvent() {

        for (final TrickCardPlayedEvent event : eventList) {
            event.processForward(data);
        }

        final List<TrickCardPlayedEvent> reverseEventList = new ArrayList<>(eventList);
        Collections.reverse(reverseEventList);
        final Iterator<TrickCardPlayedEvent> events = reverseEventList.iterator();

        assertHand(data.getPlayerCards(Player.FOREHAND));
        assertHand(data.getPlayerCards(Player.MIDDLEHAND));
        assertHand(data.getPlayerCards(Player.REARHAND));
        assertEmptyTrick(data.getCurrentTrick());
        assertTrick(data.getLastCompletedTrick(), Card.HA, Card.CA, Card.SA, Player.FOREHAND);

        events.next().processBackward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND));
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
        assertHand(data.getPlayerCards(Player.REARHAND));
        assertTrick(data.getCurrentTrick(), Card.HA, Card.CA, null, null);
        assertThat(data.getScore(Player.FOREHAND)).isEqualTo(6);

        events.next().processBackward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
        assertHand(data.getPlayerCards(Player.REARHAND));
        assertTrick(data.getCurrentTrick(), Card.HA, null, null, null);

        events.next().processBackward(data);

        assertHand(data.getPlayerCards(Player.FOREHAND), Card.CA);
        assertHand(data.getPlayerCards(Player.MIDDLEHAND), Card.SA);
        assertHand(data.getPlayerCards(Player.REARHAND), Card.HA);
        assertEmptyTrick(data.getCurrentTrick());
        assertTrick(data.getLastCompletedTrick(), Card.C7, Card.C8, Card.C9, Player.REARHAND);
    }

    private static void assertTrick(final Trick trick,
                                    final Card firstCard,
                                    final Card secondCard,
                                    final Card thirdCard,
                                    final Player trickWinner) {

        assertThat(trick.getFirstCard()).isEqualTo(firstCard);
        assertThat(trick.getSecondCard()).isEqualTo(secondCard);
        assertThat(trick.getThirdCard()).isEqualTo(thirdCard);
        assertThat(trick.getTrickWinner()).isEqualTo(trickWinner);
    }
}
