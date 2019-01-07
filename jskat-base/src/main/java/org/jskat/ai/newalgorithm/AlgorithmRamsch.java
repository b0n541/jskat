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

import org.jskat.data.JSkatOptions;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Loreck
 *
 */
public class AlgorithmRamsch extends AbstractAlgorithmAI {

	private static final Logger log = LoggerFactory.getLogger(AlgorithmRamsch.class);

	/**
	 *
	 */
	AlgorithmRamsch(final AlgorithmAI p, final GameType pGameType) {
		super(p, pGameType);

		log.debug(String.format("/s is %s", myPlayer.getPlayerName(), this
				.getClass().getName()));
	}

	@Override
	public Card startGame() {
		final CardList cards = knowledge.getOwnCards();
		if (cards.get(0).getRank() == Rank.JACK
				&& cards.get(0).getSuit().ordinal() > 2
				&& cards.get(1).getRank() != Rank.JACK) {
			return cards.get(0);
		}
		int resultIndex = -1;
		for (final Suit s : Suit.values()) {
			if (cards.getSuitCount(s, false) == 1) {
				if (resultIndex < 0
						|| cards.get(resultIndex).getRamschOrder() > cards.get(
								cards.getFirstIndexOfSuit(s)).getRamschOrder()) {
					resultIndex = cards.getFirstIndexOfSuit(s);
				}
			} else if (cards.getSuitCount(s, false) == 2
					&& cards.get(cards.getLastIndexOfSuit(s)).getRank() == Rank.SEVEN) {
				resultIndex = cards.getFirstIndexOfSuit(s);
			}
		}
		if (resultIndex >= 0) {
			return cards.get(resultIndex);
		}
		return cards.get(cards.size() - 1);
	}

	@Override
	public Card playForehandCard() {
		final CardList cards = knowledge.getOwnCards();
		final int[] playedCards = knowledge.getPlayedCardsBinary();
		int resultIndex = -1;
		// see if I want to get rid of any single cards
		for (final Suit s : Suit.values()) {
			if ((knowledge.getPlayedCardsBinary()[s.ordinal()] & 127) > 0) {
				continue;
			}
			final Card c = cards.get(cards.getFirstIndexOfSuit(s, false));
			if (cards.getSuitCount(s, false) == 1
					&& c.getRank().getRamschOrder() < 6
					&& c.getRank().getRamschOrder() > 2) {
				if (resultIndex < 0) {
					resultIndex = cards.getIndexOf(c);
				} else {
					if (c.getRamschOrder() > cards.get(resultIndex)
							.getRamschOrder()) {
						resultIndex = cards.getIndexOf(c);
					}
				}
			}
			final Card lowCard = cards.get(cards.getLastIndexOfSuit(s, false));
			if (cards.getSuitCount(s, false) == 2
					&& c.getRank().getRamschOrder() < 6
					&& lowCard.getRank().getRamschOrder() < 2) {
				resultIndex = cards.getIndexOf(c);
			}
		}
		if (resultIndex >= 0) {
			final Card result = cards.get(resultIndex);
			log.debug("Playing single (or high double) suit card: " + result
					+ " of " + cards);
			return result;
		}

		final int jack = Rank.JACK.toBinaryFlag();
		if ((playedCards[0] & jack) + (playedCards[1] & jack)
				+ (playedCards[2] & jack) + (playedCards[3] & jack) == 0) {
			log.debug("no jack played yet - trying it myself");
			if (cards.get(0).getRank() == Rank.JACK
					&& cards.get(0).getSuit().ordinal() > 1
					&& cards.get(1).getRank() != Rank.JACK) {
				return cards.get(0);
			}
		}

		// check best card, if there are no "easy" suits
		for (final Card c : cards) {
			if (c.getRamschOrder() == 1) {
				resultIndex = cards.getIndexOf(c);
			} else if (c.getRamschOrder() == 0 && resultIndex < 0) {
				resultIndex = cards.getIndexOf(c);
			}
		}
		if (resultIndex >= 0) {
			return cards.get(resultIndex);
		}
		return cards.get(cards.size() - 1);
	}

	@Override
	public Card playMiddlehandCard() {
		log.debug("I (" + myPlayer.getPlayerName()
				+ ") am in middlehand (OpponentPlayer)");
		final CardList cards = myPlayer.getPlayableCards(knowledge.getTrickCards());
		final Card initialCard = knowledge.getTrickCards().get(0);
		final GameType gameType = knowledge.getGameType();
		// Card result = null;
		// fallback: get last valid card
		return getDefaultCard(cards, initialCard, gameType);
	}

