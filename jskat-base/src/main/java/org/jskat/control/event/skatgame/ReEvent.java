package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for calling contra.
 */
public final class ReEvent extends AbstractPlayerMoveEvent {

    public ReEvent(Player player) {
        super(player);
    }

    @Override
    public final void processForward(SkatGameData data) {
        data.setRe(true);
    }

    @Override
    public final void processBackward(SkatGameData data) {
        data.setRe(false);
    }

    @Override
    protected String getMoveDetails() {
        return "re";
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
