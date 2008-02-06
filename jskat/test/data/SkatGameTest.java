/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test.data;

import jskat.data.SkatGameData;
import jskat.share.SkatConstants;
import junit.framework.TestCase;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 * 
 */
public class SkatGameTest extends TestCase {
	/**
	 * Constructor for SkatGameTest.
	 * 
	 * @param arg0
	 */
	public SkatGameTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SkatGameTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		game001 = new SkatGameData();
		game001.setGameType(SkatConstants.GameTypes.RAMSCH);
		game001.geschoben();
		game001.addToPlayerPoints(0, 56);
		game001.addToPlayerPoints(1, 64);
		game001.addToPlayerPoints(2, 0);
		game001.setJungFrau(true);
		game001.calcResult();

		game002 = new SkatGameData();
		game002.setGameType(SkatConstants.GameTypes.RAMSCH);
		game002.addToPlayerPoints(0, 11);
		game002.addToPlayerPoints(1, 45);
		game002.addToPlayerPoints(2, 64);
		game002.calcResult();

		game003 = new SkatGameData();
		game003.setGameType(SkatConstants.GameTypes.RAMSCH);
		game003.geschoben();
		game003.geschoben();
		game003.geschoben();
		game003.addToPlayerPoints(0, 64);
		game003.addToPlayerPoints(1, 28);
		game003.addToPlayerPoints(2, 28);
		game003.calcResult();
	}

	public void testGetSinglePlayer() {
		assertEquals(1, game001.getSinglePlayer());
		assertEquals(2, game002.getSinglePlayer());
		assertEquals(0, game003.getSinglePlayer());
	}

	public void testGetPlayerScore() {
		assertEquals(64, game001.getSinglePlayerScore());
		assertEquals(64, game002.getSinglePlayerScore());
		assertEquals(64, game003.getSinglePlayerScore());
	}

	public void testGetGameLost() {
		assertTrue(game001.isGameLost());
		assertTrue(game002.isGameLost());
		assertTrue(game003.isGameLost());
	}

	public void testGetGameResult() {
		assertEquals(-4 * 64, game001.getGameResult());
		assertEquals(-64, game002.getGameResult());
		assertEquals(-8 * 64, game003.getGameResult());
	}

	private SkatGameData game001;

	private SkatGameData game002;

	private SkatGameData game003;
}
