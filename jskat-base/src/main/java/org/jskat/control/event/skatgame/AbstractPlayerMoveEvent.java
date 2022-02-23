package org.jskat.control.event.skatgame;

import org.jskat.util.Player;

import java.util.Objects;

public abstract class AbstractPlayerMoveEvent implements SkatGameEvent {

    public final Player player;

    public AbstractPlayerMoveEvent(Player player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return player + ": " + getMoveDetails();
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
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

        final AbstractPlayerMoveEvent other = (AbstractPlayerMoveEvent) obj;

        return Objects.equals(player, other.player);
    }

    protected abstract String getMoveDetails();
}
