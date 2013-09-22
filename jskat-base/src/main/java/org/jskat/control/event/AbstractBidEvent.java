package org.jskat.control.event;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

public class AbstractBidEvent implements Event {

	protected final Player player;
	protected final Integer bid;

	public AbstractBidEvent(Player player, Integer bid) {
		this.player = player;
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

}