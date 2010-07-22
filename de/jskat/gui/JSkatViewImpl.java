/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.control.JSkatMaster;
import de.jskat.control.SkatTable;
import de.jskat.control.iss.ChatMessageType;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameState;
import de.jskat.data.iss.ISSChatMessage;
import de.jskat.data.iss.ISSGameStartInformation;
import de.jskat.data.iss.ISSMoveInformation;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.data.iss.MovePlayer;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.action.human.ContinueSkatSeriesAction;
import de.jskat.gui.action.human.DiscardAction;
import de.jskat.gui.action.human.GameAnnounceAction;
import de.jskat.gui.action.human.HoldBidAction;
import de.jskat.gui.action.human.LookIntoSkatAction;
import de.jskat.gui.action.human.PassBidAction;
import de.jskat.gui.action.human.PlayCardAction;
import de.jskat.gui.action.human.PlayHandGameAction;
import de.jskat.gui.action.human.PutCardIntoSkatAction;
import de.jskat.gui.action.human.TakeCardFromSkatAction;
import de.jskat.gui.action.iss.ChangeTableSeatsAction;
import de.jskat.gui.action.iss.ConnectAction;
import de.jskat.gui.action.iss.CreateISSTableAction;
import de.jskat.gui.action.iss.InvitePlayerAction;
import de.jskat.gui.action.iss.JoinISSTableAction;
import de.jskat.gui.action.iss.LeaveISSTableAction;
import de.jskat.gui.action.iss.ObserveISSTableAction;
import de.jskat.gui.action.iss.ReadyAction;
import de.jskat.gui.action.iss.SendChatMessageAction;
import de.jskat.gui.action.iss.ShowLoginPanelAction;
import de.jskat.gui.action.iss.TalkEnableAction;
import de.jskat.gui.action.main.AboutAction;
import de.jskat.gui.action.main.ChangeActiveTableAction;
import de.jskat.gui.action.main.CreateTableAction;
import de.jskat.gui.action.main.ExitAction;
import de.jskat.gui.action.main.HelpAction;
import de.jskat.gui.action.main.LicenseAction;
import de.jskat.gui.action.main.LoadNeuralNetworksAction;
import de.jskat.gui.action.main.LoadSeriesAction;
import de.jskat.gui.action.main.PauseSkatSeriesAction;
import de.jskat.gui.action.main.PreferencesAction;
import de.jskat.gui.action.main.SaveNeuralNetworksAction;
import de.jskat.gui.action.main.SaveSeriesAction;
import de.jskat.gui.action.main.SaveSeriesAsAction;
import de.jskat.gui.action.main.StartSkatSeriesAction;
import de.jskat.gui.action.main.TrainNeuralNetworksAction;
import de.jskat.gui.help.JSkatHelpDialog;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.iss.ISSTablePanel;
import de.jskat.gui.iss.LobbyPanel;
import de.jskat.gui.iss.LoginPanel;
import de.jskat.gui.iss.PlayerInvitationPanel;
import de.jskat.gui.table.HandPanelType;
import de.jskat.gui.table.SkatSeriesStartDialog;
import de.jskat.gui.table.SkatTablePanel;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.Player;

/**
 * Implementation of JSkatView interface
 */
public class JSkatViewImpl implements IJSkatView {

	static Log log = LogFactory.getLog(JSkatViewImpl.class);

	private JFrame mainFrame;
	private SkatSeriesStartDialog skatSeriesStartDialog;
	private JSkatPreferencesDialog preferencesDialog;
	private JTabbedPane tabs;
	private Map<String, SkatTablePanel> tables;
	private JSkatGraphicRepository bitmaps;
	private ResourceBundle strings;
	ActionMap actions;

	private LobbyPanel issLobby;

