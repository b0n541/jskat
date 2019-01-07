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

import java.util.Random;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.jskat.util.rule.NullRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus J. Luzius (markus@luzius.de)
 * 
 */
public class OpponentPlayer extends AbstractCardPlayer {

	/**
	 * log
	 */
	private static Logger log = LoggerFactory.getLogger(OpponentPlayer.class);

	private final String name;

	private final Random rand = new Random();

	/**
	 * 
	 */
	OpponentPlayer(final CardList cards, final String name) {
		super(cards);
		this.name = name;
		log.debug("Constructing a new opponent player called <" + name + ">...");
	}

	/**
	 * Gets the next card that the player wants to play
	 * 
	 * @param knowledge
	 *            all necessary information about the game
	 * @return index of the card to play
	 * @see org.jskat.ai.mjl.CardPlayer#playNextCard(ImmutablePlayerKnowledge)
	 */
	@Override
	public Card playNextCard(final ImmutablePlayerKnowledge knowledge) {
		log.debug("Play next card with trick size "
				+ knowledge.getTrickCards().size());
		if (knowledge.getGameType() == GameType.NULL) {
			return playNextCardNullGame(knowledge);
		}

		int bestToBePlayed = -1;
		log.debug(".playNextCard(): Processing hand [" + cards
				+ "] with trick [" + knowledge.getTrickCards()
				+ "]. Game type is " + knowledge.getGameType() + ".");

		if (knowledge.getTrickCards().size() > 1) {
			bestToBePlayed = findRearhandCard(knowledge);
		} else if (knowledge.getTrickCards().size() > 0) {
			bestToBePlayed = findMiddlehandCard(knowledge);
		} else {
			// No card on the table yet
			if (knowledge.getNoOfTricks() < 1) {
				bestToBePlayed = findFirstInitial(knowledge);
			} else {
				bestToBePlayed = findInitial(knowledge);
			}
			log.debug(".playNextCard(): (in forehand) " + name + ": "
					+ cards.get(bestToBePlayed));
		}

		if (bestToBePlayed < 0 || bestToBePlayed > cards.size() - 1) {
			log.debug("----- Error in finding a good opponent card: "
					+ bestToBePlayed + " -----");
			bestToBePlayed = 0;
		}

		log.debug("Playing " + cards.get(bestToBePlayed));

		if (bestToBePlayed < 0) {
			log.warn("Can't find a suitable card!");
			return null;
		}
		return cards.remove(bestToBePlayed);
	}

