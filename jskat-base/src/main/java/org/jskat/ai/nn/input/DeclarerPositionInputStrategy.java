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

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets the network inputs for the declarer position
 */
public class DeclarerPositionInputStrategy extends AbstractInputStrategy {

	@Override
	public double[] getNetworkInput(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		switch (knowledge.getDeclarer()) {
		case FOREHAND:
			result[0] = 1.0;
			break;
		case MIDDLEHAND:
			result[1] = 1.0;
			break;
		case REARHAND:
			result[2] = 1.0;
			break;
		default:
			throw new IllegalArgumentException("Declarer player is unknown.");
		}

		return result;
	}

	@Override
	public int getNeuronCount() {
		return 3;
	}
}
