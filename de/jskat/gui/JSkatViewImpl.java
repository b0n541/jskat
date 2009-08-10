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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ActionMap;
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
import de.jskat.control.SkatGame;
import de.jskat.control.SkatTable;
import de.jskat.control.iss.ChatMessageType;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameStates;
import de.jskat.data.iss.ISSChatMessage;
import de.jskat.data.iss.ISSGameStatus;
import de.jskat.data.iss.ISSMoveInformation;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.data.iss.MovePlayer;
import de.jskat.gui.action.JSkatAction;
import de.jskat.gui.action.human.DiscardAction;
import de.jskat.gui.action.human.GameAnnounceAction;
import de.jskat.gui.action.human.HoldBidAction;
import de.jskat.gui.action.human.LookIntoSkatAction;
import de.jskat.gui.action.human.PassBidAction;
import de.jskat.gui.action.human.PlayCardAction;
import de.jskat.gui.action.human.PlayHandGameAction;
import de.jskat.gui.action.human.PutCardIntoSkatAction;
import de.jskat.gui.action.human.TakeCardFromSkatAction;
import de.jskat.gui.action.iss.ConnectAction;
import de.jskat.gui.action.iss.CreateISSTableAction;
import de.jskat.gui.action.iss.JoinISSTableAction;
import de.jskat.gui.action.iss.LeaveISSTableAction;
import de.jskat.gui.action.iss.ObserveISSTableAction;
import de.jskat.gui.action.iss.SendChatMessageAction;
import de.jskat.gui.action.iss.ShowLoginPanelAction;
import de.jskat.gui.action.main.AboutAction;
import de.jskat.gui.action.main.ChangeActiveTableAction;
import de.jskat.gui.action.main.ContinueSkatSeriesAction;
import de.jskat.gui.action.main.CreateTableAction;
import de.jskat.gui.action.main.ExitAction;
import de.jskat.gui.action.main.HelpAction;
import de.jskat.gui.action.main.LicenseAction;
import de.jskat.gui.action.main.LoadGameAction;
import de.jskat.gui.action.main.LoadNeuralNetworksAction;
import de.jskat.gui.action.main.PreferencesAction;
import de.jskat.gui.action.main.SaveGameAction;
import de.jskat.gui.action.main.SaveGameAsAction;
import de.jskat.gui.action.main.SaveNeuralNetworksAction;
import de.jskat.gui.action.main.StartSkatSeriesAction;
import de.jskat.gui.action.main.TrainNeuralNetworksAction;
import de.jskat.gui.help.JSkatHelpDialog;
import de.jskat.gui.img.JSkatGraphicRepository;
import de.jskat.gui.iss.ISSTablePanel;
import de.jskat.gui.iss.LobbyPanel;
import de.jskat.gui.iss.LoginPanel;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.Player;

/**
 * Implementation of JSkatView interface
 */
