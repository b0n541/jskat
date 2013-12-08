/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing.table;

import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jskat.data.GameSummary;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Player;

class GameOverPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private GameResultPanel gameResultPanel;

	public GameOverPanel(final ActionMap actions, final List<JSkatAction> activeActions) {

		initPanel(actions, activeActions);
	}

	private void initPanel(final ActionMap actions, final List<JSkatAction> activeActions) {

		this.setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "[grow][shrink]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		gameResultPanel = new GameResultPanel();
		panel.add(gameResultPanel, "grow, wrap"); //$NON-NLS-1$

		JPanel buttonPanel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
		for (JSkatAction action : activeActions) {
			buttonPanel.add(new JButton(actions.get(action)), "center, shrink"); //$NON-NLS-1$
		}
		buttonPanel.setOpaque(false);
		panel.add(buttonPanel, "center"); //$NON-NLS-1$

		panel.setOpaque(false);
		this.add(panel, "center"); //$NON-NLS-1$

		setOpaque(false);
	}

	void setUserPosition(final Player player) {

		gameResultPanel.setUserPosition(player);
	}

	void setGameSummary(final GameSummary summary) {

		gameResultPanel.setGameSummary(summary);
	}

	public void resetPanel() {

		gameResultPanel.resetPanel();
	}
}
