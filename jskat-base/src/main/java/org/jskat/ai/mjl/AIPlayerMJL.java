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
package org.jskat.ai.mjl;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JSkat AI Player
 * 
 * @author Markus J. Luzius (markus@luzius.de)
 */
public class AIPlayerMJL extends AbstractAIPlayer {

	private static Logger log = LoggerFactory.getLogger(AIPlayerMJL.class);
	private CardPlayer aiPlayer;
	int maxBidValue = -1;

	public AIPlayerMJL() {
		log.debug(".new()");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {
		// reset implementation of aiPlayer
		aiPlayer = null;
		maxBidValue = -1;
		// nothing else to do right now...
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#bidMore(int)
	 */
	@Override
	public Integer bidMore(final int nextBidValue) {
		if (maxBidValue < 0) {
			maxBidValue = new Bidding(knowledge.getOwnCards()).getMaxBid();
		}
		if (maxBidValue < nextBidValue) {
			aiPlayer = new OpponentPlayer(knowledge.getOwnCards(), playerName);
			return -1;
		}
		return nextBidValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#holdBid(int)
	 */
	@Override
	public Boolean holdBid(final int currBidValue) {
		if (maxBidValue < 0) {
			maxBidValue = new Bidding(knowledge.getOwnCards()).getMaxBid();
		}
		boolean result = !(maxBidValue < 18) && maxBidValue >= currBidValue;
		if (!result) {
			aiPlayer = new OpponentPlayer(knowledge.getOwnCards(), playerName);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#lookIntoSkat()
	 */
	@Override
	public Boolean pickUpSkat() {
		// TODO really look into skat?
		// aiPlayer = new SinglePlayer(cards, rules);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {
		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(new Bidding(knowledge.getOwnCards())
				.getSuggestedGameType());
		return factory.getAnnouncement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#discardSkat()
	 */
	@Override
	public CardList getCardsToDiscard() {
		// should be done: check which cards should best be discarded
		if (aiPlayer == null || aiPlayer instanceof OpponentPlayer) {
			knowledge.getOwnCards().remove(knowledge.getSkat().get(0));
			knowledge.getOwnCards().remove(knowledge.getSkat().get(1));
			log.debug("aiplayer is not SinglePlayer, discarding original skat of ["
					+ knowledge.getSkat()
					+ "], cards.size="
					+ knowledge.getOwnCards().size());
			return knowledge.getSkat();
		}

		return ((SinglePlayer) aiPlayer).discardSkat(knowledge.getSkat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		log.debug("Starting game for player (" + getPlayerName() + ")");
		if (knowledge.getDeclarer() != knowledge.getPlayerPosition()) {
			log.debug("ok? AIPlayerMJL should be OpponentPlayer - actually is: "
					+ (aiPlayer == null ? "null" : aiPlayer.getClass()
							.getName()));
		} else {
			log.debug("ok? setting AIPlayerMJL to be SinglePlayer - actually is: "
					+ (aiPlayer == null ? "null" : aiPlayer.getClass()
							.getName()));
			aiPlayer = new SinglePlayer(knowledge.getOwnCards(), rules);
		}
		aiPlayer.startGame(knowledge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {
		log.debug("--------------------- start (" + playerName
				+ ") ----------------------------------");
		log.debug(".playCard(): my position: " + knowledge.getPlayerPosition()
				+ ", single player: " + knowledge.getDeclarer());
		Card toPlay = aiPlayer.playNextCard(knowledge);
		// make sure, that there is a card
		if (toPlay != null) {
			return toPlay;
		}
		// if there is none, just play the first valid card
		log.debug("no card returned from AIPlayer - just taking the first valid card");
		CardList result = getPlayableCards(this.knowledge.getTrickCards());
		if (result.size() < 1) {
			log.warn("no playable cards - shouldn't be possible!");
			log.debug("my cards: " + knowledge.getOwnCards() + ", trick: "
					+ this.knowledge.getTrickCards());
			log.debug("--------------------- done (" + playerName
					+ ") -----------------------------------");
			return null;
		}
		log.debug("--------------------- done -----------------------------------");
		return result.get(0);
	}

	@Override
	public void finalizeGame() {
		// don't know what to do here (yet)...
		log.debug("finalizing game...");
	}

	@Override
	public Boolean callContra() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean callRe() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean playGrandHand() {
		// TODO Auto-generated method stub
		return false;
	}
}