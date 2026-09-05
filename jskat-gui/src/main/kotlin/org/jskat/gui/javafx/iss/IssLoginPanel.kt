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
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.IconSize
import org.jskat.util.JSkatResourceBundle

class IssLoginPanel(private val actions: Map<JSkatAction, AbstractJSkatAction>) : VBox() {

    private val bitmaps = JSkatGraphicRepository.INSTANCE
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
        val headerLabel = Label(strings.getString("loginToIssTitle"))
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
        grid.columnConstraints.addAll(col1)

        // Login
        grid.addRow(0, Label(strings.getString("login")), loginField)

        // Password
        grid.addRow(1, Label(strings.getString("password")), passwordField)

        // Buttons
        val buttonBox = HBox(10.0)
        buttonBox.alignment = Pos.CENTER

        val loginButton = createActionButton(
            JSkatAction.CONNECT_TO_ISS,
            strings.getString("connectToIss"),
            JSkatGraphicRepository.Icon.CONNECT_ISS,
        )
        loginButton.defaultButtonProperty()
            .bind(loginField.textProperty().isNotEmpty.and(passwordField.textProperty().isNotEmpty))

        val homepageButton = createActionButton(
            JSkatAction.OPEN_ISS_HOMEPAGE,
            strings.getString("openIssHomepage"),
            JSkatGraphicRepository.Icon.WEB
        )

        val registerButton = createActionButton(
            JSkatAction.REGISTER_ON_ISS,
            strings.getString("registerOnIss"),
            JSkatGraphicRepository.Icon.REGISTER
        )

        buttonBox.children.addAll(loginButton, homepageButton, registerButton)

        grid.add(buttonBox, 0, 2, 2, 1)
        GridPane.setHalignment(buttonBox, HPos.CENTER)

        return grid
    }

    fun setFocus() {
        loginField.requestFocus()
    }

    private fun createActionButton(
        action: JSkatAction,
        text: String,
        icon: JSkatGraphicRepository.Icon
    ): Button {
        val button = Button(text)
        button.graphic = bitmaps.getImageView(icon, IconSize.BIG)
        button.maxWidth = Double.MAX_VALUE
        button.setOnAction { event ->
            var actionSource = if (action == JSkatAction.CONNECT_TO_ISS) {
                LoginCredentials(loginField.text, passwordField.text)
            } else {
                event.source
            }

            actions[action]?.actionPerformed(JSkatActionEvent(action, actionSource))
        }
        return button
    }
}
