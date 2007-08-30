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
 * 
 * @author Jan Schäfer
 * @author Markus J. Luzius
 */
public class Card {

	static Logger log = Logger.getLogger(jskat.share.Card.class);

	/**
	 * Creates a new instance of Card by a definition string in the form of "SUIT-VALUE",
	 * each being a one-character abbreviation:<br>
	 * e.g. "D-K" for king of diamonds, "S-1" for ten of spades
	 * @param card
	 */
	public Card(String card) {

		if(card.length()!=3) {
			if(card.length()!=4 || !card.substring(1).equals("-10")) {
				throw new IllegalArgumentException("Wrong card string: "+card);
			}
		}
		
		switch(card.toUpperCase().charAt(0)) {
		case 'D':
			suit = SkatConstants.DIAMONDS;
			break;
		case 'H':
			suit = SkatConstants.HEARTS;
			break;
		case 'S':
			suit = SkatConstants.SPADES;
			break;
		case 'C':
			suit = SkatConstants.CLUBS;
			break;
		default:
			throw new IllegalArgumentException("Wrong card string - no valid suit: "+card);
		}
		
		switch(card.toUpperCase().charAt(2)) {
		case '7':
			value = SkatConstants.SEVEN;
			break;
		case '8':
			value = SkatConstants.EIGHT;
			break;
		case '9':
			value = SkatConstants.NINE;
			break;
		case '1':
		case 'T':
			value = SkatConstants.TEN;
			break;
		case 'J':
			value = SkatConstants.JACK;
			break;
		case 'Q':
			value = SkatConstants.QUEEN;
			break;
		case 'K':
			value = SkatConstants.KING;
			break;
		case 'A':
			value = SkatConstants.ACE;
			break;
		default:
			throw new IllegalArgumentException("Wrong card string - no valid value: "+card);
		}
		
		setCardEnv();
	}
	
	/** Creates a new instance of Card */
	public Card(int suit, int value) {

		this.suit = suit;
		this.value = value;
		
		setCardEnv();

	}

	private void setCardEnv() {
		switch (suit) {

		case (SkatConstants.CLUBS):
			this.suitValue = SkatConstants.CLUBS_VAL;
			break;
		case (SkatConstants.SPADES):
			this.suitValue = SkatConstants.SPADES_VAL;
			break;
		case (SkatConstants.HEARTS):
			this.suitValue = SkatConstants.HEARTS_VAL;
			break;
		case (SkatConstants.DIAMONDS):
			this.suitValue = SkatConstants.DIAMONDS_VAL;
			break;
		}

		switch (value) {

		case (SkatConstants.SEVEN):
			this.calcValue = SkatConstants.SEVEN_VAL;
			this.suitGrandValue = SkatConstants.SEVEN_SUIT_GRAND_VAL;
			this.nullValue = SkatConstants.SEVEN_NULL_VAL;
			this.ramschValue = SkatConstants.SEVEN_RAMSCH_VAL;
			break;
		case (SkatConstants.EIGHT):
			this.calcValue = SkatConstants.EIGHT_VAL;
			this.suitGrandValue = SkatConstants.EIGHT_SUIT_GRAND_VAL;
			this.nullValue = SkatConstants.EIGHT_NULL_VAL;
			this.ramschValue = SkatConstants.EIGHT_RAMSCH_VAL;
			break;
		case (SkatConstants.NINE):
			this.calcValue = SkatConstants.NINE_VAL;
			this.suitGrandValue = SkatConstants.NINE_SUIT_GRAND_VAL;
			this.nullValue = SkatConstants.NINE_NULL_VAL;
			this.ramschValue = SkatConstants.NINE_RAMSCH_VAL;
			break;
		case (SkatConstants.JACK):
			this.calcValue = SkatConstants.JACK_VAL;
			this.suitGrandValue = SkatConstants.JACK_SUIT_GRAND_VAL;
			this.nullValue = SkatConstants.JACK_NULL_VAL;
			this.ramschValue = SkatConstants.JACK_RAMSCH_VAL;
			break;
		case (SkatConstants.QUEEN):
			this.calcValue = SkatConstants.QUEEN_VAL;
			this.suitGrandValue = SkatConstants.QUEEN_SUIT_GRAND_VAL;
			this.nullValue = SkatConstants.QUEEN_NULL_VAL;
			this.ramschValue = SkatConstants.QUEEN_RAMSCH_VAL;
			break;
		case (SkatConstants.KING):
			this.calcValue = SkatConstants.KING_VAL;
			this.suitGrandValue = SkatConstants.KING_SUIT_GRAND_VAL;
			this.nullValue = SkatConstants.KING_NULL_VAL;
			this.ramschValue = SkatConstants.KING_RAMSCH_VAL;
			break;
		case (SkatConstants.TEN):
			this.calcValue = SkatConstants.TEN_VAL;
			this.suitGrandValue = SkatConstants.TEN_SUIT_GRAND_VAL;
			this.nullValue = SkatConstants.TEN_NULL_VAL;
			this.ramschValue = SkatConstants.TEN_RAMSCH_VAL;
			break;
		case (SkatConstants.ACE):
			this.calcValue = SkatConstants.ACE_VAL;
			this.suitGrandValue = SkatConstants.ACE_SUIT_GRAND_VAL;
			this.nullValue = SkatConstants.ACE_NULL_VAL;
			this.ramschValue = SkatConstants.ACE_RAMSCH_VAL;
			break;
		}
	}
	
