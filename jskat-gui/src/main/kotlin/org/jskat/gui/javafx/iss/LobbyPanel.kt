package org.jskat.gui.javafx.iss

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.Font
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.iss.ChatMessage
import org.jskat.gui.img.JSkatGraphicRepository
import java.awt.image.BufferedImage
import javax.swing.ActionMap

class LobbyPanel(private val actions: ActionMap) : VBox() {

    private val playerList = FXCollections.observableArrayList<Player>()
    private val tableList = FXCollections.observableArrayList<Table>()
    private val chatPanel = ChatPanel(actions)
    private val bitmaps = JSkatGraphicRepository.INSTANCE

    init {
        padding = Insets(10.0)
        spacing = 10.0

        val header = Label("Welcome to ISS").apply {
            font = Font("System Bold", 32.0)
        }
        children.add(header)

        val tablesAndPlayers = GridPane().apply {
            hgap = 10.0
            vgap = 10.0
            add(Label("Players"), 0, 0)
            add(createPlayerTable(), 0, 1)
            add(Label("Tables"), 1, 0)
            add(createTableTable(), 1, 1)

            val col1 = ColumnConstraints().apply { hgrow = Priority.ALWAYS; percentWidth = 50.0 }
            val col2 = ColumnConstraints().apply { hgrow = Priority.ALWAYS; percentWidth = 50.0 }
            columnConstraints.addAll(col1, col2)
        }
        setVgrow(tablesAndPlayers, Priority.ALWAYS)
        children.add(tablesAndPlayers)

        val buttons = HBox(10.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                createActionButton("Create table", JSkatAction.CREATE_ISS_TABLE, JSkatGraphicRepository.Icon.TABLE),
                createActionButton("Disconnect", JSkatAction.DISCONNECT_FROM_ISS, JSkatGraphicRepository.Icon.LOG_OUT)
            )
        }
        children.add(buttons)

