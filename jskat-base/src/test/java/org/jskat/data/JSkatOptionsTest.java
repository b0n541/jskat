/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
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
package org.jskat.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.gui.img.CardFace;
import org.jskat.gui.img.CardSet;
import org.junit.Test;

/**
 * Tests for JSkat options
 */
public class JSkatOptionsTest extends AbstractJSkatTest {

	/**
	 * Tests the default values
	 */
	@Test
	public void testDefaultValues() {

		JSkatOptions options = JSkatOptions.instance();
		options.setDefaultProperties(new DesktopSavePathResolver());

		assertTrue(options.isShowTipsAtStartUp().booleanValue());
		assertFalse(options.isCheckForNewVersionAtStartUp().booleanValue());

		if (Locale.getDefault().getLanguage()
				.equals(Locale.GERMAN.getLanguage())) {
			assertEquals(SupportedLanguage.GERMAN, options.getLanguage());
		} else {
			assertEquals(SupportedLanguage.ENGLISH, options.getLanguage());
		}
		assertEquals(CardSet.ISS_TOURNAMENT, options.getCardSet());
		assertEquals(CardFace.TOURNAMENT, options.getCardSet().getCardFace());
		assertEquals(options.getDefaultSaveDir(), options.getSavePath());

		// rule defaults
		assertEquals(RuleSet.ISPA, options.getRules());

		assertFalse(options.isPlayContra());
		assertFalse(options.isPlayContra(true));
		assertTrue(options.isPlayContra(false));

		assertFalse(options.isContraAfterBid18());
		assertFalse(options.isContraAfterBid18(true));
		assertTrue(options.isContraAfterBid18(false));

		assertFalse(options.isPlayBock());
		assertFalse(options.isPlayBock(true));
		assertTrue(options.isPlayBock(false));

		assertFalse(options.isBockEventAllPlayersPassed());
		assertFalse(options.isBockEventAllPlayersPassed(true));
		assertFalse(options.isBockEventContraReAnnounced());
		assertFalse(options.isBockEventContraReAnnounced(true));
		assertFalse(options.isBockEventLostAfterContra());
		assertFalse(options.isBockEventLostAfterContra(true));
		assertFalse(options.isBockEventLostGrand());
		assertFalse(options.isBockEventLostGrand(true));
		assertFalse(options.isBockEventLostWith60());
		assertFalse(options.isBockEventLostWith60(true));
		assertFalse(options.isBockEventPlayerHasX00Points());
		assertFalse(options.isBockEventPlayerHasX00Points(true));

		assertFalse(options.isPlayRamsch());
		assertFalse(options.isPlayRamsch(true));
		assertTrue(options.isPlayRamsch(false));

		assertFalse(options.isPlayRevolution());
		assertFalse(options.isPlayRevolution(true));
		assertFalse(options.isPlayRevolution(false));

		// ISS defaults
		assertEquals("skatgame.net", options.getIssAddress()); //$NON-NLS-1$
		assertEquals(Integer.valueOf(7000), options.getIssPort());
	}
}
