/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.10.0-SNAPSHOT
 * Build date: 2011-10-09
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * Context panel for discarding
 */
class SchieberamschContextPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(SchieberamschContextPanel.class);

	private DiscardPanel discardPanel;
	private RamschDiscardingDonePanel ramschDiscardingDonePanel;

	SchieberamschContextPanel(ActionMap actions,
			JSkatGraphicRepository jskatBitmaps, JSkatUserPanel newUserPanel,
			int maxCards) {

		setLayout(new MigLayout("fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		discardPanel = new DiscardPanel(actions, 4);
		add(discardPanel, "grow"); //$NON-NLS-1$

		ramschDiscardingDonePanel = new RamschDiscardingDonePanel(actions, newUserPanel, discardPanel, Boolean.TRUE);
		add(ramschDiscardingDonePanel, "width 25%"); //$NON-NLS-1$

		setOpaque(false);

		resetPanel();
	}

	public void resetPanel() {

		discardPanel.resetPanel();
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
