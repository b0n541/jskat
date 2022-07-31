package org.jskat.gui.swing.table;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.LayoutFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main panel for the play ground
 */
public class PlayGroundPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Image backGroundImage;

    /**
     * Constructor
     *
     * @param gameInfoPanel      Game info panel
     * @param leftOpponentPanel  Left opponent panel
     * @param rightOpponentPanel Right opponent panel
     * @param gameContextPanel   Game context panel
     * @param userPanel          User panel
     */
    public PlayGroundPanel(GameInformationPanel gameInfoPanel,
                           OpponentPanel leftOpponentPanel, OpponentPanel rightOpponentPanel,
                           JPanel gameContextPanel, JSkatUserPanel userPanel) {

        super(LayoutFactory.getMigLayout(
                "fill, ins 0, gap 0 0", "fill", "[shrink][shrink][grow][shrink][shrink]"));

        add(gameInfoPanel, "span 2, growx, shrinky, align center, wrap");
        add(leftOpponentPanel,
                "width 50%, growx, growy, hmin 15%, hmax 15%, align left");
        add(rightOpponentPanel,
                "width 50%, growx, growy, hmin 15%, hmax 15%, align right, wrap");
        add(gameContextPanel, "span 2, growx, growy, align center, wrap");
        add(userPanel,
                "span 2, growx, growy, hmin 33%, hmax 33%, align center, wrap");

        this.backGroundImage = JSkatGraphicRepository.INSTANCE
                .getSkatTableImage();
    }

    @Override
    public void paintComponent(Graphics g) {

        int width = getWidth();
        int height = getHeight();

        int imageWidth = this.backGroundImage.getWidth(null);
        int imageHeight = this.backGroundImage.getHeight(null);

        int currX = 0;
        int currY = 0;

        while (currX < width) {
            while (currY < height) {
                g.drawImage(this.backGroundImage, currX, currY, null);
                currY += imageHeight;
            }
            currY = 0;
            currX += imageWidth;
        }
    }
}