	/** Compares the Cards whether the suit is the same or not */
	public boolean isSameSuit(Card cardToCompare) {

		return this.getSuit() == cardToCompare.getSuit();
	}

	/** Compares the Cards whether the value is the same or not */
	public boolean isSameValue(Card cardToCompare) {

		return this.getValue() == cardToCompare.getValue();
	}

	/** Compares the Cards whether the value is the same or not */
	public boolean isSameCalcValue(Card cardToCompare) {

		return this.getCalcValue() == cardToCompare.getCalcValue();
	}

	/** Compares the Cards whether the value is the same or not */
	public boolean isSameSuitGrandValue(Card cardToCompare) {

		return this.getSuitGrandValue() == cardToCompare.getSuitGrandValue();
	}

	/** Compares the Cards whether the value is the same or not */
	public boolean isSameNullValue(Card cardToCompare) {

		return this.getNullValue() == cardToCompare.getNullValue();
	}

	/** Compares the Cards whether the value is the same or not */
	public boolean isSameRamschValue(Card cardToCompare) {

		return this.getRamschValue() == cardToCompare.getRamschValue();
	}

	/** Get the Card suit */
	public int getSuit() {

		return suit;
	}

	/** Get the Card suit value */
	public int getSuitValue() {

		return suitValue;
	}

	/** Get the Card value */
	public int getValue() {

		return value;
	}

	/** Get the Card value for calculations */
	public int getCalcValue() {

		return calcValue;
	}

	/** Get the Card value for suit or grand games */
	public int getSuitGrandValue() {

		return suitGrandValue;
	}

	/** Get the Card value for null games */
	public int getNullValue() {

		return nullValue;
	}

	/** Get the Card value for ramsch games */
	public int getRamschValue() {

		return ramschValue;
	}

	/**
	 * Checks whether the card beats another given card under the current game
	 * type and trumpf color with respect to the initial card on the table
	 * 
	 * @deprecated
	 * 
	 * @param cardToCompare
	 * @param gameType
	 * @param currentTrumpColor
	 * @param initialColor
	 * @return TRUE if the card beats the other one
	 */
	public boolean beats(Card cardToCompare, int gameType,
			int currentTrumpColor, int initialColor) {

		GameAnnouncement gameAnnouncement = new GameAnnouncement();

		gameAnnouncement.setGameType(gameType);
		gameAnnouncement.setTrump(currentTrumpColor);

		return beats(cardToCompare, gameAnnouncement,
				new Card(initialColor, -1));
	}

