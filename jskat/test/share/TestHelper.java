/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test.share;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;

public class TestHelper {

	/**
	 * Build a CardVector out of a given string of cards. suit and value must be
	 * separated by any character, as well as each card.<br>
	 * Suits are represented by D (Diamonds), H (Hearts), S (Spades) and C
	 * (Clubs)<br>
	 * Values are represented by A (Ace), T (10), K (King), Q (Queen), J (Jack),
	 * 9, 8 and 7<br>
	 * <b>Example:</b> S-J,D-J,H-A,H-T,C-T
	 * 
	 * @param cards
	 *            string with the CardVector to build
	 * @return a valid CardVector
	 */
	public static CardVector buildDeck(String cards) {
		// TODO (mjl) use new constructor in Card
		System.out.println("cards: [" + cards + "], length=" + cards.length());
		CardVector deck = new CardVector();
		SkatConstants.Suits suit;
		SkatConstants.Ranks rank;
		int i = 0;
		while (cards.length() > i * 4) {
			System.out.println("i=" + i + ", substring of " + cards + " from "
					+ i * 4);

			String card = cards.toUpperCase().substring(i * 4, i * 4 + 3);
			System.out.println("i=" + i + ", card: [" + card + "]");
			suit = convertSuit(card.charAt(0));
			if (suit != null) {
				rank = convertRank(card.charAt(2));
			} else {
				suit = convertSuit(card.charAt(2));
				rank = convertRank(card.charAt(0));
			}
			if (suit != null && rank != null)
				deck.add(new Card(suit, rank));
			i++;
		}

		System.out.println("deck: [" + deck + "]");

		return deck;
	}

	private static SkatConstants.Suits convertSuit(char c) {
		SkatConstants.Suits result = null;
		System.out.println("suit: [" + c + "]");
		switch (c) {
		case 'D':
			result = SkatConstants.Suits.DIAMONDS;
			break;
		case 'H':
			result = SkatConstants.Suits.HEARTS;
			break;
		case 'S':
			result = SkatConstants.Suits.SPADES;
			break;
		case 'C':
			result = SkatConstants.Suits.CLUBS;
			break;
		}
		return result;
	}

	private static SkatConstants.Ranks convertRank(char c) {
		SkatConstants.Ranks result = null;
		System.out.println("value: [" + c + "]");
		switch (c) {
		case 'A':
			result = SkatConstants.Ranks.ACE;
			break;
		case '1':
		case 'T':
			result = SkatConstants.Ranks.TEN;
			break;
		case 'K':
			result = SkatConstants.Ranks.KING;
			break;
		case 'Q':
			result = SkatConstants.Ranks.QUEEN;
			break;
		case 'J':
			result = SkatConstants.Ranks.JACK;
			break;
		case '9':
			result = SkatConstants.Ranks.NINE;
			break;
		case '8':
			result = SkatConstants.Ranks.EIGHT;
			break;
		case '7':
			result = SkatConstants.Ranks.SEVEN;
			break;
		}
		return result;
	}

	public static ArrayList<HashSet<Card>> dealCardset(int selection) {
		
		ArrayList<HashSet<Card>> dealtCards = new ArrayList<HashSet<Card>>();
		
		switch (selection) {
		// One of the "perfect" grand hand card distributions
		// for playing durchmarsch or grand hand
		case 0:
			dealtCards.add(convertCardsToHashSet(buildDeck("Q-C,9-C,8-C,7-C,Q-S,9-S,8-S,7-S,A-H,T-H")));
			dealtCards.add(convertCardsToHashSet(buildDeck("K-H,Q-H,9-H,8-H,7-H,A-D,T-D,K-D,Q-D,9-D")));
			dealtCards.add(convertCardsToHashSet(buildDeck("J-C,J-S,J-H,J-D,A-C,T-C,K-C,A-S,T-S,K-S")));
			dealtCards.add(convertCardsToHashSet(buildDeck("8-D,7-D")));
			break;
			// a distribution for playing a jungfrau ramsch
		case 1:
			dealtCards.add(convertCardsToHashSet(buildDeck("A-C,T-S,A-S,Q-H,K-H,T-H,A-H,9-D,Q-D,K-D")));
			dealtCards.add(convertCardsToHashSet(buildDeck("J-C,J-S,J-H,J-D,9-H,K-S,7-D,8-D,K-C,T-C")));
			dealtCards.add(convertCardsToHashSet(buildDeck("Q-C,9-C,8-C,7-C,Q-S,9-S,8-S,7-S,7-H,8-H")));
			dealtCards.add(convertCardsToHashSet(buildDeck("A-D,T-D")));
			break;
		// a distribution for playing a null ouvert
		case 2:
			dealtCards.add(convertCardsToHashSet(buildDeck("J-H,Q-H,K-H,A-H,7-D,8-D,9-D,T-D,J-D,Q-D")));
			dealtCards.add(convertCardsToHashSet(buildDeck("9-S,T-S,J-S,Q-S,K-S,A-S,7-H,8-H,9-H,T-H")));
			dealtCards.add(convertCardsToHashSet(buildDeck("7-C,8-C,9-C,T-C,J-C,Q-C,K-C,A-C,7-S,8-S")));
			dealtCards.add(convertCardsToHashSet(buildDeck("K-D,A-D")));
			break;
		default:
			throw new IllegalArgumentException("Illegal predefined cardset!");
		}
		
		return dealtCards;
	}

	private static HashSet<Card> convertCardsToHashSet(CardVector cards) {
		
		HashSet<Card> result = new HashSet<Card>();
		Iterator<Card> iter = cards.iterator();
		
		while (iter.hasNext()) {
			
			result.add(iter.next());
		}
		
		return result;
	}
}
