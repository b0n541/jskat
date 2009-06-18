/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import de.jskat.data.GameAnnouncement;
import de.jskat.data.Trick;
import de.jskat.util.CardDeck;
import de.jskat.util.CardList;
import de.jskat.util.Card;
import de.jskat.util.Player;
import de.jskat.util.Rank;
import de.jskat.util.Suit;

/**
 * Holds the complete knowledge about a game, contains perfect and imperfect
 * information
 */
public class PlayerKnowledge {

	/** the basic game information */
	private GameAnnouncement game;

	/** Player position */
	private Player playerPosition;

	/**
	 * Contains all cards played by the players
	 */
	private List<Set<Card>> playedCards = new ArrayList<Set<Card>>();

	/**
	 * Contains all cards that could be on a certain position
	 */
	private List<Set<Card>> cardPositions = new ArrayList<Set<Card>>();

	/**
	 * Holds the highest bid every player has made during bidding
	 */
	// TODO use player position for accessing the values
	private int[] highestBid = new int[3];
	
	/**
	 * A complete card deck for doing some boolean operations with a subset of a deck
	 */
	private final CardDeck completeDeck = new CardDeck();
	
	/**
	 * Card played by the player on the left, represents first card in a trick or is NULL otherwise
	 */
	private Card leftPlayerTrickCard;
	
	/**
	 * Card played by the player on the right, represents the first or second card in a trick or is NULL otherwise
	 */
	private Card rightPlayerTrickCard;

	/**
	 * Counts the trump cards still on players hand
	 */
	private int trumpCount;
	
	/**
	 * Counts the number of cards on players hand for every card
	 */
	// TODO use suit for accessing the values
	private int[] suitCount = new int[4];
	
	/**
	 * Counts the points for every suit on players hand
	 */
	// TODO use suit for accessing the values
	private int[] suitPoints = new int[4];

	/**
	 * Holds trick information
	 */
	private List<Trick> tricks = new ArrayList<Trick>();

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

		this.playedCards.clear();
		this.cardPositions.clear();
		
		for (int i = 0; i < 4; i++) {
			
			if (i < 3) {
				// only for the players
				this.playedCards.add(EnumSet.noneOf(Card.class));
			}

			this.cardPositions.add(EnumSet.allOf(Card.class));
		}

		this.highestBid[0] = this.highestBid[1] = this.highestBid[2] = 0;
		
		this.leftPlayerTrickCard = null;
		this.rightPlayerTrickCard = null;
		
		this.trumpCount = 0;
		this.suitCount[0] = this.suitCount[1] = this.suitCount[2] = this.suitCount[3] = 0;
		this.suitPoints[0] = this.suitPoints[1] = this.suitPoints[2] = this.suitPoints[3] = 0;
		