public class JSkatViewImpl implements JSkatView {

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
		this.strings = PropertyResourceBundle.getBundle("de/jskat/i18n/i18n",
				new Locale("de", "DE"));
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
		this.actions.put(JSkatAction.LOAD_SERIES, new LoadGameAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatAction.SAVE_SERIES, new SaveGameAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatAction.SAVE_SERIES_AS, new SaveGameAsAction(
				jskat, this.bitmaps));
		this.actions.put(JSkatAction.HELP, new HelpAction(jskat, this.bitmaps));
		this.actions.put(JSkatAction.LICENSE, new LicenseAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatAction.EXIT_JSKAT, new ExitAction(jskat,
				this.bitmaps, this.strings));
		this.actions.put(JSkatAction.PREFERENCES, new PreferencesAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatAction.ABOUT_JSKAT, new AboutAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatAction.CHANGE_ACTIVE_TABLE,
				new ChangeActiveTableAction(jskat));
		// skat table actions
		this.actions.put(JSkatAction.CREATE_LOCAL_TABLE, new CreateTableAction(
				jskat, this.bitmaps));
		this.actions.put(JSkatAction.START_LOCAL_SERIES,
				new StartSkatSeriesAction(jskat, this.bitmaps));
		this.actions.put(JSkatAction.CONTINUE_LOCAL_SERIES,
				new ContinueSkatSeriesAction(jskat));
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
		// Neural network actions
		this.actions.put(JSkatAction.TRAIN_NEURAL_NETWORKS,
				new TrainNeuralNetworksAction(jskat));
		this.actions.put(JSkatAction.LOAD_NEURAL_NETWORKS,
				new LoadNeuralNetworksAction(jskat, this.bitmaps));
		this.actions.put(JSkatAction.SAVE_NEURAL_NETWORKS,
				new SaveNeuralNetworksAction(jskat, this.bitmaps));
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
			@Override
			public void stateChanged(ChangeEvent e) {

				if (e.getSource() instanceof JTabbedPane) {

					JTabbedPane changedTabs = (JTabbedPane) e.getSource();
					Component tab = changedTabs.getSelectedComponent();

					if (tab instanceof JSkatTabPanel) {

						String tableName = ((JSkatTabPanel) tab).getName();
						log.debug("showing table pane of table " + tableName);

						// FIXME set active table name in JSkatMasters data
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

		JMenu fileMenu = new JMenu(this.strings.getString("game"));
		fileMenu.add(new JMenuItem(this.actions.get(JSkatAction.LOAD_SERIES)));
		fileMenu.add(new JMenuItem(this.actions.get(JSkatAction.SAVE_SERIES)));
		fileMenu
				.add(new JMenuItem(this.actions.get(JSkatAction.SAVE_SERIES_AS)));
		fileMenu.add(new JSeparator());
		fileMenu.add(new JMenuItem(this.actions.get(JSkatAction.EXIT_JSKAT)));
		menu.add(fileMenu);

		JMenu tableMenu = new JMenu("Skat table");
		tableMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.CREATE_LOCAL_TABLE)));
		tableMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.START_LOCAL_SERIES)));
		tableMenu.add(new JMenuItem("Pause skat series"));
		menu.add(tableMenu);

		JMenu neuralNetworkMenu = new JMenu("Neural networks");
		neuralNetworkMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.LOAD_NEURAL_NETWORKS)));
		neuralNetworkMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.TRAIN_NEURAL_NETWORKS)));
		neuralNetworkMenu.add(new JMenuItem(this.actions
				.get(JSkatAction.SAVE_NEURAL_NETWORKS)));
		menu.add(neuralNetworkMenu);

		JMenu issMenu = new JMenu("ISS");
		issMenu
				.add(new JMenuItem(this.actions.get(JSkatAction.SHOW_ISS_LOGIN)));
		issMenu.add(new JSeparator());
		issMenu.add(new JMenuItem("Create new skat table"));
		issMenu.add(new JMenuItem("Invite player"));
		menu.add(issMenu);

		JMenu extraMenu = new JMenu("Extras");
		extraMenu.add(new JMenuItem(this.actions.get(JSkatAction.PREFERENCES)));
		menu.add(extraMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new JMenuItem(this.actions.get(JSkatAction.HELP)));
		helpMenu.add(new JSeparator());
		helpMenu.add(new JMenuItem(this.actions.get(JSkatAction.LICENSE)));
		helpMenu.add(new JMenuItem(this.actions.get(JSkatAction.ABOUT_JSKAT)));
		menu.add(helpMenu);

		return menu;
	}

	/**
	 * @see JSkatView#showTable(SkatTable)
	 */
	public void showTable(SkatTable table) {
		// TODO implement it
	}

	/**
	 * @see JSkatView#startSeries(String)
	 */
	public void startSeries(String tableName) {

		this.tables.get(tableName).clearSkatList();
	}

	/**
	 * @see JSkatView#showSeriesResults()
	 */
	public void showSeriesResults() {
		// TODO implement it
	}

	/**
	 * @see JSkatView#startGame(SkatGame)
	 */
	public void startGame(SkatGame game) {
		// TODO implement it
	}

	/**
	 * @see JSkatView#startBidding()
	 */
	public void startBidding() {
		// TODO implement it
	}

	/**
	 * @see JSkatView#startDiscarding()
	 */
	public void startDiscarding() {
		// TODO implement it
	}

	/**
	 * @see JSkatView#startPlaying()
	 */
	public void startPlaying() {
		// TODO implement it
	}

	/**
	 * @see JSkatView#showGameResults()
	 */
	public void showGameResults() {
		// TODO implement it
	}

	/**
	 * @see JSkatView#createISSTable(String)
	 */
	public void createISSTable(String name) {
		// FIXME not needed as separate method,
		// do it in createSkatTablePanel
		ISSTablePanel newTable = new ISSTablePanel(name, this.bitmaps,
				this.actions);
		this.tables.put(name, newTable);

		this.tabs.add("ISS table: " + name, newTable); //$NON-NLS-1$
		this.tabs.setSelectedComponent(newTable);
	}

	/**
	 * @see JSkatView#createSkatTablePanel(String)
	 */
	public SkatTablePanel createSkatTablePanel(String name) {

		SkatTablePanel newPanel = new SkatTablePanel(name, this.bitmaps,
				this.actions);
		this.tabs.addTab(name, newPanel);
		this.tabs.setSelectedComponent(newPanel);

		this.actions.get(JSkatAction.START_LOCAL_SERIES).setEnabled(true);

		this.tables.put(name, newPanel);

		return newPanel;
	}

	/**
	 * @see JSkatView#showAboutMessage()
	 */
	public void showAboutMessage() {

		JOptionPane
				.showMessageDialog(
						this.mainFrame,
						"JSkat V0.7\n\n"
								+ "Authors: Jan Schaefer, Markus J. Luzius\n\n"
								+ "Icons: Tango project, Silvestre Herrera and Alex Roberts\n\n"
								+ "This program comes with ABSOLUTELY NO WARRANTY; for details see licence dialog\n"
								+ "This is free software, and you are welcome to redistribute it\n"
								+ "under certain conditions; see licence dialog for details.",
						"About JSkat", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * @see JSkatView#showMessage(int, String)
	 */
	public void showMessage(int messageType, String message) {

		JOptionPane.showMessageDialog(this.mainFrame, message, "Message",
				messageType);
	}

	/**
	 * @see JSkatView#showExitDialog()
	 */
	public int showExitDialog() {

		String[] options = { "Yes", "No" };

		return JOptionPane.showOptionDialog(this.mainFrame,
				"Do you really want to quit?", "Really quit?",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);
	}

	/**
	 * @see JSkatView#addCard(String, Player, Card)
	 */
	@Override
	public void addCard(String tableName, Player player, Card card) {

		this.tables.get(tableName).addCard(player, card);
	}

	/**
	 * @see JSkatView#clearHand(String, Player)
	 */
	@Override
	public void clearHand(String tableName, Player player) {

		this.tables.get(tableName).clearHand(player);
	}

	/**
	 * @see JSkatView#removeCard(String, Player, Card)
	 */
	@Override
	public void removeCard(String tableName, Player player, Card card) {

		this.tables.get(tableName).removeCard(player, card);
	}

	/**
	 * @see JSkatView#setPositions(String, Player, Player, Player)
	 */
	@Override
	public void setPositions(String tableName, Player leftPosition,
			Player rightPosition, Player playerPosition) {

		this.tables.get(tableName).setPositions(leftPosition, rightPosition,
				playerPosition);
	}

	/**
	 * @see JSkatView#setTrickCard(String, Player, Card)
	 */
	@Override
	public void setTrickCard(String tableName, Player position, Card card) {

		this.tables.get(tableName).setTrickCard(position, card);
	}

	/**
	 * @see JSkatView#clearTrickCards(String)
	 */
	@Override
	public void clearTrickCards(String tableName) {

		this.tables.get(tableName).clearTrickCards();
	}

	/**
	 * @see JSkatView#setGameAnnouncement(String, GameAnnouncement, boolean)
	 */
	@Override
	public void setGameAnnouncement(String tableName, GameAnnouncement ann,
			boolean hand) {

		this.tables.get(tableName).setGameAnnouncement(ann, hand);
	}

	/**
	 * @see JSkatView#setGameState(String, GameStates)
	 */
	@Override
	public void setGameState(String tableName, GameStates state) {

		this.tables.get(tableName).setGameState(state);
	}

	/**
	 * @see JSkatView#addGameResult(String, SkatGameData)
	 */
	@Override
	public void addGameResult(String tableName, SkatGameData data) {

		this.tables.get(tableName).addGameResult(data);
	}

	/**
	 * @see JSkatView#showHelpDialog()
	 */
	@Override
	public void showHelpDialog() {

		new JSkatHelpDialog(null, this.mainFrame, true, "Help",
				"de/jskat/gui/help/jskat_help.html").setVisible(true);
	}

	/**
	 * @see JSkatView#showLicenseDialog()
	 */
	@Override
	public void showLicenseDialog() {

		new JSkatHelpDialog(null, this.mainFrame, true, "License",
				"de/jskat/gui/help/gpl3.html").setVisible(true);
	}

	/**
	 * @see JSkatView#clearTable(String)
	 */
	@Override
	public void clearTable(String tableName) {

		this.tables.get(tableName).clearTable();
	}

	/**
	 * @see JSkatView#setNextBidValue(String, int)
	 */
	@Override
	public void setNextBidValue(String tableName, int nextBidValue) {

		// TODO this should be set for every table seperately
		this.actions.get(JSkatAction.HOLD_BID).putValue(Action.NAME,
				Integer.toString(nextBidValue));
	}

	/**
	 * @see JSkatView#setBid(String, Player, int)
	 */
	@Override
	public void setBid(String tableName, Player player, int bidValue) {

		this.tables.get(tableName).setBid(player, bidValue);
	}

	/**
	 * @see JSkatView#setTrickForeHand(String, Player)
	 */
	@Override
	public void setTrickForeHand(String tableName, Player trickForeHand) {

		this.tables.get(tableName).setTrickForeHand(trickForeHand);
	}

	/**
	 * @see JSkatView#setSkat(String, CardList)
	 */
	@Override
	public void setSkat(String tableName, CardList skat) {
		// TODO maybe this is not needed anymore
		// this.tables.get(tableName).setSkat(skat);
	}

	/**
	 * @see JSkatView#putCardIntoSkat(String, Card)
	 */
	@Override
	public void putCardIntoSkat(String tableName, Card card) {

		this.tables.get(tableName).putCardIntoSkat(card);
	}

	/**
	 * @see JSkatView#takeCardFromSkat(String, Card)
	 */
	@Override
	public void takeCardFromSkat(String tableName, Card card) {

		this.tables.get(tableName).takeCardFromSkat(card);
	}

	/**
	 * @see JSkatView#showStartSkatSeriesDialog()
	 */
	@Override
	public void showStartSkatSeriesDialog() {

		this.skatSeriesStartDialog.setVisible(true);
	}

	/**
	 * @see JSkatView#showISSLogin()
	 */
	public void showISSLogin() {

		this.tabs.add("ISS login", new LoginPanel("ISS login", this.bitmaps, //$NON-NLS-1$ //$NON-NLS-2$
				this.actions));
	}

	/**
	 * @see JSkatView#updateISSLobbyPlayerList(String, String, long, double)
	 */
	@Override
	public void updateISSLobbyPlayerList(String playerName, String language,
			long gamesPlayed, double strength) {

		this.issLobby.updatePlayer(playerName, language, gamesPlayed, strength);
	}

	/**
	 * @see JSkatView#removeFromISSLobbyPlayerList(String)
	 */
	@Override
	public void removeFromISSLobbyPlayerList(String playerName) {

		this.issLobby.removePlayer(playerName);
	}

	/**
	 * @see JSkatView#showISSLobby()
	 */
	@Override
	public void showISSLobby() {

		this.issLobby = new LobbyPanel("ISS lobby", this.bitmaps, this.actions); //$NON-NLS-1$
		this.tabs.add("ISS lobby", this.issLobby); //$NON-NLS-1$
		this.tabs.setSelectedComponent(this.issLobby);
	}

	/**
	 * @see JSkatView#updateISSLobbyTableList(String, int, long, String, String,
	 *      String)
	 */
	@Override
	public void updateISSLobbyTableList(String tableName, int maxPlayers,
			long gamesPlayed, String player1, String player2, String player3) {

		this.issLobby.updateTable(tableName, maxPlayers, gamesPlayed, player1,
				player2, player3);
	}

	/**
	 * @see JSkatView#removeFromISSLobbyTableList(String)
	 */
	@Override
	public void removeFromISSLobbyTableList(String tableName) {

		this.issLobby.removeTable(tableName);
	}

	/**
	 * @see JSkatView#appendISSChatMessage(ChatMessageType, ISSChatMessage)
	 */
	@Override
	public void appendISSChatMessage(ChatMessageType messageType,
			ISSChatMessage message) {

		log.debug("appendISSChatMessage");

		this.issLobby.appendChatMessage(message);
	}

	/**
	 * @see de.jskat.gui.JSkatView#updateISSTable(String, ISSTablePanelStatus)
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
	 * @see JSkatView#updateISSTable(String, ISSGameStatus)
	 */
	@Override
	public void updateISSTable(String tableName, ISSGameStatus status) {

		SkatTablePanel table = this.tables.get(tableName);

		// TODO dirty hack
		table.setPositions(Player.MIDDLE_HAND, Player.HIND_HAND,
				Player.FORE_HAND);
		table.setPlayerInformation(HandPanelType.PLAYER, status
				.getPlayerName(Player.FORE_HAND), status
				.getPlayerTime(Player.FORE_HAND));
		table.setPlayerInformation(HandPanelType.LEFT_OPPONENT, status
				.getPlayerName(Player.MIDDLE_HAND), status
				.getPlayerTime(Player.MIDDLE_HAND));
		table.setPlayerInformation(HandPanelType.RIGHT_OPPONENT, status
				.getPlayerName(Player.HIND_HAND), status
				.getPlayerTime(Player.HIND_HAND));
	}

	/**
	 * @see JSkatView#getNewTableName()
	 */
	@Override
	public String getNewTableName() {

		return JOptionPane.showInputDialog(this.mainFrame,
				"Please name your table:", "New skat table",
				JOptionPane.OK_CANCEL_OPTION);
	}

	/**
	 * @see JSkatView#updateISSMove(String, ISSMoveInformation)
	 */
	@Override
	public void updateISSMove(String tableName,
			ISSMoveInformation moveInformation) {

		switch (moveInformation.getType()) {
		// TODO add other types too
		case CARD_PLAY:
			switch (moveInformation.getPosition()) {
			case FORE_HAND:
				this.playTrickCard(tableName, Player.FORE_HAND, moveInformation
						.getCard());
				break;
			case MIDDLE_HAND:
				this.playTrickCard(tableName, Player.MIDDLE_HAND,
						moveInformation.getCard());
				break;
			case HIND_HAND:
				this.playTrickCard(tableName, Player.HIND_HAND, moveInformation
						.getCard());
				break;
			}
		}

		if (moveInformation.getPosition() != MovePlayer.WORLD) {
			// dirty hack
			SkatTablePanel table = this.tables.get(tableName);
			table.setPlayerInformation(HandPanelType.PLAYER, null,
					moveInformation.getPlayerTime(Player.FORE_HAND));
			table.setPlayerInformation(HandPanelType.LEFT_OPPONENT, null,
					moveInformation.getPlayerTime(Player.MIDDLE_HAND));
			table.setPlayerInformation(HandPanelType.RIGHT_OPPONENT, null,
					moveInformation.getPlayerTime(Player.HIND_HAND));

			table.setContextPanel(ContextPanelTypes.TRICK_PLAYING);
		}
	}

	/**
	 * @see JSkatView#playTrickCard(String, Player, Card)
	 */
	@Override
	public void playTrickCard(String tableName, Player position, Card card) {

		this.removeCard(tableName, position, card);
		this.setTrickCard(tableName, position, card);
	}

	/**
	 * @see JSkatView#setLastTrick(String, Player, Card, Card, Card)
	 */
	@Override
	public void setLastTrick(String tableName, Player trickForeHand,
			Card foreHandCard, Card middleHandCard, Card hindHandCard) {

		SkatTablePanel table = this.tables.get(tableName);

		table.setLastTrick(trickForeHand, foreHandCard, middleHandCard,
				hindHandCard);
	}

	/**
	 * @see JSkatView#showPreferences()
	 */
	@Override
	public void showPreferences() {

		this.preferencesDialog.setVisible(true);
	}
}
