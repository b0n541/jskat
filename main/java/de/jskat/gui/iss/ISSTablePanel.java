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
import de.jskat.data.iss.ISSPlayerStatus;
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

		for (String playerName : tableStatus.getPlayerInformations().keySet()) {

			ISSPlayerStatus status = tableStatus.getPlayerInformation(playerName);

			if (!status.isPlayerLeft()) {
				addPlayerName(playerName);
			}
			setPlayerReadyToPlay(playerName, status.isReadyToPlay());
			setPlayerChatEnabled(playerName, status.isTalkEnabled());
			if (status.isPlayerLeft()) {
				removePlayerName(playerName);
			}
		}
	}

	private void addPlayerName(String playerName) {

		if (!playerNamesAndPositions.keySet().contains(playerName)) {

			playerNamesAndPositions.put(playerName, null);

			if (userPanel.getPlayerName() == null) {

				userPanel.setPlayerName(playerName);

			} else if (leftOpponentPanel.getPlayerName() == null) {

				leftOpponentPanel.setPlayerName(playerName);

			} else if (rightOpponentPanel.getPlayerName() == null) {

				rightOpponentPanel.setPlayerName(playerName);
			}
		}
	}

	private void removePlayerName(String playerName) {

		if (playerNamesAndPositions.keySet().contains(playerName)) {

			playerNamesAndPositions.remove(playerName);

			if (playerName.equals(userPanel.getPlayerName())) {

				userPanel.setPlayerName(""); //$NON-NLS-1$

			} else if (playerName.equals(leftOpponentPanel.getPlayerName())) {

				leftOpponentPanel.setPlayerName(""); //$NON-NLS-1$

			} else if (playerName.equals(rightOpponentPanel.getPlayerName())) {

				rightOpponentPanel.setPlayerName(""); //$NON-NLS-1$
			}
		}
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
