package org.jskat.control.event;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.GameAnnouncementEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameAnnouncementEventTest extends AbstractJSkatTest {

    private SkatGameData data;

    private GameAnnouncementEvent event;

    @BeforeEach
    public void setUp() {

        data = new SkatGameData();

        final var builder = GameAnnouncement.builder(GameType.GRAND);
        builder.discardedCards(new CardList(Card.CJ, Card.SJ));

        event = new GameAnnouncementEvent(Player.FOREHAND, builder.build());
    }

    @Test
    public void skatGameDataAfterEvent() {
        assertThat(data.getDeclarer()).isNull();
        assertThat(data.getAnnouncement().gameType()).isNull();
        assertThat(data.getAnnouncement().discardedCards()).isEmpty();
        assertThat(data.getAnnouncement().hand()).isTrue();
        assertThat(data.getAnnouncement().ouvert()).isFalse();
        assertThat(data.getAnnouncement().ouvertCards()).isEmpty();
        assertThat(data.getAnnouncement().schneider()).isFalse();
        assertThat(data.getAnnouncement().schwarz()).isFalse();

        event.processForward(data);

        assertThat(data.getDeclarer()).isEqualTo(Player.FOREHAND);
        assertThat(data.getAnnouncement().gameType()).isEqualTo(GameType.GRAND);
        assertThat(data.getAnnouncement().discardedCards()).containsExactlyInAnyOrder(Card.CJ, Card.SJ);
        assertThat(data.getAnnouncement().hand()).isFalse();
        assertThat(data.getAnnouncement().ouvert()).isFalse();
        assertThat(data.getAnnouncement().ouvertCards()).isEmpty();
        assertThat(data.getAnnouncement().schneider()).isFalse();
        assertThat(data.getAnnouncement().schwarz()).isFalse();
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        assertThat(data.getDeclarer()).isNull();
        assertThat(data.getAnnouncement().gameType()).isNull();
        assertThat(data.getAnnouncement().discardedCards()).hasSize(0);
        assertThat(data.getAnnouncement().hand()).isTrue();
        assertThat(data.getAnnouncement().ouvert()).isFalse();
        assertThat(data.getAnnouncement().ouvertCards()).isEmpty();
        assertThat(data.getAnnouncement().schneider()).isFalse();
        assertThat(data.getAnnouncement().schwarz()).isFalse();
    }
}