        setVgrow(chatPanel, Priority.ALWAYS)
        children.add(chatPanel)
        chatPanel.addNewChat("Lobby", "Lobby")
    }

    private fun createPlayerTable(): TableView<Player> {
        val table = TableView(playerList)
        table.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        table.columns.add(TableColumn<Player, String>("Name").apply {
            cellValueFactory = javafx.util.Callback { it.value.name }
        })
        table.columns.add(TableColumn<Player, Long>("Games").apply {
            cellValueFactory = javafx.util.Callback { it.value.gamesPlayed as javafx.beans.value.ObservableValue<Long> }
        })
        table.columns.add(TableColumn<Player, Double>("Strength").apply {
            cellValueFactory = javafx.util.Callback { it.value.strength as javafx.beans.value.ObservableValue<Double> }
        })
        table.columns.add(TableColumn<Player, String>("Language").apply {
            cellValueFactory = javafx.util.Callback { it.value.language }
            cellFactory = javafx.util.Callback {
                object : TableCell<Player, String>() {
                    override fun updateItem(item: String?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = null
                        graphic = if (empty || item == null) {
                            null
                        } else {
                            val flagChar = item.firstOrNull()
                            if (flagChar != null) {
                                val flag = JSkatGraphicRepository.Flag.valueOf(flagChar)
                                if (flag != null) {
                                    val awtImage = bitmaps.getFlagImage(flag)
                                    val bufferedImage = BufferedImage(
                                        awtImage.getWidth(null),
                                        awtImage.getHeight(null),
                                        BufferedImage.TYPE_INT_ARGB
                                    )
                                    val g = bufferedImage.createGraphics()
                                    g.drawImage(awtImage, 0, 0, null)
                                    g.dispose()
                                    ImageView(SwingFXUtils.toFXImage(bufferedImage, null))
                                } else {
                                    null
                                }
                            } else {
                                null
                            }
                        }
                    }
                }
            }
        })
        return table
    }

    private fun createTableTable(): TableView<Table> {
        val table = TableView(tableList)
        table.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        table.columns.add(TableColumn<Table, String>("Name").apply {
            cellValueFactory = javafx.util.Callback { it.value.name }
        })
        table.columns.add(TableColumn<Table, Long>("Seats").apply {
            cellValueFactory = javafx.util.Callback { it.value.maxPlayers as javafx.beans.value.ObservableValue<Long> }
        })
        table.columns.add(TableColumn<Table, Long>("Games").apply {
            cellValueFactory = javafx.util.Callback { it.value.gamesPlayed as javafx.beans.value.ObservableValue<Long> }
        })
        table.columns.add(TableColumn<Table, String>("Player 1").apply {
            cellValueFactory = javafx.util.Callback { it.value.player1 }
        })
        table.columns.add(TableColumn<Table, String>("Player 2").apply {
            cellValueFactory = javafx.util.Callback { it.value.player2 }
        })
        table.columns.add(TableColumn<Table, String>("Player 3").apply {
            cellValueFactory = javafx.util.Callback { it.value.player3 }
        })

        table.setOnMouseClicked { event ->
            if (event.clickCount == 2) {
                val selectedTable = table.selectionModel.selectedItem
                if (selectedTable != null) {
                    val action = actions.get(JSkatAction.JOIN_ISS_TABLE)
                    action?.actionPerformed(
                        java.awt.event.ActionEvent(
                            selectedTable.name.get(),
                            java.awt.event.ActionEvent.ACTION_PERFORMED,
                            null
                        )
                    )
                }
            }
        }

        return table
    }

    private fun createActionButton(text: String, action: JSkatAction, icon: JSkatGraphicRepository.Icon): Button {
        val button = Button(text)
        button.graphic = bitmaps.getImageView(icon, JSkatGraphicRepository.IconSize.BIG)
        button.setOnAction {
            val swingAction = actions.get(action)
            swingAction?.actionPerformed(
                java.awt.event.ActionEvent(
                    it.source,
                    java.awt.event.ActionEvent.ACTION_PERFORMED,
                    null
                )
            )
        }
        return button
    }

    fun updatePlayer(playerName: String, language: String, gamesPlayed: Long, strength: Double) {
        val existingPlayer = playerList.find { it.name.get() == playerName }
        if (existingPlayer != null) {
            existingPlayer.language.set(language)
            existingPlayer.gamesPlayed.set(gamesPlayed)
            existingPlayer.strength.set(strength)
        } else {
            playerList.add(Player(playerName, language, gamesPlayed, strength))
        }
    }

    fun removePlayer(playerName: String) {
        playerList.removeIf { it.name.get() == playerName }
    }

    fun updateTable(
        tableName: String,
        maxPlayers: Int,
        gamesPlayed: Long,
        player1: String,
        player2: String,
        player3: String
    ) {
        val existingTable = tableList.find { it.name.get() == tableName }
        if (existingTable != null) {
            existingTable.maxPlayers.set(maxPlayers.toLong())
            existingTable.gamesPlayed.set(gamesPlayed)
            existingTable.player1.set(player1)
            existingTable.player2.set(player2)
            existingTable.player3.set(player3)
        } else {
            tableList.add(Table(tableName, maxPlayers, gamesPlayed, player1, player2, player3))
        }
    }

    fun removeTable(tableName: String) {
        tableList.removeIf { it.name.get() == tableName }
    }

    fun appendChatMessage(message: ChatMessage) {
        chatPanel.appendMessage(message)
    }

    fun setFocus() {
        chatPanel.setFocus()
    }

    data class Player(
        val name: SimpleStringProperty,
        val language: SimpleStringProperty,
        val gamesPlayed: SimpleLongProperty,
        val strength: SimpleDoubleProperty
    ) {
        constructor(name: String, language: String, gamesPlayed: Long, strength: Double) : this(
            SimpleStringProperty(name),
            SimpleStringProperty(language),
            SimpleLongProperty(gamesPlayed),
            SimpleDoubleProperty(strength)
        )
    }

    data class Table(
        val name: SimpleStringProperty,
        val maxPlayers: SimpleLongProperty,
        val gamesPlayed: SimpleLongProperty,
        val player1: SimpleStringProperty,
        val player2: SimpleStringProperty,
        val player3: SimpleStringProperty
    ) {
        constructor(
            name: String,
            maxPlayers: Int,
            gamesPlayed: Long,
            player1: String,
            player2: String,
            player3: String
        ) : this(
            SimpleStringProperty(name),
            SimpleLongProperty(maxPlayers.toLong()),
            SimpleLongProperty(gamesPlayed),
            SimpleStringProperty(player1),
            SimpleStringProperty(player2),
            SimpleStringProperty(player3)
        )
    }
}
