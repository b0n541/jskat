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
package org.jskat.gui.javafx;

import java.io.IOException;

import org.jskat.JSkatFX;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.general.ShowWelcomeInformationCommand;
import org.jskat.control.event.general.NewJSkatVersionAvailableEvent;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.util.JSkatResourceBundle;

import com.google.common.eventbus.Subscribe;

import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;

/**
 * Main window for JSkat.
 */
public class JSkatMainWindow extends Stage {

	private static JSkatResourceBundle STRINGS = JSkatResourceBundle.INSTANCE;

	public JSkatMainWindow(String version, MenuBar menu, JSkatViewImpl jskatView, Screen targetScreen,
			Point2D screenPosition) {

		super(StageStyle.DECORATED);

		JSkatEventBus.INSTANCE.register(this);

		setTitle("JSkat " + version);

		getIcons().add(JSkatGraphicRepository.INSTANCE.getJSkatLogoImageFX());

		SwingNode swingNode = new SwingNode();
		swingNode.setContent(jskatView.mainPanel);

		VBox pane = new VBox();
		pane.getChildren().addAll(menu, swingNode);
		VBox.setVgrow(swingNode, Priority.ALWAYS);

		Dimension2D dimension = getMainWindowDimension(targetScreen);
		Scene scene = new Scene(pane, dimension.getWidth(), dimension.getHeight());
		scene.widthProperty().addListener(
				(observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameWidth(newValue.intValue()));
		scene.heightProperty().addListener(
				(observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameHeight(newValue.intValue()));

		setScene(scene);

		xProperty().addListener(
				(observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameXPosition(newValue.intValue()));
		yProperty().addListener(
				(observable, oldValue, newValue) -> JSkatOptions.instance().setMainFrameYPosition(newValue.intValue()));

		setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				JSkatMaster.INSTANCE.exitJSkat();
			}
		});

		placeMainWindow(targetScreen, this, screenPosition);
	}

	private static Dimension2D getMainWindowDimension(Screen targetScreen) {
		double width = JSkatOptions.instance().getMainFrameSize().getWidth();
		double height = JSkatOptions.instance().getMainFrameSize().getHeight();

		// on first startup the default values for width and height are
		// Integer.MIN_VALUE
		return new Dimension2D(width > 0 ? width : targetScreen.getBounds().getWidth() * 2 / 3,
				height > 0 ? height : targetScreen.getBounds().getHeight() * 2 / 3);
	}

	private static void placeMainWindow(Screen screen, Stage mainWindow, Point2D position) {

		if (screen.getVisualBounds().contains(position)) {
			mainWindow.setX(position.getX());
			mainWindow.setY(position.getY());
		} else {
			mainWindow.centerOnScreen();
		}
	}

	@Subscribe
	public void showInfoMessageOn(NewJSkatVersionAvailableEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(this);
		alert.setTitle(STRINGS.getString("new_version_title"));
		alert.setHeaderText(STRINGS.getString("new_version_header", event.newVersion));
		alert.setContentText(STRINGS.getString("new_version_message", event.newVersion));

		// this is a workaround for a bug under Linux that cuts long texts
		alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
				.forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
		alert.getDialogPane().setStyle("-fx-background-color: #e2d9ca;");

		alert.showAndWait();
	}

	@Subscribe
	public void showWelcomeDialogOn(ShowWelcomeInformationCommand command) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(
				JSkatFX.class.getResource("/org/jskat/gui/javafx/dialog/firststeps/view/FirstStepsDialog.fxml"));
		loader.setResources(JSkatResourceBundle.INSTANCE.getStringResources());
		VBox rootLayout;
		rootLayout = (VBox) loader.load();
		Stage stage = new Stage();
		stage.setTitle(JSkatResourceBundle.INSTANCE.getString("show_tips"));
		Scene scene = new Scene(rootLayout);
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(this);
		stage.show();
	}
}