	/**
	 * Constructor
	 * 
	 * @param jskat
	 *            JSkatMaster
	 * @param jskatBitmaps
	 *            Bitmaps for JSkat
	 */
	public JSkatViewImpl(JSkatMaster jskat, JSkatGraphicRepository jskatBitmaps) {

		this.bitmaps = jskatBitmaps;
		// TODO make this setable
		this.strings = ResourceBundle.getBundle("de/jskat/i18n/i18n", //$NON-NLS-1$
				new Locale("de", "DE")); //$NON-NLS-1$//$NON-NLS-2$
		this.tables = new HashMap<String, SkatTablePanel>();
		initActionMap(jskat);
		initGUI(jskat);

		this.skatSeriesStartDialog = new SkatSeriesStartDialog(jskat,
				this.mainFrame);
		this.preferencesDialog = new JSkatPreferencesDialog(jskat,
				this.mainFrame);

		this.mainFrame.setVisible(true);
	}

	private void initActionMap(JSkatMaster jskat) {

		this.actions = new ActionMap();

		// common actions
		this.actions.put(JSkatAction.LOAD_SERIES, new LoadSeriesAction(jskat,
				this.bitmaps, this.strings));
		this.actions.put(JSkatAction.SAVE_SERIES, new SaveSeriesAction(jskat,
				this.bitmaps, this.strings));
		this.actions.put(JSkatAction.SAVE_SERIES_AS, new SaveSeriesAsAction(
				jskat, this.bitmaps, this.strings));
		this.actions.put(JSkatAction.HELP, new HelpAction(jskat, this.bitmaps,
				this.strings));
		this.actions.put(JSkatAction.LICENSE, new LicenseAction(jskat,
				this.bitmaps, this.strings));
		this.actions.put(JSkatAction.EXIT_JSKAT, new ExitAction(jskat,
				this.bitmaps, this.strings));
		this.actions.put(JSkatAction.PREFERENCES, new PreferencesAction(jskat,
				this.bitmaps, this.strings));
		this.actions.put(JSkatAction.ABOUT_JSKAT, new AboutAction(jskat,
				this.bitmaps, this.strings));
		this.actions.put(JSkatAction.CHANGE_ACTIVE_TABLE,
				new ChangeActiveTableAction(jskat));
		// skat table actions
		this.actions.put(JSkatAction.CREATE_LOCAL_TABLE, new CreateTableAction(
				jskat, this.bitmaps, this.strings));
		this.actions.put(JSkatAction.START_LOCAL_SERIES,
				new StartSkatSeriesAction(jskat, this.bitmaps, this.strings));
		this.actions
				.put(JSkatAction.CONTINUE_LOCAL_SERIES,
						new ContinueSkatSeriesAction(jskat, this.bitmaps,
								this.strings));
		this.actions.put(JSkatAction.PAUSE_LOCAL_SERIES,
				new PauseSkatSeriesAction(jskat, this.bitmaps, this.strings));
		// ISS actions
		this.actions.put(JSkatAction.SHOW_ISS_LOGIN, new ShowLoginPanelAction(
				jskat, this.bitmaps));
		this.actions.put(JSkatAction.CONNECT_TO_ISS, new ConnectAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatAction.SEND_CHAT_MESSAGE,
				new SendChatMessageAction(jskat));
		this.actions.put(JSkatAction.CREATE_ISS_TABLE,
				new CreateISSTableAction(jskat));
		this.actions.put(JSkatAction.JOIN_ISS_TABLE, new JoinISSTableAction(
				jskat));
		this.actions.put(JSkatAction.LEAVE_ISS_TABLE, new LeaveISSTableAction(
				jskat));
		this.actions.put(JSkatAction.OBSERVE_ISS_TABLE,
				new ObserveISSTableAction(jskat));
		this.actions.put(JSkatAction.READY_TO_PLAY, new ReadyAction(jskat));
		this.actions.put(JSkatAction.TALK_ENABLED, new TalkEnableAction(jskat));
		this.actions.put(JSkatAction.CHANGE_TABLE_SEATS,
				new ChangeTableSeatsAction(jskat));
		this.actions.put(JSkatAction.INVITE_ISS_PLAYER, new InvitePlayerAction(
				jskat));
		// Neural network actions
		this.actions
				.put(JSkatAction.TRAIN_NEURAL_NETWORKS,
						new TrainNeuralNetworksAction(jskat, this.bitmaps,
								this.strings));
		this.actions
				.put(JSkatAction.LOAD_NEURAL_NETWORKS,
						new LoadNeuralNetworksAction(jskat, this.bitmaps,
								this.strings));
		this.actions
				.put(JSkatAction.SAVE_NEURAL_NETWORKS,
						new SaveNeuralNetworksAction(jskat, this.bitmaps,
								this.strings));
		// Human player actions
		this.actions.put(JSkatAction.HOLD_BID, new HoldBidAction(jskat));
		this.actions.put(JSkatAction.PASS_BID, new PassBidAction(jskat));
		this.actions.put(JSkatAction.LOOK_INTO_SKAT, new LookIntoSkatAction(
				jskat));
		this.actions.put(JSkatAction.PLAY_HAND_GAME, new PlayHandGameAction(
				jskat));
		this.actions.put(JSkatAction.ANNOUNCE_GAME, new GameAnnounceAction(
				jskat));
		this.actions.put(JSkatAction.PUT_CARD_INTO_SKAT,
				new PutCardIntoSkatAction(jskat));
		this.actions.put(JSkatAction.TAKE_CARD_FROM_SKAT,
				new TakeCardFromSkatAction(jskat));
		this.actions.put(JSkatAction.DISCARD_CARDS, new DiscardAction(jskat));
		this.actions.put(JSkatAction.PLAY_CARD, new PlayCardAction(jskat));
	}

