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
package org.jskat.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.gui.UnitTestView;
import org.junit.Test;

/**
 * Test class for {@link JSkatMaster}
 */
public class JSkatMasterTest extends AbstractJSkatTest {

	/**
	 * Tests the creation of tables
	 */
	@Test
	public void createTable() {
		JSkatMaster master = JSkatMaster.instance();
		UnitTestView view = new UnitTestView();
		master.setView(view);

		master.createTable();

		assertEquals(1, view.tables.size());
		assertTrue(view.tables.contains("UnitTestTable 1")); //$NON-NLS-1$

		master.createTable();

		assertEquals(2, view.tables.size());
		assertTrue(view.tables.contains("UnitTestTable 1")); //$NON-NLS-1$
		assertTrue(view.tables.contains("UnitTestTable 2")); //$NON-NLS-1$
	}
}
