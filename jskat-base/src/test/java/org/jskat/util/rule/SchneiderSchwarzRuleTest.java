/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	private static SuitGrandRule clubRules = (SuitGrandRule) SkatRuleFactory
			.getSkatRules(GameType.CLUBS);

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

		factory.setHand(false);
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
		SuitGrandRule nullRules = (SuitGrandRule) SkatRuleFactory
				.getSkatRules(GameType.NULL);
	}

	/**
	 * Test for casting ramsch rules into suit/grand rules
	 */
	@Test(expected = ClassCastException.class)
	public void testCast002() {

		data.setAnnouncement(factory.getAnnouncement());
		SuitGrandRule nullRules = (SuitGrandRule) SkatRuleFactory
				.getSkatRules(GameType.RAMSCH);
	}
}
