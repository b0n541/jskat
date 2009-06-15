/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.iss;

import java.awt.Dimension;
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

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.iss.ISSChatMessage;
import de.jskat.gui.JSkatTabPanel;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Represents the lobby of the ISS with an overview about players and tables
 * that are currently online
 */
public class LobbyPanel extends JSkatTabPanel {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(LobbyPanel.class);

	private PlayerListTableModel playerListTableModel;
	private JTable playerListTable;
	private JScrollPane playerListScrollPane;

	private TableListTableModel tableListTableModel;
	JTable tableListTable;
	private JScrollPane tableListScrollPane;
	private ChatPanel chatPanel;
	
	private static ActionMap actions;
	
	/**
	 * Constructor
	 * 
	 * @param tableName
	 * @param jskatBitmaps
	 * @param actions
	 */
	public LobbyPanel(String tableName, JSkatGraphicRepository jskatBitmaps,
			ActionMap actions) {

		super(tableName, jskatBitmaps, actions);
		
		log.debug("SkatTablePanel: name: " + tableName); //$NON-NLS-1$
	}

	/**
	 * @see de.jskat.gui.JSkatTabPanel#initPanel()
	 */
	@Override
	protected void initPanel() {

		setLayout(new MigLayout("fill")); //$NON-NLS-1$

		add(getLobbyPanel(), "center"); //$NON-NLS-1$
		
		this.actions = this.getActionMap();
	}

	private JPanel getLobbyPanel() {

		JPanel lobby = new JPanel(new MigLayout());

		lobby
				.add(
						new JLabel("Welcome to the International Skat Server"), "span 2, align center, wrap"); //$NON-NLS-1$ //$NON-NLS-2$
		lobby.add(new JLabel("Players")); //$NON-NLS-1$
		lobby.add(new JLabel("Tables"), "wrap"); //$NON-NLS-1$ //$NON-NLS-2$

		lobby.add(getPlayerListPanel());
		lobby.add(getTableListPanel(), "wrap"); //$NON-NLS-1$

		lobby.add(getActionButtonPanel(), "span 2, wrap");
		
		this.chatPanel = new ChatPanel(this);
		lobby.add(this.chatPanel, "span 2, growx, align center"); //$NON-NLS-1$
		
		return lobby;
	}

	private JPanel getPlayerListPanel() {

		JPanel panel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.playerListTableModel = new PlayerListTableModel();
		this.playerListTable = new JTable(this.playerListTableModel);

		this.playerListTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		this.playerListScrollPane = new JScrollPane(this.playerListTable);
		this.playerListScrollPane.setPreferredSize(new Dimension(300, 200));
		this.playerListScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		panel.add(this.playerListScrollPane);

		return panel;
	}

	private JPanel getTableListPanel() {

		JPanel panel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.tableListTableModel = new TableListTableModel();
		this.tableListTable = new JTable(this.tableListTableModel);

		this.tableListTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				int column = LobbyPanel.this.tableListTable.getSelectedColumn();
				int row = LobbyPanel.this.tableListTable.getSelectedRow();
				String tableName = (String) LobbyPanel.this.tableListTable.getValueAt(row, 0);
				String value = (String) LobbyPanel.this.tableListTable.getValueAt(row, column);
				
				if (column == 0) {
					// TODO kiebitz
				}
				else if (value.equals("?")) {
					// sit down on free seat at table
					LobbyPanel.actions.get(JSkatAction.JOIN_ISS_TABLE).actionPerformed(new ActionEvent(tableName, 1, null));
				}
				else {
					
					log.debug("No free seat!");
				}
			}
		});
		
		this.tableListTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		this.tableListScrollPane = new JScrollPane(this.tableListTable);
		this.tableListScrollPane.setPreferredSize(new Dimension(400, 200));
		this.tableListScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		panel.add(this.tableListScrollPane);

		return panel;
	}

	private JPanel getActionButtonPanel() {
		
		JPanel panel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		panel.add(new JButton(this.getActionMap().get(JSkatAction.CREATE_ISS_TABLE)));
		panel.add(new JButton("Join table"));
		
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
	public void updatePlayer(String playerName, String language,
			long gamesPlayed, double strength) {

		this.playerListTableModel.updatePlayer(playerName, language,
				gamesPlayed, strength);
	}

	/**
	 * Removes a player from the player list
	 * 
	 * @param playerName
	 *            Player name
	 */
	public void removePlayer(String playerName) {

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
	public void updateTable(String tableName, int maxPlayers, long gamesPlayed,
			String player1, String player2, String player3) {

		this.tableListTableModel.updatePlayer(tableName, maxPlayers,
				gamesPlayed, player1, player2, player3);
	}

	/**
	 * Removes a table from the table list
	 * 
	 * @param tableName Table name
	 */
	public void removeTable(String tableName) {

		this.tableListTableModel.removeTable(tableName);
	}
	
	/**
	 * Adds a new chat message
	 * 
	 * @param message New message
	 */
	public void appendChatMessage(ISSChatMessage message) {
		
		log.debug("appendChatMessage");
		
		this.chatPanel.addMessage(message);
	}
}
