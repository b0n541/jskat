/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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
package org.jskat.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jskat.control.JSkatMaster;
import org.jskat.control.SkatTable;
import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.JSkatOptions;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.data.Trick;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.GameStartInformation;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.MovePlayer;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.action.human.ContinueSkatSeriesAction;
import org.jskat.gui.action.human.DiscardAction;
import org.jskat.gui.action.human.GameAnnounceAction;
import org.jskat.gui.action.human.HoldBidAction;
import org.jskat.gui.action.human.MakeBidAction;
import org.jskat.gui.action.human.PassBidAction;
import org.jskat.gui.action.human.PickUpSkatAction;
import org.jskat.gui.action.human.PlayCardAction;
import org.jskat.gui.action.human.PlayHandGameAction;
import org.jskat.gui.action.human.PutCardIntoSkatAction;
import org.jskat.gui.action.human.TakeCardFromSkatAction;
import org.jskat.gui.action.iss.ChangeTableSeatsAction;
import org.jskat.gui.action.iss.ConnectAction;
import org.jskat.gui.action.iss.CreateIssTableAction;
import org.jskat.gui.action.iss.DisconnectAction;
import org.jskat.gui.action.iss.InvitePlayerAction;
import org.jskat.gui.action.iss.JoinIssTableAction;
import org.jskat.gui.action.iss.LeaveIssTableAction;
import org.jskat.gui.action.iss.ObserveTableAction;
import org.jskat.gui.action.iss.OpenHomepageAction;
import org.jskat.gui.action.iss.ReadyAction;
import org.jskat.gui.action.iss.RegisterAction;
import org.jskat.gui.action.iss.ResignAction;
import org.jskat.gui.action.iss.SendChatMessageAction;
import org.jskat.gui.action.iss.ShowLoginPanelAction;
import org.jskat.gui.action.iss.TalkEnableAction;
import org.jskat.gui.action.main.AboutAction;
import org.jskat.gui.action.main.ChangeActiveTableAction;
import org.jskat.gui.action.main.CreateTableAction;
import org.jskat.gui.action.main.ExitAction;
import org.jskat.gui.action.main.HelpAction;
import org.jskat.gui.action.main.LicenseAction;
import org.jskat.gui.action.main.LoadNeuralNetworksAction;
import org.jskat.gui.action.main.LoadSeriesAction;
import org.jskat.gui.action.main.PauseSkatSeriesAction;
import org.jskat.gui.action.main.PreferencesAction;
import org.jskat.gui.action.main.ResetNeuralNetworksAction;
import org.jskat.gui.action.main.SaveNeuralNetworksAction;
import org.jskat.gui.action.main.SaveSeriesAction;
import org.jskat.gui.action.main.SaveSeriesAsAction;
import org.jskat.gui.action.main.StartSkatSeriesAction;
import org.jskat.gui.action.main.TrainNeuralNetworksAction;
import org.jskat.gui.help.JSkatHelpDialog;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.iss.ISSTablePanel;
import org.jskat.gui.iss.LobbyPanel;
import org.jskat.gui.iss.LoginPanel;
import org.jskat.gui.iss.PlayerInvitationPanel;
import org.jskat.gui.nn.NeuralNetworkTrainingOverview;
import org.jskat.gui.table.SkatSeriesStartDialog;
import org.jskat.gui.table.SkatTablePanel;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;

/**
 * Implementation of JSkatView interface
 */
public class JSkatViewImpl implements IJSkatView {

	static Log log = LogFactory.getLog(JSkatViewImpl.class);

	private JFrame mainFrame;
	private SkatSeriesStartDialog skatSeriesStartDialog;
	private JSkatPreferencesDialog preferencesDialog;
	private NeuralNetworkTrainingOverview trainingOverview;
	private JTabbedPane tabs;
	private Map<String, SkatTablePanel> tables;
	private JSkatGraphicRepository bitmaps;
	private JSkatResourceBundle strings;
	private JSkatMaster jskat;
	static ActionMap actions;
	private LobbyPanel issLobby;