	private void initGUI(JSkatMaster jskat) {

		this.mainFrame = new JFrame("JSkat"); //$NON-NLS-1$
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setPreferredSize(new Dimension(1000, 700));

		this.mainFrame.setIconImage(this.bitmaps.getIconImage(
				JSkatGraphicRepository.Icon.JSKAT,
				JSkatGraphicRepository.IconSize.BIG));

		this.mainFrame.setJMenuBar(getMenuBar());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		// symbol button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout());
		buttonPanel.add(new ToolbarButton(this.actions
				.get(JSkatAction.CREATE_LOCAL_TABLE)));
		buttonPanel.add(new ToolbarButton(this.actions
				.get(JSkatAction.START_LOCAL_SERIES)));
		buttonPanel.add(new ToolbarButton(this.actions
				.get(JSkatAction.SHOW_ISS_LOGIN)));
		buttonPanel.add(new ToolbarButton(this.actions.get(JSkatAction.HELP)));
		mainPanel.add(buttonPanel, BorderLayout.NORTH);

		// main area
		this.tabs = new JTabbedPane();
		this.tabs.setAutoscrolls(true);
		this.tabs.addChangeListener(new ChangeListener() {
			/**
			 * @see ChangeListener#stateChanged(ChangeEvent)
			 */

			public void stateChanged(ChangeEvent e) {

				if (e.getSource() instanceof JTabbedPane) {

					JTabbedPane changedTabs = (JTabbedPane) e.getSource();
					Component tab = changedTabs.getSelectedComponent();

					if (tab instanceof AbstractTabPanel) {

						String tableName = ((AbstractTabPanel) tab).getName();
						log.debug("showing table pane of table " + tableName); //$NON-NLS-1$

						JSkatViewImpl.this.actions.get(
								JSkatAction.CHANGE_ACTIVE_TABLE)
								.actionPerformed(
										new ActionEvent(e.getSource(), 1,
												tableName));
					}
				}
			}
		});

		mainPanel.add(this.tabs, BorderLayout.CENTER);

		this.mainFrame.setContentPane(mainPanel);
		this.mainFrame.pack();
	}

