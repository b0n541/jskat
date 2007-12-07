/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * 
 * @author Jan Schäfer
 */

// TODO (js) refactor implementation of find() method!!! 

public class CardVector extends Observable {

	/** Creates a new instance of CardVector */
	public CardVector() {

		cards = new Vector<Card>();
	}

	/** Creates a new instance of CardVector from a HashSet of Cards 
	 * @see jskat.data.SkatGameData#getDealtCards() 
	 */
	public CardVector(HashSet<Card> cardSet) {
		cards = new Vector<Card>();
		Iterator<Card> i = cardSet.iterator();
		while(i.hasNext()) {
			Card c = (Card) i.next();
			cards.add(new Card(c.getSuit(), c.getValue()));
		}
	}

	/** 
	 * Adds a Card to the CardVector by creating a new Card object instead of using the reference
	 *
	 * @param card The Card to be added
	 */
	public void addNew(Card card) {

		Card newCard = new Card(card.getSuit(), card.getValue());
		cards.add(newCard);
		
		//log.debug("add card " + card + " " + countObservers() + " observers will be informed.");
		
		setChanged();
		notifyObservers();
	}


	/** 
	 * Adds a Card to the CardVector
	 *
	 * @param card The Card to be added
	 */
	public void add(Card card) {

		cards.add(card);
		
		//log.debug("add card " + card + " " + countObservers() + " observers will be informed.");
		
		setChanged();
		notifyObservers();
	}

	/**
	 * Returns a reference to a Card in the CardVector without removing it
	 *
	 * @param index The Index of the Card to be returned
	 */
	public Card getCard(int index) {

		return (Card) cards.get(index);
	}

	/**
	 * Returns a reference to a Card in the CardVector and removes this card
	 * from the CardVector
	 * 
	 * @param index The index of the Card to remove
	 * @return The Card
	 */
	public Card remove(int index) {
		
		Card removedCard = (Card) cards.remove(index);

		//log.debug("remove card " + removedCard + " " + countObservers() + " observers will be informed.");
		
		setChanged();
		notifyObservers();
		
		return removedCard;
	}

	/**
	 * Returns a reference to a Card in the CardVector and removes this card
	 * from the CardVector
	 * 
	 * @param card The Card to remove
	 * @return The Card
	 */
	public Card remove(Card card) {
		
		int index = find(card);
		
		if(index<0) {
			log.error("Card "+card+" found at index "+index+" --> "+this);
			return null;
		}

		return remove(index);
	}

	/**
	 * Returns a reference to a Card in the CardVector and removes this card
	 * from the CardVector
	 * 
	 * @param suit The suit of the card to remove
	 * @param value The value of the card to remove
	 * @return The Card
	 */
	public Card remove(int suit, int value) {

		int index = find(suit, value);

		return remove(index);
	}

	/**
	 * Searches in the Vector for the given card
	 * 
	 * @param suit The suit of the card
	 * @param value The value of the card
	 * @return TRUE if the card is in the CardVector
	 */
	public boolean contains(int suit, int value) {

		boolean result = false;
		Card currCard = null;

		for (int i = 0; i < cards.size(); i++) {
			
			currCard = getCard(i);
			
			if (currCard.getSuit() == suit 
					&& currCard.getValue() == value) {
			
				result = true;
			}
		}

		return result;
	}

	/**
	 * Searches in the Vector for the given card
	 * 
	 * @param card The Card
	 * @return TRUE if the card is in the CardVector
	 */
	public boolean contains(Card card) {

		return contains(card.getSuit(), card.getValue());
	}

	/**
	 * Searches in the Vector for the given card Returns true if the card is in
	 * the Vector
	 * 
	 * This method doesn't take the game type into account
	 * 
	 * @deprecated
	 * @param suit Suit to be tested
	 * @return TRUE, when the suit is on the hand
	 */
	public boolean hasSuit(int suit) {

		boolean hasCard = false;

		for (int i = 0; i < cards.size(); i++) {
			if (getCard(i).getSuit() == suit) {
				hasCard = true;
			}
		}

		return hasCard;
	}
	
