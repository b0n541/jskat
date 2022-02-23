package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for passing a bid.
 */
public final class PassBidEvent extends AbstractPlayerMoveEvent {

    public PassBidEvent(Player player) {
        super(player);
    }

    @Override
    public final void processForward(SkatGameData data) {
        data.setPlayerPass(player, true);
    }

    @Override
    public final void processBackward(SkatGameData data) {
        data.setPlayerPass(player, false);
    }

    @Override
    protected String getMoveDetails() {
        return "pass";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
}
