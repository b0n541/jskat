package org.jskat.gui.javafx.table

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.scene.control.Label
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import org.jskat.util.JSkatResourceBundle

class ScoreListTableView(playerNames: List<String>) : TableView<ScoreHistoryRow>() {

    private val strings = JSkatResourceBundle.INSTANCE

    init {
        // Keep every score column in the viewport; the score sheet is not meant to scroll sideways.
        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN

        val gameNoColumn = TableColumn<ScoreHistoryRow, Int>("#").apply {
            prefWidth = 30.0
            setCellFactory {
                object : TableCell<ScoreHistoryRow, Int>() {
                    override fun updateItem(item: Int?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = if (tableRow.index <= items.size - 1)
                            "${tableRow.index + 1}"
                        else {
                            null
                        }
                    }
                }
            }
        }

        columns.add(gameNoColumn)

        val playerColumns = playerNames.map { playerName ->
            TableColumn<ScoreHistoryRow, Int>(playerName)
        }

        for ((i, column) in playerColumns.withIndex()) {
            column.setCellValueFactory { cellData ->
                ReadOnlyObjectWrapper(cellData.value.playerTotals[i])
            }
            column.setCellFactory {
                object : TableCell<ScoreHistoryRow, Int>() {
                    override fun updateItem(item: Int?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = if (empty) null else item?.toString() ?: "-"
                    }
                }
            }
        }

        columns.addAll(playerColumns)

        val gameValueColumn = TableColumn<ScoreHistoryRow, Int>(strings.getString("game")).apply {
            prefWidth = 60.0
            setCellValueFactory { cellData ->
                ReadOnlyObjectWrapper(cellData.value.gameValue)
            }
        }

        columns.add(gameValueColumn)

        columns.forEach { column -> column.style = "-fx-alignment: center-right;" }

        // don't show a placeholder text on an empty table
        placeholder = Label("")
    }

    fun setPlayerNames(upperLeftPlayerName: String, upperRightPlayerName: String, lowerPlayerName: String) {
        columns[1].text = upperLeftPlayerName
        columns[2].text = upperRightPlayerName
        columns[3].text = lowerPlayerName
    }

    fun setPlayerName(index: Int, playerName: String) {
        columns[index].text = playerName
    }
}
