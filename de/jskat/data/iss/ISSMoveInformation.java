/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.data.iss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jskat.data.GameAnnouncement;
import de.jskat.util.Card;
import de.jskat.util.Player;

/**
 * Holds all data for a ISS skat game
 */
public class ISSMoveInformation {

	private MovePlayer position;
	private Map<Player, Double> playerTimes = new HashMap<Player, Double>();
	private MoveType type;
	private int bidValue;
	private GameAnnouncement announcement;
	private Card skat0;
	private Card skat1;
	private List<Card> foreHandCards = new ArrayList<Card>();
	private List<Card> middleHandCards = new ArrayList<Card>();
	private List<Card> hindHandCards = new ArrayList<Card>();
	private Card card;

	/**
	 * Gets the game announcement
	 * 
	 * @return Game announcement
	 */
	public GameAnnouncement getGameAnnouncement() {
		return this.announcement;
	}

	/**
	 * Sets the game announcement
	 * 
	 * @param ann
	 *            Game announcement
	 */
	public void setGameAnnouncement(GameAnnouncement ann) {
		this.announcement = ann;
	}

	/**
	 * Gets a card from the skat
	 * 
	 * @param index
	 *            Index of card
	 * @return Card
	 */
	public Card getSkat(int index) {
		// TODO dirty hack
		Card result = null;
		if (index == 0) {

			result = this.skat0;
		} else if (index == 1) {

			result = this.skat1;
		}

		return result;
	}

	/**
	 * Sets the skat cards
	 * 
	 * @param newSkat0
	 *            First card
	 * @param newSkat1
	 *            Second card
	 */
	public void setSkatCards(Card newSkat0, Card newSkat1) {

		this.skat0 = newSkat0;
		this.skat1 = newSkat1;
	}

	/**
	 * Gets the position of the player who made the last move
	 * 
	 * @return Position of the player
	 */
	public MovePlayer getPosition() {

		return this.position;
	}

	/**
	 * Sets the position of the player who made the last move
	 * 
	 * @param newPosition
	 *            Position of the player
	 */
	public void setPosition(MovePlayer newPosition) {

		this.position = newPosition;
	}

	/**
	 * Clears all player times
	 */
	public void clearPlayerTimes() {

		this.playerTimes.clear();
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

		this.playerTimes.put(playerPosition, time);
	}

	/**
	 * Gets a player time
	 * 
	 * @param playerPosition
	 *            Player position
	 * @return Time
	 */
	public double getPlayerTime(Player playerPosition) {

		return this.playerTimes.get(playerPosition).doubleValue();
	}

	/**
	 * Gets the move type
	 * 
	 * @return Move type
	 */
	public MoveType getType() {

		return this.type;
	}

	/**
	 * Sets the move type
	 * 
	 * @param newType
	 *            Move type
	 */
	public void setType(MoveType newType) {

		this.type = newType;
	}

	/**
	 * Gets the bid value
	 * 
	 * @return Bid value
	 */
	public int getBidValue() {

		return this.bidValue;
	}

	/**
	 * Sets the bid value
	 * 
	 * @param newBidValue
	 *            Bid value
	 */
	public void setBidValue(int newBidValue) {

		this.bidValue = newBidValue;
	}

	/**
	 * Gets the played card
	 * 
	 * @return Card
	 */
	public Card getCard() {

		return this.card;
	}

	/**
	 * Sets the played card
	 * 
	 * @param newCard
	 */
	public void setCard(Card newCard) {

		this.card = newCard;
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
			this.foreHandCards.clear();
			break;
		case MIDDLE_HAND:
			this.middleHandCards.clear();
			break;
		case HIND_HAND:
			this.hindHandCards.clear();
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
			this.foreHandCards.add(newCard);
			break;
		case MIDDLE_HAND:
			this.middleHandCards.add(newCard);
			break;
		case HIND_HAND:
			this.hindHandCards.add(newCard);
			break;
		}
	}

	/**
	 * Gets cards from a player
	 * 
	 * @param player
	 *            Player
	 * @return Cards of a player
	 */
	public List<Card> getCards(Player player) {

		List<Card> result = null;

		switch (player) {
		case FORE_HAND:
			result = this.foreHandCards;
			break;
		case MIDDLE_HAND:
			result = this.middleHandCards;
			break;
		case HIND_HAND:
			result = this.hindHandCards;
			break;
		}

		return result;
	}
}
