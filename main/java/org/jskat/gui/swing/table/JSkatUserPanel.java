/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-09
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
package org.jskat.gui.swing.table;

import javax.swing.ActionMap;

import org.jskat.data.SkatGameData.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for showing informations about the user
 */
public class JSkatUserPanel extends AbstractHandPanel {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(JSkatUserPanel.class);

	private CardPanel lastClickedCardPanel;
	private GameState gameState;

	/**
	 * @see AbstractHandPanel#AbstractHandPanel(ActionMap, int, boolean)
	 */
	public JSkatUserPanel(final ActionMap actions, final int maxCards, final boolean showIssWidgets) {

		super(actions, maxCards, showIssWidgets);
		this.showCards();
	}

	CardPanel getLastClickedCardPanel() {

		return this.lastClickedCardPanel;
	}

	void setGameState(final GameState newGameState) {
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
