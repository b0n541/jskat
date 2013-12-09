/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.gui.UnitTestView;
import org.jskat.player.JSkatPlayer;
import org.junit.Test;

/**
 * Test class for {@link SkatSeries}
 */
public class SkatSeriesTest extends AbstractJSkatTest {

	@Test
	public void testSkatSeriesRun() {
		SkatSeries series = new SkatSeries("ASDF"); //$NON-NLS-1$
		UnitTestView view = new UnitTestView();
		series.setView(view);

		List<JSkatPlayer> player = new ArrayList<JSkatPlayer>();
		player.add(new AIPlayerRND());
		player.add(new AIPlayerRND());
		player.add(new AIPlayerRND());

		series.setPlayer(player);

		series.setMaxRounds(1, false);

		series.start();
		try {
			series.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(SeriesState.SERIES_FINISHED, series.getSeriesState());
	}
}
