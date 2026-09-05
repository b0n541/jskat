package org.jskat.gui.javafx.table

import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import org.jskat.control.gui.action.JSkatAction
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.util.Card
import org.jskat.util.CardList
import org.jskat.util.GameType

class DeclaringContextPanel(
    tableName: String,
    actions: Map<JSkatAction, AbstractJSkatAction>,
    newUserPanel: JSkatUserPanel
) : GridPane() {

    private val discardPanel = DiscardPanel(tableName, actions, 2)
    private val announcePanel = GameAnnouncePanel(actions, newUserPanel, discardPanel)

    init {
        style = "-fx-background-color: transparent;"
        alignment = Pos.CENTER
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

        add(announcePanel, 0, 0)
        GridPane.setHalignment(announcePanel, HPos.CENTER)

        discardPanel.setAnnouncePanel(announcePanel)
        add(discardPanel, 1, 0)

        val blankRegion = Region()
        add(blankRegion, 2, 0)
    }

    fun resetPanel() {
        discardPanel.resetPanel()
        announcePanel.resetPanel()
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

    fun preselectGameTypeIfUnset(gameType: GameType?) {
        if (announcePanel.selectedGameType() == null && gameType != null) {
            announcePanel.selectGameType(gameType)
        }
    }

    fun selectedGameType(): GameType? = announcePanel.selectedGameType()
}
