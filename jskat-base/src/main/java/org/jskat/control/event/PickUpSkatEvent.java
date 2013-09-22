package org.jskat.control.event;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for passing a bid.
 */
public final class PickUpSkatEvent implements Event {

	private final Player player;

	public PickUpSkatEvent(Player player) {
		this.player = player;
	}

	@Override
	public final void processForward(SkatGameData data) {
		data.addSkatToPlayer(player);
	}

	@Override
	public final void processBackward(SkatGameData data) {
		data.removeSkatFromPlayer(player);
	}
}
