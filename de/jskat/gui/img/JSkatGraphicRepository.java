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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.JSkatOptions;
import de.jskat.util.Rank;
import de.jskat.util.SkatConstants;
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

		this.icons = new Image[PREFERENCES_ICON + 1][2];
		loadIcons();

		log.debug("Bitmaps for icons loaded...");

		this.cards = new Image[4][8];

		loadCards(this.cardFace);

		log.debug("Bitmaps for cards loaded...");
	}

	/**
	 * Loads all icons
	 */
	public void loadIcons() {

		String fileName = new String();

		for (int i = ABOUT_ICON; i <= PREFERENCES_ICON; i++) {

			switch (i) {
			case ABOUT_ICON:
				fileName = "about";
				break;
			case EXIT_ICON:
				fileName = "exit";
				break;
			case HELP_ICON:
				fileName = "help";
				break;
			case NEW_ICON:
				fileName = "new";
				break;
			case OPEN_ICON:
				fileName = "open";
				break;
			case SAVE_ICON:
				fileName = "save";
				break;
			case FIRST_ICON:
				fileName = "first";
				break;
			case PREVIOUS_ICON:
				fileName = "previous";
				break;
			case NEXT_ICON:
				fileName = "next";
				break;
			case LAST_ICON:
				fileName = "last";
				break;
			case PREFERENCES_ICON:
				fileName = "preferences";
				break;
			}

			for (int j = BIG_ICON; j <= SMALL_ICON; j++) {

				if (j == SMALL_ICON)
					fileName += "_16";

				this.icons[i][j] = Toolkit.getDefaultToolkit().getImage(
						ClassLoader.getSystemResource("de/jskat/gui/img/gui/"
								+ fileName + ".png"));
				this.tracker.addImage(this.icons[i][j], 1);
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
		
		String cardType;
		
		if (cardFace == CardFaces.FRENCH) {
			
			cardType = "french";
		} 
		else if (cardFace == CardFaces.GERMAN) {
			
			cardType = "german";
		} 
		else {
			
			cardType = "tournament";
		}

		// TODO this is ugly, refactor it!
		String card = null;

		for (Suit currSuit : Suit.values()) {

			for (Rank currRank : Rank.values()) {

				card = currSuit.shortString() + '-' + currRank.shortString();
				
				this.cards[currSuit.getSuitOrder()][currRank.getSuitGrandOrder()] = Toolkit.getDefaultToolkit().getImage(
						ClassLoader.getSystemResource("de/jskat/gui/img/cards/"
								+ cardType + "/gnome/" + card + ".gif"));
				
				this.tracker.addImage(this.cards[currSuit.getSuitOrder()][currRank.getSuitGrandOrder()], 2);
			}
		}

		this.cardBack = Toolkit.getDefaultToolkit().getImage(
				ClassLoader.getSystemResource("de/jskat/gui/img/cards/" + cardType
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
	public Image getIconImage(int iconNo, int bigSmall) {

		try {
			return this.icons[iconNo][bigSmall];
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

			result = this.cards[suit.getSuitOrder()][value.getSuitGrandOrder()];
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

	private Image cards[][];

	private Image cardBack;

	private Image icons[][];

	private Image jskatLogo;

	// TODO move to an enumeration
	/**
	 * Code for the About icon
	 */
	public static final int ABOUT_ICON = 0;

	/**
	 * Code for the Exit icon
	 */
	public static final int EXIT_ICON = 1;

	/**
	 * Code for the Help icon
	 */
	public static final int HELP_ICON = 2;

	/**
	 * Code for the New Skat Round icon
	 */
	public static final int NEW_ICON = 3;

	/**
	 * Code for the Open Skat Round icon
	 */
	public static final int OPEN_ICON = 4;

	/**
	 * Code for the Save Skat Round Icon
	 */
	public static final int SAVE_ICON = 5;

	/**
	 * Code for the First Trick icon
	 */
	public static final int FIRST_ICON = 6;

	/**
	 * Code for the Previous Trick icon
	 */
	public static final int PREVIOUS_ICON = 7;

	/**
	 * Code for the Next Trick icon
	 */
	public static final int NEXT_ICON = 8;

	/**
	 * Code for the Last Trick icon
	 */
	public static final int LAST_ICON = 9;

	/**
	 * Code for the Preferences icon
	 */
	public static final int PREFERENCES_ICON = 10;

	/**
	 * Code for big icons
	 */
	public static final int BIG_ICON = 0;

	/**
	 * Code for small icons
	 */
	public static final int SMALL_ICON = 1;

}
