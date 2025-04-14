package org.jskat.control.event.skatgame;

import org.jskat.data.GameSummary;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameResult;

import java.util.Objects;

/**
 * Event for game finish.
 */
public final class GameFinishEvent implements SkatGameEvent {

    public String declarerName;
    public final GameSummary gameSummary;

    public GameFinishEvent(final String declarerName, final GameSummary gameSummary) {
        this.declarerName = declarerName;
        this.gameSummary = gameSummary;
    }

    @Override
    public void processForward(final SkatGameData data) {
        data.setResult(gameSummary.gameResult);
    }

    @Override
    public void processBackward(final SkatGameData data) {
        data.setResult(new SkatGameResult());
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameSummary);
    }

    @Override
    public boolean equals(final Object obj) {
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
