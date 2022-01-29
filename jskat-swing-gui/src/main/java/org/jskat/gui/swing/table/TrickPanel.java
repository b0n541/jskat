package org.jskat.gui.swing.table;

import org.jskat.control.gui.img.CardFace;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Renders all cards of a trick
 */
class TrickPanel extends JPanel implements ComponentListener {

    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(TrickPanel.class);

    private static final double TRICK_SIZE_FACTOR = 1.0d + 2.0d / 3.0d;

    private static JSkatOptions options = JSkatOptions.instance();
    private final JSkatGraphicRepository bitmaps = JSkatGraphicRepository.INSTANCE;
    private final List<Double> cardRotations;
    private final List<Player> positions;
    private final CardList trick;
    private final Random rand = new Random();
    private Player userPosition;
    private Player rightOpponent;
    private Player leftOpponent;

    private CardFace cardFace;
    private final boolean randomPlacement;
    private final double globalScale;

    /**
     * Constructor
     *
     * @param randomPlacement Random placement of cards
     */
    TrickPanel(final boolean randomPlacement) {
        this(1.0, randomPlacement);
    }

    /**
     * Constructor
     *
     * @param globalScale     Global scale of cards
     * @param randomPlacement Random placement of cards
     */
    TrickPanel(final double globalScale, final boolean randomPlacement) {

        this.cardFace = options.getCardSet().getCardFace();

        this.randomPlacement = randomPlacement;
        this.globalScale = globalScale;

        this.trick = new CardList();
        this.positions = new ArrayList<>();
        this.cardRotations = new ArrayList<>();

        setOpaque(false);

        addComponentListener(this);
    }

    /**
     * Adds a card to the trick
     *
     * @param player Position of the player
     * @param card   Card
     */
    void addCard(final Player player, final Card card) {

        this.positions.add(player);
        this.trick.add(card);

        if (this.randomPlacement) {
            this.cardRotations.add(Double.valueOf(0.5 * this.rand.nextDouble() - 0.25));
        } else {
            this.cardRotations.add(Double.valueOf(0.0));
        }

        repaint();
    }

    /**
     * Removes the last card
     */
    void removeCard() {

        this.positions.remove(this.positions.size() - 1);
        this.trick.remove(this.trick.size() - 1);
        this.cardRotations.remove(this.trick.size() - 1);
        repaint();
    }

    /**
     * Removes all cards from the trick
     */
    void clearCards() {

        this.positions.clear();
        this.trick.clear();
        this.cardRotations.clear();
        repaint();
    }

    /**
     * @see JPanel#paintComponent(Graphics)
     */
    @Override
    protected synchronized void paintComponent(final Graphics g) {

        super.paintComponent(g);

        if (isNewCardFace()) {
            this.cardFace = options.getCardSet().getCardFace();
        }

        final int panelWidth = getWidth();
        final int panelHeight = getHeight();

        final Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        final double cardScale = getCardScale() * this.globalScale;
        final Image image = this.bitmaps.getCardImage(Card.CJ);

        final double xScaleSize = image.getWidth(this);
        final double xAllTrickCardsSize = xScaleSize * TRICK_SIZE_FACTOR;
        final double xBorder = (panelWidth * (1 / cardScale) - xAllTrickCardsSize) / 2.0d;

        final double yScaleSize = image.getHeight(this);
        final double yAllTrickCardsSize = yScaleSize * TRICK_SIZE_FACTOR;
        final double yBorder = (panelHeight * (1 / cardScale) - yAllTrickCardsSize) / 2.0d;

        for (int i = 0; i < this.trick.size(); i++) {

            final Card card = this.trick.get(i);
            final Player player = this.positions.get(i);

            if (card != null && player != null) {
                // Calculate translation
                double posX = 0.0d;
                double posY = 0.0d;
                if (player.equals(this.leftOpponent)) {

                    posX = xBorder;
                    posY = yBorder + yScaleSize * (1.0d / 3.0d);

                } else if (player.equals(this.rightOpponent)) {

                    posX = xBorder + xScaleSize * (2.0d / 3.0d);
                    posY = yBorder;

                } else if (player.equals(this.userPosition)) {

                    posX = xBorder + xScaleSize * (1.0d / 3.0d);
                    posY = yBorder + yScaleSize * (2.0d / 3.0d);
                }

                final AffineTransform transform = new AffineTransform();
                transform.translate(posX * cardScale, posY * cardScale);
                transform.rotate(this.cardRotations.get(i).doubleValue());
                transform.scale(cardScale, cardScale);

                g2D.drawImage(this.bitmaps.getCardImage(card), transform, this);
            }
        }
    }

    private double getCardScale() {

        final Image sampleCard = this.bitmaps.getCardImage(Card.CJ);
        final double imageWidth = sampleCard.getWidth(this) * TRICK_SIZE_FACTOR;
        final double imageHeight = sampleCard.getHeight(this)
                * TRICK_SIZE_FACTOR;

        final double scaleX = getWidth() / imageWidth;
        final double scaleY = getHeight() / imageHeight;

        double scaleFactor = 1.0;
        if (scaleX < 1.0 || scaleY < 1.0) {
            if (scaleX < scaleY) {
                scaleFactor = scaleX;
            } else {
                scaleFactor = scaleY;
            }
        }

        return scaleFactor;
    }

    boolean isNewCardFace() {
        return !this.cardFace.equals(options.getCardSet().getCardFace());
    }

    void setUserPosition(final Player newUserPosition) {

        this.userPosition = newUserPosition;
        this.leftOpponent = this.userPosition.getLeftNeighbor();
        this.rightOpponent = this.userPosition.getRightNeighbor();
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        repaint();
    }

    @Override
    public void componentMoved(final ComponentEvent e) {
        // not needed
    }

    @Override
    public void componentShown(final ComponentEvent e) {
        repaint();
    }

    @Override
    public void componentHidden(final ComponentEvent e) {
        // not needed
    }
}
