/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.table;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.SkatGameData;
import de.jskat.data.Trick;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.util.Player;

/**
 * Holds all informations about a game at the end
 */
class GameResultPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(GameResultPanel.class);

	private Player userPosition;
	private List<TrickPanel> trickPanelList;

	/**
	 * Game result panel
	 */
	GameResultPanel(JSkatGraphicRepository bitmaps, ResourceBundle strings) {

		initPanel(bitmaps, strings);
	}

	private void initPanel(JSkatGraphicRepository bitmaps,
			ResourceBundle strings) {

		setLayout(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		trickPanelList = new ArrayList<TrickPanel>();
		for (int i = 0; i < 10; i++) {

			trickPanelList.add(new TrickPanel(bitmaps, 0.5, false));
		}

		JPanel trickPanel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		for (TrickPanel panel : trickPanelList) {
			trickPanel.add(panel);
		}
		trickPanel.setOpaque(false);

		add(trickPanel, "grow"); //$NON-NLS-1$

		setOpaque(false);
	}

	public void setGameResult(SkatGameData gameData) {

		List<Trick> tricks = gameData.getTricks();

		log.debug("Trick size: " + tricks.size()); //$NON-NLS-1$

		for (int i = 0; i < 10; i++) {

			TrickPanel trickPanel = trickPanelList.get(i);
			trickPanel.clearCards();

			if (i < tricks.size()) {
				Trick trick = tricks.get(i);
				if (trick != null) {
					trickPanel.setUserPosition(userPosition);
					trickPanel.addCard(trick.getForeHand(),
							trick.getFirstCard());
					trickPanel.addCard(trick.getForeHand().getLeftNeighbor(),
							trick.getSecondCard());
					trickPanel.addCard(trick.getForeHand().getRightNeighbor(),
							trick.getThirdCard());
				}
			}
		}
	}

	public void setUserPosition(Player newUserPosition) {

		userPosition = newUserPosition;
	}
}
