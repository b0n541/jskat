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
