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
package org.jskat.gui.swing.table;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.LayoutFactory;

/**
 * Main panel for the play ground
 */
public class PlayGroundPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final Image backGroundImage;

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
				"fill, ins 0, gap 0 0", "fill", "[shrink][shrink][grow][shrink][shrink]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		add(gameInfoPanel, "span 2, growx, shrinky, align center, wrap"); //$NON-NLS-1$
		add(leftOpponentPanel,
				"width 50%, growx, growy, hmin 15%, hmax 15%, align left"); //$NON-NLS-1$
		add(rightOpponentPanel,
				"width 50%, growx, growy, hmin 15%, hmax 15%, align right, wrap"); //$NON-NLS-1$
		add(gameContextPanel, "span 2, growx, growy, align center, wrap"); //$NON-NLS-1$
		add(userPanel,
				"span 2, growx, growy, hmin 33%, hmax 33%, align center, wrap"); //$NON-NLS-1$

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
