/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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


package de.jskat.data.iss;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jskat.data.GameAnnouncement;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.Player;

/**
 * Holds all data for a ISS skat game
 */
public class ISSMoveInformation {

	private MovePlayer movePlayer;
	private Map<Player, Double> playerTimes = new HashMap<Player, Double>();
	private MoveType type;
	private int bidValue;
	private GameAnnouncement announcement;
	// FIXME (jan 23.11.2010) this doesn't work until card lists are static
	private CardList skat = new CardList();
	private CardList foreHandCards = new CardList();
	private CardList middleHandCards = new CardList();
	private CardList hindHandCards = new CardList();
	private CardList ouvertCards = new CardList();
	private Card card;
	private Player timeOutPlayer;

	/**
	 * Gets the game announcement
	 * 
	 * @return Game announcement
	 */
	public GameAnnouncement getGameAnnouncement() {
		return announcement;
	}

	/**
	 * Sets the game announcement
	 * 
	 * @param ann
	 *            Game announcement
	 */
	public void setGameAnnouncement(GameAnnouncement ann) {
		announcement = ann;
	}

	/**
	 * Gets a card from the skat
	 * 
	 * @param index
	 *            Index of card
	 * @return Card
	 */
	public CardList getSkat() {

		return skat;
	}

	/**
	 * Sets the skat cards
	 * 
	 * @param newSkat0
	 *            First card
	 * @param newSkat1
	 *            Second card
	 */
	public void setSkat(CardList newSkat) {

		skat = newSkat;
	}

	/**
	 * Gets the player who made the last move
	 * 
	 * @return Position of the player
	 */
	public MovePlayer getMovePlayer() {

		return movePlayer;
	}

	/**
	 * Gets the {@link Player} from a move player
	 * 
	 * @return Player, NULL if the move was done by WORLD (ISS)
	 */
	public Player getPlayer() {
		return getPlayer(movePlayer);
	}

	/**
	 * Sets the player who made the last move
	 * 
	 * @param newMovePlayer
	 *            Position of the player
	 */
	public void setMovePlayer(MovePlayer newMovePlayer) {

		movePlayer = newMovePlayer;
	}

	/**
	 * Clears all player times
	 */
	public void clearPlayerTimes() {

		playerTimes.clear();
	}

	/**
	 * Sets a player times
	 * 
	 * @param playerPosition
	 *            Player position
	 * @param time
	 *            Time
	 */
	public void putPlayerTime(Player playerPosition, Double time) {

		playerTimes.put(playerPosition, time);
	}

	/**
	 * Gets a player time
	 * 
	 * @param playerPosition
	 *            Player position
	 * @return Time
	 */
	public double getPlayerTime(Player playerPosition) {

		return playerTimes.get(playerPosition).doubleValue();
	}

	/**
	 * Gets the move type
	 * 
	 * @return Move type
	 */
	public MoveType getType() {

		return type;
	}

	/**
	 * Sets the move type
	 * 
	 * @param newType
	 *            Move type
	 */
	public void setType(MoveType newType) {

		type = newType;
	}

	/**
	 * Gets the bid value
	 * 
	 * @return Bid value
	 */
	public int getBidValue() {

		return bidValue;
	}

	/**
	 * Sets the bid value
	 * 
	 * @param newBidValue
	 *            Bid value
	 */
	public void setBidValue(int newBidValue) {

		bidValue = newBidValue;
	}

	/**
	 * Gets the played card
	 * 
	 * @return Card
	 */
	public Card getCard() {

		return card;
	}

	/**
	 * Sets the played card
	 * 
	 * @param newCard
	 */
	public void setCard(Card newCard) {

		card = newCard;
	}

	/**
	 * Clears cards of a player
	 * 
	 * @param player
	 *            Player
	 */
	public void clearCards(Player player) {

		switch (player) {
		case FORE_HAND:
			foreHandCards.clear();
			break;
		case MIDDLE_HAND:
			middleHandCards.clear();
			break;
		case HIND_HAND:
			hindHandCards.clear();
			break;
		}
	}

	/**
	 * Adds an card to a player
	 * 
	 * @param player
	 *            Player
	 * @param newCard
	 *            Card
	 */
	public void addCard(Player player, Card newCard) {

		switch (player) {
		case FORE_HAND:
			foreHandCards.add(newCard);
			break;
		case MIDDLE_HAND:
			middleHandCards.add(newCard);
			break;
		case HIND_HAND:
			hindHandCards.add(newCard);
			break;
		}
	}

	/**
	 * Sets all cards after dealing, list contains cards from fore hand, middle
	 * hand, hind hand and skat
	 * 
	 * @param deal
	 */
	public void setDealCards(List<CardList> deal) {

		foreHandCards = deal.get(0);
		middleHandCards = deal.get(1);
		hindHandCards = deal.get(2);
		skat = deal.get(3);
	}

	/**
	 * Gets cards from a player
	 * 
	 * @param player
	 *            Player
	 * @return Cards of a player
	 */
	public CardList getCards(Player player) {

		CardList result = null;

		switch (player) {
		case FORE_HAND:
			result = foreHandCards;
			break;
		case MIDDLE_HAND:
			result = middleHandCards;
			break;
		case HIND_HAND:
			result = hindHandCards;
			break;
		}

		return result;
	}

	/**
	 * Sets the time out player
	 * 
	 * @param newTimeOutPlayer
	 *            Time out player
	 */
	public void setTimeOutPlayer(Player newTimeOutPlayer) {

		timeOutPlayer = newTimeOutPlayer;
	}

	/**
	 * Gets the time out player
	 * 
	 * @return Time out player
	 */
	public Player getTimeOutPlayer() {

		return timeOutPlayer;
	}

	private static Player getPlayer(MovePlayer movePlayer) {

		Player result = null;

		switch (movePlayer) {
		case FORE_HAND:
			result = Player.FORE_HAND;
			break;
		case MIDDLE_HAND:
			result = Player.MIDDLE_HAND;
			break;
		case HIND_HAND:
			result = Player.HIND_HAND;
			break;
		case WORLD:
			break;
		}

		return result;
	}

	/**
	 * Sets the ouvert cards
	 * 
	 * @param newOuvertCards
	 *            Ouvert cards
	 */
	public void setOuvertCards(CardList newOuvertCards) {
		ouvertCards = newOuvertCards;
	}

	/**
	 * Gets the ouvert cards
	 * 
	 * @return Ouvert cards
	 */
	public CardList getOuvertCards() {
		return ouvertCards;
	}
}
