/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.gui.main;

import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import jskat.gui.JSkatGraphicRepository;

/**
 * CardPanel for showing a card
 * 
 * @author Jan Schäfer <jan.schaefer@b0n541.net>
 */
public class CardPanel extends JPanel implements Observer {

	static Logger log = Logger.getLogger(CardPanel.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8931526586921335864L;

	/**
	 * Creates a new instance of CardPanel
	 * 
	 * @param jskatBitmaps
	 *            The JSkatGraphicRepository the holds all images used in JSkat
	 * @param showBack
	 *            TRUE if the card should hide its face
	 */
	public CardPanel(CardHoldingPanel holdingPanel, JSkatGraphicRepository jskatBitmaps, boolean showBack) {

		this.cardHoldingPanel = holdingPanel;
		this.jskatBitmaps = jskatBitmaps;
		this.showBack = showBack;
	}

	/**
	 * Overwrites the paintComponent method of JPanel draws the card image on
	 * the CardPanel
	 * 
	 * @param g
	 *            The Graphics environment
	 */
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (suit >= 0 && value >= 0) {

			if (showBack) {

				g.drawImage(jskatBitmaps.getCardImage(-1, -1), 0, 0, this);

			} else {

				g.drawImage(jskatBitmaps.getCardImage(suit, value), 0, 0, this);
			}
		}
	}

	/**
	 * Sets the card suit and value
	 * 
	 * @param newSuit
	 *            ID of the suit
	 * @param newValue
	 *            value of the card
	 */
	public void setCard(int newSuit, int newValue) {

		suit = newSuit;
		value = newValue;
	}

	/**
	 * Flips the card
	 */
	public void flipCard() {

		if (showBack) {

			showBack = false;
		} else {

			showBack = true;
		}
	}

	/**
	 * Gets the suit of the card
	 * 
	 * @return ID of the suit
	 */
	public int getSuit() {

		return suit;
	}

	/**
	 * Gets the value of the card
	 * 
	 * @return The value of the card
	 */
	public int getValue() {

		return value;
	}

	/**
	 * Tells the JSkatMaster when the panel was clicked by the user
	 */
	public void cardClicked() {

		cardHoldingPanel.cardPanelClicked(suit, value);
	}

	/**
	 * 
	 */
	public void update(Observable observ, Object obj) {

		// log.debug("UPDATE " + observ + ": " + obj + " has changed...");

		if (observ instanceof JSkatGraphicRepository) {

			repaint();
		}
	}

	private CardHoldingPanel cardHoldingPanel;

	private JSkatGraphicRepository jskatBitmaps;

	private int suit = -1;

	private int value = -1;

	private boolean showBack = true;
}
