package de.jskat.util;

import java.util.ResourceBundle;

import de.jskat.gui.img.CardFace;

/**
 * Helper class for skat related i18n string resolves
 */
public class SkatResourceBundle {

	public static String getGameType(GameType gameType, ResourceBundle strings,
			CardFace cardFace) {

		String result = null;

		switch (gameType) {
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
			result = getGameTypeStringForCardFace(gameType, strings, cardFace);
			break;
		case NULL:
			result = strings.getString("null"); //$NON-NLS-1$
			break;
		case GRAND:
			result = strings.getString("grand"); //$NON-NLS-1$
			break;
		case RAMSCH:
			result = strings.getString("ramsch"); //$NON-NLS-1$
			break;
		case PASSED_IN:
			result = strings.getString("passed_in"); //$NON-NLS-1$
			break;
		}

		return result;
	}

	public static String getCardStringForCardFace(Card card,
			ResourceBundle strings, CardFace cardFace) {

		return getSuitStringForCardFace(card.getSuit(), strings, cardFace)
				+ " " + getRankStringForCardFace(card.getRank(), strings, cardFace); //$NON-NLS-1$
	}

	public static String getRankStringForCardFace(Rank rank,
			ResourceBundle strings, CardFace cardFace) {

		String result = null;

		switch (cardFace) {
		case FRENCH:
		case TOURNAMENT:
			result = getFrenchRankString(rank, strings);
			break;
		case GERMAN:
			result = getGermanRankString(rank, strings);
			break;
		}
		return result;
	}

	private static String getFrenchRankString(Rank rank, ResourceBundle strings) {

		String result = null;

		switch (rank) {
		case ACE:
			result = strings.getString("ace"); //$NON-NLS-1$
			break;
		case KING:
			result = strings.getString("king"); //$NON-NLS-1$
			break;
		case QUEEN:
			result = strings.getString("queen"); //$NON-NLS-1$
			break;
		case JACK:
			result = strings.getString("jack"); //$NON-NLS-1$
			break;
		case TEN:
			result = strings.getString("ten"); //$NON-NLS-1$
			break;
		case NINE:
			result = strings.getString("nine"); //$NON-NLS-1$
			break;
		case EIGHT:
			result = strings.getString("eight"); //$NON-NLS-1$
			break;
		case SEVEN:
			result = strings.getString("seven"); //$NON-NLS-1$
			break;
		}

		return result;
	}

	private static String getGermanRankString(Rank rank, ResourceBundle strings) {

		String result = null;

		switch (rank) {
		case ACE:
			result = strings.getString("ace_german"); //$NON-NLS-1$
			break;
		case KING:
			result = strings.getString("king_german"); //$NON-NLS-1$
			break;
		case QUEEN:
			result = strings.getString("queen_german"); //$NON-NLS-1$
			break;
		case JACK:
			result = strings.getString("jack_german"); //$NON-NLS-1$
			break;
		case TEN:
			result = strings.getString("ten_german"); //$NON-NLS-1$
			break;
		case NINE:
			result = strings.getString("nine_german"); //$NON-NLS-1$
			break;
		case EIGHT:
			result = strings.getString("eight_german"); //$NON-NLS-1$
			break;
		case SEVEN:
			result = strings.getString("seven_german"); //$NON-NLS-1$
			break;
		}

		return result;
	}

	public static String getSuitStringForCardFace(Suit suit,
			ResourceBundle strings, CardFace cardFace) {

		String result = null;

		switch (cardFace) {
		case FRENCH:
		case TOURNAMENT:
			result = getFrenchSuitString(suit, strings);
			break;
		case GERMAN:
			result = getGermanSuitString(suit, strings);
			break;
		}
		return result;
	}

	private static String getFrenchSuitString(Suit suit, ResourceBundle strings) {

		String result = null;

		switch (suit) {
		case CLUBS:
			result = strings.getString("clubs"); //$NON-NLS-1$
			break;
		case SPADES:
			result = strings.getString("spades"); //$NON-NLS-1$
			break;
		case HEARTS:
			result = strings.getString("hearts"); //$NON-NLS-1$
			break;
		case DIAMONDS:
			result = strings.getString("diamonds"); //$NON-NLS-1$
			break;
		}
		return result;
	}

	private static String getGermanSuitString(Suit suit, ResourceBundle strings) {

		String result = null;

		switch (suit) {
		case CLUBS:
			result = strings.getString("clubs_german"); //$NON-NLS-1$
			break;
		case SPADES:
			result = strings.getString("spades_german"); //$NON-NLS-1$
			break;
		case HEARTS:
			result = strings.getString("hearts_german"); //$NON-NLS-1$
			break;
		case DIAMONDS:
			result = strings.getString("diamonds_german"); //$NON-NLS-1$
			break;
		}
		return result;
	}

	private static String getGameTypeStringForCardFace(GameType gameType,
			ResourceBundle strings, CardFace cardFace) {

		String result = null;

		switch (cardFace) {
		case FRENCH:
		case TOURNAMENT:
			result = getFrenchGameTypeString(gameType, strings);
			break;
		case GERMAN:
			result = getGermanGameTypeString(gameType, strings);
			break;
		}

		return result;
	}

	private static String getGermanGameTypeString(GameType gameType,
			ResourceBundle strings) {

		String result = null;

		switch (gameType) {
		case CLUBS:
			result = strings.getString("clubs_german"); //$NON-NLS-1$
			break;
		case SPADES:
			result = strings.getString("spades_german"); //$NON-NLS-1$
			break;
		case HEARTS:
			result = strings.getString("hearts_german"); //$NON-NLS-1$
			break;
		case DIAMONDS:
			result = strings.getString("diamonds_german"); //$NON-NLS-1$
			break;
		default:
			// other game types not needed here
			break;
		}

		return result;
	}

	private static String getFrenchGameTypeString(GameType gameType,
			ResourceBundle strings) {

		String result = null;

		switch (gameType) {
		case CLUBS:
			result = strings.getString("clubs"); //$NON-NLS-1$
			break;
		case SPADES:
			result = strings.getString("spades"); //$NON-NLS-1$
			break;
		case HEARTS:
			result = strings.getString("hearts"); //$NON-NLS-1$
			break;
		case DIAMONDS:
			result = strings.getString("diamonds"); //$NON-NLS-1$
			break;
		default:
			// other game types not needed here
			break;
		}

		return result;
	}
}
