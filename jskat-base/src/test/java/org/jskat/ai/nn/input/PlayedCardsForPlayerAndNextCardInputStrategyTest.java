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
