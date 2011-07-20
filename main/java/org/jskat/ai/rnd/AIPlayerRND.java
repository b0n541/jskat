/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20 21:16:11
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
package org.jskat.ai.rnd;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.AbstractJSkatPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;

/**
 * Random player for testing purposes
 */
public class AIPlayerRND extends AbstractJSkatPlayer {

	private static Log log = LogFactory.getLog(AIPlayerRND.class);

	/**
	 * Random generator
	 */
	private Random rand = new Random();

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
	 * {@inheritDoc}
	 */
	@Override
	public boolean pickUpSkat() {

		return this.rand.nextBoolean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameAnnouncement announceGame() {

		log.debug("position: " + this.knowledge.getPlayerPosition()); //$NON-NLS-1$
		log.debug("bids: " + this.knowledge.getHighestBid(Player.FOREHAND) + //$NON-NLS-1$
				" " + this.knowledge.getHighestBid(Player.MIDDLEHAND) + //$NON-NLS-1$
				" " + this.knowledge.getHighestBid(Player.REARHAND)); //$NON-NLS-1$

		GameAnnouncement newGame = new GameAnnouncement();

		// select a random game type (without RAMSCH and PASSED_IN)
		newGame.setGameType(GameType.values()[this.rand.nextInt(GameType
				.values().length - 2)]);
		newGame.setOuvert(this.rand.nextBoolean());

		return newGame;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int bidMore(int nextBidValue) {

		int result = -1;

		if (this.rand.nextBoolean()) {

			result = nextBidValue;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean holdBid(int currBidValue) {

		return this.rand.nextBoolean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startGame() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Card playCard() {

		int index = -1;

		log.debug('\n' + this.knowledge.toString());

		// first find all possible cards
		CardList possibleCards = getPlayableCards(this.knowledge
				.getTrickCards());

		log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards); //$NON-NLS-1$//$NON-NLS-2$

		// then choose a random one
		index = this.rand.nextInt(possibleCards.size());

		log.debug("choosing card " + index); //$NON-NLS-1$
		log.debug("as player " + this.knowledge.getPlayerPosition() + ": " + possibleCards.get(index)); //$NON-NLS-1$//$NON-NLS-2$

		return possibleCards.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAIPlayer() {

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CardList discardSkat() {
		CardList cards = knowledge.getMyCards();
		CardList result = new CardList();

		log.debug("Player cards before discarding: " + cards); //$NON-NLS-1$

		// just discard two random cards
		result.add(cards.remove(this.rand.nextInt(cards.size())));
		result.add(cards.remove(this.rand.nextInt(cards.size())));

		log.debug("Player cards after discarding: " + cards); //$NON-NLS-1$

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preparateForNewGame() {
		// nothing to do for AIPlayerRND
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finalizeGame() {
		// nothing to do for AIPlayerRND
	}
}