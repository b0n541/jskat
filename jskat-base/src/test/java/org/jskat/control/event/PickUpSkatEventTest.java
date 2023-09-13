package org.jskat.control.event;


import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.PickUpSkatEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PickUpSkatEventTest extends AbstractJSkatTest {

    private SkatGameData data;
    private PickUpSkatEvent event;

    @BeforeEach
    public void setUp() {
        data = new SkatGameData();

        final CardList skat = new CardList(Card.CJ, Card.SJ);

        data.setDealtSkatCards(skat);
        event = new PickUpSkatEvent(Player.FOREHAND);
    }

    @Test
    public void skatGameDataAfterEvent() {

        event.processForward(data);

        assertThat(data.getPlayerCards(Player.FOREHAND)).containsExactlyInAnyOrder(Card.CJ, Card.SJ);
        assertThat(data.getSkat()).hasSize(0);
        assertThat(data.isSkatPickedUp()).isTrue();
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        assertThat(data.getPlayerCards(Player.FOREHAND)).hasSize(0);
        assertThat(data.getSkat()).containsExactlyInAnyOrder(Card.CJ, Card.SJ);
        assertThat(data.isSkatPickedUp()).isFalse();
    }
}
