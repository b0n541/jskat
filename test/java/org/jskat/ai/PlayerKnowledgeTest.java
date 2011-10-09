/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.10.0-SNAPSHOT
 * Build date: 2011-10-09
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
package org.jskat.ai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.Player;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test cases for class PlayerKnowledge
 */
public class PlayerKnowledgeTest extends AbstractJSkatTest {

	private static Log log = LogFactory.getLog(PlayerKnowledgeTest.class);

	/**
	 * Test the player knowledge by simulating a complete game and check the knowledge
	 * at various points
	 */
	@Test
	public void testPlayerKnowledge() {

		log.debug("Testing player knowledge...");
		
		PlayerKnowledge knowledge = new PlayerKnowledge();
		
		assertEquals(true, knowledge.getMyCards().isEmpty());
		assertEquals(0, knowledge.getTrumpCount());
		
		// set up player cards
		knowledge.setPlayerPosition(Player.MIDDLEHAND);
		knowledge.addCard(Card.CA);
		knowledge.addCard(Card.CQ);
		knowledge.addCard(Card.C8);
		knowledge.addCard(Card.ST);
		knowledge.addCard(Card.SQ);
		knowledge.addCard(Card.DT);
		knowledge.addCard(Card.DK);
		knowledge.addCard(Card.D7);
		knowledge.addCard(Card.HJ);
		knowledge.addCard(Card.HA);
		
		// test knowledge initial state
		assertEquals(10, knowledge.getMyCards().size());
		assertEquals(true, knowledge.couldHaveCard(Player.MIDDLEHAND, Card.CQ));
		assertEquals(true, knowledge.hasCard(Player.MIDDLEHAND, Card.CQ));
		assertEquals(false, knowledge.couldHaveCard(Player.FOREHAND, Card.CQ));
		assertEquals(false, knowledge.couldHaveCard(Player.REARHAND, Card.CQ));
		assertEquals(true, knowledge.couldHaveCard(Player.FOREHAND, Card.CK));
		assertEquals(true, knowledge.couldHaveCard(Player.REARHAND, Card.CK));
		assertEquals(false, knowledge.hasCard(Player.REARHAND, Card.CQ));
		assertEquals(false, knowledge.hasCard(Player.REARHAND, Card.CK));
		
		
		
		log.debug("Testing player knowledge done");
		
	}

}
