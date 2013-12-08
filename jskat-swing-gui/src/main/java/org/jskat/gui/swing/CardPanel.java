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
package org.jskat.gui.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for showing cards.
 */
public class CardPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(CardPanel.class);

	protected final JSkatGraphicRepository bitmaps;

	protected Double scaleFactor = 1.0;
	private Boolean showBackside = true;

	private Integer mouseXPosition = Integer.MAX_VALUE;
	protected Integer activeCardMinXPosition = Integer.MAX_VALUE;
	protected Integer activeCardMaxXPosition = Integer.MAX_VALUE;

	/**
	 * Holds the game type for the sorting order.
	 */
	private GameType sortGameType = GameType.GRAND;

	protected CardList cards;

	/**
	 * Creates a new instance of CardPanel.
	 * 
	 * @param scaleFactor
	 *            Scale factor for cards
	 * @param showBackside
	 *            TRUE if the Card should hide its face
	 */
	public CardPanel(final Double scaleFactor, final Boolean showBackside) {

		setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		createMouseAdapter();

		bitmaps = JSkatGraphicRepository.instance();
		this.scaleFactor = scaleFactor;
		this.showBackside = showBackside;

		cards = new CardList();

		setOpaque(false);
	}

	private void createMouseAdapter() {
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseXPosition = e.getX();
				repaintIfNecessary();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseXPosition = e.getX();
				resetActiveCardPosition();
				repaintIfNecessary();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				resetMousePositions();
				repaint();
			}
		};

		addMouseMotionListener(adapter);
		addMouseListener(adapter);
	}

	private void resetMousePositions() {
		mouseXPosition = Integer.MAX_VALUE;
		resetActiveCardPosition();
	}

	private void resetActiveCardPosition() {
		activeCardMinXPosition = Integer.MAX_VALUE;
		activeCardMaxXPosition = Integer.MAX_VALUE;
	}

	protected void repaintIfNecessary() {

		if (!showBackside
				&& (mouseXPosition < activeCardMinXPosition || mouseXPosition > activeCardMaxXPosition)) {
			repaint();
		}
	}

	/**
	 * Adds a card.
	 * 
	 * @param newCard
	 *            Card
	 */
	public final void addCard(final Card newCard) {

		cards.add(newCard);
		cards.sort(sortGameType);
		repaint();
	}

	/**
	 * Adds a list of cards.
	 * 
	 * @param newCards
	 *            List of cards
	 */
	public final void addCards(final CardList newCards) {

		cards.addAll(newCards);
		cards.sort(sortGameType);
		repaint();
	}

	public final void removeCard(final Card cardToRemove) {

		if (cards.contains(cardToRemove)) {
			cards.remove(cardToRemove);
		} else if (showBackside) {
			// card panels with hidden cards may contain unknown cards
			// remove the last one
			cards.remove(cards.size() - 1);
		}
		resetActiveCardPosition();
		repaint();
	}

	public final Card get(final int index) {

		return cards.get(index);
	}

	/**
	 * @see JPanel#paintComponent(Graphics)
	 */
	@Override
	protected synchronized void paintComponent(final Graphics g) {

		super.paintComponent(g);

		// copying cards prevents ConcurrentModificationException
		final CardList cardsToPaint = new CardList(cards);

		// rendering hints
		final Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// calculate card gap
		int panelWidth = getWidth();
		int cardWidth = bitmaps.getCardImage(Card.CJ).getWidth(this);
		int cardGap = calculateCardGap(panelWidth, cardWidth);

		adjustActiveCardPositions(cardWidth, cardGap);
		paintAllCards(cardsToPaint, g2D, cardWidth, cardGap);
		// drawMouseMarkers(g2D);
	}

	private int calculateCardGap(final int panelWidth, final int cardWidth) {
		int cardGap = cardWidth;
		if (cards.size() * cardGap > panelWidth) {
			// cards overlap
			cardGap = (panelWidth - cardWidth) / (cards.size() - 1);
		}
		return cardGap;
	}

	private void paintAllCards(final CardList cardsToPaint,
			final Graphics2D g2D, final int cardWidth, int cardGap) {
		int cardNo = 0;
		for (final Card card : cardsToPaint) {

			final AffineTransform transform = new AffineTransform();
			transform.scale(scaleFactor, scaleFactor);

			if (cardNo * cardGap <= activeCardMinXPosition) {
				transform.translate(cardNo * cardGap, 0);
			} else if (activeCardMaxXPosition < cardNo * cardGap + cardWidth) {
				transform.translate((cardNo - 1) * cardGap + cardWidth, 0);
			}
			g2D.drawImage(getCardImage(card), transform, this);

			cardNo++;
		}
	}

	private void adjustActiveCardPositions(int cardWidth, int cardGap) {
		if (mouseXPosition < activeCardMinXPosition) {
			activeCardMinXPosition = (mouseXPosition / cardGap) * cardGap;
		} else if (mouseXPosition > activeCardMaxXPosition) {
			activeCardMinXPosition = ((mouseXPosition - cardWidth + cardGap) / (cardGap))
					* cardGap;
		}
		activeCardMaxXPosition = activeCardMinXPosition + cardWidth;
	}

	private void drawMouseMarkers(final Graphics2D g2D) {
		g2D.setColor(Color.RED);
		g2D.setStroke(new BasicStroke(10.0f));
		g2D.drawLine(activeCardMinXPosition, 0, activeCardMinXPosition, 10);
		g2D.drawLine(activeCardMaxXPosition, 0, activeCardMaxXPosition, 10);
		g2D.drawOval(mouseXPosition, 10, 5, 5);
	}

	private Image getCardImage(final Card card) {
		Image image = null;

		if (showBackside) {

			image = bitmaps.getCardImage(null);
		} else {
			if (card == null) {
				// e.g. in debug mode
				image = bitmaps.getCardImage(null);
			} else {
				image = bitmaps.getCardImage(card);
			}
		}
		return image;
	}

	/**
	 * Clears the card panel.
	 */
	public final void clearCards() {

		cards.clear();
		repaint();
	}

	/**
	 * Flips the cards.
	 */
	public final void flipCards() {

		if (showBackside) {

			showCards();
		} else {

			hideCards();
		}
	}

	/**
	 * Shows the cards.
	 */
	public final void showCards() {

		showBackside = false;
		repaint();
	}

	/**
	 * Hides the cards.
	 */
	public final void hideCards() {

		if (!JSkatOptions.instance().isCheatDebugMode().booleanValue()) {
			showBackside = true;
			repaint();
		}
	}

	/**
	 * Returns the number of cards.
	 * 
	 * @return Number of cards
	 */
	public final int getCardCount() {

		return cards.size();
	}

	/**
	 * Sets the sorting order.
	 * 
	 * @param newGameType
	 *            Game type
	 */
	public final void setSortType(final GameType newGameType) {

		sortGameType = newGameType;
		cards.sort(sortGameType);
		repaint();
	}

	/**
	 * Returns the cards.
	 * 
	 * @return Cards
	 */
	public final CardList getCards() {
		return cards.getImmutableCopy();
	}
}
