package org.jskat.gui.javafx.main;

import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jskat.JSkatFX;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.general.*;
import org.jskat.control.command.iss.IssShowLoginCommand;
import org.jskat.control.command.table.NextReplayMoveCommand;
import org.jskat.control.command.table.ReplayGameCommand;
import org.jskat.control.command.table.StartSkatSeriesCommand;
import org.jskat.control.event.general.NewJSkatVersionAvailableEvent;
import org.jskat.control.event.iss.*;
import org.jskat.control.event.table.EmptyTableNameInputEvent;
import org.jskat.control.event.table.TableCreatedEvent;
import org.jskat.control.event.table.TableRemovedEvent;
import org.jskat.data.JSkatApplicationData;
import org.jskat.data.JSkatViewType;
import org.jskat.gui.swing.JSkatOptionsDialog;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.gui.swing.iss.ISSTablePanel;
import org.jskat.gui.swing.iss.LobbyPanel;
import org.jskat.gui.swing.table.SkatTablePanel;
import org.jskat.util.JSkatResourceBundle;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;


public class JSkatMainWindowController {

    private final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;

    @FXML
    private Parent root;
    @FXML
    private TabPane tabs;

    @FXML
    private MenuItem preferencesMenuItem;
    @FXML
    private Button preferencesButton;

    @FXML
    private MenuItem issLoginMenuItem;
    @FXML
    private Button issLoginToolbarButton;
    @FXML
    private Button issLoginButton;
    @FXML
    private MenuItem localTableMenuItem;
    @FXML
    private Button localTableToolbarButton;
    @FXML
    private Button localTableButton;
    @FXML
    private MenuItem startSkatSeriesMenuItem;
    @FXML
    private Button startSkatSeriesToolbarButton;
    @FXML
    private MenuItem replayGameMenuItem;
    @FXML
    private Button replayGameToolbarButton;
    @FXML
    private MenuItem nextReplayMoveMenuItem;
    @FXML
    private Button nextReplayMoveButton;

    @FXML
    private Button helpButton;
    @FXML
    private MenuItem helpMenuItem;
    @FXML
    private MenuItem licenseMenuItem;
    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MenuItem exitJSkatMenuItem;
    @FXML
    private Button exitJSkatButton;

    // TODO remove when Swing has been retired
    private JSkatOptionsDialog preferencesDialog;
    private LobbyPanel issLobby;

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/org/jskat/gui/javafx/jskat.css").toExternalForm());

        JSkatEventBus.INSTANCE.register(this);

