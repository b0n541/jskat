package org.jskat.control.event;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for passing a bid.
 */
public class PickUpSkatEvent implements Event {

	private final Player player;

	public PickUpSkatEvent(Player player) {
		this.player = player;
	}

	@Override
	public void processForward(SkatGameData data) {
		data.addSkatToPlayer(player);
	}

	@Override
	public void processBackward(SkatGameData data) {
		data.removeSkatFromPlayer(player);
	}
}
