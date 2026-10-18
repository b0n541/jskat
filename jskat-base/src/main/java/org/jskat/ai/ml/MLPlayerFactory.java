package org.jskat.ai.ml;

import org.jskat.player.JSkatPlayer;

/**
 * Sole player creation seam for ML types and ordinary reflective player types.
 */
public final class MLPlayerFactory {

    private MLPlayerFactory() {
    }

    /**
     * Create.
     * @param playerType player type value.
     * @param models models value.
     * @return create result.
     */
    public static PlayerCreationResult create(final String playerType, final ResolvedModelSet models) {
        if (MLPlayer.class.getName().equals(playerType)) {
            if (models == null) {
                return PlayerCreationResult.unavailable(new ModelUnavailable(
                        ModelUnavailableReason.NO_ACTIVE_RELEASE,
                        "ML Player is unavailable because no Resolved model set was established at startup",
                        null));
            }
            try {
                return PlayerCreationResult.created(new MLPlayer(models));
            } catch (final RuntimeException e) {
                return PlayerCreationResult.failed("Could not create ML Player: " + e.getMessage());
            }
        }
        if (MLPlayerPro.class.getName().equals(playerType)) {
            if (models == null) {
                return PlayerCreationResult.unavailable(new ModelUnavailable(
                        ModelUnavailableReason.NO_ACTIVE_RELEASE,
                        "ML Player Pro is unavailable because no Resolved model set was established at startup",
                        null));
            }
            try {
                return PlayerCreationResult.created(new MLPlayerPro(models));
            } catch (final RuntimeException e) {
                return PlayerCreationResult.failed("Could not create ML Player Pro: " + e.getMessage());
            }
        }
        try {
            return PlayerCreationResult.created((JSkatPlayer) Class.forName(playerType)
                    .getDeclaredConstructor().newInstance());
        } catch (final ReflectiveOperationException | ClassCastException e) {
            return PlayerCreationResult.failed("Could not create player " + playerType + ": " + e.getMessage());
        }
    }
}
