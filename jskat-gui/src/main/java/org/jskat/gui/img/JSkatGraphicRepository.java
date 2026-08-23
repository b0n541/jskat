package org.jskat.gui.img;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jskat.control.gui.img.CardSet;
import org.jskat.data.JSkatOptions;
import org.jskat.util.Card;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository that holds all images used in JSkat
 */
public class JSkatGraphicRepository {

    private static final Logger log = LoggerFactory.getLogger(JSkatGraphicRepository.class);

    public final static JSkatGraphicRepository INSTANCE = new JSkatGraphicRepository();

    private static final JSkatOptions options = JSkatOptions.instance();

    private Map<CardSet, Map<Card, Image>> cards;
    private Map<CardSet, Image> cardBacks;
    private Map<Icon, Map<IconSize, Image>> icons;
    private List<Image> flags;
    private List<Image> bidBubbles;

    /**
     * Creates a new instance of JSkatGraphicRepository
     */
    private JSkatGraphicRepository() {

        loadAllJSkatImages();
    }

    public void reloadAllJSkatImages() {
        loadAllJSkatImages();
    }

    private void loadAllJSkatImages() {
        loadBidBubbles();

        icons = new HashMap<>();
        loadIcons();

        log.debug("Bitmaps for icons loaded...");

        cards = new HashMap<>();
        cardBacks = new HashMap<>();
        loadCards();

        log.debug("Bitmaps for cards loaded...");

        flags = new ArrayList<>();
        loadFlags();

        log.debug("Bitmaps for flags loaded...");
    }

    private void loadFlags() {
        for (final Flag flag : Flag.values()) {
            flags.add(loadGuiImage("flag_" + flag.toString().toLowerCase() + ".png"));
        }
    }

    private void loadBidBubbles() {
        bidBubbles = new ArrayList<>();
        bidBubbles.add(loadGuiImage("bid_left.png"));
        bidBubbles.add(loadGuiImage("bid_right.png"));
        bidBubbles.add(loadGuiImage("bid_user.png"));
    }

    /**
     * Loads all icons
     */
    private void loadIcons() {
        for (final Icon icon : Icon.values()) {
            icons.put(icon, new HashMap<>());
            for (final IconSize size : IconSize.values()) {
                icons.get(icon).put(size, loadGuiImage(
                        icon.toString().toLowerCase() + '_' + size.toString().toLowerCase() + ".png"));
            }
        }
    }

    /**
     * Load all card images.
     */
    private void loadCards() {
        for (final CardSet set : CardSet.values()) {
            cards.put(set, new HashMap<>());
            for (final Card card : Card.values()) {
                cards.get(set).put(card,
                        loadImage("/org/jskat/gui/img/card/"
                                + set.getCardFace().toString().toLowerCase() + "/"
                                + getCardSetNameInLowerCase(set) + "/" + getImageFileName(card) + "."
                                + set.getFileType()));
            }
            cardBacks.put(set, loadImage(
                    "/org/jskat/gui/img/card/back/" + getCardSetNameInLowerCase(set) + "." + set.getFileType()));
        }
    }

    public String getImageFileName(final Card card) {

        return card.getSuit().getShortString() + "-" + card.getRank().getShortString();
    }

    private String getCardSetNameInLowerCase(final CardSet set) {
        return set.getCardSetName().toLowerCase().replace(" ", "");
    }

    /**
     * Gets an icon image
     *
     * @param icon Icon
     * @param size Size
     * @return Icon image
     */
    public ImageView getImageView(final Icon icon, final IconSize size) {
        return new ImageView(icons.get(icon).get(size));
    }

    public Image getCardImageFX(final Card card) {
        final CardSet set = options.getCardSet();
        return card == null ? cardBacks.get(set) : cards.get(set).get(card);
    }

    public Image getFlagImageFX(final Flag flag) {
        return flags.get(flag.ordinal());
    }

    public Image getSkatTableImageFX() {
        return loadGuiImage("skat_table.png");
    }

    public Image getJSkatLogoImageFX() {
        return loadGuiImage("jskat_logo.png");
    }

    public Image getLeftBidBubbleFX() {
        return bidBubbles.get(0);
    }

