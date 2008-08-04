/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package jskat.share;

import jskat.data.GameAnnouncement;
import jskat.share.rules.SkatRulesFactory;
import jskat.share.rules.TrumpDecorator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Playing card
 */
public class Card {

	Log log = LogFactory.getLog(Card.class);

	/**
	 * Creates a new instance of Card by a definition string in the form of
	 * "SUIT-VALUE", each being a one-character abbreviation:<br>
	 * e.g. "D-K" for king of diamonds, "S-1" for ten of spades
	 * 
	 * @param card
	 *            String representing the card
	 */
	public Card(String card) {

		if (card.length() != 3) {
			if (card.length() != 4 || !card.substring(1).equals("-10")) {
				throw new IllegalArgumentException("Wrong card string: " + card);
			}
		}

		switch (card.toUpperCase().charAt(0)) {
		case 'D':
			suit = SkatConstants.Suits.DIAMONDS;
			break;
		case 'H':
			suit = SkatConstants.Suits.HEARTS;
			break;
		case 'S':
			suit = SkatConstants.Suits.SPADES;
			break;
		case 'C':
			suit = SkatConstants.Suits.CLUBS;
			break;
		default:
			throw new IllegalArgumentException(
					"Wrong card string - no valid suit: " + card);
		}

		switch (card.toUpperCase().charAt(2)) {
		case '7':
			rank = SkatConstants.Ranks.SEVEN;
			break;
		case '8':
			rank = SkatConstants.Ranks.EIGHT;
			break;
		case '9':
			rank = SkatConstants.Ranks.NINE;
			break;
		case '1':
		case 'T':
			rank = SkatConstants.Ranks.TEN;
			break;
		case 'J':
			rank = SkatConstants.Ranks.JACK;
			break;
		case 'Q':
			rank = SkatConstants.Ranks.QUEEN;
			break;
		case 'K':
			rank = SkatConstants.Ranks.KING;
			break;
		case 'A':
			rank = SkatConstants.Ranks.ACE;
			break;
		default:
			throw new IllegalArgumentException(
					"Wrong card string - no valid value: " + card);
		}
	}

	/**
	 * Creates a new instance of Card
	 * 
	 * @param suit
	 *            Suit of the card
	 * @param rank
	 *            Rank of the card
	 */
	public Card(SkatConstants.Suits suit, SkatConstants.Ranks rank) {

		this.suit = suit;
		this.rank = rank;
	}

	/**
	 * Compares the Cards whether the suit is the same or not
	 * 
	 * @param cardToCompare
	 *            Card to compare to
	 * @return TRUE if the suits are the same
	 */
	public boolean isSameSuit(Card cardToCompare) {

		return this.getSuit() == cardToCompare.getSuit();
	}

	/**
	 * Compares the Cards whether the rank is the same or not
	 * 
	 * @param cardToCompare
	 *            Card to compare to
	 * @return TRUE if the ranks are the same
	 */
	public boolean isSameRank(Card cardToCompare) {

		return this.getRank() == cardToCompare.getRank();
	}

	/**
	 * Compares the Cards whether the points is the same or not
	 * 
	 * @param cardToCompare
	 *            Card to compare to
	 * @return TRUE if the points are the same
	 */
	public boolean isSamePoints(Card cardToCompare) {

		return this.getPoints() == cardToCompare.getPoints();
	}

	/**
	 * Compares the Cards whether the order in suit or grand games is the same
	 * or not
	 * 
	 * @param cardToCompare
	 *            Card to compare to
	 * @return TRUE if the order is the same
	 */
	public boolean isSameSuitGrandOrder(Card cardToCompare) {

		return this.getSuitGrandOrder() == cardToCompare.getSuitGrandOrder();
	}

	/**
	 * Compares the Cards whether the order in null games is the same or not
	 * 
	 * @param cardToCompare
	 *            Card to compare to
	 * @return TRUE if the order is the same
	 */
	public boolean isSameNullOrder(Card cardToCompare) {

		return this.getNullOrder() == cardToCompare.getNullOrder();
	}

	/**
	 * Compares the Cards whether the order in Ramsch games is the same or not
	 * 
	 * @param cardToCompare
	 *            Card to compare to
	 * @return TRUE if the order is the same
	 */
	public boolean isSameRamschOrder(Card cardToCompare) {

		return this.getRamschOrder() == cardToCompare.getRamschOrder();
	}

