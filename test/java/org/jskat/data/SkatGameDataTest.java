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
package org.jskat.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SkatGameDataTest {

	SkatGameData gameData;

	@Before
	public void createGameData() {
		gameData = new SkatGameData();
	}

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