	/**
	 * @param knowledge
	 * @return the index of the card to play by a middlehand opponent player
	 */
	private int findMiddlehandCard(final ImmutablePlayerKnowledge knowledge) {
		GameType gameType = knowledge.getGameType();
		Card initialCard = (knowledge.getTrickCards().size() > 0 ? knowledge
				.getTrickCards().get(0) : null);
		Suit trumpSuit = gameType.getTrumpSuit();

		int bestToBePlayed;
		// Only one card is played in this trick yet
		// the player is forced to play after the color
		// of the first card

		// At first: Is trump played?
		if (initialCard.getSuit() == trumpSuit
				|| initialCard.getRank() == Rank.JACK) {

			// Trump is played
			log.debug(".playNextCard(): first card is trump...");
			// Does the player have trump?
			if (Helper.hasTrump(cards, trumpSuit)) {

				// Play the highest trump
				// Check whether there is a Jack in the cards or not
				if (cards.contains(Card.CJ)) {
					bestToBePlayed = cards.getIndexOf(Card.CJ);
				} else if (cards.contains(Card.SJ)) {
					bestToBePlayed = cards.getIndexOf(Card.SJ);
				} else if (cards.contains(Card.HJ)) {
					bestToBePlayed = cards.getIndexOf(Card.HJ);
				} else if (cards.contains(Card.DJ)) {
					bestToBePlayed = cards.getIndexOf(Card.DJ);
				} else {
					// No Jack in the cards
					log.debug(".playNextCard(): ... but i don't have a jack...");
					bestToBePlayed = cards.getLastIndexOfSuit(trumpSuit, false);
				}

			} else {

				// Player doesn't have trump
				// it doesn't matter what card is played
				// just play the first card in the CardList
				bestToBePlayed = findLowCard(cards, trumpSuit);
				log.debug(".playNextCard(): ... but i don't have any trumps...");
			}

		} else {

			log.debug(".playNextCard(): first card is a color card...");
			// If trump is not played the player is forced
			// to play the color of the first card
			if (cards.hasSuit(gameType, initialCard.getSuit())) {

				// Player has the color
				// check if it's ours or if I can beat
				if (Helper.isSinglePlayerWin(knowledge)) {
					bestToBePlayed = Helper.isAbleToBeat(cards, initialCard,
							initialCard, gameType);
					if (bestToBePlayed < 0) {
						log.debug(".playNextCard(): ...which i can't beat...");
						bestToBePlayed = cards.getLastIndexOfSuit(
								initialCard.getSuit(), false);
					}
				} else {
					// Play the card with the highest value
					log.debug(".playNextCard(): ...to which i try to put some value...");
					bestToBePlayed = cards.getFirstIndexOfSuit(
							initialCard.getSuit(), false);
				}

			} else {

				log.debug(".playNextCard(): ...which i don't have...");
				// Player doesn't have the color
				// is there any trump in the cards
				if (Helper.hasTrump(cards, trumpSuit)) {

					// Play the highest trump
					// Check whether there is a Jack in the cards or not
					if (cards.contains(Card.CJ)) {

						bestToBePlayed = cards.getIndexOf(Card.CJ);

					} else if (cards.contains(Card.SJ)) {

						bestToBePlayed = cards.getIndexOf(Card.SJ);

					} else if (cards.contains(Card.HJ)) {

						bestToBePlayed = cards.getIndexOf(Card.HJ);

					} else if (cards.contains(Card.DJ)) {

						bestToBePlayed = cards.getIndexOf(Card.DJ);

					} else {

						// No Jack in the cards
						bestToBePlayed = cards.getLastIndexOfSuit(trumpSuit,
								false);
					}

					log.debug(".playNextCard(): ...so i take it...");
				} else {

					// it doesn't matter what card is played
					// possible improvement: check card value!
					bestToBePlayed = cards.size() - 1;
					log.debug(".playNextCard(): ...but i can't take it...");
				}
			}
		}

		log.debug(".playNextCard(): player " + name + ": "
				+ cards.get(bestToBePlayed));
		return bestToBePlayed;
	}

	/**
	 * @param knowledge
	 * @return the card to play by a rearhand opponent player
	 */
	private int findRearhandCard(final ImmutablePlayerKnowledge knowledge) {
		GameType gameType = knowledge.getGameType();
		Card initialCard = (knowledge.getTrickCards().size() > 0 ? knowledge
				.getTrickCards().get(0) : null);
		Suit trumpSuit = gameType.getTrumpSuit();
		int bestToBePlayed;
		// I'm in rearhand
		// 1: check if player can match initial suit
		if (Helper.isAbleToMatch(cards, initialCard, gameType)) {
			// 1.1 if yes (i.e. I can match the initial suit): check if
			// necessary to beat
			log.debug(".playNextCard(): I can match the demanded color");
			if (Helper.isSinglePlayerWin(knowledge)) {
				log.debug(".playNextCard(): Trick is SinglePlayerWin");
				// 1.1.1: if yes: can beat?
				Card currentWinner;
				if (knowledge.getTrickCards().get(1)
						.beats(gameType, initialCard)) {
					currentWinner = knowledge.getTrickCards().get(1);
				} else {
					currentWinner = initialCard;
				}
				bestToBePlayed = Helper.isAbleToBeat(cards, currentWinner,
						initialCard, gameType);
				if (bestToBePlayed > -1) {
					// 1.1.1.1: if I can beat: do it
					log.debug(".playNextCard(): ...but I can beat it...");
				} else {
					log.debug(".playNextCard(): ...which I can't beat...");
					// 1.1.1.2: if I can't beat: find lowest matching card
					if (initialCard.isTrump(gameType)) {
						bestToBePlayed = cards.getFirstIndexOfSuit(
								gameType.getTrumpSuit(), false);
						if (bestToBePlayed < 0) {
							log.debug(".playNextCard(): Damn! I have to play a Jack...");
							bestToBePlayed = 0;
							while (cards.size() > bestToBePlayed + 1
									&& cards.get(bestToBePlayed + 1).getRank() == Rank.JACK) {
								bestToBePlayed++;
							}
						}
					} else {
						bestToBePlayed = cards.getFirstIndexOfSuit(
								initialCard.getSuit(), false);
					}
				}
			} else {
				log.debug(".playNextCard(): I can match the demanded color, and it's our trick already...");
				// 1.1.2: if no: find highest matching card
				if (initialCard.isTrump(gameType)) {
					bestToBePlayed = cards.getLastIndexOfSuit(
							gameType.getTrumpSuit(), false);
					if (bestToBePlayed < 0) {
						log.debug(".playNextCard(): Damn! I have to play a Jack...");
						bestToBePlayed = 0;
						while (cards.size() > bestToBePlayed + 1
								&& cards.get(bestToBePlayed + 1).getRank() == Rank.JACK) {
							bestToBePlayed++;
						}
					}
				} else {
					bestToBePlayed = cards.getLastIndexOfSuit(
							initialCard.getSuit(), false);
				}
			}
		} else {
			// 1.2: if no (i.e. I can't match the initial suit): is ours?
			if (Helper.isSinglePlayerWin(knowledge)) {
				// 1.2.1: if no: do I have trump?
				log.debug(".playNextCard(): I cannot match and trick is SinglePlayerWin");
				if (cards.hasTrump(gameType)) {
					// 1.2.1.1: if yes: do I want to trump
					if (knowledge.getTrickCards().getTotalValue() > 3) {
						log.debug(".playNextCard(): But I can and will take it...");
						// 1.2.1.1.1: if yes: find a good trump
						bestToBePlayed = findValuableTrump(cards, trumpSuit);
					} else {
						log.debug(".playNextCard(): I could trump, but I don't want to...");
						// 1.2.1.1.2: if no: find a low card
						bestToBePlayed = findLowCard(cards, trumpSuit);
					}
				} else {
					log.debug(".playNextCard(): I'd love to have it, but I can't match the initial suit...");
					// 1.2.1.2: if no: find a low card
					bestToBePlayed = findLowCard(cards, trumpSuit);
				}
			} else {
				log.debug(".playNextCard(): I cannot match but it's our trick already...");
				// 1.2.2: if yes: find highest value card (but no ace)
				bestToBePlayed = findHighCard(cards, trumpSuit);
				log.debug(".playNextCard(): got back value " + bestToBePlayed);

			}
		}
		return bestToBePlayed;
	}

