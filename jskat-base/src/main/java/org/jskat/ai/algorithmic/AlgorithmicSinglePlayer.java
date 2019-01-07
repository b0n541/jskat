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
package org.jskat.ai.algorithmic;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus J. Luzius <br>
 *         created: 15.06.2011 19:13:50
 *
 */
public class AlgorithmicSinglePlayer implements IAlgorithmicAIPlayer {

	private static final Logger log = LoggerFactory.getLogger(AlgorithmicSinglePlayer.class);

	private final AlgorithmicAIPlayer myPlayer;
	private final ImmutablePlayerKnowledge knowledge;

	/**
	 *
	 */
	AlgorithmicSinglePlayer(final AlgorithmicAIPlayer p) {
		myPlayer = p;
		knowledge = p.getKnowledge();
		log.debug("Defining player <" + myPlayer.getPlayerName() + "> as "
				+ this.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.algorithmic.IAlgorithmicAIPlayer#discardSkat(org.jskat.ai
	 * .algorithmic.BidEvaluator)
	 */
	@Override
	public CardList discardSkat(final BidEvaluator bid) {
		final CardList cards = new CardList(knowledge.getOwnCards());
		cards.sort(bid.getSuggestedGameType());
		final CardList toDiscard = new CardList();
		toDiscard.add(cards.remove(10));
		toDiscard.add(cards.remove(10));
		return toDiscard;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {
		if (knowledge.getTrickCards() == null
				|| knowledge.getTrickCards().isEmpty()) {
			if (knowledge.getNoOfTricks() < 1) {
				return openGame();
			}
			return openTrick();
		}
		if (knowledge.getTrickCards().size() == 1) {
			return playMiddlehandCard();
		}
		return playRearhandCard();
	}

	private Card openGame() {
		// at present, game is always opened with trump - using a jack, if
		// possible
		// TODO (markus 15.07.11) open single player game with none-trump
		final CardList cards = knowledge.getOwnCards();
		if (cards.get(0).getRank() != Rank.JACK) {
			Card c = cards.get(cards.getLastIndexOfSuit(
					knowledge.getTrumpSuit(), true));
			if (c == null) {
				// should not happen - if there is no jack, there should be at
				// least one trump suit card
				// just to make sure that a card is played
				c = cards.get(0);
			}
			return c;
		}
		// from here onwards: first card must be a jack
		if (cards.get(0).getSuit() == Suit.CLUBS) {
			if (cards.get(1).getSuit() != Suit.SPADES
					|| cards.get(1).getRank() != Rank.JACK) {
				return cards.get(0);
			}
			if (cards.get(2).getSuit() != Suit.HEARTS
					|| cards.get(2).getRank() != Rank.JACK) {
				return cards.get(1);
			}
			if (cards.get(3).getSuit() != Suit.DIAMONDS
					|| cards.get(3).getRank() != Rank.JACK) {
				return cards.get(2);
			}
			return cards.get(3);
		}
		if (cards.get(0).getSuit() == Suit.SPADES) {
			if (cards.get(1).getSuit() != Suit.HEARTS
					|| cards.get(1).getRank() != Rank.JACK) {
				return cards.get(0);
			}
			if (cards.get(2).getSuit() != Suit.HEARTS
					|| cards.get(2).getRank() != Rank.JACK) {
				return cards.get(0);
			}
		}
		return cards.get(0);
	}

	private Card openTrick() {
		final CardList cards = knowledge.getOwnCards();
		// 1: check, if there are still trump cards out
		if (knowledge.couldOpponentsHaveTrump()) {
			if (openGame().getRank() == Rank.JACK) {
				return openGame();
			}
			final Card c = cards.get(cards.getLastIndexOfSuit(
					knowledge.getTrumpSuit(), true));
			if (c != null) {
				return c;
			}
		}
		// if both opponent player don't have a suit, but single player has:
		// play it!
		suitLoop: for (final Suit s : Suit.values()) {
			if (!cards.hasSuit(knowledge.getGameType(), s)) {
				continue;
			}
			playerLoop: for (final Player p : Player.values()) {
				if (p == knowledge.getDeclarer()) {
					continue playerLoop;
				}
				if (knowledge.couldHaveSuit(p, s)) {
					continue suitLoop;
				}
			}
			return cards.get(cards.getLastIndexOfSuit(s));
		}
		// do i have any aces?
		for (final Card c : cards) {
			if (c.getRank() == Rank.ACE
					&& c.getSuit() != knowledge.getTrumpSuit()) {
				return c;
			}
			if (c.getRank() == Rank.TEN
					&& knowledge.isCardPlayed(Card.getCard(c.getSuit(),
							Rank.ACE))) {
				return c;
			}
			if (c.getRank() == Rank.KING
					&& knowledge.isCardPlayed(Card.getCard(c.getSuit(),
							Rank.ACE))
					&& knowledge.isCardPlayed(Card.getCard(c.getSuit(),
							Rank.TEN))) {
				return c;
			}
		}
		// do i have a ten and a lower card of the same suit?
		final int[] myCards = cards.toBinary();
		for (final Suit s : Suit.values()) {
			if (s == knowledge.getTrumpSuit()) {
				continue;
			}
			final int i = myCards[s.ordinal()];
			if ((i & 63) > 32) {
				return cards.get(cards.getLastIndexOfSuit(s));
			}
		}
		// fallback: just play the first card
		return cards.get(0);
	}

	private Card playMiddlehandCard() {
		log.debug("Single player is in middlehand");
		// TODO (mjl 23.08.2011) single player middlehand card
		// fallback: take the first valid card (which is a trump, if there still
		// is one)
		final CardList cards = knowledge.getOwnCards();
		for (final Card c : cards) {
			if (c.isAllowed(knowledge.getGameType(), knowledge.getTrickCards()
					.isEmpty() ? null : knowledge.getTrickCards().get(0), cards)) {
				return c;
			}
		}
		log.warn("no possible card found in card list [" + cards + "] with "
				+ knowledge.getGameType() + " / "
				+ knowledge.getTrickCards().get(0));
		return cards.get(0);
	}

	private Card playRearhandCard() {
		log.debug("Single player is in rearhand");
		// TODO (mjl 23.08.2011) single player rearhand card
		// fallback: take the first valid card (which is a trump, if there still
		// is one)
		final CardList cards = knowledge.getOwnCards();
		for (final Card c : cards) {
			if (c.isAllowed(knowledge.getGameType(), knowledge.getTrickCards()
					.isEmpty() ? null : knowledge.getTrickCards().get(0), cards)) {
				return c;
			}
		}
		log.warn("no possible card found in card list [" + cards + "] with "
				+ knowledge.getGameType() + " / "
				+ knowledge.getTrickCards().get(0));
		return cards.get(0);
	}

}
