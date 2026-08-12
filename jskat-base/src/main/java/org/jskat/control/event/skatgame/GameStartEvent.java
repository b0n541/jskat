package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;

/**
 * Event for game start.
 */
public record GameStartEvent(Integer gameNo, GameVariant gameVariant, Player leftPlayerPosition,
                             Player rightPlayerPosition, Player userPosition,
                             boolean isPracticeGame) implements SkatGameEvent {

    /**
     * Constructor for normal (non-practice) games.
     */
    public GameStartEvent(Integer gameNo, GameVariant gameVariant, Player leftPlayerPosition,
                          Player rightPlayerPosition, Player userPosition) {
        this(gameNo, gameVariant, leftPlayerPosition, rightPlayerPosition, userPosition, false);
    }

    @Override
    public void processForward(SkatGameData data) {
    }

    @Override
    public void processBackward(SkatGameData data) {
    }
}
