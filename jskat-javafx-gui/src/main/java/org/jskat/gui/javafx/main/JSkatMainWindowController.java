package org.jskat.gui.javafx.main;

import com.google.common.eventbus.Subscribe;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.general.ShowAboutInformationCommand;
import org.jskat.control.command.general.ShowHelpCommand;
import org.jskat.control.command.general.ShowLicenseCommand;
import org.jskat.control.command.general.ShowPreferencesCommand;
import org.jskat.control.event.table.EmptyTableNameInputEvent;
import org.jskat.control.event.table.TableCreatedEvent;
import org.jskat.data.JSkatViewType;
import org.jskat.gui.swing.JSkatOptionsDialog;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.gui.swing.iss.ISSTablePanel;
import org.jskat.gui.swing.table.SkatTablePanel;
import org.jskat.util.JSkatResourceBundle;

import javax.swing.*;
import java.util.Optional;


public class JSkatMainWindowController {

    private final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;

    @FXML
    private Parent root;
    @FXML
    private TabPane tabs;
    @FXML
    private Button playOnLocalTableButton;
    @FXML
    private Button preferencesButton;
    @FXML
    private MenuItem preferencesMenuItem;
    @FXML
    private Button exitJSkatButton;
    @FXML
    private MenuItem exitJSkatMenuItem;
    @FXML
    private Button helpButton;
    @FXML
    private MenuItem helpMenuItem;
    @FXML
    private MenuItem licenseMenuItem;
    @FXML
    private MenuItem aboutMenuItem;

    private JSkatOptionsDialog preferencesDialog;

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/org/jskat/gui/javafx/jskat.css").toExternalForm());

        JSkatEventBus.INSTANCE.register(this);

        preferencesDialog = new JSkatOptionsDialog(null);
    }

    @FXML
    public void createNewLocalTable() {

        TextInputDialog dialog = new TextInputDialog(strings.getString("local.table") + " " + 1);

        dialog.setTitle(strings.getString("new.table.dialog.title"));
        dialog.setHeaderText(strings.getString("new.table.dialog.message"));
        dialog.setContentText(strings.getString("name"));

        Optional<String> result = dialog.showAndWait();

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
    public void addNewTableTabOn(final TableCreatedEvent event) {

        SwingNode swingNode = new SwingNode();
        String tableName = event.tableName;

        SwingUtilities.invokeLater(() -> {
            SkatTablePanel panel = null;
            if (JSkatViewType.LOCAL_TABLE.equals(event.tableType)) {
                panel = new SkatTablePanel(tableName, JSkatViewImpl.actions);
            } else if (JSkatViewType.ISS_TABLE.equals(event.tableType)) {
                panel = new ISSTablePanel(tableName, JSkatViewImpl.actions);
            }
            swingNode.setContent(panel);
        });

        String tabTitle = null;
        if (JSkatViewType.LOCAL_TABLE.equals(event.tableType)) {
            tabTitle = tableName;
        } else if (JSkatViewType.ISS_TABLE.equals(event.tableType)) {
            tabTitle = strings.getString("iss_table") + ": " + tableName;
        }

        Tab tab = new Tab(tabTitle);
        tab.setClosable(true);
        tab.setContent(swingNode);
        tabs.getTabs().add(tab);
        tabs.getSelectionModel().select(tab);
    }
}
