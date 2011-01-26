/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util.rule;

import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Rank;
import de.jskat.util.Suit;

/**
 * Rules that are in common for suit, grand and ramsch games
 */
public abstract class SuitGrandRamschRules extends AbstractSkatRules {

	/**
	 * @see BasicSkatRules#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Override
	public boolean isCardAllowed(GameType gameType, Card initialCard,
			CardList hand, Card card) {

		boolean result = false;

		if (initialCard == null) {
			// no initial card given --> every card is allowed
			result = true;
		} else if (initialCard.isTrump(gameType)) {

			if (card.isTrump(gameType)) {
				// card must serve trump
				result = true;
			} else if (!hand.hasTrump(gameType)) {
				// no trump on hand --> every card is allowed
				result = true;
			}
		} else if (initialCard.getSuit() == card.getSuit()
				&& !card.isTrump(gameType)) {
			// card must serve suit
			result = true;
		} else if (!hand.hasSuit(gameType, initialCard.getSuit())) {
			// no suit on hand --> every card is allowed
			result = true;
		}

		return result;
	}

	/**
	 * @see BasicSkatRules#isCardBeatsCard(GameType, Card, Card)
	 */
	public boolean isCardBeatsCard(GameType gameType, Card cardToBeat, Card card) {

		boolean result = false;

		if (cardToBeat.isTrump(gameType)) {
			// card to beat is a trump card
			if (card.isTrump(gameType)) {

				if (cardToBeat.getSuitGrandOrder() < card.getSuitGrandOrder()) {
					// card is a trump card too and has higher suit order
					result = true;
				} else if (cardToBeat.getSuitGrandOrder() == card
						.getSuitGrandOrder()) {
					// cards have same suit grand order
					// only possible if two jacks are checked
					if (cardToBeat.getSuit().getSuitOrder() < card.getSuit()
							.getSuitOrder()) {

						result = true;
					}
				}
			}
		} else {
			// card to beat is not a trump card
			if (card.isTrump(gameType)) {
				// card is a trump card
				result = true;
			} else if (cardToBeat.getSuit() == card.getSuit()
					&& cardToBeat.getSuitGrandOrder() < card
							.getSuitGrandOrder()) {
				// cards have the same suit and card has higher order in
				// suit/grand games
				result = true;
			}
		}

		return result;
	}

	/**
	 * @see BasicSkatRules#hasSuit(GameType, CardList, Suit)
	 */
	public boolean hasSuit(GameType gameType, CardList hand, Suit suit) {

		boolean result = false;

		int index = 0;
		while (result == false && index < hand.size()) {

			if (hand.get(index).getSuit() == suit
					&& !hand.get(index).isTrump(gameType)) {

				result = true;
			}

			index++;
		}

		return result;
	}

	/**
	 * Checks whether a card is a trump card
	 * 
	 * @param gameType
	 *            Game type in which the Card is checked
	 * @param card
	 *            Card to be checked
	 * @return TRUE if the card is a trump card
	 */
	public boolean isTrump(GameType gameType, Card card) {

		boolean result = false;

		if (gameType == GameType.GRAND || gameType == GameType.RAMSCH) {

			result = (card.getRank() == Rank.JACK);

		} else {

			boolean isSuitTrump = false;

			// first check for a Jack
			boolean isJack = (card.getRank() == Rank.JACK);

			if (!isJack) {
				// then check the suit
				Suit suit = card.getSuit();

				if (gameType == GameType.CLUBS && suit == Suit.CLUBS
						|| gameType == GameType.SPADES && suit == Suit.SPADES
						|| gameType == GameType.HEARTS && suit == Suit.HEARTS
						|| gameType == GameType.DIAMONDS
						&& suit == Suit.DIAMONDS) {

					isSuitTrump = true;
				}
			}

			result = (isJack || isSuitTrump);
		}

		return result;
	}
}
