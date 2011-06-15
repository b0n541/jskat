/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
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

package org.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.ai.AbstractJSkatPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * A JSkat AI Player
 * 
 * @author Markus J. Luzius <markus@luzius.de>
 */
public class AIPlayerMJL extends AbstractJSkatPlayer {

	private Log log = LogFactory.getLog(AIPlayerMJL.class);
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
	public int bidMore(int nextBidValue) {
		if (maxBidValue < 0) {
			maxBidValue = new Bidding(cards).getMaxBid();
		}
		if (maxBidValue < nextBidValue) {
			aiPlayer = new OpponentPlayer(cards, playerName);
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
	public boolean holdBid(int currBidValue) {
		if (maxBidValue < 0) {
			maxBidValue = new Bidding(cards).getMaxBid();
		}
		boolean result = !(maxBidValue < 18) && maxBidValue >= currBidValue;
		if (!result) {
			aiPlayer = new OpponentPlayer(cards, playerName);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#lookIntoSkat()
	 */
	@Override
	public boolean pickUpSkat() {
		// TODO really look into skat?
//		aiPlayer = new SinglePlayer(cards, rules);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {
		GameAnnouncement game = new GameAnnouncement();
		game.setGameType(new Bidding(cards).getSuggestedGameType());
		return game;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#discardSkat()
	 */
	@Override
	public CardList discardSkat() {
		// TODO check which cards should best be discarded
		if(aiPlayer==null || aiPlayer instanceof OpponentPlayer) {
			cards.remove(skat.get(0));
			cards.remove(skat.get(1));
			log.debug("aiplayer ["+aiPlayer.getClass()+"] is not SinglePlayer, discarding original skat of [" + skat
					+ "], cards.size="+cards.size());
			return skat;
		}
		return ((SinglePlayer)aiPlayer).discardSkat(skat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		log.debug("Starting game for player ("+getPlayerName()+")");
		if (knowledge.getDeclarer() != knowledge.getPlayerPosition()) {
			log.debug("ok? AIPlayerMJL should be OpponentPlayer - actually is: "
					+ (aiPlayer == null ? "null" : aiPlayer.getClass()
							.getName()));
		} else {
			log.debug("ok? setting AIPlayerMJL to be SinglePlayer - actually is: "
					+ (aiPlayer == null ? "null" : aiPlayer.getClass()
							.getName()));
			aiPlayer = new SinglePlayer(cards, rules);
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
		if (toPlay != null)
			return toPlay;
		// if there is none, just play the first valid card
		log.debug("no card returned from AIPlayer - just taking the first valid card");
		CardList result = getPlayableCards(this.knowledge.getTrickCards());
		if (result.size() < 1) {
			log.warn("no playable cards - shouldn't be possible!");
			log.debug("my cards: " + cards + ", trick: "
					+ this.knowledge.getTrickCards());
			log.debug("--------------------- done (" + playerName
					+ ") -----------------------------------");
			return null;
		}
		log.debug("--------------------- done -----------------------------------");
		return result.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {
		// don't know what to do here (yet)...
		log.debug("finalizing game...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jskat.ai.JSkatPlayer#isAIPlayer()
	 */
	@Override
	public boolean isAIPlayer() {
		return true;
	}

}