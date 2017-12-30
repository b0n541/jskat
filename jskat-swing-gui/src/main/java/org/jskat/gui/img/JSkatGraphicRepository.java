/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.img;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jskat.data.JSkatOptions;
import org.jskat.util.Card;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.ImageView;

/**
 * Repository that holds all images used in JSkat
 */
public class JSkatGraphicRepository {

	private static Logger log = LoggerFactory.getLogger(JSkatGraphicRepository.class);

	public final static JSkatGraphicRepository INSTANCE = new JSkatGraphicRepository();

	private static JSkatOptions options = JSkatOptions.instance();

	private Image skatTable;

	private Map<CardSet, Map<Card, Image>> cards;

	private Map<CardSet, Image> cardBacks;

	private List<List<Image>> awtIcons;
	private Map<Icon, Map<IconSize, javafx.scene.image.Image>> icons;

	private List<Image> flags;

	private Image jskatLogo;

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
		final MediaTracker tracker = new MediaTracker(new Canvas());

		loadImages(tracker);

		log.debug("Bitmaps for JSkat logo and skat table loaded..."); //$NON-NLS-1$

		awtIcons = new ArrayList<List<Image>>();
		icons = new HashMap<>();
		loadIcons(tracker);

		log.debug("Bitmaps for icons loaded..."); //$NON-NLS-1$

		cards = new HashMap<CardSet, Map<Card, Image>>();
		cardBacks = new HashMap<CardSet, Image>();
		loadCards(tracker);

		log.debug("Bitmaps for cards loaded..."); //$NON-NLS-1$

		flags = new ArrayList<>();
		loadFlags(tracker);

