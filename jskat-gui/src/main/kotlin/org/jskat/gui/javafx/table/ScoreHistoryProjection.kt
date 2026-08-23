package org.jskat.gui.javafx.table

import org.jskat.data.GameSummary
import org.jskat.util.Player

data class ScoreHistoryRow(val playerTotals: List<Int?>, val gameValue: Int)

data class ScoreHistoryPlayerOrder(val leftOpponent: Player, val rightOpponent: Player, val user: Player) {
    val players: List<Player> = listOf(leftOpponent, rightOpponent, user)
}

class ScoreHistoryProjection(initialPlayerNames: List<String>) {

    var playerNames: List<String> = initialPlayerNames.toList()
        private set

    private val totals = MutableList(PLAYER_COUNT) { 0 }
    private val projectedRows = mutableListOf<ScoreHistoryRow>()

    val rows: List<ScoreHistoryRow>
        get() = projectedRows

    init {
        require(initialPlayerNames.size == PLAYER_COUNT) { "Exactly three player names are required." }
    }

    fun addResult(playerOrder: ScoreHistoryPlayerOrder, gameSummary: GameSummary) {
        val changedPlayers = when {
            gameSummary.declarer != null -> setOf(gameSummary.declarer)
            gameSummary.ramschLosers.isNotEmpty() -> gameSummary.ramschLosers
            else -> emptySet()
        }
        val values = playerOrder.players.mapIndexed { index, player ->
            if (player in changedPlayers) {
                totals[index] += gameSummary.gameValue
                totals[index]
            } else {
                null
            }
        }
        projectedRows += ScoreHistoryRow(values, gameSummary.gameValue)
    }

    fun setPlayerNames(upperLeftPlayer: String, upperRightPlayer: String, lowerPlayer: String) {
        playerNames = listOf(upperLeftPlayer, upperRightPlayer, lowerPlayer)
    }

    fun clear() {
        totals.fill(0)
        projectedRows.clear()
    }

    companion object {
        private const val PLAYER_COUNT = 3
    }
}
