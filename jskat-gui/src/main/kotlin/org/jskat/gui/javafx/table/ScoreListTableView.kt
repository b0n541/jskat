package org.jskat.gui.javafx.table

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.scene.control.Label
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import org.jskat.util.JSkatResourceBundle

class ScoreListTableView(playerNames: List<String>) : TableView<ScoreListEntry>() {

    private val strings = JSkatResourceBundle.INSTANCE

    init {

        val gameNoColumn = TableColumn<ScoreListEntry, Int>("#").apply {
            prefWidth = 30.0
            setCellFactory {
                object : TableCell<ScoreListEntry, Int>() {
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
            TableColumn<ScoreListEntry, Int>(playerName)
        }

        for ((i, column) in playerColumns.withIndex()) {
            column.setCellFactory {
                object : TableCell<ScoreListEntry, Int>() {
                    override fun updateItem(item: Int?, empty: Boolean) {
                        super.updateItem(item, empty)
                        // If this cell is empty or no valid row is available, clear the text.
                        if (empty || tableRow == null || tableRow.index < 0) {
                            text = null
                        } else {
                            val currentIndex = tableRow.index
                            val playerName = tableColumn.text
                            if (tableView.items[currentIndex].data[playerName] == null) {
                                text = "-"
                            } else {
                                // Compute the cumulative total for this player by filtering
                                // all GameData items up to (and including) this row.
                                val cumulative = tableView.items
                                    .take(currentIndex + 1)
                                    .filter { it.data.containsKey(playerName) }
                                    .sumOf { it.data[playerName] ?: 0 }
                                text = cumulative.toString()
                            }
                        }
                    }
                }
            }
        }

        columns.addAll(playerColumns)

        val gameValueColumn = TableColumn<ScoreListEntry, Int>(strings.getString("game")).apply {
            prefWidth = 60.0
            setCellValueFactory { cellData ->
                ReadOnlyObjectWrapper(cellData.value.data.entries.first().value)
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