        preferencesDialog = new JSkatOptionsDialog(null);
    }

    @FXML
    public void showIssLoginPanel() {
        JSkatEventBus.INSTANCE.post(new IssShowLoginCommand());
    }

    @FXML
    public void showPreferences() {
        SwingUtilities.invokeLater(() -> {
            JSkatEventBus.INSTANCE.post(new ShowPreferencesCommand());
        });
    }

    @FXML
    public void exitJSkat() {
        // FIXME get rid of that god class
        JSkatMaster.INSTANCE.exitJSkat();
    }

    @FXML
    public void createNewLocalTable() {

        final TextInputDialog dialog = new TextInputDialog(strings.getString("local.table") + " " + 1);

        dialog.setTitle(strings.getString("new.table.dialog.title"));
        dialog.setHeaderText(strings.getString("new.table.dialog.message"));
        dialog.setContentText(strings.getString("name"));

        final Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String tableName = result.get();

            if (tableName.isEmpty()) {
                JSkatEventBus.INSTANCE.post(new EmptyTableNameInputEvent());
            } else {
                if (tableName.length() > 100) {
                    tableName = tableName.substring(0, 100);
                }

                JSkatMaster.INSTANCE.createTable(tableName);
            }
        }
    }

    @FXML
    public void startSkatSeries() {
        JSkatEventBus.INSTANCE.post(new StartSkatSeriesCommand(JSkatApplicationData.INSTANCE.getActiveTable()));
    }

    @FXML
    public void replayGame() {
        JSkatEventBus.INSTANCE.post(new ReplayGameCommand(JSkatApplicationData.INSTANCE.getActiveTable()));
    }

    @FXML
    public void nextReplayMove() {
        JSkatEventBus.INSTANCE.post(new NextReplayMoveCommand(JSkatApplicationData.INSTANCE.getActiveTable()));
    }

    @FXML
    public void showHelp() {
        SwingUtilities.invokeLater(() -> {
            JSkatEventBus.INSTANCE.post(new ShowHelpCommand());
        });
    }

    @FXML
    public void showLicense() {
        SwingUtilities.invokeLater(() -> {
            JSkatEventBus.INSTANCE.post(new ShowLicenseCommand());
        });
    }

    @FXML
    public void showAboutInformation() {
        SwingUtilities.invokeLater(() -> {
            JSkatEventBus.INSTANCE.post(new ShowAboutInformationCommand());
        });
    }

    @Subscribe
    public void showWelcomeDialogOn(final ShowWelcomeInformationCommand command) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(
                JSkatFX.class.getResource("/org/jskat/gui/javafx/dialog/firststeps/view/FirstStepsDialog.fxml"));
        loader.setResources(JSkatResourceBundle.INSTANCE.getStringResources());
        final VBox rootLayout = loader.load();
        final Stage stage = new Stage();
        stage.setTitle(JSkatResourceBundle.INSTANCE.getString("show_tips"));
        final Scene scene = new Scene(rootLayout);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(root.getScene().getWindow());
        stage.show();
    }

    @Subscribe
    public void showNewVersionInfoOn(final NewJSkatVersionAvailableEvent event) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(root.getScene().getWindow());
        alert.setTitle(JSkatResourceBundle.INSTANCE.getString("new_version_title"));
        alert.setHeaderText(JSkatResourceBundle.INSTANCE.getString("new_version_header", event.newVersion));
        alert.setContentText(JSkatResourceBundle.INSTANCE.getString("new_version_message", event.newVersion));

        // this is a workaround for a bug under Linux that cuts long texts
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
                .forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));

        alert.showAndWait();
    }

    @Subscribe
    public void showIssLoginOn(final IssShowLoginCommand command) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/org/jskat/gui/javafx/iss/IssLogin.fxml"));
        loader.setResources(JSkatResourceBundle.INSTANCE.getStringResources());
        final VBox loginPanel = loader.load();

        final Tab tab = new Tab(strings.getString("iss_login"));
        tab.setId(JSkatMainWindowTabType.ISS_LOGIN.name());
        tab.setClosable(true);
        tab.setContent(loginPanel);

        tabs.getTabs().add(tab);
        tabs.getSelectionModel().select(tab);
    }

    @Subscribe
    public void addNewTableTabOn(final TableCreatedEvent event) {

        final SwingNode swingNode = new SwingNode();
        final String tableName = event.tableName();

        try {
            SwingUtilities.invokeAndWait(() -> {
                SkatTablePanel panel = null;
                if (JSkatViewType.LOCAL_TABLE.equals(event.tableType())) {
                    panel = new SkatTablePanel(tableName, JSkatViewImpl.actions);
                } else if (JSkatViewType.ISS_TABLE.equals(event.tableType())) {
                    panel = new ISSTablePanel(tableName, JSkatViewImpl.actions);
                }
                swingNode.setContent(panel);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        String tabTitle = null;
        String tabId = null;
        if (JSkatViewType.LOCAL_TABLE.equals(event.tableType())) {
            tabTitle = tableName;
            tabId = JSkatMainWindowTabType.LOCAL_TABLE.name() + ":" + tableName;
        } else if (JSkatViewType.ISS_TABLE.equals(event.tableType())) {
            tabTitle = strings.getString("iss_table") + ": " + tableName;
            tabId = JSkatMainWindowTabType.ISS_TABLE.name() + ":" + tableName;
        }

        final Tab tab = new Tab(tabTitle);
        tab.setId(tabId);
        tab.setClosable(true);
        tab.setContent(swingNode);

        Platform.runLater(() -> {
            tabs.getTabs().add(tab);
            tabs.getSelectionModel().select(tab);
        });
    }

    @Subscribe
    public void showIssLobbyOn(final IssConnectedEvent event) {
        tabs.getTabs().stream()
                .filter(tab -> JSkatMainWindowTabType.ISS_LOGIN.name().equals(tab.getId()))
                .findFirst()
                .ifPresent(tab -> Platform.runLater(() -> tabs.getTabs().remove(tab)));

        final SwingNode swingNode = new SwingNode();
        try {
            SwingUtilities.invokeAndWait(() -> {
                issLobby = new LobbyPanel("ISS Lobby", JSkatViewImpl.actions);
                swingNode.setContent(issLobby);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        final Tab tab = new Tab("ISS Lobby");
        tab.setId(JSkatMainWindowTabType.ISS_LOBBY.name());
        tab.setClosable(true);
        tab.setContent(swingNode);

        Platform.runLater(() -> {
            tabs.getTabs().add(tab);
            tabs.getSelectionModel().select(tab);
        });
    }

    @Subscribe
    public void removeTableOn(TableRemovedEvent event) {
        tabs.getTabs().stream()
                .filter(tab -> tab.getId().contains(event.tableName()))
                .forEach(tab -> Platform.runLater(() -> tabs.getTabs().remove(tab)));
    }

    @Subscribe
    public void closeAllIssTabsOn(final IssDisconnectedEvent event) {
        tabs.getTabs().stream()
                .filter(tab -> Set.of(
                                JSkatMainWindowTabType.ISS_LOGIN.name(),
                                JSkatMainWindowTabType.ISS_LOBBY.name(),
                                JSkatMainWindowTabType.ISS_TABLE.name())
                        .contains(tab.getId()))
                .forEach(tab -> Platform.runLater(() -> tabs.getTabs().remove(tab)));
    }

    @Subscribe
    public void updateIssTableListOn(final IssTableDataUpdatedEvent event) {
        issLobby.updateTable(event.tableName(), event.maxPlayers(), event.gamesPlayed(), event.player1(), event.player2(), event.player3());
    }

    @Subscribe
    public void deleteTableFromIssTableListOn(final IssTableDeletedEvent event) {
        issLobby.removeTable(event.tableName());
    }

    @Subscribe
    public void updateIssPlayerListOn(final IssPlayerDataUpdatedEvent event) {
        issLobby.updatePlayer(event.playerName(), event.language(), event.gamesPlayed(), event.strength());
    }

    @Subscribe
    public void deletePlayerFromIssPlayerListOn(final IssPlayerLeftEvent event) {
        issLobby.removeTable(event.playerName());
    }
}
