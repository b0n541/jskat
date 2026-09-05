package org.jskat.gui.javafx.table

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.GameContract
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.GameType
import org.jskat.util.JSkatResourceBundle
import org.slf4j.LoggerFactory

class SchieberamschContextPanel(
    tableName: String,
    actions: Map<JSkatAction, AbstractJSkatAction>,
    private val userPanel: JSkatUserPanel,
    maxCards: Int
) : GridPane() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val log = LoggerFactory.getLogger(SchieberamschContextPanel::class.java)

    private val discardPanel = DiscardPanel(tableName, actions, maxCards)
    private val centerPanel = StackPane()
    private val grandHandPanel: GridPane

    init {
        style = "-fx-background-color: transparent;"
        sceneProperty().addListener { _, _, newScene ->
            newScene?.fill = Color.TRANSPARENT
        }

        val col1 = ColumnConstraints()
        col1.percentWidth = 25.0
        val col2 = ColumnConstraints()
        col2.hgrow = Priority.ALWAYS
        val col3 = ColumnConstraints()
        col3.percentWidth = 25.0
        columnConstraints.addAll(col1, col2, col3)

        grandHandPanel = createGrandHandSchiebeRamschPanel(actions)
        centerPanel.children.addAll(grandHandPanel, discardPanel)
        add(centerPanel, 1, 0)

        resetPanel()
    }

    private fun createGrandHandSchiebeRamschPanel(actions: Map<JSkatAction, AbstractJSkatAction>): GridPane {
        val result = GridPane()
        result.style = "-fx-background-color: transparent;"

        val question = Label(strings.getString("wantPlayGrandHand"))
        question.style = "-fx-font-size: 16px; -fx-font-weight: bold;"
        result.add(question, 0, 0, 2, 1)

        val grandHandButton = Button(strings.getString("yes"))
        grandHandButton.setOnAction {
            try {
                val contract = GameContract(GameType.GRAND).withHand()
                actions[JSkatAction.PLAY_GRAND_HAND]?.actionPerformed(JSkatActionEvent(JSkatAction.PLAY_GRAND_HAND, contract))
            } catch (e: IllegalArgumentException) {
                log.error(e.message)
            }
        }
        result.add(grandHandButton, 0, 1)

        val schieberamschButton = Button(strings.getString("no"))
        schieberamschButton.setOnAction {
            showPanel(DISCARD)
        }
        result.add(schieberamschButton, 1, 1)

        return result
    }

    fun resetPanel() {
        discardPanel.resetPanel()
        showPanel(GRAND_HAND)
    }

    private fun showPanel(panelName: String) {
        when (panelName) {
            GRAND_HAND -> {
                grandHandPanel.isVisible = true
                discardPanel.isVisible = false
            }
            DISCARD -> {
                grandHandPanel.isVisible = false
                discardPanel.isVisible = true
            }
        }
    }

    fun removeCard(card: Card) {
        discardPanel.removeCard(card)
    }

    fun isHandFull(): Boolean {
        return discardPanel.isHandFull()
    }

    fun addCard(card: Card) {
        discardPanel.addCard(card)
    }

    fun setSkat(skat: CardList) {
        discardPanel.setSkat(skat)
    }

    companion object {
        private const val GRAND_HAND = "GRAND_HAND"
        private const val DISCARD = "DISCARD"
    }
}
