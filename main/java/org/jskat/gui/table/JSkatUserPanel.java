package org.jskat.gui.table;

import javax.swing.ActionMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.SkatGameData.GameState;

/**
 * Panel for showing informations about the user
 */
public class JSkatUserPanel extends AbstractHandPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(JSkatUserPanel.class);

	private CardPanel lastClickedCardPanel;
	private GameState gameState;

	/**
	 * @see AbstractHandPanel#AbstractHandPanel(ActionMap, int, boolean)
	 */
	public JSkatUserPanel(ActionMap actions, int maxCards,
			boolean showIssWidgets) {

		super(actions, maxCards, showIssWidgets);
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

	@Override
	void hideCards() {
		// ignore hiding of cards for user panel
	}
}
