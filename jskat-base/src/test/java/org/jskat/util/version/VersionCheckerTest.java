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
        assertTrue(VersionChecker.isHigherVersionAvailable("0.10.0", "0.10.1"));
        assertTrue(VersionChecker.isHigherVersionAvailable("0.10.1", "1.0.0"));
        assertTrue(VersionChecker.isHigherVersionAvailable("0.10.1", "0.11.0"));
        assertFalse(VersionChecker.isHigherVersionAvailable("0.11.0", "0.10.1"));
        assertFalse(VersionChecker.isHigherVersionAvailable("0.10.0", "0.10.0"));
        assertFalse(VersionChecker.isHigherVersionAvailable("0.10.0", "0.9.0"));
    }
}
