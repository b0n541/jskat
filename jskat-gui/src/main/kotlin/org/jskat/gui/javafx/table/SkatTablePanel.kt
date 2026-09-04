package org.jskat.gui.javafx.table

import com.google.common.eventbus.Subscribe
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*
import org.jskat.control.JSkatEventBus
import org.jskat.control.command.table.ShowCardsCommand
import org.jskat.control.event.skatgame.*
import org.jskat.control.event.table.*
import org.jskat.control.gui.action.JSkatAction
import org.jskat.control.gui.action.JSkatActionEvent
import org.jskat.data.JSkatOptions
import org.jskat.data.SkatGameData
import org.jskat.gui.action.AbstractJSkatAction
import org.jskat.gui.action.main.StartSkatSeriesAction
import org.jskat.gui.img.JSkatGraphicRepository
import org.jskat.util.*
import org.slf4j.LoggerFactory

open class SkatTablePanel(val tableName: String, protected val actions: Map<JSkatAction, AbstractJSkatAction>) :
    BorderPane() {

    private val log = LoggerFactory.getLogger(SkatTablePanel::class.java)
    protected val strings = JSkatResourceBundle.INSTANCE
    protected val options = JSkatOptions.instance()
    protected val bitmaps = JSkatGraphicRepository.INSTANCE

    protected val playerPassed: MutableMap<Player, Boolean> = mutableMapOf()
    protected val playerNamesAndPositions: MutableMap<String, Player?> = mutableMapOf()
    protected var declarer: Player? = null

    protected lateinit var foreHand: AbstractHandPanel
    protected lateinit var middleHand: AbstractHandPanel
    protected lateinit var rearHand: AbstractHandPanel
    protected lateinit var leftOpponentPanel: OpponentPanel
    protected lateinit var rightOpponentPanel: OpponentPanel
    protected lateinit var userPanel: JSkatUserPanel
    protected lateinit var gameInfoPanel: GameInformationPanel
    private lateinit var gameContextStackPane: StackPane
    private lateinit var contextPanelStack: ContextPanelStack
    protected lateinit var trickPanel: TrickPanel
    protected lateinit var lastTrickPanel: TrickPanel
    protected lateinit var gameOverPanel: GameOverPanel
    protected lateinit var biddingPanel: BiddingContextPanel
    protected lateinit var declaringPanel: DeclaringContextPanel
    protected lateinit var schieberamschPanel: SchieberamschContextPanel

    protected var ramsch: Boolean = false
    protected var replay: Boolean = false

    init {
        JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)?.register(this)
        log.debug("SkatTablePanel created for table: $tableName")
        initPanel()
    }

    protected open fun initPanel() {
        center = getPlayGroundPanel()
    }

    protected open fun getPlayGroundPanel(): PlayGroundPanel {
        gameInfoPanel = GameInformationPanel()
        leftOpponentPanel = getOpponentPanel()
        rightOpponentPanel = getOpponentPanel()
        userPanel = createPlayerPanel()
        createGameContextStackPane()

        return PlayGroundPanel(
            gameInfoPanel, leftOpponentPanel,
            rightOpponentPanel, gameContextStackPane, userPanel
        )
    }

    protected open fun getOpponentPanel(): OpponentPanel = OpponentPanel(12, false)

    protected open fun showReplayGameButton(): Boolean = true

    protected open fun continueSeriesAction(): JSkatAction = JSkatAction.CONTINUE_LOCAL_SERIES

    protected open fun createPlayerPanel(): JSkatUserPanel = JSkatUserPanel(tableName, 12, false, actions)

    private fun createGameContextStackPane() {
        contextPanelStack = ContextPanelStack()
        gameContextStackPane = contextPanelStack.pane
        // gameContextStackPane.isOpaque = false // Removed

        val startSkatSeriesAction = actions[JSkatAction.START_LOCAL_SERIES] as StartSkatSeriesAction
        val startPanel = StartContextPanel(startSkatSeriesAction)
        addContextPanel(ContextPanelType.START, startPanel)

        biddingPanel = BiddingContextPanel(actions, bitmaps, userPanel)
        addContextPanel(ContextPanelType.BIDDING, biddingPanel)

        declaringPanel = DeclaringContextPanel(tableName, actions, userPanel)
        addContextPanel(ContextPanelType.DECLARING, declaringPanel)

        schieberamschPanel = SchieberamschContextPanel(tableName, actions, userPanel, 4)
        addContextPanel(ContextPanelType.SCHIEBERAMSCH, schieberamschPanel)

        addContextPanel(ContextPanelType.RE_AFTER_CONTRA, createCallReAfterContraPanel())

        val trickHoldingPanel = HBox()
        // trickHoldingPanel.isOpaque = false // Removed
        lastTrickPanel = TrickPanel(0.6, false)
        lastTrickPanel.prefWidth = 0.0
        HBox.setHgrow(lastTrickPanel, Priority.ALWAYS)
        trickHoldingPanel.children.add(lastTrickPanel)

        trickPanel = TrickPanel(0.8, true)
        trickPanel.prefWidth = 0.0
        HBox.setHgrow(trickPanel, Priority.ALWAYS)
        trickHoldingPanel.children.add(trickPanel)

        val rightPanel = getRightPanelForTrickPanel()
        rightPanel.prefWidth = 0.0
        HBox.setHgrow(rightPanel, Priority.ALWAYS)
        trickHoldingPanel.children.add(rightPanel)

        addContextPanel(ContextPanelType.TRICK_PLAYING, trickHoldingPanel)

        gameOverPanel = GameOverPanel(tableName, actions, showReplayGameButton(), continueSeriesAction())
        addContextPanel(ContextPanelType.GAME_OVER, gameOverPanel)

        setContextPanel(ContextPanelType.START)
    }

    protected fun addContextPanel(panelType: ContextPanelType, panel: Node) {
        contextPanelStack.add(panelType, panel)
    }

    private fun createCallReAfterContraPanel(): Node {
        val result = VBox(10.0)
        result.alignment = Pos.CENTER

        val question = HBox(10.0)
        question.alignment = Pos.CENTER
        val questionIconLabel =
            Label("", bitmaps.getImageView(JSkatGraphicRepository.Icon.USER_INFO, JSkatGraphicRepository.IconSize.BIG))
        val questionLabel = Label(strings.getString("want_call_re_after_contra"))
        question.children.addAll(questionIconLabel, questionLabel)
        result.children.add(question)

        val buttonBox = HBox(10.0)
        buttonBox.alignment = Pos.CENTER
        val callReAction = actions[JSkatAction.CALL_RE]
        if (callReAction != null) {
            val callReButton =
                Button(callReAction.getValue(AbstractJSkatAction.NAME) as? String ?: JSkatAction.CALL_RE.name)
            callReButton.graphic =
                bitmaps.getImageView(JSkatGraphicRepository.Icon.OK, JSkatGraphicRepository.IconSize.BIG)
            callReButton.setOnAction {
                callReAction.actionPerformed(JSkatActionEvent(JSkatAction.CALL_RE, true))
            }
            buttonBox.children.add(callReButton)

            val noReButton = Button(strings.getString("no"))
            noReButton.graphic =
                bitmaps.getImageView(JSkatGraphicRepository.Icon.STOP, JSkatGraphicRepository.IconSize.BIG)
            noReButton.setOnAction {
                callReAction.actionPerformed(JSkatActionEvent(JSkatAction.CALL_RE, false))
            }
            buttonBox.children.add(noReButton)
        }

        result.children.add(buttonBox)
        return result
    }

    protected open fun getRightPanelForTrickPanel(): Pane {
        val additionalActionsPanel = VBox()
        additionalActionsPanel.alignment = Pos.CENTER

        val contraAction = actions[JSkatAction.CALL_CONTRA]
        if (options.isPlayContra && contraAction != null) {
            val contraButton =
                Button(
                    contraAction.getValue(AbstractJSkatAction.NAME) as? String ?: JSkatAction.CALL_CONTRA.name
                ).apply {
                    setOnAction {
                        contraAction.actionPerformed(JSkatActionEvent(JSkatAction.CALL_CONTRA, it.source))
                    }
                    graphic = JSkatGraphicRepository.INSTANCE.getImageView(
                        contraAction.icon, JSkatGraphicRepository.IconSize.BIG
                    )
                    alignment = Pos.CENTER
                }
            additionalActionsPanel.children.add(contraButton)
        }

        return additionalActionsPanel
    }

    @Subscribe
    fun setReplayModeOn(event: SkatGameReplayStartedEvent) {
        replay = true
    }

    @Subscribe
    fun setReplayModeOff(event: SkatGameReplayFinishedEvent) {
        replay = false
    }

    // TODO: this does similar things like IssTablePanel.resetTableOn(event: IssTableGameStartedEvent)
    @Subscribe
    fun resetTableOn(event: GameStartedEvent) {
        Platform.runLater { resetTable(event) }
    }

    protected fun resetTable(event: GameStartedEvent) {
        gameInfoPanel.setGameState(SkatGameData.GameState.GAME_START)
        gameInfoPanel.setGameNumber(event.gameNo)

        leftOpponentPanel.position = event.leftPlayerPosition
        rightOpponentPanel.position = event.rightPlayerPosition
        userPanel.position = event.userPosition

        biddingPanel.setUserPosition(event.userPosition)
        trickPanel.setUserPosition(event.userPosition)
        lastTrickPanel.setUserPosition(event.userPosition)
        gameOverPanel.setUserPosition(event.userPosition)

        when (event.userPosition) {
            Player.FOREHAND -> {
                foreHand = userPanel
                middleHand = leftOpponentPanel
                rearHand = rightOpponentPanel
            }

            Player.MIDDLEHAND -> {
                foreHand = rightOpponentPanel
                middleHand = userPanel
                rearHand = leftOpponentPanel
            }

            Player.REARHAND -> {
                foreHand = leftOpponentPanel
                middleHand = rightOpponentPanel
                rearHand = userPanel
            }
        }

        clearTable()
    }

    protected fun clearTable() {
        gameInfoPanel.clear()
        biddingPanel.resetPanel()
        declaringPanel.resetPanel()
        gameOverPanel.resetPanel()
        schieberamschPanel.resetPanel()
        Player.entries.forEach { clearHand(it) }
        trickPanel.clearCards()
        lastTrickPanel.clearCards()
        listOf(leftOpponentPanel, rightOpponentPanel, userPanel).forEach { it.setSortGameType(GameType.GRAND) }
        resetGameData()
    }

    @Subscribe
    fun setDealtCardsOn(event: CardDealEvent) {
        Platform.runLater { setCardsForPlayers(event.playerCards) }
    }

    @Subscribe
    fun handleTrickCompleted(event: TrickCompletedEvent) {
        Platform.runLater {
            lastTrickPanel.clearCards()
            val trick = event.trick
            lastTrickPanel.addCard(trick.foreHand, trick.firstCard)
            lastTrickPanel.addCard(trick.middleHand, trick.secondCard)
            lastTrickPanel.addCard(trick.rearHand, trick.thirdCard)
            trickPanel.clearCards()
            setTrickNumber(trick.trickNumberInGame + 2)
        }
    }

    @Subscribe
    fun handleTrickCardPlayed(event: TrickCardPlayedEvent) {
        Platform.runLater {
            getHandPanel(event.player).removeCard(event.card)
            trickPanel.addCard(event.player, event.card)
        }
    }

    @Subscribe
    fun setGameAnnouncementOn(event: GameAnnouncementEvent) {
        Platform.runLater {
            val announcement = event.announcement
            gameInfoPanel.setGameContract(announcement.contract())

            listOf(leftOpponentPanel, rightOpponentPanel, userPanel).forEach {
                it.setSortGameType(announcement.contract().gameType())
            }

            if (announcement.contract().gameType() !in listOf(GameType.PASSED_IN, GameType.RAMSCH)) {
                getHandPanel(event.player).declarer = true
            }

            if (announcement.contract().ouvert()) {
                val declarerPanel = getHandPanel(event.player)
                declarerPanel.removeAllCards()
                declarerPanel.addCards(announcement.contract().ouvertCards())
                declarerPanel.showCards()
            }
        }
    }

    @Subscribe
    private fun setGameStateOn(event: SkatGameStateChangedEvent) {
        log.info("New game state: {}", event.gameState)
        Platform.runLater {
            gameInfoPanel.setGameState(event.gameState)
            userPanel.gameState = event.gameState

            when (event.gameState) {
                SkatGameData.GameState.GAME_START -> {
                    setContextPanel(ContextPanelType.START)
                    resetGameData()
                }

                SkatGameData.GameState.DEALING -> setContextPanel(ContextPanelType.START)
                SkatGameData.GameState.BIDDING -> setContextPanel(ContextPanelType.BIDDING)
                SkatGameData.GameState.RAMSCH_GRAND_HAND_ANNOUNCING, SkatGameData.GameState.SCHIEBERAMSCH -> {
                    setContextPanel(ContextPanelType.SCHIEBERAMSCH)
                    ramsch = true
                }

                SkatGameData.GameState.PICKING_UP_SKAT, SkatGameData.GameState.DISCARDING, SkatGameData.GameState.DECLARING -> {
                    if (userPanel.position == declarer) {
                        setContextPanel(ContextPanelType.DECLARING)
                    }
                }

                SkatGameData.GameState.RE -> setContextPanel(ContextPanelType.RE_AFTER_CONTRA)
                SkatGameData.GameState.CONTRA -> {
                    // Handle CONTRA state if needed
                }

                SkatGameData.GameState.TRICK_PLAYING -> setContextPanel(ContextPanelType.TRICK_PLAYING)
                SkatGameData.GameState.CALCULATING_GAME_VALUE, SkatGameData.GameState.PRELIMINARY_GAME_END, SkatGameData.GameState.GAME_OVER -> {
                    setContextPanel(ContextPanelType.GAME_OVER)
                    listOf(foreHand, middleHand, rearHand).forEach { it.isActivePlayer = false }
                }
            }
        }
    }

    private fun resetGameData() {
        Player.entries.forEach { playerPassed[it] = false }
        ramsch = false
        declarer = null
    }

    protected fun setContextPanel(panelType: ContextPanelType) {
        contextPanelStack.show(panelType)
    }

    @Subscribe
    fun addGameResultOn(event: GameFinishEvent) {
        Platform.runLater {
            gameOverPanel.setGameSummary(event.gameSummary)
            gameInfoPanel.setGameSummary(event.gameSummary)
        }
    }

    @Subscribe
    fun setBidValueOn(event: BidEvent) {
        log.debug("${event.player} bids: ${event.bid}")
        Platform.runLater {
            setBidValue(event)
            biddingPanel.setBidValueToHold(event.bid)
        }
    }

    @Subscribe
    fun setBidValueOn(event: HoldBidEvent) {
        log.debug("${event.player} holds: ${event.bid}")
        Platform.runLater {
            setBidValue(event)
            biddingPanel.setNextBidValue(SkatConstants.getNextBidValue(event.bid))
        }
    }

    private fun setBidValue(event: AbstractBidEvent) {
        biddingPanel.setBid(event.player, event.bid)
        getHandPanel(event.player).bidValue = event.bid
    }

    @Subscribe
    fun setPassOn(event: PassBidEvent) {
        log.debug("${event.player} passes, next bid: ${event.nextBidValue}")
        Platform.runLater {
            biddingPanel.setNextBidValue(event.nextBidValue)
            playerPassed[event.player] = true
            getHandPanel(event.player).playerPassed = true
            biddingPanel.setPass(event.player)
        }
    }

    @Subscribe
    fun setSkatOn(event: SkatCardsPickedUpEvent) {
        Platform.runLater { setSkat(event.cards) }
    }

    @Subscribe
    fun setSkatOn(event: SkatCardsChangedEvent) {
        Platform.runLater { setSkat(event.cards) }
    }

    open fun setSkat(skat: CardList) {
        if (ramsch) schieberamschPanel.setSkat(skat) else declaringPanel.setSkat(skat)
    }

    @Subscribe
    fun takeCardFromSkatOn(event: SkatCardTakenEvent) =
        Platform.runLater { takeCardFromSkat(userPanel, event.card) }

    fun takeCardFromSkat(player: Player, card: Card) {
        getHandPanel(player).let { takeCardFromSkat(it, card) }
    }

    private fun takeCardFromSkat(panel: AbstractHandPanel, card: Card) {
        if (!panel.isHandFull()) {
            declaringPanel.removeCard(card)
            schieberamschPanel.removeCard(card)
            panel.addCard(card)
        } else {
            log.warn("Player panel is full, cannot take card from skat.")
        }
    }

    @Subscribe
    fun putCardIntoSkatOn(event: SkatCardPutEvent) = Platform.runLater { putCardIntoSkat(userPanel, event.card) }

    fun putCardIntoSkat(player: Player, card: Card) {
        getHandPanel(player).let { putCardIntoSkat(it, card) }
    }

    private fun putCardIntoSkat(panel: AbstractHandPanel, card: Card) {
        if (!declaringPanel.isHandFull() && !schieberamschPanel.isHandFull()) {
            panel.removeCard(card)
            declaringPanel.addCard(card)
            schieberamschPanel.addCard(card)
        } else {
            log.warn("Discard panel is full, cannot put card into skat.")
        }
    }

    @Subscribe
    fun setPlayerNamesOn(event: PlayerNamesChangedEvent) {
        Platform.runLater {
            leftOpponentPanel.playerName = event.upperLeftPlayerName
            leftOpponentPanel.isAIPlayer = event.isUpperLeftPlayerAIPlayer
            rightOpponentPanel.playerName = event.upperRightPlayerName
            rightOpponentPanel.isAIPlayer = event.isUpperRightPlayerAIPlayer
            userPanel.playerName = event.lowerPlayerName
            userPanel.isAIPlayer = event.isLowerPlayerAIPlayer
        }
    }

    @Subscribe
    fun setDeclarerOn(event: DeclarerChangedEvent) {
        log.info("New declarer: {}", event.declarer)
        declarer = event.declarer
        Platform.runLater {
            Player.entries.forEach { player ->
                getHandPanel(player).declarer = (player == event.declarer)
            }
        }
    }

    @Subscribe
    fun showCardsOn(command: ShowCardsCommand) {
        Platform.runLater {
            setCardsForPlayers(command.cards)
            Player.entries.forEach { showCards(it) }
            gameOverPanel.setDealtSkat(command.skat)
        }
    }

    private fun setCardsForPlayers(cards: Map<Player, CardList>) {
        cards.forEach { (player, cardList) ->
            val panel = getHandPanel(player)
            panel.removeAllCards()
            panel.addCards(cardList)
            if (replay) {
                showCards(player)
            }
        }
    }

    @Subscribe
    fun setContraOn(event: ContraEvent) {
        Platform.runLater {
            getHandPanel(event.player).setContra()
            gameInfoPanel.setContra()
        }
    }

    @Subscribe
    fun setReOn(event: ReEvent) {
        Platform.runLater {
            getHandPanel(event.player).setRe()
            gameInfoPanel.setRe()
        }
    }

    @Subscribe
    fun setActivePlayerOn(event: ActivePlayerChangedEvent) {
        Platform.runLater {
            foreHand.isActivePlayer = (event.player == Player.FOREHAND)
            middleHand.isActivePlayer = (event.player == Player.MIDDLEHAND)
            rearHand.isActivePlayer = (event.player == Player.REARHAND)
        }
    }

    protected fun getHandPanel(player: Player): AbstractHandPanel = when (player) {
        Player.FOREHAND -> foreHand
        Player.MIDDLEHAND -> middleHand
        Player.REARHAND -> rearHand
    }

    protected fun getHandPanel(playerName: String): AbstractHandPanel? {
        return when (playerName) {
            userPanel.playerName -> userPanel
            leftOpponentPanel.playerName -> leftOpponentPanel
            rightOpponentPanel.playerName -> rightOpponentPanel
            else -> null
        }
    }

    fun clearHand(player: Player) = getHandPanel(player).clearHandPanel()
    fun showCards(player: Player) = getHandPanel(player).showCards()
    fun setTrickNumber(trickNumber: Int) = gameInfoPanel.setTrickNumber(trickNumber)

    fun setResign(player: Player) {
        runOnFxThread { getHandPanel(player).setResign(true) }
    }

    fun setGeschoben(player: Player) {
        runOnFxThread { getHandPanel(player).setGeschoben() }
    }

    fun setPlayerTime(player: Player, time: Double) {
        runOnFxThread { getHandPanel(player).setPlayerTime(time) }
    }

    fun setDiscardedSkat(player: Player, skatBefore: CardList, discardedSkat: CardList) {
        Platform.runLater {
            for (card in skatBefore) {
                takeCardFromSkat(player, card)
            }
            for (card in discardedSkat) {
                putCardIntoSkat(player, card)
            }
        }
    }

    fun hideCards(player: Player) {
        runOnFxThread { getHandPanel(player).hideCards() }
    }

    private fun runOnFxThread(action: () -> Unit) {
        if (Platform.isFxApplicationThread()) action() else Platform.runLater(action)
    }
}
