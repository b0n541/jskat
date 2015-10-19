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
import java.util.List;
import java.util.Random;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.command.table.CreateTableCommand;
import org.jskat.data.JSkatViewType;
import org.jskat.data.SkatGameResult;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class GameSimulationTest extends AbstractJSkatTest {

	private static final String TABLE_NAME = "ASDF";
	private final static Random RANDOM = new Random();
	private final static List<GameType> GAME_TYPES = Arrays.asList(
			GameType.GRAND, GameType.CLUBS, GameType.SPADES, GameType.HEARTS,
			GameType.DIAMONDS, GameType.NULL, GameType.RAMSCH);
	private final static List<Player> PLAYER_POSITIONS = Arrays
			.asList(Player.FOREHAND, Player.MIDDLEHAND, Player.REARHAND);

	@Before
	public void setup() {
		JSkatEventBus.INSTANCE.post(
				new CreateTableCommand(JSkatViewType.LOCAL_TABLE, TABLE_NAME));
	}

	@Test
	public void testSimulationAllGamesWonWithSchneiderSchwarz() {
		GameSimulation simulation = new GameSimulation(GameType.GRAND,
				getRandomPlayer(), CardList.getPerfectGrandSuitHand(),
				new CardList(Card.D7, Card.D8));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame(TABLE_NAME);
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonGames(), is(i + 1));
			assertThat(simulation.getWonRate(), is(1.0));
			assertThat(simulation.getWonGamesWithSchneider(), is(i + 1));
			assertThat(simulation.getWonRateWithSchneider(), is(1.0));
			assertThat(simulation.getWonGamesWithSchwarz(), is(i + 1));
			assertThat(simulation.getWonRateWithSchwarz(), is(1.0));
		}
	}

	@Test
	public void testSimulationKnownSkat() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				getRandomPlayer(), CardList.getRandomCards(10),
				CardList.getRandomCards(2));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame(TABLE_NAME);
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	@Test
	public void testSimulationUnknownSkat() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				getRandomPlayer(), CardList.getRandomCards(10));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame(TABLE_NAME);
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	@Test
	public void testSimulationForeHand() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				Player.FOREHAND, CardList.getRandomCards(10),
				CardList.getRandomCards(2));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame(TABLE_NAME);
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	@Test
	public void testSimulationMiddleHand() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				Player.MIDDLEHAND, CardList.getRandomCards(10),
				CardList.getRandomCards(2));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame(TABLE_NAME);
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	@Test
	public void testSimulationRearHand() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				Player.REARHAND, CardList.getRandomCards(10),
				CardList.getRandomCards(2));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame(TABLE_NAME);
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	@Test
	public void testSimulationStatistics() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				getRandomPlayer(), CardList.getRandomCards(10),
				CardList.getRandomCards(2));
		GameSimulation.Statistics statistics = simulation.new Statistics();

		assertThat(statistics.getSimulatedGames(), is(0L));
		assertThat(statistics.getWonGames(), is(0L));

		SkatGameResult gameResult = new SkatGameResult();
		statistics.adjust(gameResult);

		assertThat(statistics.getSimulatedGames(), is(1L));
		assertThat(statistics.getWonGames(), is(0L));
		assertThat(statistics.getWonRate(), is(0.0));
		assertThat(statistics.getWonGamesWithSchneider(), is(0L));
		assertThat(statistics.getWonRateWithSchneider(), is(0.0));
		assertThat(statistics.getWonGamesWithSchwarz(), is(0L));
		assertThat(statistics.getWonRateWithSchwarz(), is(0.0));

		gameResult.setWon(true);
		statistics.adjust(gameResult);

		assertThat(statistics.getSimulatedGames(), is(2L));
		assertThat(statistics.getWonGames(), is(1L));
		assertThat(statistics.getWonRate(), is(0.5));
		assertThat(statistics.getWonGamesWithSchneider(), is(0L));
		assertThat(statistics.getWonRateWithSchneider(), is(0.0));
		assertThat(statistics.getWonGamesWithSchwarz(), is(0L));
		assertThat(statistics.getWonRateWithSchwarz(), is(0.0));

		gameResult.setSchneider(true);
		statistics.adjust(gameResult);

		assertThat(statistics.getSimulatedGames(), is(3L));
		assertThat(statistics.getWonGames(), is(2L));
		assertThat(statistics.getWonRate(), is(2.0 / 3.0));
		assertThat(statistics.getWonGamesWithSchneider(), is(1L));
		assertThat(statistics.getWonRateWithSchneider(), is(1.0 / 3.0));
		assertThat(statistics.getWonGamesWithSchwarz(), is(0L));
		assertThat(statistics.getWonRateWithSchwarz(), is(0.0));

		gameResult.setSchwarz(true);
		statistics.adjust(gameResult);

		assertThat(statistics.getSimulatedGames(), is(4L));
		assertThat(statistics.getWonGames(), is(3L));
		assertThat(statistics.getWonRate(), is(0.75));
		assertThat(statistics.getWonGamesWithSchneider(), is(2L));
		assertThat(statistics.getWonRateWithSchneider(), is(0.5));
		assertThat(statistics.getWonGamesWithSchwarz(), is(1L));
		assertThat(statistics.getWonRateWithSchwarz(), is(0.25));
	}

	private GameType getRandomGameType() {
		return GAME_TYPES.get(RANDOM.nextInt(GAME_TYPES.size()));
	}

	private Player getRandomPlayer() {
		return PLAYER_POSITIONS.get(RANDOM.nextInt(PLAYER_POSITIONS.size()));
	}
}
