/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