	/**
	 * Get the Card suit
	 * 
	 * @return Suit of the card
	 */
	public SkatConstants.Suits getSuit() {

		return suit;
	}

	/**
	 * Get the Card rank
	 * 
	 * @return Rank of the card
	 */
	public SkatConstants.Ranks getRank() {

		return rank;
	}

	/**
	 * Get the Card value for calculations
	 * 
	 * @return Points of the card
	 */
	public int getPoints() {

		return rank.getPoints();
	}

	/**
	 * Get the card order value for suit or grand games
	 * 
	 * @return Order of the card in suit or Grand games
	 */
	public int getSuitGrandOrder() {

		return rank.getSuitGrandOrder();
	}

	/**
	 * Get the card order value for null games
	 * 
	 * @return Order of the card in null games
	 */
	public int getNullOrder() {

		return rank.getNullOrder();
	}

	/**
	 * Get the card order value for ramsch games
	 * 
	 * @return Order of the card in ramsch games
	 */
	public int getRamschOrder() {

		return rank.getRamschOrder();
	}

	/**
	 * Checks whether the card beats another given card under the current game
	 * type and trump color with respect to the initial card on the table
	 * 
	 * @param cardToCompare
	 * 			Card to compare
	 * @param gameType
	 * 			Game type
	 * @param trump
	 * 			Trump suit
	 * @param initialCard 
	 * 			First card in trick
	 * @return TRUE if the card beats the other one
	 */
	public boolean beats(Card cardToCompare, SkatConstants.GameTypes gameType,
			SkatConstants.Suits trump, Card initialCard) {

		return SkatRulesFactory.getSkatRules(gameType).isCardBeatsCard(this,
				cardToCompare, trump);

		/*
		 * boolean result = false;
		 * 
		 * if (gameType == SkatConstants.GameTypes.NULL) {
		 * 
		 * log.debug(".beats(): NULL GAME: this card: " + toString() + " /-/
		 * other card: " + cardToCompare + " [initial=" +
		 * initialTrickCard.getSuit() + "]");
		 * 
		 * if (suit == initialTrickCard.getSuit()) { if (cardToCompare.getSuit() !=
		 * initialTrickCard.getSuit()) { result = true; } else { result =
		 * (getNullOrder() > cardToCompare.getNullOrder()); } } } else if
		 * (gameType == SkatConstants.GameTypes.GRAND || gameType ==
		 * SkatConstants.GameTypes.RAMSCH || gameType ==
		 * SkatConstants.GameTypes.RAMSCHGRAND) {
		 * 
		 * log.debug(".beats(): GRAND GAME: this card: " + toString() + " /-/
		 * other card: " + cardToCompare + " [initial=" +
		 * initialTrickCard.getSuit() + "]");
		 * 
		 * if (rank == SkatConstants.Ranks.JACK) {
		 * 
		 * if (cardToCompare.getRank() != SkatConstants.Ranks.JACK) { // only
		 * this card is a jack, so it wins result = true; } else { // both cards
		 * are Jacks, so the higher one wins result = (suit.getSuitOrder() <
		 * cardToCompare.getSuit().getSuitOrder()); } } else if (suit ==
		 * initialTrickCard.getSuit()) {
		 * 
		 * if (cardToCompare.getSuit() != initialTrickCard.getSuit() &&
		 * cardToCompare.getRank() != SkatConstants.Ranks.JACK) { result = true; }
		 * else {
		 *  // this one isn't a jack, so if the other one is, it always // wins //
		 * and I only have to consider the case where both aren't // jacks if
		 * (cardToCompare.getRank() != SkatConstants.Ranks.JACK) { if (gameType ==
		 * SkatConstants.GameTypes.GRAND) { result = (getSuitGrandOrder() >
		 * cardToCompare.getSuitGrandOrder()); } else { result =
		 * (getRamschOrder() > cardToCompare.getRamschOrder()); } }
		 *  } } } // must be a suit game then else if (gameType ==
		 * SkatConstants.GameTypes.SUIT) {
		 * 
		 * log.debug(".beats(): SUIT GAME: this card: " + toString() + " /-/
		 * other card: " + cardToCompare + " [initial=" +
		 * initialTrickCard.getSuit() + "]");
		 * 
		 * if (isTrump(trump) && !cardToCompare.isTrump(trump)) {
		 * 
		 * log.debug("trump beats non-trump"); result = true; } else if
		 * (isTrump(trump) && cardToCompare.isTrump(trump)) {
		 * 
		 * log.debug("both cards are trump"); if (rank ==
		 * SkatConstants.Ranks.JACK) { if (cardToCompare.getRank() !=
		 * SkatConstants.Ranks.JACK) { result = true; } else { // both cards are
		 * Jacks result = (suit.getSuitOrder() <
		 * cardToCompare.getSuit().getSuitOrder()); } } else { result =
		 * (getSuitGrandOrder() > cardToCompare.getSuitGrandOrder()); } log
		 * .debug("both cards are trump - " + (result ? "this one beats" : "this
		 * one doesn't beat")); } else if (!cardToCompare.isTrump(trump)) {
		 * 
		 * if (suit == initialTrickCard.getSuit()) { if (cardToCompare.getSuit() !=
		 * initialTrickCard.getSuit()) { log.debug("correct suit beats false
		 * one"); result = true; } else { result = (getSuitGrandOrder() >
		 * cardToCompare.getSuitGrandOrder()); log.debug("both cards are same
		 * suit - " + (result ? "this one beats" : "this one doesn't beat")); } } } }
		 * else { log.warn("Error: Undefined game type in Card.beats()."); }
		 * return result;
		 */
	}

