package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

import java.util.Objects;

/**
 * Event for passing a bid.
 */
public final class PassBidEvent extends AbstractPlayerMoveEvent {

    public final int nextBidValue;

    public PassBidEvent(Player player, int nextBidValue) {
        super(player);
        this.nextBidValue = nextBidValue;
    }

    @Override
    public void processForward(SkatGameData data) {
        data.setPlayerPass(player, true);
    }

    @Override
    public void processBackward(SkatGameData data) {
        data.setPlayerPass(player, false);
    }

    @Override
    protected String getMoveDetails() {
        return "pass";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PassBidEvent that = (PassBidEvent) o;
        return nextBidValue == that.nextBidValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nextBidValue);
    }
}
