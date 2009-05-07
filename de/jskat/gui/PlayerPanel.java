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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData.GameStates;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Panel for showing informations about opponents
 */
class PlayerPanel extends HandPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(PlayerPanel.class);

	private CardPanel lastClickedCardPanel;
	private GameStates gameState;

	/**
	 * @see HandPanel#HandPanel(SkatTablePanel, JSkatGraphicRepository)
	 */
	PlayerPanel(SkatTablePanel newParent, JSkatGraphicRepository jskatBitmaps, int maxCards) {
		
		super(newParent, jskatBitmaps, maxCards);
		this.showCards();
	}

	/**
	 * @see HandPanel#initPanel()
	 */
	@Override
	void initPanel() {
		
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		setLayout(new MigLayout("fill", "fill", "fill"));
		
		add(this.headerLabel, "wrap"); //$NON-NLS-1$
		
		JPanel cardPanels = new JPanel(new MigLayout("fill, gapx 0", "fill", "fill"));
		for (CardPanel panel : this.panels) {
			
			cardPanels.add(panel, "grow"); //$NON-NLS-1$
		}
		
		add(cardPanels, "grow");
	}

	CardPanel getLastClickedCardPanel() {
		
		return this.lastClickedCardPanel;
	}

	void setGameState(GameStates newGameState) {
		this.gameState = newGameState;
	}

	GameStates getGameState() {
		return this.gameState;
	}
}
