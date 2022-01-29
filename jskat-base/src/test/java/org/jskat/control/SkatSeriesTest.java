
package org.jskat.control;


import com.google.common.eventbus.EventBus;
import org.jskat.AbstractJSkatTest;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.gui.UnitTestView;
import org.jskat.player.JSkatPlayer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test class for {@link SkatSeries}
 */
public class SkatSeriesTest extends AbstractJSkatTest {

    private final static String TABLE_NAME = "Table 1";

    @Test
    public void testSkatSeriesRun() {
        JSkatEventBus.TABLE_EVENT_BUSSES.put(TABLE_NAME, new EventBus());
        final SkatSeries series = new SkatSeries(TABLE_NAME);
        final UnitTestView view = new UnitTestView();
        series.setView(view);

        final List<JSkatPlayer> players = new ArrayList<JSkatPlayer>();
        players.add(new AIPlayerRND());
        players.add(new AIPlayerRND());
        players.add(new AIPlayerRND());

        series.setPlayers(players);

        series.setMaxRounds(1, false);

        try {
            CompletableFuture.runAsync(() -> series.run()).get();
        } catch (final InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertThat(series.getSeriesState()).isEqualTo(SeriesState.SERIES_FINISHED);
    }
}
