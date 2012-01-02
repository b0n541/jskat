package org.jskat.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.JSkatOptions.SupportedLanguage;
import org.jskat.data.SkatTableOptions.RuleSet;
import org.jskat.gui.img.CardFace;
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
		options.setDefaultProperties();

		assertTrue(options.isShowFirstSteps().booleanValue());
		assertFalse(options.isCheckForNewVersionAtStartUp().booleanValue());

		if (Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())) {
			assertEquals(SupportedLanguage.GERMAN, options.getLanguage());
		} else {
			assertEquals(SupportedLanguage.ENGLISH, options.getLanguage());
		}
		assertEquals(CardFace.TOURNAMENT, options.getCardFace());
		assertEquals("", options.getSavePath()); //$NON-NLS-1$
		assertEquals(Integer.valueOf(2000), options.getTrickRemoveDelayTime());
		assertFalse(options.isTrickRemoveAfterClick().booleanValue());
		assertFalse(options.isGameShortCut().booleanValue());

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
