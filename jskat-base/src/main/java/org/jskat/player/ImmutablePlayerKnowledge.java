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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

/**
 * Holds the complete knowledge about a game, contains perfect and imperfect
 * information
 */
public class ImmutablePlayerKnowledge {

	/**
	 * Declarer player
	 */
	protected Player declarer;

	/** the basic game information */
	protected GameAnnouncement announcement;

	/** Player position */
	protected Player playerPosition;

	/**
	 * Contains all cards played by the players
	 */
	// FIXME (markus 07.11.11.) why is this a "Set<Card>" instead of "CardList"?
	protected final Map<Player, Set<Card>> playedCards = new HashMap<>();

	/**
	 * Contains all cards that could be on a certain position
	 */
	// FIXME (markus 07.11.11.) why is this a "Set<Card>" instead of "CardList"?
	protected final Map<Player, Set<Card>> possiblePlayerCards = new HashMap<>();

	/**
	 * Contains all cards that could be in the skat
	 */
	// FIXME (markus 07.11.11.) why is this a "Set<Card>" instead of "CardList"?
	protected final Set<Card> possibleSkatCards = new HashSet<>();

	/**
	 * Holds the highest bid every player has made during bidding
	 */
	protected final Map<Player, Integer> highestBid = new HashMap<>();

	/**
	 * The current trick
	 */
	protected Trick currentTrick;

	/**
	 * Card played by the player on the left, represents first card in a trick or is
	 * NULL otherwise
	 */
	protected Card leftPlayerTrickCard;

	/**
	 * Card played by the player on the right, represents the first or second card
	 * in a trick or is NULL otherwise
	 */
	protected Card rightPlayerTrickCard;

	/**
	 * Counts the trump cards still on players hand
	 */
	protected int trumpCount;

	/**
	 * Counts the number of cards on players hand for every card
	 *
	 * FIXME: do we need this numbers and if do we count the Jacks into this number
	 */
	// protected final Map<Suit, Integer> suitCount = new HashMap<Suit,
	// Integer>();

	/**
	 * Counts the points for every suit on players hand
	 *
	 * FIXME: do we need this numbers and if do we count the Jacks into this number
	 */
	// protected final Map<Suit, Integer> suitPoints = new HashMap<Suit,
	// Integer>();

	/**
	 * Holds trick information
	 */
	protected final List<Trick> tricks = new ArrayList<>();

	/** Player cards */
	protected final Set<Card> ownCards = new HashSet<>();
	/** Skat cards */
	protected final Set<Card> skat = new HashSet<>();
	/** Cards of the single player */
	protected Set<Card> singlePlayerCards = new HashSet<>();
	/** Flag for hand game */
	protected boolean handGame;
	/** Flag for ouvert game */
	protected boolean ouvertGame;
	/** Flag for schneider announced */
	protected boolean schneiderAnnounced;
	/** Flag for schwarz announced */
	protected boolean schwarzAnnounced;

	/**
	 * Constructor
	 */
	protected ImmutablePlayerKnowledge() {
	}

	/**
	 * Checks whether a player could have a card information, this is an uncertain
	 * information
	 *
	 * @param player
	 *            Player ID
	 * @param card
	 *            Card to check
	 * @return TRUE if the player could have the card
	 */
	public final boolean couldHaveCard(final Player player, final Card card) {

		return possiblePlayerCards.get(player).contains(card);
	}

	/**
	 * Checks whether a player could have a card of the given suit, this is an
	 * uncertain information
	 *
	 * @param player
	 *            Player ID
	 * @param suit
	 *            Suit to check
	 * @return TRUE if the player could have any card of the suit
	 */
	public final boolean couldHaveSuit(final Player player, final Suit suit) {
		boolean result = false;
		for (final Rank rank : Rank.values()) {
			if (Rank.JACK.equals(rank)) {
				continue;
			}
			result |= couldHaveCard(player, Card.getCard(suit, rank));
		}
		return result;
	}

