/**
 * Copyright (C) 2018 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.nn.input;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets the network inputs for unplayed cards in the game
 */
public class UnplayedCardsStrategy extends AbstractCardStrategy {

	@Override
	public double[] getNetworkInput(ImmutablePlayerKnowledge knowledge,
			Card cardToPlay) {

		double[] result = getEmptyInputs();

		for (Card card : Card.values()) {
			if (!knowledge.isCardPlayed(card)) {
				result[getNetworkInputIndex(card)] = ON;
			}
		}

		return result;
	}
}