		log.debug("Bitmaps for flags loaded..."); //$NON-NLS-1$
	}

	private void loadFlags(final MediaTracker tracker) {
		// for all flags
		for (final Flag flag : Flag.values()) {
			// add flag
			flags.add(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("org/jskat/gui/img/gui/" //$NON-NLS-1$
					+ "flag_" + flag.toString().toLowerCase() + ".png"))); //$NON-NLS-1$ //$NON-NLS-2$
			tracker.addImage(flags.get(flag.ordinal()), 3);
		}

		try {
			tracker.waitForID(3);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void loadImages(final MediaTracker tracker) {
		skatTable = Toolkit.getDefaultToolkit()
				.getImage(ClassLoader.getSystemResource("org/jskat/gui/img/gui/skat_table.png")); //$NON-NLS-1$
		tracker.addImage(skatTable, 0);
		jskatLogo = Toolkit.getDefaultToolkit()
				.getImage(ClassLoader.getSystemResource("org/jskat/gui/img/gui/jskat_logo.png")); //$NON-NLS-1$
		tracker.addImage(jskatLogo, 0);

		bidBubbles = new ArrayList<>();
		bidBubbles.add(Toolkit.getDefaultToolkit()
				.getImage(ClassLoader.getSystemResource("org/jskat/gui/img/gui/bid_left.png"))); //$NON-NLS-1$
		bidBubbles.add(Toolkit.getDefaultToolkit()
				.getImage(ClassLoader.getSystemResource("org/jskat/gui/img/gui/bid_right.png"))); //$NON-NLS-1$
		bidBubbles.add(Toolkit.getDefaultToolkit()
				.getImage(ClassLoader.getSystemResource("org/jskat/gui/img/gui/bid_user.png"))); //$NON-NLS-1$
		tracker.addImage(bidBubbles.get(0), 0);
		tracker.addImage(bidBubbles.get(1), 0);
		tracker.addImage(bidBubbles.get(2), 0);
		try {
			tracker.waitForID(0);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all icons
	 */
	private void loadIcons(final MediaTracker tracker) {

		// for all icons
		for (final Icon icon : Icon.values()) {

			// new array list for all sizes
			awtIcons.add(new ArrayList<>());
			icons.put(icon, new HashMap<>());

			// for all sizes
			for (final IconSize size : IconSize.values()) {

				// add icon
				awtIcons.get(icon.ordinal())
						.add(Toolkit.getDefaultToolkit().getImage(ClassLoader
								.getSystemResource("org/jskat/gui/img/gui/"
										// $NON-NLS-1$
										+ icon.toString().toLowerCase() + '_'
										+ size.toString().toLowerCase()
										+ ".png"))); //$NON-NLS-1$
				tracker.addImage(
						awtIcons.get(icon.ordinal()).get(size.ordinal()),
						1);

				icons.get(icon).put(size, new javafx.scene.image.Image("org/jskat/gui/img/gui/" //$NON-NLS-1$
						+ icon.toString().toLowerCase() + '_' + size.toString().toLowerCase() + ".png")); //$NON-NLS-1$
			}
		}

		try {
			tracker.waitForID(1);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load all card images
	 *
	 * @param cardFace
	 *            The directory name for the card set to be loaded
	 */
	private void loadCards(final MediaTracker tracker) {

		cards.clear();
		for (final CardSet set : CardSet.values()) {

			cards.put(set, new HashMap<Card, Image>());

			for (final Card card : Card.values()) {

				cards.get(set).put(card,
						Toolkit.getDefaultToolkit()
								.getImage(ClassLoader.getSystemResource("org/jskat/gui/img/card/" //$NON-NLS-1$
										+ set.getCardFace().toString().toLowerCase() + "/" //$NON-NLS-1$
										+ getCardSetNameInLowerCase(set) + "/" + getImageFileName(card) + "."
										+ set.getFileType())));

				tracker.addImage(cards.get(set).get(card), 2);
			}

			cardBacks.put(set, Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(
					"org/jskat/gui/img/card/back/" + getCardSetNameInLowerCase(set) + "." + set.getFileType()))); //$NON-NLS-1$
			tracker.addImage(cardBacks.get(set), 2);
		}
		try {
			tracker.waitForID(2);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getImageFileName(final Card card) {

		return (card.getSuit().getShortString() + "-" + card.getRank().getShortString());
	}

	private String getCardSetNameInLowerCase(final CardSet set) {
		return set.getName().toLowerCase().replace(" ", "");
	}

	/**
	 * Gets an icon image
	 *
	 * @param icon
	 *            Icon name
	 * @param size
	 *            Icon size
	 * @return The icon image
	 */
	public Image getIconImage(final Icon icon, final IconSize size) {

		return awtIcons.get(icon.ordinal()).get(size.ordinal());
	}

	/**
	 * Gets an icon image
	 *
	 * @param icon
	 *            Icon
	 * @param size
	 *            Size
	 * @return Icon image
	 */
	public ImageView getImageView(final Icon icon, final IconSize size) {
		return new ImageView(icons.get(icon).get(size));
	}

	/**
	 * Gets the card image
	 *
	 * @param card
	 *            Card
	 * @return The card image
	 */
	public Image getCardImage(final Card card) {

		Image result = null;

		if (card != null) {

			result = cards.get(options.getCardSet()).get(card);
		} else {

			result = cardBacks.get(CardSet.ISS_GERMAN);
		}

		return result;
	}

	/**
	 * Gets a flag image
	 *
	 * @param flag
	 *            Flag
	 * @return Flag image
	 */
	public Image getFlagImage(final Flag flag) {
		return flags.get(flag.ordinal());
	}

	/**
	 * Gets the image for the skat table
	 *
	 * @return The image for the skat table
	 */
	public Image getSkatTableImage() {

		return skatTable;
	}

	/**
	 * Gets the image for the JSkat logo
	 *
	 * @return The image for the JSkat logo
	 */
	public Image getJSkatLogoImage() {

		return jskatLogo;
	}

	public javafx.scene.image.Image getJSkatLogoImageFX() {
		return new javafx.scene.image.Image(getClass().getResourceAsStream("/org/jskat/gui/img/gui/jskat_logo.png"));
	}

	/**
	 * Gets the image for the left opponent bid bubble
	 *
	 * @return Image for the left opponent bid bubble
	 */
	public Image getLeftBidBubble() {
		return bidBubbles.get(0);
	}

	/**
	 * Gets the image for the right opponent bid bubble
	 *
	 * @return Image for the right opponent bid bubble
	 */
	public Image getRightBidBubble() {
		return bidBubbles.get(1);
	}

	/**
	 * Gets the image for the user bid bubble
	 *
	 * @return Image for the user bid bubble
	 */
	public Image getUserBidBubble() {
		return bidBubbles.get(2);
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
		THINKING;
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
				return "48"; //$NON-NLS-1$
			}
		},
		/**
		 * Small
		 */
		SMALL {
			@Override
			public String getSize() {
				return "22"; //$NON-NLS-1$
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
		 * @param languageChar
		 *            Character
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
				result = strings.getString("german"); //$NON-NLS-1$
				break;
			case ENGLISH:
				result = strings.getString("english"); //$NON-NLS-1$
				break;
			case FRENCH:
				result = strings.getString("french"); //$NON-NLS-1$
				break;
			case SPANISH:
				result = strings.getString("spanish"); //$NON-NLS-1$
				break;
			case POLISH:
				result = strings.getString("polish"); //$NON-NLS-1$
				break;
			case CZECH:
				result = strings.getString("czech"); //$NON-NLS-1$
				break;
			}

			return result;
		}
	}
}
