/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-10
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.player;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

/**
 * Holds the complete knowledge about a game, contains perfect and imperfect
 * information
 */
public class PlayerKnowledge {

	/**
	 * Declarer player
	 */
	private Player declarer;

	/** the basic game information */
	private GameAnnouncement game;

	/** Player position */
	private Player playerPosition;

	/**
	 * Contains all cards played by the players
	 */
	// FIXME (markus 07.11.11.) why is this a "Set<Card>" instead of "CardList"?
	private final Map<Player, Set<Card>> playedCards = new HashMap<Player, Set<Card>>();

	/**
	 * Contains all cards that could be on a certain position
	 */
	// FIXME (markus 07.11.11.) why is this a "Set<Card>" instead of "CardList"?
	private final Map<Player, Set<Card>> possiblePlayerCards = new HashMap<Player, Set<Card>>();

	/**
	 * Contains all cards that could be in the skat
	 */
	// FIXME (markus 07.11.11.) why is this a "Set<Card>" instead of "CardList"?
	private final Set<Card> possibleSkatCards = new HashSet<Card>();

	/**
	 * Holds the highest bid every player has made during bidding
	 */
	private final Map<Player, Integer> highestBid = new HashMap<Player, Integer>();

	/**
	 * A complete card deck for doing some boolean operations with a subset of a
	 * deck
	 */
	private final CardDeck completeDeck = new CardDeck();

	/**
	 * The current trick
	 */
	private Trick currentTrick;

	/**
	 * Card played by the player on the left, represents first card in a trick
	 * or is NULL otherwise
	 */
	private Card leftPlayerTrickCard;

	/**
	 * Card played by the player on the right, represents the first or second
	 * card in a trick or is NULL otherwise
	 */
	private Card rightPlayerTrickCard;

	/**
	 * Counts the trump cards still on players hand
	 */
	private int trumpCount;

	/**
	 * Counts the number of cards on players hand for every card
	 */
	private final Map<Suit, Integer> suitCount = new HashMap<Suit, Integer>();

	/**
	 * Counts the points for every suit on players hand
	 */
	private final Map<Suit, Integer> suitPoints = new HashMap<Suit, Integer>();

	/**
	 * Holds trick information
	 */
	private final List<Trick> tricks = new ArrayList<Trick>();

	/** Player cards */
	private final CardList ownCards = new CardList();
	/** Skat cards */
	private final CardList skat = new CardList();
	/** Cards of the single player */
	private CardList singlePlayerCards = new CardList();
	/** Flag for hand game */
	private boolean handGame;
	/** Flag for ouvert game */
	private boolean ouvertGame;
	/** Flag for schneider announced */
	private boolean schneiderAnnounced;
	/** Flag for schwarz announced */
	private boolean schwarzAnnounced;

	/**
	 * Constructor
	 */
	public PlayerKnowledge() {

		initializeVariables();
	}

	/**
	 * Initializes all parameters
	 */
	public void initializeVariables() {

		ownCards.clear();
		skat.clear();
		singlePlayerCards.clear();
		schneiderAnnounced = false;
		schwarzAnnounced = false;
		handGame = false;
		ouvertGame = false;
		for (Player player : Player.values()) {
			highestBid.put(player, Integer.valueOf(0));

			playedCards.put(player, EnumSet.noneOf(Card.class));
			possiblePlayerCards.put(player, EnumSet.allOf(Card.class));
		}
		possibleSkatCards.clear();
		possibleSkatCards.addAll(EnumSet.allOf(Card.class));

		leftPlayerTrickCard = null;
		rightPlayerTrickCard = null;

		trumpCount = 0;

		for (Suit suit : Suit.values()) {
			suitCount.put(suit, Integer.valueOf(0));
			suitPoints.put(suit, Integer.valueOf(0));
		}

		tricks.clear();
	}

