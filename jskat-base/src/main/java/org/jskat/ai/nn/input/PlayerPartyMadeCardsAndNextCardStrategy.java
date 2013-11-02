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

import java.util.Set;

import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.Player;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

public class PlayerPartyMadeCardsAndNextCardStrategy extends
		PlayerPartyMadeCardsStrategy {

	@Override
	public double[] getNetworkInput(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = super.getNetworkInput(knowledge, cardToPlay);

		Trick trick = (Trick) knowledge.getCurrentTrick().clone();

		if (trick.getFirstCard() != null && trick.getSecondCard() != null
				&& trick.getThirdCard() == null) {

			trick.setThirdCard(cardToPlay);

			SkatRule rule = SkatRuleFactory.getSkatRules(knowledge
					.getGameType());
			Set<Player> partyMembers = getPartyMembers(knowledge);

			if (partyMembers.contains(rule.calculateTrickWinner(
					knowledge.getGameType(), trick))) {
				// trick was won by player's party
				for (Card card : trick.getCardList()) {
					result[getNetworkInputIndex(card)] = 1.0;
				}
			}
		}

		return result;
	}
}
