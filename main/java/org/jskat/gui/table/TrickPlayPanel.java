/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
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

import javax.swing.JPanel;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.Player;

import net.miginfocom.swing.MigLayout;

/**
 * Holds a trick panel
 */
// FIXME (jan 07.12.2010) is this class necessary?
class TrickPlayPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private TrickPanel trickPanel;

	TrickPlayPanel(JSkatGraphicRepository jskatBitmaps, double cardScaleFactor,
			boolean randomPlacement) {

		initPanel(jskatBitmaps, cardScaleFactor, randomPlacement);
	}

	private void initPanel(JSkatGraphicRepository jskatBitmaps,
			double cardScaleFactor, boolean randomPlacement) {

		this.setLayout(new MigLayout("fill, fill, fill")); //$NON-NLS-1$
		this.trickPanel = new TrickPanel(jskatBitmaps, cardScaleFactor,
				randomPlacement);
		trickPanel.setOpaque(false);
		this.add(this.trickPanel, "growx, growy, center"); //$NON-NLS-1$

		setOpaque(false);
	}

	/**
	 * Clears all cards of the trick
	 */
	void clearCards() {

		this.trickPanel.clearCards();
	}

	/**
	 * Sets a card in the trick panel
	 * 
	 * @param player
	 *            Player position
	 * @param card
	 *            Card
	 */
	void setCard(Player player, Card card) {

		this.trickPanel.addCard(player, card);
	}

	void setUserPosition(Player userPosition) {

		this.trickPanel.setUserPosition(userPosition);
	}
}
