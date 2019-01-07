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
package org.jskat.ai.newalgorithm;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Loreck
 *
 */
public class AlgorithmAI extends AbstractAIPlayer {

	private static final Logger log = LoggerFactory.getLogger(AlgorithmAI.class);

	private AbstractAlgorithmAI aiPlayer = null;
	BidEvaluator bidEvaluator = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {
		log.debug("New game preparation for player <" + playerName + ">");
		aiPlayer = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {
		bidEvaluator = null; // not necessry any more
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#bidMore(int)
	 */
	@Override
	public Integer bidMore(final int nextBidValue) {
		if (bidEvaluator == null) {
			bidEvaluator = new BidEvaluator(knowledge.getOwnCards(),
					knowledge.getPlayerPosition());
		}
		if (bidEvaluator.getMaxBid() >= nextBidValue) {
			return nextBidValue;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#holdBid(int)
	 */
	@Override
	public Boolean holdBid(final int currBidValue) {
		if (bidEvaluator == null) {
			bidEvaluator = new BidEvaluator(knowledge.getOwnCards(),
					knowledge.getPlayerPosition());
		}
		return bidEvaluator.getMaxBid() >= currBidValue;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#pickUpSkat()
	 */
	@Override
	public Boolean pickUpSkat() {
		if (bidEvaluator == null) {
			bidEvaluator = new BidEvaluator(knowledge.getOwnCards(),
					knowledge.getPlayerPosition());
		}
		return !bidEvaluator.canPlayHandGame();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#discardSkat()
	 */
	@Override
	public CardList getCardsToDiscard() {
		// Wenn Ramschspiel
		if (knowledge.getGameType() == GameType.RAMSCH) {
			aiPlayer = new AlgorithmRamsch(this, GameType.RAMSCH);
		} else {
			// Ermitteln was gespielt werden soll
			// normalerweise muss der BidEvaluator gesetzt sein, sonst kann er
			// eigentlich garnicht hier reinlaufen
			if (bidEvaluator == null) {
				bidEvaluator = new BidEvaluator(knowledge.getOwnCards(),
						knowledge.getPlayerPosition());
			}
			bidEvaluator.eval(knowledge.getOwnCards());

			// Wenn Null-Spiel
			if (bidEvaluator.getSuggestedGameType() == GameType.NULL) {
				// aiPlayer = new AlgorithmNull(this,
				// bidEvaluator.getSuggestedGameType());
				log.debug(playerName
						+ " ist AlgorithmNull-Spieler / getCardsToDiscard");
			}
			// Wenn Grand
			else if (bidEvaluator.getSuggestedGameType() == GameType.GRAND) {
				aiPlayer = new AlgorithmGrand(this,
						bidEvaluator.getSuggestedGameType());
				log.debug(playerName
						+ " ist AlgorithmGrand-Spieler / getCardsToDiscard");
			}
			// Wenn Farb-Spiel
			else if (bidEvaluator.getSuggestedGameType() == GameType.CLUBS
					|| bidEvaluator.getSuggestedGameType() == GameType.SPADES
					|| bidEvaluator.getSuggestedGameType() == GameType.HEARTS
					|| bidEvaluator.getSuggestedGameType() == GameType.DIAMONDS) {
				aiPlayer = new AlgorithmSuit(this,
						bidEvaluator.getSuggestedGameType());
				log.debug(playerName
						+ " ist AlgorithmSuit-Spieler / getCardsToDiscard");
			}
		}

		return aiPlayer.discardSkat(bidEvaluator);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {
		if (bidEvaluator == null) {
			bidEvaluator = new BidEvaluator(knowledge.getOwnCards(),
					knowledge.getPlayerPosition());
		} else {
			bidEvaluator.eval(knowledge.getOwnCards());
		}

		// Wenn Null-Spiel
		if (bidEvaluator.getSuggestedGameType() == GameType.NULL) {
			// aiPlayer = new AlgorithmNull(this,
			// bidEvaluator.getSuggestedGameType());
			log.debug(playerName
					+ " ist AlgorithmNull-Spieler / announceGame");
		}
		// Wenn Grand
		else if (bidEvaluator.getSuggestedGameType() == GameType.GRAND) {
			aiPlayer = new AlgorithmGrand(this,
					bidEvaluator.getSuggestedGameType());
			log.debug(playerName
					+ " ist AlgorithmGrand-Spieler / announceGame");
		}
		// Wenn Farb-Spiel
		else if (bidEvaluator.getSuggestedGameType() == GameType.CLUBS
				|| bidEvaluator.getSuggestedGameType() == GameType.SPADES
				|| bidEvaluator.getSuggestedGameType() == GameType.HEARTS
				|| bidEvaluator.getSuggestedGameType() == GameType.DIAMONDS) {
			aiPlayer = new AlgorithmSuit(this,
					bidEvaluator.getSuggestedGameType());
			log.debug(playerName
					+ " ist AlgorithmSuit-Spieler / announceGame");
		}

		final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(bidEvaluator.getSuggestedGameType());
		return factory.getAnnouncement();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {
		// Wenn Ramsch-Spiel abgeloest wurde
		// if(aiPlayer instanceof AlgorithmRamsch
		// && knowledge.getGameType() != GameType.RAMSCH) {
		// aiPlayer = new AlgorithmOpponentGrand(this, GameType.GRAND);
		// log.debug(this.playerName+
		// " ist AlgorithmOpponentGrand-Spieler / playCard");
		// }

		final Card c = aiPlayer.playCard();
		if (c != null) {
			return c;
		}

		log.warn("no card returned from AI player!");
		if (knowledge.getTrickCards().size() < 1) {
			return knowledge.getOwnCards().get(0);
		}
		for (final Card c2 : knowledge.getOwnCards()) {
			if (c2.isAllowed(knowledge.getGameType(), knowledge.getTrickCards()
					.get(0), knowledge.getOwnCards())) {
				return c2;
			}
		}
		log.warn("no valid card found!");
		return knowledge.getOwnCards().get(0);
	}

	@Override
	public void startGame() {
		if (aiPlayer == null) {
			// Wenn RAMSCH-Spiel
			if (knowledge.getGameType() == GameType.RAMSCH) {
				aiPlayer = new AlgorithmRamsch(this, knowledge.getGameType());
				log.debug(playerName
						+ " ist AlgorithmRamsch-Spieler / startGame");
			}
			// Wenn Null-Spiel
			if (knowledge.getGameType() == GameType.NULL) {
				aiPlayer = new AlgorithmOpponentNull(this,
						knowledge.getGameType());
				log.debug(playerName
						+ " ist AlgorithmOpponentNull-Spieler / startGame");
			}
			// Wenn Grand
			else if (knowledge.getGameType() == GameType.GRAND) {
				aiPlayer = new AlgorithmOpponentGrand(this,
						knowledge.getGameType());
				log.debug(playerName
						+ " ist AlgorithmOpponentGrand-Spieler / startGame");
			}
			// Wenn Farb-Spiel
			else if (knowledge.getGameType() == GameType.CLUBS
					|| knowledge.getGameType() == GameType.SPADES
					|| knowledge.getGameType() == GameType.HEARTS
					|| knowledge.getGameType() == GameType.DIAMONDS) {
				aiPlayer = new AlgorithmOpponentSuit(this,
						knowledge.getGameType());
				log.debug(playerName
						+ " ist AlgorithmOpponentSuit-Spieler / startGame");
			}
		}
	}

	protected ImmutablePlayerKnowledge getKnowledge() {
		return knowledge;
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
