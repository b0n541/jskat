package org.jskat.gui.javafx.iss

import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.jskat.control.gui.action.JSkatAction
import org.jskat.data.iss.ChatMessage
import javax.swing.ActionMap

class ChatPanel(private val actions: ActionMap) : VBox() {

    private val inputLine = TextField()
    private val chatTabs = TabPane()
    private val chats = mutableMapOf<String, TextArea>()
    private var activeChatName: String = ""

    init {
        VBox.setVgrow(chatTabs, Priority.ALWAYS)
        children.addAll(chatTabs, inputLine)

        inputLine.setOnAction {
            val message = inputLine.text
            val chatMessage = ChatMessage(activeChatName, message)
            val swingAction = actions.get(JSkatAction.SEND_CHAT_MESSAGE)
            swingAction?.actionPerformed(java.awt.event.ActionEvent(chatMessage, java.awt.event.ActionEvent.ACTION_PERFORMED, null))
            inputLine.clear()
        }

        chatTabs.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            activeChatName = newValue?.userData as? String ?: ""
        }
    }

    fun addNewChat(title: String, name: String): TextArea {
        val chatArea = TextArea().apply {
            isEditable = false
            isWrapText = true
        }
        chats[name] = chatArea

        val tab = Tab(title).apply {
            content = chatArea
            userData = name
        }
        chatTabs.tabs.add(tab)
        chatTabs.selectionModel.select(tab)
        activeChatName = name

        return chatArea
    }

    fun appendMessage(message: ChatMessage) {
        val chatArea = chats.getOrPut(message.chatName) {
            addNewChat(message.chatName, message.chatName)
        }
        chatArea.appendText(message.message + "\n")
    }

    fun setFocus() {
        inputLine.requestFocus()
    }
}