    public Image getRightBidBubbleFX() {
        return bidBubbles.get(1);
    }

    public Image getUserBidBubbleFX() {
        return bidBubbles.get(2);
    }

    private Image loadGuiImage(final String fileName) {
        return loadImage("/org/jskat/gui/img/gui/" + fileName);
    }

    private Image loadImage(final String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    /**
     * Holds all icon types
     */
    public enum Icon {
        /**
         * About
         */
        ABOUT,
        /**
         * Blank
         */
        BLANK,
        /**
         * Exit
         */
        EXIT,
        /**
         * Help
         */
        HELP,
        /**
         * New skat round
         */
        NEW,
        /**
         * Load skat round
         */
        LOAD,
        /**
         * Save
         */
        SAVE,
        /**
         * Save under new name
         */
        SAVE_AS,
        /**
         * First
         */
        FIRST,
        /**
         * Previous
         */
        PREVIOUS,
        /**
         * Next
         */
        NEXT,
        /**
         * Last
         */
        LAST,
        /**
         * Preferences
         */
        PREFERENCES,
        /**
         * Table
         */
        TABLE,
        /**
         * Start series / continue series etc.
         */
        PLAY,
        /**
         * Pause series
         */
        PAUSE,
        /**
         * Connect ISS
         */
        CONNECT_ISS,
        /**
         * Leave table / log out from ISS
         */
        LOG_OUT,
        /**
         * License
         */
        LICENSE,
        /**
         * Close icon for windows and tabs
         */
        CLOSE,
        /**
         * JSkat logo
         */
        JSKAT,
        /**
         * Train Neural Networks
         */
        TRAIN_NN,
        /**
         * OK / Bid / Hold bid
         */
        OK,
        /**
         * Cancel / Pass
         */
        STOP,
        /**
         * Chat
         */
        CHAT,
        /**
         * Chat disabled
         */
        CHAT_DISABLED,
        /**
         * User info
         */
        USER_INFO,
        /**
         * Web / Home page
         */
        WEB,
        /**
         * Clock
         */
        CLOCK,
        /**
         * Invite
         */
        INVITE,
        /**
         * Register
         */
        REGISTER,
        /**
         * White flag
         */
        WHITE_FLAG,
        /**
         * Undo
         */
        UNDO,
        /**
         * REDO
         */
        REDO,
        /**
         * Thinking icon
         */
        THINKING
    }

    /**
     * Holds all icon sizes
     */
    public enum IconSize {
        /**
         * Big
         */
        BIG {
            @Override
            public String getSize() {
                return "48";
            }
        },
        /**
         * Small
         */
        SMALL {
            @Override
            public String getSize() {
                return "22";
            }
        };

        /**
         * Gets a string representing the size of the icon
         *
         * @return Size string
         */
        public abstract String getSize();
    }

    /**
     * Enumeration for flag symbols
     */
    public enum Flag {
        GERMAN, ENGLISH, FRENCH, SPANISH, POLISH, CZECH;

        private final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;

        /**
         * Gets a flag from a character
         *
         * @param languageChar Character
         * @return Flag
         */
        public static Flag valueOf(final char languageChar) {

            Flag result = null;

            switch (languageChar) {
                case 'D':
                    result = GERMAN;
                    break;
                case 'E':
                    result = ENGLISH;
                    break;
                case 'F':
                    result = FRENCH;
                    break;
                case 'S':
                    result = SPANISH;
                    break;
                case 'P':
                    result = POLISH;
                    break;
                case 'C':
                    result = CZECH;
                    break;
            }

            return result;
        }

        /**
         * Gets the language for a flag
         *
         * @return Language
         */
        public String getLanguageForFlag() {
            String result = null;

            switch (this) {
                case GERMAN:
                    result = strings.getString("german");
                    break;
                case ENGLISH:
                    result = strings.getString("english");
                    break;
                case FRENCH:
                    result = strings.getString("french");
                    break;
                case SPANISH:
                    result = strings.getString("spanish");
                    break;
                case POLISH:
                    result = strings.getString("polish");
                    break;
                case CZECH:
                    result = strings.getString("czech");
                    break;
            }

            return result;
        }
    }
}
