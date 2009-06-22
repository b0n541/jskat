/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.Player;

class TrickPlayPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private TrickPanel trickPanel;

	TrickPlayPanel(JSkatGraphicRepository jskatBitmaps) {
		
		initPanel(jskatBitmaps);
	}
	
	private void initPanel(JSkatGraphicRepository jskatBitmaps) {
		
		this.setLayout(new MigLayout("fill, fill, fill")); //$NON-NLS-1$
		this.trickPanel = new TrickPanel(jskatBitmaps);
		this.add(this.trickPanel, "growx, growy, center"); //$NON-NLS-1$
	}
	
	/**
	 * Clears all cards of the trick
	 */
	void clearCards() {
		
		this.trickPanel.clearCards();
	}
	
	/**
	 * Sets a card in the trick panel
	 * 
	 * @param player Player position
	 * @param card Card
	 */
	void setCard(Player player, Card card) {
		
		this.trickPanel.addCard(player, card);
	}

	/**
	 * Sets the fore hand player for a trick
	 * 
	 * @param trickForeHand Trick fore hand
	 */
	void setTrickForeHand(Player trickForeHand) {
		
		this.trickPanel.setTrickForeHand(trickForeHand);
	}

	void setUserPosition(Player userPosition) {
		
		this.trickPanel.setUserPosition(userPosition);
	}
}
