package org.jskat.control.event.skatgame;

import org.jskat.util.Player;

import java.util.Objects;

/**
 * Base event for a move made by one player during a game.
 *
 * @see SkatGameEvent
 */
public abstract class AbstractPlayerMoveEvent implements SkatGameEvent {

    /**
     * Player who made the move.
     */
    public final Player player;

    /**
     * Creates a move event for the given player.
     * @param player player who made the move
     */
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

    /**
     * Returns a short description of the move for event display.
     * @return text describing the move
     */
    protected abstract String getMoveDetails();
}
