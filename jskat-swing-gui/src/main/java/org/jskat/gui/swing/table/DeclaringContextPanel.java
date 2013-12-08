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

import org.jskat.gui.JSkatView;
import org.jskat.gui.img.JSkatGraphicRepository;
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

	DeclaringContextPanel(JSkatView view, ActionMap actions,
			JSkatGraphicRepository jskatBitmaps, JSkatUserPanel newUserPanel,
			int maxCards) {

		setLayout(LayoutFactory.getMigLayout(
				"fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		discardPanel = new DiscardPanel(actions, 4);
		add(discardPanel, "grow"); //$NON-NLS-1$

		announcePanel = new GameAnnouncePanel(view, actions, newUserPanel,
				discardPanel);
		add(announcePanel, "width 25%"); //$NON-NLS-1$
		discardPanel.setAnnouncePanel(announcePanel);

		setOpaque(false);

		resetPanel();
	}

	public void resetPanel() {

		discardPanel.resetPanel();
		announcePanel.resetPanel();
	}

	public void removeCard(Card card) {
		discardPanel.removeCard(card);
	}

	public boolean isHandFull() {
		return discardPanel.isHandFull();
	}

	public void addCard(Card card) {
		discardPanel.addCard(card);
	}

	public void setSkat(CardList skat) {

		discardPanel.setSkat(skat);
	}
}