	/**
	 * Gets the next card that the player wants to play in a null game
	 * 
	 * @param knowledge
	 * @return the index of the card that should be played
	 */
	private Card playNextCardNullGame(final ImmutablePlayerKnowledge knowledge) {
		int bestToBePlayed = -1;
		log.debug(".playNextCardNullGame(): cards: [" + cards + "]");

		if (knowledge.getTrickCards().size() > 0) {
			Card initialCard = knowledge.getTrickCards().get(0);
			if (!cards.hasSuit(knowledge.getGameType(), initialCard.getSuit())) {
				// TODO null game: abwerfen
				log.debug(".playNextCardNullGame(): abwerfen...");
				bestToBePlayed = 0;
			} else {
				// do I have a lower card?
				int toBePlayed = findLowerCard(cards, initialCard);
				if (toBePlayed < 0) {
					// no: take the highest one of that suit
					bestToBePlayed = cards.getFirstIndexOfSuit(
							initialCard.getSuit(), false);
				} else {
					bestToBePlayed = toBePlayed;
				}
			}
		} else {
			int toBePlayed = findInitialForNullGame(cards);
			log.debug(".playNextCardNullGame(): (initial for null): "
					+ toBePlayed);
			bestToBePlayed = toBePlayed;
		}
		log.debug(".playNextCardNullGame(): playing: ["
				+ cards.get(bestToBePlayed) + "]");
		if (bestToBePlayed < 0) {
			return null;
		}
		return cards.remove(bestToBePlayed);
	}

	/**
	 * Chooses the highest valued trump card
	 * 
	 * @param cards
	 * @param trump
	 * @return index of the card
	 */
	private int findValuableTrump(final CardList cards, final Suit trump) {
		// should be improved: consider, which other trumps have already been
		// played
		if (cards.size() < 1) {
			return 0;
		}
		int highCard = 0;
		int index = 0;
		while (++index < cards.size()) {
			// if(cards.get(index).isTrump(GameType.SUIT, trump) &&
			// cards.get(index).getPoints() > cards.get(highCard).getPoints()) {
			// highCard = index;
			// log.debug("     highest card set to "+index);
			// }
		}
		return (highCard < cards.size() ? highCard : 0);
	}

