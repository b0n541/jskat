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
package org.jskat.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.general.HideToolbarCommand;
import org.jskat.control.command.general.ShowAboutInformationCommand;
import org.jskat.control.command.general.ShowHelpCommand;
import org.jskat.control.command.general.ShowLicenseCommand;
import org.jskat.control.command.general.ShowPreferencesCommand;
import org.jskat.control.command.general.ShowToolbarCommand;
import org.jskat.control.command.general.ShowTrainingOverviewCommand;
import org.jskat.control.command.iss.IssDisconnectCommand;
import org.jskat.control.command.skatseries.CreateSkatSeriesCommand;
import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.iss.IssConnectedEvent;
import org.jskat.control.event.nntraining.TrainingResultEvent;
import org.jskat.control.event.skatgame.BidEvent;
import org.jskat.control.event.skatgame.CardDealEvent;
import org.jskat.control.event.skatgame.GameAnnouncementEvent;
import org.jskat.control.event.skatgame.GameStartEvent;
import org.jskat.control.event.skatgame.HoldBidEvent;
import org.jskat.control.event.skatgame.InvalidNumberOfCardsInDiscardedSkatEvent;
import org.jskat.control.event.skatgame.PassBidEvent;
import org.jskat.control.event.skatgame.TrickCardPlayedEvent;
import org.jskat.control.event.table.DuplicateTableNameInputEvent;
import org.jskat.control.event.table.EmptyTableNameInputEvent;
import org.jskat.control.event.table.TableCreatedEvent;
import org.jskat.control.event.table.TableGameMoveEvent;
import org.jskat.control.event.table.TableRemovedEvent;
import org.jskat.control.event.table.TrickCompletedEvent;
import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatViewType;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatSeriesData.SeriesState;
import org.jskat.data.Trick;
import org.jskat.data.iss.ChatMessage;
import org.jskat.data.iss.GameStartInformation;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.MovePlayer;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.gui.JSkatView;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.action.human.CallContraAction;
import org.jskat.gui.action.human.CallReAction;
import org.jskat.gui.action.human.DiscardAction;
import org.jskat.gui.action.human.GameAnnounceAction;
import org.jskat.gui.action.human.HoldBidAction;
import org.jskat.gui.action.human.MakeBidAction;
import org.jskat.gui.action.human.PassBidAction;
import org.jskat.gui.action.human.PickUpSkatAction;
import org.jskat.gui.action.human.PlayCardAction;
import org.jskat.gui.action.human.PlayGrandHandAction;
import org.jskat.gui.action.human.PlayHandGameAction;
import org.jskat.gui.action.human.PlaySchiebeRamschAction;
import org.jskat.gui.action.human.SchiebenAction;
import org.jskat.gui.action.iss.ChangeTableSeatsAction;
import org.jskat.gui.action.iss.ConnectAction;
import org.jskat.gui.action.iss.CreateIssTableAction;
import org.jskat.gui.action.iss.InvitePlayerAction;
import org.jskat.gui.action.iss.JoinIssTableAction;
import org.jskat.gui.action.iss.LeaveIssTableAction;
import org.jskat.gui.action.iss.LogoutAction;
import org.jskat.gui.action.iss.ObserveTableAction;
import org.jskat.gui.action.iss.OpenHomepageAction;
import org.jskat.gui.action.iss.ReadyAction;
import org.jskat.gui.action.iss.RegisterAction;
import org.jskat.gui.action.iss.ResignAction;
import org.jskat.gui.action.iss.SendChatMessageAction;
import org.jskat.gui.action.iss.ShowCardsAction;
import org.jskat.gui.action.iss.ShowLoginPanelAction;
import org.jskat.gui.action.iss.TalkEnableAction;
import org.jskat.gui.action.main.AboutAction;
import org.jskat.gui.action.main.ChangeActiveTableAction;
import org.jskat.gui.action.main.ContinueSkatSeriesAction;
import org.jskat.gui.action.main.CreateTableAction;
import org.jskat.gui.action.main.ExitAction;
import org.jskat.gui.action.main.HelpAction;
import org.jskat.gui.action.main.LicenseAction;
import org.jskat.gui.action.main.LoadNeuralNetworksAction;
import org.jskat.gui.action.main.LoadSeriesAction;
import org.jskat.gui.action.main.NextReplayMoveAction;
import org.jskat.gui.action.main.PreferencesAction;
import org.jskat.gui.action.main.PutCardIntoSkatAction;
import org.jskat.gui.action.main.ReplayGameAction;
import org.jskat.gui.action.main.ResetNeuralNetworksAction;
import org.jskat.gui.action.main.SaveNeuralNetworksAction;
import org.jskat.gui.action.main.SaveSeriesAction;
import org.jskat.gui.action.main.SaveSeriesAsAction;
import org.jskat.gui.action.main.StartSkatSeriesAction;
import org.jskat.gui.action.main.StopTrainNeuralNetworksAction;
import org.jskat.gui.action.main.TakeCardFromSkatAction;
import org.jskat.gui.action.main.TrainNeuralNetworksAction;
import org.jskat.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.gui.human.SwingHumanPlayer;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.help.JSkatHelpDialog;
import org.jskat.gui.swing.iss.ISSTablePanel;
import org.jskat.gui.swing.iss.LobbyPanel;
import org.jskat.gui.swing.iss.LoginPanel;
import org.jskat.gui.swing.iss.PlayerInvitationPanel;
import org.jskat.gui.swing.nn.NeuralNetworkTrainingOverview;
import org.jskat.gui.swing.table.SkatSeriesStartDialog;
import org.jskat.gui.swing.table.SkatTablePanel;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameVariant;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Screen;

