package org.jskat.ai.nn.train;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jskat.ai.PlayerType;
import org.junit.Test;

public class NNTrainerTest {

	@Test
	public void testCreatePlayerPermutations_NoNNPlayer() {
		List<PlayerType> playerTypes = new ArrayList<PlayerType>();
		playerTypes.add(PlayerType.RANDOM);

		Set<List<PlayerType>> permutations = NNTrainer.createPlayerPermutations(playerTypes);

		assertEquals(0, permutations.size());
	}

	@Test
	public void testCreatePlayerPermutations_OnlyNNPlayer() {
		List<PlayerType> playerTypes = new ArrayList<PlayerType>();
		playerTypes.add(PlayerType.NEURAL_NETWORK);

		Set<List<PlayerType>> permutations = NNTrainer.createPlayerPermutations(playerTypes);

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

		Set<List<PlayerType>> permutations = NNTrainer.createPlayerPermutations(playerTypes);

		assertEquals(19, permutations.size());

		for (List<PlayerType> permutation : permutations) {
			assertTrue(permutation.contains(PlayerType.NEURAL_NETWORK));
		}
	}
}
