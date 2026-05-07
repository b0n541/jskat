package org.jskat.gui.javafx.table

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.GameAnnouncement
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.CardList
import org.jskat.util.GameType
import org.jskat.util.JSkatResourceBundle
import org.slf4j.LoggerFactory

class GameAnnouncePanel(
    private val actions: Map<JSkatAction, AbstractJSkatAction>,
    private val userPanel: JSkatUserPanel,
    private val discardPanel: DiscardPanel?
) : GridPane() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val bitmaps = JSkatGraphicRepository.INSTANCE
    private val log = LoggerFactory.getLogger(GameAnnouncePanel::class.java)

    private val gameTypeGroup = ToggleGroup()
    private val grandButton = createRadioButton(GameType.GRAND)
    private val clubsButton = createRadioButton(GameType.CLUBS)
    private val spadesButton = createRadioButton(GameType.SPADES)
    private val heartsButton = createRadioButton(GameType.HEARTS)
    private val diamondsButton = createRadioButton(GameType.DIAMONDS)
    private val nullButton = createRadioButton(GameType.NULL)

    private val handBox = CheckBox(strings.getString("hand"))
    private val ouvertBox = CheckBox(strings.getString("ouvert"))
    private val schneiderBox = CheckBox(strings.getString("schneider"))
    private val schwarzBox = CheckBox(strings.getString("schwarz"))

    private val announceButton =
        Button(actions[JSkatAction.ANNOUNCE_GAME]?.getValue(AbstractJSkatAction.NAME) as? String ?: "")

    private var userPickedUpSkat = false

    init {
        style = "-fx-background-color: -fx-base;"
        initPanel()
    }

    private fun initPanel() {

        stylesheets.add("/org/jskat/gui/javafx/jskat.css")

        padding = Insets(10.0)
        hgap = 10.0
        vgap = 5.0
        alignment = Pos.CENTER

        add(Label(strings.getString("game")), 0, 0, 2, 1)

        add(clubsButton, 0, 1)
        add(spadesButton, 1, 1)
        add(heartsButton, 0, 2)
        add(diamondsButton, 1, 2)
        add(grandButton, 0, 3)
        add(nullButton, 1, 3)

        add(Label(strings.getString("win_levels")), 0, 4, 2, 1)

        add(handBox, 0, 5)
        add(ouvertBox, 1, 5)
        add(schneiderBox, 0, 6)
        add(schwarzBox, 1, 6)

        announceButton.graphic =
            bitmaps.getImageView(JSkatGraphicRepository.Icon.PLAY, JSkatGraphicRepository.IconSize.BIG)
        announceButton.maxWidth = Double.MAX_VALUE
        add(announceButton, 0, 7, 2, 1)

        handBox.isDisable = true

        gameTypeGroup.selectedToggleProperty().addListener { _, _, newToggle ->
            if (newToggle != null) {
                val gameType = (newToggle as RadioButton).userData as GameType
                updateGameType(gameType)
            }
        }

        ouvertBox.selectedProperty().addListener { _, _, isSelected ->
            if (isSelected && handBox.isSelected && getSelectedGameType() != null) {
                if (GameType.NULL != getSelectedGameType()) {
                    schneiderBox.isSelected = true
                    schwarzBox.isSelected = true
                }
            }
        }

        schwarzBox.selectedProperty().addListener { _, _, isSelected ->
            if (isSelected) {
                schneiderBox.isSelected = true
            }
        }

        announceButton.setOnAction {
            announceGame()
        }

        resetPanel()
    }

    private fun createRadioButton(gameType: GameType): RadioButton {
        val button = RadioButton(strings.getGameType(gameType))
        button.userData = gameType
        button.toggleGroup = gameTypeGroup
        return button
    }

    private fun updateGameType(gameType: GameType) {
        Platform.runLater {
            userPanel.setSortGameType(gameType)
        }

        if (userPickedUpSkat) {
            ouvertBox.isDisable = (gameType != GameType.NULL)
        } else {
            ouvertBox.isDisable = false
            if (gameType != GameType.NULL) {
                schneiderBox.isDisable = false
                schwarzBox.isDisable = false
                if (ouvertBox.isSelected) {
                    schneiderBox.isSelected = true
                    schwarzBox.isSelected = true
                }
            } else {
                schneiderBox.isDisable = true
                schneiderBox.isSelected = false
                schwarzBox.isDisable = true
                schwarzBox.isSelected = false
            }
        }
    }

    private fun getSelectedGameType(): GameType? {
        return gameTypeGroup.selectedToggle?.userData as? GameType
    }

    private fun announceGame() {
        val gameType = getSelectedGameType() ?: return

        val isHand = handBox.isSelected
        val isOuvert = ouvertBox.isSelected
        val isSchneider = schneiderBox.isSelected
        val isSchwarz = schwarzBox.isSelected
        val pickedUpSkat = discardPanel?.userPickedUpSkat ?: false
        val discardedCards = discardPanel?.discardedCards ?: CardList()

        try {
            var contract = org.jskat.data.GameContract(gameType)

            if (pickedUpSkat) {
                if (discardedCards.size() != 2) {
                    org.jskat.control.JSkatEventBus.INSTANCE.post(org.jskat.control.event.skatgame.InvalidNumberOfCardsInDiscardedSkatEvent())
                    return
                }

                if (GameType.NULL == gameType && isOuvert) {
                    contract = contract.withOuvert(CardList(userPanel.getHandCards()))
                }

                val announcement = GameAnnouncement(contract, discardedCards)
                fireAnnounceAction(announcement)

            } else {
                if (isHand) contract = contract.withHand()
                if (isSchneider) contract = contract.withSchneider()
                if (isSchwarz) contract = contract.withSchwarz()
                if (isOuvert) {
                    contract = contract.withOuvert(CardList(userPanel.getHandCards()))
                }

                val announcement = GameAnnouncement(contract, discardedCards)
                fireAnnounceAction(announcement)
            }
        } catch (e: Exception) {
            log.error(e.message)
        }
    }

    private fun fireAnnounceAction(announcement: GameAnnouncement) {
        actions[JSkatAction.ANNOUNCE_GAME]?.actionPerformed(JSkatActionEvent(JSkatAction.ANNOUNCE_GAME, announcement))
    }

    fun resetPanel() {
        Platform.runLater {
            gameTypeGroup.selectToggle(null)
            handBox.isSelected = true
            ouvertBox.isSelected = false
            schneiderBox.isSelected = false
            schwarzBox.isSelected = false
        }
    }

    fun setUserPickedUpSkat(isUserPickedUpSkat: Boolean) {
        Platform.runLater {
            userPickedUpSkat = isUserPickedUpSkat
            if (isUserPickedUpSkat) {
                handBox.isSelected = false
                ouvertBox.isDisable = (GameType.NULL == getSelectedGameType())
                schneiderBox.isDisable = true
                schwarzBox.isDisable = true
            } else {
                handBox.isSelected = true
                ouvertBox.isDisable = false
                schneiderBox.isDisable = true
                schwarzBox.isDisable = true
            }
        }
    }
}
