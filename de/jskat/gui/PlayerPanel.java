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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData.GameState;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Panel for showing informations about opponents
 */
class PlayerPanel extends HandPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(PlayerPanel.class);

	private CardPanel lastClickedCardPanel;
	private GameState gameState;

	/**
	 * @see HandPanel#HandPanel(SkatTablePanel, JSkatGraphicRepository)
	 */
	PlayerPanel(SkatTablePanel newParent, JSkatGraphicRepository jskatBitmaps, int maxCards) {
		
		super(newParent, jskatBitmaps, maxCards);
		this.showCards();
	}

	CardPanel getLastClickedCardPanel() {
		
		return this.lastClickedCardPanel;
	}

	void setGameState(GameState newGameState) {
		this.gameState = newGameState;
	}

	GameState getGameState() {
		return this.gameState;
	}
}
