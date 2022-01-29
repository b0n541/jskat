
package org.jskat.control.event;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.HoldBidEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HoldBidEventTest extends AbstractJSkatTest {

    private SkatGameData data;
    private HoldBidEvent event;
    private HoldBidEvent event2;
    private HoldBidEvent event3;

    @BeforeEach
    public void setUp() {
        data = new SkatGameData();
        event = new HoldBidEvent(Player.FOREHAND, 18);
        event2 = new HoldBidEvent(Player.MIDDLEHAND, 18);
        event3 = new HoldBidEvent(Player.FOREHAND, 20);
    }

    @Test
    public void skatGameDataAfterEvent() {

        event.processForward(data);

        checkMaxBid(18);
        checkPlayerBid(Player.FOREHAND, 18);
        checkPlayerBid(Player.MIDDLEHAND, 0);
        checkPlayerBid(Player.REARHAND, 0);

        event2.processForward(data);

        checkMaxBid(18);
        checkPlayerBid(Player.FOREHAND, 18);
        checkPlayerBid(Player.MIDDLEHAND, 18);
        checkPlayerBid(Player.REARHAND, 0);

        event3.processForward(data);

        checkMaxBid(20);
        checkPlayerBid(Player.FOREHAND, 20);
        checkPlayerBid(Player.MIDDLEHAND, 18);
        checkPlayerBid(Player.REARHAND, 0);
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

        checkMaxBid(18);
        checkPlayerBid(Player.FOREHAND, 18);
        checkPlayerBid(Player.MIDDLEHAND, 18);
        checkPlayerBid(Player.REARHAND, 0);

        event2.processBackward(data);

        checkMaxBid(18);
        checkPlayerBid(Player.FOREHAND, 18);
        checkPlayerBid(Player.MIDDLEHAND, 0);
        checkPlayerBid(Player.REARHAND, 0);

        event.processBackward(data);

        checkMaxBid(0);
        checkPlayerBid(Player.FOREHAND, 0);
        checkPlayerBid(Player.MIDDLEHAND, 0);
        checkPlayerBid(Player.REARHAND, 0);
    }
}
