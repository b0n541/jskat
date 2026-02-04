package org.jskat.gui.javafx.main

import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.Font
import org.jskat.control.gui.action.JSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.JSkatResourceBundle
import java.awt.event.ActionEvent
import javax.swing.ActionMap

class WelcomePanel(private val actions: ActionMap) : VBox() {

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

    private fun createButtonGrid(): GridPane {
        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.padding = Insets(10.0)
        grid.alignment = Pos.CENTER

        val col1 = ColumnConstraints()
        col1.prefWidth = 200.0
        col1.hgrow = Priority.SOMETIMES
        val col2 = ColumnConstraints()
        col2.hgrow = Priority.SOMETIMES
        grid.columnConstraints.addAll(col1, col2)

        // ISS Table
        val issButton = createActionButton(JSkatAction.SHOW_ISS_LOGIN, strings.getString("show_iss_lobby"))
        val issDescription =
            Label(strings.getString("explain_iss_table_1") + "\n" + strings.getString("explain_iss_table_2"))
        issDescription.isWrapText = true
        grid.addRow(0, issButton, issDescription)

        // Local Table
        val localButton = createActionButton(JSkatAction.CREATE_LOCAL_TABLE, strings.getString("create_local_table"))
        val localDescription =
            Label(strings.getString("explain_local_table_1") + "\n" + strings.getString("explain_local_table_2"))
        localDescription.isWrapText = true
        grid.addRow(1, localButton, localDescription)

        // Options
        val optionsButton = createActionButton(JSkatAction.PREFERENCES, strings.getString("preferences"))
        val optionsDescription = Label(strings.getString("explain_options_1"))
        optionsDescription.isWrapText = true
        grid.addRow(2, optionsButton, optionsDescription)

        // Quit
        val quitButton = createActionButton(JSkatAction.EXIT_JSKAT, strings.getString("exit_jskat"))
        val quitDescription = Label(strings.getString("explain_exit"))
        quitDescription.isWrapText = true
        grid.addRow(3, quitButton, quitDescription)

        return grid
    }

    private fun createActionButton(action: JSkatAction, text: String): Button {
        val button = Button(text)
        button.maxWidth = Double.MAX_VALUE
        button.setOnAction { e ->
            val swingAction = actions.get(action)
            swingAction?.actionPerformed(
                ActionEvent(
                    e.source,
                    ActionEvent.ACTION_PERFORMED,
                    null
                )
            )
        }
        GridPane.setHalignment(button, HPos.CENTER)
        return button
    }
}