	/**
	 * Chooses the most valuable matching card of the given suit (considering
	 * the jacks if the suit is trump)
	 * 
	 * @param cards
	 * @param suit
	 * @param trump
	 * @return index of the card
	 */
	private int findMostValuableMatchingCard(final CardList cards,
			final Suit suit, final Suit trump) {
		if (cards.size() < 1) {
			return 0;
		}
		int highCard = 0;
		int index = 0;
		while (++index < cards.size()) {
			// if(!cards.get(index).isTrump(GameType.SUIT, trump)
			// && cards.get(index).getPoints() > cards.get(highCard).getPoints()
			// && cards.get(index).getRank() != Rank.ACE
			// && cards.get(index).getSuit() == suit) {
			// highCard = index;
			// }
		}
		return (highCard < cards.size() ? highCard : 0);
	}

	/**
	 * Chooses the highest valued card that is not trump (and not an ace)
	 * 
	 * @param cards
	 * @param trump
	 * @return index of the card
	 */
	private int findHighCard(final CardList cards, final Suit trump) {
		// TODO: add a flag whether aces should be included
		// or just consider the CardMemory
		if (cards.size() < 1) {
			return 0;
		}
		int highCard = 0;
		int index = 0;
		while (++index < cards.size()) {
			// if(cards.get(highCard).isTrump(GameType.SUIT, trump) &&
			// !cards.get(index).isTrump(GameType.SUIT, trump) &&
			// cards.get(index).getRank() != Rank.ACE) {
			// highCard = index;
			// }
			// else if(!cards.get(index).isTrump(GameType.SUIT, trump) &&
			// cards.get(index).getPoints() > cards.get(highCard).getPoints() &&
			// cards.get(index).getRank() != Rank.ACE) {
			// highCard = index;
			// }
		}
		return (highCard < cards.size() ? highCard : 0);
	}

	/**
	 * Selects the first card of the hand that has no value and is not trump
	 * 
	 * @param cards
	 * @param trump
	 * @return index of the card
	 */
	private int findLowCard(final CardList cards, final Suit trump) {
		if (cards.size() < 2) {
			return 0;
		}
		int lowCard = 0;
		int index = 0;
		boolean found = false;
		while (!found && ++index < cards.size()) {
			// if(!cards.get(index).isTrump(GameType.SUIT, trump) &&
			// cards.get(index).getPoints() < cards.get(lowCard).getPoints()) {
			// lowCard = index;
			// }
			// if(cards.get(lowCard).getPoints() == 0) found = true;
		}
		return (index < cards.size() ? index : 0);
	}

	/**
	 * Tries to find a lower card than the given card (should only be used in
	 * null games)
	 * 
	 * @param cards
	 * @param initialCard
	 * @return index of the card, -1 if none is found
	 */
	private int findLowerCard(final CardList cards, final Card initialCard) {

		int index = -1;
		boolean lowerCardFound = false;
		Card bestCard = null;
		NullRule rules = new NullRule();

		for (int i = 0; i < cards.size(); i++) {

			Card currCard = cards.get(i);
			if (rules
					.isCardAllowed(GameType.NULL, initialCard, cards, currCard)) {
				if (bestCard == null) {
					// no card found yet
					bestCard = currCard;
					index = i;
				} else if (!lowerCardFound
						&& currCard.getNullOrder() < bestCard.getNullOrder()) {
					// lower card found
					bestCard = currCard;
					index = i;
					lowerCardFound = true;
				}
			}
		}

		if (index == -1) {
			// still no good card found
			// --> play a random card
			index = rand.nextInt(cards.size());
		}

		log.debug(".findLowerCard(): " + index);
		return index;
	}

	/**
	 * Finds an initial card in a null game (not implemented yet --> just takes
	 * the last one)
	 * 
	 * @param cards
	 * @return index of the card
	 */
	private int findInitialForNullGame(final CardList cards) {
		// TODO initial card for null game
		return cards.size() - 1;
	}

