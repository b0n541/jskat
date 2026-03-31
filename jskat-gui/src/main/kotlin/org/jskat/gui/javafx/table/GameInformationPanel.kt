package org.jskat.gui.javafx.table

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.OverrunStyle
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import org.jskat.data.GameContract
import org.jskat.data.GameSummary
import org.jskat.data.SkatGameData.GameState
import org.jskat.util.GameType
import org.jskat.util.JSkatResourceBundle
import org.jskat.util.Player

class GameInformationPanel : HBox() {

    private val strings = JSkatResourceBundle.INSTANCE
    private val label = Label()

    private var gameNumber: Int = 0
    private var gameState: GameState = GameState.GAME_START
    private var gameType: GameType? = null
    private var playWithJacks: Boolean = false
    private var matadors: Int = 0
    private var handGame: Boolean = false
    private var ouvertGame: Boolean = false
    private var schneiderAnnounced: Boolean = false
    private var schneider: Boolean = false
    private var schwarzAnnounced: Boolean = false
    private var schwarz: Boolean = false
    private var overBid: Boolean = false
    private var contra: Boolean = false
    private var re: Boolean = false
    private var trick: Int = 1
    private var gameWon: Boolean = false
    private var declarerPoints: Int = 0
    private var opponentPoints: Int = 0
    private var ramschLosers: Set<Player> = HashSet()

    init {
        style = "-fx-background-color: #E2D9CA;"
        minHeight = 25.0
        maxHeight = 25.0
        minWidth = 0.0
        alignment = Pos.CENTER
        
        label.font = Font.font("Dialog", FontWeight.BOLD, 16.0)
        label.alignment = Pos.CENTER
        label.maxWidth = Double.MAX_VALUE
        label.minWidth = 0.0
        label.textOverrun = OverrunStyle.ELLIPSIS
        
        children.add(label)
        setHgrow(label, Priority.ALWAYS)

        setGameState(GameState.GAME_START)
    }

    fun clear() {
        label.text = " "
    }

    fun setGameState(newGameState: GameState) {
        gameState = newGameState
        if (gameState == GameState.GAME_START) {
            resetGameData()
        }
        refreshText()
    }

    fun setGameContract(contract: GameContract) {
        gameType = contract.gameType()
        handGame = contract.hand()
        ouvertGame = contract.ouvert()
        schneiderAnnounced = contract.schneider()
        schwarzAnnounced = contract.schwarz()
        refreshText()
    }

    private fun resetGameData() {
        gameType = null
        matadors = 0
        playWithJacks = false
        handGame = false
        ouvertGame = false
        schneiderAnnounced = false
        schneider = false
        schwarzAnnounced = false
        schwarz = false
        overBid = false
        contra = false
        re = false
        trick = 1
        gameWon = false
        declarerPoints = 0
        opponentPoints = 0
        ramschLosers = HashSet()
    }

    private fun refreshText() {
        val text = StringBuilder()
        appendGameNumber(text)
        text.append(getGameStateString(gameState))
        appendGameType(text)
        appendGameStateDetails(text)
        label.text = text.toString()
    }

    private fun appendGameStateDetails(text: StringBuilder) {
        if (gameState == GameState.TRICK_PLAYING) {
            appendTrickPlayingDetails(text)
        } else if (gameState == GameState.GAME_OVER) {
            appendGameOverDetails(text)
        }
    }

    private fun appendGameOverDetails(text: StringBuilder) {
        if (gameType != GameType.PASSED_IN) {
            text.append(" - ")
            if (gameWon) {
                text.append(strings.getString("won"))
            } else {
                text.append(strings.getString("lost"))
            }
        }

        if (gameType == GameType.RAMSCH) {
            text.append(" - ")
            val iterator = ramschLosers.iterator()
            if (iterator.hasNext()) {
                text.append(strings.getPlayerString(iterator.next()))
            }
            while (iterator.hasNext()) {
                text.append(", ")
                text.append(strings.getPlayerString(iterator.next()))
            }
        } else if (gameType != GameType.NULL && gameType != GameType.PASSED_IN) {
            text.append(" - ")
            text.append("$declarerPoints ${strings.getString("versus")} ")
            text.append("$opponentPoints ${strings.getString("points")}")
        }
    }

