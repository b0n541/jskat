/**
 * This file is part of JSkat.
 * <p>
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
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

		this.bitmaps = JSkatGraphicRepository.INSTANCE;
        this.scaleFactor = scaleFactor;
        this.showBackside = showBackside;

        this.cards = new CardList();

        setOpaque(false);
    }

    private void createMouseAdapter() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                CardPanel.this.mouseXPosition = e.getX();
                repaintIfNecessary();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                CardPanel.this.mouseXPosition = e.getX();
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
        this.mouseXPosition = Integer.MAX_VALUE;
        resetActiveCardPosition();
    }

    private void resetActiveCardPosition() {
        this.activeCardMinXPosition = Integer.MAX_VALUE;
        this.activeCardMaxXPosition = Integer.MAX_VALUE;
    }

    protected void repaintIfNecessary() {

        if (!this.showBackside
                && (this.mouseXPosition < this.activeCardMinXPosition || this.mouseXPosition > this.activeCardMaxXPosition)) {
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

        this.cards.add(newCard);
        this.cards.sort(this.sortGameType);
        repaint();
    }

    /**
     * Adds a list of cards.
     *
     * @param newCards
     *            List of cards
     */
    public final void addCards(final CardList newCards) {

        this.cards.addAll(newCards);
        this.cards.sort(this.sortGameType);
        repaint();
    }

    /**
     * Removes a card.
     *
     * @param cardToRemove
     *            Card to remove
     */
    public final void removeCard(final Card cardToRemove) {

		if (cards.contains(cardToRemove)) {
			cards.remove(cardToRemove);
		} else if (cards.size() > 0) {
            // card panels with hidden cards may contain unknown cards
            // remove the last one
			cards.remove(cards.size() - 1);
        }
        resetActiveCardPosition();
        repaint();
    }

    /**
     * Gets a card.
     *
     * @param index
     *            Index of card
     * @return Card
     */
    public final Card get(final int index) {

        return this.cards.get(index);
    }

    /**
     * @see JPanel#paintComponent(Graphics)
     */
    @Override
    protected final synchronized void paintComponent(final Graphics g) {

        super.paintComponent(g);

        // copying cards prevents ConcurrentModificationException
        final CardList cardsToPaint = new CardList(this.cards);

        // rendering hints
        final Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // calculate card gap
        int panelWidth = getWidth();
        int cardWidth = this.bitmaps.getCardImage(Card.CJ).getWidth(this);
        int cardGap = calculateCardGap(panelWidth, cardWidth);

        adjustActiveCardPositions(cardWidth, cardGap);
        paintAllCards(cardsToPaint, g2D, cardWidth, cardGap);
        // drawMouseMarkers(g2D);
    }

    private int calculateCardGap(final int panelWidth, final int cardWidth) {
        int cardGap = cardWidth;
        if (this.cards.size() * cardGap > panelWidth) {
            // cards overlap
            cardGap = (panelWidth - cardWidth) / (this.cards.size() - 1);
        }
        return cardGap;
    }

    private void paintAllCards(final CardList cardsToPaint,
            final Graphics2D g2D, final int cardWidth, int cardGap) {
        int cardNo = 0;
        for (final Card card : cardsToPaint) {

            final AffineTransform transform = new AffineTransform();
            transform.scale(this.scaleFactor, this.scaleFactor);

            if (cardNo * cardGap <= this.activeCardMinXPosition) {
                transform.translate(cardNo * cardGap, 0);
            } else if (this.activeCardMaxXPosition < cardNo * cardGap + cardWidth) {
                transform.translate((cardNo - 1) * cardGap + cardWidth, 0);
            }
            g2D.drawImage(getCardImage(card), transform, this);

            cardNo++;
        }
    }

    private void adjustActiveCardPositions(int cardWidth, int cardGap) {
        if (this.mouseXPosition < this.activeCardMinXPosition) {
            this.activeCardMinXPosition = (this.mouseXPosition / cardGap) * cardGap;
        } else if (this.mouseXPosition > this.activeCardMaxXPosition) {
            this.activeCardMinXPosition = ((this.mouseXPosition - cardWidth + cardGap) / (cardGap))
                    * cardGap;
        }
        this.activeCardMaxXPosition = this.activeCardMinXPosition + cardWidth;
    }

    private Image getCardImage(final Card card) {
        Image image = null;

        if (this.showBackside) {
            image = this.bitmaps.getCardImage(null);
        } else {
            if (card == null) {
                // e.g. in debug mode
                image = this.bitmaps.getCardImage(null);
            } else {
                image = this.bitmaps.getCardImage(card);
            }
        }
        return image;
    }

    /**
     * Clears the card panel.
     */
    public final void clearCards() {
        this.cards.clear();
        repaint();
    }

    /**
     * Flips the cards.
     */
    public final void flipCards() {
        if (this.showBackside) {
            showCards();
        } else {
            hideCards();
        }
    }

    /**
     * Shows the cards.
     */
    public final void showCards() {
        this.showBackside = false;
        repaint();
    }

    /**
     * Hides the cards.
     */
    public final void hideCards() {
        if (!JSkatOptions.instance().isCheatDebugMode().booleanValue()) {
            this.showBackside = true;
            repaint();
        }
    }

    /**
     * Returns the number of cards.
     *
     * @return Number of cards
     */
    public final int getCardCount() {
        return this.cards.size();
    }

    /**
     * Sets the sorting order.
     *
     * @param newGameType
     *            Game type
     */
    public final void setSortType(final GameType newGameType) {
        this.sortGameType = newGameType;
        this.cards.sort(this.sortGameType);
        repaint();
    }

    /**
     * Returns the cards.
     *
     * @return Cards
     */
    public final CardList getCards() {
        return this.cards.getImmutableCopy();
    }
}