	/**
	 * Finds an initial card to play (from the second trick onward)
	 * 
	 * @param knowledge
	 * @return index of the card
	 */
	private int findInitial(final ImmutablePlayerKnowledge knowledge) {
		GameType gameType = knowledge.getGameType();
		int[] rating = new int[cards.size()];
		// First, look for any aces that are not trump
		for (int x = 0; x < cards.size(); x++) {
			Card c = cards.get(x);
			if (knowledge.getDeclarer() == Player.MIDDLEHAND) {
				// add to the rating the number of remaining cards
			} else {
				// subtract from the rating the number of remaining cards
			}
			if (c.getRank() == Rank.ACE
					&& c.getSuit() != gameType.getTrumpSuit()) {
				if (knowledge.couldHaveSuit(knowledge.getDeclarer(),
						c.getSuit())) {
					rating[x] += 20;
					if (knowledge.getDeclarer() == Player.MIDDLEHAND) {
						rating[x] += cards.getSuitCount(c.getSuit(), false);
					} else {
						rating[x] -= cards.getSuitCount(c.getSuit(), false);
					}
				} else {
					rating[x] -= 20;
				}
			}
			if (c.getRank() == Rank.JACK) {
				rating[x] -= 20;
			}
			if (c.getRank() == Rank.TEN
					&& knowledge.isCardOutstanding(Card.getCard(c.getSuit(),
							Rank.ACE))) {
				rating[x] -= 40;
			}
			if (c.getRank() != Rank.JACK
					&& c.getSuit() == gameType.getTrumpSuit()) {
				rating[x] -= 30;
			}
			if (!knowledge.couldHaveSuit(knowledge.getDeclarer(), c.getSuit())) {
				rating[x] += (10 + c.getRank().ordinal() + cards.getSuitCount(
						c.getSuit(), false));
			}
		}
		StringBuilder sb = new StringBuilder();
		int result = 0;
		sb.append("[" + rating[0] + "]");
		for (int i = 1; i < rating.length; i++) {
			sb.append("[" + rating[i] + "]");
			if (rating[i] > rating[result]) {
				result = i;
			}
		}
		log.debug("Rating={" + sb + "}");
		return result;
	}

	/**
	 * Finds an initial card to play on the very first trick of the game
	 * 
	 * @param knowledge
	 * @return index of the card
	 */
	private int findFirstInitial(final ImmutablePlayerKnowledge knowledge) {
		GameType gameType = knowledge.getGameType();
		log.debug("Opening the game...");
		// First, look for any aces that are not trump
		int store = -1;
		for (int x = 0; x < cards.size(); x++) {
			if (cards.get(x).getRank() == Rank.ACE) {
				if (cards.get(x).getSuit() != gameType.getTrumpSuit()) {
					if (store >= 0) {
						if (knowledge.getDeclarer() == Player.MIDDLEHAND
								&& cards.getSuitCount(cards.get(x).getSuit(),
										false) > cards.getSuitCount(
										cards.get(store).getSuit(), false)) {
							store = x;
						} else if (knowledge.getDeclarer() == Player.REARHAND
								&& cards.getSuitCount(cards.get(x).getSuit(),
										false) < cards.getSuitCount(
										cards.get(store).getSuit(), false)) {
							store = x;
						}
					} else {
						store = x;
					}
				}
			}
		}
		if (store > 0) {
			return store;
		}
		if (knowledge.getDeclarer() == Player.MIDDLEHAND) {
			// If you don't have any, look for longest color
			// "kurzer Weg, lange Farbe"
			Suit maxSuit = null;
			for (Suit s : Suit.values()) {
				if (maxSuit == null) {
					maxSuit = s;
					continue;
				}
				if (s.equals(gameType.getTrumpSuit())) {
					continue;
				}
				if (cards.getSuitCount(s, false) > cards.getSuitCount(maxSuit,
						false)) {
					maxSuit = s;
				}
			}
			if (cards.get(cards.getFirstIndexOfSuit(maxSuit, false)).getRank() == Rank.ACE) {
				return cards.getFirstIndexOfSuit(maxSuit, false);
			}
			return cards.getLastIndexOfSuit(maxSuit, false);
		} else {
			// If you don't have any, look for shortest color
			// "langer Weg, kurze Farbe"
			Suit minSuit = null;
			for (Suit s : Suit.values()) {
				if (minSuit == null) {
					minSuit = s;
					continue;
				}
				if (s.equals(gameType.getTrumpSuit())) {
					continue;
				}
				if (cards.getSuitCount(s, false) < cards.getSuitCount(minSuit,
						false)) {
					minSuit = s;
				}
			}
			if (cards.get(cards.getFirstIndexOfSuit(minSuit, false)).getRank() == Rank.ACE) {
				return cards.getFirstIndexOfSuit(minSuit, false);
			}
			return cards.getLastIndexOfSuit(minSuit, false);
		}
	}
}
