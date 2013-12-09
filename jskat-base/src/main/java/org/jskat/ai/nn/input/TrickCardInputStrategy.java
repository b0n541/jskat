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
import org.jskat.util.Player;

/**
 * Gets the network inputs for played cards in the game per trick
 */
public class TrickCardInputStrategy extends AbstractInputStrategy {

	@Override
	public int getNeuronCount() {
		// 10 for tricks, 3 for trick forehand position, 3*32 for played cards
		// in trick
		return 10 * (3 + 3 * 32);
	}

	@Override
	public double[] getNetworkInput(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		for (Trick trick : knowledge.getCompletedTricks()) {
			setTrickInputs(result, trick);
		}
		setTrickInputs(result, knowledge.getCurrentTrick());

		return result;
	}

	private void setTrickInputs(double[] result, Trick trick) {

		// set trick forehand position
		result[getTrickOffset(trick) + getTrickForehand(trick.getForeHand())] = 1.0;

		// set already played cards
		if (trick.getFirstCard() != null) {
			result[getTrickOffset(trick) + 3
					+ getNetworkInputIndex(trick.getFirstCard())] = 1.0;
		}
		if (trick.getSecondCard() != null) {
			result[getTrickOffset(trick) + 3 + 32
					+ getNetworkInputIndex(trick.getSecondCard())] = 1.0;
		}
		if (trick.getThirdCard() != null) {
			result[getTrickOffset(trick) + 3 + 64
					+ getNetworkInputIndex(trick.getThirdCard())] = 1.0;
		}
	}

	protected int getTrickOffset(Trick trick) {
		return trick.getTrickNumberInGame() * (3 + 3 * 32);
	}

	private static int getTrickForehand(Player player) {

		int result = -1;

		switch (player) {
		case FOREHAND:
			result = 0;
			break;
		case MIDDLEHAND:
			result = 1;
			break;
		case REARHAND:
			result = 2;
			break;
		default:
			throw new IllegalArgumentException(
					"Trick forehand player is unknown.");
		}

		return result;
	}

	protected static int getNetworkInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}
}
