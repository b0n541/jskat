package de.jskat.gui.iss;

import java.awt.Dimension;

import javax.swing.ActionMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.gui.JSkatTabPanel;
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
	}

	private JPanel getLobbyPanel() {

		JPanel lobby = new JPanel(new MigLayout());

		lobby
				.add(
						new JLabel("Welcome to the International Skat Server"), "span 2, align center, wrap"); //$NON-NLS-1$ //$NON-NLS-2$
		lobby.add(new JLabel("Players")); //$NON-NLS-1$
		lobby.add(new JLabel("Tables"), "wrap"); //$NON-NLS-1$ //$NON-NLS-2$

		lobby.add(getPlayerListPanel());

		return lobby;
	}

	private JPanel getPlayerListPanel() {

		JPanel panel = new JPanel(new MigLayout("fill", "fill", "fill")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.playerListTableModel = new PlayerListTableModel();
		this.playerListTable = new JTable(this.playerListTableModel);

		this.playerListTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		this.playerListScrollPane = new JScrollPane(this.playerListTable);
		this.playerListScrollPane.setPreferredSize(new Dimension(250, 400));
		this.playerListScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		panel.add(this.playerListScrollPane);

		return panel;
	}

	/**
	 * Updates player information
	 * 
	 * @param playerName Player name
	 * @param language Language
	 * @param gamesPlayed Games played
	 * @param strength Play strength
	 */
	public void updatePlayer(String playerName, String language,
			long gamesPlayed, double strength) {

		this.playerListTableModel.updatePlayer(playerName, language,
				gamesPlayed, strength);
	}
}
