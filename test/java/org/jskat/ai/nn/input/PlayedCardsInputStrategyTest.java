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

public class PlayedCardsInputStrategyTest {

	static PlayerKnowledge knowledge;
	static PlayedCardsInputStrategy strategy;

	@BeforeClass
	public static void setUp() {
		knowledge = new PlayerKnowledge();
		knowledge.resetCurrentGameData();
		strategy = new PlayedCardsInputStrategy();
	}

	@Test
	public void playedCard() {
		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(GameType.CLUBS);
		knowledge.setGame(factory.getAnnouncement());
		knowledge.setPlayerPosition(Player.FOREHAND);
		knowledge.setCurrentTrick(new Trick(0, Player.FOREHAND));
		knowledge.setCardPlayed(Player.FOREHAND, Card.CJ);
		double[] inputs = strategy.getNetworkInput(knowledge, null);
		assertEquals(32, inputs.length);
		assertEquals(1.0, inputs[3 * 8 + 4], 0.0);
		knowledge.setCardPlayed(Player.MIDDLEHAND, Card.SJ);
		inputs = strategy.getNetworkInput(knowledge, null);
		assertEquals(32, inputs.length);
		assertEquals(1.0, inputs[3 * 8 + 4], 0.0);
		assertEquals(1.0, inputs[2 * 8 + 4], 0.0);
		knowledge.setCardPlayed(Player.REARHAND, Card.HJ);
		inputs = strategy.getNetworkInput(knowledge, null);
		assertEquals(32, inputs.length);
		assertEquals(1.0, inputs[3 * 8 + 4], 0.0);
		assertEquals(1.0, inputs[2 * 8 + 4], 0.0);
		assertEquals(1.0, inputs[1 * 8 + 4], 0.0);
	}
}