	/**
	 * Checks whether the card beats another given card under the current game
	 * type and trump color with respect to the initial card on the table
	 * 
	 * @param cardToCompare
	 * 			Card to compare
	 * @param gameType
	 * 			Game type
	 * @param initialCard 
	 * 			First card in trick
	 * @return TRUE if the card beats the other one
	 */
	public boolean beats(Card cardToCompare, SkatConstants.GameTypes gameType,
			Card initialCard) {

		if (gameType == SkatConstants.GameTypes.SUIT) {
			
			throw new IllegalArgumentException("Method is not valid for SUIT games.");
		}
		
		return SkatRulesFactory.getSkatRules(gameType).isCardBeatsCard(this,
				cardToCompare, null);
	}
	
	/**
	 * Checks whether the card beats another given card under the current game
	 * type and trump suit with respect to the initial card on the table
	 * 
	 * @param cardToCompare
	 * 			Card to compare
	 * @param gameAnnouncement
	 * 			Game announcement, contains game type and trump suit
	 * @param initialCard 
	 * 			First card in trick
	 * @return TRUE if the card beats the other one
	 */
	public boolean beats(Card cardToCompare, GameAnnouncement gameAnnouncement,
							Card initialCard) {

		return beats(cardToCompare, gameAnnouncement.getGameType(),
				gameAnnouncement.getTrump(), initialCard);
	}

	/**
	 * Checks whether a Card is a trump card or not
	 * 
	 * @param gameType
	 * @return TRUE, when the card is a trump card
	 */
	public boolean isTrump(SkatConstants.GameTypes gameType) {

		if (gameType == SkatConstants.GameTypes.SUIT) {
			
			throw new IllegalArgumentException("This method is not suitable for SUIT games.");
		}
		
		return isTrump(gameType, null);
	}

	/**
	 * Checks whether a Card is a trump card or not
	 * 
	 * @param gameType
	 *            The game type within the card is checked
	 * @param trump
	 *            The trump color, only needed in suit games
	 * @return TRUE, when the card is a trump card
	 */
	public boolean isTrump(SkatConstants.GameTypes gameType,
			SkatConstants.Suits trump) {

		return ((TrumpDecorator)SkatRulesFactory.getSkatRules(gameType)).isTrump(this, trump);
	}

	/**
	 * Implementation of the equals() method
	 * 
	 * @param obj
	 *            An object
	 * @return TRUE if the object is a Card object and has the same suit and
	 *         rank
	 */
	public boolean equals(Object obj) {

		boolean result = false;

		if (obj instanceof Card) {

			result = equals((Card) obj);
		}

		return result;
	}

	/**
	 * Implementation of the equals() method
	 * 
	 * @param card
	 *            The Card to compare
	 * @return TRUE if the Cards are equal
	 */
	public boolean equals(Card card) {

		boolean result = false;

		if (card != null && card.getSuit() == suit && card.getRank() == rank) {

			result = true;
		}

		return result;
	}

	/**
	 * Implementation of the toString() method
	 * 
	 * @return A human readable String for the card
	 */
	public String toString() {

		return suit.shortString() + "-" + rank.shortString();
	}

	/**
	 * Suit of the card
	 */
	private final SkatConstants.Suits suit;

	/**
	 * Rank of the card
	 */
	private final SkatConstants.Ranks rank;
}