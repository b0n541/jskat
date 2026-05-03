package org.jskat.gui.swing;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.general.HideToolbarCommand;
import org.jskat.control.command.general.ShowHelpCommand;
import org.jskat.control.command.general.ShowLicenseCommand;
import org.jskat.control.command.general.ShowToolbarCommand;
import org.jskat.control.command.iss.IssDisconnectCommand;
import org.jskat.control.command.iss.IssInvitePlayerCommand;
import org.jskat.control.command.iss.IssShowLoginCommand;
import org.jskat.control.command.table.RequestCreateTableCommand;
import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.command.table.StartSkatSeriesCommand;
import org.jskat.control.event.iss.IssConnectedEvent;
import org.jskat.control.event.iss.IssPlayerDataUpdatedEvent;
import org.jskat.control.event.iss.IssTableDataUpdatedEvent;
import org.jskat.control.event.iss.IssTableDeletedEvent;
import org.jskat.control.event.skatgame.*;
import org.jskat.control.event.table.*;
import org.jskat.control.gui.JSkatView;
import org.jskat.control.gui.action.JSkatAction;
import org.jskat.control.gui.human.AbstractHumanJSkatPlayer;
import org.jskat.data.*;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.MovePlayer;
import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.human.SwingHumanPlayer;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.javafx.dialog.help.JSkatHelpDialog;
import org.jskat.gui.javafx.iss.LobbyPanelFX;
import org.jskat.gui.javafx.iss.LoginPanelFX;
import org.jskat.gui.javafx.main.JSkatActions;
import org.jskat.gui.javafx.table.SkatSeriesStartDialog;
import org.jskat.gui.javafx.table.SkatTablePanel;
import org.jskat.gui.swing.iss.ISSTablePanelWrapper;
import org.jskat.gui.swing.table.SkatTablePanelWrapper;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.FutureTask;

public class JSkatViewImpl implements JSkatView {

    private static final Logger LOG = LoggerFactory.getLogger(JSkatViewImpl.class);

    public final JPanel mainPanel;
    private JPanel toolbar;
    private JTabbedPane tabs;
    private String activeView;
    @Deprecated
    private final Map<String, SkatTablePanelWrapper> tables;
    private final JSkatGraphicRepository bitmaps = JSkatGraphicRepository.INSTANCE;
    private final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;
    private final JSkatOptions options = JSkatOptions.instance();
    private LobbyPanelFX issLobby;

