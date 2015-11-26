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
import org.jskat.control.command.skatseries.CreateSkatSeriesCommand;
import org.jskat.data.DesktopSavePathResolver;
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

		JSkatViewImpl jskatView = new JSkatViewImpl();
		JSkatGraphicRepository.INSTANCE.toString();
		JSkatMaster.INSTANCE.setView(jskatView);

		SwingNode swingNode = new SwingNode();
		swingNode.setContent(jskatView.mainPanel);

		MenuBar menu = getMenu();

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
		skatTableMenu.getItems().addAll(playOnLocalTable, new SeparatorMenuItem(), startSkatSeriesMenuItem,
				new SeparatorMenuItem(), new MenuItem(strings.getString("replay_game")),
				new MenuItem(strings.getString("next_replay_move")));

		MenuItem resetNeuralNetworksMenuItem = new MenuItem(strings.getString("reset_nn"));
		resetNeuralNetworksMenuItem.setOnAction(actionEvent -> JSkatMaster.INSTANCE.resetNeuralNetworks());
		MenuItem trainNeuralNetworksMenuItem = new MenuItem(strings.getString("train_nn"));
		trainNeuralNetworksMenuItem.setOnAction(actionEvent -> JSkatMaster.INSTANCE.trainNeuralNetworks());
		MenuItem stopTrainNeuralNetworksMenuItem = new MenuItem(strings.getString("stop_train_nn"));
		stopTrainNeuralNetworksMenuItem.setOnAction(actionEvent -> JSkatMaster.INSTANCE.stopTrainNeuralNetworks());

		Menu neuralNetworksMenu = new Menu(strings.getString("neural_networks"));
		neuralNetworksMenu.getItems().addAll(new MenuItem(strings.getString("load_nn")),
				new MenuItem(strings.getString("save_nn")), new SeparatorMenuItem(), resetNeuralNetworksMenuItem,
				trainNeuralNetworksMenuItem, stopTrainNeuralNetworksMenuItem);

		Menu issMenu = new Menu(strings.getString("iss"));
		issMenu.getItems().addAll(new MenuItem(strings.getString("play_on_iss")), new SeparatorMenuItem(),
				new MenuItem(strings.getString("new_table")), new MenuItem(strings.getString("invite")));

		Menu extrasMenu = new Menu(strings.getString("extras"));
		extrasMenu.getItems().addAll(new MenuItem(strings.getString("preferences")));

		Menu helpMenu = new Menu(strings.getString("help"));
		helpMenu.getItems().addAll(new MenuItem(strings.getString("help")), new SeparatorMenuItem(),
				new MenuItem(strings.getString("license")), new MenuItem(strings.getString("about")));

		menuBar.getMenus().addAll(fileMenu, skatTableMenu, neuralNetworksMenu, issMenu, extrasMenu, helpMenu);

		return menuBar;
	}
}