	/**
	 * Checks whether a card was played already
	 * 
	 * @param card
	 *            Card to check
	 * @return TRUE if the card was played
	 */
	public boolean isCardPlayed(final Card card) {

		return playedCards.get(Player.FOREHAND).contains(card)
				|| playedCards.get(Player.MIDDLEHAND).contains(card)
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
	public boolean isCardPlayedBy(final Player player, final Card card) {

		return playedCards.get(player).contains(card);
	}

	/**
	 * Sets the current trick
	 * 
	 * @param trick
	 */
	public final void setCurrentTrick(final Trick trick) {
		this.currentTrick = trick;
	}

	/**
	 * Provides access to the current trick
	 * 
	 * @return the current trick
	 */
	public final Trick getCurrentTrick() {
		return currentTrick;
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

		for (Player currPlayer : Player.values()) {
			possiblePlayerCards.get(currPlayer).remove(card);
		}
		possibleSkatCards.remove(card);
		if (card.isTrump(getGameType()) && player != playerPosition) {
			trumpCount++;
		}

		setTrickCard(player, card);
	}

	/**
	 * Sets a card played by another player
	 * 
	 * @param otherPlayer
	 *            Player position of other player
	 * @param playedCard
	 *            Card played
	 */
	private void setTrickCard(final Player otherPlayer, final Card playedCard) {

		if (getPlayerPosition().getLeftNeighbor() == otherPlayer) {

			leftPlayerTrickCard = playedCard;
		} else if (getPlayerPosition().getRightNeighbor() == otherPlayer) {

			rightPlayerTrickCard = playedCard;
		}

		currentTrick.addCard(playedCard);

		// adjust the knowledge about "could have" cards
		Card firstCard = currentTrick.getFirstCard();
		Card secondCard = currentTrick.getSecondCard();
		Card thirdCard = currentTrick.getThirdCard();
		if (firstCard != null && (secondCard != null || thirdCard != null)) {

			Card cardToCheck = null;
			if (thirdCard == null) {
				cardToCheck = secondCard;
			} else {
				cardToCheck = thirdCard;
			}

			if (GameType.NULL.equals(getGameType())) {
				if (!firstCard.isSameSuit(cardToCheck)) {
					// player has not followed suit
					// this means he has no cards with this suit
					// remove all cards from same suit from "could have" cards
					for (Card currCard : Card.values()) {
						if (currCard.isSameSuit(firstCard)) {
							possiblePlayerCards.get(otherPlayer).remove(
									currCard);
						}
					}
				}
			} else {
				SkatRule skatRules = SkatRuleFactory
						.getSkatRules(getGameType());

				if (firstCard.isTrump(getGameType())
						&& !cardToCheck.isTrump(getGameType())) {
					// first card was a trump card, player card was not
					// remove jacks from the "could have" cards
					possiblePlayerCards.get(otherPlayer).remove(Card.CJ);
					possiblePlayerCards.get(otherPlayer).remove(Card.SJ);
					possiblePlayerCards.get(otherPlayer).remove(Card.HJ);
					possiblePlayerCards.get(otherPlayer).remove(Card.DJ);
					// remove other trump cards for suit games
					if (GameType.CLUBS.equals(getGameType())
							|| GameType.SPADES.equals(getGameType())
							|| GameType.HEARTS.equals(getGameType())
							|| GameType.DIAMONDS.equals(getGameType())) {
						for (Card currCard : Card.values()) {
							if (currCard.isSameSuit(firstCard)) {
								possiblePlayerCards.get(otherPlayer).remove(
										currCard);
							}
						}
					}
				} else {
					// first card was not a trump card
					if (!firstCard.isSameSuit(cardToCheck)) {
						// player has not followed suit
						// this means he has no cards with this suit
						// remove all cards for that suit in "could have"
						// cards, except of the jacks
						for (Card currCard : Card.values()) {
							if (currCard.isSameSuit(firstCard)
									&& currCard.getRank() != Rank.JACK) {
								possiblePlayerCards.get(otherPlayer).remove(
										currCard);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Checks whether a card was played by another player in the current trick
	 * 
	 * @param otherPlayer
	 *            Player position of the other player
	 * @param card
	 *            Card played
	 * @return TRUE if the card was played by the other player in the current
	 *         trick
	 */
	public boolean isCardPlayedInTrick(final Player otherPlayer, final Card card) {

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
	 * Clears the cards played in the trick
	 */
	public void clearTrickCards() {

		leftPlayerTrickCard = null;
		rightPlayerTrickCard = null;
	}

	/**
	 * Checks whether a card is still outstanding
	 * 
	 * @param card
	 *            Card to check
	 * @return TRUE if the card is still outstanding
	 */
	public boolean isCardOutstanding(final Card card) {

		return !isCardPlayed(card);
	}

	/**
	 * Checks whether a player has a card by ruling out all other options, this
	 * might be an uncertain information
	 * 
	 * @param player
	 *            Player ID
	 * @param card
	 *            Card to check
	 * @return TRUE if and only if the player has the card alone
	 */
	public boolean hasCard(final Player player, final Card card) {

		int possessionCount = 0;

		if (couldHaveCard(player, card)) {

			// check all players and the skat whether the card could be there
			for (Set<Card> s : possiblePlayerCards.values()) {
				if (s.contains(card)) {
					possessionCount++;
				}
			}
			if (possibleSkatCards.contains(card)) {
				possessionCount++;
			}
		}

		return (possessionCount == 1);
	}

	/**
	 * Checks whether a card is on the players hand
	 * 
	 * @param card
	 *            Card
	 * @return TRUE, if the card is on the players hand
	 */
	public boolean isOwnCard(Card card) {
		return ownCards.contains(card);
	}

	/**
	 * Checks whether a player could have a card information, this is an
	 * uncertain information
	 * 
	 * @param player
	 *            Player ID
	 * @param card
	 *            Card to check
	 * @return TRUE if the player could have the card
	 */
	public boolean couldHaveCard(final Player player, final Card card) {

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
	public boolean couldHaveSuit(final Player player, final Suit suit) {
		for (Rank r : Rank.values()) {
			if (r == Rank.JACK) {
				continue;
			}
			return couldHaveCard(player, Card.getCard(suit, r));
		}
		return false;
	}

	/**
	 * Checks how many cards of the given suit a player could have, this is an
	 * uncertain information
	 * 
	 * @param player
	 *            Player ID
	 * @param suit
	 *            Suit to check
	 * @return TRUE if the player could have any card of the suit
	 */
	public int getPotentialSuitCount(final Player player, final Suit suit,
			final boolean isTrump, final boolean includeJacks) {
		int result = 0;
		for (Rank r : Rank.values()) {
			if (r == Rank.JACK && !includeJacks) {
				continue;
			} else if (couldHaveCard(player, Card.getCard(suit, r))) {
				result++;
			}
		}
		if (isTrump) {
			for (Suit s : Suit.values()) {
				if (couldHaveCard(player, Card.getCard(s, Rank.JACK))) {
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * Checks whether a player could have any trump cards left, this is an
	 * uncertain information
	 * 
	 * @param player
	 *            Player ID
	 * @return TRUE if the player could have any trump card
	 */
	public boolean couldHaveTrump(final Player player) {
		for (Card c : Card.values()) {
			if (c.isTrump(getGameType()) && couldHaveCard(player, c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether any player might have any trump cards left
	 * 
	 * @return TRUE if any player could still have any trump cards
	 */
	public boolean couldOpponentsHaveTrump() {
		if (playerPosition == declarer) {
			for (Player p : Player.values()) {
				if (p == declarer) {
					continue;
				}
				for (Suit s : Suit.values()) {
					if (couldHaveCard(p, Card.getCard(s, Rank.JACK))) {
						return true;
					}
				}
				for (Rank r : Rank.values()) {
					if (couldHaveCard(p, Card.getCard(getGame().getGameType()
							.getTrumpSuit(), r))) {
						return true;
					}
				}
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
	public boolean couldLieInSkat(final Card card) {
		return possibleSkatCards.contains(card);
	}

	/**
	 * Adjusts the knowledge when a player has not followed a suit
	 * 
	 * @param player
	 *            Player ID
	 * @param suit
	 */
	public void setMissingSuit(final Player player, final Suit suit) {

		for (Rank rank : Rank.values()) {
			if (rank != Rank.JACK || GameType.NULL.equals(getGameType())
					|| GameType.RAMSCH.equals(getGameType())) {
				possiblePlayerCards.get(player)
						.remove(Card.getCard(suit, rank));
			}
		}
	}

	/**
	 * Gets the highest bid for a player
	 * 
	 * @param player
	 *            Player ID
	 * @return Highest bid for the player
	 */
	public Integer getHighestBid(final Player player) {

		return highestBid.get(player);
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
	 * Gets a complete card deck
	 * 
	 * @return Complete deck
	 */
	public CardDeck getCompleteDeck() {

		return completeDeck;
	}

	/**
	 * Gets the player position
	 * 
	 * @return Player position
	 */
	public Player getPlayerPosition() {

		return playerPosition;
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
	 * Set the declarer position
	 * 
	 * @param newDeclarer
	 *            Declarer position
	 */
	public void setDeclarer(final Player newDeclarer) {

		declarer = newDeclarer;
	}

	/**
	 * Gets the declarer position
	 * 
	 * @return Declarer position
	 */
	public Player getDeclarer() {

		return declarer;
	}

	/**
	 * Adds a card to the own cards and suit/point counter
	 * 
	 * @param card
	 *            the Card to add
	 */
	public void addOwnCard(final Card card) {

		if (!ownCards.contains(card)) {
			ownCards.add(card);

			possiblePlayerCards.get(playerPosition.getLeftNeighbor()).remove(
					card);
			possiblePlayerCards.get(playerPosition.getRightNeighbor()).remove(
					card);
			possibleSkatCards.remove(card);

			suitCount.put(card.getSuit(), suitCount.get(card.getSuit()) + 1);
			suitPoints.put(card.getSuit(), suitCount.get(card.getSuit())
					+ card.getRank().getPoints());
		}
	}

	/**
	 * Adds cards to the own cards and the suit/point counter
	 * 
	 * @param cards
	 *            Card to be added
	 */
	public void addOwnCards(final CardList cards) {
		for (Card card : cards) {
			addOwnCard(card);
		}
	}

	/**
	 * Removes a card from the suit/point counter
	 * 
	 * @param card
	 *            Card
	 */
	public void removeCard(final Card card) {

		suitCount.put(card.getSuit(), suitCount.get(card.getSuit()) - 1);
		suitPoints.put(card.getSuit(), suitCount.get(card.getSuit())
				- card.getRank().getPoints());
	}

	public void removeOwnCard(final Card card) {
		removeCard(card);
		ownCards.remove(card);
	}

	public void removeOwnCards(final CardList cards) {
		for (Card card : cards) {
			removeOwnCard(card);
		}
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();

		result.append("Played cards:\n"); //$NON-NLS-1$
		for (Suit suit : Suit.values()) {

			result.append(suit.shortString()).append(": "); //$NON-NLS-1$

			for (Rank rank : Rank.values()) {

				if (playedCards.get(Player.FOREHAND).contains(
						Card.getCard(suit, rank))
						|| playedCards.get(Player.MIDDLEHAND).contains(
								Card.getCard(suit, rank))
						|| playedCards.get(Player.REARHAND).contains(
								Card.getCard(suit, rank))) {

					result.append(suit.shortString())
							.append(rank.shortString()).append(' ');
				} else {

					result.append("-- "); //$NON-NLS-1$
				}
			}

			result.append('\n');
		}

		return result.toString();
	}

	/**
	 * Converts all the cards from the tricks to the binary matrix, one int for
	 * each suit<br>
	 * &nbsp;<br>
	 * The index of the array equals the Suit ordinal (0=Clubs, 3=Diamonds).
	 * 
	 * @return an array int[4]
	 */
	public int[] getPlayedCardsBinary() {
		int[] result = new int[4];
		for (Trick t : tricks) {
			int[] tmp = t.getCardList().toBinary();
			for (int i = 0; i < 4; i++) {
				result[i] += tmp[i];
			}
		}
		return result;
	}

	/**
	 * Gets the number of tricks
	 * 
	 * @return Number of tricks
	 */
	public int getNoOfTricks() {
		return tricks.size();
	}

	/**
	 * Get cards of the current trick
	 * 
	 * @return List of cards played in the current trick
	 */
	public CardList getTrickCards() {

		CardList trick = new CardList();

		if (leftPlayerTrickCard != null) {

			trick.add(leftPlayerTrickCard);
		}

		if (rightPlayerTrickCard != null) {

			trick.add(rightPlayerTrickCard);
		}

		return trick;
	}

	/**
	 * Adds a trick to the knowledge
	 * 
	 * @param trick
	 *            Trick to be added
	 */
	public void addTrick(final Trick trick) {

		tricks.add(trick);
	}

	/**
	 * Gets all tricks that are completed
	 */
	public List<Trick> getCompletedTricks() {
		return tricks;
	}

	/**
	 * Sets the game announcement
	 * 
	 * @param gameAnn
	 *            Game announcement to set
	 */
	public void setGame(final GameAnnouncement gameAnn) {

		game = gameAnn;
		if (!GameType.PASSED_IN.equals(getGameType())) {
			ownCards.sort(getGameType());
			for (Card c : ownCards) {
				// FIXME (jansch 21.09.2011) Cards shouldn't check whether they
				// are trump or not, let skat rules do the job
				if (c.isTrump(getGameType())) {
					trumpCount++;
				}
			}
		}
	}

	/**
	 * @return the game
	 */
	public GameAnnouncement getGame() {

		return game;
	}

	/**
	 * convenience method for getGame().getGameType()
	 * 
	 * @return the gameType
	 */
	public GameType getGameType() {
		if (game == null) {
			return null;
		}
		return game.getGameType();
	}

	/**
	 * Gets the current trump suit
	 * 
	 * @return Trump suit or null if there is no trump
	 */
	public Suit getTrumpSuit() {
		if (game == null || game.getGameType() == null) {
			throw new IllegalStateException("Game type not available."); //$NON-NLS-1$
		}
		return game.getGameType().getTrumpSuit();
	}

	/**
	 * Gets the number of trump cards that is known to the player (either by
	 * being on his own hand or by having been played)
	 * 
	 * @return the number of known trump cards
	 */
	public int getTrumpCount() {
		return trumpCount;
	}

	/**
	 * Gets the suit count
	 * 
	 * @param suit
	 *            Suit
	 * @return Number of cards from this suit
	 */
	public int getSuitCount(final Suit suit) {

		return suitCount.get(suit).intValue();
	}

	/**
	 * Gets the suit points
	 * 
	 * @param suit
	 *            Suit
	 * @return Points from this suit
	 */
	public int getSuitPoints(final Suit suit) {

		return suitPoints.get(suit).intValue();
	}

	/**
	 * Resets the data of the current game
	 */
	public void resetCurrentGameData() {
		initializeVariables();
	}

	/**
	 * @return the ownCards
	 */
	public CardList getOwnCards() {
		return ownCards;
	}

	/**
	 * @param newCards
	 *            the ownCards to set
	 */
	public void setOwnCards(final CardList newCards) {
		ownCards.clear();
		ownCards.addAll(newCards);
	}

	/**
	 * @return the skat
	 */
	public CardList getSkat() {
		return skat.getImmutableCopy();
	}

	/**
	 * @param newSkat
	 *            the skat to set
	 */
	public void setSkat(final CardList newSkat) {
		skat.clear();
		skat.addAll(newSkat);
	}

	/**
	 * @return the singlePlayerCards
	 */
	public CardList getSinglePlayerCards() {
		return singlePlayerCards;
	}

	/**
	 * @param singlePlayerCards
	 *            the singlePlayerCards to set
	 */
	public void setSinglePlayerCards(final CardList singlePlayerCards) {
		this.singlePlayerCards = singlePlayerCards;
	}

	/**
	 * @return the handGame
	 */
	public boolean isHandGame() {
		return handGame;
	}

	/**
	 * @param handGame
	 *            the handGame to set
	 */
	public void setHandGame(final boolean handGame) {
		this.handGame = handGame;
	}

	/**
	 * @return the ouvertGame
	 */
	public boolean isOuvertGame() {
		return ouvertGame;
	}

	/**
	 * @param ouvertGame
	 *            the ouvertGame to set
	 */
	public void setOuvertGame(final boolean ouvertGame) {
		this.ouvertGame = ouvertGame;
	}

	/**
	 * @return the schneiderAnnounced
	 */
	public boolean isSchneiderAnnounced() {
		return schneiderAnnounced;
	}

	/**
	 * @param schneiderAnnounced
	 *            the schneiderAnnounced to set
	 */
	public void setSchneiderAnnounced(final boolean schneiderAnnounced) {
		this.schneiderAnnounced = schneiderAnnounced;
	}

	/**
	 * @return the schwarzAnnounced
	 */
	public boolean isSchwarzAnnounced() {
		return schwarzAnnounced;
	}

	/**
	 * @param schwarzAnnounced
	 *            the schwarzAnnounced to set
	 */
	public void setSchwarzAnnounced(final boolean schwarzAnnounced) {
		this.schwarzAnnounced = schwarzAnnounced;
	}
}
