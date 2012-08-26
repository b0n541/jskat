package org.jskat.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SkatGameDataTest {

	SkatGameData gameData;

	@Before
	public void createGameData() {
		gameData = new SkatGameData();
	}

	@Ignore
	@Test
	public void hand() {

		assertTrue(gameData.isHand());

		gameData.setDeclarerPickedUpSkat(true);

		assertFalse(gameData.isHand());
	}

	@Test
	public void schneider() {

		assertFalse(gameData.isSchneider());

		gameData.setDeclarerScore(91);

		assertTrue(gameData.isSchneider());
	}

	@Test
	public void schwarz() {

		assertFalse(gameData.isSchwarz());

		gameData.setDeclarerScore(120);

		assertTrue(gameData.isSchwarz());
	}

}
