/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;

import javax.swing.Action;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData.GameStates;
import de.jskat.gui.action.JSkatActions;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.Rank;
import de.jskat.util.SkatConstants;
import de.jskat.util.Suit;

/**
 * Panel for showing a Card
 */
class CardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(CardPanel.class);

	private JSkatGraphicRepository bitmaps;
	private Suit suit;
	private Rank rank;
	private boolean showBackside = true;
	private HandPanel parent = null;

	/**
	 * Creates a new instance of CardPanel
	 * 
	 * @param newParent Parent panel
	 * @param jSkatBitmaps
	 *            Graphic repository that holds all images used in JSkat
	 * @param newShowBackside
	 *            TRUE if the Card should hide its face
	 */
	CardPanel(HandPanel newParent, JSkatGraphicRepository jSkatBitmaps,
			boolean newShowBackside) {

		this.parent = newParent;
		setActionMap(this.parent.getActionMap());
		this.bitmaps = jSkatBitmaps;
		this.showBackside = newShowBackside;

		this.setOpaque(false);

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				cardClicked();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * @see JPanel#paintComponent(Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (this.suit != null && this.rank != null) {

			Image image = null;

			if (this.showBackside) {

				image = this.bitmaps.getCardImage(null, null);
			} else {

				image = this.bitmaps.getCardImage(this.suit, this.rank);
			}

			double scaleFactor = 1.0d / ((double) image.getHeight(null) / (double) this
					.getHeight());
			AffineTransform transform = new AffineTransform();
			transform.scale(scaleFactor, scaleFactor);
			g2D.drawImage(image, transform, this);

			// g2D.drawString(this.suit.shortString()+"-"+this.rank.shortString(),
			// 20, 20);
			// g2D.drawString(this.getWidth()+" "+this.getHeight(), 20, 40);
		}
	}

	/**
	 * Sets the card suit and value
	 * 
	 * @param newSuit
	 *            New suit of the card
	 * @param newRank
	 *            New rank of the card
	 */
	void setCard(Suit newSuit, Rank newRank) {

		this.suit = newSuit;
		this.rank = newRank;
		repaint();
	}

	/**
	 * Sets the card
	 * 
	 * @param card
	 *            Card
	 */
	void setCard(Card card) {

		this.setCard(card.getSuit(), card.getRank());
	}

	/**
	 * Clears the card panel
	 */
	void clear() {

		this.suit = null;
		this.rank = null;
		repaint();
	}

	/**
	 * Flips the card
	 */
	void flipCard() {

		if (this.showBackside) {

			showCard();
		} else {

			hideCard();
		}
	}

	/**
	 * Shows the card
	 */
	void showCard() {

		this.showBackside = false;
		repaint();
	}

	/**
	 * Hides the card
	 */
	void hideCard() {

		this.showBackside = true;
		repaint();
	}

	/**
	 * Gets the suit of the card
	 * 
	 * @return ID of the suit
	 */
	Suit getSuit() {

		return this.suit;
	}

	/**
	 * Gets the rank of the card
	 * 
	 * @return The rank of the card
	 */
	Rank getRank() {

		return this.rank;
	}

	/**
	 * Tells the JSkatMaster when the panel was clicked by the user
	 */
	void cardClicked() {
		
		log.debug("Card panel clicked: " + this.suit + " " + this.rank);
		
		if (this.suit != null && this.rank != null) {
			// send event only, if the card panel shows a card

			Action action = null;
		
			if (this.parent instanceof DiscardPanel) {
				// card panel in discard panel was clicked
				action = getActionMap().get(JSkatActions.TAKE_CARD_FROM_SKAT);
			}
			else if (this.parent instanceof PlayerPanel) {
				// card panel in player panel was clicked
				
				GameStates state = ((PlayerPanel) this.parent).getGameState();
				
				if (state == GameStates.DISCARDING) {
					// discarding phase
					action = getActionMap().get(JSkatActions.PUT_CARD_INTO_SKAT);
				}
				else if (state == GameStates.TRICK_PLAYING) {
					// trick playing phase
					action = getActionMap().get(JSkatActions.PLAY_CARD);
				}
			}
			else {
				
				log.debug("Other parent " + this.parent);
			}
			
			if (action != null) {
				
				action.actionPerformed(new ActionEvent(Card.getCardFromString(this.suit.shortString() + this.rank.shortString()), ActionEvent.ACTION_PERFORMED, (String) action.getValue(Action.ACTION_COMMAND_KEY)));
			}
			else {
				
				log.debug("Action is null");
			}
		}
	}
}
