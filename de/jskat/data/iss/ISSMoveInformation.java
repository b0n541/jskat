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
	
	public GameAnnouncement getGameAnnouncement() {
		return this.announcement;
	}
	public void setGameAnnouncement(GameAnnouncement ann) {
		this.announcement = ann;
	}
	public Card getSkat(int index) {
		// TODO dirty hack
		Card result = null;
		if (index == 0) {
		
			result = skat0;
		}
		else if (index == 1) {
			
			result = skat1;
		}
		
		return result;
	}
	public void setSkatCards(Card skat0, Card skat1) {
		this.skat0 = skat0;
		this.skat1 = skat1;
	}
	public Card getSkat1() {
		return skat1;
	}
	public void setSkat1(Card skat1) {
		this.skat1 = skat1;
	}
	private Card card;
	
	public MovePlayer getPosition() {
		return this.position;
	}
	public void setPosition(MovePlayer newPosition) {
		this.position = newPosition;
	}
	public void clearPlayerTimes() {
		
		this.playerTimes.clear();
	}
	
	public void putPlayerTime(Player position, Double time) {
		
		this.playerTimes.put(position, time);
	}
	
	public double getPlayerTime(Player position) {
		
		return this.playerTimes.get(position).doubleValue();
	}
	public MoveType getType() {
		return type;
	}
	public void setType(MoveType type) {
		this.type = type;
	}
	public int getBidValue() {
		return bidValue;
	}
	public void setBidValue(int bidValue) {
		this.bidValue = bidValue;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}

	public void clearOuvertCards() {
		
		this.ouvertCards.clear();
	}
	
	public void addOuvertCard(Card card) {
		
		this.ouvertCards.add(card);
	}
	
	public List<Card> getOuvertCards() {
		
		return this.ouvertCards;
	}
}
