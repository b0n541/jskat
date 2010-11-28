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
		addContextPanel(ContextPanelTypes.START,
				new StartContextPanel(this.getActionMap()));
		addContextPanel(ContextPanelTypes.GAME_OVER,
				new StartContextPanel(this.getActionMap()));
		setGameState(GameState.GAME_START);
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

		setMaxPlayers(tableStatus.getMaxPlayers());

		// TODO set player informations
	}
}
