package org.jskat.control.event;

import org.jskat.util.Player;

/**
 * Event for bidding.
 */
public class BidEvent extends AbstractBidEvent {
	public BidEvent(Player player, Integer bidRaise) {
		super(player, bidRaise);
	}
}
