package org.jskat.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.test.NoBiddingTestPlayer;
import org.jskat.data.SkatGameResult;
import org.jskat.gui.UnitTestView;
import org.jskat.util.GameVariant;
import org.junit.Test;

/**
 * Test class for {@link SkatGame}
 */
public class SkatGameTest extends AbstractJSkatTest {

	/**
	 * When no player bids, ramsch is played
	 */
	@Test
	public void testRamschAfterNoBids() {
		SkatGame game = new SkatGame("Table 1", GameVariant.STANDARD, new NoBiddingTestPlayer(), //$NON-NLS-1$
				new NoBiddingTestPlayer(), new NoBiddingTestPlayer());
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SkatGameResult gameResult = game.getGameResult();
		assertFalse(gameResult.isWon());
		assertTrue(gameResult.getGameValue() < 0);
	}
}
