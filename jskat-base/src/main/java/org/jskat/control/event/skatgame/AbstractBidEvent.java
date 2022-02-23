package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

import java.util.Objects;

public abstract class AbstractBidEvent extends AbstractPlayerMoveEvent {

    public final Integer bid;

    public AbstractBidEvent(Player player, Integer bid) {
        super(player);
        this.bid = bid;
    }

    @Override
    public final void processForward(SkatGameData data) {
        data.addPlayerBid(player, bid);
    }

    @Override
    public final void processBackward(SkatGameData data) {
        data.removeLastPlayerBid(player);
    }

    @Override
    protected final String getMoveDetails() {
        return bid.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(bid);
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
        final AbstractBidEvent other = (AbstractBidEvent) obj;

        return Objects.equals(bid, other.bid);
    }
}