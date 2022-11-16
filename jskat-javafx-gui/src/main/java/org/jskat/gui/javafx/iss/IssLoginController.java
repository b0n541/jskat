package org.jskat.gui.javafx.iss;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.jskat.control.JSkatEventBus;
import org.jskat.control.JSkatMaster;
import org.jskat.control.command.iss.IssConnectCommand;
import org.jskat.data.iss.LoginCredentials;

public class IssLoginController {
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button issConnectButton;
    @FXML
    private Button issOpenHomepageButton;
    @FXML
    private Button issRegisterButton;

    @FXML
    public void connectToIss() {
        JSkatEventBus.INSTANCE.post(
                new IssConnectCommand(
                        new LoginCredentials(userNameField.getText(), passwordField.getText())));
    }

    @FXML
    public void openIssHomepage() {
        JSkatMaster.INSTANCE.openIssHomepage();
    }

    @FXML
    public void registerOnIss() {
        JSkatMaster.INSTANCE.openIssRegisterPage();
    }
}
