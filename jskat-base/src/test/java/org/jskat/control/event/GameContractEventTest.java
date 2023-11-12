package org.jskat.control.event;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.GameAnnouncementEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameContract;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameContractEventTest extends AbstractJSkatTest {

    private SkatGameData data;

    private GameAnnouncementEvent event;

    @BeforeEach
    public void setUp() {

        data = new SkatGameData();
        event = new GameAnnouncementEvent(Player.FOREHAND, new GameAnnouncement(new GameContract(GameType.GRAND), new CardList(Card.CJ, Card.SJ)));
    }

    @Test
    public void skatGameDataAfterEvent() {
        assertThat(data.getDeclarer()).isNull();
        assertThat(data.getAnnouncement()).isNull();

        event.processForward(data);

        assertThat(data.getDeclarer()).isEqualTo(Player.FOREHAND);
        assertThat(data.getAnnouncement().contract().gameType()).isEqualTo(GameType.GRAND);
        assertThat(data.getAnnouncement().discardedCards()).containsExactlyInAnyOrder(Card.CJ, Card.SJ);
        assertThat(data.getAnnouncement().contract().hand()).isFalse();
        assertThat(data.getAnnouncement().contract().ouvert()).isFalse();
        assertThat(data.getAnnouncement().contract().ouvertCards()).isEmpty();
        assertThat(data.getAnnouncement().contract().schneider()).isFalse();
        assertThat(data.getAnnouncement().contract().schwarz()).isFalse();
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        assertThat(data.getDeclarer()).isNull();
        assertThat(data.getAnnouncement()).isNull();
    }
}
