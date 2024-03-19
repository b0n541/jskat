package org.jskat;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.general.ShowWelcomeInformationCommand;
import org.jskat.data.DesktopSavePathResolver;
import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.Option;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.javafx.JSkatMenuFactory;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.gui.swing.LookAndFeelSetter;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.version.VersionChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class JSkatFX extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(JSkatFX.class);
    private static final String VERSION = "0.24.0";

    private static final int SPLASH_WIDTH = 500;
    private static final int SPLASH_HEIGHT = 300;

    public static void main(final String[] args) {

        JSkatOptions.instance(new DesktopSavePathResolver());

        launch(args);
    }

    @Override
    public void init() {
    }

    @Override
    public void start(final Stage primaryStage) {

        // TODO: set JSkat style here globally
        // Application.setUserAgentStylesheet("/org/jskat/gui/javafx/jskat.css");

        final Point2D savedScreenPosition = getSavedScreenPosition();
        final Screen targetScreen = getTargetScreen(savedScreenPosition);

        final Task<InitializedGuiElements> startupTasks = new Task<>() {
            @Override
            protected InitializedGuiElements call() {

                updateMessage(JSkatResourceBundle.INSTANCE.getString("splash_init_application"));

                SwingUtilities.invokeLater(() -> LookAndFeelSetter.setLookAndFeel(targetScreen));

                updateProgress(1, 3);
                updateMessage(JSkatResourceBundle.INSTANCE.getString("splash_load_card_sets"));

                JSkatGraphicRepository.INSTANCE.toString();

                updateProgress(2, 3);
                updateMessage(JSkatResourceBundle.INSTANCE.getString("splash_look_for_ai_players"));

                final MenuBar menu = JSkatMenuFactory.build();
                final JSkatViewImpl jskatView = new JSkatViewImpl(targetScreen, menu, VERSION);
                JSkatMaster.INSTANCE.setView(jskatView);

                return new InitializedGuiElements(menu, jskatView);
            }
        };

        showSplashScreen(targetScreen, primaryStage, startupTasks,
                () -> showJSkatMainWindow(targetScreen, savedScreenPosition, startupTasks.valueProperty().get().menu,
                        startupTasks.valueProperty().get().jskatView));

        new Thread(startupTasks).start();
    }

    private static void showJSkatMainWindow(final Screen targetScreen, final Point2D screenPosition, final MenuBar menu,
                                            final JSkatViewImpl jskatView) {

//        final JSkatMainWindow jskatMainWindow = new JSkatMainWindow(VERSION, menu, jskatView, targetScreen,
//                screenPosition);
//
//        jskatMainWindow.show();

        try {
            showNewMainWindow(targetScreen, screenPosition, VERSION);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        if (JSkatOptions.instance().getBoolean(Option.SHOW_TIPS_AT_START_UP)) {
            JSkatEventBus.INSTANCE.post(new ShowWelcomeInformationCommand());
        }

        if (JSkatOptions.instance().getBoolean(Option.CHECK_FOR_NEW_VERSION_AT_START_UP)) {
            JSkatMaster.checkJSkatVersion(VERSION, VersionChecker.getLatestVersion());
        }
    }

    private static void showNewMainWindow(final Screen targetScreen, final Point2D screenPosition, final String version) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(JSkatFX.class.getResource("/org/jskat/gui/javafx/main/JSkatMainWindow.fxml"));
        loader.setResources(JSkatResourceBundle.INSTANCE.getStringResources());
        final VBox rootLayout = loader.load();

        final Dimension2D dimension = getMainWindowDimension(targetScreen);
        LOG.info("Main window size {} x {}", dimension.getWidth(), dimension.getHeight());
        final Scene scene = new Scene(rootLayout, dimension.getWidth(), dimension.getHeight());

        // TODO: set this globally
        scene.getStylesheets().add("/org/jskat/gui/javafx/jskat.css");

        scene.widthProperty().addListener((observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameWidth(newValue.intValue()));
        scene.heightProperty().addListener((observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameHeight(newValue.intValue()));

        final Stage stage = new Stage();
        stage.setTitle("JSkat " + version);
        stage.setOnCloseRequest(event -> JSkatMaster.INSTANCE.exitJSkat());

        stage.setScene(scene);

        stage.setWidth(dimension.getWidth());
        stage.setHeight(dimension.getHeight());
        stage.xProperty().addListener((observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameXPosition(newValue.intValue()));
        stage.yProperty().addListener((observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameYPosition(newValue.intValue()));

        placeMainWindow(targetScreen, stage, screenPosition);

        stage.show();
    }

    private void showSplashScreen(final Screen targetScreen, final Stage splashStage, final Task<?> startupTask,
                                  final InitializationCompleteHandler initializationCompleteHandler) {

        final ImageView splashScreenImage = new ImageView(
                new Image(ClassLoader.getSystemResourceAsStream("org/jskat/gui/img/gui/splash.png")));
        final ProgressBar splashScreenProgressBar = new ProgressBar();
        splashScreenProgressBar.setPrefWidth(SPLASH_WIDTH);
        final Label splashScreenProgressText = new Label("Loading JSkat...");
        splashScreenProgressText.setAlignment(Pos.CENTER);
        final VBox splashScreenLayout = new VBox();
        splashScreenLayout.getChildren().addAll(splashScreenImage, splashScreenProgressBar, splashScreenProgressText);
        splashScreenLayout.setStyle("-fx-padding: 5; -fx-spacing: 5; -fx-border-width:2;");
        splashScreenLayout.setEffect(new DropShadow());

        splashScreenProgressText.textProperty().bind(startupTask.messageProperty());
        splashScreenProgressBar.progressProperty().bind(startupTask.progressProperty());

        startupTask.stateProperty().addListener((progressValue, oldState, newState) -> {
            if (Worker.State.SUCCEEDED.equals(newState)) {
                splashScreenProgressBar.progressProperty().unbind();
                splashScreenProgressBar.setProgress(1);
                splashStage.toFront();
                final FadeTransition fadeSplashScreen = new FadeTransition(Duration.seconds(1), splashScreenLayout);
                fadeSplashScreen.setFromValue(1.0);
                fadeSplashScreen.setToValue(0.0);
                fadeSplashScreen.setOnFinished(actionEvent -> splashStage.hide());
                fadeSplashScreen.play();

                initializationCompleteHandler.complete();
            }
        });

        final Scene splashScene = new Scene(splashScreenLayout);
        // TODO: set this globally
        splashScene.getStylesheets().add("/org/jskat/gui/javafx/jskat.css");
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setScene(splashScene);
        final Rectangle2D bounds = targetScreen.getBounds();
        splashStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        splashStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);

        splashStage.show();
    }

    private static Screen getTargetScreen(final Point2D savedScreenPosition) {
        for (final Screen screen : Screen.getScreens()) {
            if (screen.getVisualBounds().contains(savedScreenPosition)) {
                return screen;
            }
        }

        return Screen.getPrimary();
    }

    private static Point2D getSavedScreenPosition() {
        final Point mainFramePosition = JSkatOptions.instance().getMainFramePosition();
        return new Point2D(mainFramePosition.getX(), mainFramePosition.getY());
    }

    private static Dimension2D getMainWindowDimension(final Screen targetScreen) {
        final double width = JSkatOptions.instance().getMainFrameSize().getWidth();
        final double height = JSkatOptions.instance().getMainFrameSize().getHeight();

        // on first startup the default values for width and height are 2/3 of screen size
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

    private interface InitializationCompleteHandler {
        void complete();
    }

    private class InitializedGuiElements {
        final MenuBar menu;
        final JSkatViewImpl jskatView;

        InitializedGuiElements(final MenuBar menu, final JSkatViewImpl jskatView) {
            this.menu = menu;
            this.jskatView = jskatView;
        }
    }
}