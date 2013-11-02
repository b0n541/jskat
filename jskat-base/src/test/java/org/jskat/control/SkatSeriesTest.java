/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
