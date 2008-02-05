/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share;

import jskat.data.GameAnnouncement;

import org.apache.log4j.Logger;

/**
 * Card class
 */
public class Card {

	static Logger log = Logger.getLogger(jskat.share.Card.class);

	/**
	 * Creates a new instance of Card by a definition string in the form of "SUIT-VALUE",
	 * each being a one-character abbreviation:<br>
	 * e.g. "D-K" for king of diamonds, "S-1" for ten of spades
	 * 
	 * @param card String representing the card
	 */
	public Card(String card) {

		if(card.length()!=3) {
			if(card.length()!=4 || !card.substring(1).equals("-10")) {
				throw new IllegalArgumentException("Wrong card string: "+card);
			}
		}
		
		switch(card.toUpperCase().charAt(0)) {
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
			throw new IllegalArgumentException("Wrong card string - no valid suit: "+card);
		}
		
		switch(card.toUpperCase().charAt(2)) {
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
			throw new IllegalArgumentException("Wrong card string - no valid value: "+card);
		}
	}
	
	/** Creates a new instance of Card */
	public Card(SkatConstants.Suits suit, SkatConstants.Ranks rank) {

		this.suit = suit;
		this.rank = rank;
	}
	
	/** 
	 * Compares the Cards whether the suit is the same or not 
	 */
	public boolean isSameSuit(Card cardToCompare) {

		return this.getSuit() == cardToCompare.getSuit();
	}

	/** 
	 * Compares the Cards whether the value is the same or not 
	 */
	public boolean isSameRank(Card cardToCompare) {

		return this.getRank() == cardToCompare.getRank();
	}

	/** 
	 * Compares the Cards whether the value is the same or not 
	 */
	public boolean isSameCalcValue(Card cardToCompare) {

		return this.getCalcValue() == cardToCompare.getCalcValue();
	}

	/** 
	 * Compares the Cards whether the value is the same or not 
	 */
	public boolean isSameSuitGrandOrder(Card cardToCompare) {

		return this.getSuitGrandOrder() == cardToCompare.getSuitGrandOrder();
	}

	/**
	 * Compares the Cards whether the value is the same or not 
	 */
	public boolean isSameNullOrder(Card cardToCompare) {

		return this.getNullOrder() == cardToCompare.getNullOrder();
	}

	/** 
	 * Compares the Cards whether the value is the same or not 
	 */
	public boolean isSameRamschOrder(Card cardToCompare) {

		return this.getRamschOrder() == cardToCompare.getRamschOrder();
	}

	/** 
	 * Get the Card suit 
	 */
	public SkatConstants.Suits getSuit() {

		return suit;
	}

	/** 
	 * Get the Card value 
	 */
	public SkatConstants.Ranks getRank() {

		return rank;
	}

	/** 
	 * Get the Card value for calculations 
	 */
	public int getCalcValue() {

		return calcValue;
	}

	/** 
	 * Get the card order value for suit or grand games 
	 */
	public int getSuitGrandOrder() {

		return rank.getSuitGrandOrder();
	}

	/** 
	 * Get the card order value for null games 
	 */
	public int getNullOrder() {

		return rank.getNullOrder();
	}

	/** 
	 * Get the card order value for ramsch games 
	 */
	public int getRamschOrder() {

		return rank.getRamschOrder();
	}

	/**
	 * Checks whether the card beats another given card under the current game
	 * type and trump color with respect to the initial card on the table
	 * 
	 * @deprecated
	 * 
	 * @param cardToCompare
	 * @param gameType
	 * @param currentTrumpColor
	 * @param initialColor
	 * @return TRUE if the card beats the other one
	 */
	public boolean beats(Card cardToCompare, SkatConstants.GameTypes gameType,
			SkatConstants.Suits currentTrumpColor, Card firstCard) {

		GameAnnouncement gameAnnouncement = new GameAnnouncement();

		gameAnnouncement.setGameType(gameType);
		gameAnnouncement.setTrump(currentTrumpColor);

		return beats(cardToCompare, gameAnnouncement, firstCard);
	}

