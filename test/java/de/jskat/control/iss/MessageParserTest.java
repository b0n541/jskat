/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

 */

package de.jskat.control.iss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Test;

import de.jskat.AbstractJSkatTest;
import de.jskat.data.SkatGameData;
import de.jskat.data.iss.ISSPlayerStatus;
import de.jskat.data.iss.ISSTablePanelStatus;
import de.jskat.util.Player;

/**
 * Tests the parsing of the ISS messages
 */
public class MessageParserTest extends AbstractJSkatTest {

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

	/**
	 * Test the parsing of the game summary
	 */
	@Test
	public void testParseGameSummary002() {

		String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[29859]ID[684159]DT[2011-04-05/20:35:55/UTC]P0[foo]P1[xskat:2]P2[xskat]R0[0.0]R1[]R2[]MV[w SQ.DK.ST.S7.CT.HK.S9.SK.H7.C7.DT.CA.CQ.CK.DJ.DA.H8.SA.D7.C8.SJ.HA.CJ.S8.C9.DQ.HJ.HQ.D9.D8.HT.H9 1 18 0 y 1 20 0 y 1 22 0 y 1 23 0 y 1 24 0 y 1 p 2 27 0 p 2 s w HT.H9 2 G.S8.C9 0 DK 1 DA 2 D8 1 DT 2 D9 0 ST 1 SA 2 CJ 0 S7 2 HJ 0 CT 1 DJ 2 DQ 0 C7 1 D7 2 HA 0 H7 1 H8 2 HT 0 HK 1 C8 2 HQ 0 S9 1 CQ 2 H9 0 SQ 1 CK 2 SJ 0 SK 1 CA ]R[d:2 win v:96 m:3 bidok p:85 t:8 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:0] ;)"; //$NON-NLS-1$

		SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

		assertTrue(gameData.isGameWon());

		// FIXME (jansch 05.04.2011) add further asserts
	}

	/**
	 * Test the leaving of an player
	 */
	@Test
	public void testParseTableUpdatePlayerLeft() {

		String tableUpdate = "table .4 foo state 3 foo . . . foo . 0 0 0 0 0 0 1 0 xskat $ 2 1 83 157 0 0 1 0 xskat:2 $ 0 0 0 0 0 0 1 0 . . 0 0 0 0 0 0 0 0 false 0";

		StringTokenizer token = new StringTokenizer(tableUpdate);
		token.nextToken(); // table
		token.nextToken(); // .4
		String creator = token.nextToken(); // foo
		token.nextToken(); // state
		List<String> detailParams = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			detailParams.add(token.nextToken());
		}

		ISSTablePanelStatus status = MessageParser.getTableStatus(creator, detailParams);

		assertEquals(3, status.getMaxPlayers());
		assertEquals(3, status.getPlayerInformations().size());

		ISSPlayerStatus playerStatus = status.getPlayerInformation("xskat"); //$NON-NLS-1$
		assertNotNull(playerStatus);
		assertEquals(2, playerStatus.getGamesPlayed());
		assertEquals(1, playerStatus.getGamesWon());
		assertEquals(83, playerStatus.getLastGameResult());
		assertEquals(157, playerStatus.getTotalPoints());
		assertTrue(playerStatus.isPlayerLeft());
		assertFalse(playerStatus.isReadyToPlay());
		assertTrue(playerStatus.isTalkEnabled());
	}
}
