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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.jskat.AbstractJSkatTest;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Test;
import org.mockito.Mockito;

public class GameSimulator2Test extends AbstractJSkatTest {

	@Test
	public void testSimulateGamesWithEpisodeLimit() {
		GameSimulator2 gameSimulator = new GameSimulator2();
		gameSimulator.add(new GameSimulation(GameType.GRAND, Player.FOREHAND, CardList.getPerfectGrandSuitHand()));

		GameSimulation bestSimulation = gameSimulator.simulateMaxEpisodes(10L);

		assertThat(bestSimulation.getEpisodes(), is(equalTo(10L)));
		assertThat(bestSimulation.getWonGames(), is(equalTo(10L)));
	}

	@Test
	public void testSimulateGamesWithTimeLimit() {
		GameSimulator2 gameSimulator = new GameSimulator2();
		gameSimulator.add(new GameSimulation(GameType.GRAND, Player.FOREHAND, CardList.getPerfectGrandSuitHand()));

		GameSimulation bestSimulation = gameSimulator.simulateMaxTime(1000L);

		assertThat(bestSimulation.getEpisodes(), is(greaterThan(10L)));
		assertThat(bestSimulation.getWonGames(), is(greaterThan(10L)));
	}

	@Test
	public void testSimulateGamesFiveGameTypesEqualDistribution() {

		GameSimulator2 gameSimulator = new GameSimulator2();
		gameSimulator.add(new GameSimulation(GameType.GRAND, Player.FOREHAND, CardList.getPerfectGrandSuitHand()));
		gameSimulator.add(new GameSimulation(GameType.CLUBS, Player.FOREHAND, CardList.getPerfectGrandSuitHand()));
		gameSimulator.add(new GameSimulation(GameType.SPADES, Player.FOREHAND, CardList.getPerfectGrandSuitHand()));
		gameSimulator.add(new GameSimulation(GameType.HEARTS, Player.FOREHAND, CardList.getPerfectGrandSuitHand()));
		gameSimulator.add(new GameSimulation(GameType.DIAMONDS, Player.FOREHAND, CardList.getPerfectGrandSuitHand()));

		GameSimulation bestSimulation = gameSimulator.simulateMaxEpisodes(100L);

		assertThat(bestSimulation.getEpisodes(), is(20L));
		assertThat(bestSimulation.getWonGames(), is(20L));
	}

	@Test
	public void testSimulationSelection() {

		GameSimulator2 gameSimulator = new GameSimulator2();

		GameSimulation nullSimulation = Mockito.mock(GameSimulation.class);
		Mockito.when(nullSimulation.getGameType()).thenReturn(GameType.NULL);
		GameSimulation grandSimulation = Mockito.mock(GameSimulation.class);
		Mockito.when(grandSimulation.getGameType()).thenReturn(GameType.GRAND);

		gameSimulator.add(nullSimulation);
		gameSimulator.add(grandSimulation);

		Mockito.when(nullSimulation.getEpisodes()).thenReturn(0L);
		Mockito.when(nullSimulation.getWonRate()).thenReturn(0.0);
		Mockito.when(nullSimulation.getDeclarerPointsMedian()).thenReturn(10.0);

		Mockito.when(grandSimulation.getEpisodes()).thenReturn(1L);
		Mockito.when(grandSimulation.getWonRate()).thenReturn(1.0);
		Mockito.when(grandSimulation.getDeclarerPointsMedian()).thenReturn(80.0);

		assertThat(gameSimulator.getNextSimulation().getGameType(), is(GameType.NULL));

		GameSimulation bestSimulation = gameSimulator.simulateMaxEpisodes(1L);

		assertThat(bestSimulation.getGameType(), is(GameType.GRAND));

		Mockito.when(nullSimulation.getEpisodes()).thenReturn(1L);

		assertThat(gameSimulator.getNextSimulation().getGameType(), is(GameType.GRAND));

		bestSimulation = gameSimulator.simulateMaxEpisodes(1L);

		assertThat(bestSimulation.getGameType(), is(GameType.GRAND));

		Mockito.when(nullSimulation.getWonRate()).thenReturn(0.9);
		Mockito.when(grandSimulation.getEpisodes()).thenReturn(2L);
		Mockito.when(grandSimulation.getWonRate()).thenReturn(0.9);

		assertThat(gameSimulator.getNextSimulation().getGameType(), is(GameType.NULL));

		gameSimulator.simulateMaxEpisodes(1L);

		Mockito.when(grandSimulation.getWonRate()).thenReturn(0.8);

		bestSimulation = gameSimulator.simulateMaxEpisodes(1L);

		assertThat(bestSimulation.getGameType(), is(GameType.NULL));
	}
}
