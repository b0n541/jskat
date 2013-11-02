/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
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
package org.jskat.util.version;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.junit.Test;

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
