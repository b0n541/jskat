/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.Player;

/**
 * Holds a trick panel
 */
// FIXME (jan 07.12.2010) is this class necessary?
class TrickPlayPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private TrickPanel trickPanel;

	TrickPlayPanel(JSkatGraphicRepository jskatBitmaps, double cardScaleFactor,
			boolean randomPlacement) {

		initPanel(jskatBitmaps, cardScaleFactor, randomPlacement);
	}

	private void initPanel(JSkatGraphicRepository jskatBitmaps,
			double cardScaleFactor, boolean randomPlacement) {

		this.setLayout(new MigLayout("fill, fill, fill")); //$NON-NLS-1$
		this.trickPanel = new TrickPanel(jskatBitmaps, cardScaleFactor,
				randomPlacement);
		trickPanel.setOpaque(false);
		this.add(this.trickPanel, "growx, growy, center"); //$NON-NLS-1$

		setOpaque(false);
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
	 * @param player
	 *            Player position
	 * @param card
	 *            Card
	 */
	void setCard(Player player, Card card) {

		this.trickPanel.addCard(player, card);
	}

	void setUserPosition(Player userPosition) {

		this.trickPanel.setUserPosition(userPosition);
	}
}
