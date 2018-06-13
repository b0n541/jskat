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

import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;

public class CurrentTrickStrategy extends AbstractInputStrategy {

	@Override
	public int getNeuronCount() {
		return 3 * 12;
	}

	@Override
	public double[] getNetworkInput(final ImmutablePlayerKnowledge knowledge, final Card cardToPlay) {

		final double[] result = getEmptyInputs();

		final Trick trick = knowledge.getCurrentTrick();

		setTrickCardInputs(result, trick);

		return result;
	}

	protected final void setTrickCardInputs(final double[] result, final Trick trick) {
		// set already played cards
		if (trick.getFirstCard() != null) {
			result[trick.getFirstCard().getSuit().getSortOrder()] = ON;
			result[4 + trick.getFirstCard().getRank().getNullOrder()] = ON;
		}
		if (trick.getSecondCard() != null) {
			result[12 + trick.getSecondCard().getSuit().getSortOrder()] = ON;
			result[12 + 4 + trick.getSecondCard().getRank().getNullOrder()] = ON;
		}
		if (trick.getThirdCard() != null) {
			result[24 + trick.getThirdCard().getSuit().getSortOrder()] = ON;
			result[24 + 4 + trick.getThirdCard().getRank().getNullOrder()] = ON;
		}
	}
}
