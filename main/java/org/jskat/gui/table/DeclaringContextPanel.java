package org.jskat.gui.table;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import org.jskat.gui.LayoutFactory;
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

	DeclaringContextPanel(ActionMap actions, JSkatGraphicRepository jskatBitmaps, JSkatUserPanel newUserPanel,
			int maxCards) {

		setLayout(LayoutFactory.getMigLayout("fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		discardPanel = new DiscardPanel(actions, 4);
		add(discardPanel, "grow"); //$NON-NLS-1$

		announcePanel = new GameAnnouncePanel(actions, newUserPanel, discardPanel);
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
