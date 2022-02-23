package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for calling contra.
 */
public final class ContraEvent extends AbstractPlayerMoveEvent {

    public ContraEvent(Player player) {
        super(player);
    }

    @Override
    public final void processForward(SkatGameData data) {
        data.setContra(true);
    }

    @Override
    public final void processBackward(SkatGameData data) {
        data.setContra(false);
    }

    @Override
    protected String getMoveDetails() {
        return "contra";
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
