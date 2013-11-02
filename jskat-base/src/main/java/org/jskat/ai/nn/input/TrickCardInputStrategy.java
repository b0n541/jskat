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
