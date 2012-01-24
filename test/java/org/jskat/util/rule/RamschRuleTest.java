package org.jskat.util.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link RamschRules}
 */
public class RamschRuleTest extends AbstractJSkatTest {

	private SkatGameData data;
	private GameAnnouncementFactory factory;

	private static BasicSkatRules ramschRules = SkatRuleFactory.getSkatRules(GameType.RAMSCH);

	/**
	 * {@inheritDoc}
	 */
	@Before
	public void initialize() {

		data = new SkatGameData();
	}

	/**
	 * Tests game value calculation<br>
	 * Fore hand made the most points
	 */
	@Test
	public void testCalcGameValue_ForeHandMostPoints() {

		data.addPlayerPoints(Player.FOREHAND, 100);
		data.addPlayerPoints(Player.MIDDLEHAND, 15);
		data.addPlayerPoints(Player.REARHAND, 5);

		assertFalse(ramschRules.calcGameWon(data));
		assertEquals(-100, ramschRules.calcGameResult(data));
	}

	/**
	 * Tests game value calculation<br>
	 * Middle hand made the most points
	 */
	@Test
	public void testCalcGameValue_MiddleHandMostPoints() {

		data.addPlayerPoints(Player.FOREHAND, 15);
		data.addPlayerPoints(Player.MIDDLEHAND, 100);
		data.addPlayerPoints(Player.REARHAND, 5);

		assertFalse(ramschRules.calcGameWon(data));
		assertEquals(-100, ramschRules.calcGameResult(data));
	}

	/**
	 * Tests game value calculation<br>
	 * Rear hand made the most points
	 */
	@Test
	public void testCalcGameValue_RearHandMostPoints() {

		data.addPlayerPoints(Player.FOREHAND, 15);
		data.addPlayerPoints(Player.MIDDLEHAND, 5);
		data.addPlayerPoints(Player.REARHAND, 100);

		assertFalse(ramschRules.calcGameWon(data));
		assertEquals(-100, ramschRules.calcGameResult(data));
	}

	/**
	 * Tests game value calculation<br>
	 * Fore hand and middle hand made the most points
	 */
	@Test
	public void testCalcGameValue_ForeHandMiddleHandMostPoints() {

		data.addPlayerPoints(Player.FOREHAND, 50);
		data.addPlayerPoints(Player.MIDDLEHAND, 50);
		data.addPlayerPoints(Player.REARHAND, 20);

		assertFalse(ramschRules.calcGameWon(data));
		assertEquals(-50, ramschRules.calcGameResult(data));
	}

	/**
	 * Tests game value calculation<br>
	 * Middle hand and rear hand made the most points
	 */
	@Test
	public void testCalcGameValue_MiddleHandRearHandMostPoints() {

		data.addPlayerPoints(Player.FOREHAND, 20);
		data.addPlayerPoints(Player.MIDDLEHAND, 50);
		data.addPlayerPoints(Player.REARHAND, 50);

		assertFalse(ramschRules.calcGameWon(data));
		assertEquals(-50, ramschRules.calcGameResult(data));
	}

	/**
	 * Tests game value calculation<br>
	 * Fore hand and rear hand made the most points
	 */
	@Test
	public void testCalcGameValue_ForeHandRearHandMostPoints() {

		data.addPlayerPoints(Player.FOREHAND, 50);
		data.addPlayerPoints(Player.MIDDLEHAND, 20);
		data.addPlayerPoints(Player.REARHAND, 50);

		assertFalse(ramschRules.calcGameWon(data));
		assertEquals(-50, ramschRules.calcGameResult(data));
	}

	/**
	 * Tests game value calculation<br>
	 * Rear hand made the most points
	 */
	@Test
	public void testCalcGameValue_AllPlayerEqualPoints() {

		data.addPlayerPoints(Player.FOREHAND, 40);
		data.addPlayerPoints(Player.MIDDLEHAND, 40);
		data.addPlayerPoints(Player.REARHAND, 40);

		assertFalse(ramschRules.calcGameWon(data));
		assertEquals(-40, ramschRules.calcGameResult(data));
	}

	/**
	 * Test the calculation of the multiplier
	 */
	@Test
	public void testGetMultiplierJungfrau() {

		data.addGeschoben();
		assertEquals(2, ramschRules.getMultiplier(data));

		data.addGeschoben();
		assertEquals(4, ramschRules.getMultiplier(data));

		data.addGeschoben();
		assertEquals(8, ramschRules.getMultiplier(data));

		for (int i = 0; i < 10; i++) {
			Trick trick = new Trick(0, Player.FOREHAND);
			if (i < 9) {
				trick.setTrickWinner(Player.FOREHAND);
			} else {
				trick.setTrickWinner(Player.MIDDLEHAND);
			}
			data.addTrick(trick);
		}

		data.setJungfrauDurchmarsch();
		assertEquals(16, ramschRules.getMultiplier(data));
	}

	/**
	 * Test the calculation of the multiplier
	 */
	@Test
	public void testGetMultiplierDurchmarsch() {

		data.addGeschoben();
		assertEquals(2, ramschRules.getMultiplier(data));

		data.addGeschoben();
		assertEquals(4, ramschRules.getMultiplier(data));

		data.addGeschoben();
		assertEquals(8, ramschRules.getMultiplier(data));

		// all tricks are made by forehand player
		for (int i = 0; i < 10; i++) {
			Trick trick = new Trick(i, Player.FOREHAND);
			trick.setTrickWinner(Player.FOREHAND);
			data.addTrick(trick);
		}

		data.setJungfrauDurchmarsch();
		assertEquals(16, ramschRules.getMultiplier(data));
	}
}