		this.tricks.clear();
	}

	/**
	 * Checks whether a card was played already
	 * 
	 * @param card
	 *            Card to check
	 * @return TRUE if the card was played
	 */
	public boolean isCardPlayed(Card card) {

		return this.playedCards.get(0).contains(card)
				|| this.playedCards.get(1).contains(card)
				|| this.playedCards.get(2).contains(card);
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
	public boolean isCardPlayedBy(Player player, Card card) {

		return this.playedCards.get(player.getOrder()).contains(card);
	}

	/**
	 * Sets a card played
	 * 
	 * @param player
	 *            Player ID
	 * @param card
	 *            Card
	 */
	public void setCardPlayed(Player player, Card card) {

		this.playedCards.get(player.getOrder()).add(card);

		for (int i = 0; i < 2; i++) {

			this.cardPositions.get((player.getOrder() + i) % 3).remove(card);
		}
		
		setTrickCard(player, card);
	}

	/**
	 * Sets a card played by another player
	 * 
	 * @param otherPlayer Player position of other player
	 * @param card Card played
	 */
	private void setTrickCard(Player otherPlayer, Card card) {
		
		if (this.getPlayerPosition().getLeftNeighbor() == otherPlayer) {
			
			this.leftPlayerTrickCard = card;
		}
		else if (this.getPlayerPosition().getRightNeighbor() == otherPlayer) {
			
			this.rightPlayerTrickCard = card;
		}
	}
	
	/**
	 * Checks whether a card was played by another player in the current trick
	 * 
	 * @param otherPlayer Player position of the other player
	 * @param card Card played
	 * @return TRUE if the card was played by the other player in the current trick
	 */
	public boolean isCardPlayedInTrick(Player otherPlayer, Card card) {

		boolean result = false;
		
		if (this.getPlayerPosition().getLeftNeighbor() == otherPlayer) {
			
			if (card.equals(this.leftPlayerTrickCard)) {
				
				result = true;
			}
		}
		else if (this.getPlayerPosition().getRightNeighbor() == otherPlayer) {
			
			if (card.equals(this.rightPlayerTrickCard)) {
				
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * Clears the cards played in the trick
	 */
	public void clearTrickCards() {
		
		this.leftPlayerTrickCard = null;
		this.rightPlayerTrickCard = null;
	}

	/**
	 * Checks whether a card is still outstanding
	 * 
	 * @param card
	 *            Card to check
	 * @return TRUE if the card is still outstanding
	 */
	public boolean isCardOutstanding(Card card) {

		return !isCardPlayed(card);
	}

	/**
	 * Checks whether a player is the only person who has the card
	 * 
	 * @param player
	 *            Player ID
	 * @param card
	 *            Card to check
	 * @return TRUE if and only if the player has the card allone
	 */
	public boolean hasCard(Player player, Card card) {

		int possessionCount = 0;

		if (couldHaveCard(player, card)) {

			// check all players and the skat whether the card could be there
			for (int i = 0; i < 4; i++) {
	
				if (this.cardPositions.get(i).contains(card)) {
	
					possessionCount++;
				}
			}
		}
		
		return (possessionCount == 1);
	}

	/**
	 * Checks whether a player could have a card information,
	 * this is an uncertain information
	 * 
	 * @param player
	 *            Player ID
	 * @param card
	 *            Card to check
	 * @return TRUE if the player could have the card
	 */
	public boolean couldHaveCard(Player player, Card card) {

		return this.cardPositions.get(player.getOrder()).contains(card);
	}

	/**
	 * Adjusts the knowledge when a player has not served a suit
	 * 
	 * @param player
	 *            Player ID
	 * @param suit
	 */
	public void setMissingSuit(Player player, Suit suit) {

		for (Rank rank : Rank.values()) {

			this.cardPositions.get(player.getOrder()).remove(Card.getCard(suit, rank));
		}
	}

	/**
	 * Gets the highest bid for a player
	 * 
	 * @param player
	 *            Player ID
	 * @return Highest bid for the player
	 */
	public int getHighestBid(Player player) {

		return this.highestBid[player.getOrder()];
	}

	/**
	 * Sets the highest bid for a player
	 * 
	 * @param player
	 *            Player ID
	 * @param bidValue
	 *            Highest bid for the player
	 */
	public void setHighestBid(Player player, int bidValue) {

		this.highestBid[player.getOrder()] = bidValue;
	}

	/**
	 * Gets a complete card deck
	 * 
	 * @return Complete deck
	 */
	public CardDeck getCompleteDeck() {
		
		return this.completeDeck;
	}
	
	/**
	 * Gets the player position
	 * 
	 * @return Player position
	 */
	public Player getPlayerPosition() {
		
		return this.playerPosition;
	}

	/**
	 * Sets the player position
	 * 
	 * @param newPlayerPosition Player position
	 */
	public void setPlayerPosition(Player newPlayerPosition) {
		
		this.playerPosition = newPlayerPosition;
	}

	/**
	 * Adds a card to the suit/point counter
	 * 
	 * @param card Card
	 */
	public void addCard(Card card) {
		
		this.suitCount[card.getSuit().ordinal()]++;
		this.suitPoints[card.getSuit().ordinal()] += card.getRank().getPoints();
	}

	/**
	 * Removes a card from the suit/point counter
	 * 
	 * @param card Card
	 */
	public void removeCard(Card card) {
		
		this.suitCount[card.getSuit().ordinal()]--;
		this.suitPoints[card.getSuit().ordinal()] -= card.getRank().getPoints();
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
				
				if (this.playedCards.get(0).contains(Card.getCard(suit, rank)) ||
						this.playedCards.get(1).contains(Card.getCard(suit, rank)) ||
						this.playedCards.get(2).contains(Card.getCard(suit, rank))) {
					
					result.append(suit.shortString()).append(rank.shortString()).append(' ');
				}
				else {
					
					result.append("-- "); //$NON-NLS-1$
				}
			}
			
			result.append('\n');
		}
		
		return result.toString();
	}

	/**
	 * Get cards of the current trick
	 * 
	 * @return List of cards played in the current trick
	 */
	public CardList getTrickCards() {
		
		CardList trick = new CardList();
		
		if (this.leftPlayerTrickCard != null) {
			
			trick.add(this.leftPlayerTrickCard);
		}
		
		if (this.rightPlayerTrickCard != null) {
			
			trick.add(this.rightPlayerTrickCard);
		}
		
		return trick;
	}

	/**
	 * Adds a trick to the knowledge
	 * 
	 * @param trick Trick to be added
	 */
	public void addTrick(Trick trick) {
		
		this.tricks.add(trick);
	}

	/**
	 * Gets card positions according the player knowledge
	 * 
	 * @return String of card symbols
	 */
	public String getPossibleCardPositions(Player position) {
		// FIXME returns only an empty string!
		StringBuffer cards = new StringBuffer();
		
		return null;
	}

	/**
	 * Gets all cards that are already known
	 * 
	 * @return String of card symbols
	 */
	public CardDeck getKnownCards() {
		// FIXME returns complete card deck
		CardDeck deck = new CardDeck();
		
		return null;
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(GameAnnouncement game) {
		this.game = game;
	}

	/**
	 * @return the game
	 */
	public GameAnnouncement getGame() {
		return game;
	}
}
