package org.jskat.gui.javafx

import javafx.scene.control.ButtonType
import org.jskat.data.SkatGameData
import org.jskat.data.iss.MoveInformation
import org.jskat.util.Player
import java.util.Optional

/** Toolkit-neutral feedback derived from an ISS move. */
object IssMoveFeedback {
    fun clockUpdates(move: MoveInformation): List<Pair<Player, Double>> =
        Player.entries.map { player -> player to move.getPlayerTime(player) }

    fun timedOutPlayerName(gameData: SkatGameData, move: MoveInformation): String =
        gameData.getPlayerName(requireNotNull(move.timeOutPlayer))

    fun invitationAccepted(result: Optional<ButtonType>): Boolean = result.orElse(ButtonType.NO) == ButtonType.YES
}
