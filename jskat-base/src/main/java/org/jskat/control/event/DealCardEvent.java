package org.jskat.control.event;

import java.util.HashMap;
import java.util.Map;

import org.jskat.data.SkatGameData;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Event for card dealing.
 */
public final class DealCardEvent implements Event {

	private final Map<Player, CardList> playerCards = new HashMap<Player, CardList>();
	private final CardList skat = new CardList();

	public DealCardEvent(Map<Player, CardList> playerCards, CardList skat) {
		this.playerCards.putAll(playerCards);
		this.skat.addAll(skat);
	}

	@Override
	public final void processForward(SkatGameData data) {
		for (Player player : playerCards.keySet()) {
			data.addDealtCards(player, playerCards.get(player));
		}
		data.setDealtSkatCards(skat);
	}

	@Override
	public final void processBackward(SkatGameData data) {
		for (Player player : playerCards.keySet()) {
			data.removeDealtCards(player, playerCards.get(player));
		}
		data.removeDealtSkatCards(skat);
	}
}