	/**
	 * Checks whether the card beats another given card under the current game
	 * type and trumpf color with respect to the initial card on the table
	 * 
	 * @param cardToCompare
	 * @param gameAnnouncement
	 * @param firstTrickCard
	 * @return TRUE if the card beats the other one
	 */
	public boolean beats(Card cardToCompare, GameAnnouncement gameAnnouncement,
			Card firstTrickCard) {

		boolean result = false;

		if (gameAnnouncement.getGameType() == SkatConstants.NULL) {

			log.debug(".beats(): NULL GAME: this card: " + toString()
					+ " /-/ other card: " + cardToCompare + " [initial="
					+ firstTrickCard.getSuit() + "]");

			if (suit == firstTrickCard.getSuit()) {
				if (cardToCompare.getSuit() != firstTrickCard.getSuit()) {
					result = true;
				} else {
					result = (nullValue > cardToCompare.getNullValue());
				}
			}
		} else if (gameAnnouncement.getGameType() == SkatConstants.GRAND
				|| gameAnnouncement.getGameType() == SkatConstants.RAMSCH
				|| gameAnnouncement.getGameType() == SkatConstants.RAMSCHGRAND) {

			log.debug(".beats(): GRAND GAME: this card: " + toString()
					+ " /-/ other card: " + cardToCompare + " [initial="
					+ firstTrickCard.getSuit() + "]");

			if (value == SkatConstants.JACK) {

				if (cardToCompare.getValue() != SkatConstants.JACK) {
					// only this card is a jack, so it wins
					result = true;
				} else {
					// both cards are Jacks, so the higher one wins
					result = (suit < cardToCompare.getSuit());
				}
			} else if (suit == firstTrickCard.getSuit()) {

				if (cardToCompare.getSuit() != firstTrickCard.getSuit()
						&& cardToCompare.getValue() != SkatConstants.JACK) {
					result = true;
				} else {

					// this one isn't a jack, so if the other one is, it always
					// wins
					// and I only have to consider the case where both aren't
					// jacks
					if (cardToCompare.getValue() != SkatConstants.JACK) {
						if (gameAnnouncement.getGameType() == SkatConstants.GRAND) {
							result = (value > cardToCompare.getValue());
						} else {
							result = (ramschValue > cardToCompare
									.getRamschValue());
						}
					}

				}
			}
		}
		// must be a suit game then
		else if (gameAnnouncement.getGameType() == SkatConstants.SUIT) {

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
				if (value == SkatConstants.JACK) {
					if (cardToCompare.getValue() != SkatConstants.JACK) {
						result = true;
					} else {
						// both cards are Jacks
						result = (suit < cardToCompare.getSuit());
					}
				} else {
					result = (value > cardToCompare.getValue());
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
						result = (value > cardToCompare.getValue());
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
	public boolean isTrump(int currentTrumpColor) {
		
		return (suit == currentTrumpColor || value == SkatConstants.JACK);
	}
	
	/**
	 * Checks whether a Card is a trump card or not
	 * 
	 * @param gameType The game type within the card is checked
	 * @param trumpColor The trump color, only needed in suit games
	 * @return TRUE, when the card is a trump card
	 */
	public boolean isTrump(int gameType, int trumpColor) {
		
		boolean result = false;
		
		switch(gameType) {
		
			case SkatConstants.SUIT:
				
				result = (suit == trumpColor || value == SkatConstants.JACK);
				break;
				
			case SkatConstants.GRAND:
			case SkatConstants.RAMSCH:
			case SkatConstants.RAMSCHGRAND:
				
				result = (value == SkatConstants.JACK);
				break;
		}
		
		return result;
	}

	public boolean equals(Object card) {
		if(!(card instanceof Card)) {
			return false;
		}
		return equals((Card) card);
	}
	
	/**
	 * Implementation of the equals() method
	 * 
	 * @param card The Card to compare
	 * @return TRUE, when the Cards are equal
	 */
	public boolean equals(Card card) {
		
		boolean result = false;
		
		if (card != null &&
			card.getSuit() == suit && card.getValue() == value) {
			
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
		String cardName = "";
		switch (suit) {
		case 0:
			cardName = "C";
			break;
		case 1:
			cardName = "S";
			break;
		case 2:
			cardName = "H";
			break;
		case 3:
			cardName = "D";
			break;
		default:
			cardName = "undefined";
			break;
		}
		cardName = cardName + "-";
		switch (value) {
		case 0:
			cardName = cardName + "7";
			break;
		case 1:
			cardName = cardName + "8";
			break;
		case 2:
			cardName = cardName + "9";
			break;
		case 3:
			cardName = cardName + "Q";
			break;
		case 4:
			cardName = cardName + "K";
			break;
		case 5:
			cardName = cardName + "10";
			break;
		case 6:
			cardName = cardName + "A";
			break;
		case 7:
			cardName = cardName + "J";
			break;
		default:
			cardName = "undefined";
			break;
		}
		return cardName;
	}

	private int suit;

	private int suitValue;

	private int value;

	private int calcValue;

	private int suitGrandValue;

	private int nullValue;

	private int ramschValue;
}