	/**
	 * Constructor
	 */
	public JSkatViewImpl() {

		bitmaps = JSkatGraphicRepository.instance();
		strings = JSkatResourceBundle.instance();
		log.debug("I18n strings loaded: " + strings.getLocale()); //$NON-NLS-1$
		tables = new HashMap<String, SkatTablePanel>();
		jskat = JSkatMaster.instance();
		initActionMap();
		initGUI();

		skatSeriesStartDialog = new SkatSeriesStartDialog(jskat, mainFrame);
		// FIXME (jansch 08.12.2010) make this part of the main frame
		preferencesDialog = new JSkatPreferencesDialog(mainFrame);
		trainingOverview = new NeuralNetworkTrainingOverview();

		addTabPanel(new WelcomePanel(strings.getString("welcome"), actions), //$NON-NLS-1$
				strings.getString("welcome")); //$NON-NLS-1$

		mainFrame.setVisible(true);
	}

	private void initActionMap() {

		actions = new ActionMap();

		// common actions
		actions.put(JSkatAction.LOAD_SERIES, new LoadSeriesAction());
		actions.put(JSkatAction.SAVE_SERIES, new SaveSeriesAction());
		actions.put(JSkatAction.SAVE_SERIES_AS, new SaveSeriesAsAction());
		actions.put(JSkatAction.HELP, new HelpAction());
		actions.put(JSkatAction.LICENSE, new LicenseAction());
		actions.put(JSkatAction.EXIT_JSKAT, new ExitAction());
		actions.put(JSkatAction.PREFERENCES, new PreferencesAction());
		actions.put(JSkatAction.ABOUT_JSKAT, new AboutAction());
		actions.put(JSkatAction.CHANGE_ACTIVE_TABLE,
				new ChangeActiveTableAction());
		// skat table actions
		actions.put(JSkatAction.CREATE_LOCAL_TABLE, new CreateTableAction());
		actions.put(JSkatAction.START_LOCAL_SERIES, new StartSkatSeriesAction());
		actions.put(JSkatAction.CONTINUE_LOCAL_SERIES,
				new ContinueSkatSeriesAction());
		actions.put(JSkatAction.PAUSE_LOCAL_SERIES, new PauseSkatSeriesAction());
		// ISS actions
		actions.put(JSkatAction.REGISTER_ON_ISS, new RegisterAction());
		actions.put(JSkatAction.OPEN_ISS_HOMEPAGE, new OpenHomepageAction());
		actions.put(JSkatAction.SHOW_ISS_LOGIN, new ShowLoginPanelAction());
		actions.put(JSkatAction.CONNECT_TO_ISS, new ConnectAction());
		actions.put(JSkatAction.DISCONNECT_FROM_ISS, new DisconnectAction());
		actions.put(JSkatAction.SEND_CHAT_MESSAGE, new SendChatMessageAction());
		actions.put(JSkatAction.CREATE_ISS_TABLE, new CreateIssTableAction());
		actions.put(JSkatAction.JOIN_ISS_TABLE, new JoinIssTableAction());
		actions.put(JSkatAction.LEAVE_ISS_TABLE, new LeaveIssTableAction());
		actions.put(JSkatAction.OBSERVE_ISS_TABLE, new ObserveTableAction());
		actions.put(JSkatAction.READY_TO_PLAY, new ReadyAction());
		actions.put(JSkatAction.TALK_ENABLED, new TalkEnableAction());
		actions.put(JSkatAction.CHANGE_TABLE_SEATS,
				new ChangeTableSeatsAction());
		actions.put(JSkatAction.INVITE_ISS_PLAYER, new InvitePlayerAction());
		actions.put(JSkatAction.RESIGN, new ResignAction());
		// Neural network actions
		actions.put(JSkatAction.TRAIN_NEURAL_NETWORKS,
				new TrainNeuralNetworksAction());
		actions.put(JSkatAction.LOAD_NEURAL_NETWORKS,
				new LoadNeuralNetworksAction());
		actions.put(JSkatAction.SAVE_NEURAL_NETWORKS,
				new SaveNeuralNetworksAction());
		actions.put(JSkatAction.RESET_NEURAL_NETWORKS,
				new ResetNeuralNetworksAction());
		// Human player actions
		actions.put(JSkatAction.MAKE_BID, new MakeBidAction());
		actions.put(JSkatAction.HOLD_BID, new HoldBidAction());
		actions.put(JSkatAction.PASS_BID, new PassBidAction());
		actions.put(JSkatAction.PICK_UP_SKAT, new PickUpSkatAction());
		actions.put(JSkatAction.PLAY_HAND_GAME, new PlayHandGameAction());
		actions.put(JSkatAction.ANNOUNCE_GAME, new GameAnnounceAction());
		actions.put(JSkatAction.PUT_CARD_INTO_SKAT, new PutCardIntoSkatAction());
		actions.put(JSkatAction.TAKE_CARD_FROM_SKAT,
				new TakeCardFromSkatAction());
		actions.put(JSkatAction.DISCARD_CARDS, new DiscardAction());
		actions.put(JSkatAction.PLAY_CARD, new PlayCardAction());

		// disable some actions
		actions.get(JSkatAction.LOAD_SERIES).setEnabled(false);
		actions.get(JSkatAction.SAVE_SERIES).setEnabled(false);
		actions.get(JSkatAction.SAVE_SERIES_AS).setEnabled(false);
		actions.get(JSkatAction.START_LOCAL_SERIES).setEnabled(false);
		actions.get(JSkatAction.PAUSE_LOCAL_SERIES).setEnabled(false);
		actions.get(JSkatAction.CONTINUE_LOCAL_SERIES).setEnabled(false);
		actions.get(JSkatAction.CREATE_ISS_TABLE).setEnabled(false);
		actions.get(JSkatAction.INVITE_ISS_PLAYER).setEnabled(false);
	}

