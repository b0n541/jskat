/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import de.jskat.data.iss.ISSPlayerStatus;
import de.jskat.data.iss.ISSTablePanelStatus;
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
	public ISSTablePanel(String tableName, JSkatGraphicRepository jskatBitmaps, ActionMap actions) {
		
		super(tableName, jskatBitmaps, actions);
	}

	/**
	 * @see SkatTablePanel#getPlayGroundPanel()
	 */
	@Override
	protected JPanel getPlayGroundPanel() {
		
		JPanel panel = super.getPlayGroundPanel();
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
	}
}
