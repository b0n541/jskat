package org.jskat.control.event;

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for game start.
 */
public final class GameStartEvent implements Event {

	private final Player dealer;

	public GameStartEvent(Player dealer) {
		this.dealer = dealer;
	}

	@Override
	public final void processForward(SkatGameData data) {
		data.setDealer(dealer);
	}

	@Override
	public final void processBackward(SkatGameData data) {
		data.setDealer(null);
	}
}
