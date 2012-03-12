package org.jskat.util.rule;

import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests schneider and schwarz rules
 */
public class SchneiderSchwarzRuleTest extends AbstractJSkatTest {

	private SkatGameData data;
	private GameAnnouncementFactory factory;

	private static SuitGrandRules clubRules = (SuitGrandRules) SkatRuleFactory.getSkatRules(GameType.CLUBS);

	/**
	 * @see BeforeClass
	 */
	@Before
	public void setUpBeforeClass() {

		data = new SkatGameData();
		factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.CLUBS);
		data.setDeclarer(Player.FOREHAND);
	}

	/**
	 * Test case 000 for schneider rule
	 */
	@Test
	public void testSchneider000() {

		data.setDeclarerPickedUpSkat(true);
		data.setAnnouncement(factory.getAnnouncement());
		assertTrue(clubRules.isSchneider(data));
	}

	/**
	 * Test case 000 for schwarz rule
	 */
	@Test
	public void testSchwarz000() {

		data.setAnnouncement(factory.getAnnouncement());
		assertTrue(clubRules.isSchwarz(data));
	}

	/**
	 * Test for casting null rules into suit/grand rules
	 */
	@Test(expected = ClassCastException.class)
	public void testCast001() {

		data.setAnnouncement(factory.getAnnouncement());
		SuitGrandRules nullRules = (SuitGrandRules) SkatRuleFactory.getSkatRules(GameType.NULL);
	}

	/**
	 * Test for casting ramsch rules into suit/grand rules
	 */
	@Test(expected = ClassCastException.class)
	public void testCast002() {

		data.setAnnouncement(factory.getAnnouncement());
		SuitGrandRules nullRules = (SuitGrandRules) SkatRuleFactory.getSkatRules(GameType.RAMSCH);
	}
}
