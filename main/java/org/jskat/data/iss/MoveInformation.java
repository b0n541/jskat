/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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
package org.jskat.data.iss;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jskat.data.GameAnnouncement;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Holds all data for a ISS skat game
 */
public class MoveInformation {

	private MovePlayer movePlayer;
	private Map<Player, Double> playerTimes = new HashMap<Player, Double>();
	private MoveType type;
	private int bidValue;
	private GameAnnouncement announcement;
	// FIXME (jan 23.11.2010) this doesn't work until card lists are static
	private CardList skat = new CardList();
	private CardList foreHandCards = new CardList();
	private CardList middleHandCards = new CardList();
	private CardList rearHandCards = new CardList();
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
	 * Gets  the skat
	 * 
	 * @return CardList
	 */
	public CardList getSkat() {

		return skat;
	}

	/**
	 * Sets the skat cards
	 * 
	 * @param newSkat
	 *            skat cards
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
		case FOREHAND:
			foreHandCards.clear();
			break;
		case MIDDLEHAND:
			middleHandCards.clear();
			break;
		case REARHAND:
			rearHandCards.clear();
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
		case FOREHAND:
			foreHandCards.add(newCard);
			break;
		case MIDDLEHAND:
			middleHandCards.add(newCard);
			break;
		case REARHAND:
			rearHandCards.add(newCard);
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
		rearHandCards = deal.get(2);
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
		case FOREHAND:
			result = foreHandCards;
			break;
		case MIDDLEHAND:
			result = middleHandCards;
			break;
		case REARHAND:
			result = rearHandCards;
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
		case FOREHAND:
			result = Player.FOREHAND;
			break;
		case MIDDLEHAND:
			result = Player.MIDDLEHAND;
			break;
		case REARHAND:
			result = Player.REARHAND;
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
