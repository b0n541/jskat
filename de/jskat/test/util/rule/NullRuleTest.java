/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.test.util.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.util.GameType;
import de.jskat.util.Player;
import de.jskat.util.rule.SkatRuleFactory;


public class NullRuleTest {

	private static SkatGameData data;
	private static GameAnnouncement ann;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		
		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$

		data = new SkatGameData();
		ann = new GameAnnouncement();
		ann.setGameType(GameType.NULL);
		data.setAnnouncement(ann);
		data.setDeclarer(Player.FORE_HAND);
	}
	
	@Test 
	public void calcGameWon() {
		assertTrue(SkatRuleFactory.getSkatRules(GameType.NULL).calcGameWon(data));
	}
	
	@Test
	public void calcGameResult() {
		assertEquals(SkatRuleFactory.getSkatRules(GameType.NULL)
				.calcGameResult(data), -46);
	}
}
