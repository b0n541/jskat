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

import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.event.general.NewJSkatVersionAvailableEvent;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.swing.JSkatViewImpl;
import org.jskat.util.JSkatResourceBundle;

import com.google.common.eventbus.Subscribe;

import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
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

		placeMainWindow(targetScreen, screenPosition);
	}

	private static Dimension2D getMainWindowDimension(Screen targetScreen) {
		double width = JSkatOptions.instance().getMainFrameSize().getWidth();
		double height = JSkatOptions.instance().getMainFrameSize().getHeight();

		// on first startup the default values for width and height are
		// Integer.MIN_VALUE
		return new Dimension2D(width > 0 ? width : targetScreen.getBounds().getWidth() / 2,
				height > 0 ? height : targetScreen.getBounds().getHeight() / 2);
	}

	private void placeMainWindow(Screen targetScreen, Point2D screenPosition) {

		double targetXPosition = screenPosition.getX();
		double targetYPosition = screenPosition.getY();

		if (targetXPosition < targetScreen.getBounds().getMinX()) {
			targetXPosition = targetScreen.getBounds().getMinX() + 400;
		}
		if (targetXPosition > targetScreen.getBounds().getMaxX()) {
			targetXPosition = targetScreen.getBounds().getMaxX() - 400;
		}

		if (targetYPosition < targetScreen.getBounds().getMinY()) {
			targetYPosition = targetScreen.getBounds().getMinY() + 400;
		}
		if (targetYPosition > targetScreen.getBounds().getMaxY()) {
			targetYPosition = targetScreen.getBounds().getMaxY() - 400;
		}

		placeMainWindow(targetScreen, this, new Point2D(targetXPosition, targetYPosition));
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

		alert.showAndWait();
	}
}
