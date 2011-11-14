package org.jskat.control;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.IJSkatPlayer;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.gui.UnitTestView;
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

		List<IJSkatPlayer> player = new ArrayList<IJSkatPlayer>();
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
