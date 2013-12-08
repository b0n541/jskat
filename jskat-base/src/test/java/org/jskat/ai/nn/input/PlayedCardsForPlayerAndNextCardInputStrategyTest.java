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
package org.jskat.ai.nn.input;

import static org.junit.Assert.assertEquals;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.Trick;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.BeforeClass;
import org.junit.Test;

public class PlayedCardsForPlayerAndNextCardInputStrategyTest {

	static PlayerKnowledge knowledge;
	static InputStrategy strategy;

	@BeforeClass
	public static void setUp() {
		knowledge = new PlayerKnowledge();
		knowledge.resetCurrentGameData();
		strategy = new PlayedCardsForPlayerAndNextCardInputStrategy();
	}

	@Test
	public void playedCard() {
		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.CLUBS);
		knowledge.setGame(factory.getAnnouncement());
		knowledge.setPlayerPosition(Player.FOREHAND);
		knowledge.setCurrentTrick(new Trick(0, Player.FOREHAND));
		knowledge.setCardPlayed(Player.FOREHAND, Card.CJ);
		double[] inputs = strategy.getNetworkInput(knowledge, Card.DJ);
		assertEquals(32, inputs.length);
		assertEquals(1.0, inputs[3 * 8 + 4], 0.0);
		assertEquals(1.0, inputs[4], 0.0);
		knowledge.setCardPlayed(Player.MIDDLEHAND, Card.SJ);
		inputs = strategy.getNetworkInput(knowledge, Card.DJ);
		assertEquals(32, inputs.length);
		assertEquals(1.0, inputs[3 * 8 + 4], 0.0);
		assertEquals(0.0, inputs[2 * 8 + 4], 0.0);
		assertEquals(1.0, inputs[4], 0.0);
		knowledge.setCardPlayed(Player.REARHAND, Card.HJ);
		inputs = strategy.getNetworkInput(knowledge, Card.DJ);
		assertEquals(32, inputs.length);
		assertEquals(1.0, inputs[3 * 8 + 4], 0.0);
		assertEquals(0.0, inputs[2 * 8 + 4], 0.0);
		assertEquals(0.0, inputs[1 * 8 + 4], 0.0);
		assertEquals(1.0, inputs[4], 0.0);
	}
}
