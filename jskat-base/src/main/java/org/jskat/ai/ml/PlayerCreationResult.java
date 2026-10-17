package org.jskat.ai.ml;

import org.jskat.player.JSkatPlayer;

/** Result of resolving a configured player type without triggering model lifecycle work. */
public final class PlayerCreationResult {

    private final JSkatPlayer player;
    private final ModelUnavailable unavailable;
    private final String failureMessage;

    private PlayerCreationResult(final JSkatPlayer player, final ModelUnavailable unavailable,
                                 final String failureMessage) {
        this.player = player;
        this.unavailable = unavailable;
        this.failureMessage = failureMessage;
    }

    public static PlayerCreationResult created(final JSkatPlayer player) {
        return new PlayerCreationResult(player, null, null);
    }

    public static PlayerCreationResult unavailable(final ModelUnavailable unavailable) {
        return new PlayerCreationResult(null, unavailable, null);
    }

    public static PlayerCreationResult failed(final String message) {
        return new PlayerCreationResult(null, null, message);
    }

    public boolean isCreated() {
        return player != null;
    }

    public JSkatPlayer player() {
        return player;
    }

    public ModelUnavailable unavailable() {
        return unavailable;
    }

    public String failureMessage() {
        return failureMessage;
    }
}
