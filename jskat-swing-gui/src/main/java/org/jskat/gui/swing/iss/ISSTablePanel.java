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
package org.jskat.gui.swing.iss;

import java.util.Arrays;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.PlayerStatus;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.swing.LayoutFactory;
import org.jskat.gui.swing.table.ContextPanelType;
import org.jskat.gui.swing.table.JSkatUserPanel;
import org.jskat.gui.swing.table.OpponentPanel;
import org.jskat.gui.swing.table.SkatTablePanel;

/**
 * Panel for ISS table
 */
public class ISSTablePanel extends SkatTablePanel {

	private static final long serialVersionUID = 1L;

	ChatPanel chatPanel;

	// FIXME (jansch 05.04.2011) Dirty hack
	TablePanelStatus lastTableStatus;

	/**
	 * Constructor
	 * 
	 * @param tableName
	 *            Table name
	 * @param actions
	 *            Action map
	 */
	public ISSTablePanel(final String tableName, final ActionMap actions) {

		super(tableName, actions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<JSkatAction> getGameOverActions() {
		return Arrays.asList(JSkatAction.INVITE_ISS_PLAYER,
				JSkatAction.READY_TO_PLAY, JSkatAction.TALK_ENABLED,
				JSkatAction.LEAVE_ISS_TABLE);
	}

	/**
	 * @see SkatTablePanel#getPlayGroundPanel()
	 */
	@Override
	protected JPanel getPlayGroundPanel() {

		final JPanel panel = new JPanel(LayoutFactory.getMigLayout(
				"fill,insets 0", "[grow][shrink]", //$NON-NLS-1$ //$NON-NLS-2$
				"fill")); //$NON-NLS-1$
		panel.add(super.getPlayGroundPanel(), "width 80%, grow"); //$NON-NLS-1$

		// replace game start context panel
		addContextPanel(
				ContextPanelType.START,
				new StartContextPanel(this.getActionMap(), getGameOverActions()));
		setGameState(GameState.GAME_START);

		return panel;
	}

	@Override
	protected JTabbedPane getLeftPanel() {

		JTabbedPane leftPanel = super.getLeftPanel();

		this.chatPanel = getChatPanel();
		this.chatPanel.addNewChat(
				this.strings.getString("table") + " " + getName(), getName()); //$NON-NLS-1$//$NON-NLS-2$
		leftPanel.add(this.strings.getString("chat"), this.chatPanel); //$NON-NLS-1$

		return leftPanel;
	}

	@Override
	protected OpponentPanel getOpponentPanel() {

		return new OpponentPanel(getActionMap(), 12, true);
	}

	@Override
	protected JSkatUserPanel createPlayerPanel() {

		return new JSkatUserPanel(getActionMap(), 12, true);
	}

	private ChatPanel getChatPanel() {

		return new ChatPanel(this);
	}

	@Override
	protected JPanel getRightPanelForTrickPanel() {
		final JPanel additionalActionsPanel = new JPanel(
				LayoutFactory.getMigLayout());
		additionalActionsPanel.setOpaque(false);

		final JButton resignButton = new JButton(getActionMap().get(
				JSkatAction.RESIGN));
		additionalActionsPanel.add(resignButton, "growx, wrap"); //$NON-NLS-1$
		final JButton showCardsButton = new JButton(getActionMap().get(
				JSkatAction.SHOW_CARDS));
		additionalActionsPanel.add(showCardsButton, "growx"); //$NON-NLS-1$

		return additionalActionsPanel;
	}

	/**
	 * Updates the panel with the new status
	 * 
	 * @param tableStatus
	 *            New table status
	 */
	public void setTableStatus(final TablePanelStatus tableStatus) {

		// FIXME (jansch 05.04.2011) make 3<>4 change possible
		// setMaxPlayers(tableStatus.getMaxPlayers());

		for (final String playerName : tableStatus.getPlayerInformations()
				.keySet()) {

			final PlayerStatus status = tableStatus
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

		this.lastTableStatus = tableStatus;
	}

	private void addPlayerName(final String playerName) {

		if (!this.playerNamesAndPositions.keySet().contains(playerName)) {

			this.playerNamesAndPositions.put(playerName, null);

			if (this.userPanel.getPlayerName() == null) {

				this.userPanel.setPlayerName(playerName);

			} else if (this.leftOpponentPanel.getPlayerName() == null) {

				this.leftOpponentPanel.setPlayerName(playerName);

			} else if (this.rightOpponentPanel.getPlayerName() == null) {

				this.rightOpponentPanel.setPlayerName(playerName);
			}
		}
	}

	private void removePlayerName(final String playerName) {

		if (this.playerNamesAndPositions.keySet().contains(playerName)) {

			this.playerNamesAndPositions.remove(playerName);

			if (playerName.equals(this.userPanel.getPlayerName())) {

				this.userPanel.setPlayerName(""); //$NON-NLS-1$

			} else if (playerName.equals(this.leftOpponentPanel.getPlayerName())) {

				this.leftOpponentPanel.setPlayerName(""); //$NON-NLS-1$

			} else if (playerName.equals(this.rightOpponentPanel.getPlayerName())) {

				this.rightOpponentPanel.setPlayerName(""); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Adds a new chat message to the chat
	 * 
	 * @param message
	 *            Chat message
	 */
	public void appendChatMessage(final ChatMessage message) {

		this.chatPanel.appendMessage(message);
	}
}
