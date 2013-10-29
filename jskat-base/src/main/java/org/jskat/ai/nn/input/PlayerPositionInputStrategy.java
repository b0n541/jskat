/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-10
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.nn.input;

import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;

/**
 * Gets the network inputs for the player position
 */
public class PlayerPositionInputStrategy extends AbstractInputStrategy {

	@Override
	public double[] getNetworkInput(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {

		double[] result = getEmptyInputs();

		switch (knowledge.getPlayerPosition()) {
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
			throw new IllegalArgumentException("Player position is unknown.");
		}

		return result;
	}

	@Override
	public int getNeuronCount() {
		return 3;
	}
}
