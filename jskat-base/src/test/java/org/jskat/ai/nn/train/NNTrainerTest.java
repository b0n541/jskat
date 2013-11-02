/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
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
package org.jskat.ai.nn.train;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jskat.player.JSkatPlayerResolver;
import org.junit.Test;

public class NNTrainerTest {

	@Test
	public void testCreatePlayerPermutations_NoNNPlayer() {
		List<String> playerTypes = new ArrayList<String>();
		playerTypes.add(JSkatPlayerResolver.HUMAN_PLAYER_CLASS);

		Set<List<String>> permutations = NNTrainer
				.createPlayerPermutations(playerTypes);

		assertEquals(0, permutations.size());
	}

	@Test
	public void testCreatePlayerPermutations_OnlyNNPlayer() {
		List<String> playerTypes = new ArrayList<String>();
		playerTypes.add(NNTrainer.NEURAL_NETWORK_PLAYER_CLASS);

		Set<List<String>> permutations = NNTrainer
				.createPlayerPermutations(playerTypes);

		assertEquals(1, permutations.size());
		List<String> permutation = permutations.iterator().next();
		assertEquals(NNTrainer.NEURAL_NETWORK_PLAYER_CLASS, permutation.get(0));
		assertEquals(NNTrainer.NEURAL_NETWORK_PLAYER_CLASS, permutation.get(1));
		assertEquals(NNTrainer.NEURAL_NETWORK_PLAYER_CLASS, permutation.get(2));
	}

	@Test
	public void testCreatePlayerPermutations_ThreePlayerTypes() {
		List<String> playerTypes = new ArrayList<String>();
		for (String aiPlayer : JSkatPlayerResolver
				.getAllAIPlayerImplementations()) {
			playerTypes.add(aiPlayer);
		}

		Set<List<String>> permutations = NNTrainer
				.createPlayerPermutations(playerTypes);

		assertEquals(19, permutations.size());

		for (List<String> permutation : permutations) {
			assertTrue(permutation
					.contains(NNTrainer.NEURAL_NETWORK_PLAYER_CLASS));
		}
	}
}
