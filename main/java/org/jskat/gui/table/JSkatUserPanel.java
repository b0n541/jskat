/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	 * @see AbstractHandPanel#HandPanel(ActionMap, int, boolean)
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
