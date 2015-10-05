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
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class GameSimulationTest extends AbstractJSkatTest {

	private final static Random RANDOM = new Random();
	private final static List<GameType> GAME_TYPES = Arrays.asList(
			GameType.GRAND, GameType.CLUBS, GameType.SPADES, GameType.HEARTS,
			GameType.DIAMONDS, GameType.NULL, GameType.RAMSCH);
	private final static List<Player> PLAYER_POSITIONS = Arrays.asList(
			Player.FOREHAND, Player.MIDDLEHAND, Player.REARHAND);

	@Before
	public void setup() {
		JSkatEventBus.INSTANCE.post(new CreateTableCommand(
				JSkatViewType.LOCAL_TABLE, "ASDF"));
	}

	@Test
	public void testSimulationAllGamesWonWithSchneiderSchwarz() {
		GameSimulation simulation = new GameSimulation(GameType.GRAND,
				getRandomPlayer(), new CardList(Card.CJ, Card.SJ, Card.HJ,
						Card.DJ, Card.CA, Card.SA, Card.HA, Card.DA, Card.CT,
						Card.ST), new CardList(Card.D7, Card.D8));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame("ASDF");
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
				getRandomPlayer(), getRandomCards(10), getRandomCards(2));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame("ASDF");
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	@Test
	public void testSimulationUnknownSkat() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				getRandomPlayer(), getRandomCards(10));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame("ASDF");
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	@Test
	public void testSimulationForeHand() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				Player.FOREHAND, getRandomCards(10), getRandomCards(2));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame("ASDF");
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	@Test
	public void testSimulationMiddleHand() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				Player.MIDDLEHAND, getRandomCards(10), getRandomCards(2));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame("ASDF");
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	@Test
	public void testSimulationRearHand() {
		GameSimulation simulation = new GameSimulation(getRandomGameType(),
				Player.REARHAND, getRandomCards(10), getRandomCards(2));
		for (long i = 0; i < 10; i++) {
			simulation.simulateGame("ASDF");
			assertThat(simulation.getSimulatedGames(), is(i + 1));
			assertThat(simulation.getWonRate(),
					is(((double) simulation.getWonGames())
							/ ((double) simulation.getSimulatedGames())));
		}
	}

	private CardList getRandomCards(int cardCount) {
		CardDeck cardDeck = new CardDeck();
		CardList result = new CardList();
		for (int i = 0; i < cardCount; i++) {
			result.add(cardDeck.remove(RANDOM.nextInt(cardDeck.size())));
		}
		return result;
	}

	private GameType getRandomGameType() {
		return GAME_TYPES.get(RANDOM.nextInt(GAME_TYPES.size()));
	}

	private Player getRandomPlayer() {
		return PLAYER_POSITIONS.get(RANDOM.nextInt(PLAYER_POSITIONS.size()));
	}
}
