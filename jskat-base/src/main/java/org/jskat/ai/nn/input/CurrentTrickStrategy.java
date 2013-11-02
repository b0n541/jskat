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
import org.jskat.util.Player;

public class CurrentTrickStrategy extends AbstractInputStrategy implements
		InputStrategy {

	@Override
	public int getNeuronCount() {
		// 3 for trick forehand and 3 * 32 for each trick card
		return 3 + 3 * 32;
	}

	@Override
	public double[] getNetworkInput(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {
		double[] result = getEmptyInputs();

		Trick trick = knowledge.getCurrentTrick();

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

		return result;
	}

	protected static int getTrickForehand(Player player) {

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