	/**
	 * Tests whether a card with a suit is in the CardVector or not
	 * 
	 * @param gameType Game type of the game played
	 * @param suit Suit color
	 * @return TRUE, when a card with the suit is found
	 */
	public boolean hasSuit(int gameType, int suit) {

		boolean result = false;

		// Got through all cards
		for (int i = 0; i < cards.size(); i++) {

			if (gameType == SkatConstants.NULL
					&& (getCard(i).getSuit() == suit)) {
				
				result = true;
			}
			else if ((gameType == SkatConstants.SUIT
						|| gameType == SkatConstants.GRAND
						|| gameType == SkatConstants.RAMSCH
						|| gameType == SkatConstants.RAMSCHGRAND)
					&& (getCard(i).getSuit() == suit)
					&& (getCard(i).getValue() != SkatConstants.JACK)) {
				
				result = true;
			}
			
			/*
			if (getCard(i).getSuit() == suit
					&& (gameType == SkatConstants.NULL || getCard(i).getValue() != SkatConstants.JACK)) {

				// if Suit, Grand or Ramsch game is played
				// Jacks don't count as suit --> they are trump
				hasCard = true;
			}
			*/
		}

		return result;
	}

	/**
	 * Searches in the Vector for any trump cards. Returns true if at least one
	 * trump card is in the Vector
	 * 
	 * This is only valid for suit games
	 * 
	 * @deprecated
	 * @param trump Current trump color
	 * @return TRUE, when a trump card is found
	 */
	public boolean hasTrump(int trump) {
		
		return (hasSuit(trump)
				|| contains(SkatConstants.CLUBS, SkatConstants.JACK)
				|| contains(SkatConstants.SPADES, SkatConstants.JACK)
				|| contains(SkatConstants.HEARTS, SkatConstants.JACK) || contains(
				SkatConstants.DIAMONDS, SkatConstants.JACK));
	}