public class JSkatViewImpl implements JSkatView {

	private static Logger LOG = LoggerFactory.getLogger(JSkatViewImpl.class);

	public final JPanel mainPanel = new JPanel();
	private JPanel toolbar;
	private final SkatSeriesStartDialog skatSeriesStartDialog;
	private final JSkatOptionsDialog preferencesDialog;
	private final NeuralNetworkTrainingOverview trainingOverview;
	private JTabbedPane tabs;
	private String activeView;
	@Deprecated
	private final Map<String, SkatTablePanel> tables = new HashMap<>();
	private final JSkatGraphicRepository bitmaps = JSkatGraphicRepository.INSTANCE;
	private final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;
	private final JSkatOptions options = JSkatOptions.instance();
	static ActionMap actions;
	private LobbyPanel issLobby;

	private static String VERSION;

	/**
	 * Constructor
	 * 
	 * @param targetScreen
	 *            Target screen for main window
	 * @param menu
	 *            Menu bar
	 * @param version
	 *            JSkat version
	 */
	public JSkatViewImpl(final Screen targetScreen, final MenuBar menu, final String version) {

		JSkatViewImpl.VERSION = version;

		JSkatEventBus.INSTANCE.register(this);

		this.skatSeriesStartDialog = new SkatSeriesStartDialog(mainPanel);
		this.preferencesDialog = new JSkatOptionsDialog(mainPanel);
		this.trainingOverview = new NeuralNetworkTrainingOverview(mainPanel);

		initActionMap(menu);
		initGUI(targetScreen);
	}

