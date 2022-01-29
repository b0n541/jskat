package org.jskat.control.event.skatgame;

import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameResult;

import java.util.Objects;

/**
 * Event for game start.
 */
public final class GameFinishEvent implements SkatGameEvent {

    public final GameSummary gameSummary;

    public GameFinishEvent(GameSummary gameSummary) {
        this.gameSummary = gameSummary;
    }

    @Override
    public final void processForward(SkatGameData data) {
        data.setResult(gameSummary.gameResult);
    }

    @Override
    public final void processBackward(SkatGameData data) {
        data.setResult(new SkatGameResult());
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameSummary);
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
        final GameFinishEvent other = (GameFinishEvent) obj;

        return Objects.equals(gameSummary, other.gameSummary);
    }
}
