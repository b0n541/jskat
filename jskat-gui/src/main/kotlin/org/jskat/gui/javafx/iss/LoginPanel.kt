package org.jskat.gui.javafx.iss

import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.text.Font
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.iss.LoginCredentials
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.util.JSkatResourceBundle

class LoginPanel(private val actions: Map<JSkatAction, AbstractJSkatAction>) : VBox() {


    private val strings = JSkatResourceBundle.INSTANCE
    private val loginField = TextField()
    private val passwordField = PasswordField()

    init {
        alignment = Pos.CENTER
        spacing = 20.0
        padding = Insets(20.0)

        children.add(createHeader())
        children.add(createLoginForm())

        loginField.requestFocus()
    }

    private fun createHeader(): Label {
        val headerLabel = Label(strings.getString("login_to_iss_title"))
        headerLabel.font = Font("System Bold", 32.0)
        return headerLabel
    }

    private fun createLoginForm(): GridPane {
        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.padding = Insets(10.0)
        grid.alignment = Pos.CENTER

        val col1 = ColumnConstraints()
        col1.hgrow = Priority.NEVER
        val col2 = ColumnConstraints()
        col2.hgrow = Priority.ALWAYS
        grid.columnConstraints.addAll(col1, col2)

        // Login
        grid.addRow(0, Label(strings.getString("login")), loginField)

        // Password
        grid.addRow(1, Label(strings.getString("password")), passwordField)

        // Buttons
        val buttonBox = HBox(10.0)
        buttonBox.alignment = Pos.CENTER

        val loginButton = Button(strings.getString("connect_to_iss"))
        loginButton.setOnAction {
            val credentials = LoginCredentials(loginField.text, passwordField.text)
            val action = actions[JSkatAction.CONNECT_TO_ISS]
            // Pass credentials as source
            action?.actionPerformed(JSkatActionEvent(JSkatAction.CONNECT_TO_ISS.toString(), credentials))
        }
        loginButton.defaultButtonProperty()
            .bind(loginField.textProperty().isNotEmpty.and(passwordField.textProperty().isNotEmpty))

        val homepageButton = Button(strings.getString("iss_homepage"))
        homepageButton.setOnAction {
            val action = actions[JSkatAction.OPEN_ISS_HOMEPAGE]
            action?.actionPerformed(JSkatActionEvent(JSkatAction.OPEN_ISS_HOMEPAGE, it.source))
        }

        val registerButton = Button(strings.getString("register_on_iss"))
        registerButton.setOnAction {
            val action = actions[JSkatAction.REGISTER_ON_ISS]
            action?.actionPerformed(JSkatActionEvent(JSkatAction.REGISTER_ON_ISS, it.source))
        }

        buttonBox.children.addAll(loginButton, homepageButton, registerButton)

        grid.add(buttonBox, 0, 2, 2, 1)
        GridPane.setHalignment(buttonBox, HPos.CENTER)

        return grid
    }

    fun setFocus() {
        loginField.requestFocus()
    }
}
