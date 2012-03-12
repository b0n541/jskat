package org.jskat.gui.table;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import org.jskat.gui.LayoutFactory;
import org.jskat.gui.img.JSkatGraphicRepository;

/**
 * Main panel for the play ground
 */
public class PlayGroundPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Image backGroundImage;

	/**
	 * Constructor
	 * 
	 * @param gameInfoPanel
	 *            Game info panel
	 * @param leftOpponentPanel
	 *            Left opponent panel
	 * @param rightOpponentPanel
	 *            Right opponent panel
	 * @param gameContextPanel
	 *            Game context panel
	 * @param userPanel
	 *            User panel
	 */
	public PlayGroundPanel(GameInformationPanel gameInfoPanel,
			OpponentPanel leftOpponentPanel, OpponentPanel rightOpponentPanel,
			JPanel gameContextPanel, JSkatUserPanel userPanel) {

		super(LayoutFactory.getMigLayout(
				"fill", "fill", "[shrink][shrink][grow][shrink][shrink]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		add(gameInfoPanel, "span 2, growx, shrinky, align center, wrap"); //$NON-NLS-1$
		add(leftOpponentPanel,
				"width 50%, growx, growy, hmin 20%, hmax 20%, align left"); //$NON-NLS-1$
		add(rightOpponentPanel,
				"width 50%, growx, growy, hmin 20%, hmax 20%, align right, wrap"); //$NON-NLS-1$
		add(gameContextPanel, "span 2, growx, growy, align center, wrap"); //$NON-NLS-1$
		add(userPanel,
				"span 2, growx, growy, hmin 33%, hmax 33%, align center, wrap"); //$NON-NLS-1$

		backGroundImage = JSkatGraphicRepository.instance().getSkatTableImage();
	}

	@Override
	public void paintComponent(Graphics g) {

		int width = getWidth();
		int height = getHeight();

		int imageWidth = backGroundImage.getWidth(null);
		int imageHeight = backGroundImage.getHeight(null);

		int currX = 0;
		int currY = 0;

		while (currX < width) {
			while (currY < height) {
				g.drawImage(backGroundImage, currX, currY, null);
				currY += imageHeight;
			}
			currY = 0;
			currX += imageWidth;
		}
	}
}
