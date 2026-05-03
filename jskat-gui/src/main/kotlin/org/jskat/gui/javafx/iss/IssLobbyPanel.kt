package org.jskat.gui.javafx.iss

import com.google.common.eventbus.Subscribe
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
import org.jskat.control.JSkatEventBus
import org.jskat.control.event.iss.IssNewChatMessageEvent
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.control.iss.ChatMessageType
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.JSkatResourceBundle
import java.awt.image.BufferedImage

class IssLobbyPanel(private val actions: Map<JSkatAction, AbstractJSkatAction>, private val userName: String?) :
    VBox() {

    private val strings = JSkatResourceBundle.INSTANCE

    private val playerList = FXCollections.observableArrayList<Player>()
    private val tableList = FXCollections.observableArrayList<Table>()
    private val chatPanel = IssChatPanel(actions)
    private val bitmaps = JSkatGraphicRepository.INSTANCE
    private val playerTableView = TableView(playerList)

    init {
        padding = Insets(10.0)
        spacing = 10.0

        val header = Label(strings.getString("welcome_to_iss_title")).apply {
            font = Font("System Bold", 32.0)
            alignment = Pos.CENTER
            maxWidth = Double.MAX_VALUE
        }
        children.add(header)

        val tablesAndPlayers = GridPane().apply {
            hgap = 10.0
            vgap = 10.0
            add(Label(strings.getString("players")), 0, 0)
            add(createPlayerTable(), 0, 1)
            add(Label(strings.getString("tables")), 1, 0)
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
                createActionButton(
                    strings.getString("iss.new.table"),
                    JSkatAction.CREATE_ISS_TABLE,
                    JSkatGraphicRepository.Icon.TABLE
                ),
                createActionButton(
                    strings.getString("iss.disconnect"),
                    JSkatAction.DISCONNECT_FROM_ISS,
                    JSkatGraphicRepository.Icon.LOG_OUT
                )
            )
        }
        children.add(buttons)

        setVgrow(chatPanel, Priority.ALWAYS)
        children.add(chatPanel)
        chatPanel.addNewChat(strings.getString("lobby"), strings.getString("lobby"))

        JSkatEventBus.INSTANCE.register(this)
    }

    private fun createPlayerTable(): TableView<Player> {
        val table = playerTableView
        table.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        table.columns.add(TableColumn<Player, String>(strings.getString("name")).apply {
            cellValueFactory = javafx.util.Callback { it.value.name }
        })
        table.columns.add(TableColumn<Player, Long>(strings.getString("games")).apply {
            cellValueFactory = javafx.util.Callback { it.value.gamesPlayed as javafx.beans.value.ObservableValue<Long> }
            styleClass.add("right-aligned")
        })
        table.columns.add(TableColumn<Player, Double>(strings.getString("strength")).apply {
            cellValueFactory = javafx.util.Callback { it.value.strength as javafx.beans.value.ObservableValue<Double> }
            styleClass.add("right-aligned")
        })
        table.columns.add(TableColumn<Player, String>(strings.getString("language")).apply {
            cellValueFactory = javafx.util.Callback { it.value.language }
            cellFactory = javafx.util.Callback {
                object : TableCell<Player, String>() {
                    override fun updateItem(item: String?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = null
                        graphic = if (empty || item == null) {
                            null
                        } else {
                            HBox(2.0).apply {
                                alignment = Pos.CENTER
                                item.forEach { flagChar ->
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
                                        children.add(ImageView(SwingFXUtils.toFXImage(bufferedImage, null)))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        )
        table.setRowFactory {
            object : TableRow<Player>() {
                override fun updateItem(item: Player?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item != null && item.name.get() == userName) {
                        styleClass.add("table-row-highlighted")
                    } else {
                        styleClass.remove("table-row-highlighted")
                    }
                }
            }
        }
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
            styleClass.add("right-aligned")
        })
        table.columns.add(TableColumn<Table, Long>("Games").apply {
            cellValueFactory = javafx.util.Callback { it.value.gamesPlayed as javafx.beans.value.ObservableValue<Long> }
            styleClass.add("right-aligned")
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
                    val action = actions[JSkatAction.JOIN_ISS_TABLE]
                    action?.actionPerformed(JSkatActionEvent(JSkatAction.JOIN_ISS_TABLE, selectedTable.name.get()))
                }
            }
        }

        return table
    }

    private fun createActionButton(text: String, action: JSkatAction, icon: JSkatGraphicRepository.Icon): Button {
        val button = Button(text)
        button.graphic = bitmaps.getImageView(icon, JSkatGraphicRepository.IconSize.BIG)
        button.setOnAction {
            val jskatAction = actions[action]
            jskatAction?.actionPerformed(JSkatActionEvent(action, it.source))
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

    @Subscribe
    fun appendChatMessageOn(event: IssNewChatMessageEvent) {
        if (event.messageType == ChatMessageType.LOBBY) {
            chatPanel.appendMessage(event.message)
        }
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
