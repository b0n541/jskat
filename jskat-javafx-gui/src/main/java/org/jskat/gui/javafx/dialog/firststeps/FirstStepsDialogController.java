/**
 * This file is part of JSkat.
 * <p>
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.javafx.dialog.firststeps;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.jskat.JSkatFX;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.javafx.dialog.firststeps.model.FirstStepsDialogModel;

public class FirstStepsDialogController {

    @FXML
    private Parent root;

    @FXML
    private WebView firstStepsContent;

    @FXML
    private CheckBox showTipsOnStartUp;

    private final FirstStepsDialogModel model = new FirstStepsDialogModel();

    @FXML
    public void initialize() {
        root.setStyle("-fx-background-color: #e2d9ca;");
        loadFirstStepsText();
        showTipsOnStartUp.selectedProperty().bindBidirectional(model.isShowTipsOnStartUp);
        showTipsOnStartUp.selectedProperty().addListener(
                (observable, oldValue, newValue) -> JSkatOptions.instance().setShowTipsAtStartUp(newValue));
    }

    @FXML
    public void closeFirstStepsDialog() {
        final Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private void loadFirstStepsText() {
        final WebEngine engine = firstStepsContent.getEngine();
        final String contentUrl = JSkatFX.class
                .getResource("/org/jskat/gui/help/" + JSkatOptions.instance().getI18NCode() + "/gettingStarted.html")
                .toExternalForm();
        engine.load(contentUrl);
    }
}
