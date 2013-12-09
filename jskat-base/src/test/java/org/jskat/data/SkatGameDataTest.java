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
package org.jskat.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class SkatGameDataTest {

	SkatGameData gameData;

	@Before
	public void createGameData() {
		gameData = new SkatGameData();
	}

	@Test
	public void hand() {

		assertTrue(gameData.isHand());

		gameData.addSkatToPlayer(Player.FOREHAND);

		assertFalse(gameData.isHand());
	}

	@Test
	public void schneider() {

		assertFalse(gameData.isSchneider());

		gameData.setDeclarerScore(91);

		assertTrue(gameData.isSchneider());
	}

	@Test
	public void schwarz() {

		assertFalse(gameData.isSchwarz());

		gameData.setDeclarerScore(120);

		assertTrue(gameData.isSchwarz());
	}

}
