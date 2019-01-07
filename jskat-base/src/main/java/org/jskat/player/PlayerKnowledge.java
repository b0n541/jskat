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
package org.jskat.player;

import java.util.EnumSet;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

public final class PlayerKnowledge extends ImmutablePlayerKnowledge {

	public PlayerKnowledge() {
		initializeVariables();
	}

	private void addOwnCard(final Card card) {
		if (ownCards.add(card)) {
			possiblePlayerCards.get(playerPosition.getLeftNeighbor()).remove(card);
			possiblePlayerCards.get(playerPosition.getRightNeighbor()).remove(card);
			possibleSkatCards.remove(card);
		}
	}

	/**
	 * Adds cards to the own cards and the suit/point counter
	 *
	 * @param cards
	 *            Card to be added
	 */
	public void addOwnCards(final Iterable<Card> cards) {
		for (final Card card : cards) {
			addOwnCard(card);
		}
		possiblePlayerCards.get(playerPosition).clear();
		possiblePlayerCards.get(playerPosition).addAll(ownCards);
	}

	/**
	 * Adds a trick to the knowledge
	 *
	 * @param trick
	 *            Trick to be added
	 */
	public void addCompletedTrick(final Trick trick) {

		tricks.add(trick);
	}

	/**
	 * Clears the cards played in the trick
	 */
	private void clearTrickCards() {

		leftPlayerTrickCard = null;
		rightPlayerTrickCard = null;
	}

	/**
	 * Initializes all parameters
	 */
	private void initializeVariables() {

		resetCurrentGameData();
	}

	public void removeOwnCard(final Card card) {
		ownCards.remove(card);
	}

	public void removeOwnCards(final CardList cards) {
		for (final Card card : cards) {
			removeOwnCard(card);
		}
	}

	/**
	 * Resets the data of the current game
	 */
	public void resetCurrentGameData() {
		ownCards.clear();
		skat.clear();
		singlePlayerCards.clear();
		schneiderAnnounced = false;
		schwarzAnnounced = false;
		handGame = false;
		ouvertGame = false;
		for (final Player player : Player.values()) {
			highestBid.put(player, Integer.valueOf(0));

			playedCards.put(player, EnumSet.noneOf(Card.class));
			possiblePlayerCards.put(player, EnumSet.allOf(Card.class));
		}
		possibleSkatCards.clear();
		possibleSkatCards.addAll(EnumSet.allOf(Card.class));

		leftPlayerTrickCard = null;
		rightPlayerTrickCard = null;

		trumpCount = 0;

		tricks.clear();
	}

	/**
	 * Sets a card played
	 *
	 * @param player
	 *            Player ID
	 * @param card
	 *            Card
	 */
	public void setCardPlayed(final Player player, final Card card) {

		playedCards.get(player).add(card);

		if (playerPosition.equals(player)) {
			removeOwnCard(card);
			if (card.isTrump(getGameType())) {
				trumpCount--;
			}
		}

		setTrickCard(player, card);
	}

	/**
	 * Sets the current trick
	 *
	 * @param trickNo
	 *            Trick no in game
	 * @param trickForehand
	 *            Forehand player for the trick
	 */
	public void setNextTrick(final int trickNo, final Player trickForehand) {
		currentTrick = new Trick(trickNo, trickForehand);
		clearTrickCards();
	}

	/**
	 * Set the declarer position
	 *
	 * @param newDeclarer
	 *            Declarer position
	 */
	public void setDeclarer(final Player newDeclarer) {

		declarer = newDeclarer;
	}

	/**
	 * Sets the game announcement
	 *
	 * @param gameAnn
	 *            Game announcement to set
	 */
	public void setGame(final GameAnnouncement gameAnn) {

		announcement = gameAnn;
		trumpCount = 0;
		if (!GameType.PASSED_IN.equals(getGameType())) {
			for (final Card c : ownCards) {
				// FIXME (jansch 21.09.2011) Cards shouldn't check whether they
				// are trump or not, let skat rules do the job
				if (c.isTrump(getGameType())) {
					trumpCount++;
				}
			}
		}
	}

	/**
	 * @param handGame
	 *            the handGame to set
	 */
	public void setHandGame(final boolean handGame) {
		this.handGame = handGame;
	}

	/**
	 * Sets the highest bid for a player
	 *
	 * @param player
	 *            Player ID
	 * @param bidValue
	 *            Highest bid for the player
	 */
	public void setHighestBid(final Player player, final Integer bidValue) {

		highestBid.put(player, bidValue);
	}

	/**
	 * Adjusts the knowledge when a player has not followed a suit
	 *
	 * @param player
	 *            Player ID
	 * @param suit
	 *            Suit that the player didn't follow
	 */
	public void setMissingSuit(final Player player, final Suit suit) {

		for (final Rank rank : Rank.values()) {
			if (rank != Rank.JACK || GameType.NULL.equals(getGameType()) || GameType.RAMSCH.equals(getGameType())) {
				possiblePlayerCards.get(player).remove(Card.getCard(suit, rank));
			}
		}
	}

