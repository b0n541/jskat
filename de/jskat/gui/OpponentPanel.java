/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.Color;

import javax.swing.BorderFactory;

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

		this.cardPanel = new CardPanel(this, this.bitmaps, true);
		this.cardPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		
		add(this.cardPanel, "grow"); //$NON-NLS-1$
	}
}