	/**
	 * Checks whether the card beats another given card under the current game
	 * type and trump color with respect to the initial card on the table
	 * 
	 * @param cardToCompare
	 * @param gameAnnouncement
	 * @param firstTrickCard
	 * @return TRUE if the card beats the other one
	 */
	public boolean beats(Card cardToCompare, GameAnnouncement gameAnnouncement,
			Card firstTrickCard) {

		boolean result = false;

		if (gameAnnouncement.getGameType() == SkatConstants.GameTypes.NULL) {

			log.debug(".beats(): NULL GAME: this card: " + toString()
					+ " /-/ other card: " + cardToCompare + " [initial="
					+ firstTrickCard.getSuit() + "]");

			if (suit == firstTrickCard.getSuit()) {
				if (cardToCompare.getSuit() != firstTrickCard.getSuit()) {
					result = true;
				} else {
					result = (getNullOrder() > cardToCompare.getNullOrder());
				}
			}
		} else if (gameAnnouncement.getGameType() == SkatConstants.GameTypes.GRAND
				|| gameAnnouncement.getGameType() == SkatConstants.GameTypes.RAMSCH
				|| gameAnnouncement.getGameType() == SkatConstants.GameTypes.RAMSCHGRAND) {

			log.debug(".beats(): GRAND GAME: this card: " + toString()
					+ " /-/ other card: " + cardToCompare + " [initial="
					+ firstTrickCard.getSuit() + "]");

			if (rank == SkatConstants.Ranks.JACK) {

				if (cardToCompare.getRank() != SkatConstants.Ranks.JACK) {
					// only this card is a jack, so it wins
					result = true;
				} else {
					// both cards are Jacks, so the higher one wins
					result = (suit.getSuitOrder() < cardToCompare.getSuit().getSuitOrder());
				}
			} else if (suit == firstTrickCard.getSuit()) {

				if (cardToCompare.getSuit() != firstTrickCard.getSuit()
						&& cardToCompare.getRank() != SkatConstants.Ranks.JACK) {
					result = true;
				} else {

					// this one isn't a jack, so if the other one is, it always
					// wins
					// and I only have to consider the case where both aren't
					// jacks
					if (cardToCompare.getRank() != SkatConstants.Ranks.JACK) {
						if (gameAnnouncement.getGameType() == SkatConstants.GameTypes.GRAND) {
							result = (getSuitGrandOrder() > cardToCompare.getSuitGrandOrder());
						} else {
							result = (getRamschOrder() > cardToCompare.getRamschOrder());
						}
					}

				}
			}
		}
		// must be a suit game then
		else if (gameAnnouncement.getGameType() == SkatConstants.GameTypes.SUIT) {

			log.debug(".beats(): SUIT GAME: this card: " + toString()
					+ " /-/ other card: " + cardToCompare + " [initial="
					+ firstTrickCard.getSuit() + "]");

			if (isTrump(gameAnnouncement.getTrump())
					&& !cardToCompare.isTrump(gameAnnouncement.getTrump())) {

				log.debug("trump beats non-trump");
				result = true;
			} else if (isTrump(gameAnnouncement.getTrump())
					&& cardToCompare.isTrump(gameAnnouncement.getTrump())) {

				log.debug("both cards are trump");
				if (rank == SkatConstants.Ranks.JACK) {
					if (cardToCompare.getRank() != SkatConstants.Ranks.JACK) {
						result = true;
					} else {
						// both cards are Jacks
						result = (suit.getSuitOrder() < cardToCompare.getSuit().getSuitOrder());
					}
				} else {
					result = (getSuitGrandOrder() > cardToCompare.getSuitGrandOrder());
				}
				log
						.debug("both cards are trump - "
								+ (result ? "this one beats"
										: "this one doesn't beat"));
			} else if (!cardToCompare.isTrump(gameAnnouncement.getTrump())) {

				if (suit == firstTrickCard.getSuit()) {
					if (cardToCompare.getSuit() != firstTrickCard.getSuit()) {
						log.debug("correct suit beats false one");
						result = true;
					} else {
						result = (getSuitGrandOrder() > cardToCompare.getSuitGrandOrder());
						log.debug("both cards are same suit - "
								+ (result ? "this one beats"
										: "this one doesn't beat"));
					}
				}
			}
		} else {
			log.warn("Error: Undefined game type in Card.beats().");
		}
		return result;
	}

	/**
	 * Checks whether a Card is a trump card or not
	 * This method is deprecated, because it is only valid for suit games
	 * 
	 * @deprecated
	 * @param currentTrumpColor
	 * @return TRUE, when the card is a trump card
	 */
	public boolean isTrump(SkatConstants.Suits currentTrumpColor) {
		
		return (suit == currentTrumpColor || rank == SkatConstants.Ranks.JACK);
	}
	
	/**
	 * Checks whether a Card is a trump card or not
	 * 
	 * @param gameType The game type within the card is checked
	 * @param trumpColor The trump color, only needed in suit games
	 * @return TRUE, when the card is a trump card
	 */
	public boolean isTrump(SkatConstants.GameTypes gameType, SkatConstants.Suits trumpColor) {
		
		boolean result = false;
		
		if (gameType == SkatConstants.GameTypes.SUIT) {
				
			result = (suit == trumpColor || rank == SkatConstants.Ranks.JACK);
		}
		else if (gameType == SkatConstants.GameTypes.GRAND ||
				 gameType == SkatConstants.GameTypes.RAMSCH ||
				 gameType == SkatConstants.GameTypes.RAMSCHGRAND) {
				
			result = (rank == SkatConstants.Ranks.JACK);
		}
		
		return result;
	}

	/**
	 * Implementation of the equals() method
	 * 
	 * @param obj An object
	 * @return TRUE if the object is a Card object and has the same suit and rank
	 */
	public boolean equals(Object obj) {
		
		boolean result = false;
		
		if(obj instanceof Card) {
			
			result = equals((Card) obj);
		}
		
		return result;
	}
	
	/**
	 * Implementation of the equals() method
	 * 
	 * @param card The Card to compare
	 * @return TRUE if the Cards are equal
	 */
	public boolean equals(Card card) {
		
		boolean result = false;
		
		if (card != null &&
			card.getSuit() == suit && card.getRank() == rank) {
			
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
	/**
	 * Calculation value for the player points
	 */
	private int calcValue;
}