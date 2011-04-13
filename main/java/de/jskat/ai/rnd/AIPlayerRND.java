/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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

/*

@ShortLicense@

Author: @JS@

Released: @ReleaseDate@

 */

package de.jskat.ai.rnd;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.AbstractJSkatPlayer;
import de.jskat.ai.IJSkatPlayer;
import de.jskat.data.GameAnnouncement;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;

/**
 * Random player for testing purposes
 */
public class AIPlayerRND extends AbstractJSkatPlayer {

	private static Log log = LogFactory.getLog(AIPlayerRND.class);

	/**
	 * Creates a new instance of AIPlayerRND
	 */
	public AIPlayerRND() {

		this("unknown"); //$NON-NLS-1$
	}

	/**
	 * Creates a new instance of AIPlayerRND
	 * 
	 * @param newPlayerName
	 *            Player's name
	 */
	public AIPlayerRND(String newPlayerName) {

		log.debug("Constructing new AIPlayerRND"); //$NON-NLS-1$
		setPlayerName(newPlayerName);
	}

	/**
	 * @see IJSkatPlayer#lookIntoSkat()
	 */
	public boolean lookIntoSkat() {

		return this.rand.nextBoolean();
	}

	/**
	 * @see IJSkatPlayer#announceGame()
	 */
	public GameAnnouncement announceGame() {

		log.debug("position: " + this.knowledge.getPlayerPosition()); //$NON-NLS-1$
		log.debug("bids: " + this.knowledge.getHighestBid(Player.FORE_HAND) + //$NON-NLS-1$
				" " + this.knowledge.getHighestBid(Player.MIDDLE_HAND) + //$NON-NLS-1$
				" " + this.knowledge.getHighestBid(Player.HIND_HAND)); //$NON-NLS-1$

		GameAnnouncement newGame = new GameAnnouncement();

		// select a random game type (without RAMSCH and PASSED_IN)
		newGame.setGameType(GameType.values()[this.rand.nextInt(GameType
				.values().length - 2)]);
		newGame.setOuvert(this.rand.nextBoolean());

		return newGame;
	}

	/**
	 * @see IJSkatPlayer#bidMore(int)
	 */
	public int bidMore(int nextBidValue) {

		int result = -1;

		if (this.rand.nextBoolean()) {

			result = nextBidValue;
		}

		return result;
	}

	/**
	 * @see IJSkatPlayer#holdBid(int)
	 */
	public boolean holdBid(int currBidValue) {

		return this.rand.nextBoolean();
	}

	/**
	 * @see de.jskat.ai.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		// CHECK Auto-generated method stub

	}

	/**
	 * @see IJSkatPlayer#playCard()
	 */
	public Card playCard() {

		int index = -1;

		log.debug('\n' + this.knowledge.toString());

		// first find all possible cards
		CardList possibleCards = getPlayableCards(this.knowledge
				.getTrickCards());

		log
				.debug("found " + possibleCards.size() + " possible cards: " + possibleCards); //$NON-NLS-1$//$NON-NLS-2$

		// then choose a random one
		index = this.rand.nextInt(possibleCards.size());

		log.debug("choosing card " + index); //$NON-NLS-1$
		log
				.debug("as player " + this.knowledge.getPlayerPosition() + ": " + possibleCards.get(index)); //$NON-NLS-1$//$NON-NLS-2$

		return possibleCards.get(index);
	}

	/**
	 * @see IJSkatPlayer#isAIPlayer()
	 */
	public boolean isAIPlayer() {

		return true;
	}

	/**
	 * @see de.jskat.ai.IJSkatPlayer#discardSkat()
	 */
	public CardList discardSkat() {

		CardList result = new CardList();

		log.debug("Player cards before discarding: " + this.cards); //$NON-NLS-1$

		// just discard two random cards
		result.add(this.cards.remove(this.rand.nextInt(this.cards.size())));
		result.add(this.cards.remove(this.rand.nextInt(this.cards.size())));

		log.debug("Player cards after discarding: " + this.cards); //$NON-NLS-1$

		return result;
	}

	/**
	 * @see de.jskat.ai.IJSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {
		// nothing to do for AIPlayerRND
	}

	/**
	 * @see de.jskat.ai.IJSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {
		// nothing to do for AIPlayerRND
	}

	/**
	 * Random generator
	 */
	private Random rand = new Random();

}