	private void initActionMap(final MenuBar menu) {

		actions = new ActionMap();

		// common actions
		final ObservableList<Menu> menus = menu.getMenus();
		final LoadSeriesAction loadSeriesAction = new LoadSeriesAction();
		loadSeriesAction.setMenuItem(menus.get(0).getItems().get(0));
		actions.put(JSkatAction.LOAD_SERIES, loadSeriesAction);
		final SaveSeriesAction saveSeriesAction = new SaveSeriesAction();
		saveSeriesAction.setMenuItem(menus.get(0).getItems().get(1));
		actions.put(JSkatAction.SAVE_SERIES, saveSeriesAction);
		final SaveSeriesAsAction saveSeriesAsAction = new SaveSeriesAsAction();
		saveSeriesAsAction.setMenuItem(menus.get(0).getItems().get(2));
		actions.put(JSkatAction.SAVE_SERIES_AS, saveSeriesAsAction);
		actions.put(JSkatAction.HELP, new HelpAction());
		actions.put(JSkatAction.LICENSE, new LicenseAction());
		actions.put(JSkatAction.EXIT_JSKAT, new ExitAction());
		actions.put(JSkatAction.PREFERENCES, new PreferencesAction());
		actions.put(JSkatAction.ABOUT_JSKAT, new AboutAction());
		actions.put(JSkatAction.CHANGE_ACTIVE_TABLE, new ChangeActiveTableAction());
		// skat table actions
		actions.put(JSkatAction.CREATE_LOCAL_TABLE, new CreateTableAction());
		actions.put(JSkatAction.START_LOCAL_SERIES, new StartSkatSeriesAction());
		actions.put(JSkatAction.CONTINUE_LOCAL_SERIES, new ContinueSkatSeriesAction());
		actions.put(JSkatAction.REPLAY_GAME, new ReplayGameAction());
		actions.put(JSkatAction.NEXT_REPLAY_STEP, new NextReplayMoveAction());
		// ISS actions
		actions.put(JSkatAction.REGISTER_ON_ISS, new RegisterAction());
		actions.put(JSkatAction.OPEN_ISS_HOMEPAGE, new OpenHomepageAction());
		actions.put(JSkatAction.SHOW_ISS_LOGIN, new ShowLoginPanelAction());
		actions.put(JSkatAction.CONNECT_TO_ISS, new ConnectAction());
		actions.put(JSkatAction.DISCONNECT_FROM_ISS, new LogoutAction());
		actions.put(JSkatAction.SEND_CHAT_MESSAGE, new SendChatMessageAction());
		actions.put(JSkatAction.CREATE_ISS_TABLE, new CreateIssTableAction());
		actions.put(JSkatAction.JOIN_ISS_TABLE, new JoinIssTableAction());
		actions.put(JSkatAction.LEAVE_ISS_TABLE, new LeaveIssTableAction());
		actions.put(JSkatAction.OBSERVE_ISS_TABLE, new ObserveTableAction());
		actions.put(JSkatAction.READY_TO_PLAY, new ReadyAction());
		actions.put(JSkatAction.TALK_ENABLED, new TalkEnableAction());
		actions.put(JSkatAction.CHANGE_TABLE_SEATS, new ChangeTableSeatsAction());
		actions.put(JSkatAction.INVITE_ISS_PLAYER, new InvitePlayerAction());
		actions.put(JSkatAction.RESIGN, new ResignAction());
		actions.put(JSkatAction.SHOW_CARDS, new ShowCardsAction());
		// Neural network actions
		actions.put(JSkatAction.TRAIN_NEURAL_NETWORKS, new TrainNeuralNetworksAction());
		actions.put(JSkatAction.STOP_TRAIN_NEURAL_NETWORKS, new StopTrainNeuralNetworksAction());
		actions.put(JSkatAction.LOAD_NEURAL_NETWORKS, new LoadNeuralNetworksAction());
		actions.put(JSkatAction.SAVE_NEURAL_NETWORKS, new SaveNeuralNetworksAction());
		actions.put(JSkatAction.RESET_NEURAL_NETWORKS, new ResetNeuralNetworksAction());
		// Human player actions
		actions.put(JSkatAction.MAKE_BID, new MakeBidAction());
		actions.put(JSkatAction.HOLD_BID, new HoldBidAction());
		actions.put(JSkatAction.PASS_BID, new PassBidAction());
		actions.put(JSkatAction.PICK_UP_SKAT, new PickUpSkatAction());
		actions.put(JSkatAction.PLAY_GRAND_HAND, new PlayGrandHandAction());
		actions.put(JSkatAction.CALL_CONTRA, new CallContraAction());
		actions.put(JSkatAction.CALL_RE, new CallReAction());
		actions.put(JSkatAction.PLAY_SCHIEBERAMSCH, new PlaySchiebeRamschAction());
		actions.put(JSkatAction.SCHIEBEN, new SchiebenAction());
		actions.put(JSkatAction.PLAY_HAND_GAME, new PlayHandGameAction());
		actions.put(JSkatAction.ANNOUNCE_GAME, new GameAnnounceAction());
		actions.put(JSkatAction.PUT_CARD_INTO_SKAT, new PutCardIntoSkatAction());
		actions.put(JSkatAction.TAKE_CARD_FROM_SKAT, new TakeCardFromSkatAction());
		actions.put(JSkatAction.DISCARD_CARDS, new DiscardAction());
		actions.put(JSkatAction.PLAY_CARD, new PlayCardAction());

		// disable some actions
		actions.get(JSkatAction.LOAD_SERIES).setEnabled(false);
		actions.get(JSkatAction.SAVE_SERIES).setEnabled(false);
		actions.get(JSkatAction.SAVE_SERIES_AS).setEnabled(false);
		actions.get(JSkatAction.START_LOCAL_SERIES).setEnabled(false);
		actions.get(JSkatAction.CREATE_ISS_TABLE).setEnabled(false);
		actions.get(JSkatAction.INVITE_ISS_PLAYER).setEnabled(false);
		actions.get(JSkatAction.REPLAY_GAME).setEnabled(false);
		actions.get(JSkatAction.NEXT_REPLAY_STEP).setEnabled(false);
	}

	private void initGUI(final Screen targetScreen) {

		mainPanel.setLayout(new BorderLayout());

		createToolbar();
		if (ScreenResolution.isBigScreen(targetScreen) && !options.isHideToolbar()) {
			addToolbar();
		}

		// main area
		addTabbedPane();
		addTabPanel(new WelcomePanel(this.strings.getString("welcome"), actions), //$NON-NLS-1$
				this.strings.getString("welcome")); //$NON-NLS-1$

		LOG.debug("GUI initialization finished.");
	}

	@Subscribe
	public void hideToolbarOn(final HideToolbarCommand command) {
		mainPanel.remove(toolbar);
		mainPanel.validate();
	}

	@Subscribe
	public void addToolbar(final ShowToolbarCommand command) {
		addToolbar();
	}

	private void addToolbar() {
		mainPanel.add(toolbar, BorderLayout.NORTH);
		mainPanel.validate();
	}

