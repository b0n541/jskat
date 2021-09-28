package org.jskat.ai.deeplearning;

import com.google.common.eventbus.EventBus;
import org.jskat.AbstractJSkatTest;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.SkatSeries;
import org.jskat.gui.UnitTestView;
import org.jskat.player.JSkatPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AIPlayerDLTest extends AbstractJSkatTest {
    @Test
    void testRandomPlayer() {
        JSkatEventBus.TABLE_EVENT_BUSSES.put("test", new EventBus());
        final JSkatPlayer foreHand = new AIPlayerRND();
        final JSkatPlayer middleHand = new AIPlayerDL();
        final JSkatPlayer rearHand = new AIPlayerDL();

        final SkatSeries series = new SkatSeries("test");
        series.setView(new UnitTestView());
        series.setMaxRounds(100, false);
        series.setPlayers(List.of(foreHand, middleHand, rearHand));

        series.run();

        System.out.println(series.getSeriesResult());
    }
}
