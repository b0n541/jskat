package jskat.player;

import jskat.share.Card;
import jskat.share.SkatConstants;

/**
 * Holds the complete knowledge about a game, 
 * contains perfect and imperfect information 
 */
public class PlayerKnowledge {

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
		
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 4; j++) {
				
				if (j < 3) {
					playedCards[i][j] = false;
				}
				cardPositions[i][j] = true;
			}
		}
		
		highestBid[0] = highestBid[1] = highestBid[2] = 0;
	}
	
	private int getCardIndex(Card card) {
		
		return card.getSuit().ordinal() * 8 + card.getRank().ordinal();
	}
	
	/**
	 * Checks whether a card was played already
	 * 
	 * @param card Card to check
	 * @return TRUE if the card was played
	 */
	public boolean isCardPlayed(Card card) {
		
		return playedCards[0][getCardIndex(card)] ||
				playedCards[1][getCardIndex(card)] ||
				playedCards[2][getCardIndex(card)];
	}
	
	/**
	 * Checks whether a card was played by a player
	 * 
	 * @param player Player ID
	 * @param card Card
	 * @return TRUE if the card was played by the player
	 */
	public boolean isCardPlayedBy(int player, Card card) {
		
		return playedCards[player][getCardIndex(card)];
	}
	
	/**
	 * Sets a card played
	 * 
	 * @param player Player ID
	 * @param card Card
	 */
	public void setCardPlayed(int player, Card card) {
		
		playedCards[player][getCardIndex(card)] = true;
		
		for (int i = 0; i < 0; i++) {
			
			cardPositions[i][getCardIndex(card)] = false;
		}
	}
	
	/**
	 * Checks whether a card is still outstanding
	 * 
	 * @param card Card to check
	 * @return TRUE if the card is still outstanding
	 */
	public boolean isCardOutstanding(Card card) {
		
		return !isCardPlayed(card);
	}
	
	/**
	 * Checks whether a player is the only person who has the card
	 * 
	 * @param player Player ID
	 * @param card Card to check
	 * @return TRUE if and only if the player has the card allone
	 */
	public boolean hasCard(int player, Card card) {
		
		int possessionCount = 0;
		
		// check all players and the skat whether the card could be there
		for (int i = 0; i < 4; i++) {
			
			if (cardPositions[i][getCardIndex(card)]) {
				
				possessionCount++;
			}
		}
		
		return possessionCount == 1;
	}
	
	/**
	 * Checks whether a player could have a card
	 * information is uncertain
	 * 
	 * @param player Player ID
	 * @param card Card to check
	 * @return TRUE if the player could have the card
	 */
	public boolean couldHaveCard(int player, Card card) {
		
		return cardPositions[player][getCardIndex(card)];
	}
	
	/**
	 * Adjusts the knowledge when a player has not served a suit
	 * 
	 * @param player Player ID
	 * @param suit 
	 */
	public void setMissingSuit(int player, SkatConstants.Suits suit) {
		
		for (int i = 0; i < 8; i++) {
			
			cardPositions[player][suit.ordinal() + i] = false;
		}
	}
	
	/**
	 * Gets the highest bid for a player
	 * 
	 * @param player Player ID
	 * @return Highest bid for the player
	 */
	public int getHighestBid(int player) {
		
		return highestBid[player];
	}

	/**
	 * Sets the highest bid for a player
	 * 
	 * @param player Player ID
	 * @param bidValue Highest bid for the player
	 */
	public void setHighestBid(int player, int bidValue) {
		
		highestBid[player] = bidValue;
	}

	/** TRUE if a card was played already
	 *  first index is 0 - me, 1 and 2 - other players, 3 - skat 
	 *	second index is calculated by SUIT.ordinal * 8 + RANK.ordinal
	 */
	private boolean[][] playedCards = new boolean[3][32];
	
	/** TRUE if a card is or could be held by a player or is in the skat 
	 *  first index is 0 - me, 1 and 2 - other players, 3 - skat 
	 *	second index is calculated by SUIT.ordinal * 8 + RANK.ordinal
	 */
	private boolean[][] cardPositions = new boolean[4][32];
	
	/**
	 * Holds the highest bid every player has made during bidding
	 */
	private int[] highestBid = new int[3];
}
