package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;

/**
 * Event for game start.
 */
/**
 * Event containing the initial player and game details for a new game.
 *
 * @param gameNo number of the game within its series
 * @param gameVariant variant being played
 * @param leftPlayerPosition position of the player to the user's left
 * @param rightPlayerPosition position of the player to the user's right
 * @param userPosition position occupied by the local user
 */
public record GameStartedEvent(
        Integer gameNo,
        GameVariant gameVariant,
        Player leftPlayerPosition,
        Player rightPlayerPosition,
        Player userPosition) implements SkatGameEvent {

    @Override
    public void processForward(SkatGameData data) {
    }

    @Override
    public void processBackward(SkatGameData data) {
    }
}
