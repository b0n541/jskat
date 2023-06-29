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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PickUpSkatEventTest extends AbstractJSkatTest {

    private SkatGameData data;
    private PickUpSkatEvent event;

    @BeforeEach
    public void setUp() {
        data = new SkatGameData();

        final CardList skat = new CardList(Card.CJ, Card.SJ);

        data.addDealtCards(Player.FOREHAND, new CardList(Card.HJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.CT, Card.C9, Card.C8, Card.C7, Card.SA));
        data.setDealtSkatCards(skat);
        event = new PickUpSkatEvent(Player.FOREHAND);
    }

    @Test
    public void skatGameDataAfterEvent() {

        event.processForward(data);

        assertFalse(data.isHand());
        assertThat(data.getPlayerCards(Player.FOREHAND)).containsExactlyInAnyOrder(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.CT, Card.C9, Card.C8, Card.C7, Card.SA);
        assertThat(data.getSkat()).hasSize(0);
        assertTrue(data.isSkatPickedUp());
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        assertTrue(data.isHand());
        assertThat(data.getPlayerCards(Player.FOREHAND)).contains(Card.HJ, Card.DJ, Card.CA, Card.CK, Card.CQ, Card.CT, Card.C9, Card.C8, Card.C7, Card.SA);
        assertThat(data.getSkat()).containsExactlyInAnyOrder(Card.CJ, Card.SJ);
        assertFalse(data.isSkatPickedUp());
    }
}
