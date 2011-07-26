/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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
package org.jskat.control.iss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.MoveType;
import org.jskat.data.iss.PlayerStatus;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.Test;

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

		assertEquals("zoot", gameData.getPlayerName(Player.FOREHAND)); //$NON-NLS-1$
		assertEquals("kermit", gameData.getPlayerName(Player.MIDDLEHAND)); //$NON-NLS-1$
		assertEquals("foo", gameData.getPlayerName(Player.REARHAND)); //$NON-NLS-1$

		assertEquals(Player.REARHAND, gameData.getDeclarer());
		assertFalse(gameData.isGameWon());
		assertEquals(-54, gameData.getResult().getGameValue());
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
	 * Test the parsing of the game summary<br>
	 * Passed game
	 */
	@Test
	public void testParseGameSummary_PassedGame() {

		String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[32407]ID[756788]DT[2011-05-28/08:46:19/UTC]P0[xskat]P1[bonsai]P2[bernie]R0[]R1[0.0]R2[]MV[w C8.DQ.DJ.HK.S9.SK.SQ.HQ.CK.D9.S8.DT.SJ.C9.CQ.SA.DK.HT.D7.H7.ST.HJ.C7.H8.S7.DA.CJ.CT.D8.H9.CA.HA 1 p 2 p 0 p ]R[passed] ;)"; //$NON-NLS-1$

		SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

		// assertTrue(gameData.isGamePassed());
		assertEquals(-1, gameData.getResult().getGameValue());

		// FIXME (jan 28.05.2011) add further asserts
	}

	/**
	 * Test the leaving of a player
	 */
	@Test
	public void testParseTableUpdatePlayerLeft() {

		String tableUpdate = "table .4 foo state 3 foo . . . foo . 0 0 0 0 0 0 1 0 xskat $ 2 1 83 157 0 0 1 0 xskat:2 $ 0 0 0 0 0 0 1 0 . . 0 0 0 0 0 0 0 0 false 0"; //$NON-NLS-1$

		StringTokenizer token = new StringTokenizer(tableUpdate);
		token.nextToken(); // table
		token.nextToken(); // .4
		String creator = token.nextToken(); // foo
		token.nextToken(); // state
		List<String> detailParams = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			detailParams.add(token.nextToken());
		}

		TablePanelStatus status = MessageParser.getTableStatus(creator,
				detailParams);

		assertEquals(3, status.getMaxPlayers());
		assertEquals(3, status.getPlayerInformations().size());

		PlayerStatus playerStatus = status.getPlayerInformation("xskat"); //$NON-NLS-1$
		assertNotNull(playerStatus);
		assertEquals(2, playerStatus.getGamesPlayed());
		assertEquals(1, playerStatus.getGamesWon());
		assertEquals(83, playerStatus.getLastGameResult());
		assertEquals(157, playerStatus.getTotalPoints());
		assertTrue(playerStatus.isPlayerLeft());
		assertFalse(playerStatus.isReadyToPlay());
		assertTrue(playerStatus.isTalkEnabled());
	}

	/**
	 * Tests the resigning of a player
	 */
	public void testParseTableUpdatePlayerResign() {

		String playerResign = "table .4 foo play 1 RE 124.1 173.9 177.8"; //$NON-NLS-1$

		StringTokenizer token = new StringTokenizer(playerResign);
		token.nextToken(); // table
		token.nextToken(); // .4
		String creator = token.nextToken(); // foo
		token.nextToken(); // play
		List<String> detailParams = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			detailParams.add(token.nextToken());
		}

		MoveInformation moveInfo = MessageParser
				.getMoveInformation(detailParams);

		assertEquals(MoveType.RESIGN, moveInfo.getType());
	}

	/**
	 * Tests showing cards
	 */
	public void testParseTableUpdatePlayerShowCards() {

		String playerResign = "table .3 foo play 2 SC.HT.HA.SJ.SQ.SK.CJ 164.1 177.0 156.4"; //$NON-NLS-1$

		StringTokenizer token = new StringTokenizer(playerResign);
		token.nextToken(); // table
		token.nextToken(); // .3
		String creator = token.nextToken(); // foo
		token.nextToken(); // play
		List<String> detailParams = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			detailParams.add(token.nextToken());
		}

		MoveInformation moveInfo = MessageParser
				.getMoveInformation(detailParams);

		assertEquals(MoveType.SHOW_CARDS, moveInfo.getType());
		CardList ouvertCards = moveInfo.getOuvertCards();
		assertEquals(6, ouvertCards.size());
		assertTrue(ouvertCards.contains(Card.HT));
		assertTrue(ouvertCards.contains(Card.HA));
		assertTrue(ouvertCards.contains(Card.SJ));
		assertTrue(ouvertCards.contains(Card.SQ));
		assertTrue(ouvertCards.contains(Card.SK));
		assertTrue(ouvertCards.contains(Card.CJ));
	}

	/**
	 * Tests the announcing of an ouvert game
	 */
	@Test
	public void testParseTableUpdateOuvertGame() {

		String ouvertGame = "table .1 foo play 2 SO.D7.DK.HJ.HQ.HK.S7.S9.ST.SK.C7 180.0 174.2 160.9"; //$NON-NLS-1$

		StringTokenizer token = new StringTokenizer(ouvertGame);
		token.nextToken(); // table
		token.nextToken(); // .1
		String creator = token.nextToken(); // foo
		token.nextToken(); // play
		List<String> detailParams = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			detailParams.add(token.nextToken());
		}

		MoveInformation moveInfo = MessageParser
				.getMoveInformation(detailParams);

		assertEquals(MoveType.GAME_ANNOUNCEMENT, moveInfo.getType());

		GameAnnouncement announcement = moveInfo.getGameAnnouncement();
		assertEquals(GameType.SPADES, announcement.getGameType());
		assertTrue(announcement.isOuvert());

		CardList ouvertCards = moveInfo.getOuvertCards();
		assertEquals(10, ouvertCards.size());
		assertTrue(ouvertCards.contains(Card.D7));
		assertTrue(ouvertCards.contains(Card.DK));
		assertTrue(ouvertCards.contains(Card.HJ));
		assertTrue(ouvertCards.contains(Card.HQ));
		assertTrue(ouvertCards.contains(Card.HK));
		assertTrue(ouvertCards.contains(Card.S7));
		assertTrue(ouvertCards.contains(Card.S9));
		assertTrue(ouvertCards.contains(Card.ST));
		assertTrue(ouvertCards.contains(Card.SK));
		assertTrue(ouvertCards.contains(Card.C7));
	}
}
