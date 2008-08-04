/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

 */

package jskat.player.AIPlayerMJL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jskat.share.CardVector;
import jskat.share.Card;
import jskat.share.SkatConstants;
import jskat.share.rules.SkatRules;

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
	// public static boolean isSinglePlayerWin(CardVector trick, int trump, int
	// gameType, int singlePlayerPos) {
	public static boolean isSinglePlayerWin(TrickInfo trick) {
		if (trick.getTrick().size() < 2)
			return false;
		if (trick.getTrick().size() < trick.getSinglePlayerPos() - 1)
			return false;
		if (trick.getSinglePlayerPos() == 0) {
			if (trick.getCard(0).beats(trick.getCard(1),
					trick.getGameInfo().getGameType(),
					trick.getGameInfo().getTrump(), trick.getCard(0)))
				return true;
			else
				return false;
		} else if (trick.getSinglePlayerPos() == 1) {
			if (trick.getCard(1).beats(trick.getCard(0),
					trick.getGameInfo().getGameType(),
					trick.getGameInfo().getTrump(), trick.getCard(0)))
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
	public static int isAbleToBeat(SkatRules rules, CardVector cards,
			Card cardToBeat, SkatConstants.Suits trump, Card initialCard,
			SkatConstants.GameTypes gameType) {
		int result = -1;
		for (int i = 0; i < cards.size(); i++) {
			if (rules
					.isCardAllowed(cards.getCard(i), cards, initialCard, trump)) {
				if (cards.getCard(i).beats(cardToBeat, gameType, trump,
						initialCard)) {
					log.debug(cards.getCard(i) + " can beat " + cardToBeat
							+ ".");
					result = i;
					break;
				}
			}
		}
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
	public static boolean isAbleToMatch(SkatRules rules, CardVector cards,
			SkatConstants.Suits trump, Card initialCard,
			SkatConstants.GameTypes gameType) {
		boolean result = false;
		for (int i = 0; i < cards.size(); i++) {
			boolean sameSuit = (cards.getCard(i).getSuit() == initialCard
					.getSuit());
			if (rules
					.isCardAllowed(cards.getCard(i), cards, initialCard, trump)) {
				if (gameType != SkatConstants.GameTypes.NULL) {
					if (cards.getCard(i).isTrump(gameType, trump)
							&& initialCard.isTrump(gameType, trump)) {
						result = true;
					} else if (!cards.getCard(i).isTrump(gameType, trump)
							&& !initialCard.isTrump(gameType, trump) && sameSuit) {
						result = true;
					}
				} else if (sameSuit) {
					result = true;
				}

			}
			if (result)
				break;
		}
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
	public static int getHighestTrump(CardVector cards,
			SkatConstants.Suits currTrump) {
		if (cards.size() < 1)
			return 0;
		int index = 0;
		for (int i = 1; i < cards.size(); i++) {
			if (cards.getCard(i).beats(cards.getCard(index),
					SkatConstants.GameTypes.SUIT, currTrump, cards.getCard(i)))
				index = i;
		}
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
	public static boolean hasTrump(CardVector cards,
			SkatConstants.Suits currTrump) {
		return (cards.hasTrump(SkatConstants.GameTypes.SUIT, currTrump));
	}

	/**
	 * Gets the game multiplier
	 * 
	 * @param cards
	 *            a hand
	 * @return multiplier (only positive values)
	 */
	public static int getMultiplier(CardVector cards) {

		// TODO (js) this might be a candidate for SkatRules

		int multiplier = 2;
		if (cards.contains(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.JACK)) {
			// game was played with jacks
			if (cards.contains(SkatConstants.Suits.SPADES,
					SkatConstants.Ranks.JACK)) {
				multiplier++;
				if (cards.contains(SkatConstants.Suits.HEARTS,
						SkatConstants.Ranks.JACK)) {
					multiplier++;
					if (cards.contains(SkatConstants.Suits.DIAMONDS,
							SkatConstants.Ranks.JACK)) {
						multiplier++;
					}
				}
			}
		} else {
			// game was played without jacks
			if (!cards.contains(SkatConstants.Suits.SPADES,
					SkatConstants.Ranks.JACK)) {
				multiplier++;
				if (!cards.contains(SkatConstants.Suits.HEARTS,
						SkatConstants.Ranks.JACK)) {
					multiplier++;
					if (!cards.contains(SkatConstants.Suits.DIAMONDS,
							SkatConstants.Ranks.JACK)) {
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
	public static int suitCardsToBinaryWithSkat(CardVector cards,
			CardVector skat, SkatConstants.Suits suit) {
		int counter = 0;
		if (cards.contains(suit, SkatConstants.Ranks.SEVEN)
				|| skat.contains(suit, SkatConstants.Ranks.SEVEN))
			counter += 1;
		if (cards.contains(suit, SkatConstants.Ranks.EIGHT)
				|| skat.contains(suit, SkatConstants.Ranks.EIGHT))
			counter += 2;
		if (cards.contains(suit, SkatConstants.Ranks.NINE)
				|| skat.contains(suit, SkatConstants.Ranks.NINE))
			counter += 4;
		if (cards.contains(suit, SkatConstants.Ranks.QUEEN)
				|| skat.contains(suit, SkatConstants.Ranks.QUEEN))
			counter += 8;
		if (cards.contains(suit, SkatConstants.Ranks.KING)
				|| skat.contains(suit, SkatConstants.Ranks.KING))
			counter += 16;
		if (cards.contains(suit, SkatConstants.Ranks.TEN)
				|| skat.contains(suit, SkatConstants.Ranks.TEN))
			counter += 32;
		if (cards.contains(suit, SkatConstants.Ranks.ACE)
				|| skat.contains(suit, SkatConstants.Ranks.ACE))
			counter += 64;
		if (cards.contains(suit, SkatConstants.Ranks.JACK)
				|| skat.contains(suit, SkatConstants.Ranks.JACK))
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
	public static int suitCardsToBinary(CardVector cards,
			SkatConstants.Suits suit) {
		int counter = 0;
		if (cards.contains(suit, SkatConstants.Ranks.SEVEN))
			counter += 1;
		if (cards.contains(suit, SkatConstants.Ranks.EIGHT))
			counter += 2;
		if (cards.contains(suit, SkatConstants.Ranks.NINE))
			counter += 4;
		if (cards.contains(suit, SkatConstants.Ranks.QUEEN))
			counter += 8;
		if (cards.contains(suit, SkatConstants.Ranks.KING))
			counter += 16;
		if (cards.contains(suit, SkatConstants.Ranks.TEN))
			counter += 32;
		if (cards.contains(suit, SkatConstants.Ranks.ACE))
			counter += 64;
		if (cards.contains(suit, SkatConstants.Ranks.JACK))
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
	public static int suitCardsToBinaryNullGame(CardVector cards,
			SkatConstants.Suits suit) {
		int counter = 0;
		if (cards.contains(suit, SkatConstants.Ranks.SEVEN))
			counter += 1;
		if (cards.contains(suit, SkatConstants.Ranks.EIGHT))
			counter += 2;
		if (cards.contains(suit, SkatConstants.Ranks.NINE))
			counter += 4;
		if (cards.contains(suit, SkatConstants.Ranks.TEN))
			counter += 8;
		if (cards.contains(suit, SkatConstants.Ranks.JACK))
			counter += 16;
		if (cards.contains(suit, SkatConstants.Ranks.QUEEN))
			counter += 32;
		if (cards.contains(suit, SkatConstants.Ranks.KING))
			counter += 64;
		if (cards.contains(suit, SkatConstants.Ranks.ACE))
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
	public static int getJacks(CardVector cards) {
		int counter = 0;
		if (cards.contains(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.JACK))
			counter += 1;
		if (cards
				.contains(SkatConstants.Suits.SPADES, SkatConstants.Ranks.JACK))
			counter += 2;
		if (cards
				.contains(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK))
			counter += 4;
		if (cards.contains(SkatConstants.Suits.DIAMONDS,
				SkatConstants.Ranks.JACK))
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
	public static int countJacks(CardVector cards) {
		int counter = 0;
		if (cards.contains(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.JACK))
			counter++;
		if (cards
				.contains(SkatConstants.Suits.SPADES, SkatConstants.Ranks.JACK))
			counter++;
		if (cards
				.contains(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK))
			counter++;
		if (cards.contains(SkatConstants.Suits.DIAMONDS,
				SkatConstants.Ranks.JACK))
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
	public static SkatConstants.Suits binaryToSuit(int binary) {
		SkatConstants.Suits result = null;
		if (!(binary == 1 || binary == 2 || binary == 4 || binary == 8)) {
			log
					.warn(".binaryToSuit(): warning: more than one suit possible! -->"
							+ binary);
			return result;
		}
		if ((binary & 1) > 0)
			result = SkatConstants.Suits.DIAMONDS;
		if ((binary & 2) > 0)
			result = SkatConstants.Suits.HEARTS;
		if ((binary & 4) > 0)
			result = SkatConstants.Suits.SPADES;
		if ((binary & 8) > 0)
			result = SkatConstants.Suits.CLUBS;
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
	public static String suitName(SkatConstants.Suits suit) {
		if (suit == SkatConstants.Suits.DIAMONDS)
			return "D";
		else if (suit == SkatConstants.Suits.HEARTS)
			return "H";
		else if (suit == SkatConstants.Suits.SPADES)
			return "S";
		else if (suit == SkatConstants.Suits.CLUBS)
			return "C";
		else
			return "x";
	}
}
