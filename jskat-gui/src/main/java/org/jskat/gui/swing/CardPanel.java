package org.jskat.gui.swing;

import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * Panel for showing cards.
 */
public class CardPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(CardPanel.class);

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
     * @param scaleFactor  Scale factor for cards
     * @param showBackside TRUE if the Card should hide its face
     */
    public CardPanel(final Double scaleFactor, final Boolean showBackside) {

        setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill"));

        createMouseAdapter();

        bitmaps = JSkatGraphicRepository.INSTANCE;
        this.scaleFactor = scaleFactor;
        this.showBackside = showBackside;

        cards = new CardList();

        setOpaque(false);
    }

    private void createMouseAdapter() {
        final MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                mouseXPosition = e.getX();
                repaintIfNecessary();
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                mouseXPosition = e.getX();
                resetActiveCardPosition();
                repaintIfNecessary();
            }

            @Override
            public void mouseExited(final MouseEvent e) {
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
     * @param newCard Card
     */
    public final void addCard(final Card newCard) {

        cards.add(newCard);
        cards.sort(sortGameType);
        repaint();
    }

    /**
     * Adds a list of cards.
     *
     * @param newCards List of cards
     */
    public final void addCards(final CardList newCards) {

        cards.addAll(newCards);
        cards.sort(sortGameType);
        repaint();
    }

    /**
     * Removes a card.
     *
     * @param cardToRemove Card to remove
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
     * @param index Index of card
     * @return Card
     */
    public final Card get(final int index) {

        return cards.get(index);
    }

    /**
     * @see JPanel#paintComponent(Graphics)
     */
    @Override
    protected final synchronized void paintComponent(final Graphics g) {

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
        final int cardGap = calculateCardGap(panelWidth, cardWidth);

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
                               final Graphics2D g2D, final int cardWidth, final int cardGap) {
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

    private void adjustActiveCardPositions(final int cardWidth, final int cardGap) {
        if (mouseXPosition < activeCardMinXPosition) {
            activeCardMinXPosition = (mouseXPosition / cardGap) * cardGap;
        } else if (mouseXPosition > activeCardMaxXPosition) {
            activeCardMinXPosition = ((mouseXPosition - cardWidth + cardGap) / (cardGap))
                    * cardGap;
        }
        activeCardMaxXPosition = activeCardMinXPosition + cardWidth;
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
     * @param newGameType Game type
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
