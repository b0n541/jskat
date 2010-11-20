/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Main panel for the play ground
 */
public class PlayGroundPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Image backGroundImage;

	/**
	 * Constructor
	 * 
	 * @param jskatBitmaps
	 *            Graphic repository
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
	public PlayGroundPanel(JSkatGraphicRepository jskatBitmaps,
			GameInformationPanel gameInfoPanel,
			OpponentPanel leftOpponentPanel, OpponentPanel rightOpponentPanel,
			JPanel gameContextPanel, JSkatUserPanel userPanel) {

		super(new MigLayout(
				"fill", "fill", "[shrink][shrink][grow][shrink][shrink]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		add(gameInfoPanel, "span 2, growx, shrinky, align center, wrap"); //$NON-NLS-1$
		add(leftOpponentPanel,
				"width 50%, growx, growy, hmin 20%, hmax 20%, align left"); //$NON-NLS-1$
		add(rightOpponentPanel,
				"width 50%, growx, growy, hmin 20%, hmax 20%, align right, wrap"); //$NON-NLS-1$
		add(gameContextPanel, "span 2, growx, growy, align center, wrap"); //$NON-NLS-1$
		add(userPanel,
				"span 2, growx, growy, hmin 20%, hmax 20%, align center, wrap"); //$NON-NLS-1$

		backGroundImage = jskatBitmaps.getSkatTableImage();
	}

	@Override
	public void paintComponent(Graphics g) {

		g.drawImage(backGroundImage, 0, 0, null);
	}
}
