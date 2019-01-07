/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.rnd;

import java.util.Random;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Random player for testing purposes and driving the other players nuts.
 */
public class AIPlayerRND extends AbstractAIPlayer {

	private static Logger log = LoggerFactory.getLogger(AIPlayerRND.class);

	/**
	 * Random generator for decision making.
	 */
	private final Random random = new Random();

	/**
	 * Creates a new instance of AIPlayerRND.
	 */
	public AIPlayerRND() {

		this("unknown"); //$NON-NLS-1$
	}

	/**
	 * Creates a new instance of AIPlayerRND.
	 *
	 * @param newPlayerName
	 *            Player's name
	 */
	public AIPlayerRND(final String newPlayerName) {

		log.debug("Constructing new AIPlayerRND"); //$NON-NLS-1$
		setPlayerName(newPlayerName);
	}

	@Override
	public Boolean pickUpSkat() {
		return random.nextBoolean();
	}

	@Override
	public Boolean playGrandHand() {
		return random.nextBoolean();
	}

	@Override
	public GameAnnouncement announceGame() {
		log.debug("position: " + knowledge.getPlayerPosition()); //$NON-NLS-1$
		log.debug("bids: " + knowledge.getHighestBid(Player.FOREHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.MIDDLEHAND) + //$NON-NLS-1$
				" " + knowledge.getHighestBid(Player.REARHAND)); //$NON-NLS-1$

		final GameAnnouncementFactory factory = GameAnnouncement.getFactory();

		// select a random game type (without RAMSCH and PASSED_IN)
		final GameType gameType = GameType.values()[random.nextInt(GameType
				.values().length - 2)];
		factory.setGameType(gameType);
		if (Boolean.valueOf(random.nextBoolean())) {
			factory.setOuvert(true);
			if (gameType != GameType.NULL) {
				factory.setHand(true);
				factory.setSchneider(true);
				factory.setSchwarz(true);
			}
		}

		return factory.getAnnouncement();
	}

	@Override
	public Integer bidMore(final int nextBidValue) {
		int result = -1;

		if (random.nextBoolean()) {

			result = nextBidValue;
		}

		return result;
	}

	@Override
	public Boolean holdBid(final int currBidValue) {
		return random.nextBoolean();
	}

	@Override
	public void startGame() {
		// do nothing
	}

	@Override
	public Card playCard() {

		int index = -1;

		log.debug('\n' + knowledge.toString());

		// first find all possible cards
		final CardList possibleCards = getPlayableCards(knowledge
				.getTrickCards());

		log.debug("found " + possibleCards.size() + " possible cards: " + possibleCards); //$NON-NLS-1$//$NON-NLS-2$

		// then choose a random one
		index = random.nextInt(possibleCards.size());

		log.debug("choosing card " + index); //$NON-NLS-1$
		log.debug("as player " + knowledge.getPlayerPosition() + ": " + possibleCards.get(index)); //$NON-NLS-1$//$NON-NLS-2$

		return possibleCards.get(index);
	}

	@Override
	public CardList getCardsToDiscard() {
		final CardList result = new CardList();

		CardList discardableCards = new CardList(knowledge.getOwnCards());

		// just discard two random cards
		result.add(discardableCards.remove(random.nextInt(discardableCards
				.size())));
		result.add(discardableCards.remove(random.nextInt(discardableCards
				.size())));

		return result;
	}

	@Override
	public void preparateForNewGame() {
		// nothing to do for AIPlayerRND
	}

	@Override
	public void finalizeGame() {
		// nothing to do for AIPlayerRND
	}

	@Override
	public Boolean callContra() {
		return random.nextBoolean();
	}

	@Override
	public Boolean callRe() {
		return random.nextBoolean();
	}
}