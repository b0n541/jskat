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
	private List<Card> ouvertCards = new ArrayList<Card>();
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
	 * @param ann Game announcement
	 */
	public void setGameAnnouncement(GameAnnouncement ann) {
		this.announcement = ann;
	}
	
	/**
	 * Gets a card from the skat
	 * 
	 * @param index Index of card
	 * @return Card
	 */
	public Card getSkat(int index) {
		// TODO dirty hack
		Card result = null;
		if (index == 0) {
		
			result = this.skat0;
		}
		else if (index == 1) {
			
			result = this.skat1;
		}
		
		return result;
	}
	
	/**
	 * Sets the skat cards
	 * 
	 * @param newSkat0 First card
	 * @param newSkat1 Second card
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
	 * @param newPosition Position of the player
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
	 * @param playerPosition Player position
	 * @param time Time
	 */
	public void putPlayerTime(Player playerPosition, Double time) {
		
		this.playerTimes.put(playerPosition, time);
	}
	
	/**
	 * Gets a player time
	 * 
	 * @param playerPosition Player position
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
	 * @param newType Move type
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
	 * @param newBidValue Bid value
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
	 * Clears the ouvert cards
	 */
	public void clearOuvertCards() {
		
		this.ouvertCards.clear();
	}
	
	/**
	 * Add an ouvert card
	 * 
	 * @param newCard Card
	 */
	public void addOuvertCard(Card newCard) {
		
		this.ouvertCards.add(newCard);
	}
	
	/**
	 * Gets the ouvert cards
	 * 
	 * @return Ouvert cards
	 */
	public List<Card> getOuvertCards() {
		
		return this.ouvertCards;
	}
}
