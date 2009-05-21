/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

 */

package de.jskat.ai.mjl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Rank;
import de.jskat.util.SkatConstants;
import de.jskat.util.Suit;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 * 
 */
public class Helper {

	private static Log log = LogFactory.getLog(Helper.class);

	/**
	 * Checks whether the current trick would be won by the single player, so
	 * that the AIPlayer can decide which card to play
	 * 
	 * @param trick
	 *            All the necessary trick infos
	 * @return true, if the single player would win the trick
	 */
	// public static boolean isSinglePlayerWin(CardList trick, int trump, int
	// gameType, int singlePlayerPos) {
	public static boolean isSinglePlayerWin(TrickInfo trick) {
		if (trick.getTrick().size() < 2)
			return false;
		if (trick.getTrick().size() < trick.getSinglePlayerPos() - 1)
			return false;
		if (trick.getSinglePlayerPos() == 0) {
			if (trick.getCard(0).beats(trick.getGameInfo().getGameType(), trick.getCard(1)))
				return true;
			else
				return false;
		} else if (trick.getSinglePlayerPos() == 1) {
			if (trick.getCard(1).beats(trick.getGameInfo().getGameType(), trick.getCard(0)))
				return true;
			else
				return false;
		} else {
			log.warn("Request for wrong singlePlayerPos ("
					+ trick.getSinglePlayerPos() + ")!");
			return false;
		}
	}

	/**
	 * Decides whether a player is able to beat a certain card
	 * 
	 * @param cards
	 *            hand of the player
	 * @param cardToBeat
	 *            card that should be beaten
	 * @param trump
	 *            current trump color
	 * @param initialCard
	 *            initial card of the trick
	 * @param gameType
	 *            game type
	 * @return true, if <b>cards</b> contain a card that can beat the
	 *         <b>cardToBeat</b>
	 */
	public static int isAbleToBeat(SkatRules rules, CardList cards,
			Card cardToBeat, Suit trump, Card initialCard,
			GameType gameType) {
		int result = -1;
//		for (int i = 0; i < cards.size(); i++) {
//			if (rules
//					.isCardAllowed(cards.get(i), cards, initialCard, trump)) {
//				if (cards.get(i).beats(cardToBeat, gameType, trump,
//						initialCard)) {
//					log.debug(cards.get(i) + " can beat " + cardToBeat
//							+ ".");
//					result = i;
//					break;
//				}
//			}
//		}
		return result;
	}

	/**
	 * Decides whether a player is able to match a certain initial card
	 * 
	 * @param cards
	 * @param trump
	 * @param initialCard
	 * @param gameType
	 * @return true if there is at least one card in the hand that can match
	 *         <b>initialCard</b>
	 */
	public static boolean isAbleToMatch(SkatRules rules, CardList cards,
			Suit trump, Card initialCard,
			GameType gameType) {
		boolean result = false;
//		for (int i = 0; i < cards.size(); i++) {
//			boolean sameSuit = (cards.get(i).getSuit() == initialCard
//					.getSuit());
//			if (rules
//					.isCardAllowed(cards.get(i), cards, initialCard, trump)) {
//				if (gameType != GameType.NULL) {
//					if (cards.get(i).isTrump(gameType, trump)
//							&& initialCard.isTrump(gameType, trump)) {
//						result = true;
//					} else if (!cards.get(i).isTrump(gameType, trump)
//							&& !initialCard.isTrump(gameType, trump) && sameSuit) {
//						result = true;
//					}
//				} else if (sameSuit) {
//					result = true;
//				}
//
//			}
//			if (result)
//				break;
//		}
		return result;
	}

	/**
	 * Gets the highest trump card out of a given hand
	 * 
	 * @param cards
	 *            a hand
	 * @param currTrump
	 *            the current trump color
	 * @return index of the highest trump, 0 if there is no trump
	 */
	public static int getHighestTrump(CardList cards,
			Suit currTrump) {
//		if (cards.size() < 1)
//			return 0;
		int index = 0;
//		for (int i = 1; i < cards.size(); i++) {
//			if (cards.get(i).beats(cards.get(index),
//					GameType.SUIT, currTrump, cards.get(i)))
//				index = i;
//		}
		return index;
	}

