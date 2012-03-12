package org.jskat.gui.table;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.gui.LayoutFactory;
import org.jskat.gui.action.JSkatAction;
import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * Holds widgets for deciding of looking into skat or playing hand game
 */
class DiscardPanel extends JPanel {

	private static Log log = LogFactory.getLog(DiscardPanel.class);
	private static final String PICK_UP_SKAT_BUTTON = "PICK_UP_SKAT_BUTTON"; //$NON-NLS-1$

	private static final String CARD_PANEL = "CARD_PANEL"; //$NON-NLS-1$

	private static final long serialVersionUID = 1L;

	private Action pickUpSkatAction;
	private JButton pickUpSkatButton;
	private boolean userPickedUpSkat = false;

	private int maxCardCount = 0;

	private CardPanel cardPanel;

	private GameAnnouncePanel announcePanel;

	/**
	 * Constructor
	 * 
	 * @param actions
	 *            Actions for discarding panel
	 * @param newMaxCardCount
	 *            Maximum number of cards
	 */
	public DiscardPanel(ActionMap actions, int newMaxCardCount) {

		setActionMap(actions);
		maxCardCount = newMaxCardCount;

		initPanel();
	}

	void initPanel() {

		setBackground(Color.WHITE);

		setLayout(new CardLayout());

		cardPanel = new CardPanel(this, 0.75, false);
		add(cardPanel, CARD_PANEL);

		pickUpSkatAction = getActionMap().get(JSkatAction.PICK_UP_SKAT);
		pickUpSkatButton = new JButton(pickUpSkatAction);
		pickUpSkatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				log.debug("user picked up skat");
				userPickedUpSkat = true;
				if (announcePanel != null) {
					announcePanel.setUserPickedUpSkat(true);
				}
				DiscardPanel.this.showPanel(CARD_PANEL);

				// fire event again
				pickUpSkatButton.dispatchEvent(e);
			}
		});

		JPanel lookIntoSkatPanel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
		lookIntoSkatPanel.add(pickUpSkatButton, "center"); //$NON-NLS-1$
		lookIntoSkatPanel.setOpaque(false);
		add(lookIntoSkatPanel, PICK_UP_SKAT_BUTTON);

		setOpaque(false);
		cardPanel.showCards();
	}

	protected void setSkat(CardList skat) {

		clearCards();
		addCard(skat.get(0));
		addCard(skat.get(1));

		userPickedUpSkat = true;
	}

	void clearCards() {
		cardPanel.clearCards();
	}

	void addCard(Card card) {
		cardPanel.addCard(card);
	}

	void removeCard(Card card) {
		cardPanel.removeCard(card);
	}

	public void resetPanel() {
		cardPanel.clearCards();
		userPickedUpSkat = false;
		showPanel(PICK_UP_SKAT_BUTTON);
	}

	protected void showPanel(String panelType) {
		((CardLayout) getLayout()).show(DiscardPanel.this, panelType);
	}

	public CardList getDiscardedCards() {
		return (CardList) cardPanel.cards.clone();
	}

	public boolean isUserLookedIntoSkat() {
		return userPickedUpSkat;
	}

	public boolean isHandFull() {
		return cardPanel.getCardCount() == maxCardCount;
	}

	boolean isUserPickedUpSkat() {
		return userPickedUpSkat;
	}

	void setAnnouncePanel(GameAnnouncePanel announcePanel) {
		this.announcePanel = announcePanel;
	}

}
