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
package org.jskat.ai.nn;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.jskat.AbstractJSkatTest;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Test;

public class GameSimulator2Test extends AbstractJSkatTest {

	@Test
	public void testSimulateGamesOneGameType() {
		GameSimulator2 gameSimulator = new GameSimulator2(10L, null);
		gameSimulator.add(new GameSimulation(GameType.GRAND, Player.FOREHAND,
				CardList.getPerfectGrandSuitHand()));

		GameSimulation bestSimulation = gameSimulator
				.simulate(Arrays.asList(GameType.GRAND));

		assertThat(bestSimulation.getSimulatedGames(), is(10L));
		assertThat(bestSimulation.getWonGames(), is(10L));
	}

	@Test
	public void testSimulateGamesFiveGameTypes() {
		GameSimulator2 gameSimulator = new GameSimulator2(10L, null);
		gameSimulator.add(new GameSimulation(GameType.GRAND, Player.FOREHAND,
				CardList.getPerfectGrandSuitHand()));
		gameSimulator.add(new GameSimulation(GameType.CLUBS, Player.FOREHAND,
				CardList.getPerfectGrandSuitHand()));
		gameSimulator.add(new GameSimulation(GameType.SPADES, Player.FOREHAND,
				CardList.getPerfectGrandSuitHand()));
		gameSimulator.add(new GameSimulation(GameType.HEARTS, Player.FOREHAND,
				CardList.getPerfectGrandSuitHand()));
		gameSimulator.add(new GameSimulation(GameType.DIAMONDS, Player.FOREHAND,
				CardList.getPerfectGrandSuitHand()));

		GameSimulation bestSimulation = gameSimulator
				.simulate(Arrays.asList(GameType.GRAND, GameType.CLUBS,
						GameType.SPADES, GameType.HEARTS, GameType.DIAMONDS));

		assertThat(bestSimulation.getSimulatedGames(), is(2L));
		assertThat(bestSimulation.getWonGames(), is(2L));
	}
}
