/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