	private JMenuBar getMenuBar() {

		JMenuBar menu = new JMenuBar();

		JMenu fileMenu = new JMenu(this.strings.getString("file")); //$NON-NLS-1$
		fileMenu.add(new JMenuItem(this.actions.get(JSkatAction.LOAD_SERIES)));
		fileMenu.add(new JMenuItem(this.actions.get(JSkatAction.SAVE_SERIES)));
		fileMenu
				.add(new JMenuItem(this.actions.get(JSkatAction.SAVE_SERIES_AS)));
		fileMenu.add(new JSeparator());
		fileMenu.add(new JMenuItem(this.actions.get(JSkatAction.EXIT_JSKAT)));
		menu.add(fileMenu);

		JMenu tableMenu = new JMenu(this.strings.getString("skat_table")); //$NON-NLS-1$
		tableMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.CREATE_LOCAL_TABLE)));
		tableMenu.add(new JSeparator());
		tableMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.START_LOCAL_SERIES)));
		tableMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.PAUSE_LOCAL_SERIES)));
		tableMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.CONTINUE_LOCAL_SERIES)));
		menu.add(tableMenu);

		JMenu neuralNetworkMenu = new JMenu(this.strings
				.getString("neural_networks")); //$NON-NLS-1$
		neuralNetworkMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.LOAD_NEURAL_NETWORKS)));
		neuralNetworkMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.SAVE_NEURAL_NETWORKS)));
		neuralNetworkMenu.add(new JSeparator());
		neuralNetworkMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.TRAIN_NEURAL_NETWORKS)));
		menu.add(neuralNetworkMenu);

		JMenu issMenu = new JMenu("ISS");
		issMenu
				.add(new JMenuItem(this.actions.get(JSkatAction.SHOW_ISS_LOGIN)));
		issMenu.add(new JSeparator());
		issMenu.add(new JMenuItem("Create new skat table"));
		issMenu.add(new JMenuItem("Invite player"));
		menu.add(issMenu);

		JMenu extraMenu = new JMenu(this.strings.getString("extras")); //$NON-NLS-1$
		extraMenu.add(new JMenuItem(this.actions.get(JSkatAction.PREFERENCES)));
		menu.add(extraMenu);

		JMenu helpMenu = new JMenu(this.strings.getString("help")); //$NON-NLS-1$
		helpMenu.add(new JMenuItem(this.actions.get(JSkatAction.HELP)));
		helpMenu.add(new JSeparator());
		helpMenu.add(new JMenuItem(this.actions.get(JSkatAction.LICENSE)));
		helpMenu.add(new JMenuItem(this.actions.get(JSkatAction.ABOUT_JSKAT)));
		menu.add(helpMenu);

		return menu;
	}

	/**
	 * @see IJSkatView#showTable(SkatTable)
	 */
	@Override
	public void showTable(SkatTable table) {
		// TODO implement it
	}

	/**
	 * @see IJSkatView#startSeries(String)
	 */
	@Override
	public void startSeries(String tableName) {

		this.tables.get(tableName).clearSkatList();
	}

	/**
	 * @see IJSkatView#showSeriesResults()
	 */
	@Override
	public void showSeriesResults() {
		// TODO implement it
	}

	/**
	 * @see IJSkatView#startGame(String)
	 */
	@Override
	public void startGame(String tableName) {

		this.tables.get(tableName).startGame();
	}

	/**
	 * @see IJSkatView#startBidding()
	 */
	@Override
	public void startBidding() {
		// TODO implement it
	}

	/**
	 * @see IJSkatView#startDiscarding()
	 */
	@Override
	public void startDiscarding() {
		// TODO implement it
	}

	/**
	 * @see IJSkatView#startPlaying()
	 */
	@Override
	public void startPlaying() {
		// TODO implement it
	}

	/**
	 * @see IJSkatView#showGameResults()
	 */
	@Override
	public void showGameResults() {
		// TODO implement it
	}

	/**
	 * @see IJSkatView#createISSTable(String)
	 */
	@Override
	public void createISSTable(String name) {
		// FIXME not needed as separate method,
		// do it in createSkatTablePanel
		ISSTablePanel newTable = new ISSTablePanel(name, this.bitmaps,
				this.actions, this.strings);
		addTabPanel(newTable, "ISS table: " + name);
		this.tables.put(name, newTable);
	}

	/**
	 * @see IJSkatView#createSkatTablePanel(String)
	 */
	@Override
	public void createSkatTablePanel(String name) {

		SkatTablePanel newPanel = new SkatTablePanel(name, this.bitmaps,
				this.actions, this.strings);
		addTabPanel(newPanel, name);
		this.tables.put(name, newPanel);

		this.actions.get(JSkatAction.START_LOCAL_SERIES).setEnabled(true);
	}

	/**
	 * @see IJSkatView#showAboutMessage()
	 */
	@Override
	public void showAboutMessage() {
		// TODO save text for message centrally
		JOptionPane
				.showMessageDialog(
						this.mainFrame,
						"JSkat Version 0.7\n\n"
								+ "Authors: Jan Schaefer, Markus J. Luzius\n\n"
								+ "Icons: Gnome Desktop Icons, Tango project, Silvestre Herrera and Alex Roberts\n\n"
								+ "This program comes with ABSOLUTELY NO WARRANTY; for details see licence dialog\n"
								+ "This is free software, and you are welcome to redistribute it\n"
								+ "under certain conditions; see licence dialog for details.",
						this.strings.getString("about"), JOptionPane.INFORMATION_MESSAGE, //$NON-NLS-1$
						new ImageIcon(this.bitmaps.getJSkatLogoImage()));
	}

	/**
	 * @see IJSkatView#showMessage(int, String)
	 */
	@Override
	public void showMessage(int messageType, String message) {

		JOptionPane.showMessageDialog(this.mainFrame, message, null,
				messageType);
	}

	/**
	 * @see IJSkatView#showExitDialog()
	 */
	@Override
	public int showExitDialog() {

		return JOptionPane.showOptionDialog(this.mainFrame, this.strings
				.getString("exit_dialog_message"), this.strings //$NON-NLS-1$
				.getString("exit_dialog_title"), JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
				JOptionPane.QUESTION_MESSAGE, null, null, null);
	}

	/**
	 * @see IJSkatView#addCard(String, Player, Card)
	 */
	@Override
	public void addCard(String tableName, Player player, Card card) {

		this.tables.get(tableName).addCard(player, card);
	}

	/**
	 * @see IJSkatView#clearHand(String, Player)
	 */
	@Override
	public void clearHand(String tableName, Player player) {

		this.tables.get(tableName).clearHand(player);
	}

	/**
	 * @see IJSkatView#removeCard(String, Player, Card)
	 */
	@Override
	public void removeCard(String tableName, Player player, Card card) {

		this.tables.get(tableName).removeCard(player, card);
	}

	/**
	 * @see IJSkatView#setPositions(String, Player, Player, Player)
	 */
	@Override
	public void setPositions(String tableName, Player leftPosition,
			Player rightPosition, Player playerPosition) {

		this.tables.get(tableName).setPositions(leftPosition, rightPosition,
				playerPosition);
	}

	/**
	 * @see IJSkatView#setTrickCard(String, Player, Card)
	 */
	@Override
	public void setTrickCard(String tableName, Player position, Card card) {

		this.tables.get(tableName).setTrickCard(position, card);
	}

	/**
	 * @see IJSkatView#clearTrickCards(String)
	 */
	@Override
	public void clearTrickCards(String tableName) {

		this.tables.get(tableName).clearTrickCards();
	}

	/**
	 * @see IJSkatView#setGameAnnouncement(String, GameAnnouncement)
	 */
	@Override
	public void setGameAnnouncement(String tableName, GameAnnouncement ann) {

		this.tables.get(tableName).setGameAnnouncement(ann);
	}

	/**
	 * @see IJSkatView#setGameState(String, GameState)
	 */
	@Override
	public void setGameState(String tableName, GameState state) {

		setActions(state);
		this.tables.get(tableName).setGameState(state);
	}

	void setActions(GameState state) {

		switch (state) {
		case NEW_GAME:
			this.actions.get(JSkatAction.CONTINUE_LOCAL_SERIES).setEnabled(
					false);
			break;
		case GAME_OVER:
			this.actions.get(JSkatAction.CONTINUE_LOCAL_SERIES)
					.setEnabled(true);
			break;
		}
	}

	/**
	 * @see IJSkatView#addGameResult(String, SkatGameData)
	 */
	@Override
	public void addGameResult(String tableName, SkatGameData data) {

		this.tables.get(tableName).addGameResult(data);
	}

	/**
	 * @see IJSkatView#showHelpDialog()
	 */
	@Override
	public void showHelpDialog() {

		new JSkatHelpDialog(
				this.mainFrame,
				this.strings.getString("help"), "de/jskat/gui/help/jskat_help.html", this.strings) //$NON-NLS-1$ //$NON-NLS-2$
				.setVisible(true);
	}

	/**
	 * @see IJSkatView#showLicenseDialog()
	 */
	@Override
	public void showLicenseDialog() {

		new JSkatHelpDialog(
				this.mainFrame,
				this.strings.getString("license"), "de/jskat/gui/help/gpl3.html", this.strings).setVisible(true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see IJSkatView#clearTable(String)
	 */
	@Override
	public void clearTable(String tableName) {

		this.tables.get(tableName).clearTable();
	}

	/**
	 * @see IJSkatView#setNextBidValue(String, int)
	 */
	@Override
	public void setNextBidValue(String tableName, int nextBidValue) {

		// TODO this should be set for every table seperately
		this.actions.get(JSkatAction.HOLD_BID).putValue(Action.NAME,
				Integer.toString(nextBidValue));
	}

	/**
	 * @see IJSkatView#setBid(String, Player, int)
	 */
	@Override
	public void setBid(String tableName, Player player, int bidValue) {

		this.tables.get(tableName).setBid(player, bidValue);
	}

	/**
	 * @see IJSkatView#setTrickForeHand(String, Player)
	 */
	@Override
	public void setTrickForeHand(String tableName, Player trickForeHand) {

		this.tables.get(tableName).setTrickForeHand(trickForeHand);
	}

	/**
	 * @see IJSkatView#putCardIntoSkat(String, Card)
	 */
	@Override
	public void putCardIntoSkat(String tableName, Card card) {

		this.tables.get(tableName).putCardIntoSkat(card);
	}

	/**
	 * @see IJSkatView#takeCardFromSkat(String, Card)
	 */
	@Override
	public void takeCardFromSkat(String tableName, Card card) {

		this.tables.get(tableName).takeCardFromSkat(card);
	}

	/**
	 * @see IJSkatView#showStartSkatSeriesDialog()
	 */
	@Override
	public void showStartSkatSeriesDialog() {

		this.skatSeriesStartDialog.setVisible(true);
	}

	/**
	 * @see IJSkatView#showISSLogin()
	 */
	@Override
	public void showISSLogin() {

		LoginPanel loginPanel = new LoginPanel("ISS login", this.bitmaps, //$NON-NLS-1$ //$NON-NLS-2$
				this.actions, this.strings);
		addTabPanel(loginPanel, "ISS login");
	}

	/**
	 * @see IJSkatView#updateISSLobbyPlayerList(String, String, long, double)
	 */
	@Override
	public void updateISSLobbyPlayerList(String playerName, String language,
			long gamesPlayed, double strength) {

		this.issLobby.updatePlayer(playerName, language, gamesPlayed, strength);
	}

	/**
	 * @see IJSkatView#removeFromISSLobbyPlayerList(String)
	 */
	@Override
	public void removeFromISSLobbyPlayerList(String playerName) {

		this.issLobby.removePlayer(playerName);
	}

	/**
	 * @see IJSkatView#showISSLobby()
	 */
	@Override
	public void showISSLobby() {

		this.issLobby = new LobbyPanel("ISS lobby", this.bitmaps, this.actions,
				this.strings);
		addTabPanel(this.issLobby, "ISS lobby");
	}

	/**
	 * @see IJSkatView#updateISSLobbyTableList(String, int, long, String,
	 *      String, String)
	 */
	@Override
	public void updateISSLobbyTableList(String tableName, int maxPlayers,
			long gamesPlayed, String player1, String player2, String player3) {

		this.issLobby.updateTable(tableName, maxPlayers, gamesPlayed, player1,
				player2, player3);
	}

	/**
	 * @see IJSkatView#removeFromISSLobbyTableList(String)
	 */
	@Override
	public void removeFromISSLobbyTableList(String tableName) {

		this.issLobby.removeTable(tableName);
	}

	/**
	 * @see IJSkatView#appendISSChatMessage(ChatMessageType, ISSChatMessage)
	 */
	@Override
	public void appendISSChatMessage(ChatMessageType messageType,
			ISSChatMessage message) {

		log.debug("appendISSChatMessage"); //$NON-NLS-1$

		this.issLobby.appendChatMessage(message);
	}

	/**
	 * @see de.jskat.gui.IJSkatView#updateISSTable(String, ISSTablePanelStatus)
	 */
	@Override
	public void updateISSTable(String tableName, ISSTablePanelStatus tableStatus) {

		SkatTablePanel panel = this.tables.get(tableName);

		if (panel == null) {

			this.createISSTable(tableName);
			panel = this.tables.get(tableName);
		}

		if (panel instanceof ISSTablePanel) {

			((ISSTablePanel) panel).setTableStatus(tableStatus);
		}
	}

	/**
	 * @see IJSkatView#updateISSTable(String, String, ISSGameStartInformation)
	 */
	@Override
	public void updateISSTable(String tableName, String playerName,
			ISSGameStartInformation status) {

		if (playerName.equals(status.getPlayerName(Player.FORE_HAND))) {

			updateISSTable(tableName, Player.MIDDLE_HAND, Player.HIND_HAND,
					Player.FORE_HAND, status);
		} else if (playerName.equals(status.getPlayerName(Player.MIDDLE_HAND))) {

			updateISSTable(tableName, Player.HIND_HAND, Player.FORE_HAND,
					Player.MIDDLE_HAND, status);
		} else if (playerName.equals(status.getPlayerName(Player.HIND_HAND))) {

			updateISSTable(tableName, Player.FORE_HAND, Player.MIDDLE_HAND,
					Player.HIND_HAND, status);
		}
	}

	void updateISSTable(String tableName, Player leftOpponent,
			Player rightOpponent, Player player, ISSGameStartInformation status) {

		SkatTablePanel table = this.tables.get(tableName);

		table.setPositions(leftOpponent, rightOpponent, player);

		table.setPlayerInformation(HandPanelType.LEFT_OPPONENT, status
				.getPlayerName(leftOpponent), status
				.getPlayerTime(leftOpponent));
		table.setPlayerInformation(HandPanelType.RIGHT_OPPONENT, status
				.getPlayerName(rightOpponent), status
				.getPlayerTime(rightOpponent));
		table.setPlayerInformation(HandPanelType.PLAYER, status
				.getPlayerName(player), status.getPlayerTime(player));
	}

	void updateISSTablePanel(String tableName, HandPanelType panel,
			String playerName, double playerTime) {

		this.tables.get(tableName).setPlayerInformation(panel, playerName,
				playerTime);
	}

	/**
	 * @see IJSkatView#getNewTableName()
	 */
	@Override
	public String getNewTableName() {

		return JOptionPane.showInputDialog(this.mainFrame, this.strings
				.getString("new_table_dialog_message"), this.strings //$NON-NLS-1$
				.getString("table")); //$NON-NLS-1$
	}

	/**
	 * @see IJSkatView#updateISSMove(String, ISSMoveInformation)
	 */
	@Override
	public void updateISSMove(String tableName,
			ISSMoveInformation moveInformation) {

		switch (moveInformation.getType()) {
		// TODO add other types too
		case DEAL:
			this.setGameState(tableName, GameState.DEALING);
			this.addCards(tableName, Player.FORE_HAND, moveInformation
					.getCards(Player.FORE_HAND));
			this.addCards(tableName, Player.MIDDLE_HAND, moveInformation
					.getCards(Player.MIDDLE_HAND));
			this.addCards(tableName, Player.HIND_HAND, moveInformation
					.getCards(Player.HIND_HAND));
			this.setGameState(tableName, GameState.BIDDING);
			break;
		case BID:
		case PASS:
			this.setGameState(tableName, GameState.BIDDING);
			this.setBid(tableName, getPlayer(moveInformation.getMovePlayer()),
					moveInformation.getBidValue());
			// TODO show whos bidding or passing
			break;
		case SKAT_REQUEST:
		case SKAT_LOOKING:
			this.setGameState(tableName, GameState.LOOK_INTO_SKAT);
			break;
		case GAME_ANNOUNCEMENT:
			this.setGameState(tableName, GameState.DECLARING);
			break;
		case CARD_PLAY:
			this.playTrickCard(tableName, getPlayer(moveInformation
					.getMovePlayer()), moveInformation.getCard());
			break;
		case TIME_OUT:
			// TODO show time out message box
			break;
		}

		if (moveInformation.getMovePlayer() != MovePlayer.WORLD) {
			// dirty hack
			SkatTablePanel table = this.tables.get(tableName);
			table.setPlayerInformation(HandPanelType.PLAYER, null,
					moveInformation.getPlayerTime(Player.FORE_HAND));
			table.setPlayerInformation(HandPanelType.LEFT_OPPONENT, null,
					moveInformation.getPlayerTime(Player.MIDDLE_HAND));
			table.setPlayerInformation(HandPanelType.RIGHT_OPPONENT, null,
					moveInformation.getPlayerTime(Player.HIND_HAND));
		}
	}

	Player getPlayer(MovePlayer movePlayer) {

		Player result = null;

		switch (movePlayer) {
		case FORE_HAND:
			result = Player.FORE_HAND;
			break;
		case HIND_HAND:
			result = Player.MIDDLE_HAND;
			break;
		case MIDDLE_HAND:
			result = Player.HIND_HAND;
			break;
		case WORLD:
			break;
		}

		return result;
	}

	/**
	 * @see IJSkatView#playTrickCard(String, Player, Card)
	 */
	@Override
	public void playTrickCard(String tableName, Player position, Card card) {

		this.removeCard(tableName, position, card);
		this.setTrickCard(tableName, position, card);
	}

	/**
	 * @see IJSkatView#setLastTrick(String, Player, Card, Card, Card)
	 */
	@Override
	public void setLastTrick(String tableName, Player trickForeHand,
			Card foreHandCard, Card middleHandCard, Card hindHandCard) {

		SkatTablePanel table = this.tables.get(tableName);

		table.setLastTrick(trickForeHand, foreHandCard, middleHandCard,
				hindHandCard);
	}

	/**
	 * @see IJSkatView#showPreferences()
	 */
	@Override
	public void showPreferences() {

		this.preferencesDialog.setVisible(true);
	}

	/**
	 * @see IJSkatView#closeTabPanel(java.lang.String)
	 */
	@Override
	public void closeTabPanel(String tabName) {

		AbstractTabPanel panel = (AbstractTabPanel) this.tabs.getSelectedComponent();
		if (!tabName.equals(panel.getName())) {
			for (Component currPanel : this.tabs.getComponents()) {
				if (tabName.equals(currPanel.getName())) {
					panel = (AbstractTabPanel) currPanel;
				}
			}
		}

		if (panel instanceof SkatTablePanel || panel instanceof ISSTablePanel) {
			// remove from table list
			this.tables.remove(panel.getName());
		}

		this.tabs.remove(panel);
	}

	/**
	 * @see IJSkatView#getPlayerForInvitation(Set)
	 */
	@Override
	public List<String> getPlayerForInvitation(Set<String> playerNames) {

		List<String> result = null;

		PlayerInvitationPanel invitationPanel = new PlayerInvitationPanel(
				playerNames);
		int dialogResult = JOptionPane.showConfirmDialog(this.mainFrame,
				invitationPanel, "Player invitation",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dialogResult == JOptionPane.OK_OPTION) {
			result = invitationPanel.getPlayer();
		}

		log.debug("Players to invite: " + result);

		return result;
	}

	/**
	 * @see IJSkatView#addCards(String, Player, CardList)
	 */
	@Override
	public void addCards(String tableName, Player player, CardList cards) {

		for (Card card : cards) {
			addCard(tableName, player, card);
		}
	}

	private void addTabPanel(AbstractTabPanel newPanel, String title) {

		this.tabs.addTab(title, newPanel);
		this.tabs.setTabComponentAt(this.tabs.indexOfComponent(newPanel),
				new JSkatTabComponent(this.tabs, this.bitmaps));
		this.tabs.setSelectedComponent(newPanel);
		newPanel.setFocus();
	}
}
