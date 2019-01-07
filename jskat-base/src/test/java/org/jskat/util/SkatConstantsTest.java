/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.util;

import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.junit.Test;

/**
 * Test cases for class Card
 */
public class SkatConstantsTest extends AbstractJSkatTest {

	/**
	 * Tests calculation of game values after tournament rules
	 */
	@Test
	public void getTournamentGameValue001() {

		assertTrue(SkatConstants.getTournamentGameValue(true, 18, 3) == 68);
		assertTrue(SkatConstants.getTournamentGameValue(true, 18, 4) == 68);
		assertTrue(SkatConstants.getTournamentGameValue(true, -36, 3) == -86);
		assertTrue(SkatConstants.getTournamentGameValue(true, -36, 4) == -86);
		assertTrue(SkatConstants.getTournamentGameValue(true, 20, 3) == 70);
		assertTrue(SkatConstants.getTournamentGameValue(true, 20, 4) == 70);
		assertTrue(SkatConstants.getTournamentGameValue(true, -40, 3) == -90);
		assertTrue(SkatConstants.getTournamentGameValue(true, -40, 4) == -90);
		assertTrue(SkatConstants.getTournamentGameValue(false, 18, 3) == 0);
		assertTrue(SkatConstants.getTournamentGameValue(false, 18, 4) == 0);
		assertTrue(SkatConstants.getTournamentGameValue(false, -36, 3) == 40);
		assertTrue(SkatConstants.getTournamentGameValue(false, -36, 4) == 30);
		assertTrue(SkatConstants.getTournamentGameValue(false, 20, 3) == 0);
		assertTrue(SkatConstants.getTournamentGameValue(false, 20, 4) == 0);
		assertTrue(SkatConstants.getTournamentGameValue(false, -40, 3) == 40);
		assertTrue(SkatConstants.getTournamentGameValue(false, -40, 4) == 30);
	}
}
