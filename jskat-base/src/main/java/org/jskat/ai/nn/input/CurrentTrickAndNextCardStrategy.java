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
