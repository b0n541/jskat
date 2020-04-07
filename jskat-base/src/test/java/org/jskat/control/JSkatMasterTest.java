/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        assertTrue(view.tables.contains("UnitTestTable 1")); //$NON-NLS-1$

        JSkatMaster.INSTANCE.createTable();

        assertThat(view.tables.size()).isEqualTo(2);
        assertTrue(view.tables.contains("UnitTestTable 1")); //$NON-NLS-1$
        assertTrue(view.tables.contains("UnitTestTable 2")); //$NON-NLS-1$
    }
}