	/**
	 * @param ouvertGame
	 *            the ouvertGame to set
	 */
	public void setOuvertGame(final boolean ouvertGame) {
		this.ouvertGame = ouvertGame;
	}

	/**
	 * Sets the player position
	 *
	 * @param newPlayerPosition
	 *            Player position
	 */
	public void setPlayerPosition(final Player newPlayerPosition) {

		playerPosition = newPlayerPosition;
	}

	/**
	 * @param schneiderAnnounced
	 *            the schneiderAnnounced to set
	 */
	public void setSchneiderAnnounced(final boolean schneiderAnnounced) {
		this.schneiderAnnounced = schneiderAnnounced;
	}

	/**
	 * @param schwarzAnnounced
	 *            the schwarzAnnounced to set
	 */
	public void setSchwarzAnnounced(final boolean schwarzAnnounced) {
		this.schwarzAnnounced = schwarzAnnounced;
	}

	/**
	 * @param singlePlayerCards
	 *            the singlePlayerCards to set
	 */
	public void setSinglePlayerCards(final CardList singlePlayerCards) {
		this.singlePlayerCards.clear();
		for (final Card card : singlePlayerCards) {
			this.singlePlayerCards.add(card);
		}
	}

	/**
	 * @param newSkat
	 *            the skat to set
	 */
	public void setSkat(final CardList newSkat) {
		skat.clear();
		for (final Card card : newSkat) {
			skat.add(card);
		}
	}

	/**
	 * Sets a card played by a player
	 *
	 * @param player
	 *            Player position of other player
	 * @param playedCard
	 *            Card played
	 */
	private void setTrickCard(final Player player, final Card playedCard) {

		if (getPlayerPosition().getLeftNeighbor() == player) {

			leftPlayerTrickCard = playedCard;
		} else if (getPlayerPosition().getRightNeighbor() == player) {

			rightPlayerTrickCard = playedCard;
		}

		for (final Player currPlayer : Player.values()) {
			possiblePlayerCards.get(currPlayer).remove(playedCard);
		}
		possibleSkatCards.remove(playedCard);

		currentTrick.addCard(playedCard);

		// adjust the knowledge about "could have" cards
		final Card firstCard = currentTrick.getFirstCard();
		final Card secondCard = currentTrick.getSecondCard();
		final Card thirdCard = currentTrick.getThirdCard();
		if (firstCard != null && (secondCard != null || thirdCard != null)) {

			Card cardToCheck = null;
			if (thirdCard == null) {
				cardToCheck = secondCard;
			} else {
				cardToCheck = thirdCard;
			}

			adjustPossibleCards(player, firstCard, cardToCheck);
		}
	}

	private void adjustPossibleCards(final Player player, final Card firstTrickCard, final Card cardPlayed) {

		if (GameType.NULL.equals(getGameType())) {
			if (!firstTrickCard.isSameSuit(cardPlayed)) {
				// player has not followed suit
				// this means he has no cards with this suit
				// remove all cards from same suit from "could have" cards
				for (final Card currCard : Card.values()) {
					if (currCard.isSameSuit(firstTrickCard)) {
						possiblePlayerCards.get(player).remove(currCard);
					}
				}
			}
		} else {
			final SkatRule skatRules = SkatRuleFactory.getSkatRules(getGameType());

			if (firstTrickCard.isTrump(getGameType())) {
				if (!cardPlayed.isTrump(getGameType())) {
					// first card was a trump card, player card was not
					// remove jacks from the "could have" cards
					possiblePlayerCards.get(player).remove(Card.CJ);
					possiblePlayerCards.get(player).remove(Card.SJ);
					possiblePlayerCards.get(player).remove(Card.HJ);
					possiblePlayerCards.get(player).remove(Card.DJ);
					// remove other trump cards for suit games
					if (GameType.CLUBS.equals(getGameType()) || GameType.SPADES.equals(getGameType())
							|| GameType.HEARTS.equals(getGameType()) || GameType.DIAMONDS.equals(getGameType())) {
						for (final Card currCard : Card.values()) {
							if (getGameType().getTrumpSuit().equals(currCard.getSuit())) {
								possiblePlayerCards.get(player).remove(currCard);
							}
						}
					}
				}
			} else {
				// first card was not a trump card
				if (!firstTrickCard.isSameSuit(cardPlayed)) {
					// player has not followed suit
					// this means he has no cards with this suit
					// remove all cards for that suit in "could have"
					// cards, except of the jacks
					for (final Card currCard : Card.values()) {
						if (currCard.isSameSuit(firstTrickCard) && currCard.getRank() != Rank.JACK) {
							possiblePlayerCards.get(player).remove(currCard);
						}
					}
				}
			}
		}
	}
}