    private fun appendTrickPlayingDetails(text: StringBuilder) {
        text.append(" ${strings.getString("trick")} $trick")
    }

    private fun appendGameType(text: StringBuilder) {
        if (gameType != null) {
            text.append(" [${strings.getGameType(gameType)}")

            if (gameState == GameState.GAME_OVER && matadors > 0) {
                if (playWithJacks) {
                    text.append(" ${strings.getString("with")}")
                } else {
                    text.append(" ${strings.getString("without")}")
                }
                text.append(" $matadors")
                text.append(" ${strings.getString("play")}")
                text.append(" ${matadors + 1}")
            }

            if (handGame) {
                text.append(" ${strings.getString("hand")}")
            }
            if (ouvertGame) {
                text.append(" ${strings.getString("ouvert")}")
            }
            if (schneiderAnnounced) {
                text.append(" ${strings.getString("schneider_announced")}")
            } else if (schneider) {
                text.append(" ${strings.getString("schneider")}")
            }
            if (schwarzAnnounced) {
                text.append(" ${strings.getString("schwarz_announced")}")
            } else if (schwarz) {
                text.append(" ${strings.getString("schwarz")}")
            }
            if (contra) {
                text.append(" ${strings.getString("contra")}")
            }
            if (re) {
                text.append(" ${strings.getString("re")}")
            }
            if (overBid) {
                text.append(" ${strings.getString("overbidded")}")
            }
            text.append("]")
        }
    }

    private fun appendGameNumber(text: StringBuilder) {
        if (gameNumber > 0) {
            text.append("${strings.getString("game")} $gameNumber: ")
        }
    }

    fun setGameSummary(summary: GameSummary) {
        playWithJacks = summary.isGamePlayedWithJacks
        matadors = summary.matadors
        gameWon = summary.isGameWon
        declarerPoints = summary.finalDeclarerPoints
        opponentPoints = summary.finalOpponentScore
        ramschLosers = summary.ramschLosers
        overBid = summary.gameResult.overBid
        schneider = summary.gameResult.isSchneider
        schwarz = summary.gameResult.isSchwarz
        refreshText()
    }

    private fun getGameStateString(state: GameState): String {
        return when (state) {
            GameState.BIDDING -> strings.getString("bidding_phase")
            GameState.CALCULATING_GAME_VALUE -> strings.getString("calc_game_value_phase")
            GameState.DEALING -> strings.getString("dealing_phase")
            GameState.DECLARING -> strings.getString("declaring_phase")
            GameState.DISCARDING -> strings.getString("discarding_phase")
            GameState.GAME_OVER -> strings.getString("game_over_phase")
            GameState.GAME_START -> strings.getString("game_start_phase")
            GameState.PICKING_UP_SKAT, GameState.RAMSCH_GRAND_HAND_ANNOUNCING, GameState.SCHIEBERAMSCH -> strings.getString("pick_up_skat_phase")
            GameState.PRELIMINARY_GAME_END -> strings.getString("preliminary_game_end_phase")
            GameState.TRICK_PLAYING -> strings.getString("trick_playing_phase")
            GameState.CONTRA -> strings.getString("contra_or_play_phase")
            GameState.RE -> strings.getString("re_or_play_phase")
            else -> state.name
        }
    }

    fun setTrickNumber(trickNumber: Int) {
        trick = trickNumber
        refreshText()
    }

    fun setGameNumber(newGameNumber: Int) {
        gameNumber = newGameNumber
        refreshText()
    }

    fun setContra() {
        contra = true
        refreshText()
    }

    fun setRe() {
        re = true
        refreshText()
    }
}
