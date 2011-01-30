/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.jskat.data.SkatGameData.GameState;
import de.jskat.data.iss.ISSChatMessage;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.gui.table.ContextPanelTypes;
import de.jskat.gui.table.SkatTablePanel;

/**
 * Panel for ISS table
 */
public class ISSTablePanel extends SkatTablePanel {

	private static final long serialVersionUID = 1L;

	String loginName;
	ChatPanel chatPanel;

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
	public ISSTablePanel(String tableName, ActionMap actions, String newLoginName) {

		super(tableName, actions);

		loginName = newLoginName;
	}

	/**
	 * @see SkatTablePanel#getPlayGroundPanel()
	 */
	@Override
	protected JPanel getPlayGroundPanel() {

		JPanel panel = new JPanel(new MigLayout("fill", "[grow][shrink]", //$NON-NLS-1$ //$NON-NLS-2$
				"fill")); //$NON-NLS-1$
		panel.add(super.getPlayGroundPanel(), "grow"); //$NON-NLS-1$
		chatPanel = getChatPanel();
		chatPanel.addNewChat(this.getName());
		panel.add(chatPanel, "width 20%, growy"); //$NON-NLS-1$

		// replace game start context panel
		addContextPanel(ContextPanelTypes.START, new StartContextPanel(this.getActionMap()));
		// FIXME (jan 07.12.2010) add game over panel
		addContextPanel(ContextPanelTypes.GAME_OVER, new StartContextPanel(this.getActionMap()));
		setGameState(GameState.GAME_START);

		return panel;
	}

	private ChatPanel getChatPanel() {

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

	/**
	 * Adds a new chat message to the chat
	 * 
	 * @param message
	 *            Chat message
	 */
	public void appendChatMessage(ISSChatMessage message) {

		chatPanel.addMessage(message);
	}
}
