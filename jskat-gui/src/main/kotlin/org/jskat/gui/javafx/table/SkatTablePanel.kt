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
    private lateinit var gameContextStackPane: Pane
    private lateinit var contextCompositionHost: ContextCompositionHost
    private lateinit var contextPanelStack: ContextPanelStack
    protected lateinit var trickPanel: TrickPanel
    protected lateinit var lastTrickPanel: TrickPanel
    protected lateinit var gameOverPanel: GameOverPanel
    protected lateinit var biddingPanel: BiddingContextPanel
    protected lateinit var declaringPanel: DeclaringContextPanel
    protected lateinit var schieberamschPanel: SchieberamschContextPanel

    protected var ramsch: Boolean = false
    protected var replay: Boolean = false
    private var contextGameState = SkatGameData.GameState.GAME_START

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

    protected open fun contextMode(): ContextMode = ContextMode.LOCAL

    protected open fun continueSeriesAction(): JSkatAction = JSkatAction.CONTINUE_LOCAL_SERIES

    protected open fun gameOverAdditionalAction(): JSkatAction? = null

    protected open fun createPlayerPanel(): JSkatUserPanel = JSkatUserPanel(tableName, 12, false, actions)

    private fun createGameContextStackPane() {
        contextCompositionHost = ContextCompositionHost()
        contextPanelStack = contextCompositionHost.contextPanelStack
        gameContextStackPane = contextCompositionHost.pane
        // gameContextStackPane.isOpaque = false // Removed

        val startSkatSeriesAction = actions[JSkatAction.START_LOCAL_SERIES] as StartSkatSeriesAction
        val startPanel = StartContextPanel(startSkatSeriesAction)
        addContextPanel(ContextPanelType.START, startPanel)
        registerSharedAction(ContextPanelType.START, JSkatAction.START_LOCAL_SERIES, startPanel.actionControl)

        biddingPanel = BiddingContextPanel(actions, bitmaps, userPanel)
        addContextPanel(ContextPanelType.BIDDING, biddingPanel)
        registerSharedAction(ContextPanelType.BIDDING, JSkatAction.MAKE_BID, biddingPanel.bidActionControl)
        registerSharedAction(ContextPanelType.BIDDING, JSkatAction.HOLD_BID, biddingPanel.bidActionControl)
        registerSharedAction(ContextPanelType.BIDDING, JSkatAction.PASS_BID, biddingPanel.passActionControl)

        declaringPanel = DeclaringContextPanel(tableName, actions, userPanel)
        addContextPanel(ContextPanelType.DECLARING, declaringPanel)
        contextCompositionHost.registerLowerLeftContent(
            LowerLeftContent.REPLAY_SKAT,
            declaringPanel.replaySkatPanel,
            declaringPanel::setReplaySkatPresentation
        )
        registerSharedAction(ContextPanelType.DECLARING, JSkatAction.ANNOUNCE_GAME, declaringPanel.announceActionControl)
        registerSharedAction(ContextPanelType.DECLARING, JSkatAction.PICK_UP_SKAT, declaringPanel.pickUpActionControl)

        schieberamschPanel = SchieberamschContextPanel(tableName, actions, userPanel, 4)
        addContextPanel(ContextPanelType.SCHIEBERAMSCH, schieberamschPanel)
        registerSharedAction(ContextPanelType.SCHIEBERAMSCH, JSkatAction.PLAY_GRAND_HAND, schieberamschPanel.grandHandActionControl)
        registerSharedAction(ContextPanelType.SCHIEBERAMSCH, JSkatAction.PLAY_SCHIEBERAMSCH, schieberamschPanel.schieberamschActionControl)
        registerSharedAction(ContextPanelType.SCHIEBERAMSCH, JSkatAction.SCHIEBEN, schieberamschPanel.schiebenActionControl)
        registerSharedAction(ContextPanelType.SCHIEBERAMSCH, JSkatAction.PICK_UP_SKAT, schieberamschPanel.pickUpActionControl)

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

        gameOverPanel = GameOverPanel(
            tableName,
            actions,
            showReplayGameButton(),
            continueSeriesAction(),
            gameOverAdditionalAction(),
            moveCommandsToSharedActionArea = contextMode() == ContextMode.LOCAL
        )
        addContextPanel(ContextPanelType.GAME_OVER, gameOverPanel)
        contextCompositionHost.registerLowerLeftContent(
            LowerLeftContent.GAME_OVER_SKAT,
            gameOverPanel.replaySkatPanel
        )
        registerSharedActionIndependentOfPhase(JSkatAction.REPLAY_GAME, createReplayActionControl(JSkatAction.REPLAY_GAME))
        registerSharedAction(ContextPanelType.GAME_OVER, continueSeriesAction(), gameOverPanel.actionControl(continueSeriesAction()))
        registerSharedActionIndependentOfPhase(
            JSkatAction.NEXT_REPLAY_STEP,
            createReplayActionControl(JSkatAction.NEXT_REPLAY_STEP, bindEnabledState = true)
        )

        setContextPanel(ContextPanelType.START)
    }

    protected fun addContextPanel(panelType: ContextPanelType, panel: Node) {
        contextPanelStack.add(panelType, panel)
    }

    protected fun registerSharedAction(phaseContent: ContextPanelType, action: JSkatAction, control: Node?) {
        if (control != null) {
            contextCompositionHost.sharedActionArea.register(phaseContent, action, control)
        }
    }

    private fun registerSharedActionIndependentOfPhase(action: JSkatAction, control: Node?) {
        if (control != null) {
            contextCompositionHost.sharedActionArea.registerIndependentOfPhase(action, control)
        }
    }

    private fun createReplayActionControl(actionType: JSkatAction, bindEnabledState: Boolean = false): Button? =
        actions[actionType]?.let { action ->
            BigActionButton.create(
                action.getValue(AbstractJSkatAction.NAME) as? String ?: actionType.name,
                action.icon,
                action.enabledProperty().takeIf { bindEnabledState }
            ) { BigActionButton.dispatchTableCommand(action, tableName, it.source) }
        }

    private fun createCallReAfterContraPanel(): Node {
        val result = VBox(10.0)
        result.alignment = Pos.CENTER

        val question = HBox(10.0)
        question.alignment = Pos.CENTER
        val questionIconLabel =
            Label("", bitmaps.getImageView(JSkatGraphicRepository.Icon.USER_INFO, JSkatGraphicRepository.IconSize.BIG))
        val questionLabel = Label(strings.getString("wantCallReAfterContra"))
        question.children.addAll(questionIconLabel, questionLabel)
        result.children.add(question)

        val buttonBox = HBox(10.0)
        buttonBox.alignment = Pos.CENTER
        val callReAction = actions[JSkatAction.CALL_RE]
        if (callReAction != null) {
            val callReButton = BigActionButton.create(
                callReAction.getValue(AbstractJSkatAction.NAME) as? String ?: JSkatAction.CALL_RE.name,
                JSkatGraphicRepository.Icon.OK
            ) {
                BigActionButton.dispatch(callReAction, JSkatAction.CALL_RE, true)
            }
            buttonBox.children.add(callReButton)

            val noReButton = BigActionButton.create(
                strings.getString("no"),
                JSkatGraphicRepository.Icon.STOP
            ) {
                BigActionButton.dispatch(callReAction, JSkatAction.CALL_RE, false)
            }
            registerSharedAction(ContextPanelType.RE_AFTER_CONTRA, JSkatAction.CALL_RE, callReButton)
            registerSharedAction(ContextPanelType.RE_AFTER_CONTRA, JSkatAction.CALL_RE, noReButton)
        }

        result.children.add(buttonBox)
        return result
    }

    protected open fun getRightPanelForTrickPanel(): Pane {
        val additionalActionsPanel = VBox()
        additionalActionsPanel.alignment = Pos.CENTER

        val contraAction = actions[JSkatAction.CALL_CONTRA]
        if (options.isPlayContra && contraAction != null) {
            val contraButton = BigActionButton.create(
                contraAction.getValue(AbstractJSkatAction.NAME) as? String ?: JSkatAction.CALL_CONTRA.name,
                contraAction.icon
            ) {
                BigActionButton.dispatch(contraAction, JSkatAction.CALL_CONTRA, it.source)
            }.apply { alignment = Pos.CENTER }
            additionalActionsPanel.children.add(contraButton)
            registerSharedAction(ContextPanelType.TRICK_PLAYING, JSkatAction.CALL_CONTRA, contraButton)
        }

        return additionalActionsPanel
    }

    @Subscribe
    fun setReplayModeOn(event: SkatGameReplayStartedEvent) {
        replay = true
        Platform.runLater {
            declaringPanel.resetPanel()
            contextGameState = SkatGameData.GameState.GAME_START
            renderContext()
        }
    }

    @Subscribe
    fun setReplayModeOff(event: SkatGameReplayFinishedEvent) {
        replay = false
        Platform.runLater {
            declaringPanel.resetPanel()
            renderContext()
        }
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
        contextCompositionHost.clearLowerLeftContent()
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
    fun replayPickedUpSkatOn(event: PickUpSkatEvent) {
        if (replay) {
            Platform.runLater {
                setSkat(event.pickedUpSkat)
                getHandPanel(event.player).addCards(event.pickedUpSkat)
            }
        }
    }

    @Subscribe
    fun replayDiscardedSkatOn(event: DiscardSkatEvent) {
        if (replay) {
            Platform.runLater {
                event.discardedSkat.forEach { getHandPanel(event.player).removeCard(it) }
                setSkat(event.discardedSkat)
            }
        }
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

            contextGameState = event.gameState
            renderContext()

            when (event.gameState) {
                SkatGameData.GameState.GAME_START -> {
                    resetGameData()
                }

                SkatGameData.GameState.RAMSCH_GRAND_HAND_ANNOUNCING, SkatGameData.GameState.SCHIEBERAMSCH -> {
                    ramsch = true
                }

                SkatGameData.GameState.PICKING_UP_SKAT, SkatGameData.GameState.DISCARDING, SkatGameData.GameState.DECLARING -> {
                    if (userPanel.position == declarer) {
                        declaringPanel.preselectGameTypeIfUnset(biddingPanel.selectedGameType())
                    }
                }

                SkatGameData.GameState.CALCULATING_GAME_VALUE, SkatGameData.GameState.PRELIMINARY_GAME_END, SkatGameData.GameState.GAME_OVER -> {
                    listOf(foreHand, middleHand, rearHand).forEach { it.isActivePlayer = false }
                }

                else -> Unit
            }
        }
    }

    private fun renderContext() {
        val state = ContextRenderingState(
            contextMode(),
            contextGameState,
            replay,
            userPanel.position == declarer,
            options.isPlayContra
        )
        val projection = SharedContextRenderer.project(state)
        contextCompositionHost.render(projection)
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
