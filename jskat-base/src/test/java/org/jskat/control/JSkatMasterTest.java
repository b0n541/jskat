package org.jskat.control;


import org.jskat.AbstractJSkatTest;
import org.jskat.gui.UnitTestView;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link JSkatMaster}
 */
public class JSkatMasterTest extends AbstractJSkatTest {

    /**
     * Tests the creation of tables
     */
    @Test
    public void createTable() {
        final UnitTestView view = new UnitTestView();
        JSkatMaster.INSTANCE.setView(view);

        JSkatMaster.INSTANCE.createTable();

        assertThat(view.tables.size()).isEqualTo(1);
        assertTrue(view.tables.contains("UnitTestTable 1"));

        JSkatMaster.INSTANCE.createTable();

        assertThat(view.tables.size()).isEqualTo(2);
        assertTrue(view.tables.contains("UnitTestTable 1"));
        assertTrue(view.tables.contains("UnitTestTable 2"));
    }
}
