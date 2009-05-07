/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Panel for showing informations about opponents
 */
class OpponentPanel extends HandPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HandPanel#HandPanel(SkatTablePanel, JSkatGraphicRepository)
	 */
	OpponentPanel(SkatTablePanel newParent, JSkatGraphicRepository jskatBitmaps, int maxCards) {

		super(newParent, jskatBitmaps, maxCards);
	}

	/**
	 * @see HandPanel#initPanel()
	 */
	@Override
	void initPanel() {
		
		setLayout(new MigLayout("fill", "fill", "fill"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		
		setBackground(Color.RED);
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		add(this.headerLabel, "wrap"); //$NON-NLS-1$
		
		JPanel cardPanels = new JPanel(new MigLayout("fill, gapx 0, righttoleft", "fill", "fill"));
		for (CardPanel panel : this.panels) {
			
			cardPanels.add(panel, "grow"); //$NON-NLS-1$
		}
		
		add(cardPanels, "grow");
	}
}
