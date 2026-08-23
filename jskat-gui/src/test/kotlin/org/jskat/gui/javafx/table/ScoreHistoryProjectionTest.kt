package org.jskat.gui.javafx.table

import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.GameSummary
import org.jskat.data.SkatGameResult
import org.jskat.util.GameType
import org.jskat.util.Player
import org.junit.jupiter.api.Test

class ScoreHistoryProjectionTest {

    @Test
    fun `normal game scores are cumulative in screen player order`() {
        val projection = ScoreHistoryProjection(listOf("Upper left", "Upper right", "Lower"))

        projection.addResult(
            ScoreHistoryPlayerOrder(Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND),
            summary(declarer = Player.FOREHAND, gameValue = 24),
        )
        projection.addResult(
            ScoreHistoryPlayerOrder(Player.REARHAND, Player.FOREHAND, Player.MIDDLEHAND),
            summary(declarer = Player.MIDDLEHAND, gameValue = 20),
        )

        assertThat(projection.playerNames).containsExactly("Upper left", "Upper right", "Lower")
        assertThat(projection.rows).containsExactly(
            ScoreHistoryRow(listOf(null, null, 24), 24),
            ScoreHistoryRow(listOf(null, null, 44), 20),
        )
    }

    @Test
    fun `Ramsch scores every loser and preserves unchanged placeholders`() {
        val projection = ScoreHistoryProjection(listOf("Upper left", "Upper right", "Lower"))

        projection.addResult(
            ScoreHistoryPlayerOrder(Player.MIDDLEHAND, Player.REARHAND, Player.FOREHAND),
            summary(gameValue = -67, ramschLosers = setOf(Player.FOREHAND)),
        )
        projection.addResult(
            ScoreHistoryPlayerOrder(Player.REARHAND, Player.FOREHAND, Player.MIDDLEHAND),
            summary(gameValue = -50, ramschLosers = setOf(Player.MIDDLEHAND, Player.REARHAND)),
        )

        assertThat(projection.rows).containsExactly(
            ScoreHistoryRow(listOf(null, null, -67), -67),
            ScoreHistoryRow(listOf(-50, null, -117), -50),
        )
    }

    @Test
    fun `passed game has placeholders and can be cleared for a new series`() {
        val projection = ScoreHistoryProjection(listOf("1", "2", "3"))
        projection.setPlayerNames("Upper left", "Upper right", "Lower")
        projection.addResult(
            ScoreHistoryPlayerOrder(Player.FOREHAND, Player.MIDDLEHAND, Player.REARHAND),
            summary(gameValue = 0),
        )

        assertThat(projection.playerNames).containsExactly("Upper left", "Upper right", "Lower")
        assertThat(projection.rows).containsExactly(ScoreHistoryRow(listOf(null, null, null), 0))

        projection.clear()

        assertThat(projection.rows).isEmpty()
    }

    private fun summary(
        declarer: Player? = null,
        gameValue: Int,
        ramschLosers: Set<Player> = emptySet(),
    ): GameSummary {
        val result = SkatGameResult().apply {
            setWon(gameValue > 0)
            setGameValue(gameValue)
        }
        return GameSummary.getFactory().apply {
            setDeclarer(declarer)
            setGameType(
                when {
                    ramschLosers.isNotEmpty() -> GameType.RAMSCH
                    declarer == null -> GameType.PASSED_IN
                    else -> GameType.CLUBS
                }
            )
            setGameResult(result)
            setPlayerPoints(mapOf(Player.FOREHAND to 40, Player.MIDDLEHAND to 40, Player.REARHAND to 40))
            ramschLosers.forEach(::addRamschLooser)
        }.summary
    }
}