	@Override
	public Card playRearhandCard() {
		log.debug("I (" + myPlayer.getPlayerName()
				+ ") am in rearhand (OpponentPlayer)");
		final CardList cards = myPlayer.getPlayableCards(knowledge.getTrickCards());
		final Card initialCard = knowledge.getTrickCards().get(0);
		final Card middlehandCard = knowledge.getTrickCards().get(1);
		final GameType gameType = knowledge.getGameType();
		Card result = null;
		final CardList allowed = new CardList();
		for (final Card c : cards) {
			if (c.isAllowed(gameType, initialCard, cards)) {
				allowed.add(c);
			}
		}
		if (allowed.size() == 1) {
			return allowed.get(0);
		}
		// if possible, take the highest card
		result = allowed.get(0);
		for (final Card c : allowed) {
			final boolean beatsCheck = c.beats(gameType, initialCard)
					&& c.beats(gameType, middlehandCard);
			final boolean beatsResult = result.beats(gameType, initialCard)
					&& result.beats(gameType, middlehandCard);
			if (beatsResult && !beatsCheck) {
				result = c;
			}
		}

		// fallback: take the first valid card
		return result == null ? getDefaultCard(cards, initialCard, gameType)
				: result;
	}

	/**
	 * Gets a fallback card, if no other algorithm returned a card
	 *
	 * @param cards
	 * @param initialCard
	 * @param gameType
	 * @return a default card
	 */
	private Card getDefaultCard(final CardList cards, final Card initialCard,
			final GameType gameType) {
		Card result = null;
		for (final Card c : cards) {
			if (c.isAllowed(gameType, initialCard, cards)) {
				result = c;
			}
		}
		if (result != null) {
			log.debug("playCard (8)");
			return result;
		}
		log.warn("no possible card found in card list [" + cards + "] with "
				+ gameType + " / " + initialCard);
		log.debug("playCard (9)");
		return cards.get(0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.algorithmic.IAlgorithmicAIPlayer#discardSkat(org.jskat.ai
	 * .algorithmic.BidEvaluator)
	 */
	@Override
	public CardList discardSkat(final BidEvaluator bidEvaluator) {
		log.debug(myPlayer.getPlayerName() + " (" + this.getClass()
				+ ") is discarding cards");
		if (JSkatOptions.instance().isSchieberamschJacksInSkat()) {
			return discardWithJacks();
		}
		return discardNoJacks();
	}

	private CardList discardWithJacks() {
		final CardList result = new CardList();
		final CardList cards = new CardList(knowledge.getOwnCards());
		log.debug("cards left before discarding(withJacks): " + cards.size()
				+ " - " + cards);
		cards.sort(GameType.RAMSCH);
		if (cards.get(0).getRank() == Rank.JACK
				&& cards.get(0).getSuit() == Suit.CLUBS
				|| cards.get(0).getSuit() == Suit.SPADES) {
			result.add(cards.remove(0));
		}
		if (cards.get(0).getRank() == Rank.JACK
				&& cards.get(0).getSuit() == Suit.SPADES) {
			result.add(cards.remove(0));
		}
		if (result.size() == 2) {
			return result;
		}
		result.addAll(discardNoJacks());
		while (result.size() > 2) {
			cards.add(result.remove(result.size() - 1));
		}
		log.debug("cards left after discarding(withJacks): " + cards.size()
				+ " - " + cards);
		return result;
	}

	private CardList discardNoJacks() {
		final CardList result = new CardList();
		final CardList cards = new CardList(knowledge.getOwnCards());
		cards.sort(GameType.RAMSCH);
		log.debug("cards left before discarding(noJacks): " + cards.size()
				+ " - " + cards);
		for (final Card c : cards) {
			if (result.size() < 2 && c.getRank() == Rank.ACE) {
				result.add(c);
			} else if (result.size() == 2 && c.getRank() == Rank.ACE) {
				final int len = knowledge.getOwnCards().getSuitCount(c.getSuit(),
						false);
				final int len0 = knowledge.getOwnCards().getSuitCount(
						result.get(0).getSuit(), false);
				final int len1 = knowledge.getOwnCards().getSuitCount(
						result.get(1).getSuit(), false);
				if (len < len0) {
					result.remove(0);
					result.add(c);
				} else if (len < len1) {
					result.remove(1);
					result.add(c);
				}
			}
		}
		for (final Card c : cards) {
			if (result.size() < 2 && c.getRank() == Rank.TEN) {
				result.add(c);
			} else if (result.size() == 2 && c.getRank() == Rank.TEN) {
				final int len = knowledge.getOwnCards().getSuitCount(c.getSuit(),
						false);
				final int len0 = knowledge.getOwnCards().getSuitCount(
						result.get(0).getSuit(), false);
				final int len1 = knowledge.getOwnCards().getSuitCount(
						result.get(1).getSuit(), false);
				if (len < len0) {
					result.remove(0);
					result.add(c);
				} else if (len < len1) {
					result.remove(1);
					result.add(c);
				}
			}
		}
		for (final Card c : cards) {
			if (result.size() < 2 && c.getRank() == Rank.KING) {
				result.add(c);
			}
		}
		for (final Card c : cards) {
			if (result.size() < 2 && c.getRank() != Rank.JACK) {
				result.add(c);
			}
		}
		log.debug("cards left after discarding(noJacks): " + cards.size()
				+ " - " + cards);
		return result;

	}

}
