package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for picking up the skat.
 */
public final class PickUpSkatEvent extends AbstractPlayerMoveEvent {

    public PickUpSkatEvent(Player player) {
        super(player);
    }

    @Override
    public final void processForward(SkatGameData data) {
        data.setSkatPickUp(true);
        data.addSkatToPlayer(player);
    }

    @Override
    public final void processBackward(SkatGameData data) {
        data.setSkatPickUp(false);
        data.removeSkatFromPlayer(player);
    }

    @Override
    protected String getMoveDetails() {
        return "pick up skat";
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
