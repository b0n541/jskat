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
package org.jskat.player;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.jskat.AbstractJSkatTest;
import org.junit.Test;

public class JSkatPlayerResolverTest extends AbstractJSkatTest {

	@Test
	public void testGetAllAIPlayerImplementations() {

		Set<String> implementations = JSkatPlayerResolver
				.getAllAIPlayerImplementations();

		assertThat(implementations.size(), is(2));
	}
}
