package org.jskat.gui.javafx.iss

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import org.jskat.control.JSkatEventBus
import org.jskat.control.JSkatMaster
import org.jskat.control.command.iss.IssConnectCommand
import org.jskat.data.iss.LoginCredentials

class IssLoginController {
    @FXML
    private lateinit var userNameField: TextField
    @FXML
    private lateinit var passwordField: PasswordField
    @FXML
    private lateinit var issConnectButton: Button
    @FXML
    private lateinit var issOpenHomepageButton: Button
    @FXML
    private lateinit var issRegisterButton: Button

    @FXML
    fun connectToIss() {
        JSkatEventBus.INSTANCE.post(
            IssConnectCommand(
                LoginCredentials(userNameField.text, passwordField.text)
            )
        )
    }

    @FXML
    fun openIssHomepage() {
        JSkatMaster.INSTANCE.openIssHomepage()
    }

    @FXML
    fun registerOnIss() {
        JSkatMaster.INSTANCE.openIssRegisterPage()
    }
} 