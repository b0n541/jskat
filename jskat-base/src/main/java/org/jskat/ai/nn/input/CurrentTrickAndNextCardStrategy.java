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
package org.jskat.ai.nn.input;

import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;

public class CurrentTrickAndNextCardStrategy extends CurrentTrickStrategy {

	@Override
	public double[] getNetworkInput(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		Trick trick = (Trick) knowledge.getCurrentTrick().clone();

		if (trick.getFirstCard() != null && trick.getSecondCard() != null
				&& trick.getThirdCard() == null) {
			// trick will be completed by next card
			trick.setThirdCard(cardToPlay);

			SkatRule rule = SkatRuleFactory.getSkatRules(knowledge
					.getGameType());

			result[getTrickForehand(rule.calculateTrickWinner(
					knowledge.getGameType(), trick))] = 1.0;

		} else {
			// set trick forehand position
			result[getTrickForehand(trick.getForeHand())] = 1.0;

			// set already played cards
			if (trick.getFirstCard() != null) {
				result[3 + getNetworkInputIndex(trick.getFirstCard())] = 1.0;
			}
			if (trick.getSecondCard() != null) {
				result[3 + 32 + getNetworkInputIndex(trick.getSecondCard())] = 1.0;
			}
			if (trick.getThirdCard() != null) {
				result[3 + 64 + getNetworkInputIndex(trick.getThirdCard())] = 1.0;
			}
		}

		return result;
	}
}
