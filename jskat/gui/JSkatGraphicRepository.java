/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Canvas;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import jskat.share.SkatConstants;
import jskat.data.JSkatOptions;

/**
 * The JSkatGraphicRepository that holds all images used in JSkat
 * 
 * @author Jan Sch&auml;fer <jan.schaefer@b0n541.net>
 */
public class JSkatGraphicRepository extends Observable implements Observer {

	private static final Logger log = Logger
			.getLogger(jskat.gui.JSkatGraphicRepository.class);

	/** Creates a new instance of JSkatGraphicRepository */
	public JSkatGraphicRepository(JSkatOptions jskatOptions) {

		jskatOptions.addObserver(this);
		cardFace = jskatOptions.getCardFace();
		tracker = new MediaTracker(new Canvas());
		skatTable = Toolkit.getDefaultToolkit().getImage(
				ClassLoader
						.getSystemResource("jskat/gui/img/gui/skatTable.png"));
		tracker.addImage(skatTable, 0);
		jskatLogo = Toolkit.getDefaultToolkit().getImage(
				ClassLoader
						.getSystemResource("jskat/gui/img/gui/jskatLogo.png"));
		tracker.addImage(jskatLogo, 0);
		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) {
		}

		log.debug("Bitmaps for JSkat logo and skat table loaded...");

		icons = new Image[PREFERENCES_ICON + 1][2];
		loadIcons();

		log.debug("Bitmaps for icons loaded...");

		cards = new Image[4][8];

		loadIcons(cardFace);

		log.debug("Bitmaps for cards loaded...");
	}

	/**
	 * 
	 * @param cardFace
	 */
	private void loadIcons(int cardFace) {

		if (cardFace == JSkatOptions.CARD_FACE_FRENCH) {
			
			loadCards("french");
		} else if (cardFace == JSkatOptions.CARD_FACE_GERMAN) {
			
			loadCards("german");
		} else {
			
			loadCards("tournament");
		}
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

				icons[i][j] = Toolkit.getDefaultToolkit().getImage(
						ClassLoader.getSystemResource("jskat/gui/img/gui/"
								+ fileName + ".png"));
				tracker.addImage(icons[i][j], 1);
			}
		}

		try {
			tracker.waitForID(1);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Load all card images
	 * 
	 * @param cardType
	 *            The directory name for the card set to be loaded
	 */
	public void loadCards(String cardType) {

		String suitString = new String();
		String valueString = new String();

		for (int i = SkatConstants.CLUBS; i <= SkatConstants.DIAMONDS; i++) {

			switch (i) {
			case SkatConstants.CLUBS:
				suitString = "clubs_";
				break;
			case SkatConstants.SPADES:
				suitString = "spades_";
				break;
			case SkatConstants.HEARTS:
				suitString = "hearts_";
				break;
			case SkatConstants.DIAMONDS:
				suitString = "diamonds_";
				break;
			}

			for (int j = SkatConstants.SEVEN; j <= SkatConstants.JACK; j++) {

				switch (j) {
				case SkatConstants.SEVEN:
					valueString = "07";
					break;
				case SkatConstants.EIGHT:
					valueString = "08";
					break;
				case SkatConstants.NINE:
					valueString = "09";
					break;
				case SkatConstants.TEN:
					valueString = "10";
					break;
				case SkatConstants.QUEEN:
					valueString = "queen";
					break;
				case SkatConstants.KING:
					valueString = "king";
					break;
				case SkatConstants.ACE:
					valueString = "ace";
					break;
				case SkatConstants.JACK:
					valueString = "jack";
					break;
				}

				// --[DEBUG]--//
				// System.out.println(ClassLoader.getSystemResource("jskat/gui/img/cards/"
				// + cardType + "/" + suitString + valueString + ".gif"));
				// --[DEBUG]--//

				cards[i][j] = Toolkit.getDefaultToolkit().getImage(
						ClassLoader.getSystemResource("jskat/gui/img/cards/"
								+ cardType + "/" + suitString + valueString
								+ ".gif"));
				tracker.addImage(cards[i][j], 2);
			}
		}

		cardBack = Toolkit.getDefaultToolkit().getImage(
				ClassLoader.getSystemResource("jskat/gui/img/cards/" + cardType
						+ "/back.gif"));
		tracker.addImage(cardBack, 2);

		try {
			tracker.waitForID(2);

		} catch (InterruptedException e) {
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
			return icons[iconNo][bigSmall];
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
	public Image getCardImage(int suit, int value) {

		if (suit >= 0 && value >= 0) {

			try {
				return cards[suit][value];
			} catch (IndexOutOfBoundsException exc) {
				return null;
			}

		} else {

			return cardBack;
		}
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
	 * 
	 */
	public void update(Observable observ, Object obj) {
		
		// log.debug("UPDATE " + observ + ": " + obj + " has changed...");
		
		if (observ instanceof JSkatOptions) {

			int newCardFace = ((JSkatOptions) observ).getCardFace();

			if (cardFace != newCardFace) {

				cardFace = newCardFace;

				loadIcons(cardFace);
			}
			
			setChanged();
			notifyObservers();
		}
	}

	private int cardFace;

	private MediaTracker tracker;

	private Image skatTable;

	private Image cards[][];

	private Image cardBack;

	private Image icons[][];

	private Image jskatLogo;

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
