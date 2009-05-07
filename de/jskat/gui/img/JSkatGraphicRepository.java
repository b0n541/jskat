/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.gui.img;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.JSkatOptions;
import de.jskat.util.Rank;
import de.jskat.util.Suit;

/**
 * Repository that holds all images used in JSkat
 */
public class JSkatGraphicRepository {

	private static Log log = LogFactory.getLog(JSkatGraphicRepository.class);

	/** 
	 * Creates a new instance of JSkatGraphicRepository 
	 *
	 * @param jskatOptions Current JSkatOptions 
	 */
	public JSkatGraphicRepository(JSkatOptions jskatOptions) {

		this.cardFace = jskatOptions.getCardFace();
		this.tracker = new MediaTracker(new Canvas());
		this.skatTable = Toolkit.getDefaultToolkit().getImage(
				ClassLoader
						.getSystemResource("de/jskat/gui/img/gui/skatTable.png")); //$NON-NLS-1$
		this.tracker.addImage(this.skatTable, 0);
		this.jskatLogo = Toolkit.getDefaultToolkit().getImage(
				ClassLoader
						.getSystemResource("de/jskat/gui/img/gui/jskatLogo.png")); //$NON-NLS-1$
		this.tracker.addImage(this.jskatLogo, 0);
		try {
			this.tracker.waitForID(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.debug("Bitmaps for JSkat logo and skat table loaded...");

		this.icons = new ArrayList<ArrayList<Image>>();
		loadIcons();

		log.debug("Bitmaps for icons loaded...");

		this.cards = new ArrayList<ArrayList<Image>>();
		loadCards(this.cardFace);

		log.debug("Bitmaps for cards loaded...");
	}

	/**
	 * Loads all icons
	 */
	public void loadIcons() {

		// for all icons
		for (Icon icon : Icon.values()) {

			// new array list for all sizes
			this.icons.add(new ArrayList<Image>());
			
			// for all sizes
			for (IconSize size : IconSize.values()) {
				
				// add icon
				this.icons.get(icon.ordinal()).add(Toolkit.getDefaultToolkit().getImage(
						ClassLoader.getSystemResource("de/jskat/gui/img/gui/" //$NON-NLS-1$
								+ icon.toString().toLowerCase() + "_" + size.toString().toLowerCase() + ".png")));
				this.tracker.addImage(this.icons.get(icon.ordinal()).get(size.ordinal()), 1);
			}
		}

		try {
			this.tracker.waitForID(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load all card images
	 * 
	 * @param cardType
	 *            The directory name for the card set to be loaded
	 */
	public void loadCards(CardFaces cardFace) {
		
		for (Suit suit : Suit.values()) {

			this.cards.add(new ArrayList<Image>());
			
			for (Rank rank : Rank.values()) {

				this.cards.get(suit.ordinal()).add(Toolkit.getDefaultToolkit().getImage(
						ClassLoader.getSystemResource("de/jskat/gui/img/cards/"
								+ cardFace.toString().toLowerCase() + "/gnome/" + suit.shortString() + "-" + rank.shortString() + ".gif")));
				
				this.tracker.addImage(this.cards.get(suit.ordinal()).get(rank.ordinal()), 2);
			}
		}

		this.cardBack = Toolkit.getDefaultToolkit().getImage(
				ClassLoader.getSystemResource("de/jskat/gui/img/cards/" + cardFace.toString().toLowerCase()
						+ "/jskat/back.gif"));
		this.tracker.addImage(this.cardBack, 2);

		try {
			this.tracker.waitForID(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets an icon image
	 * 
	 * @param iconNo
	 *            The icon code
	 * @param bigSmall
	 *            code for big or small icons
	 * @return The icon image
	 */
	public Image getIconImage(Icon icon, IconSize size) {

		try {
			return this.icons.get(icon.ordinal()).get(size.ordinal());
		} catch (IndexOutOfBoundsException exc) {
			return null;
		}
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
	public Image getCardImage(Suit suit, Rank value) {

		Image result = null;
		
		if (suit != null && value != null) {

			result = this.cards.get(suit.ordinal()).get(value.ordinal());
		}
		else {
		
			result = this.cardBack;
		}
		
		return result;
	}

	/**
	 * Gets the image for the skat table
	 * 
	 * @return The image for the skat table
	 */
	public Image getSkatTableImage() {

		return this.skatTable;
	}

	/**
	 * Gets the image for the JSkat logo
	 * 
	 * @return The image for the JSkat logo
	 */
	public Image getJSkatLogoImage() {

		return this.jskatLogo;
	}

	private CardFaces cardFace;

	private MediaTracker tracker;

	private Image skatTable;

	private ArrayList<ArrayList<Image>> cards;

	private Image cardBack;

	private ArrayList<ArrayList<Image>> icons;

	private Image jskatLogo;

	/**
	 * Holds all icon types
	 */
	public enum Icon {
		/**
		 * About
		 */
		ABOUT,
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
		 * Start series
		 */
		START_SERIES,
		/**
		 * Pause series
		 */
		PAUSE,
		/**
		 * Connect ISS
		 */
		CONNECT_ISS;
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
}
