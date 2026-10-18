package org.jskat.ai.ml;

import org.jskat.player.JSkatPlayer;

/**
 * Result of resolving a configured player type without triggering model lifecycle work.
 */
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

    /**
     * Created.
     * @param player player value.
     * @return created result.
     */
    public static PlayerCreationResult created(final JSkatPlayer player) {
        return new PlayerCreationResult(player, null, null);
    }

    /**
     * Performs unavailable.
     * @param unavailable unavailable value.
     * @return unavailable result.
     */
    public static PlayerCreationResult unavailable(final ModelUnavailable unavailable) {
        return new PlayerCreationResult(null, unavailable, null);
    }

    /**
     * Performs failed.
     * @param message message value.
     * @return failed result.
     */
    public static PlayerCreationResult failed(final String message) {
        return new PlayerCreationResult(null, null, message);
    }

    /**
     * Returns or indicates created.
     * @return is created result.
     */
    public boolean isCreated() {
        return player != null;
    }

    /**
     * Player.
     * @return player result.
     */
    public JSkatPlayer player() {
        return player;
    }

    /**
     * Performs unavailable.
     * @return unavailable result.
     */
    public ModelUnavailable unavailable() {
        return unavailable;
    }

    /**
     * Performs failure message.
     * @return failure message result.
     */
    public String failureMessage() {
        return failureMessage;
    }
}