	private void addTabbedPane() {
		this.tabs = new JTabbedPane();
		this.tabs.setAutoscrolls(true);
		this.tabs.addChangeListener(new ChangeListener() {
			/**
			 * @see ChangeListener#stateChanged(ChangeEvent)
			 */
			@Override
			public void stateChanged(final ChangeEvent e) {

				if (e.getSource() instanceof JTabbedPane) {

					final JTabbedPane changedTabs = (JTabbedPane) e.getSource();
					final Component tab = changedTabs.getSelectedComponent();

					if (tab instanceof AbstractTabPanel) {

						final AbstractTabPanel panel = (AbstractTabPanel) tab;
						final String tableName = panel.getName();
						LOG.debug("showing table panel of table " + tableName); //$NON-NLS-1$
						panel.setFocus();

						JSkatMaster.INSTANCE.setActiveTable(tableName);
					}
				}
			}
		});
		mainPanel.add(this.tabs, BorderLayout.CENTER);
	}

	private void createToolbar() {
		toolbar = new JPanel(LayoutFactory.getMigLayout());
		toolbar.add(new ToolbarButton(actions.get(JSkatAction.CREATE_LOCAL_TABLE)));
		toolbar.add(new ToolbarButton(actions.get(JSkatAction.START_LOCAL_SERIES)));
		toolbar.add(new ToolbarButton(actions.get(JSkatAction.SHOW_ISS_LOGIN)));
		toolbar.add(new ToolbarButton(actions.get(JSkatAction.REPLAY_GAME)));
		toolbar.add(new ToolbarButton(actions.get(JSkatAction.NEXT_REPLAY_STEP)));
		toolbar.add(new ToolbarButton(actions.get(JSkatAction.HELP)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startGame(final String tableName) {

		this.tables.get(tableName).startGame();
	}

	@Subscribe
	public void createSkatTablePanelOn(final TableCreatedEvent event) {

		if (JSkatViewType.TRAINING_TABLE.equals(event.tableType)) {
			return;
		}

		final String tableName = event.tableName;
		String tabTitle = null;

		SkatTablePanel panel = null;
		if (JSkatViewType.LOCAL_TABLE.equals(event.tableType)) {
			panel = new SkatTablePanel(tableName, actions);
			tabTitle = tableName;
		} else if (JSkatViewType.ISS_TABLE.equals(event.tableType)) {
			panel = new ISSTablePanel(tableName, actions);
			tabTitle = strings.getString("iss_table") + ": " + tableName;
		}

		tables.put(tableName, panel);
		addTabPanel(panel, tabTitle);
		actions.get(JSkatAction.START_LOCAL_SERIES).setEnabled(true);
	}

	@Subscribe
	public void showAboutInformationDialogOn(final ShowAboutInformationCommand command) {
		JOptionPane.showMessageDialog(null,
				"JSkat " //$NON-NLS-1$
						+ this.strings.getString("version") //$NON-NLS-1$
						+ " " //$NON-NLS-1$
						+ VERSION + "\n\n" //$NON-NLS-1$
						+ "http://www.jskat.org\n" //$NON-NLS-1$
						+ "http://sourceforge.net/projects/jskat" //$NON-NLS-1$
						+ "\n\n" //$NON-NLS-1$
						+ this.strings.getString(
								"authors") //$NON-NLS-1$
				+ ":\nJan Schäfer (jansch@users.sourceforge.net)\nMarkus J. Luzius (jskat@luzius.de)\nDaniel Loreck (daniel.loreck@gmail.com)\nSascha Laurien\nSlovasim\nMartin Rothe\n\n" //$NON-NLS-1$
				+ this.strings.getString("cards") //$NON-NLS-1$
				+ ": International Skat Server, KDE project, OpenClipart.org\n\n" //$NON-NLS-1$
				+ this.strings.getString("icons") //$NON-NLS-1$
				+ ": Gnome Desktop Icons, Tango project, Elementary icons,\n" //$NON-NLS-1$
				+ "Silvestre Herrera, Alex Roberts and Icojoy\n\n" //$NON-NLS-1$
				+ this.strings.getString("background_image") + ": webtreats\n\n"
				+ "This program comes with ABSOLUTELY NO WARRANTY;\n" //$NON-NLS-1$
				+ "for details see licence dialog\n" //$NON-NLS-1$
				+ "This is free software, and you are welcome to redistribute it\n" //$NON-NLS-1$
				+ "under certain conditions; see licence dialog for details.", //$NON-NLS-1$
				this.strings.getString("about"), //$NON-NLS-1$
				JOptionPane.INFORMATION_MESSAGE, new ImageIcon(this.bitmaps.getJSkatLogoImage()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showMessage(final String title, final String message) {

		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void showAIPlayedSchwarzMessageDiscarding(final String playerName, final CardList discardedCards) {

		String cardString = "";

		if (discardedCards != null) {
			for (final Card card : discardedCards) {
				cardString += " " + this.strings.getCardStringForCardFace(card);
			}
		} else {
			cardString = this.strings.getString("unknown_cards");
		}

		showMessage(this.strings.getString("player_played_schwarz_title"),
				this.strings.getString("player_played_schwarz_discarding", playerName, cardString));
	}

	@Override
	public void showAIPlayedSchwarzMessageCardPlay(final String playerName, final Card card) {

		String cardString = null;

		if (card != null) {
			cardString = this.strings.getCardStringForCardFace(card);
		} else {
			cardString = this.strings.getString("unknown_card");
		}

		showMessage(this.strings.getString("player_played_schwarz_title"),
				this.strings.getString("player_played_schwarz_card_play", playerName, cardString));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showErrorMessage(final String title, final String message) {

		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameState(final String tableName, final GameState state) {

		this.tables.get(tableName).setGameState(state);
		if (this.activeView.equals(tableName)) {
			setActions(state);
		}
	}

	private void setActions(final GameState state) {
		switch (state) {
		case GAME_START:
			actions.get(JSkatAction.START_LOCAL_SERIES).setEnabled(true);
			actions.get(JSkatAction.REPLAY_GAME).setEnabled(false);
			actions.get(JSkatAction.NEXT_REPLAY_STEP).setEnabled(false);
			break;
		case BIDDING:
			actions.get(JSkatAction.ANNOUNCE_GAME).setEnabled(false);
			actions.get(JSkatAction.MAKE_BID).setEnabled(true);
			actions.get(JSkatAction.HOLD_BID).setEnabled(true);
			actions.get(JSkatAction.PASS_BID).setEnabled(true);
			break;
		case DISCARDING:
			actions.get(JSkatAction.ANNOUNCE_GAME).setEnabled(true);
			break;
		case RAMSCH_GRAND_HAND_ANNOUNCING:
			actions.get(JSkatAction.PLAY_GRAND_HAND).setEnabled(true);
			actions.get(JSkatAction.PLAY_SCHIEBERAMSCH).setEnabled(true);
			actions.get(JSkatAction.SCHIEBEN).setEnabled(false);
			actions.get(JSkatAction.PICK_UP_SKAT).setEnabled(false);
			break;
		case SCHIEBERAMSCH:
			actions.get(JSkatAction.PLAY_GRAND_HAND).setEnabled(false);
			actions.get(JSkatAction.PLAY_SCHIEBERAMSCH).setEnabled(false);
			actions.get(JSkatAction.SCHIEBEN).setEnabled(true);
			actions.get(JSkatAction.PICK_UP_SKAT).setEnabled(true);
			break;
		case PICKING_UP_SKAT:
			// FIXME jan 23.02.2013: use a different context panel when an
			// opponent discards
			actions.get(JSkatAction.MAKE_BID).setEnabled(false);
			actions.get(JSkatAction.HOLD_BID).setEnabled(false);
			actions.get(JSkatAction.PASS_BID).setEnabled(false);
			actions.get(JSkatAction.PICK_UP_SKAT).setEnabled(true);
			actions.get(JSkatAction.ANNOUNCE_GAME).setEnabled(true);
			break;
		case TRICK_PLAYING:
			if (this.options.isPlayContra()) {
				actions.get(JSkatAction.CALL_CONTRA).setEnabled(true);
			} else {
				actions.get(JSkatAction.CALL_CONTRA).setEnabled(false);
			}
			break;
		case GAME_OVER:
			actions.get(JSkatAction.CONTINUE_LOCAL_SERIES).setEnabled(true);
			actions.get(JSkatAction.REPLAY_GAME).setEnabled(true);
			actions.get(JSkatAction.NEXT_REPLAY_STEP).setEnabled(true);
			break;
		default:
			break;
		}
	}

	@Subscribe
	public void showHelpDialogOn(final ShowHelpCommand command) {

		new JSkatHelpDialog(null, this.strings.getString("help"), //$NON-NLS-1$
				"org/jskat/gui/help/" + JSkatOptions.instance().getI18NCode() //$NON-NLS-1$
						+ "/contents.html") //$NON-NLS-1$
								.setVisible(true);
	}

	@Subscribe
	public void showLicenceDialogOn(final ShowLicenseCommand command) {

		new JSkatHelpDialog(null, this.strings.getString("license"), //$NON-NLS-1$
				"org/jskat/gui/help/gpl3.html").setVisible(true); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBidValueToMake(final String tableName, final int bidValue) {

		this.tables.get(tableName).setBidValueToMake(bidValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBidValueToHold(final String tableName, final int bidValue) {

		this.tables.get(tableName).setBidValueToHold(bidValue);
	}

	@Subscribe
	public void showSkatSeriesStartDialogOn(final CreateSkatSeriesCommand command) {

		this.skatSeriesStartDialog.setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showISSLogin() {

		final LoginPanel loginPanel = new LoginPanel("ISS login", actions);
		addTabPanel(loginPanel, "ISS login");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSLobbyPlayerList(final String playerName, final String language, final long gamesPlayed,
			final double strength) {

		this.issLobby.updatePlayer(playerName, language, gamesPlayed, strength);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFromISSLobbyPlayerList(final String playerName) {

		this.issLobby.removePlayer(playerName);
	}

	@Subscribe
	public void showIssLobbyOn(final IssConnectedEvent event) {
		// show ISS lobby if connection was successfull
		// FIXME (jan 07.12.2010) use constant instead of title
		closeTabPanel("ISS login"); //$NON-NLS-1$
		this.issLobby = new LobbyPanel("ISS lobby", actions);
		addTabPanel(this.issLobby, this.strings.getString("iss_lobby"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSLobbyTableList(final String tableName, final int maxPlayers, final long gamesPlayed,
			final String player1, final String player2, final String player3) {

		this.issLobby.updateTable(tableName, maxPlayers, gamesPlayed, player1, player2, player3);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFromISSLobbyTableList(final String tableName) {

		this.issLobby.removeTable(tableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendISSChatMessage(final ChatMessageType messageType, final ChatMessage message) {

		LOG.debug("appendISSChatMessage"); //$NON-NLS-1$

		this.issLobby.appendChatMessage(message);

		for (final SkatTablePanel table : this.tables.values()) {
			if (table instanceof ISSTablePanel) {
				final ISSTablePanel issTable = (ISSTablePanel) table;
				final String chatname = message.getChatName();
				if ("Lobby".equals(chatname) || issTable.getName().equals(chatname)) {
					issTable.appendChatMessage(message);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSTable(final String tableName, final TablePanelStatus tableStatus) {

		// FIXME (jan 08.11.2010) seems very complicated
		final SkatTablePanel panel = this.tables.get(tableName);

		if (panel instanceof ISSTablePanel) {

			((ISSTablePanel) panel).setTableStatus(tableStatus);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSTable(final String tableName, final String issLogin, final GameStartInformation status) {

		if (issLogin.equals(status.getPlayerName(Player.FOREHAND))) {

			updateISSTable(tableName, Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND, status);
		} else if (issLogin.equals(status.getPlayerName(Player.MIDDLEHAND))) {

			updateISSTable(tableName, Player.REARHAND, Player.FOREHAND, Player.MIDDLEHAND, status);
		} else if (issLogin.equals(status.getPlayerName(Player.REARHAND))) {

			updateISSTable(tableName, Player.FOREHAND, Player.MIDDLEHAND, Player.REARHAND, status);
		}
	}

	private void updateISSTable(final String tableName, final Player leftOpponent, final Player rightOpponent,
			final Player player, final GameStartInformation status) {

		LOG.debug("Updating ISS table: " + tableName + " " + leftOpponent + " " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ rightOpponent + " " + player); //$NON-NLS-1$

		JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(
				new GameStartEvent(status.getGameNo(), GameVariant.STANDARD, leftOpponent, rightOpponent, player));

		// FIXME (jansch 09.11.2010) this is only done for ISS games
		final SkatTablePanel table = this.tables.get(tableName);
		table.setGameState(GameState.GAME_START);
		table.setPlayerName(leftOpponent, status.getPlayerName(leftOpponent));
		table.setPlayerTime(leftOpponent, status.getPlayerTime(leftOpponent));
		table.setPlayerName(rightOpponent, status.getPlayerName(rightOpponent));
		table.setPlayerTime(rightOpponent, status.getPlayerTime(rightOpponent));
		table.setPlayerName(player, status.getPlayerName(player));
		table.setPlayerTime(player, status.getPlayerTime(player));

		table.setPlayerNames(status.getPlayerName(leftOpponent), false, status.getPlayerName(rightOpponent), false,
				status.getPlayerName(player), false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNewTableName(final int localTablesCreated) {
		// get table name
		String tableName = JOptionPane.showInputDialog(null, this.strings.getString("new_table_dialog_message"), //$NON-NLS-1$
				this.strings.getString("local_table") + " " //$NON-NLS-1$ //$NON-NLS-2$
						+ (localTablesCreated + 1));
		// truncate table name
		if (tableName != null && tableName.length() > 100) {
			tableName = tableName.substring(0, 100);
		}
		return tableName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateISSMove(final String tableName, final SkatGameData gameData,
			final MoveInformation moveInformation) {

		final Player movePlayer = moveInformation.getPlayer();

		switch (moveInformation.getType()) {
		// TODO add other types too
		case DEAL:
			setGameState(tableName, GameState.DEALING);
			final SkatTablePanel table = this.tables.get(tableName);
			table.hideCards(Player.FOREHAND);
			table.hideCards(Player.MIDDLEHAND);
			table.hideCards(Player.REARHAND);

			final Map<Player, CardList> dealtCards = new HashMap<>();
			dealtCards.put(Player.FOREHAND, moveInformation.getCards(Player.FOREHAND));
			dealtCards.put(Player.MIDDLEHAND, moveInformation.getCards(Player.MIDDLEHAND));
			dealtCards.put(Player.REARHAND, moveInformation.getCards(Player.REARHAND));
			JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new CardDealEvent(dealtCards, new CardList()));
			setGameState(tableName, GameState.BIDDING);
			break;
		case BID:
			setGameState(tableName, GameState.BIDDING);
			JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)
					.post(new BidEvent(movePlayer, moveInformation.getBidValue()));
			setBidValueToHold(tableName, moveInformation.getBidValue());
			break;
		case HOLD_BID:
			setGameState(tableName, GameState.BIDDING);
			JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)
					.post(new HoldBidEvent(movePlayer, gameData.getMaxBidValue()));
			setBidValueToMake(tableName, SkatConstants.getNextBidValue(gameData.getMaxBidValue()));
			break;
		case PASS:
			setGameState(tableName, GameState.BIDDING);
			JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new PassBidEvent(movePlayer));
			setBidValueToMake(tableName, SkatConstants.getNextBidValue(gameData.getMaxBidValue()));
			break;
		case SKAT_REQUEST:
			setGameState(tableName, GameState.PICKING_UP_SKAT);
			break;
		case PICK_UP_SKAT:
			setGameState(tableName, GameState.DISCARDING);
			if (moveInformation.getSkat().size() == 2) {
				setSkat(tableName, moveInformation.getSkat());
			}
			break;
		case GAME_ANNOUNCEMENT:
			setGameState(tableName, GameState.DECLARING);
			JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName,
					new GameAnnouncementEvent(movePlayer, moveInformation.getGameAnnouncement())));
			if (moveInformation.getGameAnnouncement().isOuvert()) {
				showCardsForPlayer(tableName, movePlayer, moveInformation.getOuvertCards());
			}
			setGameState(tableName, GameState.TRICK_PLAYING);
			break;
		case CARD_PLAY:
			setGameState(tableName, GameState.TRICK_PLAYING);

			if (gameData.getTricks().size() > 1) {

				final Trick currentTrick = gameData.getCurrentTrick();
				final Trick lastTrick = gameData.getLastCompletedTrick();

				if (currentTrick.getFirstCard() != null && currentTrick.getSecondCard() == null
						&& currentTrick.getThirdCard() == null) {
					// first card in new trick
					JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new TrickCompletedEvent(lastTrick));
				}
			}

			JSkatEventBus.INSTANCE.post(
					new TableGameMoveEvent(tableName, new TrickCardPlayedEvent(movePlayer, moveInformation.getCard())));
			break;
		case SHOW_CARDS:
			showCardsForPlayer(tableName, movePlayer, moveInformation.getOuvertCards());
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
			final SkatTablePanel table = this.tables.get(tableName);

			table.setPlayerTime(Player.FOREHAND, moveInformation.getPlayerTime(Player.FOREHAND));
			table.setPlayerTime(Player.MIDDLEHAND, moveInformation.getPlayerTime(Player.MIDDLEHAND));
			table.setPlayerTime(Player.REARHAND, moveInformation.getPlayerTime(Player.REARHAND));
		}
	}

	private void showCardsForPlayer(final String tableName, final Player player, final CardList cards) {
		final Map<Player, CardList> ouvertCards = new HashMap<>();
		ouvertCards.put(player, cards);
		JSkatEventBus.INSTANCE.post(new ShowCardsCommand(tableName, ouvertCards));
	}

	@Subscribe
	public void showPreferencesDialogOn(final ShowPreferencesCommand command) {

		this.preferencesDialog.validate();
		this.preferencesDialog.setVisible(true);
	}

	@Subscribe
	public void showTrainingOverviewDialogOn(final ShowTrainingOverviewCommand command) {

		this.trainingOverview.setVisible(true);
	}

	@Subscribe
	public void closeTableOn(final TableRemovedEvent event) {
		closeTabPanel(event.tableName);
	}

	/**
	 * {@inheritDoc}
	 */
	private void closeTabPanel(final String tabName) {

		AbstractTabPanel panel = (AbstractTabPanel) this.tabs.getSelectedComponent();
		if (!tabName.equals(panel.getName())) {
			for (final Component currPanel : this.tabs.getComponents()) {
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
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getPlayerForInvitation(final Set<String> playerNames) {

		final List<String> result = new ArrayList<>();

		final PlayerInvitationPanel invitationPanel = new PlayerInvitationPanel(playerNames);
		final int dialogResult = JOptionPane.showConfirmDialog(null, invitationPanel,
				this.strings.getString("invite_players"), //$NON-NLS-1$
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (dialogResult == JOptionPane.OK_OPTION) {
			result.addAll(invitationPanel.getPlayer());
		}

		LOG.debug("Players to invite: " + result); //$NON-NLS-1$

		return result;
	}

	private void addTabPanel(final AbstractTabPanel newPanel, final String title) {

		this.tabs.addTab(title, newPanel);
		this.tabs.setTabComponentAt(this.tabs.indexOfComponent(newPanel),
				new JSkatTabComponent(this.tabs, this.bitmaps));
		this.tabs.setSelectedComponent(newPanel);
		newPanel.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSeriesState(final String tableName, final SeriesState state) {

		this.tables.get(tableName).setSeriesState(state);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSkat(final String tableName, final CardList skat) {

		this.tables.get(tableName).setSkat(skat);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean showISSTableInvitation(final String invitor, final String tableName) {

		boolean result = false;

		final String question = this.strings.getString("iss_table_invitation", //$NON-NLS-1$
				invitor, tableName);

		final int answer = JOptionPane.showConfirmDialog(null, question,
				this.strings.getString("iss_table_invitation_title"), //$NON-NLS-1$
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
	public void showCardNotAllowedMessage(final Card card) {

		final String title = this.strings.getString("card_not_allowed_title"); //$NON-NLS-1$

		final String message = this.strings.getString("card_not_allowed_message", //$NON-NLS-1$
				card != null ? this.strings.getSuitStringForCardFace(card.getSuit()) : "--", //$NON-NLS-1$
				card != null ? this.strings.getRankStringForCardFace(card.getRank()) : "--"); //$NON-NLS-1$

		showErrorMessage(title, message);
	}

	@Subscribe
	public void addTrainingResultOn(final TrainingResultEvent event) {

		this.trainingOverview.addTrainingResult(event.gameType, event.episodes, event.totalGamesWon,
				event.avgNetworkErrorDeclarer, event.avgNetworkErrorOpponents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPlayerNames(String tableName, String upperLeftPlayerName, boolean isUpperLeftPlayerAIPlayer,
			String upperRightPlayerName, boolean isUpperRightPlayerAIPlayer, String lowerPlayerName,
			boolean isLowerPlayerAIPlayer) {

		this.tables.get(tableName).setPlayerNames(upperLeftPlayerName, isUpperLeftPlayerAIPlayer, upperRightPlayerName,
				isUpperRightPlayerAIPlayer, lowerPlayerName, isLowerPlayerAIPlayer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeclarer(final String tableName, final Player declarer) {

		this.tables.get(tableName).setDeclarer(declarer);
	}

	@Subscribe
	public void closeAllIssTabsOn(final IssDisconnectCommand event) {

		for (final Component currPanel : this.tabs.getComponents()) {
			if (currPanel instanceof LobbyPanel || currPanel instanceof ISSTablePanel) {
				closeTabPanel(currPanel.getName());
			}
		}
	}

	@Subscribe
	public void showErrorMessageOn(final InvalidNumberOfCardsInDiscardedSkatEvent event) {
		showErrorMessage(this.strings.getString("invalid_number_of_cards_in_skat_title"), //$NON-NLS-1$
				this.strings.getString("invalid_number_of_cards_in_skat_message"));
	}

	@Subscribe
	public void showErrorMessageOn(final DuplicateTableNameInputEvent event) {

		final String message = this.strings.getString("duplicate_table_name_message", //$NON-NLS-1$
				event.tableName);

		showErrorMessage(this.strings.getString("duplicate_table_name_title"), //$NON-NLS-1$
				message);
	}

	@Subscribe
	public void showErrorMessageOn(final EmptyTableNameInputEvent event) {

		showErrorMessage(this.strings.getString("invalid_name_input_null_title"), //$NON-NLS-1$
				this.strings.getString("invalid_name_input_null_message"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResign(final String tableName, final Player player) {

		this.tables.get(tableName).setResign(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGeschoben(final String tableName, final Player player) {
		this.tables.get(tableName).setGeschoben(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDiscardedSkat(final String tableName, final Player player, final CardList skatBefore,
			final CardList discardedSkat) {
		this.tables.get(tableName).setDiscardedSkat(player, skatBefore, discardedSkat);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void takeCardFromSkat(final String tableName, final Card card) {
		this.tables.get(tableName).takeCardFromSkat(card);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putCardIntoSkat(final String tableName, final Card card) {
		this.tables.get(tableName).putCardIntoSkat(card);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void openWebPage(final String link) {
		try {
			final Desktop desktop = java.awt.Desktop.getDesktop();
			final URI uri = new URI(link);
			desktop.browse(uri);
		} catch (final URISyntaxException except) {
			LOG.error(except.toString());
		} catch (final IOException except) {
			LOG.error(except.toString());
		}
	}

	@Override
	public AbstractHumanJSkatPlayer getHumanPlayerForGUI() {
		return new SwingHumanPlayer();
	}

	@Override
	public void setActiveView(final String viewName) {
		this.activeView = viewName;
	}
}
