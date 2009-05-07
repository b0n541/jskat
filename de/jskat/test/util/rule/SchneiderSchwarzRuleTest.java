/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.test.util.rule;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.util.GameType;
import de.jskat.util.Player;
import de.jskat.util.rule.SkatRuleFactory;
import de.jskat.util.rule.SuitGrandRules;

/**
 * Tests schneider and schwarz rules
 */
public class SchneiderSchwarzRuleTest {

	/**
	 * @see BeforeClass
	 */
	@BeforeClass
	public static void setUpBeforeClass() {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$

		data = new SkatGameData();
		ann = new GameAnnouncement();
		ann.setGameType(GameType.CLUBS);
		data.setAnnouncement(ann);
		data.setDeclarer(Player.FORE_HAND);
	}

	/**
	 * @see AfterClass
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		// TODO implement it
	}

	/**
	 * @see Before
	 */
	@Before
	public void setUp() {
		// TODO implement it
	}

	/**
	 * @see After
	 */
	@After
	public void tearDown() {
		// TODO implement it
	}

	/**
	 * Test case 000 for schneider rule
	 */
	@Test
	public void testSchneider000() {

		assertTrue(clubRules.isSchneider(data));
	}

	/**
	 * Test case 000 for schwarz rule
	 */
	@Test
	public void testSchwarz000() {

		assertTrue(clubRules.isSchwarz(data));
	}

	@Test (expected=ClassCastException.class)
	public void testCast001() {

		SuitGrandRules nullRules = (SuitGrandRules) SkatRuleFactory
				.getSkatRules(GameType.NULL);
	}

	@Test (expected=ClassCastException.class)
	public void testCast002() {

		SuitGrandRules nullRules = (SuitGrandRules) SkatRuleFactory
				.getSkatRules(GameType.RAMSCH);
	}

	private static SkatGameData data;
	private static GameAnnouncement ann;

	private static SuitGrandRules clubRules = (SuitGrandRules) SkatRuleFactory
			.getSkatRules(GameType.CLUBS);
	private static SuitGrandRules spadeRules = (SuitGrandRules) SkatRuleFactory
			.getSkatRules(GameType.SPADES);
	private static SuitGrandRules heartRules = (SuitGrandRules) SkatRuleFactory
			.getSkatRules(GameType.HEARTS);
	private static SuitGrandRules diamondRules = (SuitGrandRules) SkatRuleFactory
			.getSkatRules(GameType.DIAMONDS);
	private static SuitGrandRules grandRules = (SuitGrandRules) SkatRuleFactory
			.getSkatRules(GameType.GRAND);
}
