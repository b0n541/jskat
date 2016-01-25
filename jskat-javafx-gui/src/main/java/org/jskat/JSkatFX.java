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
package org.jskat;

import java.awt.Point;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.apache.log4j.PropertyConfigurator;
import org.jskat.control.JSkatMaster;
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
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class JSkatFX extends Application {

	private static final String VERSION = "0.16.0";

	private static final int SPLASH_WIDTH = 500;
	private static final int SPLASH_HEIGHT = 300;

	private Pane splashScreenLayout;
	private ProgressBar splashScreenProgressBar;
	private Label splashScreenProgressText;

	public static void main(String[] args) {

		PropertyConfigurator.configure(ClassLoader.getSystemResource("org/jskat/config/log4j.properties")); //$NON-NLS-1$
		JSkatOptions.instance(new DesktopSavePathResolver());

		launch(args);
	}

	@Override
	public void init() {
		ImageView splashScreenImage = new ImageView(
				new Image(JSkatFX.class.getResource("/org/jskat/gui/img/gui/splash.png").toExternalForm()));
		splashScreenProgressBar = new ProgressBar();
		splashScreenProgressBar.setPrefWidth(SPLASH_WIDTH);
		splashScreenProgressText = new Label("Loading JSkat...");
		splashScreenProgressText.setAlignment(Pos.CENTER);
		splashScreenLayout = new VBox();
		splashScreenLayout.getChildren().addAll(splashScreenImage, splashScreenProgressBar, splashScreenProgressText);
		splashScreenLayout.setStyle(
				"-fx-padding: 5; " + "-fx-background-color: cornsilk; " + "-fx-border-width:2; " + "-fx-border-color: "
						+ "linear-gradient(" + "to bottom, " + "chocolate, " + "derive(chocolate, 50%)" + ");");
		splashScreenLayout.setEffect(new DropShadow());
	}

	@Override
	public void start(Stage primaryStage) {

		Point2D savedScreenPosition = getSavedScreenPosition();
		Screen targetScreen = getTargetScreen(savedScreenPosition);

		Task<InitializedGuiElements> startupTasks = new Task<InitializedGuiElements>() {
			@Override
			protected InitializedGuiElements call() throws Exception {

				updateMessage(JSkatResourceBundle.INSTANCE.getString("splash_init_application"));

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						LookAndFeelSetter.setLookAndFeel(targetScreen);
					}
				});

				updateProgress(1, 3);
				updateMessage(JSkatResourceBundle.INSTANCE.getString("splash_load_card_sets"));

				JSkatGraphicRepository.INSTANCE.toString();

				updateProgress(2, 3);
				updateMessage(JSkatResourceBundle.INSTANCE.getString("splash_look_for_ai_players"));

				MenuBar menu = JSkatMenuFactory.build();
				JSkatViewImpl jskatView = new JSkatViewImpl(targetScreen, menu, VERSION);
				JSkatMaster.INSTANCE.setView(jskatView);

				return new InitializedGuiElements(menu, jskatView);
			}
		};

		showSplashScreen(targetScreen, primaryStage, startupTasks,
				() -> showJSkatMainWindow(targetScreen, savedScreenPosition, startupTasks.valueProperty().get().menu,
						startupTasks.valueProperty().get().jskatView));

		new Thread(startupTasks).start();
	}

	private static void showJSkatMainWindow(Screen targetScreen, Point2D screenPosition, MenuBar menu,
			JSkatViewImpl jskatView) {

		JSkatMainWindow jskatMainWindow = new JSkatMainWindow(VERSION, menu, jskatView, targetScreen, screenPosition);

		jskatMainWindow.show();

		if (JSkatOptions.instance().getBoolean(Option.SHOW_TIPS_AT_START_UP)) {
			// JSkatEventBus.INSTANCE.post(new ShowWelcomeInformationCommand());
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(
						JSkatFX.class.getResource("gui/javafx/dialog/firststeps/view/FirstStepsDialog.fxml"));
				loader.setResources(JSkatResourceBundle.INSTANCE.getStringResources());
				VBox rootLayout;
				rootLayout = (VBox) loader.load();
				Stage stage = new Stage();
				stage.setTitle(JSkatResourceBundle.INSTANCE.getString("show_tips"));
				Scene scene = new Scene(rootLayout);
				stage.setScene(scene);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initOwner(jskatMainWindow);
				stage.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (JSkatOptions.instance().getBoolean(Option.CHECK_FOR_NEW_VERSION_AT_START_UP)) {
			JSkatMaster.INSTANCE.checkJSkatVersion(VERSION, VersionChecker.getLatestVersion());
		}
	}

	private void showSplashScreen(Screen targetScreen, final Stage splashStage, Task<?> startupTask,
			InitializationCompleteHandler initializationCompleteHandler) {

		splashScreenProgressText.textProperty().bind(startupTask.messageProperty());
		splashScreenProgressBar.progressProperty().bind(startupTask.progressProperty());

		startupTask.stateProperty().addListener((progressValue, oldState, newState) -> {
			if (Worker.State.SUCCEEDED.equals(newState)) {
				splashScreenProgressBar.progressProperty().unbind();
				splashScreenProgressBar.setProgress(1);
				splashStage.toFront();
				FadeTransition fadeSplashScreen = new FadeTransition(Duration.seconds(1), splashScreenLayout);
				fadeSplashScreen.setFromValue(1.0);
				fadeSplashScreen.setToValue(0.0);
				fadeSplashScreen.setOnFinished(actionEvent -> splashStage.hide());
				fadeSplashScreen.play();

				initializationCompleteHandler.complete();
			}
		});

		Scene splashScene = new Scene(splashScreenLayout);
		splashStage.initStyle(StageStyle.UNDECORATED);
		splashStage.setScene(splashScene);
		final Rectangle2D bounds = targetScreen.getBounds();
		splashStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
		splashStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
		splashStage.show();
	}

	private static Screen getTargetScreen(Point2D savedScreenPosition) {

		for (Screen screen : Screen.getScreens()) {
			if (screen.getVisualBounds().contains(savedScreenPosition)) {
				return screen;
			}
		}

		return Screen.getPrimary();
	}

	private static Point2D getSavedScreenPosition() {

		Point mainFramePosition = JSkatOptions.instance().getMainFramePosition();
		return new Point2D(mainFramePosition.getX(), mainFramePosition.getY());
	}

	private interface InitializationCompleteHandler {
		public void complete();
	}

	private class InitializedGuiElements {
		final MenuBar menu;
		final JSkatViewImpl jskatView;

		public InitializedGuiElements(MenuBar menu, JSkatViewImpl jskatView) {
			this.menu = menu;
			this.jskatView = jskatView;
		}
	}
}