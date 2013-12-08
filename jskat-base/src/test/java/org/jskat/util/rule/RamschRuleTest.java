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
package org.jskat.util.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link RamschRule}
 */
public class RamschRuleTest extends AbstractJSkatTest {

	private SkatGameData data;
	private GameAnnouncementFactory factory;

	private static SkatRule ramschRules = SkatRuleFactory
			.getSkatRules(GameType.RAMSCH);

	/**
	 * {@inheritDoc}
	 */
	@Before
	public void initialize() {

		data = new SkatGameData();
		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.RAMSCH);
		data.setAnnouncement(factory.getAnnouncement());
	}

	@Test(expected = IllegalStateException.class)
	public void testWrongGameData_NoAnnouncement() {
		data = new SkatGameData();

		data.getRamschLoosers();
	}

	@Test(expected = IllegalStateException.class)
	public void testWrongGameData_NoRamschAnnouncement() {
		data = new SkatGameData();
		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.CLUBS);
		data.setAnnouncement(factory.getAnnouncement());

		data.getRamschLoosers();
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

		assertFalse(ramschRules.isGameWon(data));
		assertEquals(-100, ramschRules.calcGameResult(data));

		data.finishRamschGame();

		assertNull(data.getDeclarer());
		Set<Player> ramschLoosers = data.getRamschLoosers();
		assertEquals(1, ramschLoosers.size());
		assertTrue(ramschLoosers.contains(Player.FOREHAND));
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

		assertFalse(ramschRules.isGameWon(data));
		assertEquals(-100, ramschRules.calcGameResult(data));

		data.finishRamschGame();

		assertNull(data.getDeclarer());
		Set<Player> ramschLoosers = data.getRamschLoosers();
		assertEquals(1, ramschLoosers.size());
		assertTrue(ramschLoosers.contains(Player.MIDDLEHAND));
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

		assertFalse(ramschRules.isGameWon(data));
		assertEquals(-100, ramschRules.calcGameResult(data));

		data.finishRamschGame();

		assertNull(data.getDeclarer());
		Set<Player> ramschLoosers = data.getRamschLoosers();
		assertEquals(1, ramschLoosers.size());
		assertTrue(ramschLoosers.contains(Player.REARHAND));
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

		assertFalse(ramschRules.isGameWon(data));
		assertEquals(-50, ramschRules.calcGameResult(data));

		data.finishRamschGame();

		assertNull(data.getDeclarer());
		Set<Player> ramschLoosers = data.getRamschLoosers();
		assertEquals(2, ramschLoosers.size());
		assertTrue(ramschLoosers.contains(Player.FOREHAND));
		assertTrue(ramschLoosers.contains(Player.MIDDLEHAND));
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

		assertFalse(ramschRules.isGameWon(data));
		assertEquals(-50, ramschRules.calcGameResult(data));

		data.finishRamschGame();

		assertNull(data.getDeclarer());
		Set<Player> ramschLoosers = data.getRamschLoosers();
		assertEquals(2, ramschLoosers.size());
		assertTrue(ramschLoosers.contains(Player.MIDDLEHAND));
		assertTrue(ramschLoosers.contains(Player.REARHAND));
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

		assertFalse(ramschRules.isGameWon(data));
		assertEquals(-50, ramschRules.calcGameResult(data));

		data.finishRamschGame();

		assertNull(data.getDeclarer());
		Set<Player> ramschLoosers = data.getRamschLoosers();
		assertEquals(2, ramschLoosers.size());
		assertTrue(ramschLoosers.contains(Player.FOREHAND));
		assertTrue(ramschLoosers.contains(Player.REARHAND));
	}

	/**
	 * Tests game value calculation<br>
	 * all player made same points
	 */
	@Test
	public void testCalcGameValue_AllPlayerEqualPoints() {

		data.addPlayerPoints(Player.FOREHAND, 40);
		data.addPlayerPoints(Player.MIDDLEHAND, 40);
		data.addPlayerPoints(Player.REARHAND, 40);

		assertFalse(ramschRules.isGameWon(data));
		assertEquals(-40, ramschRules.calcGameResult(data));

		data.finishRamschGame();

		assertNull(data.getDeclarer());
		Set<Player> ramschLoosers = data.getRamschLoosers();
		assertEquals(3, ramschLoosers.size());
		assertTrue(ramschLoosers.contains(Player.FOREHAND));
		assertTrue(ramschLoosers.contains(Player.MIDDLEHAND));
		assertTrue(ramschLoosers.contains(Player.REARHAND));
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
