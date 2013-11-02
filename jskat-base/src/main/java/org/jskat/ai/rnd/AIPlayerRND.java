/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
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
package org.jskat.ai.rnd;

import java.util.Random;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.player.AbstractJSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Random player for testing purposes
 */
public class AIPlayerRND extends AbstractJSkatPlayer {

	private static Logger log = LoggerFactory.getLogger(AIPlayerRND.class);

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
	public boolean playGrandHand() {

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

		final GameAnnouncementFactory factory = GameAnnouncement.getFactory();

		// select a random game type (without RAMSCH and PASSED_IN)
		final GameType gameType = GameType.values()[rand.nextInt(GameType
				.values().length - 2)];
		factory.setGameType(gameType);
		if (Boolean.valueOf(rand.nextBoolean())) {
			factory.setOuvert(true);
			if (gameType != GameType.NULL) {
				factory.setHand(true);
				factory.setSchneider(true);
				factory.setSchwarz(true);
			}
		}

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
		final CardList possibleCards = getPlayableCards(knowledge
				.getTrickCards());

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
		final CardList result = new CardList();

		CardList discardableCards = new CardList(knowledge.getOwnCards());

		// just discard two random cards
		result.add(discardableCards.remove(rand.nextInt(discardableCards.size())));
		result.add(discardableCards.remove(rand.nextInt(discardableCards.size())));

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