package org.jskat.gui.swing.table;

import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.CardFace;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.util.Card;
import org.jskat.util.CardList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;

public class SkatPanel extends JPanel implements ComponentListener {

    private static final long serialVersionUID = -6797067552577385026L;

    private static final JSkatOptions options = JSkatOptions.instance();
    private final JSkatGraphicRepository bitmaps = JSkatGraphicRepository.INSTANCE;

    private CardFace cardFace;

    private Card skatCardOne;
    private Card skatCardTwo;


    public SkatPanel() {

        cardFace = options.getCardSet().getCardFace();
        setOpaque(false);
    }

    public void setSkatCards(CardList skat) {
        if (skat.size() == 2) {
            skatCardOne = skat.get(0);
            skatCardTwo = skat.get(1);
            repaint();
        }
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (skatCardOne != null && skatCardTwo != null) {
            if (isNewCardFace()) {
                cardFace = options.getCardSet().getCardFace();
            }

            Graphics2D g2D = (Graphics2D) g;
            g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            g2D.drawImage(bitmaps.getCardImage(skatCardOne), new AffineTransform(), this);
            AffineTransform transform = new AffineTransform();
            Image cardTwoImage = bitmaps.getCardImage(skatCardTwo);
            transform.translate(getWidth() - cardTwoImage.getWidth(this), 0);
            g2D.drawImage(cardTwoImage, transform, this);
        }
    }

    private boolean isNewCardFace() {
        return !cardFace.equals(options.getCardSet().getCardFace());
    }

    public void resetPanel() {
        skatCardOne = null;
        skatCardTwo = null;
        repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // not needed
    }

    @Override
    public void componentShown(ComponentEvent e) {
        repaint();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // not needed
    }
}
