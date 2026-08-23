package org.jskat.gui.javafx.main

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.gui.img.JSkatGraphicRepository.IconSize
import org.jskat.util.JSkatResourceBundle

class WelcomePanel(private val actions: Map<JSkatAction, AbstractJSkatAction>) : VBox() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val bitmaps = JSkatGraphicRepository.INSTANCE

    init {
        alignment = Pos.CENTER
        spacing = 20.0
        padding = Insets(20.0)

        children.add(createHeader())
        children.add(createButtonGrid())
    }

    private fun createHeader(): HBox {
        val header = HBox(20.0)
        header.alignment = Pos.CENTER

        val logo = ImageView(bitmaps.jSkatLogoImageFX)
        logo.fitHeight = 100.0
        logo.fitWidth = 100.0
        logo.isPreserveRatio = true

        val title = Label(strings.getString("welcome_to_jskat"))
        title.font = Font("System Bold", 32.0)

        header.children.addAll(logo, title)
        return header
    }

    private fun createButtonGrid(): VBox {
        val vbox = VBox()
        vbox.spacing = 10.0
        vbox.padding = Insets(10.0)
        vbox.alignment = Pos.CENTER_LEFT
        vbox.maxWidth = USE_PREF_SIZE

        // ISS Table
        val issButton = createActionButton(
            JSkatAction.SHOW_ISS_LOGIN,
            strings.getString("play_on_iss"),
            JSkatGraphicRepository.Icon.CONNECT_ISS
        )
        val issDescription =
            Label(strings.getString("explain_iss_table_1") + "\n" + strings.getString("explain_iss_table_2"))
        issDescription.isWrapText = true
        vbox.children.add(createRow(issButton, issDescription))

        // Local Table
        val localButton = createActionButton(
            JSkatAction.CREATE_LOCAL_TABLE,
            strings.getString("create_local_table"),
            JSkatGraphicRepository.Icon.TABLE
        )
        val localDescription =
            Label(strings.getString("explain_local_table_1") + "\n" + strings.getString("explain_local_table_2"))
        localDescription.isWrapText = true
        vbox.children.add(createRow(localButton, localDescription))

        // Options
        val optionsButton = createActionButton(
            JSkatAction.PREFERENCES,
            strings.getString("preferences"),
            JSkatGraphicRepository.Icon.PREFERENCES
        )
        val optionsDescription = Label(strings.getString("explain_options_1"))
        optionsDescription.isWrapText = true
        vbox.children.add(createRow(optionsButton, optionsDescription))

        // Quit
        val quitButton = createActionButton(
            JSkatAction.EXIT_JSKAT,
            strings.getString("exit_jskat"),
            JSkatGraphicRepository.Icon.EXIT
        )
        val quitDescription = Label(strings.getString("explain_exit"))
        quitDescription.isWrapText = true
        vbox.children.add(createRow(quitButton, quitDescription))

        return vbox
    }

    private fun createRow(button: Button, description: Label): HBox {
        val hbox = HBox(20.0)
        hbox.alignment = Pos.CENTER_LEFT
        // Allow the label to grow
        HBox.setHgrow(description, Priority.ALWAYS)
        // Ensure button has a consistent size or at least isn't tiny.
        button.minWidth = 250.0 // Ensure buttons are wide enough and "bigger"
        hbox.children.addAll(button, description)
        return hbox
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
            actions[action]?.actionPerformed(JSkatActionEvent(action, event.source))
        }
        return button
    }
}
