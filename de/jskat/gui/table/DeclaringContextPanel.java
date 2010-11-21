package de.jskat.gui.table;

import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;

/**
 * Context panel for discarding
 */
class DeclaringContextPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private DiscardPanel discardPanel;
	private GameAnnouncePanel announcePanel;

	DeclaringContextPanel(ActionMap actions,
			JSkatGraphicRepository jskatBitmaps, ResourceBundle strings,
			int maxCards) {

		setLayout(new MigLayout("fill", "[shrink][grow][shrink]", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel blankPanel = new JPanel();
		blankPanel.setOpaque(false);
		add(blankPanel, "width 25%"); //$NON-NLS-1$

		discardPanel = new DiscardPanel(actions, jskatBitmaps, maxCards);
		add(discardPanel, "grow"); //$NON-NLS-1$

		announcePanel = new GameAnnouncePanel(actions, strings);
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
}
