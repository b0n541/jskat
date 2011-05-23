/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0
 * Build date: 2011-05-23 18:13:47
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.jskat.gui.iss;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.iss.ISSChatMessage;
import org.jskat.data.iss.ISSPlayerStatus;
import org.jskat.data.iss.ISSTablePanelStatus;
import org.jskat.gui.table.ContextPanelTypes;
import org.jskat.gui.table.JSkatUserPanel;
import org.jskat.gui.table.OpponentPanel;
import org.jskat.gui.table.SkatTablePanel;
import org.jskat.util.Player;

/**
 * Panel for ISS table
 */
public class ISSTablePanel extends SkatTablePanel {

	private static final long serialVersionUID = 1L;

	String loginName;
	ChatPanel chatPanel;

	// FIXME (jansch 05.04.2011) Dirty hack
	ISSTablePanelStatus lastTableStatus;

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
	public ISSTablePanel(String tableName, ActionMap actions,
			String newLoginName) {

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
		addContextPanel(ContextPanelTypes.START,
				new StartContextPanel(this.getActionMap()));
		// FIXME (jan 07.12.2010) add game over panel
		addContextPanel(ContextPanelTypes.GAME_OVER,
				new StartContextPanel(this.getActionMap()));
		setGameState(GameState.GAME_START);

		return panel;
	}

	protected OpponentPanel getOpponentPanel() {

		return new OpponentPanel(getActionMap(), 12, true);
	}

	protected JSkatUserPanel getPlayerPanel() {

		return new JSkatUserPanel(getActionMap(), 12, true);
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

		// FIXME (jansch 05.04.2011) make 3<>4 change possible
		// setMaxPlayers(tableStatus.getMaxPlayers());

		for (String playerName : tableStatus.getPlayerInformations().keySet()) {

			ISSPlayerStatus status = tableStatus
					.getPlayerInformation(playerName);

			if (!status.isPlayerLeft()) {
				addPlayerName(playerName);
			}
			setPlayerReadyToPlay(playerName, status.isReadyToPlay());
			setPlayerChatEnabled(playerName, status.isTalkEnabled());
			if (status.isPlayerLeft()) {
				removePlayerName(playerName);
			}
		}

		if (lastTableStatus == null || isNewGameResultAvailable(tableStatus)) {

			Map<Player, Integer> playerResults = extractPlayerResults(tableStatus);
			addGameResult(getDeclarer(), playerResults, 0);
		}

		lastTableStatus = tableStatus;
	}

	private boolean isNewGameResultAvailable(ISSTablePanelStatus tableStatus) {

		boolean result = false;

		for (String playerName : tableStatus.getPlayerInformations().keySet()) {

			ISSPlayerStatus newStatus = tableStatus
					.getPlayerInformation(playerName);
			ISSPlayerStatus oldStatus = lastTableStatus
					.getPlayerInformation(playerName);

			if (oldStatus != null && newStatus != null) {
				if (oldStatus.getGamesPlayed() != newStatus.getGamesPlayed()
						|| oldStatus.getLastGameResult() != newStatus
								.getLastGameResult()
						|| oldStatus.getTotalPoints() != newStatus
								.getTotalPoints()) {

					result = true;
				}
			}
		}

		return result;
	}

	private Map<Player, Integer> extractPlayerResults(
			ISSTablePanelStatus tableStatus) {

		Map<Player, Integer> result = new HashMap<Player, Integer>();

		for (String playerName : tableStatus.getPlayerInformations().keySet()) {

			ISSPlayerStatus status = tableStatus
					.getPlayerInformation(playerName);
			result.put(playerNamesAndPositions.get(playerName),
					status.getLastGameResult());
		}

		return result;
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
