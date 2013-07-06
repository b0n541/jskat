package org.jskat.control.event;

import org.jskat.data.SkatGameData;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Event for discard skat.
 */
public class DiscardSkatEvent implements Event {

	private final Player player;
	private final CardList discardedSkat = new CardList();

	public DiscardSkatEvent(Player player, CardList discardedSkat) {
		this.player = player;
		this.discardedSkat.addAll(discardedSkat);
	}

	@Override
	public void processForward(SkatGameData data) {
		data.removePlayerCards(player, discardedSkat);
		data.setSkatCards(discardedSkat);
	}

	@Override
	public void processBackward(SkatGameData data) {
		data.setSkatCards(new CardList());
		data.addPlayerCards(player, discardedSkat);
	}
}
