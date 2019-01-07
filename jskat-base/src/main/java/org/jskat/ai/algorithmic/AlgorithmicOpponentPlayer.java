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

import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus J. Luzius <br>
 *         created: 15.06.2011 19:13:50
 *
 */
public class AlgorithmicOpponentPlayer implements IAlgorithmicAIPlayer {

	private static final Logger log = LoggerFactory.getLogger(AlgorithmicOpponentPlayer.class);

	private final AlgorithmicAIPlayer myPlayer;
	private final ImmutablePlayerKnowledge knowledge;

	/**
	 *
	 */
	AlgorithmicOpponentPlayer(final AlgorithmicAIPlayer p) {
		myPlayer = p;
		knowledge = p.getKnowledge();
		log.debug("Defining player <" + myPlayer.getPlayerName() + "> as "
				+ this.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jskat.ai.IJSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {
		if (knowledge.getOwnCards().size() == 1) {
			return knowledge.getOwnCards().get(0);
		}
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
		final CardList cards = knowledge.getOwnCards();
		if (knowledge.getDeclarer() == Player.MIDDLEHAND) {
			// "kurzer Weg, lange Farbe"
			final Suit longSuit = cards.getMostFrequentSuit(knowledge.getTrumpSuit());
			if (longSuit != null
					&& cards.get(cards.getFirstIndexOfSuit(longSuit)).getRank() == Rank.ACE) {
				log.debug("playCard (1)");
				return cards.get(knowledge.getOwnCards().getFirstIndexOfSuit(
						longSuit));
			}
			log.debug("playCard (2)");
			return cards.get(knowledge.getOwnCards().getLastIndexOfSuit(
					cards.getMostFrequentSuit()));
		} else if (knowledge.getDeclarer() == Player.REARHAND) {
			// "langer Weg, kurze Farbe"
			int minCount = 9;
			Card result = null;
			for (final Card c : cards) {
				if (result == null || result.isTrump(knowledge.getGameType())) {
					result = c;
					continue;
				}
				if (cards.getSuitCount(c.getSuit(), false) < minCount
						&& !(cards.getSuitCount(c.getSuit(), false) == 1 && c
								.getRank() == Rank.TEN)) {
					result = c;
					minCount = cards.getSuitCount(c.getSuit(), false);
					continue;
				}
				if (cards.getSuitCount(c.getSuit(), false) == minCount
						&& c.getRank() == Rank.ACE) {
					result = c;
					continue;
				}
				if (c.getSuit() == result.getSuit()
						&& cards.getSuitCount(c.getSuit(), false) == minCount) {
					result = c;
					continue;
				}
			}
			return result;

			// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
			// Suit shortSuit = null;
			// for(Suit suit: Suit.values()) {
			// if(suit==knowledge.getGameType().asSuit()) continue;
			// int cardCount = cards.getSuitCount(suit, false);
			// if(cardCount>0 && cardCount<minCount) {
			// shortSuit = suit;
			// minCount = cardCount;
			// }
			// }
			// if(shortSuit==null) {
			// log.warn("no short suit found: "+cards);
			// log.debug("playCard (3)");
			// return cards.get(cards.size()-1);
			// }
			// if(cards.get(cards.getFirstIndexOfSuit(shortSuit)).getRank()==Rank.ACE)
			// {
			// log.debug("playCard (4)");
			// return cards.get(cards.getFirstIndexOfSuit(shortSuit));
			// }
			// log.debug("playCard (5)");
			// return cards.get(cards.getLastIndexOfSuit(shortSuit));
		} else {
			log.warn(".openGame(): wrong declarer position: "
					+ knowledge.getDeclarer());
			log.debug("playCard (6)");
			return cards.get(cards.size() - 1);
		}
	}

	private Card openTrick() {
		final CardList cards = knowledge.getOwnCards();
		for (final Suit s : Suit.values()) {
			if (!knowledge.couldHaveSuit(knowledge.getDeclarer(), s)
					&& cards.hasSuit(knowledge.getGameType(), s)) {
				log.debug("playCard (7)");
				return cards.get(cards.getLastIndexOfSuit(s));
			}
		}
		return openGame();
	}

	private Card playMiddlehandCard() {
		log.debug("I (" + myPlayer.getPlayerName()
				+ ") am in middlehand (OpponentPlayer)");
		// fallback: take the first valid card
		final CardList cards = knowledge.getOwnCards();
		final Card initialCard = knowledge.getTrickCards().get(0);
		final GameType gameType = knowledge.getGameType();
		Card result = null;
		if (knowledge.getDeclarer() == knowledge.getCurrentTrick()
				.getForeHand()) {
			log.debug("Single player has already played a card");
			for (final Card c : cards) {
				if (c.beats(gameType, initialCard)
						&& c.isAllowed(gameType, initialCard, cards)) {
					if (result == null) {
						result = c;
						continue;
					}
					final boolean isTrump = initialCard.isTrump(gameType);
					if (!isTrump && c.getRank() != Rank.ACE
							&& c.getPoints() >= result.getPoints()) {
						result = c;
						continue;
					}
					if (c.getRank() == Rank.ACE
							&& knowledge.getPotentialSuitCount(knowledge
									.getCurrentTrick().getForeHand(),
									initialCard.getSuit(), isTrump, false) < 2) {
						result = c;
						continue;
					}
				}
			}
			if (result != null) {
				// I can beat the single player's card - so I take it
				log.debug("playCard (12) - I try to take it");
				return result;
			}
			// I cannot beat the single player's card
			if (initialCard.isTrump(gameType)) {
				if (knowledge.couldHaveTrump(knowledge.getCurrentTrick()
						.getRearHand())) {
					int cnt = 0;
					for (final Card c : Card.getBeatingCards(gameType, initialCard)) {
						if (knowledge.couldHaveCard(knowledge.getCurrentTrick()
								.getRearHand(), c)) {
							cnt++;
						}
					}
					if (cnt > 0) {
						log.debug("Looking for a high value card - rearhand might have "
								+ cnt + " beating card(s)");
						for (final Card c : cards) {
							if (c.isAllowed(gameType, initialCard, cards)) {
								if (result == null
										|| c.getPoints() > result.getPoints()) {
									if (c.getRank() == Rank.ACE
											&& !c.isTrump(gameType)
											&& knowledge.couldHaveSuit(
													knowledge.getDeclarer(),
													c.getSuit())
											&& !knowledge
													.getOwnCards()
													.contains(
															Card.getCard(
																	c.getSuit(),
																	Rank.TEN))) {
										log.debug("Keeping my ace of "
												+ c.getSuit());
									} else {
										result = c;
									}
								}
							}
						}
						if (result != null) {
							log.debug("playCard (14), cnt=" + cnt);
							return result;
						}
					}
				}
				result = cards.get(cards.getLastIndexOfSuit(
						knowledge.getTrumpSuit(), true));
				if (result == null) {
					return getDefaultCard(cards, initialCard, gameType);
				}
				log.debug("playCard (15)");
				return result;
			} else if (knowledge.couldHaveSuit(knowledge.getCurrentTrick()
					.getRearHand(), initialCard.getSuit())) {
				// I cannot beat the single player's card, which is not a trump
				// rear hand could still have the same suit
				int cntSuit = 0;
				int cntTrump = 0;
				for (final Card c : Card.getBeatingCards(gameType, initialCard)) {
					if (knowledge.couldHaveCard(knowledge.getCurrentTrick()
							.getRearHand(), c)) {
						if (c.isTrump(gameType)) {
							cntTrump++;
						} else {
							cntSuit++;
						}
					}
				}
				if (cntSuit > 0
						|| (cntTrump > 1 && knowledge.couldHaveSuit(knowledge
								.getCurrentTrick().getRearHand(),
								initialCard
										.getSuit()))) {
					if (initialCard.getRank() == Rank.ACE
							|| (initialCard.getRank() == Rank.TEN && !knowledge
									.couldHaveCard(Player.REARHAND, Card
											.getCard(initialCard.getSuit(),
													Rank.ACE)))) {
						result = cards.get(cards.getLastIndexOfSuit(
								initialCard.getSuit(), false));
					} else {
						result = cards.get(cards.getFirstIndexOfSuit(
								initialCard.getSuit(), false));
					}

					log.debug("playCard (13pre1), cnt=" + cntSuit + " / "
							+ cntTrump);
				} else {
					result = cards.get(cards.getLastIndexOfSuit(
							initialCard.getSuit(), false));
					log.debug("playCard (13pre2), cnt=" + cntSuit + " / "
							+ cntTrump);
				}
				if (result == null) {
					for (final Card c : cards) {
						if (c.isAllowed(gameType, initialCard, cards)) {
							result = c;
						}
					}
				}
				log.debug("playCard (13)");
				return result;
			}
			if (knowledge.couldHaveTrump(knowledge.getCurrentTrick()
					.getRearHand())) {
				result = cards.get(cards.getFirstIndexOfSuit(
						initialCard.getSuit(), false));
			}
		} else {
			log.debug("Single player is in rearhand");
			if (knowledge.couldHaveSuit(knowledge.getDeclarer(),
					initialCard.getSuit())) {
				if (initialCard.getRank() != Rank.JACK
						&& initialCard.getSuit() != knowledge.getTrumpSuit()
						&& cards.contains(Card.getCard(initialCard.getSuit(),
								Rank.ACE))) {
					log.debug("playCard (11)");
					return Card.getCard(initialCard.getSuit(), Rank.ACE);
				}
			} else {
				// if the single player doesn't have that suit: take the lowest
				// one
				result = cards.get(cards.getLastIndexOfSuit(initialCard
						.getSuit()));
				if (result != null) {
					log.debug("playCard (10)");
					return result;
				}
				if (knowledge.couldHaveTrump(knowledge.getDeclarer())) {
				}
			}
		}

		// fallback: get last valid card
		return getDefaultCard(cards, initialCard, gameType);
	}

	private Card playRearhandCard() {
		log.debug("I (" + myPlayer.getPlayerName()
				+ ") am in rearhand (OpponentPlayer)");
		// fallback: take the first valid card
		final CardList cards = knowledge.getOwnCards();
		final Card initialCard = knowledge.getTrickCards().get(0);
		final GameType gameType = knowledge.getGameType();
		Card result = null;

		if (initialCard.beats(gameType, knowledge.getTrickCards().get(1))) {
			// forehand win
			log.debug("forehand win - declarer=" + knowledge.getDeclarer());

			if (knowledge.getDeclarer() == knowledge.getCurrentTrick()
					.getForeHand()) {
				// it's a single player win so far
				log.debug("Single player is in forehand and has the trick so far");
				boolean myTrick = false;
				for (final Card c : cards) {
					if (!c.isAllowed(gameType, initialCard, cards)) {
						continue;
					}
					if (result == null) {
						result = c;
						continue;
					}
					final Trick tmpTrick = (Trick) knowledge.getCurrentTrick()
							.clone();
					tmpTrick.addCard(c);
					if (SkatRuleFactory.getSkatRules(gameType)
							.calculateTrickWinner(gameType, tmpTrick) != knowledge
									.getDeclarer()) {
						if (!myTrick) {
							log.debug("I can take the trick with " + c);
							result = c;
							myTrick = true;
						} else if (c.getPoints() >= result.getPoints()) {
							result = c;
						}
						continue;
					} else if (!myTrick && c.getPoints() <= result.getPoints()) {
						result = c;
					}
				}

				if (result != null) {
					return result;
				}
				for (final Card c : cards) {
					if (c.isAllowed(gameType, initialCard, cards)
							&& (result == null || c.getPoints() <= result
									.getPoints())) {
						result = c;
					}
				}
				log.debug("playRearhandCard() (2)");
				return result;
			} else {
				// it's ours already
				log.debug("it is ours (declarer in forehand)");
				if (!initialCard.isTrump(gameType)) {
					result = cards.get(cards.getFirstIndexOfSuit(
							initialCard.getSuit(), false));
				} else {
					for (final Card c : cards) {
						if (c.isAllowed(gameType, initialCard, cards)
								&& (result == null || c.getPoints() > result
										.getPoints())) {
							result = c;
						}
					}
				}
				if (result != null) {
					log.debug("playRearhandCard() (3)");
					return result;
				}
				for (final Card c : cards) {
					if (c.isAllowed(gameType, initialCard, cards)
							&& (result == null || (c.getPoints() > result
									.getPoints() && c.getRank() != Rank.ACE))) {
						result = c;
					}
				}
				log.debug("playRearhandCard() (4)");
				return result;
			}
		} else {
			// middlehand win
			if (knowledge.getDeclarer() != knowledge.getCurrentTrick()
					.getForeHand()) {
				log.debug("Single player is in middlehand and has the trick so far");
				// it's a single player win so far
				boolean myTrick = false;
				for (final Card c : cards) {
					if (!c.isAllowed(gameType, initialCard, cards)) {
						continue;
					}
					if (result == null) {
						result = c;
						continue;
					}
					final Trick tmpTrick = (Trick) knowledge.getCurrentTrick()
							.clone();
					tmpTrick.addCard(c);
					if (SkatRuleFactory.getSkatRules(gameType)
							.calculateTrickWinner(gameType, tmpTrick) != knowledge
									.getDeclarer()) {
						if (!myTrick) {
							log.debug("I can take the trick with " + c);
							result = c;
							myTrick = true;
						} else if (c.getPoints() >= result.getPoints()) {
							result = c;
						}
						continue;
					} else if (!myTrick && c.getPoints() <= result.getPoints()) {
						result = c;
					}
				}

			} else {
				// it's ours already
				log.debug("it is ours (declarer in middlehand)");
				if (!initialCard.isTrump(gameType)) {
					result = cards.get(cards.getFirstIndexOfSuit(
							initialCard.getSuit(), false));
					if (result == null) {
						// "schmieren"
						for (final Card c : cards) {
							if (result == null) {
								result = c;
							} else if (result.isTrump(gameType)
									&& (!c.isTrump(gameType) || c.getPoints() >= result
											.getPoints())) {
								result = c;
							} else if (c.getRank() == Rank.ACE
									&& !knowledge.couldHaveSuit(
											knowledge.getDeclarer(),
											c.getSuit())) {
								result = c;
							} else if (c.getPoints() > result.getPoints()) {
								result = c;
							}
						}
						if (result != null) {
							log.debug("playRearhandCard() (9)");
							return result;
						}
					}
				} else {
					for (final Card c : cards) {
						if (c.isAllowed(gameType, initialCard, cards)
								&& (result == null
										|| c.getPoints() > result.getPoints() || result
												.getRank() == Rank.JACK)) {
							result = c;
						}
					}
				}
				if (result != null) {
					log.debug("playRearhandCard() (7)");
					return result;
				}
				for (final Card c : cards) {
					if (c.isAllowed(gameType, initialCard, cards)
							&& (result == null || (c.getPoints() > result
									.getPoints() && c.getRank() != Rank.ACE))) {
						result = c;
					}
				}
				log.debug("playRearhandCard() (8)");
				return result;
			}
		}

		return getDefaultCard(cards, initialCard, gameType);
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
		throw new IllegalStateException("opponent player cannot discard a skat");
	}
}
