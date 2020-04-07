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
package org.jskat.util.version;


import org.jskat.AbstractJSkatTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link VersionChecker}
 */
public class VersionCheckerTest extends AbstractJSkatTest {

    /**
     * Tests the version checking
     */
    @Test
    public void testIsHigherVersionAvailable() {
        assertTrue(VersionChecker.isHigherVersionAvailable("0.10.0", "0.10.1")); //$NON-NLS-1$//$NON-NLS-2$
        assertTrue(VersionChecker.isHigherVersionAvailable("0.10.1", "1.0.0")); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(VersionChecker.isHigherVersionAvailable("0.10.1", "0.11.0")); //$NON-NLS-1$//$NON-NLS-2$
        assertFalse(VersionChecker.isHigherVersionAvailable("0.11.0", "0.10.1")); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(VersionChecker.isHigherVersionAvailable("0.10.0", "0.10.0")); //$NON-NLS-1$//$NON-NLS-2$
        assertFalse(VersionChecker.isHigherVersionAvailable("0.10.0", "0.9.0")); //$NON-NLS-1$//$NON-NLS-2$
    }
}
