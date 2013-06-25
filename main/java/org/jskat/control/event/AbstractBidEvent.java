package org.jskat.control.event;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

public class AbstractBidEvent implements Event {

	protected final Player player;
	protected final Integer bidRaise;

	public AbstractBidEvent(Player player, Integer bidRaise) {
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