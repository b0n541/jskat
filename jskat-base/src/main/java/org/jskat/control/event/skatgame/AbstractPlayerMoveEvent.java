package org.jskat.control.event.skatgame;

import org.jskat.util.Player;

public abstract class AbstractPlayerMoveEvent implements SkatGameEvent {

	public final Player player;

	public AbstractPlayerMoveEvent(Player player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return player + ": " + getMoveDetails();
	}

	protected abstract String getMoveDetails();
}