	/**
	 * Checks whether a player could have any trump cards left, this is an uncertain
	 * information
	 *
	 * @param player
	 *            Player ID
	 * @return TRUE if the player could have any trump card
	 */
	public final boolean couldHaveTrump(final Player player) {
		for (final Card c : Card.values()) {
			if (c.isTrump(getGameType()) && couldHaveCard(player, c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a card could lie in the skat
	 *
	 * @param card
	 *            Card
	 * @return TRUE if card could lie in the skat
	 */
	public final boolean couldLieInSkat(final Card card) {
		return possibleSkatCards.contains(card);
	}

	/**
	 * Checks whether any player might have any trump cards left
	 *
	 * @return TRUE if any player could still have any trump cards
	 */
	public final boolean couldOpponentsHaveTrump() {
		if (playerPosition == declarer) {
			for (final Player p : Player.values()) {
				if (p == declarer) {
					continue;
				}
				for (final Suit s : Suit.values()) {
					if (couldHaveCard(p, Card.getCard(s, Rank.JACK))) {
						return true;
					}
				}
				for (final Rank r : Rank.values()) {
					if (couldHaveCard(p, Card.getCard(getGameAnnouncement().getGameType().getTrumpSuit(), r))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Gets all tricks that are completed
	 *
	 * @return List of completed tricks
	 */
	public final List<Trick> getCompletedTricks() {
		return Collections.unmodifiableList(tricks);
	}

	/**
	 * Provides access to the current trick
	 *
	 * @return The current trick
	 */
	public final Trick getCurrentTrick() {
		return (Trick) currentTrick.clone();
	}

	/**
	 * Gets the declarer position
	 *
	 * @return Declarer position
	 */
	public final Player getDeclarer() {

		return declarer;
	}

	/**
	 * @return the game
	 */
	public final GameAnnouncement getGameAnnouncement() {
		// FIXME jan 29.10.2013: make game announcement clonable
		return announcement;
	}

	/**
	 * convenience method for getGame().getGameType()
	 *
	 * @return the gameType
	 */
	public final GameType getGameType() {
		if (announcement == null) {
			return null;
		}
		return announcement.getGameType();
	}

	/**
	 * Gets the highest bid for a player
	 *
	 * @param player
	 *            Player ID
	 * @return Highest bid for the player
	 */
	public final Integer getHighestBid(final Player player) {

		return highestBid.get(player);
	}

	/**
	 * Gets the number of tricks
	 *
	 * @return Number of tricks
	 */
	public final int getNoOfTricks() {
		return tricks.size();
	}

	/**
	 * @return the ownCards
	 */
	public final CardList getOwnCards() {
		final CardList result = new CardList();
		result.addAll(ownCards);
		return result;
	}

	/**
	 * Converts all the cards from the tricks to the binary matrix, one int for each
	 * suit<br>
	 * &nbsp;<br>
	 * The index of the array equals the Suit ordinal (0=Clubs, 3=Diamonds).
	 *
	 * @return an array int[4]
	 */
	public final int[] getPlayedCardsBinary() {
		final int[] result = new int[4];
		for (final Trick t : tricks) {
			final int[] tmp = t.getCardList().toBinary();
			for (int i = 0; i < 4; i++) {
				result[i] += tmp[i];
			}
		}
		return result;
	}

	/**
	 * Gets the player position
	 *
	 * @return Player position
	 */
	public final Player getPlayerPosition() {

		return playerPosition;
	}

	/**
	 * Checks how many cards of the given suit a player could have, this is an
	 * uncertain information
	 *
	 * @param player
	 *            Player ID
	 * @param suit
	 *            Suit to check
	 * @param isTrump
	 *            TRUE, if the suit is also trump
	 * @param includeJacks
	 *            TRUE, if Jacks should be included in the count
	 * @return TRUE if the player could have any card of the suit
	 */
	public final int getPotentialSuitCount(final Player player, final Suit suit, final boolean isTrump,
			final boolean includeJacks) {
		int result = 0;
		for (final Rank r : Rank.values()) {
			if (r == Rank.JACK && !includeJacks) {
				continue;
			} else if (couldHaveCard(player, Card.getCard(suit, r))) {
				result++;
			}
		}
		if (isTrump) {
			for (final Suit s : Suit.values()) {
				if (couldHaveCard(player, Card.getCard(s, Rank.JACK))) {
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * @return the singlePlayerCards
	 */
	public final CardList getSinglePlayerCards() {
		final CardList result = new CardList();
		result.addAll(singlePlayerCards);
		return result;
	}

	/**
	 * @return the skat
	 */
	public final CardList getSkat() {
		final CardList result = new CardList();
		result.addAll(skat);
		return result;
	}

	/**
	 * Get cards of the current trick
	 *
	 * @return List of cards played in the current trick
	 */
	public final CardList getTrickCards() {

		final CardList trick = new CardList();

		if (leftPlayerTrickCard != null) {

			trick.add(leftPlayerTrickCard);
		}

		if (rightPlayerTrickCard != null) {

			trick.add(rightPlayerTrickCard);
		}

		return trick;
	}

	/**
	 * Gets the number of trump cards that is known to the player (either by being
	 * on his own hand or by having been played)
	 *
	 * @return the number of known trump cards
	 */
	public final int getTrumpCount() {
		return trumpCount;
	}

	/**
	 * Gets the current trump suit
	 *
	 * @return Trump suit or null if there is no trump
	 */
	public final Suit getTrumpSuit() {
		if (announcement == null || announcement.getGameType() == null) {
			throw new IllegalStateException("Game type not available."); //$NON-NLS-1$
		}
		return announcement.getGameType().getTrumpSuit();
	}

	/**
	 * Checks whether a player has a card alone by ruling out all other options,
	 * this might be an uncertain information
	 *
	 * @param player
	 *            Player ID
	 * @param card
	 *            Card to check
	 * @return TRUE if and only if the player has the card alone
	 */
	public final boolean hasCard(final Player player, final Card card) {

		if (couldHaveCard(player, card)) {
			return getPossibleCardPositions(card) == 1;
		}

		return false;
	}

	private int getPossibleCardPositions(final Card card) {

		int possessionCount = 0;

		for (final Player playerPosition : Player.values()) {
			if (couldHaveCard(playerPosition, card)) {
				possessionCount++;
			}
		}

		if (couldLieInSkat(card)) {
			possessionCount++;
		}

		return possessionCount;
	}

	/**
	 * Checks whether a card is still outstanding
	 *
	 * @param card
	 *            Card to check
	 * @return TRUE if the card is still outstanding
	 */
	public final boolean isCardOutstanding(final Card card) {

		return !isCardPlayed(card);
	}

	/**
	 * Checks whether a card was played already
	 *
	 * @param card
	 *            Card to check
	 * @return TRUE if the card was played
	 */
	public final boolean isCardPlayed(final Card card) {

		return playedCards.get(Player.FOREHAND).contains(card) || playedCards.get(Player.MIDDLEHAND).contains(card)
				|| playedCards.get(Player.REARHAND).contains(card);
	}

	/**
	 * Checks whether a card was played by a player
	 *
	 * @param player
	 *            Player ID
	 * @param card
	 *            Card
	 * @return TRUE if the card was played by the player
	 */
	public final boolean isCardPlayedBy(final Player player, final Card card) {

		return playedCards.get(player).contains(card);
	}

	/**
	 * Checks whether a card was played by another player in the current trick
	 *
	 * @param otherPlayer
	 *            Player position of the other player
	 * @param card
	 *            Card played
	 * @return TRUE if the card was played by the other player in the current trick
	 */
	public final boolean isCardPlayedInTrick(final Player otherPlayer, final Card card) {

		boolean result = false;

		if (getPlayerPosition().getLeftNeighbor() == otherPlayer) {

			if (card.equals(leftPlayerTrickCard)) {

				result = true;
			}
		} else if (getPlayerPosition().getRightNeighbor() == otherPlayer) {

			if (card.equals(rightPlayerTrickCard)) {

				result = true;
			}
		}

		return result;
	}

	/**
	 * @return the handGame
	 */
	public final boolean isHandGame() {
		return handGame;
	}

	/**
	 * @return the ouvertGame
	 */
	public final boolean isOuvertGame() {
		return ouvertGame;
	}

	/**
	 * Checks whether a card is on the players hand
	 *
	 * @param card
	 *            Card
	 * @return TRUE, if the card is on the players hand
	 */
	public final boolean isOwnCard(final Card card) {
		return ownCards.contains(card);
	}

	/**
	 * @return the schneiderAnnounced
	 */
	public final boolean isSchneiderAnnounced() {
		return schneiderAnnounced;
	}

	/**
	 * @return the schwarzAnnounced
	 */
	public final boolean isSchwarzAnnounced() {
		return schwarzAnnounced;
	}

	public Set<Player> getPlayerPartyMembers() {

		final Set<Player> result = new HashSet<>();
		if (getDeclarer().equals(getPlayerPosition())) {
			// player is declarer
			result.add(getDeclarer());
		} else {
			// player is opponent
			result.add(getDeclarer().getLeftNeighbor());
			result.add(getDeclarer().getRightNeighbor());
		}
		return result;
	}

	public CardList getPlayerPartyMadeCards() {

		final CardList result = new CardList();
		final Set<Player> partyMembers = getPlayerPartyMembers();

		for (final Trick trick : getCompletedTricks()) {
			if (partyMembers.contains(trick.getTrickWinner())) {
				// trick was won by player's party
				result.addAll(trick.getCardList());
			}
		}
		return result;
	}

	public Set<Player> getOpponentPartyMembers() {

		final Set<Player> result = new HashSet<>();
		if (getDeclarer().equals(getPlayerPosition())) {
			// player is declarer
			result.add(getDeclarer().getLeftNeighbor());
			result.add(getDeclarer().getRightNeighbor());
		} else {
			// player is opponent
			result.add(getDeclarer());
		}
		return result;
	}

	public CardList getOpponentPartyMadeCards() {

		final CardList result = new CardList();
		final Set<Player> opponents = getOpponentPartyMembers();

		for (final Trick trick : getCompletedTricks()) {
			if (opponents.contains(trick.getTrickWinner())) {
				// trick was won by opponent's party
				result.addAll(trick.getCardList());
			}
		}
		return result;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		final StringBuffer result = new StringBuffer();

		result.append("Player position: " + playerPosition + '\n');
		result.append("Own cards:\n"); //$NON-NLS-1$
		for (final Suit suit : Suit.values()) {

			result.append(suit.getLongString()).append(": "); //$NON-NLS-1$

			for (final Rank rank : Rank.values()) {

				final Card card = Card.getCard(suit, rank);
				if (isOwnCard(card)) {

					result.append(card).append(' ');
				} else {

					result.append("-- "); //$NON-NLS-1$
				}
			}

			result.append('\n');
		}

		result.append("Could have cards:\n"); //$NON-NLS-1$
		for (final Player player : Player.values()) {

			result.append("Player: " + player + '\n');

			for (final Suit suit : Suit.values()) {

				result.append(suit.getLongString()).append(": "); //$NON-NLS-1$

				for (final Rank rank : Rank.values()) {

					final Card card = Card.getCard(suit, rank);
					if (couldHaveCard(player, card)) {

						result.append(card).append(' ');
					} else {

						result.append("-- "); //$NON-NLS-1$
					}
				}

				result.append('\n');
			}
		}

		result.append("Played cards:\n"); //$NON-NLS-1$
		for (final Suit suit : Suit.values()) {

			result.append(suit.getLongString()).append(": "); //$NON-NLS-1$

			for (final Rank rank : Rank.values()) {

				final Card card = Card.getCard(suit, rank);
				if (playedCards.get(Player.FOREHAND).contains(card)
						|| playedCards.get(Player.MIDDLEHAND).contains(card)
						|| playedCards.get(Player.REARHAND).contains(card)) {

					result.append(card).append(' ');
				} else {

					result.append("-- "); //$NON-NLS-1$
				}
			}

			result.append('\n');
		}

		result.append("Player party made cards:\n");
		final CardList playerPartyMadeCards = getPlayerPartyMadeCards();
		for (final Suit suit : Suit.values()) {

			result.append(suit.getLongString()).append(": "); //$NON-NLS-1$

			for (final Rank rank : Rank.values()) {

				final Card card = Card.getCard(suit, rank);
				if (playerPartyMadeCards.contains(card)) {

					result.append(card).append(' ');
				} else {

					result.append("-- "); //$NON-NLS-1$
				}
			}

			result.append('\n');
		}

		result.append("Opponent party made cards:\n");
		final CardList opponentPartyMadeCards = getOpponentPartyMadeCards();
		for (final Suit suit : Suit.values()) {

			result.append(suit.getLongString()).append(": "); //$NON-NLS-1$

			for (final Rank rank : Rank.values()) {

				final Card card = Card.getCard(suit, rank);
				if (opponentPartyMadeCards.contains(card)) {

					result.append(card).append(' ');
				} else {

					result.append("-- "); //$NON-NLS-1$
				}
			}

			result.append('\n');
		}

		return result.toString();
	}
}
