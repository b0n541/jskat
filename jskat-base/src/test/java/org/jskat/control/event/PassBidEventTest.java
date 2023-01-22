package org.jskat.control.event;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.PassBidEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PassBidEventTest extends AbstractJSkatTest {

    private SkatGameData data;
    private PassBidEvent event;
    private PassBidEvent event2;
    private PassBidEvent event3;

    @BeforeEach
    public void setUp() {
        data = new SkatGameData();
        event = new PassBidEvent(Player.FOREHAND, 18);
        event2 = new PassBidEvent(Player.MIDDLEHAND, 18);
        event3 = new PassBidEvent(Player.REARHAND, 18);
    }

    @Test
    public void skatGameDataAfterEvent() {

        event.processForward(data);

        checkMaxBid(0);
        checkPlayerBid(Player.FOREHAND, 0);
        assertTrue(data.isPlayerPass(Player.FOREHAND));
        checkPlayerBid(Player.MIDDLEHAND, 0);
        assertFalse(data.isPlayerPass(Player.MIDDLEHAND));
        checkPlayerBid(Player.REARHAND, 0);
        assertFalse(data.isPlayerPass(Player.REARHAND));

        event2.processForward(data);

        checkMaxBid(0);
        checkPlayerBid(Player.FOREHAND, 0);
        assertTrue(data.isPlayerPass(Player.FOREHAND));
        checkPlayerBid(Player.MIDDLEHAND, 0);
        assertTrue(data.isPlayerPass(Player.MIDDLEHAND));
        checkPlayerBid(Player.REARHAND, 0);
        assertFalse(data.isPlayerPass(Player.REARHAND));

        event3.processForward(data);

        checkMaxBid(0);
        checkPlayerBid(Player.FOREHAND, 0);
        assertTrue(data.isPlayerPass(Player.FOREHAND));
        checkPlayerBid(Player.MIDDLEHAND, 0);
        assertTrue(data.isPlayerPass(Player.MIDDLEHAND));
        checkPlayerBid(Player.REARHAND, 0);
        assertTrue(data.isPlayerPass(Player.REARHAND));
    }

    private void checkMaxBid(final Integer bid) {
        assertThat(data.getMaxBidValue()).isEqualTo(bid);
    }

    private void checkPlayerBid(final Player player, final Integer bid) {
        assertThat(data.getMaxPlayerBid(player)).isEqualTo(bid);
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event2.processForward(data);
        event3.processForward(data);
        event3.processBackward(data);

        checkMaxBid(0);
        checkPlayerBid(Player.FOREHAND, 0);
        assertTrue(data.isPlayerPass(Player.FOREHAND));
        checkPlayerBid(Player.MIDDLEHAND, 0);
        assertTrue(data.isPlayerPass(Player.MIDDLEHAND));
        checkPlayerBid(Player.REARHAND, 0);
        assertFalse(data.isPlayerPass(Player.REARHAND));

        event2.processBackward(data);

        checkMaxBid(0);
        checkPlayerBid(Player.FOREHAND, 0);
        assertTrue(data.isPlayerPass(Player.FOREHAND));
        checkPlayerBid(Player.MIDDLEHAND, 0);
        assertFalse(data.isPlayerPass(Player.MIDDLEHAND));
        checkPlayerBid(Player.REARHAND, 0);
        assertFalse(data.isPlayerPass(Player.REARHAND));

        event.processBackward(data);

        checkMaxBid(0);
        checkPlayerBid(Player.FOREHAND, 0);
        assertFalse(data.isPlayerPass(Player.FOREHAND));
        checkPlayerBid(Player.MIDDLEHAND, 0);
        assertFalse(data.isPlayerPass(Player.MIDDLEHAND));
        checkPlayerBid(Player.REARHAND, 0);
        assertFalse(data.isPlayerPass(Player.REARHAND));
    }
}
