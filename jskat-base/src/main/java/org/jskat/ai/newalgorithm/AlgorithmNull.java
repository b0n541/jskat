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
//package org.jskat.ai.newalgorithm;
//
//import org.apache.log4j.Logger;
//import org.jskat.player.PlayerKnowledge;
//import org.jskat.util.Card;
//import org.jskat.util.CardList;
//import org.jskat.util.GameType;
//import org.jskat.util.Rank;
//import org.jskat.util.Suit;
//
//public class AlgorithmNull extends AbstractAlgorithmAI {
//	private static final Logger log = Logger.getLogger(AlgorithmNull.class);
//
//	private final AlgorithmAI myPlayer;
//	private final PlayerKnowledge knowledge;
//
//	/**
//	 * 
//	 */
//	AlgorithmNull(final AlgorithmAI p, GameType pGameType) {
//		super(p, pGameType);
//		myPlayer = p;
//		knowledge = p.getKnowledge();
//		log.debug("Defining player <" + myPlayer.getPlayerName() + "> as " + this.getClass().getName());
//	}
//
//	@Override
//	public Card playCard() {
//		if (knowledge.getOwnCards().size() == 1) {
//			return knowledge.getOwnCards().get(0);
//		}
//		if (knowledge.getTrickCards() == null || knowledge.getTrickCards().isEmpty()) {
//			if (knowledge.getNoOfTricks() < 1) {
//				return openGame();
//			}
//			return openTrick();
//		}
//		if (knowledge.getTrickCards().size() == 1) {
//			return playMiddlehandCard();
//		}
//		return playRearhandCard();
//	}
//
//
//	private Card openGame() {
//		CardList cards = knowledge.getOwnCards();
//		if (cards.get(0).getRank() == Rank.JACK && cards.get(0).getSuit().ordinal() > 2
//				&& cards.get(1).getRank() != Rank.JACK) {
//			return cards.get(0);
//		}
//		int resultIndex = -1;
//		for (Suit s : Suit.values()) {
//			if (cards.getSuitCount(s, false) == 1) {
//				if (resultIndex < 0
//						|| cards.get(resultIndex).getRamschOrder() > cards.get(cards.getFirstIndexOfSuit(s))
//								.getRamschOrder()) {
//					resultIndex = cards.getFirstIndexOfSuit(s);
//				}
//			} else if (cards.getSuitCount(s, false) == 2
//					&& cards.get(cards.getLastIndexOfSuit(s)).getRank() == Rank.SEVEN) {
//				resultIndex = cards.getFirstIndexOfSuit(s);
//			}
//		}
//		if (resultIndex >= 0) {
//			return cards.get(resultIndex);
//		}
//		return cards.get(cards.size() - 1);
//	}
//
//	private Card openTrick() {
//		CardList cards = knowledge.getOwnCards();
//		int[] playedCards = knowledge.getPlayedCardsBinary();
//		int resultIndex = -1;
//		// see if I want to get rid of any single cards
//		for (Suit s : Suit.values()) {
//			if ((knowledge.getPlayedCardsBinary()[s.ordinal()] & 127) > 0) {
//				continue;
//			}
//			Card c = cards.get(cards.getFirstIndexOfSuit(s, false));
//			// 
//			if (cards.getSuitCount(s, false) == 1 && c.getRank().getNullOrder() < 6
//					&& c.getRank().getNullOrder() > 2) {
//				if (resultIndex < 0) {
//					resultIndex = cards.getIndexOf(c);
//				} else {
//					if (c.getNullOrder() > cards.get(resultIndex).getNullOrder()) {
//						resultIndex = cards.getIndexOf(c);
//					}
//				}
//			}
//			Card lowCard = cards.get(cards.getLastIndexOfSuit(s, false));
//			if (cards.getSuitCount(s, false) == 2 && c.getRank().getNullOrder() < 6
//					&& lowCard.getRank().getNullOrder() < 2) {
//				resultIndex = cards.getIndexOf(c);
//			}
//		}
//		if (resultIndex >= 0) {
//			Card result = cards.get(resultIndex);
//			log.debug("Playing single (or high double) suit card: " + result + " of " + cards);
//			return result;
//		}
//
//		int jack = Rank.JACK.toBinaryFlag();
//		if ((playedCards[0] & jack) + (playedCards[1] & jack) + (playedCards[2] & jack) + (playedCards[3] & jack) == 0) {
//			log.debug("no jack played yet - trying it myself");
//			if (cards.get(0).getRank() == Rank.JACK && cards.get(0).getSuit().ordinal() > 1
//					&& cards.get(1).getRank() != Rank.JACK) {
//				return cards.get(0);
//			}
//		}
//
//		// check best card, if there are no "easy" suits
//		for (Card c : cards) {
//			if (c.getNullOrder() == 1) {
//				resultIndex = cards.getIndexOf(c);
//			} else if (c.getNullOrder() == 0 && resultIndex < 0) {
//				resultIndex = cards.getIndexOf(c);
//			}
//		}
//		if (resultIndex >= 0) {
//			return cards.get(resultIndex);
//		}
//		return cards.get(cards.size() - 1);
//	}
//
//	private Card playMiddlehandCard() {
//		log.debug("I (" + myPlayer.getPlayerName() + ") am in middlehand (OpponentPlayer)");
//		CardList cards = knowledge.getOwnCards();
//		Card initialCard = knowledge.getTrickCards().get(0);
//		GameType gameType = knowledge.getGameType();
//		// Card result = null;
//		// fallback: get last valid card
//		return getDefaultCard(cards, initialCard, gameType);
//	}
//
//	private Card playRearhandCard() {
//		log.debug("I (" + myPlayer.getPlayerName() + ") am in rearhand (OpponentPlayer)");
//		CardList cards = knowledge.getOwnCards();
//		Card initialCard = knowledge.getTrickCards().get(0);
//		Card middlehandCard = knowledge.getTrickCards().get(1);
//		GameType gameType = knowledge.getGameType();
//		Card result = null;
//		CardList allowed = new CardList();
//		for (Card c : cards) {
//			if (c.isAllowed(gameType, initialCard, cards)) {
//				allowed.add(c);
//			}
//		}
//		if (allowed.size() == 1) {
//			return allowed.get(0);
//		}
//		// if possible, take the highest card
//		result = allowed.get(0);
//		for (Card c : allowed) {
//			boolean beatsCheck = c.beats(gameType, initialCard) && c.beats(gameType, middlehandCard);
//			boolean beatsResult = result.beats(gameType, initialCard) && result.beats(gameType, middlehandCard);
//			if (beatsResult && !beatsCheck) {
//				result = c;
//			}
//		}
//
//		// fallback: take the first valid card
//		return result == null ? getDefaultCard(cards, initialCard, gameType) : result;
//	}
//
//	@Override
//	public CardList discardSkat(BidEvaluator bidEvaluator) {
//		CardList result = new CardList();
//		CardList cards = new CardList(knowledge.getOwnCards());
//		cards.sort(GameType.NULL);
//		log.debug("cards left before discarding(noJacks): " + cards.size() + " - " + cards);
//		for (Card c : cards) {
//			if (result.size() < 2 && c.getRank() == Rank.ACE) {
//				result.add(c);
//			} else if (result.size() == 2 && c.getRank() == Rank.ACE) {
//				int len = knowledge.getOwnCards().getSuitCount(c.getSuit(), false);
//				int len0 = knowledge.getOwnCards().getSuitCount(result.get(0).getSuit(), false);
//				int len1 = knowledge.getOwnCards().getSuitCount(result.get(1).getSuit(), false);
//				if (len < len0) {
//					result.remove(0);
//					result.add(c);
//				} else if (len < len1) {
//					result.remove(1);
//					result.add(c);
//				}
//			}
//		}
//		for (Card c : cards) {
//			if (result.size() < 2 && c.getRank() == Rank.TEN) {
//				result.add(c);
//			} else if (result.size() == 2 && c.getRank() == Rank.TEN) {
//				int len = knowledge.getOwnCards().getSuitCount(c.getSuit(), false);
//				int len0 = knowledge.getOwnCards().getSuitCount(result.get(0).getSuit(), false);
//				int len1 = knowledge.getOwnCards().getSuitCount(result.get(1).getSuit(), false);
//				if (len < len0) {
//					result.remove(0);
//					result.add(c);
//				} else if (len < len1) {
//					result.remove(1);
//					result.add(c);
//				}
//			}
//		}
//		for (Card c : cards) {
//			if (result.size() < 2 && c.getRank() == Rank.KING) {
//				result.add(c);
//			}
//		}
//		for (Card c : cards) {
//			if (result.size() < 2 && c.getRank() != Rank.JACK) {
//				result.add(c);
//			}
//		}
//		log.debug("cards left after discarding(Null): " + cards.size() + " - " + cards);
//		return result;
//	}
//}