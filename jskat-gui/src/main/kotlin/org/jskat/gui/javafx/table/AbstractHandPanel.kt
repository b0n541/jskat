package org.jskat.gui.javafx.table

import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.jskat.data.JSkatOptions
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.*

abstract class AbstractHandPanel(
    private val maxCards: Int,
    private val showIssWidgets: Boolean
) : VBox() {

    var position: Player? = null
        set(value) {
            field = value
            refreshHeader()
        }

    internal val bitmaps = JSkatGraphicRepository.INSTANCE
    internal val strings = JSkatResourceBundle.INSTANCE

    private val header = HBox()
    private val headerLabel = Label(" ")
    private val iconPanel = IconPanel()
    private val clockPanel = ClockPanel()

    var playerName: String? = null
        set(value) {
            field = value
            refreshHeader()
        }

    var bidValue: Int = 0
        set(value) {
            field = value
            refreshHeader()
        }

    var isActivePlayer = false
        set(value) {
            field = value
            refreshHeader()
            background = getPanelBackground(value)
            border = getPanelBorder(value)
            updateIssWidgets(value)
        }

    var isAIPlayer = false
    var playerPassed = false
        set(value) {
            field = value
            refreshHeader()
        }
    var playerGeschoben = false
        set(value) {
            field = value
            refreshHeader()
        }
    var declarer = false
        set(value) {
            field = value
            refreshHeader()
        }
    var playerContra = false
        set(value) {
            field = value
            refreshHeader()
        }
    var playerRe = false
        set(value) {
            field = value
            refreshHeader()
        }

    internal var cardPanel: CardPanel

    init {
        minHeight = 0.0
        minWidth = 0.0

        cardPanel = createCardPanel()
        initPanel()
        children.add(cardPanel)
        setVgrow(cardPanel, Priority.ALWAYS)

        if (JSkatOptions.instance().isCheatDebugMode) {
            showCards()
        }
    }

    protected open fun createCardPanel(): CardPanel {
        return CardPanel(1.0, true)
    }

    private fun initPanel() {
        setVgrow(header, Priority.NEVER)
        header.minHeight = USE_PREF_SIZE
        header.minWidth = 0.0
        headerLabel.minWidth = 0.0
        headerLabel.style = "-fx-text-fill: white;"

        background = getPanelBackground(isActivePlayer)
        border = getPanelBorder(isActivePlayer)

        val headerInsets = if (showIssWidgets) {
            "-fx-padding: 0 5 0 0;"
        } else {
            "-fx-padding: 5;"
        }
        header.style = headerInsets

        header.children.add(headerLabel)
        val blankPanel = HBox()
        HBox.setHgrow(blankPanel, Priority.ALWAYS)
        header.children.add(blankPanel)
        header.children.add(iconPanel)

        if (showIssWidgets) {
            iconPanel.isShowIssWidgets = true
            header.children.add(clockPanel)
        }
        children.add(header)
        VBox.setMargin(cardPanel, Insets(0.0, 5.0, 5.0, 5.0))
    }

    private fun getPanelBackground(isActivePlayer: Boolean): Background {
        return Background(
            BackgroundFill(
                Color.web("#486760"),
                CornerRadii(10.0),
                null
            )
        )
    }

    private fun getPanelBorder(isActivePlayer: Boolean): Border {
        return if (isActivePlayer) {
            Border(
                BorderStroke(
                    Color.rgb(255, 191, 0),
                    BorderStrokeStyle.SOLID,
                    CornerRadii(10.0),
                    BorderWidths(3.0)
                )
            )
        } else {
            Border(
                BorderStroke(
                    Color.BLACK,
                    BorderStrokeStyle.SOLID,
                    CornerRadii(10.0),
                    BorderWidths(3.0)
                )
            )
        }
    }

    private fun refreshHeader() {
        val headerText = StringBuilder()
        headerText.append(playerName).append(": ")

        if (position != null) {
            when (position) {
                Player.FOREHAND -> headerText.append(strings.getString("forehand"))
                Player.MIDDLEHAND -> headerText.append(strings.getString("middlehand"))
                Player.REARHAND -> headerText.append(strings.getString("rearhand"))
                else -> {
                }
            }

            headerText.append(" ${strings.getString("bid")}: ")
            headerText.append(bidValue)

            if (playerPassed || playerGeschoben || playerContra || playerRe) {
                headerText.append(" (")
                var passedGeschobenContraRe = ""
                if (playerPassed) {
                    passedGeschobenContraRe = strings.getString("passed")
                }
                if (playerGeschoben) {
                    passedGeschobenContraRe = strings.getString("geschoben")
                }

                if (passedGeschobenContraRe.isNotEmpty() && (playerContra || playerRe)) {
                    passedGeschobenContraRe += " "
                }

                if (playerContra) {
                    passedGeschobenContraRe += strings.getString("contra")
                }
                if (playerRe) {
                    passedGeschobenContraRe += strings.getString("re")
                }
                headerText.append(passedGeschobenContraRe)
                headerText.append(")")
            }

            if (declarer) {
                headerText.append(" (${strings.getString("declarer")})")
                headerLabel.style = "-fx-font-weight: bold; -fx-text-fill: white;"
            } else {
                headerLabel.style = "-fx-font-weight: normal; -fx-text-fill: white;"
            }
        }

        iconPanel.setThinking(isActivePlayer && isAIPlayer)
        headerLabel.text = headerText.toString()
    }

    fun addCard(newCard: Card) {
        cardPanel.addCard(newCard)
    }

    fun addCards(newCards: CardList) {
        cardPanel.addCards(newCards)
    }

    fun removeCard(cardToRemove: Card) {
        cardPanel.removeCard(cardToRemove)
    }

    fun removeAllCards() {
        cardPanel.clearCards()
    }

    fun clearHandPanel() {
        cardPanel.clearCards()
        bidValue = 0
        playerPassed = false
        playerGeschoben = false
        playerContra = false
        playerRe = false
        declarer = false
        iconPanel.reset()
        refreshHeader()
        isActivePlayer = false
        hideCards()
    }

    open fun hideCards() {
        cardPanel.hideCards()
    }

    fun showCards() {
        cardPanel.showCards()
    }

    fun setSortGameType(newGameType: GameType) {
        cardPanel.setSortType(newGameType)
    }

    fun isHandFull(): Boolean {
        return cardPanel.cardCount == maxCards
    }

    fun setPlayerTime(newTime: Double) {
        clockPanel.setPlayerTime(newTime)
    }

    fun setChatEnabled(isChatEnabled: Boolean) {
        iconPanel.isChatEnabled = isChatEnabled
        iconPanel.refreshIcons()
    }

    fun setReadyToPlay(isReadyToPlay: Boolean) {
        iconPanel.isReadyToPlay = isReadyToPlay
        iconPanel.refreshIcons()
    }

    fun setResign(isResign: Boolean) {
        iconPanel.setResign(isResign)
    }

    private fun updateIssWidgets(isActivePlayer: Boolean) {
        if (showIssWidgets) {
            if (isActivePlayer) {
                clockPanel.setActive()
            } else {
                clockPanel.setInactive()
            }
        }
    }

    fun setContra() {
        playerContra = true
    }

    fun setRe() {
        playerRe = true
    }

    fun setGeschoben() {
        playerGeschoben = true
    }
}
