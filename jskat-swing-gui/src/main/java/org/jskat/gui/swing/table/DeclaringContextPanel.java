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

import javax.swing.ActionMap;
import javax.swing.JPanel;

import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * Context panel for discarding
 */
class DeclaringContextPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final DiscardPanel discardPanel;
	private final GameAnnouncePanel announcePanel;

	DeclaringContextPanel(ActionMap actions, JSkatUserPanel newUserPanel) {

		setLayout(LayoutFactory.getMigLayout(
				"fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		this.discardPanel = new DiscardPanel(actions, 4);
		add(this.discardPanel, "grow"); //$NON-NLS-1$

		this.announcePanel = new GameAnnouncePanel(actions, newUserPanel,
				this.discardPanel);
		add(this.announcePanel, "width 25%"); //$NON-NLS-1$
		this.discardPanel.setAnnouncePanel(this.announcePanel);

		setOpaque(false);

		resetPanel();
	}

	public void resetPanel() {

		this.discardPanel.resetPanel();
		this.announcePanel.resetPanel();
	}

	public void removeCard(Card card) {
		this.discardPanel.removeCard(card);
	}

	public boolean isHandFull() {
		return this.discardPanel.isHandFull();
	}

	public void addCard(Card card) {
		this.discardPanel.addCard(card);
	}

	public void setSkat(CardList skat) {

		this.discardPanel.setSkat(skat);
	}
}
