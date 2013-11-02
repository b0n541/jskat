/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
