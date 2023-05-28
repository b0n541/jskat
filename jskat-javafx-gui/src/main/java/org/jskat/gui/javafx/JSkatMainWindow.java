package org.jskat.gui.javafx;

import com.google.common.eventbus.Subscribe;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jskat.JSkatFX;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.general.ShowWelcomeInformationCommand;
import org.jskat.control.event.general.NewJSkatVersionAvailableEvent;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.util.JSkatResourceBundle;

import java.io.IOException;

/**
 * Main window for JSkat.
 */
@Deprecated
public class JSkatMainWindow extends Stage {

    private static final JSkatResourceBundle STRINGS = JSkatResourceBundle.INSTANCE;

    public JSkatMainWindow(final String version, final MenuBar menu, final JSkatViewImpl jskatView, final Screen targetScreen,
                           final Point2D screenPosition) {

        super(StageStyle.DECORATED);

        JSkatEventBus.INSTANCE.register(this);

        setTitle("JSkat " + version);

        getIcons().add(JSkatGraphicRepository.INSTANCE.getJSkatLogoImageFX());

        final SwingNode swingNode = new SwingNode();
        swingNode.setContent(jskatView.mainPanel);

        final VBox pane = new VBox();
        pane.getChildren().addAll(menu, swingNode);
        VBox.setVgrow(swingNode, Priority.ALWAYS);

        final Dimension2D dimension = getMainWindowDimension(targetScreen);
        final Scene scene = new Scene(pane, dimension.getWidth(), dimension.getHeight());
        scene.widthProperty().addListener(
                (observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameWidth(newValue.intValue()));
        scene.heightProperty().addListener(
                (observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameHeight(newValue.intValue()));

        setScene(scene);

        xProperty().addListener((observable, oldValue, newValue) ->
                JSkatOptions.instance().setMainFrameXPosition(newValue.intValue()));
        yProperty().addListener((observable, oldValue, newValue) ->
                JSkatOptions.instance().setMainFrameYPosition(newValue.intValue()));

        setOnCloseRequest(event -> JSkatMaster.INSTANCE.exitJSkat());

        placeMainWindow(targetScreen, this, screenPosition);
    }

    private static Dimension2D getMainWindowDimension(final Screen targetScreen) {
        final double width = JSkatOptions.instance().getMainFrameSize().getWidth();
        final double height = JSkatOptions.instance().getMainFrameSize().getHeight();

        // on first startup the default values for width and height are
        // Integer.MIN_VALUEjskatviewIm
        return new Dimension2D(width > 0 ? width : targetScreen.getBounds().getWidth() * 2 / 3,
                height > 0 ? height : targetScreen.getBounds().getHeight() * 2 / 3);
    }

    private static void placeMainWindow(final Screen screen, final Stage mainWindow, final Point2D position) {

        if (screen.getVisualBounds().contains(position)) {
            mainWindow.setX(position.getX());
            mainWindow.setY(position.getY());
        } else {
            mainWindow.centerOnScreen();
        }
    }

    @Subscribe
    public void showInfoMessageOn(final NewJSkatVersionAvailableEvent event) {
        final Alert alert = new Alert(AlertType.INFORMATION);
        alert.initOwner(this);
        alert.setTitle(STRINGS.getString("new_version_title"));
        alert.setHeaderText(STRINGS.getString("new_version_header", event.newVersion));
        alert.setContentText(STRINGS.getString("new_version_message", event.newVersion));

        // this is a workaround for a bug under Linux that cuts long texts
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
                .forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));

        alert.showAndWait();
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
        stage.initOwner(this);
        stage.show();
    }
}