	private void initGUI() {

		mainFrame = new JFrame("JSkat"); //$NON-NLS-1$

		mainFrame.setJMenuBar(getMenuBar());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		// symbol button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout());
		buttonPanel.add(new ToolbarButton(actions
				.get(JSkatAction.CREATE_LOCAL_TABLE)));
		buttonPanel.add(new ToolbarButton(actions
				.get(JSkatAction.START_LOCAL_SERIES)));
		buttonPanel.add(new ToolbarButton(actions
				.get(JSkatAction.SHOW_ISS_LOGIN)));
		buttonPanel.add(new ToolbarButton(actions.get(JSkatAction.HELP)));
		mainPanel.add(buttonPanel, BorderLayout.NORTH);

		// main area
		tabs = new JTabbedPane();
		tabs.setAutoscrolls(true);
		tabs.addChangeListener(new ChangeListener() {
			/**
			 * @see ChangeListener#stateChanged(ChangeEvent)
			 */
			@Override
			public void stateChanged(ChangeEvent e) {

				if (e.getSource() instanceof JTabbedPane) {

					JTabbedPane changedTabs = (JTabbedPane) e.getSource();
					Component tab = changedTabs.getSelectedComponent();

					if (tab instanceof AbstractTabPanel) {

						AbstractTabPanel panel = (AbstractTabPanel) tab;
						String tableName = panel.getName();
						log.debug("showing table panel of table " + tableName); //$NON-NLS-1$
						panel.setFocus();

						JSkatViewImpl.actions.get(
								JSkatAction.CHANGE_ACTIVE_TABLE)
								.actionPerformed(
										new ActionEvent(e.getSource(), 1,
												tableName));
					}
				}
			}
		});

		mainPanel.add(tabs, BorderLayout.CENTER);

		mainFrame.setContentPane(mainPanel);
		mainFrame.pack();

		mainFrame.setIconImage(bitmaps.getIconImage(
				JSkatGraphicRepository.Icon.JSKAT,
				JSkatGraphicRepository.IconSize.BIG));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setMinimumSize(new Dimension(800, 600));
		mainFrame.setPreferredSize(new Dimension(1000, 700));
		mainFrame.setExtendedState(mainFrame.getExtendedState()
				| Frame.MAXIMIZED_BOTH);
	}

