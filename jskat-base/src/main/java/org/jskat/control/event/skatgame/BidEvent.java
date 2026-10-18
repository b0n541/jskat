package org.jskat.control.event.skatgame;

import org.jskat.util.Player;

/**
 * Event for bidding.
 */
public final class BidEvent extends AbstractBidEvent {
    /**
     * Performs bid event.
     * @param player player value.
     * @param bid bid value.
     */
    public BidEvent(Player player, Integer bid) {
        super(player, bid);
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
