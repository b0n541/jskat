package org.jskat.control.event;

import org.jskat.data.SkatGameData;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Event for card dealing.
 */
public class DealCardEvent implements Event {

	private Player player;
	private CardList cards = new CardList();

	public DealCardEvent(Player player, CardList cards) {
		this.player = player;
		this.cards.addAll(cards);
	}

	@Override
	public void processForward(SkatGameData data) {
		data.setDealtCards(player, cards);
	}

	@Override
	public void processBackward(SkatGameData data) {
		data.removeDealtCards(player, cards);
	}
}