	/**
	 * Checks, whether there is at least one trump card in a given hand
	 * 
	 * @param cards
	 *            a hand
	 * @param currTrump
	 *            trump color
	 * @return true, if there is at least one trump card in the hand
	 */
	public static boolean hasTrump(CardList cards,
			Suit currTrump) {
		return false;
//		return (cards.hasTrump(GameType.SUIT, currTrump));
	}

	/**
	 * Gets the game multiplier
	 * 
	 * @param cards
	 *            a hand
	 * @return multiplier (only positive values)
	 */
	public static int getMultiplier(CardList cards) {

		// TODO (js) this might be a candidate for SkatRules

		int multiplier = 2;
		if (cards.contains(Card.CJ)) {
			// game was played with jacks
			if (cards.contains(Card.SJ)) {
				multiplier++;
				if (cards.contains(Card.HJ)) {
					multiplier++;
					if (cards.contains(Card.DJ)) {
						multiplier++;
					}
				}
			}
		} else {
			// game was played without jacks
			if (!cards.contains(Card.SJ)) {
				multiplier++;
				if (!cards.contains(Card.HJ)) {
					multiplier++;
					if (!cards.contains(Card.DJ)) {
						multiplier++;
					}
				}
			}
		}
		return multiplier;
	}

	/**
	 * Converts a hand's and the skat's cards of a certain suit to a binary
	 * stream
	 * 
	 * @param cards
	 *            a hand
	 * @param skat
	 *            the skat
	 * @param suit
	 *            only cards of this suit are considered
	 * @return binary value of the available cards
	 */
	public static int suitCardsToBinaryWithSkat(CardList cards,
			CardList skat, Suit suit) {
		int counter = 0;
		if (cards.contains(Card.getCard(suit, Rank.SEVEN))
				|| skat.contains(Card.getCard(suit, Rank.SEVEN)))
			counter += 1;
		if (cards.contains(Card.getCard(suit, Rank.EIGHT))
				|| skat.contains(Card.getCard(suit, Rank.EIGHT)))
			counter += 2;
		if (cards.contains(Card.getCard(suit, Rank.NINE))
				|| skat.contains(Card.getCard(suit, Rank.NINE)))
			counter += 4;
		if (cards.contains(Card.getCard(suit, Rank.QUEEN))
				|| skat.contains(Card.getCard(suit, Rank.QUEEN)))
			counter += 8;
		if (cards.contains(Card.getCard(suit, Rank.KING))
				|| skat.contains(Card.getCard(suit, Rank.KING)))
			counter += 16;
		if (cards.contains(Card.getCard(suit, Rank.TEN))
				|| skat.contains(Card.getCard(suit, Rank.TEN)))
			counter += 32;
		if (cards.contains(Card.getCard(suit, Rank.ACE))
				|| skat.contains(Card.getCard(suit, Rank.ACE)))
			counter += 64;
		if (cards.contains(Card.getCard(suit, Rank.JACK))
				|| skat.contains(Card.getCard(suit, Rank.JACK)))
			counter += 128;
		return counter;
	}

	/**
	 * Converts a hand's cards of a certain suit to a binary stream
	 * 
	 * @param cards
	 *            a hand
	 * @param suit
	 *            only cards of this suit are considered
	 * @return binary value of the available cards
	 */
	public static int suitCardsToBinary(CardList cards,
			Suit suit) {
		int counter = 0;
		if (cards.contains(Card.getCard(suit, Rank.SEVEN)))
			counter += 1;
		if (cards.contains(Card.getCard(suit, Rank.EIGHT)))
			counter += 2;
		if (cards.contains(Card.getCard(suit, Rank.NINE)))
			counter += 4;
		if (cards.contains(Card.getCard(suit, Rank.QUEEN)))
			counter += 8;
		if (cards.contains(Card.getCard(suit, Rank.KING)))
			counter += 16;
		if (cards.contains(Card.getCard(suit, Rank.TEN)))
			counter += 32;
		if (cards.contains(Card.getCard(suit, Rank.ACE)))
			counter += 64;
		if (cards.contains(Card.getCard(suit, Rank.JACK)))
			counter += 128;
		return counter;
	}

