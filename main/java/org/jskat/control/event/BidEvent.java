package org.jskat.control.event;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for game start.
 */
public class BidEvent implements Event {

	private final Player player;
	private final Integer bidRaise;

	public BidEvent(Player player, Integer bidRaise) {
		this.player = player;
		this.bidRaise = bidRaise;
	}

	@Override
	public void processForward(SkatGameData data) {
		data.setMaxPlayerBid(player, data.getMaxPlayerBid(player) + bidRaise);
	}

	@Override
	public void processBackward(SkatGameData data) {
		data.setMaxPlayerBid(player, data.getMaxPlayerBid(player) - bidRaise);
	}
}