    public JSkatViewImpl() {
        this.mainPanel = new JPanel();
        this.tables = new HashMap<>();

        mainPanel.setLayout(new BorderLayout());
        addTabbedPane();
        createToolbar();

        JSkatEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void hideToolbarOn(final HideToolbarCommand command) {

        SwingUtilities.invokeLater(() -> {
            mainPanel.remove(toolbar);
            mainPanel.validate();
        });
    }

    @Subscribe
    public void showToolbarOn(final ShowToolbarCommand command) {

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

            if (e.getSource() instanceof final JTabbedPane changedTabs) {

                final Component tab = changedTabs.getSelectedComponent();

                if (tab instanceof final AbstractTabPanel panel) {

                    final String tableName = panel.getName();
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
        Map<JSkatAction, AbstractJSkatAction> actions = JSkatActions.INSTANCE.getActionMap();
        toolbar.add(new ToolbarButton(actions.get(JSkatAction.CREATE_LOCAL_TABLE)));
        toolbar.add(new ToolbarButton(actions.get(JSkatAction.START_LOCAL_SERIES)));
        toolbar.add(new ToolbarButton(actions.get(JSkatAction.SHOW_ISS_LOGIN)));
        toolbar.add(new ToolbarButton(actions.get(JSkatAction.REPLAY_GAME)));
        toolbar.add(new ToolbarButton(actions.get(JSkatAction.NEXT_REPLAY_STEP)));
        toolbar.add(new ToolbarButton(actions.get(JSkatAction.HELP)));
    }

    @Subscribe
    public void createSkatTablePanelOn(final TableCreatedEvent event) {

        if (JSkatViewType.TRAINING_TABLE.equals(event.tableType())) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            final String tableName = event.tableName();
            String tabTitle = null;

            Map<JSkatAction, AbstractJSkatAction> actions = JSkatActions.INSTANCE.getActionMap();
            SkatTablePanelWrapper panel = null;
            if (JSkatViewType.LOCAL_TABLE.equals(event.tableType())) {
                panel = new SkatTablePanelWrapper(tableName, actions);
                tabTitle = tableName;
            } else if (JSkatViewType.ISS_TABLE.equals(event.tableType())) {
                panel = new ISSTablePanelWrapper(tableName, actions);
                tabTitle = strings.getString("iss_table") + ": " + tableName;
            }

            tables.put(tableName, panel);
            addTabPanel(panel, tabTitle);
            actions.get(JSkatAction.START_LOCAL_SERIES).setEnabled(true);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showMessage(final String title, final String message) {

        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE));
    }

    @Override
    public void showAIPlayedSchwarzMessageDiscarding(final String playerName, final CardList discardedCards) {

        String cardString = "";

        if (discardedCards != null) {
            for (final Card card : discardedCards) {
                cardString += " " + strings.getCardStringForCardFace(card);
            }
        } else {
            cardString = strings.getString("unknown_cards");
        }

        showMessage(strings.getString("player_played_schwarz_title"),
                strings.getString("player_played_schwarz_discarding", playerName, cardString));
    }

    @Override
    public void showAIPlayedSchwarzMessageCardPlay(final String playerName, final Card card) {

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
    public void showErrorMessage(final String title, final String message) {

        SwingUtilities
                .invokeLater(() -> JOptionPane.showMessageDialog(mainPanel, message, title, JOptionPane.ERROR_MESSAGE));
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    public void setGameStateOn(final SkatGameStateChangedEvent event) {

        if (activeView.equals(event.tableName)) {
            setActions(event.gameState);
        }
    }

    private void setActions(final GameState state) {
        Map<JSkatAction, AbstractJSkatAction> actions = JSkatActions.INSTANCE.getActionMap();
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
                // FIXME jan 23.02.2013: use a different context window when an
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
    public void showHelpDialogOn(final ShowHelpCommand command) {

        Platform.runLater(() -> new JSkatHelpDialog(strings.getString("help"),
                "org/jskat/gui/help/" + JSkatOptions.instance().getI18NCode() + "/contents.html").showAndWait());
    }

    @Subscribe
    public void showLicenceDialogOn(final ShowLicenseCommand command) {
        Platform.runLater(
                () -> new JSkatHelpDialog(strings.getString("license"), "org/jskat/gui/help/apache2.html")
                        .showAndWait());
    }

    @Subscribe
    public void showSkatSeriesStartDialogOn(final StartSkatSeriesCommand command) {
        // Get the top-level window (the JFrame) that contains our main panel.
        final Window mainFrame = SwingUtilities.getWindowAncestor(mainPanel);

        // Disable the main Swing window on the Event Dispatch Thread (EDT).
        // This makes it unresponsive, effectively making our dialog modal.
        // TODO: remove and make the dialog really modal once everything is migrated to JavaFX
        if (mainFrame != null) {
            mainFrame.setEnabled(false);
        }

        // Schedule the JavaFX dialog to be shown on the JavaFX Application Thread.
        Platform.runLater(() -> {
            try {
                // Create the dialog, passing null for the owner as we are in a Swing context.
                final SkatSeriesStartDialog dialog = new SkatSeriesStartDialog(null);
                // This method internally calls showAndWait(), which blocks this (FX) thread.
                dialog.showAndWaitAndStartSeries();
            } finally {
                // This 'finally' block ensures the main window is ALWAYS re-enabled,
                // even if an error occurs in the dialog.
                if (mainFrame != null) {
                    // Re-enable the main frame back on the EDT.
                    SwingUtilities.invokeLater(() -> mainFrame.setEnabled(true));
                }
            }
        });
    }

    @Subscribe
    public void showISSLoginOn(final IssShowLoginCommand command) {
        SwingUtilities.invokeLater(() -> {
            Map<JSkatAction, AbstractJSkatAction> actions = JSkatActions.INSTANCE.getActionMap();
            final LoginPanelFX loginPanel = new LoginPanelFX(strings.getString("iss_login"), actions);
            addTabPanel(loginPanel, strings.getString("iss_login"));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    public void updateISSLobbyPlayerListOn(final IssPlayerDataUpdatedEvent event) {
        if (issLobby != null) {
            issLobby.updatePlayer(event.playerName(), event.language(), event.gamesPlayed(), event.strength());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    public void removePlayerFromISSLobbyPlayerListOn(final String playerName) {
        if (issLobby != null) {
            SwingUtilities.invokeLater(() -> issLobby.removePlayer(playerName));
        }
    }

    @Subscribe
    public void showIssLobbyOn(final IssConnectedEvent event) {

        SwingUtilities.invokeLater(() -> {
            // show ISS lobby if connection was successfull
            // FIXME (jan 07.12.2010) use constant instead of title
            closeTabPanel(strings.getString("iss_login"));
            Map<JSkatAction, AbstractJSkatAction> actions = JSkatActions.INSTANCE.getActionMap();
            issLobby = new LobbyPanelFX(strings.getString("iss_lobby"), actions);
            final JPanel panel = new JPanel(new BorderLayout());
            panel.add(issLobby, BorderLayout.CENTER);
            addTabPanel(panel, strings.getString("iss_lobby"));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    public void updateISSLobbyTableListOn(final IssTableDataUpdatedEvent event) {
        if (issLobby != null) {
            issLobby.updateTable(event.tableName(), event.maxPlayers(), event.gamesPlayed(), event.player1(), event.player2(), event.player3());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe
    public void removeTableFromISSLobbyTableListOn(final IssTableDeletedEvent event) {
        if (issLobby != null) {
            issLobby.removeTable(event.tableName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNewTableName(final int localTablesCreated) {
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
    public void updateISSMove(final String tableName, final SkatGameData gameData, final MoveInformation moveInformation) {

        final Player movePlayer = moveInformation.getPlayer();

        switch (moveInformation.getType()) {
            // TODO add other types too
            case DEAL:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.DEALING));
                final SkatTablePanel table = tables.get(tableName).getSkatTablePanel();
                table.hideCards(Player.FOREHAND);
                table.hideCards(Player.MIDDLEHAND);
                table.hideCards(Player.REARHAND);

                final Map<Player, CardList> dealtCards = new HashMap<>();
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
                break;
            case HOLD_BID:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.BIDDING));
                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)
                        .post(new HoldBidEvent(movePlayer, gameData.getMaxBidValue()));
                break;
            case PASS:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.BIDDING));
                JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new PassBidEvent(movePlayer, gameData.getNextBidValue()));
                break;
            case SKAT_REQUEST:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.PICKING_UP_SKAT));
                break;
            case PICK_UP_SKAT:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.DISCARDING));
                if (moveInformation.getSkat().size() == 2) {
                    JSkatEventBus.INSTANCE.post(new SkatCardsPickedUpEvent(tableName, moveInformation.getSkat()));
                }
                break;
            case GAME_ANNOUNCEMENT:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.DECLARING));
                JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, new GameAnnouncementEvent(movePlayer, moveInformation.getGameAnnouncement())));
                if (!moveInformation.getGameAnnouncement().contract().hand()) {
                    JSkatEventBus.INSTANCE.post(new SkatCardsChangedEvent(tableName, moveInformation.getGameAnnouncement().discardedCards()));
                }
                if (moveInformation.getGameAnnouncement().contract().ouvert()) {
                    showCardsForPlayer(tableName, movePlayer, moveInformation.getGameAnnouncement().contract().ouvertCards());
                }
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.TRICK_PLAYING));
                break;
            case CARD_PLAY:
                JSkatEventBus.INSTANCE.post(new SkatGameStateChangedEvent(tableName, GameState.TRICK_PLAYING));

                if (gameData.getTricks().size() > 1) {

                    final Trick currentTrick = gameData.getCurrentTrick();
                    final Trick lastTrick = gameData.getLastCompletedTrick();

                    // first card in new trick
                    if (currentTrick.getFirstCard() != null && currentTrick.getSecondCard() == null
                            && currentTrick.getThirdCard() == null) {
                        JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(new TrickCompletedEvent(lastTrick));
                    }
                }

                JSkatEventBus.INSTANCE.post(
                        new TableGameMoveEvent(tableName, new TrickCardPlayedEvent(movePlayer, moveInformation.getCard())));
                break;
            case SHOW_CARDS:
                showCardsForPlayer(tableName, movePlayer, moveInformation.getRevealedCards());
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
            final SkatTablePanel table = tables.get(tableName).getSkatTablePanel();

            table.setPlayerTime(Player.FOREHAND, moveInformation.getPlayerTime(Player.FOREHAND));
            table.setPlayerTime(Player.MIDDLEHAND, moveInformation.getPlayerTime(Player.MIDDLEHAND));
            table.setPlayerTime(Player.REARHAND, moveInformation.getPlayerTime(Player.REARHAND));
        }
    }

    private static void showCardsForPlayer(final String tableName, final Player player, final CardList cards) {
        JSkatEventBus.INSTANCE.post(new ShowCardsCommand(tableName, player, cards));
    }

    @Subscribe
    public void onRequestCreateTable(final RequestCreateTableCommand command) {
        SwingUtilities.invokeLater(() -> JSkatMaster.INSTANCE.createTable());
    }

    @Subscribe
    public void onIssInvitePlayer(final IssInvitePlayerCommand command) {
        final JSkatApplicationData data = JSkatApplicationData.INSTANCE;
        final Set<String> issPlayerNames = new HashSet<>(data.getAvailableISSPlayer());
        issPlayerNames.remove(data.getIssUserName());

        final List<String> player = getPlayerForInvitation(issPlayerNames);
        for (final String currPlayer : player) {
            JSkatMaster.INSTANCE.getIssController().invitePlayer(data.getActiveTable(), currPlayer);
        }
    }

    @Subscribe
    public void closeTableOn(final TableRemovedEvent event) {

        SwingUtilities.invokeLater(() -> closeTabPanel(event.tableName()));
    }

    /**
     * {@inheritDoc}
     */
    private void closeTabPanel(final String tabName) {

        AbstractTabPanel panel = (AbstractTabPanel) tabs.getSelectedComponent();
        if (!tabName.equals(panel.getName())) {
            for (final Component currPanel : tabs.getComponents()) {
                if (tabName.equals(currPanel.getName())) {
                    panel = (AbstractTabPanel) currPanel;
                }
            }
        }

        // remove from table list
        if (panel instanceof SkatTablePanelWrapper || panel instanceof ISSTablePanelWrapper) {
            tables.remove(panel.getName());
        }

        tabs.remove(panel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getPlayerForInvitation(final Set<String> playerNames) {

        final List<String> result = new ArrayList<>();

        if (Platform.isFxApplicationThread()) {
            try {
                final PlayerInvitationDialog dialog = new PlayerInvitationDialog(playerNames);
                dialog.initModality(Modality.APPLICATION_MODAL);
                final Optional<List<String>> dialogResult = dialog.showAndWait();
                dialogResult.ifPresent(result::addAll);
            } catch (Throwable e) {
                LOG.error("Error showing invitation dialog", e);
            }
        } else {
            final FutureTask<List<String>> task = new FutureTask<>(() -> {
                final PlayerInvitationDialog dialog = new PlayerInvitationDialog(playerNames);
                dialog.initModality(Modality.APPLICATION_MODAL);
                final Optional<List<String>> dialogResult = dialog.showAndWait();
                return dialogResult.orElse(Collections.emptyList());
            });
            Platform.runLater(task);
            try {
                result.addAll(task.get());
            } catch (Exception e) {
                LOG.error("Error showing invitation dialog", e);
            }
        }

        LOG.debug("Players to invite: " + result);

        return result;
    }

    private void addTabPanel(final AbstractTabPanel newPanel, final String title) {
        tabs.addTab(title, newPanel);
        tabs.setTabComponentAt(tabs.indexOfComponent(newPanel), new JSkatTabComponent(tabs, bitmaps));
        tabs.setSelectedComponent(newPanel);
        newPanel.setFocus();
    }

    private void addTabPanel(final JPanel newPanel, final String title) {
        tabs.addTab(title, newPanel);
        tabs.setTabComponentAt(tabs.indexOfComponent(newPanel), new JSkatTabComponent(tabs, bitmaps));
        tabs.setSelectedComponent(newPanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showISSTableInvitation(final String invitor, final String tableName) {

        boolean result = false;

        final String question = strings.getString("iss_table_invitation",
                invitor, tableName);

        final int answer = JOptionPane.showConfirmDialog(null, question,
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
    public void showCardNotAllowedMessage(final Card card) {

        final String title = strings.getString("card_not_allowed_title");

        final String message = strings.getString("card_not_allowed_message",
                card != null ? strings.getSuitStringForCardFace(card.getSuit()) : "--",
                card != null ? strings.getRankStringForCardFace(card.getRank()) : "--");

        showErrorMessage(title, message);
    }

    @Subscribe
    public void closeAllIssTabsOn(final IssDisconnectCommand event) {

        SwingUtilities.invokeLater(() -> {
            for (final Component currPanel : tabs.getComponents()) {
                if (currPanel instanceof LobbyPanelFX || currPanel instanceof ISSTablePanelWrapper) {
                    closeTabPanel(currPanel.getName());
                }
            }
        });
    }

    @Subscribe
    public void showErrorMessageOn(final InvalidNumberOfCardsInDiscardedSkatEvent event) {
        showErrorMessage(strings.getString("invalid_number_of_cards_in_skat_title"),
                strings.getString("invalid_number_of_cards_in_skat_message"));
    }

    @Subscribe
    public void showErrorMessageOn(final NoJacksAllowedInDiscardedSkatEvent event) {

        showErrorMessage(strings.getString("no_jacks_allowed_in_schieberamsch_skat_title"),
                strings.getString("no_jacks_allowed_in_schieberamsch_skat_message"));
    }

    @Subscribe
    public void showErrorMessageOn(final DuplicateTableNameInputEvent event) {

        final String message = strings.getString("duplicate_table_name_message",
                event.tableName);

        showErrorMessage(strings.getString("duplicate_table_name_title"),
                message);
    }

    @Subscribe
    public void showErrorMessageOn(final EmptyTableNameInputEvent event) {

        showErrorMessage(strings.getString("invalid_name_input_null_title"),
                strings.getString("invalid_name_input_null_message"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResign(final String tableName, final Player player) {
        tables.get(tableName).getSkatTablePanel().setResign(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGeschoben(final String tableName, final Player player) {
        tables.get(tableName).getSkatTablePanel().setGeschoben(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDiscardedSkat(final String tableName, final Player player, final CardList skatBefore,
                                 final CardList discardedSkat) {
        tables.get(tableName).getSkatTablePanel().setDiscardedSkat(player, skatBefore, discardedSkat);
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
    public void setSkat(final String tableName, final CardList skat) {
        Platform.runLater(() -> tables.get(tableName).getSkatTablePanel().setSkat(skat));
    }

    private static class PlayerInvitationDialog extends Dialog<List<String>> {
        private final ToggleGroup firstPlayerGroup = new ToggleGroup();
        private final ToggleGroup secondPlayerGroup = new ToggleGroup();

        public PlayerInvitationDialog(Set<String> playerNames) {
            setTitle(JSkatResourceBundle.INSTANCE.getString("invite_players"));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            int row = 0;
            List<String> sortedPlayers = new ArrayList<>(playerNames);
            Collections.sort(sortedPlayers);

            for (String playerName : sortedPlayers) {
                grid.add(new Label(playerName), 0, row);

                RadioButton firstButton = new RadioButton();
                firstButton.setUserData(playerName);
                firstButton.setToggleGroup(firstPlayerGroup);
                grid.add(firstButton, 1, row);

                RadioButton secondButton = new RadioButton();
                secondButton.setUserData(playerName);
                secondButton.setToggleGroup(secondPlayerGroup);
                grid.add(secondButton, 2, row);

                row++;
            }

            getDialogPane().setContent(grid);
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    List<String> result = new ArrayList<>();
                    if (firstPlayerGroup.getSelectedToggle() != null) {
                        result.add((String) firstPlayerGroup.getSelectedToggle().getUserData());
                    }
                    if (secondPlayerGroup.getSelectedToggle() != null) {
                        result.add((String) secondPlayerGroup.getSelectedToggle().getUserData());
                    }
                    return result;
                }
                return null;
            });
        }
    }
}
