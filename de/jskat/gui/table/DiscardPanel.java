/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
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
class DiscardPanel extends HandPanel {

	private static final long serialVersionUID = 1L;

	private Action discardAction;

	/**
	 * Constructor
	 * 
	 * @see HandPanel#HandPanel(SkatTablePanel, JSkatGraphicRepository, int)
	 */
	public DiscardPanel(SkatTablePanel newParent,
			JSkatGraphicRepository jskatBitmaps, int maxCards) {

		super(newParent, jskatBitmaps, maxCards);
	}

	/**
	 * @see HandPanel#initPanel()
	 */
	@Override
	void initPanel() {

		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.black));

		setLayout(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.headerLabel.setText("Skat");
		add(this.headerLabel, "wrap"); //$NON-NLS-1$

		this.cardPanel = new CardPanel(this, this.bitmaps, false);
		add(this.cardPanel, "grow, wrap"); //$NON-NLS-1$

		this.discardAction = this.parent.getActionMap().get(
				JSkatAction.DISCARD_CARDS);
		final JButton discardButton = new JButton(this.discardAction);
		discardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (DiscardPanel.this.cardPanel.getCardCount() == 2) {

					CardList newSkat = new CardList();
					newSkat.add(DiscardPanel.this.cardPanel.get(0));
					newSkat.add(DiscardPanel.this.cardPanel.get(1));

					e.setSource(newSkat);
					// fire event again
					discardButton.dispatchEvent(e);
				}
			}
		});
		JPanel buttonPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		buttonPanel.add(discardButton, "center"); //$NON-NLS-1$
		add(buttonPanel);

		setOpaque(false);

		this.showCards();
	}

	protected void setSkat(CardList skat) {

		addCard(skat.get(0));
		addCard(skat.get(1));
	}

	@Override
	void addCard(Card card) {

		super.addCard(card);

		setDiscardButton();
	}

	@Override
	void removeCard(Card card) {

		super.removeCard(card);

		setDiscardButton();
	}

	public void resetPanel() {

		this.cardPanel.clearCards();
		setDiscardButton();
	}

	private void setDiscardButton() {

		if (this.cardPanel.getCardCount() == 2) {

			this.getActionMap().get(JSkatAction.DISCARD_CARDS).setEnabled(true);
		} else {

			this.getActionMap().get(JSkatAction.DISCARD_CARDS)
					.setEnabled(false);
		}
	}
}
