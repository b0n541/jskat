/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
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
package org.jskat.ai.nn.train;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jskat.player.PlayerType;
import org.junit.Test;

public class NNTrainerTest {

	@Test
	public void testCreatePlayerPermutations_NoNNPlayer() {
		List<PlayerType> playerTypes = new ArrayList<PlayerType>();
		playerTypes.add(PlayerType.RANDOM);

		Set<List<PlayerType>> permutations = NNTrainer
				.createPlayerPermutations(playerTypes);

		assertEquals(0, permutations.size());
	}

	@Test
	public void testCreatePlayerPermutations_OnlyNNPlayer() {
		List<PlayerType> playerTypes = new ArrayList<PlayerType>();
		playerTypes.add(PlayerType.NEURAL_NETWORK);

		Set<List<PlayerType>> permutations = NNTrainer
				.createPlayerPermutations(playerTypes);

		assertEquals(1, permutations.size());
		List<PlayerType> permutation = permutations.iterator().next();
		assertEquals(PlayerType.NEURAL_NETWORK, permutation.get(0));
		assertEquals(PlayerType.NEURAL_NETWORK, permutation.get(1));
		assertEquals(PlayerType.NEURAL_NETWORK, permutation.get(2));
	}

	@Test
	public void testCreatePlayerPermutations_ThreePlayerTypes() {
		List<PlayerType> playerTypes = new ArrayList<PlayerType>();
		playerTypes.add(PlayerType.ALGORITHMIC);
		playerTypes.add(PlayerType.RANDOM);
		playerTypes.add(PlayerType.NEURAL_NETWORK);

		Set<List<PlayerType>> permutations = NNTrainer
				.createPlayerPermutations(playerTypes);

		assertEquals(19, permutations.size());

		for (List<PlayerType> permutation : permutations) {
			assertTrue(permutation.contains(PlayerType.NEURAL_NETWORK));
		}
	}
}
