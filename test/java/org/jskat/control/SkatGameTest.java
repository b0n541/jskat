package org.jskat.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.ai.test.NoBiddingTestPlayer;
import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameResult;
import org.jskat.gui.UnitTestView;
import org.jskat.util.GameType;
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
	public void testRamsch_NoBids() {
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

		GameSummary summary = game.getGameSummary();
		assertEquals(GameType.RAMSCH, summary.getGameType());

		SkatGameResult result = game.getGameResult();
		assertFalse(result.isWon());
		assertTrue(result.getGameValue() < 0);
	}

	/**
	 * Forced ramsch game
	 */
	@Test
	public void testRamsch_Forced() {
		SkatGame game = new SkatGame("Table 1", GameVariant.RAMSCH, new AIPlayerRND(), //$NON-NLS-1$
				new AIPlayerRND(), new AIPlayerRND());
		game.setView(new UnitTestView());

		game.start();
		try {
			game.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GameSummary summary = game.getGameSummary();
		assertEquals(GameType.RAMSCH, summary.getGameType());

		SkatGameResult result = game.getGameResult();
		assertFalse(result.isWon());
		assertTrue(result.getGameValue() < 0);
	}
}
