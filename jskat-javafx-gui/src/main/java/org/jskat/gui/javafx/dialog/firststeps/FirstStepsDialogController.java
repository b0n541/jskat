
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
        root.getStylesheets().add(getClass().getResource("/org/jskat/gui/javafx/jskat.css").toExternalForm());
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
