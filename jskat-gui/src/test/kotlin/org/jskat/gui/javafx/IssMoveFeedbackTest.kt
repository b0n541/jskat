package org.jskat.gui.javafx

import javafx.scene.control.ButtonType
import org.assertj.core.api.Assertions.assertThat
import org.jskat.data.SkatGameData
import org.jskat.data.iss.MoveInformation
import org.jskat.data.iss.MovePlayer
import org.jskat.util.Player
import org.junit.jupiter.api.Test
import java.util.Optional

class IssMoveFeedbackTest {

    @Test
    fun `translates an ISS-issued time-out into clock updates for every player`() {
        val move = MoveInformation().apply {
            movePlayer = MovePlayer.WORLD
            type = org.jskat.data.iss.MoveType.TIME_OUT
            putPlayerTime(Player.FOREHAND, 91.0)
            putPlayerTime(Player.MIDDLEHAND, 82.0)
            putPlayerTime(Player.REARHAND, 73.0)
        }

        assertThat(IssMoveFeedback.clockUpdates(move)).containsExactly(
            Player.FOREHAND to 91.0,
            Player.MIDDLEHAND to 82.0,
            Player.REARHAND to 73.0,
        )
    }

    @Test
    fun `names the player timed out by ISS`() {
        val game = SkatGameData().apply { setPlayerName(Player.REARHAND, "Rita") }
        val move = MoveInformation().apply { timeOutPlayer = Player.REARHAND }

        assertThat(IssMoveFeedback.timedOutPlayerName(game, move)).isEqualTo("Rita")
    }

    @Test
    fun `accepts only an affirmative table invitation choice`() {
        assertThat(IssMoveFeedback.invitationAccepted(Optional.of(ButtonType.YES))).isTrue()
        assertThat(IssMoveFeedback.invitationAccepted(Optional.of(ButtonType.NO))).isFalse()
        assertThat(IssMoveFeedback.invitationAccepted(Optional.empty())).isFalse()
    }
}
