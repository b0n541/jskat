
package org.jskat.control.event;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.ContraEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ContraEventTest extends AbstractJSkatTest {

    private SkatGameData data;
    private ContraEvent event;

    @BeforeEach
    public void setUp() {
        data = new SkatGameData();
        event = new ContraEvent(Player.FOREHAND);
    }

    @Test
    public void skatGameDataAfterEvent() {

        event.processForward(data);

        assertTrue(data.isContra());
    }

    @Test
    public void skatGameDataBeforeEvent() {

        event.processForward(data);
        event.processBackward(data);

        assertFalse(data.isContra());
    }
}
