/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import de.jskat.data.SkatGameData.GameStates;
import de.jskat.data.iss.ISSPlayerStatus;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.gui.ContextPanelTypes;
import de.jskat.gui.SkatTablePanel;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Panel for ISS table
 */
public class ISSTablePanel extends SkatTablePanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * 
	 * @param tableName Table name 
	 * @param jskatBitmaps Bitmap repository
	 * @param actions Action map
	 */
	public ISSTablePanel(String tableName, JSkatGraphicRepository jskatBitmaps, ActionMap actions, ResourceBundle strings) {
		
		super(tableName, jskatBitmaps, actions, strings);
	}

	/**
	 * @see SkatTablePanel#getPlayGroundPanel()
	 */
	@Override
	protected JPanel getPlayGroundPanel() {
		
		JPanel panel = super.getPlayGroundPanel();
		// replace game start context panel
		addContextPanel(new GameStartPanel(this.getActionMap()),
				ContextPanelTypes.START_SERIES.toString());
		setGameState(GameStates.NEW_GAME);
		panel.add(getChatPanel(), "span 2, growx, align center"); //$NON-NLS-1$
		
		return panel;
	}

	private JPanel getChatPanel() {
		
		return new ChatPanel(this);
	}

	/**
	 * Updates the panel with the new status
	 * 
	 * @param tableStatus New table status
	 */
	public void setTableStatus(ISSTablePanelStatus tableStatus) {
		
		this.setMaxPlayers(tableStatus.getMaxPlayers());
		
		Map<String, ISSPlayerStatus> playerInfo = tableStatus.getPlayerInformation();
		
		// FIXME set player informations
	}
}