	/**
	 * Tests whether a trump card is in the CardVector or not
	 * 
	 * @param gameType Game type of the game played
	 * @param trump Trump color
	 * @return TRUE, when a trump card was found in the CardVector
	 */
	public boolean hasTrump(int gameType, int trump) {
		
		boolean result = false;
		
		for (int i = 0; i < cards.size(); i++) {
		
			if (gameType == SkatConstants.SUIT
				&& (getCard(i).getSuit() == trump
						|| getCard(i).getValue() == SkatConstants.JACK)) {
				
				result = true;
			}
			else if ((gameType == SkatConstants.GRAND
						|| gameType == SkatConstants.RAMSCH
						|| gameType == SkatConstants.RAMSCHGRAND)
					&& (getCard(i).getValue() == SkatConstants.JACK)) {
				
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * Gets the index of a card in the Vector
	 */
	public int getIndexOf(Card c) {
		return getIndexOf(c.getSuit(), c.getValue());
	}

	/**
	 * Gets the index of a card in the Vector
	 */
	public int getIndexOf(int suit, int value) {

		int index = -1;

		if (contains(suit, value)) {

			int currIndex = 0;

			while (index == -1 || currIndex < cards.size()) {

				if (getCard(currIndex).getSuit() == suit
						&& getCard(currIndex).getValue() == value) {

					index = currIndex;
				}

				currIndex++;
			}
		}

		return index;
	}

	/**
	 * Gets the index of a card in the Vector
	 */
	public int getFirstIndexOfSuit(int suit) {

		return getFirstIndexOfSuit(SkatConstants.NULL, suit);
	}

	/**
	 * Gets the index of a card in the Vector
	 */
	public int getFirstIndexOfSuit(int gameType, int suit) {

		int index = -1;

		if (hasSuit(gameType, suit)) {

			int currIndex = 0;

			while (index == -1 || currIndex < cards.size()) {

				if (getCard(currIndex).getSuit() == suit
						&& (gameType == SkatConstants.NULL || getCard(currIndex)
								.getValue() != SkatConstants.JACK)) {

					index = currIndex;
				}

				currIndex++;
			}
		}

		return index;
	}

	/**
	 * Gets the index of a card in the Vector
	 */
	public int getLastIndexOfSuit(int suit) {

		return getLastIndexOfSuit(SkatConstants.NULL, suit);
	}

	/**
	 * Gets the index of a card in the Vector
	 */
	public int getLastIndexOfSuit(int gameType, int suit) {

		int index = -1;

		if (hasSuit(gameType, suit)) {

			int currIndex = size() - 1;

			while (index == -1 || currIndex >= 0) {

				if (getCard(currIndex).getSuit() == suit
						&& (gameType == SkatConstants.NULL || getCard(currIndex)
								.getValue() != SkatConstants.JACK)) {

					index = currIndex;
				}

				currIndex--;
			}
		}

		return index;
	}

	public int getMostFrequentSuitColor() {

		int mostFrequentSuitColor = SkatConstants.CLUBS;
		int highestCardCount = getSuitColorCount(SkatConstants.CLUBS);
		int currentCardCount = 0;

		currentCardCount = getSuitColorCount(SkatConstants.SPADES);

		if (currentCardCount > highestCardCount) {

			highestCardCount = currentCardCount;
			mostFrequentSuitColor = SkatConstants.SPADES;
		}

		currentCardCount = getSuitColorCount(SkatConstants.HEARTS);

		if (currentCardCount > highestCardCount) {

			highestCardCount = currentCardCount;
			mostFrequentSuitColor = SkatConstants.HEARTS;
		}
		currentCardCount = getSuitColorCount(SkatConstants.DIAMONDS);

		if (currentCardCount > highestCardCount) {

			highestCardCount = currentCardCount;
			mostFrequentSuitColor = SkatConstants.DIAMONDS;
		}

		return mostFrequentSuitColor;
	}

	/**
	 * Returns number of cards with a given suit
	 * 
	 * This is only valid for suit games
	 * 
	 * @deprecated
	 * @param suit The suit to search for
	 * @return Number of cards with this suit
	 */
	public int getSuitColorCount(int suit) {

		int count = 0;

		for (int i = 0; i < cards.size(); i++) {

			if (getCard(i).getSuit() == suit
					&& !(getCard(i).getValue() == SkatConstants.JACK))
				count++;
		}

		return count;
	}

	/**
	 * Returns the numer of cards with a given suit dependent on a game type
	 * 
	 * @param gameType The game type
	 * @param suit The suit to search for
	 * @return Number of cards with this suit
	 */
	public int getSuitColorCount(int gameType, int suit) {

		int count = 0;

		for (int i = 0; i < cards.size(); i++) {

			if (gameType == SkatConstants.NULL) {
				
				if (getCard(i).getSuit() == suit) {
					
					count++;
				}
			}
			else if (getCard(i).getSuit() == suit
					&& getCard(i).getValue() != SkatConstants.JACK) {
					
				count++;
			}
		}

		return count;
	}

	/**
	 * Changes two cards in the CardVector Helper function for sorting
	 */
	private void changeCards(int cardOne, int cardTwo) {

		Card helper = getCard(cardOne);
		cards.set(cardOne, getCard(cardTwo));
		cards.set(cardTwo, helper);

		helper = null;
	}

	public void sort(int sortType) {

		sort(sortType, -1);
	}

	/**
	 * Sorts the cards in the Vector according the sort type SkatConstants
	 */
	public void sort(int gameType, int trump) {

		int sortedCards = 0;
		int currentSuit;

		switch (gameType) {

		case SkatConstants.PASSED_IN:
		case SkatConstants.SUIT:
		case SkatConstants.GRAND:
		case SkatConstants.RAMSCH:
		case SkatConstants.RAMSCHGRAND:

			// First find the Jacks
			if (contains(SkatConstants.CLUBS, SkatConstants.JACK)) {
				changeCards(sortedCards, getIndexOf(SkatConstants.CLUBS,
						SkatConstants.JACK));
				sortedCards++;
			}
			if (contains(SkatConstants.SPADES, SkatConstants.JACK)) {
				changeCards(sortedCards, getIndexOf(SkatConstants.SPADES,
						SkatConstants.JACK));
				sortedCards++;
			}
			if (contains(SkatConstants.HEARTS, SkatConstants.JACK)) {
				changeCards(sortedCards, getIndexOf(SkatConstants.HEARTS,
						SkatConstants.JACK));
				sortedCards++;
			}
			if (contains(SkatConstants.DIAMONDS, SkatConstants.JACK)) {
				changeCards(sortedCards, getIndexOf(SkatConstants.DIAMONDS,
						SkatConstants.JACK));
				sortedCards++;
			}

			// then sort all other cards

			// first cycle through the colors for trump cards
			for (currentSuit = SkatConstants.CLUBS; currentSuit<=SkatConstants.DIAMONDS ; currentSuit++) {
				if(currentSuit==trump) {
					for (int i = SkatConstants.ACE; i >= SkatConstants.SEVEN; i--) {
						if (contains(currentSuit, i)) {
							changeCards(sortedCards, getIndexOf(currentSuit, i));
							sortedCards++;
						}
					}
				}
			}
			
			// then cycle through the colors for the remaining colors
			for (currentSuit = SkatConstants.CLUBS; currentSuit<=SkatConstants.DIAMONDS ; currentSuit++) {
				if(currentSuit!=trump) {
					for (int i = SkatConstants.ACE; i >= SkatConstants.SEVEN; i--) {
						if (contains(currentSuit, i)) {
							changeCards(sortedCards, getIndexOf(currentSuit, i));
							sortedCards++;
						}
					}
				}
			}

			if(sortedCards!=cards.size()) {
				log.warn("Not all cards have been sorted: sortedCards="+sortedCards+", cards.size()="+cards.size());
			}

			break;

		case SkatConstants.NULL:

			for (int i = 0; i < this.size() - 1; i++) {
				for (int j = i + 1; j < this.size(); j++) {
					if (getCard(j).getSuit() < getCard(i).getSuit()
							|| (getCard(j).getSuit() == getCard(i).getSuit() && getCard(
									j).getNullValue() >= getCard(i)
									.getNullValue())) {
						log.debug("i=" + i + ", j=" + j + ", " + getCard(i)
								+ " vs. " + getCard(j) + ", cards(1): [" + this
								+ "]");
						changeCards(i, j);
					}
				}
			}
			break;
		}
		
		setChanged();
		notifyObservers();
	}

	/**
	 * Finds the index of a card in the CardVector
	 * 
	 * @param card Card to find
	 * @return Index of the card
	 */
	private int find(Card card) {
		
		return find(card.getSuit(), card.getValue());
	}

	/**
	 * Finds the index of a card in the CardVector
	 * 
	 * @param suit The suit of the card to find
	 * @param value The value of the card to find
	 * @return The index of the card
	 */
	private int find(int suit, int value) {
		
		int result = -1;
		
		for (int i = 0; i < cards.size(); i++) {
			
			Card currCard = cards.get(i);
			
			if (currCard.getSuit() == suit &&
					currCard.getValue() == value) {
				
				if (result < 0) {
					
					result = i;
					
				} else {
					
					log.warn("More than one card found that equals " + currCard
							+ ":" + result + "/" + i);
					
					result = i;
				}
				
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the size of the CardVector
	 */
	public int size() {

		return cards.size();
	}

	/**
	 * Generates a String of all cards in the CardVector
	 * 
	 * @return All cards
	 */
	public String toString() {
		
		StringBuffer output = new StringBuffer();
		int vectorSize = cards.size();
		
		output.append("{");
		
		if (vectorSize > 0) {

			for (int i = 0; i < vectorSize; i++) {
			
				output.append(cards.get(i));
				
				if (i < cards.size() - 1) {
					output.append(", ");
				}
				else {
					output.append("}");
				}
			}
		}
		else {
			output.append("empty}");
		}

		return output.toString();
	}

	public Iterator<Card> iterator() {
		
		return cards.iterator();
	}
	
	/**
	 * Clears the CardVector
	 *
	 */
	public void clear() {
		
		cards.clear();
		
		setChanged();
		notifyObservers();
	}
	
	
	private Vector<Card> cards;

	private static final Logger log = Logger.getLogger(CardVector.class);
}
