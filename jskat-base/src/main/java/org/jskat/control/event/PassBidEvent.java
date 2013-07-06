package org.jskat.control.event;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for passing a bid.
 */
public class PassBidEvent implements Event {

	private final Player player;

	public PassBidEvent(Player player) {
		this.player = player;
	}

	@Override
	public void processForward(SkatGameData data) {
		data.setPlayerPass(player, true);
	}

	@Override
	public void processBackward(SkatGameData data) {
		data.setPlayerPass(player, false);
	}
}
