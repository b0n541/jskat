package org.jskat.gui.swing;

import com.google.common.eventbus.Subscribe;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Screen;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.general.*;
import org.jskat.control.command.iss.IssDisconnectCommand;
import org.jskat.control.command.skatseries.CreateSkatSeriesCommand;
import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.iss.IssConnectedEvent;
import org.jskat.control.event.skatgame.*;
import org.jskat.control.event.table.*;
import org.jskat.control.gui.JSkatView;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatViewType;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.Trick;
import org.jskat.data.iss.*;
import org.jskat.gui.action.human.*;
import org.jskat.gui.action.iss.*;
import org.jskat.gui.action.main.*;
import org.jskat.gui.human.SwingHumanPlayer;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.help.JSkatHelpDialog;
import org.jskat.gui.swing.iss.ISSTablePanel;
import org.jskat.gui.swing.iss.LobbyPanel;
import org.jskat.gui.swing.iss.LoginPanel;
import org.jskat.gui.swing.iss.PlayerInvitationPanel;
import org.jskat.gui.swing.table.SkatSeriesStartDialog;
import org.jskat.gui.swing.table.SkatTablePanel;
import org.jskat.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;

public class JSkatViewImpl implements JSkatView {

    private static final Logger LOG = LoggerFactory.getLogger(JSkatViewImpl.class);

    public final JPanel mainPanel = new JPanel();
    private JPanel toolbar;
    private final SkatSeriesStartDialog skatSeriesStartDialog;
    private final JSkatOptionsDialog preferencesDialog;
    private JTabbedPane tabs;
    private String activeView;
    @Deprecated
    private final Map<String, SkatTablePanel> tables = new HashMap<>();
    private final JSkatGraphicRepository bitmaps = JSkatGraphicRepository.INSTANCE;
    private final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;
    private final JSkatOptions options = JSkatOptions.instance();
    public static ActionMap actions;
    private LobbyPanel issLobby;

    private static String VERSION;

    /**
     * Constructor
     *
     * @param targetScreen Target screen for main window
     * @param menu         Menu bar
     * @param version      JSkat version
     */
    public JSkatViewImpl(Screen targetScreen, MenuBar menu, String version) {

        JSkatViewImpl.VERSION = version;

        JSkatEventBus.INSTANCE.register(this);

        skatSeriesStartDialog = new SkatSeriesStartDialog(mainPanel);
        preferencesDialog = new JSkatOptionsDialog(mainPanel);

        initActionMap(menu);
        initGUI(targetScreen);
    }

    private static void initActionMap(MenuBar menu) {

        actions = new ActionMap();

        // common actions
        ObservableList<Menu> menus = menu.getMenus();
        LoadSeriesAction loadSeriesAction = new LoadSeriesAction();
        loadSeriesAction.setMenuItem(menus.get(0).getItems().get(0));
        actions.put(JSkatAction.LOAD_SERIES, loadSeriesAction);
        SaveSeriesAction saveSeriesAction = new SaveSeriesAction();
        saveSeriesAction.setMenuItem(menus.get(0).getItems().get(1));
        actions.put(JSkatAction.SAVE_SERIES, saveSeriesAction);
        SaveSeriesAsAction saveSeriesAsAction = new SaveSeriesAsAction();
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

    private void initGUI(Screen targetScreen) {

        mainPanel.setLayout(new BorderLayout());

        createToolbar();
        if (ScreenResolution.isBigScreen(targetScreen) && !options.isHideToolbar()) {
            addToolbar();
        }

        // main area
        addTabbedPane();
        addTabPanel(new WelcomePanel(strings.getString("welcome"), actions),
                strings.getString("welcome"));

        LOG.debug("GUI initialization finished.");
    }

    @Subscribe
    public void hideToolbarOn(HideToolbarCommand command) {

        SwingUtilities.invokeLater(() -> {
            mainPanel.remove(toolbar);
            mainPanel.validate();
        });
    }

    @Subscribe
    public void showToolbarOn(ShowToolbarCommand command) {

        SwingUtilities.invokeLater(() -> addToolbar());
    }

    private void addToolbar() {
        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.validate();
    }

    private void addTabbedPane() {
        tabs = new JTabbedPane();
        tabs.setAutoscrolls(true);
        tabs.addChangeListener(e -> {

            if (e.getSource() instanceof JTabbedPane changedTabs) {

                Component tab = changedTabs.getSelectedComponent();

                if (tab instanceof AbstractTabPanel panel) {

                    String tableName = panel.getName();
                    LOG.debug("showing table panel of table " + tableName);
                    panel.setFocus();

                    JSkatMaster.INSTANCE.setActiveTable(tableName);
                }
            }
        });
        mainPanel.add(tabs, BorderLayout.CENTER);
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
    public void startGame(String tableName) {

        tables.get(tableName).startGame();
    }

    @Subscribe
    public void createSkatTablePanelOn(TableCreatedEvent event) {

        if (JSkatViewType.TRAINING_TABLE.equals(event.tableType)) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            String tableName = event.tableName;
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
        });
    }