	private JMenuBar getMenuBar() {

		JMenuBar menu = new JMenuBar();

		JMenu fileMenu = new JMenu(strings.getString("file")); //$NON-NLS-1$
		fileMenu.add(new JMenuItem(actions.get(JSkatAction.LOAD_SERIES)));
		fileMenu.add(new JMenuItem(actions.get(JSkatAction.SAVE_SERIES)));
		fileMenu.add(new JMenuItem(actions.get(JSkatAction.SAVE_SERIES_AS)));
		fileMenu.add(new JSeparator());
		fileMenu.add(new JMenuItem(actions.get(JSkatAction.EXIT_JSKAT)));
		menu.add(fileMenu);

		JMenu tableMenu = new JMenu(strings.getString("skat_table")); //$NON-NLS-1$
		tableMenu
				.add(new JMenuItem(actions.get(JSkatAction.CREATE_LOCAL_TABLE)));
		tableMenu.add(new JSeparator());
		tableMenu
				.add(new JMenuItem(actions.get(JSkatAction.START_LOCAL_SERIES)));
		tableMenu
				.add(new JMenuItem(actions.get(JSkatAction.PAUSE_LOCAL_SERIES)));
		tableMenu.add(new JMenuItem(actions
				.get(JSkatAction.CONTINUE_LOCAL_SERIES)));
		menu.add(tableMenu);

		JMenu neuralNetworkMenu = new JMenu(
				strings.getString("neural_networks")); //$NON-NLS-1$
		neuralNetworkMenu.add(new JMenuItem(actions
				.get(JSkatAction.LOAD_NEURAL_NETWORKS)));
		neuralNetworkMenu.add(new JMenuItem(actions
				.get(JSkatAction.SAVE_NEURAL_NETWORKS)));
		neuralNetworkMenu.add(new JMenuItem(actions
				.get(JSkatAction.RESET_NEURAL_NETWORKS)));
		neuralNetworkMenu.add(new JSeparator());
		neuralNetworkMenu.add(new JMenuItem(actions
				.get(JSkatAction.TRAIN_NEURAL_NETWORKS)));
		menu.add(neuralNetworkMenu);

		JMenu issMenu = new JMenu(strings.getString("iss")); //$NON-NLS-1$
		issMenu.add(new JMenuItem(actions.get(JSkatAction.SHOW_ISS_LOGIN)));
		issMenu.add(new JSeparator());
		issMenu.add(new JMenuItem(actions.get(JSkatAction.CREATE_ISS_TABLE)));
		issMenu.add(new JMenuItem(actions.get(JSkatAction.INVITE_ISS_PLAYER)));
		menu.add(issMenu);

		JMenu extraMenu = new JMenu(strings.getString("extras")); //$NON-NLS-1$
		extraMenu.add(new JMenuItem(actions.get(JSkatAction.PREFERENCES)));
		menu.add(extraMenu);

		JMenu helpMenu = new JMenu(strings.getString("help")); //$NON-NLS-1$
		helpMenu.add(new JMenuItem(actions.get(JSkatAction.HELP)));
		helpMenu.add(new JSeparator());
		helpMenu.add(new JMenuItem(actions.get(JSkatAction.LICENSE)));
		helpMenu.add(new JMenuItem(actions.get(JSkatAction.ABOUT_JSKAT)));
		menu.add(helpMenu);

		return menu;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showTable(SkatTable table) {
		// TODO implement it
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startSeries(String tableName) {

		tables.get(tableName).clearSkatList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showSeriesResults() {
		// TODO implement it
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startGame(String tableName) {

		tables.get(tableName).startGame();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startBidding() {
		// TODO implement it
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startDiscarding() {
		// TODO implement it
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startPlaying() {
		// TODO implement it
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showGameResults() {
		// TODO implement it
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createISSTable(String tableName, String loginName) {

		ISSTablePanel newTable = new ISSTablePanel(tableName, actions,
				loginName);
		addTabPanel(newTable, "ISS table: " + tableName);
		tables.put(tableName, newTable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createSkatTablePanel(String name) {

		SkatTablePanel newPanel = new SkatTablePanel(name, actions);
		addTabPanel(newPanel, name);
		tables.put(name, newPanel);

		actions.get(JSkatAction.START_LOCAL_SERIES).setEnabled(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showAboutMessage() {
		// TODO save text for message centrally
		JOptionPane
				.showMessageDialog(
						mainFrame,
						"JSkat " //$NON-NLS-1$
								+ strings.getString("version") //$NON-NLS-1$
								+ " " //$NON-NLS-1$
								+ JSkatResourceBundle.getVersion()
								+ "\n\n" //$NON-NLS-1$
								+ "http://www.jskat.org\n" //$NON-NLS-1$
								+ "http://sourceforge.net/projects/jskat" //$NON-NLS-1$
								+ "\n\n" //$NON-NLS-1$
								+ strings.getString("authors") //$NON-NLS-1$
								+ ":\nJan Schäfer (j@nschaefer.net),\nMarkus J. Luzius (jskat@luzius.de)\n\n" //$NON-NLS-1$
								+ strings.getString("cards") //$NON-NLS-1$
								+ ": International Skat Server\n\n" //$NON-NLS-1$
								+ strings.getString("icons") //$NON-NLS-1$
								+ ": Gnome Desktop Icons, Tango project, Elementary icons,\n" //$NON-NLS-1$
								+ "Silvestre Herrera, Alex Roberts and Icojoy\n\n" //$NON-NLS-1$
								+ "This program comes with ABSOLUTELY NO WARRANTY;\n" //$NON-NLS-1$
								+ "for details see licence dialog\n" //$NON-NLS-1$
								+ "This is free software, and you are welcome to redistribute it\n" //$NON-NLS-1$
								+ "under certain conditions; see licence dialog for details.", //$NON-NLS-1$
						strings.getString("about"), JOptionPane.INFORMATION_MESSAGE, //$NON-NLS-1$
						new ImageIcon(bitmaps.getJSkatLogoImage()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showMessage(int messageType, String title, String message) {

		JOptionPane.showMessageDialog(mainFrame, message, title, messageType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCard(String tableName, Player player, Card card) {

		tables.get(tableName).addCard(player, card);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearHand(String tableName, Player player) {

		tables.get(tableName).clearHand(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeCard(String tableName, Player player, Card card) {

		tables.get(tableName).removeCard(player, card);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPositions(String tableName, Player leftPosition,
			Player rightPosition, Player playerPosition) {

		tables.get(tableName).setPositions(leftPosition, rightPosition,
				playerPosition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrickCard(String tableName, Player position, Card card) {

		tables.get(tableName).setTrickCard(position, card);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearTrickCards(String tableName) {

		tables.get(tableName).clearTrickCards();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameAnnouncement(String tableName, Player declarer,
			GameAnnouncement ann) {

		tables.get(tableName).setGameAnnouncement(declarer, ann);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameState(String tableName, GameState state) {

		setActions(state);
		tables.get(tableName).setGameState(state);
	}

	void setActions(GameState state) {

		switch (state) {
		case GAME_START:
			actions.get(JSkatAction.CONTINUE_LOCAL_SERIES).setEnabled(false);
			break;
		case GAME_OVER:
			actions.get(JSkatAction.CONTINUE_LOCAL_SERIES).setEnabled(true);
			break;
		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addGameResult(String tableName, SkatGameData data) {

		tables.get(tableName).addGameResult(data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showHelpDialog() {

		String languageCode = "en"; //$NON-NLS-1$
		switch (JSkatOptions.instance().getLanguage()) {
		case GERMAN:
			languageCode = "de"; //$NON-NLS-1$
			break;
		case ENGLISH:
			languageCode = "en"; //$NON-NLS-1$
			break;
		}

		new JSkatHelpDialog(
				mainFrame,
				strings.getString("help"), "org/jskat/gui/help/" + languageCode + "/contents.html") //$NON-NLS-1$ 
				.setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showLicenseDialog() {

		new JSkatHelpDialog(mainFrame,
				strings.getString("license"), "org/jskat/gui/help/gpl3.html").setVisible(true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearTable(String tableName) {

		tables.get(tableName).clearTable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBidValueToMake(String tableName, int bidValue) {

		tables.get(tableName).setBidValueToMake(bidValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBidValueToHold(String tableName, int bidValue) {

		tables.get(tableName).setBidValueToHold(bidValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBid(String tableName, Player player, int bidValue,
			boolean madeBid) {

		tables.get(tableName).setBid(player, bidValue, madeBid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPass(String tableName, Player player) {

		tables.get(tableName).setPass(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrickForeHand(String tableName, Player trickForeHand) {

		tables.get(tableName).setTrickForeHand(trickForeHand);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putCardIntoSkat(String tableName, Card card) {

		tables.get(tableName).putCardIntoSkat(card);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void takeCardFromSkat(String tableName, Card card) {

		tables.get(tableName).takeCardFromSkat(card);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showStartSkatSeriesDialog() {

		skatSeriesStartDialog.setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showISSLogin() {

		LoginPanel loginPanel = new LoginPanel("ISS login", actions);
		addTabPanel(loginPanel, "ISS login");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSLobbyPlayerList(String playerName, String language,
			long gamesPlayed, double strength) {

		issLobby.updatePlayer(playerName, language, gamesPlayed, strength);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFromISSLobbyPlayerList(String playerName) {

		issLobby.removePlayer(playerName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showISSLobby() {

		issLobby = new LobbyPanel("ISS lobby", actions);
		addTabPanel(issLobby, "ISS lobby");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSLobbyTableList(String tableName, int maxPlayers,
			long gamesPlayed, String player1, String player2, String player3) {

		issLobby.updateTable(tableName, maxPlayers, gamesPlayed, player1,
				player2, player3);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFromISSLobbyTableList(String tableName) {

		issLobby.removeTable(tableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendISSChatMessage(ChatMessageType messageType,
			ChatMessage message) {

		log.debug("appendISSChatMessage"); //$NON-NLS-1$

		issLobby.appendChatMessage(message);

		for (SkatTablePanel table : tables.values()) {
			if (table instanceof ISSTablePanel) {
				ISSTablePanel issTable = (ISSTablePanel) table;
				String chatname = message.getChatName();
				if ("Lobby".equals(chatname)
						|| issTable.getName().equals(chatname)) {
					issTable.appendChatMessage(message);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSTable(String tableName, TablePanelStatus tableStatus) {

		// FIXME (jan 08.11.2010) seems very complicated
		SkatTablePanel panel = tables.get(tableName);

		if (panel == null) {

			createISSTable(tableName, tableStatus.getLoginName());
			panel = tables.get(tableName);
		}

		if (panel instanceof ISSTablePanel) {

			((ISSTablePanel) panel).setTableStatus(tableStatus);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSTable(String tableName, String issLogin,
			GameStartInformation status) {

		if (issLogin.equals(status.getPlayerName(Player.FOREHAND))) {

			updateISSTable(tableName, Player.MIDDLEHAND, Player.REARHAND,
					Player.FOREHAND, status);
		} else if (issLogin.equals(status.getPlayerName(Player.MIDDLEHAND))) {

			updateISSTable(tableName, Player.REARHAND, Player.FOREHAND,
					Player.MIDDLEHAND, status);
		} else if (issLogin.equals(status.getPlayerName(Player.REARHAND))) {

			updateISSTable(tableName, Player.FOREHAND, Player.MIDDLEHAND,
					Player.REARHAND, status);
		}
	}

	private void updateISSTable(String tableName, Player leftOpponent,
			Player rightOpponent, Player player, GameStartInformation status) {

		log.debug("Updating ISS table: " + tableName + " " + leftOpponent + " " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ rightOpponent + " " + player); //$NON-NLS-1$

		setPositions(tableName, leftOpponent, rightOpponent, player);

		// FIXME (jansch 09.11.2010) this is only done for ISS games
		SkatTablePanel table = tables.get(tableName);
		table.setGameState(GameState.GAME_START);
		table.setPlayerName(leftOpponent, status.getPlayerName(leftOpponent));
		table.setPlayerTime(leftOpponent, status.getPlayerTime(leftOpponent));
		table.setPlayerName(rightOpponent, status.getPlayerName(rightOpponent));
		table.setPlayerTime(rightOpponent, status.getPlayerTime(rightOpponent));
		table.setPlayerName(player, status.getPlayerName(player));
		table.setPlayerTime(player, status.getPlayerTime(player));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNewTableName(int localTablesCreated) {
		// get table name
		String tableName = JOptionPane.showInputDialog(mainFrame,
				strings.getString("new_table_dialog_message"), //$NON-NLS-1$
				strings.getString("local_table") + " " //$NON-NLS-1$ //$NON-NLS-2$
						+ (localTablesCreated + 1));
		// truncate table name
		if (tableName.length() > 100) {
			tableName = tableName.substring(0, 100);
		}
		return tableName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSMove(String tableName, SkatGameData gameData,
			MoveInformation moveInformation) {

		Player movePlayer = moveInformation.getPlayer();

		switch (moveInformation.getType()) {
		// TODO add other types too
		case DEAL:
			setGameState(tableName, GameState.DEALING);
			SkatTablePanel table = tables.get(tableName);
			table.hideCards(Player.FOREHAND);
			table.hideCards(Player.MIDDLEHAND);
			table.hideCards(Player.REARHAND);
			addCards(tableName, Player.FOREHAND,
					moveInformation.getCards(Player.FOREHAND));
			addCards(tableName, Player.MIDDLEHAND,
					moveInformation.getCards(Player.MIDDLEHAND));
			addCards(tableName, Player.REARHAND,
					moveInformation.getCards(Player.REARHAND));
			setGameState(tableName, GameState.BIDDING);
			setActivePlayer(tableName, Player.MIDDLEHAND);
			break;
		case BID:
			setGameState(tableName, GameState.BIDDING);
			setBid(tableName, movePlayer, moveInformation.getBidValue(), true);
			setBidValueToHold(tableName, moveInformation.getBidValue());
			break;
		case HOLD_BID:
			setGameState(tableName, GameState.BIDDING);
			setBid(tableName, movePlayer, gameData.getBidValue(), false);
			setBidValueToMake(tableName,
					SkatConstants.getNextBidValue(gameData.getBidValue()));
			break;
		case PASS:
			setGameState(tableName, GameState.BIDDING);
			setPass(tableName, movePlayer);
			setBidValueToMake(tableName,
					SkatConstants.getNextBidValue(gameData.getBidValue()));
			break;
		case SKAT_REQUEST:
			setGameState(tableName, GameState.PICK_UP_SKAT);
			break;
		case PICK_UP_SKAT:
			setGameState(tableName, GameState.DISCARDING);
			if (moveInformation.getSkat().size() == 2) {
				setSkat(tableName, moveInformation.getSkat());
			}
			break;
		case GAME_ANNOUNCEMENT:
			setGameState(tableName, GameState.DECLARING);
			setGameAnnouncement(tableName, movePlayer,
					moveInformation.getGameAnnouncement());
			if (moveInformation.getGameAnnouncement().isOuvert()) {
				setOuvertCards(tableName, movePlayer,
						moveInformation.getOuvertCards());
			}
			setGameState(tableName, GameState.TRICK_PLAYING);
			setTrickForeHand(tableName, Player.FOREHAND);
			break;
		case CARD_PLAY:
			setGameState(tableName, GameState.TRICK_PLAYING);

			if (gameData.getTricks().size() > 1) {

				Trick currentTrick = gameData.getCurrentTrick();
				Trick lastTrick = gameData.getLastTrick();

				if (currentTrick.getFirstCard() != null
						&& currentTrick.getSecondCard() == null
						&& currentTrick.getThirdCard() == null) {
					// first card in new trick
					clearTrickCards(tableName);
					setLastTrick(tableName, lastTrick.getForeHand(),
							lastTrick.getFirstCard(),
							lastTrick.getSecondCard(), lastTrick.getThirdCard());
				}
			}

			playTrickCard(tableName, movePlayer, moveInformation.getCard());
			break;
		case SHOW_CARDS:
			setOuvertCards(tableName, movePlayer,
					moveInformation.getOuvertCards());
			break;
		case RESIGN:
			setResign(tableName, movePlayer);
			break;
		case TIME_OUT:
			// TODO show message box
			break;
		}

		// adjust player times
		if (moveInformation.getMovePlayer() != MovePlayer.WORLD) {
			// FIXME dirty hack
			SkatTablePanel table = tables.get(tableName);

			table.setPlayerTime(Player.FOREHAND,
					moveInformation.getPlayerTime(Player.FOREHAND));
			table.setPlayerTime(Player.MIDDLEHAND,
					moveInformation.getPlayerTime(Player.MIDDLEHAND));
			table.setPlayerTime(Player.REARHAND,
					moveInformation.getPlayerTime(Player.REARHAND));
		}
	}

	private void setOuvertCards(String tableName, Player player,
			CardList ouvertCards) {

		SkatTablePanel table = tables.get(tableName);
		table.removeAllCards(player);
		table.addCards(player, ouvertCards);
		table.showCards(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void playTrickCard(String tableName, Player position, Card card) {

		removeCard(tableName, position, card);
		setTrickCard(tableName, position, card);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLastTrick(String tableName, Player trickForeHand,
			Card foreHandCard, Card middleHandCard, Card rearHandCard) {

		SkatTablePanel table = tables.get(tableName);

		table.setLastTrick(trickForeHand, foreHandCard, middleHandCard,
				rearHandCard);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showPreferences() {

		preferencesDialog.setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showTrainingOverview() {

		trainingOverview.setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeTabPanel(String tabName) {

		AbstractTabPanel panel = (AbstractTabPanel) tabs.getSelectedComponent();
		if (!tabName.equals(panel.getName())) {
			for (Component currPanel : tabs.getComponents()) {
				if (tabName.equals(currPanel.getName())) {
					panel = (AbstractTabPanel) currPanel;
				}
			}
		}

		if (panel instanceof SkatTablePanel || panel instanceof ISSTablePanel) {
			// remove from table list
			tables.remove(panel.getName());
		}

		tabs.remove(panel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getPlayerForInvitation(Set<String> playerNames) {

		List<String> result = new ArrayList<String>();

		PlayerInvitationPanel invitationPanel = new PlayerInvitationPanel(
				playerNames);
		int dialogResult = JOptionPane.showConfirmDialog(mainFrame,
				invitationPanel, strings.getString("invite_players"), //$NON-NLS-1$
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dialogResult == JOptionPane.OK_OPTION) {
			result.addAll(invitationPanel.getPlayer());
		}

		log.debug("Players to invite: " + result); //$NON-NLS-1$

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCards(String tableName, Player player, CardList cards) {

		tables.get(tableName).addCards(player, cards);
	}

	private void addTabPanel(AbstractTabPanel newPanel, String title) {

		tabs.addTab(title, newPanel);
		tabs.setTabComponentAt(tabs.indexOfComponent(newPanel),
				new JSkatTabComponent(tabs, bitmaps));
		tabs.setSelectedComponent(newPanel);
		newPanel.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActivePlayer(String tableName, Player player) {

		tables.get(tableName).setActivePlayer(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSeriesState(String tableName, SeriesState state) {

		tables.get(tableName).setSeriesState(state);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSkat(String tableName, CardList skat) {

		tables.get(tableName).setSkat(skat);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTrickNumber(String tableName, int trickNumber) {

		tables.get(tableName).setTrickNumber(trickNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean showISSTableInvitation(String invitor, String tableName) {

		boolean result = false;

		String question = MessageFormat.format(
				strings.getString("iss_table_invitation"), //$NON-NLS-1$
				invitor, tableName);

		int answer = JOptionPane.showConfirmDialog(null, question,
				strings.getString("iss_table_invitation_title"), //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION);

		if (answer == JOptionPane.YES_OPTION) {

			result = true;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameResult(String tableName, SkatGameData gameData) {

		tables.get(tableName).addGameResult(gameData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showCardNotAllowedMessage(Card card) {

		String title = strings.getString("card_not_allowed_title"); //$NON-NLS-1$

		String message = MessageFormat.format(strings
				.getString("card_not_allowed"), //$NON-NLS-1$
				card != null ? strings.getSuitStringForCardFace(card.getSuit())
						: "--", //$NON-NLS-1$
				card != null ? strings.getRankStringForCardFace(card.getRank())
						: "--"); //$NON-NLS-1$

		showMessage(JOptionPane.ERROR_MESSAGE, title, message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTrainingResult(GameType gameType, long episodes,
			long totalWonGames, long episodeWonGames, double avgDifference) {

		trainingOverview.addTrainingResult(gameType, episodes, totalWonGames,
				episodeWonGames, avgDifference);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameNumber(String tableName, int gameNumber) {

		tables.get(tableName).setGameNumber(gameNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPlayerNames(String tableName, String upperLeftPlayerName,
			String upperRightPlayerName, String lowerPlayerName) {

		tables.get(tableName).setPlayerNames(upperLeftPlayerName,
				upperRightPlayerName, lowerPlayerName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeclarer(String tableName, Player declarer) {

		tables.get(tableName).setDeclarer(declarer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeISSPanels() {
		for (Component currPanel : tabs.getComponents()) {
			if (currPanel instanceof LobbyPanel
					|| currPanel instanceof ISSTablePanel) {
				closeTabPanel(currPanel.getName());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameResultWithoutSkatList(String tableName,
			SkatGameData gameData) {

		tables.get(tableName).setGameResultWithoutSkatList(gameData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showDuplicateTableNameMessage(String duplicateTableName) {

		String message = MessageFormat.format(
				strings.getString("duplicate_table_name"), //$NON-NLS-1$
				duplicateTableName);

		showMessage(JOptionPane.ERROR_MESSAGE,
				strings.getString("duplicate_table_name_title"), //$NON-NLS-1$
				message);
	}

	@Override
	public void setResign(String tableName, Player player) {

		tables.get(tableName).setPlayerResign(player);
	}
}
