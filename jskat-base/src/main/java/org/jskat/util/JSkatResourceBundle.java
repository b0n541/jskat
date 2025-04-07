package org.jskat.util;

import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.SupportedLanguage;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Helper class for skat related i18n string resolves
 */
public class JSkatResourceBundle {

    public final static JSkatResourceBundle INSTANCE = new JSkatResourceBundle();

    private JSkatOptions options = null;
    private ResourceBundle strings = null;

    private JSkatResourceBundle() {

        options = JSkatOptions.instance();
        loadStrings();
    }

    private void loadStrings() {

        Locale locale = null;
        if (SupportedLanguage.ENGLISH.equals(options.getLanguage())) {

            locale = Locale.ENGLISH;

        } else if (SupportedLanguage.GERMAN.equals(options.getLanguage())) {

            locale = Locale.GERMAN;

        } else {

            locale = Locale.getDefault();
        }

        strings = ResourceBundle.getBundle("org/jskat/i18n/jskat_strings",
                locale);
    }

    /**
     * Gets the string resource bundle.
     *
     * @return String resource bundle
     */
    public ResourceBundle getStringResources() {
        return strings;
    }

    /**
     * Reloads the strings<br>
     * e.g. after changing the language or the card face
     */
    public void reloadStrings() {

        loadStrings();
    }

    /**
     * Gets an i18n string
     *
     * @param key Key
     * @return i18n string
     */
    public String getString(final String key) {

        return strings.getString(key);
    }

    /**
     * Gets an i18n string
     *
     * @param key    Key
     * @param params Parameters
     * @return i18n string
     */
    public String getString(final String key, final Object... params) {
        return MessageFormat.format(strings.getString(key), params);
    }

    /**
     * Gets the i18n string for a game type according the current card face
     *
     * @param gameType Game type
     * @return i18n string according the current card face
     */
    public String getGameType(final GameType gameType) {

        String result = null;

        switch (gameType) {
            case CLUBS:
            case SPADES:
            case HEARTS:
            case DIAMONDS:
                result = getGameTypeStringForCardFace(gameType);
                break;
            case NULL:
                result = strings.getString("null");
                break;
            case GRAND:
                result = strings.getString("grand");
                break;
            case RAMSCH:
                result = strings.getString("ramsch");
                break;
            case PASSED_IN:
                result = strings.getString("passed_in");
                break;
        }

        return result;
    }

    /**
     * Gets the i18n string for a card according the current card face
     *
     * @param card Card
     * @return i18n string according the current card face
     */
    public String getCardStringForCardFace(final Card card) {

        return getSuitStringForCardFace(card.getSuit()) + " " + getRankStringForCardFace(card.getRank());
    }

    /**
     * Gets the i18n string for a rank according the current card face
     *
     * @param rank Rank
     * @return i18n string according the current card face
     */
    public String getRankStringForCardFace(final Rank rank) {

        String result = null;

        switch (options.getCardSet().getCardFace()) {
            case FRENCH:
            case TOURNAMENT:
                result = getFrenchRankString(rank);
                break;
            case GERMAN:
                result = getGermanRankString(rank);
                break;
        }
        return result;
    }

    private String getFrenchRankString(final Rank rank) {

        String result = null;

        switch (rank) {
            case ACE:
                result = strings.getString("ace");
                break;
            case KING:
                result = strings.getString("king");
                break;
            case QUEEN:
                result = strings.getString("queen");
                break;
            case JACK:
                result = strings.getString("jack");
                break;
            case TEN:
                result = strings.getString("ten");
                break;
            case NINE:
                result = strings.getString("nine");
                break;
            case EIGHT:
                result = strings.getString("eight");
                break;
            case SEVEN:
                result = strings.getString("seven");
                break;
        }

        return result;
    }

    private String getGermanRankString(final Rank rank) {

        String result = null;

        switch (rank) {
            case ACE:
                result = strings.getString("ace_german");
                break;
            case KING:
                result = strings.getString("king_german");
                break;
            case QUEEN:
                result = strings.getString("queen_german");
                break;
            case JACK:
                result = strings.getString("jack_german");
                break;
            case TEN:
                result = strings.getString("ten_german");
                break;
            case NINE:
                result = strings.getString("nine_german");
                break;
            case EIGHT:
                result = strings.getString("eight_german");
                break;
            case SEVEN:
                result = strings.getString("seven_german");
                break;
        }

        return result;
    }

    /**
     * Gets the i18n string for a suit according the current card face
     *
     * @param suit Suit
     * @return i18n string according the current card face
     */

    public String getSuitStringForCardFace(final Suit suit) {

        String result = null;

        switch (options.getCardSet().getCardFace()) {
            case FRENCH:
            case TOURNAMENT:
                result = getFrenchSuitString(suit);
                break;
            case GERMAN:
                result = getGermanSuitString(suit);
                break;
        }
        return result;
    }

    private String getFrenchSuitString(final Suit suit) {

        String result = null;

        switch (suit) {
            case CLUBS:
                result = strings.getString("clubs");
                break;
            case SPADES:
                result = strings.getString("spades");
                break;
            case HEARTS:
                result = strings.getString("hearts");
                break;
            case DIAMONDS:
                result = strings.getString("diamonds");
                break;
        }
        return result;
    }

    private String getGermanSuitString(final Suit suit) {

        String result = null;

        switch (suit) {
            case CLUBS:
                result = strings.getString("clubs_german");
                break;
            case SPADES:
                result = strings.getString("spades_german");
                break;
            case HEARTS:
                result = strings.getString("hearts_german");
                break;
            case DIAMONDS:
                result = strings.getString("diamonds_german");
                break;
        }
        return result;
    }

    private String getGameTypeStringForCardFace(final GameType gameType) {

        String result = null;

        switch (options.getCardSet().getCardFace()) {
            case FRENCH:
            case TOURNAMENT:
                result = getFrenchGameTypeString(gameType);
                break;
            case GERMAN:
                result = getGermanGameTypeString(gameType);
                break;
        }

        return result;
    }

    private String getGermanGameTypeString(final GameType gameType) {

        String result = null;

        switch (gameType) {
            case CLUBS:
                result = strings.getString("clubs_german");
                break;
            case SPADES:
                result = strings.getString("spades_german");
                break;
            case HEARTS:
                result = strings.getString("hearts_german");
                break;
            case DIAMONDS:
                result = strings.getString("diamonds_german");
                break;
            default:
                // other game types not needed here
                break;
        }

        return result;
    }

    private String getFrenchGameTypeString(final GameType gameType) {

        String result = null;

        switch (gameType) {
            case CLUBS:
                result = strings.getString("clubs");
                break;
            case SPADES:
                result = strings.getString("spades");
                break;
            case HEARTS:
                result = strings.getString("hearts");
                break;
            case DIAMONDS:
                result = strings.getString("diamonds");
                break;
            default:
                // other game types not needed here
                break;
        }

        return result;
    }

    /**
     * Gets the {@link Locale} of the i18n strings
     *
     * @return Locale
     */
    public Locale getLocale() {

        return strings.getLocale();
    }

    /**
     * Gets the i18n for a player position
     *
     * @param position Player position
     * @return i18n for player position
     */
    public Object getPlayerString(final Player position) {
        String result = null;

        switch (position) {
            case FOREHAND:
                result = strings.getString("forehand");
                break;
            case MIDDLEHAND:
                result = strings.getString("middlehand");
                break;
            case REARHAND:
                result = strings.getString("rearhand");
                break;
        }
        return result;
    }
}
