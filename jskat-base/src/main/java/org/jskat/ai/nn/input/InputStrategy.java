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

public interface InputStrategy {

	/**
	 * Gets the number of neurons the strategy creates
	 * 
	 * @return Number of neurons
	 */
	public int getNeuronCount();

	/**
	 * Gets the network input
	 * 
	 * @param knowledge
	 *            Player knowledge
	 * @param cardToPlay
	 *            Next card to play
	 * @return Network input
	 */
	public double[] getNetworkInput(ImmutablePlayerKnowledge knowledge, Card cardToPlay);
}
