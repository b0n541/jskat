package org.jskat;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.layout.Pane;
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
import org.jskat.gui.javafx.JSkatMainWindow;
import org.jskat.gui.javafx.JSkatMenuFactory;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.gui.swing.LookAndFeelSetter;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.version.VersionChecker;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class JSkatFX extends Application {

    private static final String VERSION = "0.22.0";

    private static final int SPLASH_WIDTH = 500;
    private static final int SPLASH_HEIGHT = 300;

    private Pane splashScreenLayout;
    private ProgressBar splashScreenProgressBar;
    private Label splashScreenProgressText;

    public static void main(final String[] args) {

        JSkatOptions.instance(new DesktopSavePathResolver());

        launch(args);
    }

    @Override
    public void init() {
        final ImageView splashScreenImage = new ImageView(
                new Image(ClassLoader.getSystemResourceAsStream("org/jskat/gui/img/gui/splash.png")));
        splashScreenProgressBar = new ProgressBar();
        splashScreenProgressBar.setPrefWidth(SPLASH_WIDTH);
        splashScreenProgressText = new Label("Loading JSkat...");
        splashScreenProgressText.setAlignment(Pos.CENTER);
        splashScreenLayout = new VBox();
        splashScreenLayout.getChildren().addAll(splashScreenImage, splashScreenProgressBar, splashScreenProgressText);
        splashScreenLayout.getStylesheets().add(getClass().getResource("/org/jskat/gui/javafx/jskat.css").toExternalForm());
        splashScreenLayout.setStyle("-fx-padding: 5; -fx-spacing: 5; -fx-border-width:2;");
        splashScreenLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(final Stage primaryStage) {

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

        final JSkatMainWindow jskatMainWindow = new JSkatMainWindow(VERSION, menu, jskatView, targetScreen,
                screenPosition);

        jskatMainWindow.show();

        try {
            showNewMainWindow(VERSION);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (JSkatOptions.instance().getBoolean(Option.SHOW_TIPS_AT_START_UP)) {
            JSkatEventBus.INSTANCE.post(new ShowWelcomeInformationCommand());
        }

        if (JSkatOptions.instance().getBoolean(Option.CHECK_FOR_NEW_VERSION_AT_START_UP)) {
            JSkatMaster.INSTANCE.checkJSkatVersion(VERSION, VersionChecker.getLatestVersion());
        }
    }

    private static void showNewMainWindow(String version) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(JSkatFX.class.getResource("/org/jskat/gui/javafx/main/JSkatMainWindow.fxml"));
        loader.setResources(JSkatResourceBundle.INSTANCE.getStringResources());
        final VBox rootLayout = loader.load();
        final Stage stage = new Stage();
        stage.setTitle("JSkat " + version);
        final Scene scene = new Scene(rootLayout);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> JSkatMaster.INSTANCE.exitJSkat());
        stage.show();
    }

    private void showSplashScreen(final Screen targetScreen, final Stage splashStage, final Task<?> startupTask,
                                  final InitializationCompleteHandler initializationCompleteHandler) {

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