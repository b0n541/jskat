/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.action.JSkatActions;
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
		
		setLayout(new MigLayout("fill", "fill", "fill"));
		
		this.headerLabel.setText("Skat");
		add(this.headerLabel, "wrap"); //$NON-NLS-1$
		
		JPanel cardPanels = new JPanel(new MigLayout("fill, gapx 0", "fill", "fill")); //$NON-NLS-3$
		for (CardPanel panel : this.panels) {
			
			cardPanels.add(panel, "growy"); //$NON-NLS-1$
		}
		add(cardPanels, "grow, wrap"); //$NON-NLS-1$
		
		this.discardAction = this.parent.getActionMap().get(JSkatActions.DISCARD_CARDS);
		final JButton discardButton = new JButton(this.discardAction);
		discardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (DiscardPanel.this.cards.size() == 2) {
					
					CardList newSkat = new CardList();
					newSkat.add(DiscardPanel.this.cards.get(0));
					newSkat.add(DiscardPanel.this.cards.get(1));
					
					e.setSource(newSkat);  
					// fire event again
					discardButton.dispatchEvent(e);
				}
			}
		});
		JPanel buttonPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		buttonPanel.add(discardButton, "center"); //$NON-NLS-1$
		add(buttonPanel);
		
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
		
		this.cards.clear();
		setDiscardButton();
	}

	private void setDiscardButton() {
		
		if (this.cards.size() == 2) {
			
			this.getActionMap().get(JSkatActions.DISCARD_CARDS).setEnabled(true);
		}
		else {
			
			this.getActionMap().get(JSkatActions.DISCARD_CARDS).setEnabled(false);
		}
	}
}
