/**
 * This file is part of JSkat.
 * <p>
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumnModel;

import org.jskat.data.iss.ChatMessage;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.swing.AbstractTabPanel;
import org.jskat.gui.swing.LayoutFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the lobby of the ISS with an overview about players and tables
 * that are currently online
 */
public class LobbyPanel extends AbstractTabPanel {

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(LobbyPanel.class);

	private PlayerListTableModel playerListTableModel;
	private JTable playerListTable;
	private JScrollPane playerListScrollPane;

	private TableListTableModel tableListTableModel;
	JTable tableListTable;
	private JScrollPane tableListScrollPane;
	private ChatPanel chatPanel;

	static ActionMap actions;

	/**
	 * Constructor
	 *
	 * @param tableName
	 *            Table name
	 * @param actions
	 *            Action
	 */
	public LobbyPanel(final String tableName, final ActionMap actions) {

		super(tableName, actions);

		log.debug("SkatTablePanel: name: " + tableName); //$NON-NLS-1$
	}

	/**
	 * @see org.jskat.gui.swing.AbstractTabPanel#initPanel()
	 */
	@Override
	protected void initPanel() {

		setLayout(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$

		add(getLobbyPanel(), "center"); //$NON-NLS-1$

		LobbyPanel.actions = getActionMap();
	}

	private JPanel getLobbyPanel() {

		final JPanel lobby = new JPanel(LayoutFactory.getMigLayout("fill", "fill", //$NON-NLS-1$ //$NON-NLS-2$
				"[shrink][shrink][shrink][shrink][grow]")); //$NON-NLS-1$

		final JLabel headerLabel = new JLabel(this.strings.getString("welcome_to_iss_title")); //$NON-NLS-1$
		headerLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 32));
		final JPanel headerPanel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
		headerPanel.add(headerLabel, "center"); //$NON-NLS-1$

		lobby.add(headerPanel, "span 2, growx, align center, wrap"); //$NON-NLS-1$
		lobby.add(new JLabel(this.strings.getString("players")), "width 50%"); //$NON-NLS-1$ //$NON-NLS-2$
		lobby.add(new JLabel(this.strings.getString("tables")), "wrap"); //$NON-NLS-1$ //$NON-NLS-2$

		lobby.add(getPlayerListPanel());
		lobby.add(getTableListPanel(), "wrap"); //$NON-NLS-1$

		lobby.add(getActionButtonPanel(), "span 2, wrap"); //$NON-NLS-1$

		this.chatPanel = new ChatPanel(this);
		lobby.add(this.chatPanel, "span 2, hmin 200px, growy, align center"); //$NON-NLS-1$

		lobby.setPreferredSize(new Dimension(800, 600));

		return lobby;
	}

	private JPanel getPlayerListPanel() {

		final JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.playerListTableModel = new PlayerListTableModel();
		this.playerListTable = new JTable(this.playerListTableModel);

		this.playerListTable.setAutoCreateRowSorter(true);

		final TableColumnModel columnModel = this.playerListTable.getColumnModel();
		columnModel.getColumn(2).setCellRenderer(new PlayerStrengthTableCellRenderer());
		columnModel.getColumn(3).setCellRenderer(new FlagTableCellRenderer());

		this.playerListTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		this.playerListScrollPane = new JScrollPane(this.playerListTable);
		this.playerListScrollPane.setPreferredSize(new Dimension(300, 200));
		this.playerListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		panel.add(this.playerListScrollPane);

		return panel;
	}

	private JPanel getTableListPanel() {

		final JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.tableListTableModel = new TableListTableModel();
		this.tableListTable = new JTable(this.tableListTableModel);

		this.tableListTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(final MouseEvent e) {

				final int column = LobbyPanel.this.tableListTable.getSelectedColumn();
				final int row = LobbyPanel.this.tableListTable.getSelectedRow();
				final String tableName = (String) LobbyPanel.this.tableListTable.getValueAt(row, 0);
				final String value = (String) LobbyPanel.this.tableListTable.getValueAt(row, column);

				if (column == 0) {
					// observe a table
					// FIXME (jansch 27.04.2011) doesn't work
					// LobbyPanel.actions.get(JSkatAction.OBSERVE_ISS_TABLE).actionPerformed(
					// new ActionEvent(tableName, 1, null));
				} else if (value.equals(".")) { //$NON-NLS-1$
					// sit down on free seat at table
					LobbyPanel.actions.get(JSkatAction.JOIN_ISS_TABLE)
							.actionPerformed(new ActionEvent(tableName, 1, null));
				}
			}
		});

		this.tableListTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		this.tableListScrollPane = new JScrollPane(this.tableListTable);
		this.tableListScrollPane.setPreferredSize(new Dimension(400, 200));
		this.tableListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		panel.add(this.tableListScrollPane);

		return panel;
	}

	private JPanel getActionButtonPanel() {

		final JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		panel.add(new JButton(getActionMap().get(JSkatAction.CREATE_ISS_TABLE)), "width 50%"); //$NON-NLS-1$
		panel.add(new JButton(getActionMap().get(JSkatAction.DISCONNECT_FROM_ISS)), "width 50%"); //$NON-NLS-1$

		return panel;
	}

	/**
	 * Updates player information
	 *
	 * @param playerName
	 *            Player name
	 * @param language
	 *            Language
	 * @param gamesPlayed
	 *            Games played
	 * @param strength
	 *            Play strength
	 */
	public void updatePlayer(final String playerName, final String language, final long gamesPlayed,
			final double strength) {

		this.playerListTableModel.updatePlayer(playerName, language, gamesPlayed, strength);
	}

	/**
	 * Removes a player from the player list
	 *
	 * @param playerName
	 *            Player name
	 */
	public void removePlayer(final String playerName) {

		this.playerListTableModel.removePlayer(playerName);
	}

	/**
	 * Updates table information
	 *
	 * @param tableName
	 *            Table name
	 * @param maxPlayers
	 *            Maximum number of players
	 * @param gamesPlayed
	 *            Games played
	 * @param player1
	 *            Player 1 (? for free seat)
	 * @param player2
	 *            Player 2 (? for free seat)
	 * @param player3
	 *            Player 3 (? for free seat)
	 */
	public void updateTable(final String tableName, final int maxPlayers, final long gamesPlayed, final String player1,
			final String player2, final String player3) {

		this.tableListTableModel.updateTable(tableName, maxPlayers, gamesPlayed, player1, player2, player3);
	}

	/**
	 * Removes a table from the table list
	 *
	 * @param tableName
	 *            Table name
	 */
	public void removeTable(final String tableName) {

		this.tableListTableModel.removeTable(tableName);
	}

	/**
	 * Adds a new chat message
	 *
	 * @param message
	 *            New message
	 */
	public void appendChatMessage(final ChatMessage message) {

		log.debug("Appending chat message: " + message); //$NON-NLS-1$

		this.chatPanel.appendMessage(message);
	}

	@Override
	protected void setFocus() {
		this.chatPanel.setFocus();
	}
}
