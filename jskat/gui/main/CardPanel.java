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
import jskat.share.SkatConstants;

/**
 * Panel for showing a Card
 */
public class CardPanel extends JPanel implements Observer {

	static Logger log = Logger.getLogger(CardPanel.class);

	private static final long serialVersionUID = 8931526586921335864L;

	/**
	 * Creates a new instance of CardPanel
	 * 
	 * @param holdingPanel 
	 * 			  CardHoldingPanel where the CardPanel is hold
	 * @param jskatBitmaps
	 *            Graphic repository that holds all images used in JSkat
	 * @param showBack
	 *            TRUE if the Card should hide its face
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

		if (suit != null && rank != null) {

			if (showBack) {

				g.drawImage(jskatBitmaps.getCardImage(null, null), 0, 0, this);

			} else {

				g.drawImage(jskatBitmaps.getCardImage(suit, rank), 0, 0, this);
			}
		}
	}

	/**
	 * Sets the card suit and value
	 * 
	 * @param newSuit
	 *            New suit of the card
	 * @param newRank
	 *            New rank of the card
	 */
	public void setCard(SkatConstants.Suits newSuit, SkatConstants.Ranks newRank) {

		suit = newSuit;
		rank = newRank;
	}

	/**
	 * Flips the card
	 */
	public void flipCard() {

		if (showBack) {

			showCard();
		} 
		else {

			hideCard();
		}
	}

	/**
	 * Shows the card
	 */
	public void showCard() {
		
		showBack = false;
	}
	
	/**
	 * Hides the card
	 */
	public void hideCard() {
		
		showBack = true;
	}
	
	/**
	 * Gets the suit of the card
	 * 
	 * @return ID of the suit
	 */
	public SkatConstants.Suits getSuit() {

		return suit;
	}

	/**
	 * Gets the rank of the card
	 * 
	 * @return The rank of the card
	 */
	public SkatConstants.Ranks getRank() {

		return rank;
	}

	/**
	 * Tells the JSkatMaster when the panel was clicked by the user
	 */
	public void cardClicked() {

		cardHoldingPanel.cardPanelClicked(suit, rank);
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable observ, Object obj) {

		// log.debug("UPDATE " + observ + ": " + obj + " has changed...");

		if (observ instanceof JSkatGraphicRepository) {

			repaint();
		}
	}

	private CardHoldingPanel cardHoldingPanel;

	private JSkatGraphicRepository jskatBitmaps;

	private SkatConstants.Suits suit;

	private SkatConstants.Ranks rank;

	private boolean showBack = true;
}
