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

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;

/**
 * Context panel for discarding
 */
class SchieberamschContextPanel extends JPanel {

	private static final String GRAND_HAND = "GRAND_HAND";
	private static final String DISCARD = "DISCARD";
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(SchieberamschContextPanel.class);

	private DiscardPanel discardPanel;
	JPanel centerPanel;

	SchieberamschContextPanel(ActionMap actions,
			JSkatGraphicRepository jskatBitmaps, JSkatUserPanel newUserPanel,
			int maxCards) {

		setLayout(new MigLayout("fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		centerPanel = new JPanel(new CardLayout());
		JPanel grandHandPanel = getGrandHandSchiebeRamschPanel(actions);
		grandHandPanel.setOpaque(false);
		centerPanel.add(grandHandPanel, GRAND_HAND);

		discardPanel = new DiscardPanel(actions, 4, true);
		centerPanel.add(discardPanel, DISCARD);

		centerPanel.setOpaque(false);
		add(centerPanel, "grow"); //$NON-NLS-1$

		JPanel blankPanel2 = new JPanel();
		blankPanel2.setOpaque(false);
		add(blankPanel2, "width 25%"); //$NON-NLS-1$

		setOpaque(false);

		resetPanel();
	}

	public JPanel getGrandHandSchiebeRamschPanel(ActionMap actions) {
		JPanel result = new JPanel(new MigLayout("fill")); //$NON-NLS-1$

		final JButton grandHandButton = new JButton(actions.get(JSkatAction.PLAY_GRAND_HAND));
		grandHandButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameAnnouncement ann = getGameAnnouncement();

					e.setSource(ann);
					// fire event again
					grandHandButton.dispatchEvent(e);
				} catch (IllegalArgumentException except) {
					log.error(except.getMessage());
				}
			}

			private GameAnnouncement getGameAnnouncement() {
				GameAnnouncementFactory factory = GameAnnouncement.getFactory();
				factory.setGameType(GameType.GRAND);
				factory.setHand(Boolean.TRUE);
				return factory.getAnnouncement();
			}
		});

		result.add(grandHandButton, "align center, wrap"); //$NON-NLS-1$

		final JButton schiebenButton = new JButton(actions.get(JSkatAction.PLAY_SCHIEBERAMSCH));
		schiebenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showPanel(DISCARD);
				} catch (IllegalArgumentException except) {
					log.error(except.getMessage());
				}
			}
		});
		result.add(schiebenButton, "align center"); //$NON-NLS-1$

		return result;
	}

	public void resetPanel() {

		discardPanel.resetPanel();
		showPanel(GRAND_HAND);
	}

	void showPanel(String panelName) {
		((CardLayout) centerPanel.getLayout()).show(centerPanel, panelName);
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
