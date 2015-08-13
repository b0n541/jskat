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

import javax.swing.SwingUtilities;

import org.apache.log4j.PropertyConfigurator;
import org.jskat.control.JSkatMaster;
import org.jskat.data.DesktopSavePathResolver;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.gui.swing.LookAndFeelSetter;
import org.jskat.util.JSkatResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

		VBox pane = new VBox();
		pane.getChildren().addAll(getMenu(), swingNode);
		VBox.setVgrow(swingNode, Priority.ALWAYS);

		Scene scene = new Scene(pane, JSkatOptions.instance().getMainFrameSize().getWidth(),
				JSkatOptions.instance().getMainFrameSize().getHeight());
		scene.setFill(Color.rgb(96, 65, 34));
		scene.widthProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				System.out.println("Width: " + newValue);
				JSkatOptions.instance().setMainFrameWidth(newValue.intValue());
			}
		});
		scene.heightProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				System.out.println("Height: " + newValue);
				JSkatOptions.instance().setMainFrameHeight(newValue.intValue());
			}
		});

		primaryStage.setScene(scene);

		primaryStage.setX(JSkatOptions.instance().getMainFramePosition().getX());
		primaryStage.setY(JSkatOptions.instance().getMainFramePosition().getY());

		primaryStage.show();
	}

	private static MenuBar getMenu() {

		JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;

		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu(strings.getString("file"));

		MenuItem exitJSkatMenuItem = new MenuItem(strings.getString("exit_jskat"));
		exitJSkatMenuItem.setGraphic(new ImageView(new Image("org/jskat/gui/img/gui/exit_small.png")));
		exitJSkatMenuItem.setOnAction(actionEvent -> System.exit(0));
		fileMenu.getItems().addAll(new MenuItem(strings.getString("load_series")), new SeparatorMenuItem(),
				exitJSkatMenuItem);

		Menu menuSkatTable = new Menu(strings.getString("skat_table"));

		Menu menuNeuralNetworks = new Menu(strings.getString("neural_networks"));

		Menu menuIss = new Menu(strings.getString("iss"));

		Menu menuExtras = new Menu(strings.getString("extras"));

		Menu menuHelp = new Menu(strings.getString("help"));

		menuBar.getMenus().addAll(fileMenu, menuSkatTable, menuNeuralNetworks, menuIss, menuExtras, menuHelp);

		return menuBar;
	}
}
