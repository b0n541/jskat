/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-10
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
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

		bitmaps = JSkatGraphicRepository.instance();
		this.scaleFactor = scaleFactor;
		this.showBackside = showBackside;

		cards = new CardList();

		setOpaque(false);
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
		final int panelWidth = getWidth();
		final int cardWidth = bitmaps.getCardImage(Card.CJ).getWidth(this);
		int cardGap = cardWidth;
		if (cards.size() * cardGap > panelWidth) {

			cardGap = (panelWidth - cardWidth) / (cards.size() - 1);
		}

		// paint all cards
		int cardNo = 0;
		for (final Card card : cardsToPaint) {

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

			final AffineTransform transform = new AffineTransform();
			transform.scale(scaleFactor, scaleFactor);
			transform.translate(cardNo * cardGap, 0);
			g2D.drawImage(image, transform, this);

			cardNo++;
		}
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
