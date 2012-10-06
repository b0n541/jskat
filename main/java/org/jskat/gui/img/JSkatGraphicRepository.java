/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.gui.img;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import org.jskat.data.JSkatOptions;
import org.jskat.util.Card;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Rank;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository that holds all images used in JSkat
 */
public class JSkatGraphicRepository {

	private static Logger log = LoggerFactory
			.getLogger(JSkatGraphicRepository.class);
	private static JSkatGraphicRepository instance = null;

	/**
	 * Gets the instance of the JSkat graphic repository
	 * 
	 * @return Graphic repository
	 */
	public static JSkatGraphicRepository instance() {

		if (instance == null) {

			instance = new JSkatGraphicRepository();
		}

		return instance;
	}

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

		icons = new ArrayList<List<Image>>();
		loadIcons(tracker);

		log.debug("Bitmaps for icons loaded..."); //$NON-NLS-1$

		cards = new ArrayList<List<Image>>();
		loadCards(tracker, JSkatOptions.instance().getCardFace());

		log.debug("Bitmaps for cards loaded..."); //$NON-NLS-1$

		flags = new ArrayList<Image>();
		loadFlags(tracker);

		log.debug("Bitmaps for flags loaded..."); //$NON-NLS-1$
	}

	private void loadFlags(final MediaTracker tracker) {
		// for all flags
		for (final Flag flag : Flag.values()) {
			// add flag
			flags.add(Toolkit
					.getDefaultToolkit()
					.getImage(
							ClassLoader
									.getSystemResource("org/jskat/gui/img/gui/" //$NON-NLS-1$
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
		skatTable = Toolkit
				.getDefaultToolkit()
				.getImage(
						ClassLoader
								.getSystemResource("org/jskat/gui/img/gui/skat_table.png")); //$NON-NLS-1$
		tracker.addImage(skatTable, 0);
		jskatLogo = Toolkit
				.getDefaultToolkit()
				.getImage(
						ClassLoader
								.getSystemResource("org/jskat/gui/img/gui/jskatLogo.png")); //$NON-NLS-1$
		tracker.addImage(jskatLogo, 0);

		bidBubbles = new ArrayList<Image>();
		bidBubbles
				.add(Toolkit
						.getDefaultToolkit()
						.getImage(
								ClassLoader
										.getSystemResource("org/jskat/gui/img/gui/bid_left.png"))); //$NON-NLS-1$
		bidBubbles
				.add(Toolkit
						.getDefaultToolkit()
						.getImage(
								ClassLoader
										.getSystemResource("org/jskat/gui/img/gui/bid_right.png"))); //$NON-NLS-1$
		bidBubbles
				.add(Toolkit
						.getDefaultToolkit()
						.getImage(
								ClassLoader
										.getSystemResource("org/jskat/gui/img/gui/bid_user.png"))); //$NON-NLS-1$
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
			icons.add(new ArrayList<Image>());

			// for all sizes
			for (final IconSize size : IconSize.values()) {

				// add icon
				icons.get(icon.ordinal())
						.add(Toolkit
								.getDefaultToolkit()
								.getImage(
										ClassLoader
												.getSystemResource("org/jskat/gui/img/gui/" //$NON-NLS-1$
														+ icon.toString()
																.toLowerCase()
														+ '_'
														+ size.toString()
																.toLowerCase()
														+ ".png"))); //$NON-NLS-1$
				tracker.addImage(icons.get(icon.ordinal()).get(size.ordinal()),
						1);
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
	private void loadCards(final MediaTracker tracker, final CardFace cardFace) {

		cards.clear();
		for (final Suit suit : Suit.values()) {

			cards.add(new ArrayList<Image>());

			for (final Rank rank : Rank.values()) {

				cards.get(suit.ordinal())
						.add(Toolkit
								.getDefaultToolkit()
								.getImage(
										ClassLoader
												.getSystemResource("org/jskat/gui/img/cards/" //$NON-NLS-1$
														+ cardFace.toString()
																.toLowerCase()
														+ "/iss/" + suit.shortString() + '-' + rank.shortString() + ".gif"))); //$NON-NLS-1$//$NON-NLS-2$

				tracker.addImage(cards.get(suit.ordinal()).get(rank.ordinal()),
						2);
			}
		}

		cardBack = Toolkit
				.getDefaultToolkit()
				.getImage(
						ClassLoader
								.getSystemResource("org/jskat/gui/img/cards/" + cardFace.toString().toLowerCase() //$NON-NLS-1$
										+ "/iss/back.gif")); //$NON-NLS-1$
		tracker.addImage(cardBack, 2);

		try {
			tracker.waitForID(2);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reloads card images for another card face
	 * 
	 * @param cardFace
	 *            Card face
	 */
	public void reloadCards(final CardFace cardFace) {
		final MediaTracker tracker = new MediaTracker(new Canvas());
		loadCards(tracker, cardFace);
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

		try {
			return icons.get(icon.ordinal()).get(size.ordinal());
		} catch (final IndexOutOfBoundsException exc) {
			return null;
		}
	}

	/**
	 * Gets the card image
	 * 
	 * @param The
	 *            card
	 * @return The card image
	 */
	public Image getCardImage(final Card card) {
		return getCardImage(card.getSuit(), card.getRank());
	}

	/**
	 * Gets the card image
	 * 
	 * @param suit
	 *            The suit of the card
	 * @param value
	 *            The value of the card
	 * @return The card image
	 */
	public Image getCardImage(final Suit suit, final Rank value) {

		Image result = null;

		if (suit != null && value != null) {

			result = cards.get(suit.ordinal()).get(value.ordinal());
		} else {

			result = cardBack;
		}

		return result;
	}

	/**
	 * Gets a flag image
	 * 
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

	/**
	 * Gets the image for the left opponent bid bubble
	 * 
	 * @return Image for the left opponent bid bubble
	 */
	public Image getLeftBidBubbleImage() {
		return bidBubbles.get(0);
	}

	/**
	 * Gets the image for the right opponent bid bubble
	 * 
	 * @return Image for the right opponent bid bubble
	 */
	public Image getRightBidBubbleImage() {
		return bidBubbles.get(1);
	}

	/**
	 * Gets the image for the user bid bubble
	 * 
	 * @return Image for the user bid bubble
	 */
	public Image getUserBidBubbleImage() {
		return bidBubbles.get(2);
	}

	private Image skatTable;

	private List<List<Image>> cards;

	private Image cardBack;

	private List<List<Image>> icons;

	private List<Image> flags;

	private Image jskatLogo;

	private List<Image> bidBubbles;

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
		REDO;
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

		private final JSkatResourceBundle strings = JSkatResourceBundle
				.instance();

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
		 * @param flag
		 *            Flag
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
