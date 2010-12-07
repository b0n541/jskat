/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jskat.data.SkatGameData;
import de.jskat.util.Player;

/**
 * Tests the parsing of the ISS messages
 */
public class MessageParserTest {

	/**
	 * Creates the logger
	 */
	@BeforeClass
	public static void createLogger() {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$
	}

	/**
	 * Test the parsing of the game summary
	 */
	@Test
	public void testParseGameSummary() {

		String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[24072]ID[541932]DT[2010-12-06/18:59:24/UTC]P0[zoot]P1[kermit]P2[foo]R0[]R1[]R2[0.0]MV[w HA.SK.SJ.SA.CQ.S8.C9.H7.H9.DQ.CJ.S9.DJ.S7.D9.SQ.C8.HQ.DK.CA.D8.D7.DT.CT.ST.C7.HK.DA.HT.HJ.H8.CK 1 p 2 18 0 p 2 s w H8.CK 2 D.ST.H8 0 SA 1 S7 2 DA 2 HJ 0 SJ 1 D9 0 DQ 1 DJ 2 D7 1 CJ 2 D8 0 CQ 1 CA 2 C7 0 C9 1 S9 2 DT 0 S8 2 CT 0 H7 1 C8 2 CK 0 H9 1 HQ 2 HK 0 HA 1 SQ 0 SK 1 DK 2 HT ]R[d:2 loss v:-54 m:-2 bidok p:59 t:4 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:0] ;)"; //$NON-NLS-1$

		SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

		assertEquals("zoot", gameData.getPlayerName(Player.FORE_HAND)); //$NON-NLS-1$
		assertEquals("kermit", gameData.getPlayerName(Player.MIDDLE_HAND)); //$NON-NLS-1$
		assertEquals("foo", gameData.getPlayerName(Player.HIND_HAND)); //$NON-NLS-1$

		assertEquals(Player.HIND_HAND, gameData.getDeclarer());
		assertFalse(gameData.isGameWon());
		assertEquals(-54, gameData.getResult());
		assertEquals(59, gameData.getDeclarerScore());
		assertEquals(61, gameData.getOpponentScore());
		assertFalse(gameData.isSchneider());
		assertFalse(gameData.isSchwarz());
		assertFalse(gameData.isOverBidded());
	}
}