    @Subscribe
    public void showAboutInformationDialogOn(ShowAboutInformationCommand command) {

        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainPanel, "JSkat "
                        + strings.getString("version")
                        + " "
                        + VERSION + "\n\n"
                        + "http://www.jskat.org\n"
                        + "http://sourceforge.net/projects/jskat"
                        + "\n\n"
                        + strings.getString("authors")
                        + ":\nJan Schäfer (jansch@users.sourceforge.net)\nMarkus J. Luzius (jskat@luzius.de)\nDaniel Loreck (daniel.loreck@gmail.com)\nSascha Laurien\nSlovasim\nMartin Rothe\nTobias Markus\n\n"
                        + strings.getString("cards")
                        + ": International Skat Server, KDE project, OpenClipart.org\n\n"
                        + strings.getString("icons")
                        + ": Gnome Desktop Icons, Tango project, Elementary icons,\n"
                        + "Silvestre Herrera, Alex Roberts and Icojoy\n\n"
                        + strings.getString("background_image") + ": webtreats\n\n"
                        + "This program comes with ABSOLUTELY NO WARRANTY;\n"
                        + "for details see licence dialog\n"
                        + "This is free software, and you are welcome to redistribute it\n"
                        + "under certain conditions; see licence dialog for details.",
                strings.getString("about"),
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(bitmaps.getJSkatLogoImage())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showMessage(String title, String message) {

        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE));
    }

    @Override
    public void showAIPlayedSchwarzMessageDiscarding(String playerName, CardList discardedCards) {

        String cardString = "";

        if (discardedCards != null) {
            for (Card card : discardedCards) {
                cardString += " " + strings.getCardStringForCardFace(card);
            }
        } else {
            cardString = strings.getString("unknown_cards");
        }

        showMessage(strings.getString("player_played_schwarz_title"),
                strings.getString("player_played_schwarz_discarding", playerName, cardString));
    }

    @Override
    public void showAIPlayedSchwarzMessageCardPlay(String playerName, Card card) {

        String cardString = null;

        if (card != null) {
            cardString = strings.getCardStringForCardFace(card);
        } else {
            cardString = strings.getString("unknown_card");
        }

        showMessage(strings.getString("player_played_schwarz_title"),
                strings.getString("player_played_schwarz_card_play", playerName, cardString));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showErrorMessage(String title, String message) {

        SwingUtilities
                .invokeLater(() -> JOptionPane.showMessageDialog(mainPanel, message, title, JOptionPane.ERROR_MESSAGE));
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    public void setGameStateOn(SkatGameStateChangedEvent event) {

        if (activeView.equals(event.tableName)) {
            setActions(event.gameState);
        }
    }

    private void setActions(GameState state) {
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
                actions.get(JSkatAction.CALL_CONTRA).setEnabled(options.isPlayContra());
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
    public void showHelpDialogOn(ShowHelpCommand command) {

        SwingUtilities.invokeLater(() -> new JSkatHelpDialog(mainPanel, strings.getString("help"),
                "org/jskat/gui/help/" + JSkatOptions.instance().getI18NCode() + "/contents.html").setVisible(true));
    }

    @Subscribe
    public void showLicenceDialogOn(ShowLicenseCommand command) {

        SwingUtilities.invokeLater(
                () -> new JSkatHelpDialog(mainPanel, strings.getString("license"), "org/jskat/gui/help/apache2.html")
                        .setVisible(true));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBidValueToMake(String tableName, int bidValue) {

        SwingUtilities.invokeLater(() -> tables.get(tableName).setBidValueToMake(bidValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBidValueToHold(String tableName, int bidValue) {

        SwingUtilities.invokeLater(() -> tables.get(tableName).setBidValueToHold(bidValue));
    }

    @Subscribe
    public void showSkatSeriesStartDialogOn(CreateSkatSeriesCommand command) {

        SwingUtilities.invokeLater(() -> skatSeriesStartDialog.setVisible(true));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showISSLogin() {

        SwingUtilities.invokeLater(() -> {
            LoginPanel loginPanel = new LoginPanel("ISS login", actions);
            addTabPanel(loginPanel, "ISS login");
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateISSLobbyPlayerList(String playerName, String language, long gamesPlayed,
                                         double strength) {

        issLobby.updatePlayer(playerName, language, gamesPlayed, strength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromISSLobbyPlayerList(String playerName) {

        SwingUtilities.invokeLater(() -> issLobby.removePlayer(playerName));
    }

    @Subscribe
    public void showIssLobbyOn(IssConnectedEvent event) {

        SwingUtilities.invokeLater(() -> {
            // show ISS lobby if connection was successfull
            // FIXME (jan 07.12.2010) use constant instead of title
            closeTabPanel("ISS login");
            issLobby = new LobbyPanel("ISS lobby", actions);
            addTabPanel(issLobby, strings.getString("iss_lobby"));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateISSLobbyTableList(String tableName, int maxPlayers, long gamesPlayed,
                                        String player1, String player2, String player3) {

        issLobby.updateTable(tableName, maxPlayers, gamesPlayed, player1, player2, player3);
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
    public void appendISSChatMessage(ChatMessageType messageType, ChatMessage message) {

        LOG.debug("appendISSChatMessage");

        issLobby.appendChatMessage(message);

        for (SkatTablePanel table : tables.values()) {
            if (table instanceof ISSTablePanel issTable) {
                String chatname = message.getChatName();
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
    public void updateISSTable(String tableName, TablePanelStatus tableStatus) {

        // FIXME (jan 08.11.2010) seems very complicated
        SkatTablePanel panel = tables.get(tableName);

        if (panel instanceof ISSTablePanel) {

            ((ISSTablePanel) panel).setTableStatus(tableStatus);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateISSTable(String tableName, String issLogin, GameStartInformation status) {

        if (issLogin.equals(status.getPlayerName(Player.FOREHAND))) {

            updateISSTable(tableName, Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND, status);
        } else if (issLogin.equals(status.getPlayerName(Player.MIDDLEHAND))) {

            updateISSTable(tableName, Player.REARHAND, Player.FOREHAND, Player.MIDDLEHAND, status);
        } else if (issLogin.equals(status.getPlayerName(Player.REARHAND))) {

            updateISSTable(tableName, Player.FOREHAND, Player.MIDDLEHAND, Player.REARHAND, status);
        }
    }

    private void updateISSTable(String tableName, Player leftOpponent, Player rightOpponent,
                                Player player, GameStartInformation status) {

        LOG.debug("Updating ISS table: " + tableName + " " + leftOpponent + " " + rightOpponent + " " + player);

        JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(
                new GameStartEvent(status.getGameNo(), GameVariant.STANDARD, leftOpponent, rightOpponent, player));

        // FIXME (jansch 09.11.2010) this is only done for ISS games
        SkatTablePanel table = tables.get(tableName);
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
        String tableName = JOptionPane.showInputDialog(null, strings.getString("new_table_dialog_message"),
                strings.getString("local_table") + " " + (localTablesCreated + 1));
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
    public void updateISSMove(String tableName, SkatGameData gameData,
                              MoveInformation moveInformation) {

        Player movePlayer = moveInformation.getPlayer();

        switch (moveInformation.getType()) {
            // TODO add other types too
            case DEAL:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.DEALING));
                SkatTablePanel table = tables.get(tableName);
                table.hideCards(Player.FOREHAND);
                table.hideCards(Player.MIDDLEHAND);
                table.hideCards(Player.REARHAND);

                Map<Player, CardList> dealtCards = new HashMap<>();
                dealtCards.put(Player.FOREHAND, moveInformation.getCards(Player.FOREHAND));
                dealtCards.put(Player.MIDDLEHAND, moveInformation.getCards(Player.MIDDLEHAND));
                dealtCards.put(Player.REARHAND, moveInformation.getCards(Player.REARHAND));
                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new CardDealEvent(dealtCards, new CardList()));
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.BIDDING));
                break;
            case BID:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.BIDDING));
                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)
                        .post(new BidEvent(movePlayer, moveInformation.getBidValue()));
                setBidValueToHold(tableName, moveInformation.getBidValue());
                break;
            case HOLD_BID:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.BIDDING));
                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)
                        .post(new HoldBidEvent(movePlayer, gameData.getMaxBidValue()));
                setBidValueToMake(tableName, SkatConstants.getNextBidValue(gameData.getMaxBidValue()));
                break;
            case PASS:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.BIDDING));
                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new PassBidEvent(movePlayer));
                setBidValueToMake(tableName, SkatConstants.getNextBidValue(gameData.getMaxBidValue()));
                break;
            case SKAT_REQUEST:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.PICKING_UP_SKAT));
                break;
            case PICK_UP_SKAT:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.DISCARDING));
                if (moveInformation.getSkat().size() == 2) {
                    JSkatEventBus.INSTANCE.post(new SkatCardsChangedEvent(tableName, moveInformation.getSkat()));
                }
                break;
            case GAME_ANNOUNCEMENT:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.DECLARING));
                JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName,
                        new GameAnnouncementEvent(movePlayer, moveInformation.getGameAnnouncement())));
                if (moveInformation.getGameAnnouncement().isOuvert()) {
                    showCardsForPlayer(tableName, movePlayer, moveInformation.getOuvertCards());
                }
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.TRICK_PLAYING));
                break;
            case CARD_PLAY:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.TRICK_PLAYING));

                if (gameData.getTricks().size() > 1) {

                    Trick currentTrick = gameData.getCurrentTrick();
                    Trick lastTrick = gameData.getLastCompletedTrick();

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
            SkatTablePanel table = tables.get(tableName);

            table.setPlayerTime(Player.FOREHAND, moveInformation.getPlayerTime(Player.FOREHAND));
            table.setPlayerTime(Player.MIDDLEHAND, moveInformation.getPlayerTime(Player.MIDDLEHAND));
            table.setPlayerTime(Player.REARHAND, moveInformation.getPlayerTime(Player.REARHAND));
        }
    }

    private static void showCardsForPlayer(String tableName, Player player, CardList cards) {
        JSkatEventBus.INSTANCE.post(new ShowCardsCommand(tableName, player, cards));
    }

    @Subscribe
    public void showPreferencesDialogOn(ShowPreferencesCommand command) {

        SwingUtilities.invokeLater(() -> {
            preferencesDialog.validate();
            preferencesDialog.setVisible(true);
        });
    }

    @Subscribe
    public void closeTableOn(TableRemovedEvent event) {

        SwingUtilities.invokeLater(() -> closeTabPanel(event.tableName));
    }

    /**
     * {@inheritDoc}
     */
    private void closeTabPanel(String tabName) {

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

        List<String> result = new ArrayList<>();

        PlayerInvitationPanel invitationPanel = new PlayerInvitationPanel(playerNames);
        int dialogResult = JOptionPane.showConfirmDialog(null, invitationPanel,
                strings.getString("invite_players"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (dialogResult == JOptionPane.OK_OPTION) {
            result.addAll(invitationPanel.getPlayer());
        }

        LOG.debug("Players to invite: " + result);

        return result;
    }

    private void addTabPanel(AbstractTabPanel newPanel, String title) {

        tabs.addTab(title, newPanel);
        tabs.setTabComponentAt(tabs.indexOfComponent(newPanel), new JSkatTabComponent(tabs, bitmaps));
        tabs.setSelectedComponent(newPanel);
        newPanel.setFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showISSTableInvitation(String invitor, String tableName) {

        boolean result = false;

        String question = strings.getString("iss_table_invitation",
                invitor, tableName);

        int answer = JOptionPane.showConfirmDialog(null, question,
                strings.getString("iss_table_invitation_title"),
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
    public void showCardNotAllowedMessage(Card card) {

        String title = strings.getString("card_not_allowed_title");

        String message = strings.getString("card_not_allowed_message",
                card != null ? strings.getSuitStringForCardFace(card.getSuit()) : "--",
                card != null ? strings.getRankStringForCardFace(card.getRank()) : "--");

        showErrorMessage(title, message);
    }

    @Subscribe
    public void closeAllIssTabsOn(IssDisconnectCommand event) {

        SwingUtilities.invokeLater(() -> {
            for (Component currPanel : tabs.getComponents()) {
                if (currPanel instanceof LobbyPanel || currPanel instanceof ISSTablePanel) {
                    closeTabPanel(currPanel.getName());
                }
            }
        });
    }

    @Subscribe
    public void showErrorMessageOn(InvalidNumberOfCardsInDiscardedSkatEvent event) {
        showErrorMessage(strings.getString("invalid_number_of_cards_in_skat_title"),
                strings.getString("invalid_number_of_cards_in_skat_message"));
    }

    @Subscribe
    public void showErrorMessageOn(DuplicateTableNameInputEvent event) {

        String message = strings.getString("duplicate_table_name_message",
                event.tableName);

        showErrorMessage(strings.getString("duplicate_table_name_title"),
                message);
    }

    @Subscribe
    public void showErrorMessageOn(EmptyTableNameInputEvent event) {

        showErrorMessage(strings.getString("invalid_name_input_null_title"),
                strings.getString("invalid_name_input_null_message"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResign(String tableName, Player player) {

        tables.get(tableName).setResign(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGeschoben(String tableName, Player player) {
        tables.get(tableName).setGeschoben(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDiscardedSkat(String tableName, Player player, CardList skatBefore,
                                 CardList discardedSkat) {
        tables.get(tableName).setDiscardedSkat(player, skatBefore, discardedSkat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openWebPage(String link) {
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI uri = new URI(link);
            desktop.browse(uri);
        } catch (URISyntaxException except) {
            LOG.error(except.toString());
        } catch (IOException except) {
            LOG.error(except.toString());
        }
    }

    @Override
    public AbstractHumanJSkatPlayer getHumanPlayerForGUI() {
        return new SwingHumanPlayer();
    }

    @Override
    public void setActiveView(String viewName) {
        activeView = viewName;
    }
}
