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
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * Context panel for discarding
 */
class DeclaringContextPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private DiscardPanel discardPanel;
	private GameAnnouncePanel announcePanel;

	DeclaringContextPanel(ActionMap actions,
			JSkatGraphicRepository jskatBitmaps, JSkatUserPanel newUserPanel,
			int maxCards) {

		setLayout(new MigLayout("fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		discardPanel = new DiscardPanel(actions, jskatBitmaps, 4);
		add(discardPanel, "grow"); //$NON-NLS-1$

		announcePanel = new GameAnnouncePanel(actions, newUserPanel,
				discardPanel);
		add(announcePanel, "width 25%"); //$NON-NLS-1$

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
