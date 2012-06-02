/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.player.AbstractJSkatPlayer;
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
	private final Random rand = new Random();

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
	public AIPlayerRND(final String newPlayerName) {

		log.debug("Constructing new AIPlayerRND"); //$NON-NLS-1$
		setPlayerName(newPlayerName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean pickUpSkat() {

		return rand.nextBoolean();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameAnnouncement announceGame() {

		log.debug("position: " + knowledge.getPlayerPosition()); //$NON-NLS-1$
		log.debug("bids: " + knowledge.getHighestBid(Player.FOREHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.MIDDLEHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.REARHAND)); //$NON-NLS-1$

		GameAnnouncementFactory factory = GameAnnouncement.getFactory();

		// select a random game type (without RAMSCH and PASSED_IN)
		factory.setGameType(GameType.values()[rand.nextInt(GameType.values().length - 2)]);
		factory.setOuvert(Boolean.valueOf(rand.nextBoolean()));

		return factory.getAnnouncement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int bidMore(final int nextBidValue) {
		int result = -1;

		if (rand.nextBoolean()) {

			result = nextBidValue;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean holdBid(final int currBidValue) {
		return rand.nextBoolean();
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

		log.debug('\n' + knowledge.toString());

		// first find all possible cards
		CardList possibleCards = getPlayableCards(knowledge.getTrickCards());

		log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards); //$NON-NLS-1$//$NON-NLS-2$

		// then choose a random one
		index = rand.nextInt(possibleCards.size());

		log.debug("choosing card " + index); //$NON-NLS-1$
		log.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(index)); //$NON-NLS-1$//$NON-NLS-2$

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
	public CardList getCardsToDiscard() {
		CardList result = new CardList();

		// just discard two random cards
		result.add(knowledge.getOwnCards().get(rand.nextInt(knowledge.getOwnCards().size())));
		result.add(knowledge.getOwnCards().get(rand.nextInt(knowledge.getOwnCards().size())));

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