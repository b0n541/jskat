/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
