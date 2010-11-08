/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import de.jskat.data.SkatGameData.GameState;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.table.ContextPanelTypes;
import de.jskat.gui.table.SkatTablePanel;
import de.jskat.util.Player;

/**
 * Panel for ISS table
 */
public class ISSTablePanel extends SkatTablePanel {

	private static final long serialVersionUID = 1L;

	String loginName;

	/**
	 * Constructor
	 * 
	 * @param tableName
	 *            Table name
	 * @param jskatBitmaps
	 *            Bitmap repository
	 * @param actions
	 *            Action map
	 * @param strings
	 *            i18n strings
	 * @param newLoginName
	 *            Login name on ISS
	 */
	public ISSTablePanel(String tableName, JSkatGraphicRepository jskatBitmaps,
			ActionMap actions, ResourceBundle strings, String newLoginName) {

		super(tableName, jskatBitmaps, actions, strings);

		loginName = newLoginName;
	}

	/**
	 * @see SkatTablePanel#getPlayGroundPanel()
	 */
	@Override
	protected JPanel getPlayGroundPanel() {

		JPanel panel = super.getPlayGroundPanel();
		// replace game start context panel
		addContextPanel(ContextPanelTypes.START_SERIES,
				new GameStartPanel(this.getActionMap()));
		addContextPanel(ContextPanelTypes.GAME_OVER,
				new GameStartPanel(this.getActionMap()));
		setGameState(GameState.NEW_GAME);
		panel.add(getChatPanel(), "span 2, growx, align center"); //$NON-NLS-1$

		return panel;
	}

	private JPanel getChatPanel() {

		return new ChatPanel(this);
	}

	/**
	 * Updates the panel with the new status
	 * 
	 * @param tableStatus
	 *            New table status
	 */
	public void setTableStatus(ISSTablePanelStatus tableStatus) {

		// FIXME (jan 08.11.2010) find a better solution
		setMaxPlayers(tableStatus.getMaxPlayers());

		int playerCount = tableStatus.getNumberOfPlayers();
		int playerPosition = tableStatus.getPlayerPosition();

		if (playerPosition != -1) {
			setPlayerInformation(Player.FORE_HAND, tableStatus
					.getPlayerInformation().get(playerPosition).getName(), 0.0);
		}

		if (playerCount > 1) {
			setPlayerInformation(Player.MIDDLE_HAND, tableStatus
					.getPlayerInformation().get((playerPosition + 1) % 3)
					.getName(), 0.0);
		}

		if (playerCount > 2) {
			setPlayerInformation(Player.HIND_HAND, tableStatus
					.getPlayerInformation().get((playerPosition + 2) % 3)
					.getName(), 0.0);
		}

		// FIXME (jan 08.11.2010) what about four players?
	}
}
