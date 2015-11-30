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

import javax.swing.SwingUtilities;

import org.apache.log4j.PropertyConfigurator;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.general.ShowAboutInformationCommand;
import org.jskat.control.command.general.ShowHelpCommand;
import org.jskat.control.command.general.ShowLicenseCommand;
import org.jskat.control.command.general.ShowPreferencesCommand;
import org.jskat.control.command.skatseries.CreateSkatSeriesCommand;
import org.jskat.control.command.skatseries.ReplayGameCommand;
import org.jskat.control.command.table.NextReplayMoveCommand;
import org.jskat.data.DesktopSavePathResolver;
import org.jskat.data.JSkatApplicationData;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Icon;
import org.jskat.gui.img.JSkatGraphicRepository.IconSize;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.gui.swing.LookAndFeelSetter;
import org.jskat.util.JSkatResourceBundle;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class JSkatFX extends Application {

	public static void main(String[] args) {

		PropertyConfigurator.configure(ClassLoader.getSystemResource("org/jskat/config/log4j.properties")); //$NON-NLS-1$
		JSkatOptions.instance(new DesktopSavePathResolver());

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("JSkat " + JSkat.getVersion());

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				LookAndFeelSetter.setLookAndFeel();
			}
		});

		JSkatGraphicRepository.INSTANCE.toString();

		MenuBar menu = getMenu();

		JSkatViewImpl jskatView = new JSkatViewImpl(menu);
		JSkatMaster.INSTANCE.setView(jskatView);

		SwingNode swingNode = new SwingNode();
		swingNode.setContent(jskatView.mainPanel);


		VBox pane = new VBox();
		pane.getChildren().addAll(menu, swingNode);
		VBox.setVgrow(swingNode, Priority.ALWAYS);

		Scene scene = new Scene(pane, JSkatOptions.instance().getMainFrameSize().getWidth(),
				JSkatOptions.instance().getMainFrameSize().getHeight());
		scene.widthProperty().addListener(
				(observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameWidth(newValue.intValue()));
		scene.heightProperty().addListener(
				(observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameHeight(newValue.intValue()));

		primaryStage.setScene(scene);

		primaryStage.xProperty().addListener(
				(observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameXPosition(newValue.intValue()));
		primaryStage.yProperty().addListener(
				(observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameYPosition(newValue.intValue()));

		placeMainWindow(primaryStage);

		primaryStage.show();
	}

	private void placeMainWindow(Stage primaryStage) {

		Point mainFramePosition = JSkatOptions.instance().getMainFramePosition();
		Point2D javaFxPosition = new Point2D(mainFramePosition.getX(), mainFramePosition.getY());

		boolean screenFound = false;

		for (Screen screen : Screen.getScreens()) {
			if (screen.getVisualBounds().contains(javaFxPosition)) {
				placeMainWindow(screen, primaryStage, javaFxPosition);
				screenFound = true;
			}
		}

		if (!screenFound) {
			placeMainWindow(Screen.getPrimary(), primaryStage, javaFxPosition);
		}
	}

	private static void placeMainWindow(Screen screen, Stage primaryStage, Point2D position) {

		if (screen.getVisualBounds().contains(position)) {
			primaryStage.setX(position.getX());
			primaryStage.setY(position.getY());
		} else {
			primaryStage.centerOnScreen();
		}
	}

	private static MenuBar getMenu() {

		JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;

		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu(strings.getString("file"));

		MenuItem loadSeriesMenuItem = new MenuItem(strings.getString("load_series"));
		loadSeriesMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE.getImageView(Icon.LOAD, IconSize.SMALL));
		MenuItem saveSeriesMenuItem = new MenuItem(strings.getString("save_series"));
		saveSeriesMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE.getImageView(Icon.SAVE, IconSize.SMALL));
		MenuItem saveSeriesAsMenuItem = new MenuItem(strings.getString("save_series_as"));
		saveSeriesAsMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE.getImageView(Icon.SAVE_AS, IconSize.SMALL));
		MenuItem exitJSkatMenuItem = new MenuItem(strings.getString("exit_jskat"));
		exitJSkatMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE.getImageView(Icon.EXIT, IconSize.SMALL));
		exitJSkatMenuItem.setOnAction(actionEvent -> JSkatMaster.INSTANCE.exitJSkat());

		fileMenu.getItems().addAll(loadSeriesMenuItem, saveSeriesMenuItem, saveSeriesAsMenuItem,
				new SeparatorMenuItem(), exitJSkatMenuItem);

		Menu skatTableMenu = new Menu(strings.getString("skat_table"));

		MenuItem playOnLocalTable = new MenuItem(strings.getString("play_on_local_table")); //$NON-NLS-1$
		playOnLocalTable.setGraphic(JSkatGraphicRepository.INSTANCE.getImageView(Icon.TABLE, IconSize.SMALL));
		playOnLocalTable.setOnAction(actionEvent -> JSkatMaster.INSTANCE.createTable());
		MenuItem startSkatSeriesMenuItem = new MenuItem(strings.getString("start_series")); //$NON-NLS-1$
		startSkatSeriesMenuItem.setOnAction(actionEvent -> JSkatEventBus.INSTANCE.post(new CreateSkatSeriesCommand()));
		startSkatSeriesMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.PLAY, IconSize.SMALL));
		MenuItem replayGameMenuItem = new MenuItem(
				strings.getString("replay_game"));
		replayGameMenuItem
				.setOnAction(
						actionEvent -> JSkatEventBus.TABLE_EVENT_BUSSES
								.get(JSkatApplicationData.INSTANCE
										.getActiveTable())
								.post(new ReplayGameCommand()));
		replayGameMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.FIRST, IconSize.SMALL));
		MenuItem nextReplayMoveMenuItem = new MenuItem(
				strings.getString("next_replay_move"));
		nextReplayMoveMenuItem
				.setOnAction(
						actionEvent -> JSkatEventBus.TABLE_EVENT_BUSSES
								.get(JSkatApplicationData.INSTANCE
										.getActiveTable())
								.post(new NextReplayMoveCommand()));
		nextReplayMoveMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.NEXT, IconSize.SMALL));

		skatTableMenu.getItems().addAll(playOnLocalTable,
				new SeparatorMenuItem(), startSkatSeriesMenuItem,
				new SeparatorMenuItem(), replayGameMenuItem,
				nextReplayMoveMenuItem);

		Menu neuralNetworksMenu = new Menu(
				strings.getString("neural_networks"));

		MenuItem loadNeuralNetworksMenuItem = new MenuItem(
				strings.getString("load_nn"));
		loadNeuralNetworksMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.LOAD, IconSize.SMALL));
		MenuItem saveNeuralNetworksMenuItem = new MenuItem(
				strings.getString("save_nn"));
		saveNeuralNetworksMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.SAVE, IconSize.SMALL));
		MenuItem resetNeuralNetworksMenuItem = new MenuItem(strings.getString("reset_nn"));
		resetNeuralNetworksMenuItem.setOnAction(actionEvent -> JSkatMaster.INSTANCE.resetNeuralNetworks());
		resetNeuralNetworksMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.UNDO, IconSize.SMALL));
		MenuItem trainNeuralNetworksMenuItem = new MenuItem(strings.getString("train_nn"));
		trainNeuralNetworksMenuItem.setOnAction(actionEvent -> JSkatMaster.INSTANCE.trainNeuralNetworks());
		trainNeuralNetworksMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.TRAIN_NN, IconSize.SMALL));
		MenuItem stopTrainNeuralNetworksMenuItem = new MenuItem(strings.getString("stop_train_nn"));
		stopTrainNeuralNetworksMenuItem.setOnAction(actionEvent -> JSkatMaster.INSTANCE.stopTrainNeuralNetworks());
		stopTrainNeuralNetworksMenuItem
				.setGraphic(JSkatGraphicRepository.INSTANCE
						.getImageView(Icon.PAUSE, IconSize.SMALL));

		neuralNetworksMenu.getItems().addAll(loadNeuralNetworksMenuItem,
				saveNeuralNetworksMenuItem, new SeparatorMenuItem(),
				resetNeuralNetworksMenuItem, trainNeuralNetworksMenuItem,
				stopTrainNeuralNetworksMenuItem);

		Menu issMenu = new Menu(strings.getString("iss"));

		MenuItem playOnIssMenuItem = new MenuItem(
				strings.getString("play_on_iss"));
		playOnIssMenuItem.setOnAction(actionEvent -> JSkatMaster.INSTANCE
				.getIssController().showISSLoginPanel());
		playOnIssMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.CONNECT_ISS, IconSize.SMALL));
		MenuItem createNewTableOnIssMenuItem = new MenuItem(
				strings.getString("new_table"));
		createNewTableOnIssMenuItem
				.setOnAction(actionEvent -> JSkatMaster.INSTANCE
						.getIssController().requestTableCreation());
		createNewTableOnIssMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.TABLE, IconSize.SMALL));
		MenuItem invitePlayerMenuItem = new MenuItem(
				strings.getString("invite"));
		invitePlayerMenuItem.setOnAction(
				actionEvent -> JSkatMaster.INSTANCE.invitePlayer());
		invitePlayerMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.INVITE, IconSize.SMALL));

		issMenu.getItems().addAll(playOnIssMenuItem, new SeparatorMenuItem(),
				createNewTableOnIssMenuItem, invitePlayerMenuItem);

		Menu extrasMenu = new Menu(strings.getString("extras"));

		MenuItem preferencesMenuItem = new MenuItem(
				strings.getString("preferences"));
		preferencesMenuItem.setOnAction(actionEvent -> JSkatEventBus.INSTANCE
				.post(new ShowPreferencesCommand()));
		preferencesMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.PREFERENCES, IconSize.SMALL));

		extrasMenu.getItems().addAll(preferencesMenuItem);

		Menu helpMenu = new Menu(strings.getString("help"));

		MenuItem helpMenuItem = new MenuItem(strings.getString("help"));
		helpMenuItem.setOnAction(actionEvent -> JSkatEventBus.INSTANCE
				.post(new ShowHelpCommand()));
		helpMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.HELP, IconSize.SMALL));
		MenuItem licenseMenuItem = new MenuItem(strings.getString("license"));
		licenseMenuItem.setOnAction(actionEvent -> JSkatEventBus.INSTANCE
				.post(new ShowLicenseCommand()));
		licenseMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.LICENSE, IconSize.SMALL));
		MenuItem aboutMenuItem = new MenuItem(strings.getString("about"));
		aboutMenuItem.setOnAction(actionEvent -> JSkatEventBus.INSTANCE
				.post(new ShowAboutInformationCommand()));
		aboutMenuItem.setGraphic(JSkatGraphicRepository.INSTANCE
				.getImageView(Icon.ABOUT, IconSize.SMALL));

		helpMenu.getItems().addAll(helpMenuItem, new SeparatorMenuItem(),
				licenseMenuItem, aboutMenuItem);

		menuBar.getMenus().addAll(fileMenu, skatTableMenu, neuralNetworksMenu, issMenu, extrasMenu, helpMenu);

		return menuBar;
	}
}
