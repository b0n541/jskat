package org.jskat.control.event;

import org.jskat.util.Player;

/**
 * Event for holding a bid.
 */
public class HoldBidEvent extends AbstractBidEvent {
	public HoldBidEvent(Player player, Integer bid) {
		super(player, bid);
	}
}
