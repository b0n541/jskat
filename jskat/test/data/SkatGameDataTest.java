/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test.data;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import jskat.data.SkatGameData;
import jskat.share.SkatConstants;
import jskat.share.Tools;

/**
 * Runs all unit tests for class SkatGameData
 * 
 * @see jskat.data.SkatGameData
 */
public class SkatGameDataTest {

	/**
	 * Setup for all tests
	 */
	@BeforeClass
	public static void setUp() {

		Tools.checkLog();

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

	/**
	 * Test 001 for method getSinglePlayer
	 * 
	 * @see jskat.data.SkatGameData#getSinglePlayer()
	 */
	@Test
	public void getSinglePlayer001() {
		
		assertEquals(1, game001.getSinglePlayer());
	}
	
	/**
	 * Test 002 for method getSinglePlayer
	 * 
	 * @see jskat.data.SkatGameData#getSinglePlayer()
	 */
	@Test
	public void getSinglePlayer002() {
		
		assertEquals(2, game002.getSinglePlayer());
	}
	
	/**
	 * Test 003 for method getSinglePlayer
	 * 
	 * @see jskat.data.SkatGameData#getSinglePlayer()
	 */
	@Test
	public void getSinglePlayer003() {
			
		assertEquals(0, game003.getSinglePlayer());
	}

	/**
	 * Test 001 for method getSinglePlayerScore
	 * 
	 * @see jskat.data.SkatGameData#getSinglePlayerScore()
	 */
	@Test
	public void getSinglePlayerScore001() {
		
		assertEquals(64, game001.getSinglePlayerScore());
	}

	/**
	 * Test 002 for method getSinglePlayerScore
	 * 
	 * @see jskat.data.SkatGameData#getSinglePlayerScore()
	 */
	@Test
	public void getSinglePlayerScore002() {
		
		assertEquals(64, game002.getSinglePlayerScore());
	}
	
	/**
	 * Test 003 for method getSinglePlayerScore
	 * 
	 * @see jskat.data.SkatGameData#getSinglePlayerScore()
	 */
	@Test
	public void getSinglePlayerScore003() {
		
		assertEquals(64, game003.getSinglePlayerScore());
	}

	/**
	 * Test 001 for method isGameLost
	 * 
	 * @see jskat.data.SkatGameData#isGameLost()
	 */
	@Test
	public void isGameLost001() {
		
		assertTrue(game001.isGameLost());
	}

	/**
	 * Test 002 for method isGameLost
	 * 
	 * @see jskat.data.SkatGameData#isGameLost()
	 */
	@Test
	public void isGameLost002() {
		
		assertTrue(game002.isGameLost());
	}

	/**
	 * Test 003 for method isGameLost
	 * 
	 * @see jskat.data.SkatGameData#isGameLost()
	 */
	@Test
	public void isGameLost003() {
		
		assertTrue(game003.isGameLost());
	}

	/**
	 * Test 001 for method getGameResult
	 * 
	 * @see jskat.data.SkatGameData#getGameResult()
	 */
	@Test
	public void getGameResult001() {
		
		assertEquals(-4 * 64, game001.getGameResult());
	}

	/**
	 * Test 002 for method getGameResult
	 * 
	 * @see jskat.data.SkatGameData#getGameResult()
	 */
	@Test
	public void getGameResult002() {
		
		assertEquals(-64, game002.getGameResult());
	}

	/**
	 * Test 003 for method getGameResult
	 * 
	 * @see jskat.data.SkatGameData#getGameResult()
	 */
	@Test
	public void getGameResult003() {
		
		assertEquals(-8 * 64, game003.getGameResult());
	}

	private static SkatGameData game001;
	private static SkatGameData game002;
	private static SkatGameData game003;
}
