/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
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
package org.jskat.ai.nn.input;

import java.util.HashSet;
import java.util.Set;

import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.Player;

public class PlayerPartyMadeCardsStrategy extends AbstractInputStrategy
		implements InputStrategy {

	@Override
	public int getNeuronCount() {

		return 32;
	}

	@Override
	public double[] getNetworkInput(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		Set<Player> partyMembers = getPartyMembers(knowledge);

		for (Trick trick : knowledge.getCompletedTricks()) {
			if (partyMembers.contains(trick.getTrickWinner())) {
				// trick was won by player's party
				for (Card card : trick.getCardList()) {
					result[getNetworkInputIndex(card)] = 1.0;
				}
			}
		}

		return result;
	}

	protected Set<Player> getPartyMembers(ImmutablePlayerKnowledge knowledge) {

		Set<Player> result = new HashSet<Player>();
		if (knowledge.getDeclarer().equals(knowledge.getPlayerPosition())) {
			// player is declarer
			result.add(knowledge.getPlayerPosition());
		} else {
			// player is opponent
			result.add(knowledge.getDeclarer().getLeftNeighbor());
			result.add(knowledge.getDeclarer().getRightNeighbor());
		}
		return result;
	}

	protected static int getNetworkInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}
}
