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
import java.util.Map;

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
import de.jskat.gui.action.JSkatActions;
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
import de.jskat.gui.action.iss.SendChatMessageAction;
import de.jskat.gui.action.iss.ShowLoginPanelAction;
import de.jskat.gui.action.main.AboutAction;
import de.jskat.gui.action.main.ChangeActiveTableAction;
import de.jskat.gui.action.main.ContinueSkatSeriesAction;
import de.jskat.gui.action.main.CreateTableAction;
import de.jskat.gui.action.main.ExitAction;
import de.jskat.gui.action.main.HelpAction;
import de.jskat.gui.action.main.LoadGameAction;
import de.jskat.gui.action.main.LoadNeuralNetworksAction;
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
	private JTabbedPane tabs;
	private Map<String, SkatTablePanel> tables;
	private JSkatGraphicRepository bitmaps;
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
		this.tables = new HashMap<String, SkatTablePanel>();
		initActionMap(jskat);
		initGUI(jskat);

		this.skatSeriesStartDialog = new SkatSeriesStartDialog(jskat,
				this.mainFrame);

		this.mainFrame.setVisible(true);
	}

	private void initActionMap(JSkatMaster jskat) {

		this.actions = new ActionMap();

		// common actions
		this.actions.put(JSkatActions.LOAD_SERIES, new LoadGameAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatActions.SAVE_SERIES, new SaveGameAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatActions.SAVE_SERIES_AS, new SaveGameAsAction(
				jskat, this.bitmaps));
		this.actions
				.put(JSkatActions.HELP, new HelpAction(jskat, this.bitmaps));
		this.actions.put(JSkatActions.EXIT_JSKAT, new ExitAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatActions.ABOUT_JSKAT, new AboutAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatActions.CHANGE_ACTIVE_TABLE,
				new ChangeActiveTableAction(jskat));
		// skat table actions
		this.actions.put(JSkatActions.CREATE_LOCAL_TABLE,
				new CreateTableAction(jskat, this.bitmaps));
		this.actions.put(JSkatActions.START_LOCAL_SERIES,
				new StartSkatSeriesAction(jskat, this.bitmaps));
		this.actions.put(JSkatActions.CONTINUE_LOCAL_SERIES,
				new ContinueSkatSeriesAction(jskat));
		// ISS actions
		this.actions.put(JSkatActions.SHOW_ISS_LOGIN, new ShowLoginPanelAction(
				jskat, this.bitmaps));
		this.actions.put(JSkatActions.CONNECT_TO_ISS, new ConnectAction(jskat,
				this.bitmaps));
		this.actions.put(JSkatActions.SEND_CHAT_MESSAGE,
				new SendChatMessageAction(jskat));
		// Neural network actions
		this.actions.put(JSkatActions.TRAIN_NEURAL_NETWORKS,
				new TrainNeuralNetworksAction(jskat));
		this.actions.put(JSkatActions.LOAD_NEURAL_NETWORKS,
				new LoadNeuralNetworksAction(jskat, this.bitmaps));
		this.actions.put(JSkatActions.SAVE_NEURAL_NETWORKS,
				new SaveNeuralNetworksAction(jskat, this.bitmaps));
		// Human player actions
		this.actions.put(JSkatActions.HOLD_BID, new HoldBidAction(jskat));
		this.actions.put(JSkatActions.PASS_BID, new PassBidAction(jskat));
		this.actions.put(JSkatActions.LOOK_INTO_SKAT, new LookIntoSkatAction(
				jskat));
		this.actions.put(JSkatActions.PLAY_HAND_GAME, new PlayHandGameAction(
				jskat));
		this.actions.put(JSkatActions.ANNOUNCE_GAME, new GameAnnounceAction(
				jskat));
		this.actions.put(JSkatActions.PUT_CARD_INTO_SKAT,
				new PutCardIntoSkatAction(jskat));
		this.actions.put(JSkatActions.TAKE_CARD_FROM_SKAT,
				new TakeCardFromSkatAction(jskat));
		this.actions.put(JSkatActions.DISCARD_CARDS, new DiscardAction(jskat));
		this.actions.put(JSkatActions.PLAY_CARD, new PlayCardAction(jskat));
	}

	private void initGUI(JSkatMaster jskat) {

		this.mainFrame = new JFrame("JSkat"); //$NON-NLS-1$
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setPreferredSize(new Dimension(1000, 700));

		this.mainFrame.setJMenuBar(getMenuBar());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		// symbol button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout());
		buttonPanel.add(new ToolbarButton(this.actions
				.get(JSkatActions.CREATE_LOCAL_TABLE)));
		buttonPanel.add(new ToolbarButton(this.actions
				.get(JSkatActions.START_LOCAL_SERIES)));
		buttonPanel.add(new ToolbarButton(this.actions
				.get(JSkatActions.SHOW_ISS_LOGIN)));
		buttonPanel.add(new ToolbarButton(this.actions.get(JSkatActions.HELP)));
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
						log.debug(tableName);

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

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(this.actions.get(JSkatActions.LOAD_SERIES)));
		fileMenu.add(new JMenuItem(this.actions.get(JSkatActions.SAVE_SERIES)));
		fileMenu.add(new JMenuItem(this.actions
				.get(JSkatActions.SAVE_SERIES_AS)));
		fileMenu.add(new JSeparator());
		fileMenu.add(new JMenuItem(this.actions.get(JSkatActions.EXIT_JSKAT)));
		menu.add(fileMenu);

		JMenu tableMenu = new JMenu("Skat table");
		tableMenu.add(new JMenuItem(this.actions
				.get(JSkatActions.CREATE_LOCAL_TABLE)));
		tableMenu.add(new JMenuItem(this.actions
				.get(JSkatActions.START_LOCAL_SERIES)));
		tableMenu.add(new JMenuItem("Pause skat series"));
		menu.add(tableMenu);

		JMenu neuralNetworkMenu = new JMenu("Neural networks");
		neuralNetworkMenu.add(new JMenuItem(this.actions
				.get(JSkatActions.LOAD_NEURAL_NETWORKS)));
		neuralNetworkMenu.add(new JMenuItem(this.actions
				.get(JSkatActions.TRAIN_NEURAL_NETWORKS)));
		neuralNetworkMenu.add(new JMenuItem(this.actions
				.get(JSkatActions.SAVE_NEURAL_NETWORKS)));
		menu.add(neuralNetworkMenu);

		JMenu issMenu = new JMenu("ISS");
		issMenu
				.add(new JMenuItem(this.actions
						.get(JSkatActions.SHOW_ISS_LOGIN)));
		issMenu.add(new JMenuItem("Create new skat table"));
		issMenu.add(new JMenuItem("Invite player"));
		menu.add(issMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new JMenuItem(this.actions.get(JSkatActions.HELP)));
		helpMenu.add(new JSeparator());
		helpMenu.add(new JMenuItem(this.actions.get(JSkatActions.ABOUT_JSKAT)));
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
	 * @see JSkatView#startSeries()
	 */
	public void startSeries() {
		// TODO implement it
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
	 * @see JSkatView#connectToISS(String, String, int)
	 */
	public void connectToISS(String login, String password, int port) {

		// this.actions.get(CONNECT_TO_ISS)(login, password, port);
	}

	/**
	 * @see JSkatView#createISSTable(String)
	 */
	public void createISSTable(String name) {
		// FIXME not needed, do it in createSkatTablePanel
		this.tabs.add("ISS table no. #", new ISSTablePanel(name, this.bitmaps,
				this.actions));
	}

	/**
	 * @see JSkatView#createSkatTablePanel(String)
	 */
	public SkatTablePanel createSkatTablePanel(String name) {

		SkatTablePanel newPanel = new SkatTablePanel(name, this.bitmaps,
				this.actions);
		this.tabs.addTab(name, newPanel);
		this.tabs.setSelectedComponent(newPanel);

		this.actions.get(JSkatActions.START_LOCAL_SERIES).setEnabled(true);

		this.tables.put(name, newPanel);

		return newPanel;
	}

	/**
	 * @see JSkatView#showAboutMessage()
	 */
	public void showAboutMessage() {

		JOptionPane.showMessageDialog(this.mainFrame, "JSkat V0.7\n\n"
				+ "Authors: Jan Schaefer, Markus J. Luzius\n\n"
				+ "Icons: Tango project and Silvestre Herrera", "About JSkat",
				JOptionPane.INFORMATION_MESSAGE);
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

		new JSkatHelpDialog(null, this.mainFrame, true).setVisible(true);
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
		this.actions.get(JSkatActions.HOLD_BID).putValue(Action.NAME,
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
	public void appendISSChatMessage(ChatMessageType messageType, ISSChatMessage message) {

		log.debug("appendISSChatMessage");
		
		this.issLobby.appendChatMessage(message);
	}
}