	/**
	 * Converts a hand's cards of a certain suit to a binary stream for a null
	 * game (which means that jacks are also considered)
	 * 
	 * @param cards
	 *            a hand
	 * @param suit
	 *            only cards of this suit are considered
	 * @return binary value of the available cards
	 */
	public static int suitCardsToBinaryNullGame(CardList cards,
			Suit suit) {
		int counter = 0;
		if (cards.contains(Card.getCard(suit, Rank.SEVEN)))
			counter += 1;
		if (cards.contains(Card.getCard(suit, Rank.EIGHT)))
			counter += 2;
		if (cards.contains(Card.getCard(suit, Rank.NINE)))
			counter += 4;
		if (cards.contains(Card.getCard(suit, Rank.TEN)))
			counter += 8;
		if (cards.contains(Card.getCard(suit, Rank.JACK)))
			counter += 16;
		if (cards.contains(Card.getCard(suit, Rank.QUEEN)))
			counter += 32;
		if (cards.contains(Card.getCard(suit, Rank.KING)))
			counter += 64;
		if (cards.contains(Card.getCard(suit, Rank.ACE)))
			counter += 128;
		return counter;
	}

	/**
	 * Gets a binary representation of the jacks in the given hand
	 * 
	 * @param cards
	 *            a hand
	 * @return binary value of the available jacks
	 */
	public static int getJacks(CardList cards) {
		int counter = 0;
		if (cards.contains(Card.CJ))
			counter += 1;
		if (cards.contains(Card.SJ))
			counter += 2;
		if (cards.contains(Card.HJ))
			counter += 4;
		if (cards.contains(Card.DJ))
			counter += 8;
		return counter;
	}

	/**
	 * Gets the number of jacks in the given hand
	 * 
	 * @param cards
	 *            a hand
	 * @return number of jacks
	 */
	public static int countJacks(CardList cards) {
		int counter = 0;
		if (cards.contains(Card.CJ))
			counter++;
		if (cards.contains(Card.SJ))
			counter++;
		if (cards.contains(Card.HJ))
			counter++;
		if (cards.contains(Card.DJ))
			counter++;
		return counter;
	}

	/**
	 * Converts a binary stream to a suit color
	 * 
	 * @param binary
	 *            binary stream (four bits)
	 * @return suit color, -1 if more than one bit is set
	 */
	public static Suit binaryToSuit(int binary) {
		Suit result = null;
		if (!(binary == 1 || binary == 2 || binary == 4 || binary == 8)) {
			log.warn(".binaryToSuit(): warning: more than one suit possible! -->"
							+ binary);
			return result;
		}
		if ((binary & 1) > 0)
			result = Suit.DIAMONDS;
		if ((binary & 2) > 0)
			result = Suit.HEARTS;
		if ((binary & 4) > 0)
			result = Suit.SPADES;
		if ((binary & 8) > 0)
			result = Suit.CLUBS;
		return result;
	}

	/**
	 * Converts the suit value to a suit name (one character) <br>
	 * "D" for Diamonds, "H" for Hearts, "S" for Spades, "C" for Clubs
	 * 
	 * @param suit
	 *            suit value
	 * @return suit name ("x" if not recognized)
	 */
	public static String suitName(Suit suit) {
		if (suit == Suit.DIAMONDS)
			return "D";
		else if (suit == Suit.HEARTS)
			return "H";
		else if (suit == Suit.SPADES)
			return "S";
		else if (suit == Suit.CLUBS)
			return "C";
		else
			return "x";
	}
}
