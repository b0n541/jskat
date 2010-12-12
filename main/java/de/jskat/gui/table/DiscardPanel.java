/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.CardList;

/**
 * Holds widgets for deciding of looking into skat or playing hand game
 */
class DiscardPanel extends JPanel {

	private static final String LOOK_INTO_SKAT_BUTTON = "LOOK_INTO_SKAT_BUTTON"; //$NON-NLS-1$

	private static final String CARD_PANEL = "CARD_PANEL"; //$NON-NLS-1$

	private static final long serialVersionUID = 1L;

	JSkatGraphicRepository bitmaps;
	ResourceBundle strings;

	private Action lookIntoSkatAction;
	JButton lookIntoSkatButton;
	boolean userLookedIntoSkat = false;

	int maxCardCount = 0;

	CardPanel cardPanel;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Actions for discarding panel
	 * @param jskatBitmaps
	 *            Bitmaps
	 * @param newStrings
	 *            i18n strings
	 * @param newMaxCardCount
	 *            Maximum number of cards
	 */
	public DiscardPanel(ActionMap actions, JSkatGraphicRepository jskatBitmaps,
			ResourceBundle newStrings, int newMaxCardCount) {

		setActionMap(actions);
		bitmaps = jskatBitmaps;
		strings = newStrings;
		maxCardCount = newMaxCardCount;

		initPanel();
	}

	void initPanel() {

		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.black));

		setLayout(new CardLayout());

		cardPanel = new CardPanel(this, bitmaps, false);
		add(cardPanel, CARD_PANEL);

		lookIntoSkatAction = getActionMap().get(JSkatAction.LOOK_INTO_SKAT);
		lookIntoSkatButton = new JButton(lookIntoSkatAction);
		lookIntoSkatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				userLookedIntoSkat = true;
				DiscardPanel.this.showPanel(CARD_PANEL);

				// fire event again
				lookIntoSkatButton.dispatchEvent(e);
			}
		});
		JPanel lookIntoSkatPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		lookIntoSkatPanel.add(lookIntoSkatButton, "center, shrink"); //$NON-NLS-1$
		lookIntoSkatPanel.setOpaque(false);
		add(lookIntoSkatPanel, LOOK_INTO_SKAT_BUTTON);

		setOpaque(false);
		cardPanel.showCards();
	}

	protected void setSkat(CardList skat) {

		addCard(skat.get(0));
		addCard(skat.get(1));

		userLookedIntoSkat = true;
	}

	void addCard(Card card) {

		cardPanel.addCard(card);
	}

	void removeCard(Card card) {

		cardPanel.removeCard(card);
	}

	public void resetPanel() {

		cardPanel.clearCards();
		userLookedIntoSkat = false;
		showPanel(LOOK_INTO_SKAT_BUTTON);
	}

	protected void showPanel(String panelType) {

		((CardLayout) getLayout()).show(DiscardPanel.this, panelType);
	}

	public CardList getDiscardedCards() {

		return (CardList) cardPanel.cards.clone();
	}

	public boolean isUserLookedIntoSkat() {

		return userLookedIntoSkat;
	}

	public boolean isHandFull() {

		return cardPanel.getCardCount() == maxCardCount;
	}
}
