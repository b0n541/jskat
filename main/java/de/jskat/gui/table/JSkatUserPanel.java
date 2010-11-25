/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import javax.swing.ActionMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData.GameState;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Panel for showing informations about the user
 */
class JSkatUserPanel extends HandPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(JSkatUserPanel.class);

	private CardPanel lastClickedCardPanel;
	private GameState gameState;

	/**
	 * @see HandPanel#HandPanel(ActionMap, JSkatGraphicRepository)
	 */
	JSkatUserPanel(ActionMap actions, JSkatGraphicRepository jskatBitmaps,
			int maxCards) {

		super(actions, jskatBitmaps, maxCards);
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
