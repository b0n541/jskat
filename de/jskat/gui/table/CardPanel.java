/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData.GameState;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;

/**
 * Panel for showing a Card
 */
class CardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(CardPanel.class);

	private JSkatGraphicRepository bitmaps;

	/**
	 * Holds the game type for the sorting order
	 */
	GameType gameType = GameType.GRAND;

	CardList cards;

	private boolean showBackside = true;
	private HandPanel parent = null;

	/**
	 * Creates a new instance of CardPanel
	 * 
	 * @param newParent
	 *            Parent panel
	 * @param jSkatBitmaps
	 *            Graphic repository that holds all images used in JSkat
	 * @param newShowBackside
	 *            TRUE if the Card should hide its face
	 */
	CardPanel(HandPanel newParent, JSkatGraphicRepository jSkatBitmaps,
			boolean newShowBackside) {

		this.setLayout(new MigLayout("fill", "fill", "fill"));

		this.parent = newParent;
		setActionMap(this.parent.getActionMap());
		this.bitmaps = jSkatBitmaps;
		this.showBackside = newShowBackside;

		this.cards = new CardList();

		this.setOpaque(false);

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

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

				cardClicked(e);
			}
		});
	}

	protected void addCard(Card newCard) {

		this.cards.add(newCard);
		this.cards.sort(this.gameType);
		repaint();
	}

	protected void addCards(Collection<Card> newCards) {

		this.cards.addAll(newCards);
		this.cards.sort(this.gameType);
		repaint();
	}

	protected void removeCard(Card cardToRemove) {

		this.cards.remove(cardToRemove);
		repaint();
	}

	protected Card get(int index) {

		return this.cards.get(index);
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

		int imgWidth = this.getWidth() / (this.cards.size() + 1);

		int cardNo = 0;
		for (Card card : this.cards) {

			Image image = null;
			if (this.showBackside) {

				image = this.bitmaps.getCardImage(null, null);
			} else {

				image = this.bitmaps.getCardImage(card.getSuit(),
						card.getRank());
			}
			double scaleFactor = 1.0d;

			AffineTransform transform = new AffineTransform();
			transform.scale(scaleFactor, scaleFactor);
			transform.translate(cardNo * imgWidth, 0);
			g2D.drawImage(image, transform, this);

			cardNo++;
		}
	}

	/**
	 * Clears the card panel
	 */
	void clearCards() {

		this.cards.clear();
		repaint();
	}

	/**
	 * Flips the card
	 */
	void flipCard() {

		if (this.showBackside) {

			showCards();
		} else {

			hideCards();
		}
	}

	/**
	 * Shows the card
	 */
	void showCards() {

		this.showBackside = false;
		repaint();
	}

	/**
	 * Hides the card
	 */
	void hideCards() {

		this.showBackside = true;
		repaint();
	}

	int getCardCount() {

		return this.cards.size();
	}

	/**
	 * Tells the JSkatMaster when the panel was clicked by the user
	 */
	void cardClicked(MouseEvent e) {

		int xVal = e.getX();
		int yVal = e.getY();

		log.debug("Card panel clicked at: " + xVal + " x " + yVal); //$NON-NLS-1$ //$NON-NLS-2$

		if (xVal > -1 && xVal < this.getWidth() && yVal > -1
				&& yVal < this.getHeight()) {

			log.debug("Mouse button release inside panel"); //$NON-NLS-1$

			// get card
			int imgWidth = this.getWidth() / (this.cards.size() + 1);
			int cardIndex = xVal / imgWidth;

			Card card = null;
			if (cardIndex < this.cards.size()) {

				card = this.cards.get(cardIndex);
				log.debug("card index: " + cardIndex + " card: " + this.cards.get(cardIndex)); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (card != null) {
				// send event only, if the card panel shows a card
				Action action = null;

				if (this.parent instanceof DiscardPanel) {
					// card panel in discard panel was clicked
					action = getActionMap()
							.get(JSkatAction.TAKE_CARD_FROM_SKAT);
				} else if (this.parent instanceof PlayerPanel) {
					// card panel in player panel was clicked

					GameState state = ((PlayerPanel) this.parent)
							.getGameState();

					if (state == GameState.DISCARDING) {
						// discarding phase
						action = getActionMap().get(
								JSkatAction.PUT_CARD_INTO_SKAT);
					} else if (state == GameState.TRICK_PLAYING) {
						// trick playing phase
						action = getActionMap().get(JSkatAction.PLAY_CARD);
					}
				} else {

					log.debug("Other parent " + this.parent); //$NON-NLS-1$
				}

				if (action != null) {

					action.actionPerformed(new ActionEvent(Card
							.getCardFromString(card.getSuit().shortString()
									+ card.getRank().shortString()),
							ActionEvent.ACTION_PERFORMED, (String) action
									.getValue(Action.ACTION_COMMAND_KEY)));
				} else {

					log.debug("Action is null"); //$NON-NLS-1$
				}
			}
		}
	}

	void setSortType(GameType newGameType) {

		this.gameType = newGameType;
		this.cards.sort(this.gameType);
		repaint();
	}
}
