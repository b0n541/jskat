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
