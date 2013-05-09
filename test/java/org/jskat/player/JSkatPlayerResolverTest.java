/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-09
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
package org.jskat.player;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.jskat.AbstractJSkatTest;
import org.junit.Before;
import org.junit.Test;

public class JSkatPlayerResolverTest extends AbstractJSkatTest {

	Set<String> testImplementations;

	@Before
	public void setUp() {
		testImplementations = new HashSet<String>();
		testImplementations.add("org.jskat.player.UnitTestPlayer");
		testImplementations.add("org.jskat.ai.test.RamschTestPlayer");
		testImplementations.add("org.jskat.ai.test.NoBiddingTestPlayer");
	}

	@Test
	public void testGetAllAIPlayerImplementations() {

		Set<String> implementations = JSkatPlayerResolver
				.getAllAIPlayerImplementations();
		implementations.removeAll(testImplementations);

		assertEquals(3, implementations.size());
	}
}
