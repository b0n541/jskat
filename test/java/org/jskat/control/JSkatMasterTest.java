/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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
