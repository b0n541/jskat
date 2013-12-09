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

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context panel for Schieberamsch
 */
class SchieberamschContextPanel extends JPanel {

	JSkatGraphicRepository bitmaps = JSkatGraphicRepository.instance();
	JSkatResourceBundle strings = JSkatResourceBundle.instance();

	private static final String GRAND_HAND = "GRAND_HAND"; //$NON-NLS-1$
	private static final String DISCARD = "DISCARD"; //$NON-NLS-1$
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory
			.getLogger(SchieberamschContextPanel.class);

	private final DiscardPanel discardPanel;
	JPanel centerPanel;

	SchieberamschContextPanel(final ActionMap actions,
			final JSkatUserPanel newUserPanel, final int maxCards) {

		setLayout(LayoutFactory.getMigLayout(
				"fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		centerPanel = new JPanel(new CardLayout());
		JPanel grandHandPanel = getGrandHandSchiebeRamschPanel(actions);
		centerPanel.add(grandHandPanel, GRAND_HAND);

		discardPanel = new DiscardPanel(actions, 4);
		centerPanel.add(discardPanel, DISCARD);

		centerPanel.setOpaque(false);
		add(centerPanel, "grow"); //$NON-NLS-1$

		add(new SkatSchiebenPanel(actions, discardPanel), "width 25%"); //$NON-NLS-1$

		setOpaque(false);

		resetPanel();
	}

	// FIXME: same code can be found in class SkatTabelPanel for
	// Contra-Re-Context-Panel
	public JPanel getGrandHandSchiebeRamschPanel(final ActionMap actions) {
		JPanel result = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

		JPanel question = new JPanel();
		JLabel questionIconLabel = new JLabel(new ImageIcon(
				JSkatGraphicRepository.instance().getUserBidBubble()));
		question.add(questionIconLabel);
		JLabel questionLabel = new JLabel(
				strings.getString("want_play_grand_hand")); //$NON-NLS-1$
		questionLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		question.add(questionLabel);
		result.add(question, "center, growx, span 2, wrap"); //$NON-NLS-1$

		final JButton grandHandButton = new JButton(
				actions.get(JSkatAction.PLAY_GRAND_HAND));
		grandHandButton.setIcon(new ImageIcon(bitmaps.getIconImage(Icon.OK,
				IconSize.BIG)));
		grandHandButton.setText(strings.getString("yes")); //$NON-NLS-1$
		grandHandButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
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

		final JButton schieberamschButton = new JButton(
				actions.get(JSkatAction.PLAY_SCHIEBERAMSCH));
		schieberamschButton.setIcon(new ImageIcon(bitmaps.getIconImage(
				Icon.STOP, IconSize.BIG)));
		schieberamschButton.setText(strings.getString("no")); //$NON-NLS-1$
		schieberamschButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					showPanel(DISCARD);
				} catch (IllegalArgumentException except) {
					log.error(except.getMessage());
				}
			}
		});

		JPanel grandHandPanel = new JPanel();
		grandHandPanel.add(grandHandButton);
		grandHandPanel.setOpaque(false);
		result.add(grandHandPanel, "width 50%"); //$NON-NLS-1$

		JPanel schieberamschPanel = new JPanel();
		schieberamschPanel.add(schieberamschButton);
		schieberamschPanel.setOpaque(false);
		result.add(schieberamschPanel, "width 50%"); //$NON-NLS-1$

		result.setOpaque(false);

		return result;
	}

	public void resetPanel() {

		discardPanel.resetPanel();
		showPanel(GRAND_HAND);
	}

	void showPanel(final String panelName) {
		((CardLayout) centerPanel.getLayout()).show(centerPanel, panelName);
	}

	public void removeCard(final Card card) {
		discardPanel.removeCard(card);
	}

	public boolean isHandFull() {
		return discardPanel.isHandFull();
	}

	public void addCard(final Card card) {
		discardPanel.addCard(card);
	}

	public void setSkat(final CardList skat) {
		discardPanel.setSkat(skat);
	}
}
