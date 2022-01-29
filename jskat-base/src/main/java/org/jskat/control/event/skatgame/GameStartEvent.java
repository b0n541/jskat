package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;

import java.util.Objects;

/**
 * Event for game start.
 */
public final class GameStartEvent implements SkatGameEvent {

    public final Integer gameNo;
    public final GameVariant gameVariant;
    public final Player leftPlayerPosition;
    public final Player rightPlayerPosition;
    public final Player userPosition;

    public GameStartEvent(Integer gameNo, GameVariant gameVariant,
                          Player leftPlayerPosition, Player rightPlayerPosition,
                          Player userPosition) {
        this.gameNo = gameNo;
        this.gameVariant = gameVariant;
        this.leftPlayerPosition = leftPlayerPosition;
        this.rightPlayerPosition = rightPlayerPosition;
        this.userPosition = userPosition;
    }

    @Override
    public void processForward(SkatGameData data) {
    }

    @Override
    public void processBackward(SkatGameData data) {
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(gameNo, gameVariant, leftPlayerPosition, rightPlayerPosition, userPosition);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameStartEvent other = (GameStartEvent) obj;

        return Objects.equals(gameNo, other.gameNo) &&
                Objects.equals(gameVariant, other.gameVariant) &&
                Objects.equals(leftPlayerPosition, other.leftPlayerPosition) &&
                Objects.equals(rightPlayerPosition, other.rightPlayerPosition) &&
                Objects.equals(userPosition, other.userPosition);
    }
}
