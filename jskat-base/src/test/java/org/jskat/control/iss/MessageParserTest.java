package org.jskat.control.iss;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.data.iss.MoveInformation;
import org.jskat.data.iss.MoveType;
import org.jskat.data.iss.PlayerStatus;
import org.jskat.data.iss.TablePanelStatus;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the parsing of the ISS messages
 */
public class MessageParserTest extends AbstractJSkatTest {

    /**
     * Test the parsing of the game summary
     */
    @Test
    public void testParseGameSummary() {

        final String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[24072]ID[541932]DT[2010-12-06/18:59:24/UTC]P0[zoot]P1[kermit]P2[foo]R0[]R1[]R2[0.0]MV[w HA.SK.SJ.SA.CQ.S8.C9.H7.H9.DQ.CJ.S9.DJ.S7.D9.SQ.C8.HQ.DK.CA.D8.D7.DT.CT.ST.C7.HK.DA.HT.HJ.H8.CK 1 p 2 18 0 p 2 s w H8.CK 2 D.ST.H8 0 SA 1 S7 2 DA 2 HJ 0 SJ 1 D9 0 DQ 1 DJ 2 D7 1 CJ 2 D8 0 CQ 1 CA 2 C7 0 C9 1 S9 2 DT 0 S8 2 CT 0 H7 1 C8 2 CK 0 H9 1 HQ 2 HK 0 HA 1 SQ 0 SK 1 DK 2 HT ]R[d:2 loss v:-54 m:-2 bidok p:59 t:4 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:0] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

        assertThat(gameData.getPlayerName(Player.FOREHAND)).isEqualTo("zoot");
        assertThat(gameData.getPlayerName(Player.MIDDLEHAND)).isEqualTo("kermit");
        assertThat(gameData.getPlayerName(Player.REARHAND)).isEqualTo("foo");

        assertThat(gameData.getDealtCards().get(Player.FOREHAND)).hasSize(10);
        for (final Card card : Arrays.asList(Card.HA, Card.SK, Card.SJ, Card.SA, Card.CQ, Card.S8, Card.C9, Card.H7, Card.H9, Card.DQ)) {
            assertTrue(gameData.getDealtCards().get(Player.FOREHAND).contains(card));
        }
        assertThat(gameData.getDealtCards().get(Player.MIDDLEHAND)).hasSize(10);
        for (final Card card : Arrays.asList(Card.CJ, Card.S9, Card.DJ, Card.S7, Card.D9, Card.SQ, Card.C8, Card.HQ, Card.DK, Card.CA)) {
            assertTrue(gameData.getDealtCards().get(Player.MIDDLEHAND).contains(card));
        }
        assertThat(gameData.getDealtCards().get(Player.REARHAND)).hasSize(10);
        for (final Card card : Arrays.asList(Card.D8, Card.D7, Card.DT, Card.CT, Card.ST, Card.C7, Card.HK, Card.DA, Card.HT, Card.HJ)) {
            assertTrue(gameData.getDealtCards().get(Player.REARHAND).contains(card));
        }

        assertThat(gameData.getDealtSkat()).containsExactlyInAnyOrder(Card.H8, Card.CK);
        assertThat(gameData.getSkat()).containsExactlyInAnyOrder(Card.ST, Card.H8);

        assertThat(gameData.getMaxPlayerBid(Player.FOREHAND)).isEqualTo(0);
        assertThat(gameData.getMaxPlayerBid(Player.MIDDLEHAND)).isEqualTo(0);
        assertThat(gameData.getMaxPlayerBid(Player.REARHAND)).isEqualTo(18);
        assertThat(gameData.getMaxBidValue()).isEqualTo(18);
        assertThat(gameData.getDeclarer()).isEqualTo(Player.REARHAND);

        assertTrue(gameData.getDealtSkat().contains(Card.H8));
        assertTrue(gameData.getDealtSkat().contains(Card.CK));

        assertThat(gameData.getGameType()).isEqualTo(GameType.DIAMONDS);
        assertFalse(gameData.isHand());
        assertFalse(gameData.isOuvert());
        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchneiderAnnounced());
        assertFalse(gameData.isSchwarz());
        assertFalse(gameData.isSchwarzAnnounced());

        assertThat(gameData.getTricks()).hasSize(10);

        final List<Trick> tricks = gameData.getTricks();

        checkTrick(tricks.get(0), Player.FOREHAND, Card.SA, Card.S7, Card.DA, Player.REARHAND);
        checkTrick(tricks.get(1), Player.REARHAND, Card.HJ, Card.SJ, Card.D9, Player.FOREHAND);
        checkTrick(tricks.get(2), Player.FOREHAND, Card.DQ, Card.DJ, Card.D7, Player.MIDDLEHAND);
        checkTrick(tricks.get(3), Player.MIDDLEHAND, Card.CJ, Card.D8, Card.CQ, Player.MIDDLEHAND);
        checkTrick(tricks.get(4), Player.MIDDLEHAND, Card.CA, Card.C7, Card.C9, Player.MIDDLEHAND);
        checkTrick(tricks.get(5), Player.MIDDLEHAND, Card.S9, Card.DT, Card.S8, Player.REARHAND);
        checkTrick(tricks.get(6), Player.REARHAND, Card.CT, Card.H7, Card.C8, Player.REARHAND);
        checkTrick(tricks.get(7), Player.REARHAND, Card.CK, Card.H9, Card.HQ, Player.REARHAND);
        checkTrick(tricks.get(8), Player.REARHAND, Card.HK, Card.HA, Card.SQ, Player.FOREHAND);
        checkTrick(tricks.get(9), Player.FOREHAND, Card.SK, Card.DK, Card.HT, Player.MIDDLEHAND);

        assertFalse(gameData.isGameWon());
        assertThat(gameData.getResult().getGameValue()).isEqualTo(-54);
        assertThat(gameData.getResult().isPlayWithJacks()).isFalse();
        assertThat(gameData.getResult().getMultiplier()).isEqualTo(2);
        assertThat(gameData.getDeclarerScore()).isEqualTo(59);
        assertThat(gameData.getOpponentScore()).isEqualTo(61);
        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());
        assertFalse(gameData.isOverBid());
    }

    private static void checkTrick(final Trick trick,
                                   final Player trickForeHand, final Card firstCard,
                                   final Card secondCard, final Card thirdCard,
                                   final Player trickWinner) {
        final int trickNo = trick.getTrickNumberInGame();
        assertThat(trick.getForeHand()).isEqualTo(trickForeHand);
        assertThat(trick.getFirstCard()).isEqualTo(firstCard);
        assertThat(trick.getSecondCard()).isEqualTo(secondCard);
        assertThat(trick.getThirdCard()).isEqualTo(thirdCard);
        assertThat(trick.getTrickWinner()).isEqualTo(trickWinner);
    }

    /**
     * Test the parsing of the game summary
     */
    @Test
    public void testParseGameSummary002() {

        final String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[29859]ID[684159]DT[2011-04-05/20:35:55/UTC]P0[foo]P1[xskat:2]P2[xskat]R0[0.0]R1[]R2[]MV[w SQ.DK.ST.S7.CT.HK.S9.SK.H7.C7.DT.CA.CQ.CK.DJ.DA.H8.SA.D7.C8.SJ.HA.CJ.S8.C9.DQ.HJ.HQ.D9.D8.HT.H9 1 18 0 y 1 20 0 y 1 22 0 y 1 23 0 y 1 24 0 y 1 p 2 27 0 p 2 s w HT.H9 2 G.S8.C9 0 DK 1 DA 2 D8 1 DT 2 D9 0 ST 1 SA 2 CJ 0 S7 2 HJ 0 CT 1 DJ 2 DQ 0 C7 1 D7 2 HA 0 H7 1 H8 2 HT 0 HK 1 C8 2 HQ 0 S9 1 CQ 2 H9 0 SQ 1 CK 2 SJ 0 SK 1 CA ]R[d:2 win v:96 m:3 bidok p:85 t:8 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:0] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

        assertThat(gameData.getPlayerName(Player.FOREHAND)).isEqualTo("foo");
        assertThat(gameData.getPlayerName(Player.MIDDLEHAND)).isEqualTo("xskat:2");
        assertThat(gameData.getPlayerName(Player.REARHAND)).isEqualTo("xskat");

        assertThat(gameData.getMaxPlayerBid(Player.FOREHAND)).isEqualTo(24);
        assertThat(gameData.getMaxPlayerBid(Player.MIDDLEHAND)).isEqualTo(24);
        assertThat(gameData.getMaxPlayerBid(Player.REARHAND)).isEqualTo(27);
        assertThat(gameData.getMaxBidValue()).isEqualTo(27);
        assertThat(gameData.getDeclarer()).isEqualTo(Player.REARHAND);

        assertThat(gameData.getDealtSkat()).containsExactlyInAnyOrder(Card.HT, Card.H9);

        assertThat(gameData.getGameType()).isEqualTo(GameType.GRAND);
        assertFalse(gameData.isHand());
        assertFalse(gameData.isOuvert());
        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchneiderAnnounced());
        assertFalse(gameData.isSchwarz());
        assertFalse(gameData.isSchwarzAnnounced());

        assertThat(gameData.getTricks()).hasSize(10);

        final List<Trick> tricks = gameData.getTricks();

        checkTrick(tricks.get(0), Player.FOREHAND, Card.DK, Card.DA, Card.D8, Player.MIDDLEHAND);
        checkTrick(tricks.get(1), Player.MIDDLEHAND, Card.DT, Card.D9, Card.ST, Player.MIDDLEHAND);
        checkTrick(tricks.get(2), Player.MIDDLEHAND, Card.SA, Card.CJ, Card.S7, Player.REARHAND);
        checkTrick(tricks.get(3), Player.REARHAND, Card.HJ, Card.CT, Card.DJ, Player.REARHAND);
        checkTrick(tricks.get(4), Player.REARHAND, Card.DQ, Card.C7, Card.D7, Player.REARHAND);
        checkTrick(tricks.get(5), Player.REARHAND, Card.HA, Card.H7, Card.H8, Player.REARHAND);
        checkTrick(tricks.get(6), Player.REARHAND, Card.HT, Card.HK, Card.C8, Player.REARHAND);
        checkTrick(tricks.get(7), Player.REARHAND, Card.HQ, Card.S9, Card.CQ, Player.REARHAND);
        checkTrick(tricks.get(8), Player.REARHAND, Card.H9, Card.SQ, Card.CK, Player.REARHAND);
        checkTrick(tricks.get(9), Player.REARHAND, Card.SJ, Card.SK, Card.CA, Player.REARHAND);

        assertTrue(gameData.isGameWon());
        assertThat(gameData.getResult().getGameValue()).isEqualTo(96);
        assertThat(gameData.getResult().isPlayWithJacks()).isTrue();
        assertThat(gameData.getResult().getMultiplier()).isEqualTo(3);
        assertThat(gameData.getDeclarerScore()).isEqualTo(85);
        assertThat(gameData.getOpponentScore()).isEqualTo(35);
        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());
        assertFalse(gameData.isOverBidded());
    }

    @Test
    public void testParseGameSummary_IncompleteGrandOuvertAnnouncement() {

        final String gameSummary = "(;GM[Skat]PC[Internet Skat Server]SE[1265]ID[727]DT[2007-11-19/23:42:26/UTC]P0[bonsai]P1[mic]P2[Legolaus]R0[0.0]R1[0.0]R2[0.0]MV[w HJ.HA.DJ.HQ.SA.H7.CA.CJ.HT.H8.H9.D7.DT.C7.ST.CK.C9.SK.S9.CT.C8.SQ.HK.S8.D9.DK.SJ.D8.CQ.DA.DQ.S7 1 p 2 p 0 18 0 GO 0 CJ 0 SC 1 RE 1 H9 2 RE ]R[d:0 win v:192 m:1 bidok p:120 t:10 s:1 z:1 p0:0 p1:0 p2:0 l:-1 to:-1] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);
    }

    @Test
    public void testParseGameSummary_IncompleteHandSchneiderSchwarzAnnouncement() {

        final String gameSummary = "(;GM[Skat]PC[Internet Skat Server]CO[]SE[2511]ID[26496]DT[2008-06-22/11:14:12/UTC]P0[Madmax]P1[kermit1]P2[kermit2]R0[0.0]R1[0.0]R2[0.0]MV[w C7.SA.SJ.CJ.CK.HJ.S7.SK.C9.ST.DQ.C8.DA.SQ.D7.S8.DT.H7.DK.DJ.S9.HA.HT.D9.HK.CT.CA.H8.HQ.H9.CQ.D8 1 18 0 y 1 20 0 y 1 22 0 y 1 23 0 y 1 p 2 24 0 y 2 27 0 y 2 30 0 y 2 33 0 y 2 35 0 y 2 36 0 y 2 40 0 y 2 p 0 CHZ 0 CJ 1 C8 2 CT 0 SJ 1 DJ 2 CA 0 HJ 1 H7 2 H8 0 CK 1 S8 2 D9 0 C9 1 DQ 2 H9 0 C7 1 SQ 2 S9 0 SA 1 D7 2 HQ 0 ST 1 DK 2 HA 0 SK 1 DA 2 HT 0 S7 1 DT 2 HK ]R[d:0 win v:108 m:3 bidok p:120 t:10 s:1 z:1 p0:0 p1:0 p2:0 l:-1 to:-1] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);
    }

    @Test
    @Disabled
    public static void testParseGameSummary_IndexOutOfBoundsException() {

        final String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[26702]ID[596891]DT[2011-02-02/18:10:12/UTC]P0[xskat]P1[Knesebec]P2[bernie]R0[]R1[0.0]R2[]MV[w SJ.D8.D7.DA.HT.CT.DT.CK.DJ.S8.H9.HA.HQ.SK.C8.C7.H8.S7.S9.C9.D9.DQ.CQ.HJ.SQ.SA.CJ.H7.CA.ST.DK.HK 1 18 0 y 1 20 0 p 2 22 1 y 2 23 1 y 2 24 1 y 2 27 1 y 2 30 1 y 2 33 1 y 2 35 1 y 2 36 1 p 2 s w DK.HK 2 D 2 D9.DQ 0 S8 1 S9 2 SA 2 ST 0 DA 1 SK 0 CK 1 C7 2 CQ 0 D7 1 S7 2 DK 2 CA 0 CT 1 C8 2 CJ 0 D8 1 C9 2 SQ 0 DT 1 HA 0 SJ 1 HQ 2 HJ 0 DJ 1 H9 2 HK 0 HT 1 H8 2 H7 ]R[d:2 loss v:-72 m:1 overbid p:41 t:4 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:0] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);
    }

    /**
     * Test the parsing of the game summary<br>
     * Passed game
     */
    @Test
    public void testParseGameSummary_PassedGame() {

        final String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[32407]ID[756788]DT[2011-05-28/08:46:19/UTC]P0[xskat]P1[bonsai]P2[bernie]R0[]R1[0.0]R2[]MV[w C8.DQ.DJ.HK.S9.SK.SQ.HQ.CK.D9.S8.DT.SJ.C9.CQ.SA.DK.HT.D7.H7.ST.HJ.C7.H8.S7.DA.CJ.CT.D8.H9.CA.HA 1 p 2 p 0 p ]R[passed] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

        assertTrue(gameData.isGamePassed());
        assertThat(gameData.getResult().getGameValue()).isEqualTo(0);

        assertThat(gameData.getPlayerName(Player.FOREHAND)).isEqualTo("xskat");
        assertThat(gameData.getPlayerName(Player.MIDDLEHAND)).isEqualTo("bonsai");
        assertThat(gameData.getPlayerName(Player.REARHAND)).isEqualTo("bernie");

        assertThat(gameData.getMaxPlayerBid(Player.FOREHAND)).isEqualTo(0);
        assertThat(gameData.getMaxPlayerBid(Player.MIDDLEHAND)).isEqualTo(0);
        assertThat(gameData.getMaxPlayerBid(Player.REARHAND)).isEqualTo(0);
        assertThat(gameData.getMaxBidValue()).isEqualTo(0);
        assertNull(gameData.getDeclarer());

        assertTrue(gameData.getDealtSkat().contains(Card.CA));
        assertTrue(gameData.getDealtSkat().contains(Card.HA));

        assertThat(gameData.getGameType()).isEqualTo(GameType.PASSED_IN);
        assertFalse(gameData.isHand());
        assertFalse(gameData.isOuvert());
        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchneiderAnnounced());
        assertFalse(gameData.isSchwarz());
        assertFalse(gameData.isSchwarzAnnounced());

        assertThat(gameData.getTricks().size()).isEqualTo(0);

        assertFalse(gameData.isGameWon());
        assertThat(gameData.getResult().getGameValue()).isEqualTo(0);
        assertThat(gameData.getResult().isPlayWithJacks()).isFalse();
        assertThat(gameData.getResult().getMultiplier()).isEqualTo(0);
        assertThat(gameData.getDeclarerScore()).isEqualTo(0);
        assertThat(gameData.getOpponentScore()).isEqualTo(0);
        assertFalse(gameData.isSchneider());
        assertFalse(gameData.isSchwarz());
        assertFalse(gameData.isOverBidded());
    }

    /**
     * Test the parsing of the game summary<br>
     * Declarer shows cards and plays last card directly afterwards
     */
    @Test
    public void testParseGameSummary_ShowingCards() {

        final String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[43795]ID[1039093]DT[2012-01-17/00:07:25/UTC]P0[SkatCLE]P1[SkatKCT]P2[xskat]R0[0.0]R1[0.0]R2[]MV[w DA.S7.DK.CA.D9.CQ.CK.H9.S8.C7.HJ.DT.HA.CT.S9.C9.ST.H8.D7.DJ.HQ.DQ.SK.HK.SQ.H7.C8.D8.SJ.HT.SA.CJ 1 18 0 p 2 p 1 s w SA.CJ 1 G.CT.DT 0 CA 1 C9 2 C8 0 CK 1 D7 2 HT 0 CQ 1 DJ 2 SJ 2 H7 0 H9 1 H8 0 S8 1 S9 2 SK 2 D8 0 DA 1 SC 1 HJ 2 RE 0 RE ]R[d:1 win v:48 m:1 bidok p:84 t:5 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:1] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

        assertThat(gameData.getGameType()).isEqualTo(GameType.GRAND);
    }

    /**
     * Test the parsing of the game summary<br>
     * Declarer plays Null Ouvert and the opponents resign
     */
    @Test
    public void testParseGameSummary_NullOuvertTwoResigns() {

        final String gameSummary = "(;GM[Skat]PC[International Skat Server]CO[]SE[60842]ID[1390253]DT[2012-09-26/17:30:29/UTC]P0[kermit]P1[bonsai]P2[zoot]R0[]R1[0.0]R2[]MV[w HK.SK.HJ.CA.SA.C9.S8.H7.ST.HT.C7.HA.CJ.DK.HQ.D7.C8.DA.DT.D9.SQ.DQ.H8.S9.CK.S7.SJ.CT.H9.CQ.DJ.D8 1 18 0 y 1 20 0 y 1 22 0 y 1 23 0 y 1 24 0 y 1 27 0 y 1 30 0 y 1 33 0 y 1 35 0 p 2 p 1 s w DJ.D8 1 NO.HA.HQ 2 RE 0 RE ]R[d:1 win v:46 m:0 bidok p:14 t:0 s:0 z:0 p0:0 p1:0 p2:0 l:-1 to:-1 r:1] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);

        assertThat(gameData.getGameType()).isEqualTo(GameType.NULL);
        assertTrue(gameData.isGameWon());
    }

    @Test
    public void testParseGameSummary_PlayerLeft() {

        final String gameSummary = "(;GM[Skat]PC[Internet Skat Server]SE[146]ID[30]DT[2007-11-04/15:39:49/UTC]P0[bonsai]P1[bar]P2[foo]R0[0.0]R1[0.0]R2[null]MV[w SJ.D9.CA.DJ.SA.CJ.C8.SQ.HT.H8.ST.S8.DA.D7.S9.D8.C7.H7.DT.S7.CT.HJ.C9.SK.H9.HA.HK.DQ.CK.HQ.DK.CQ 1 18 0 p w LE.2 ]R[d:-1 penalty v:0 m:0 bidok p:0 t:0 s:0 z:0 p0:0 p1:0 p2:1 l:2 to:-1] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);
    }

    @Test
    @Disabled
    public void testParseGameSummary_CorruptData() {

        final String gameSummary = "(;GM[Skat]PC[Internet Skat Server]SE[2171]ID[18358]DT[2008-05-25/17:57:24/UTC]P0[kermit2]P1[kermit1]P2[mic]R0[0.0]R1[null]R2[0.0]MV[w C9.D7.HT.HA.SA.HQ.SJ.S7.C8.HJ.H7.CQ.DQ.D9.H9.S8.H8.CT.HK.SQ.CA.CK.SK.CJ.D8.DT.DK.C7.DJ.S9.ST.DA 1 18 0 p 2 20 1 p 2 s w ST.DA 2 G.DT.ST 0 ?? w LE.1 ]R[d:2 win v:96 m:1 bidok p:120 t:10 s:1 z:1 p0:0 p1:0 p2:0 l:1 to:-1] ;)";

        final SkatGameData gameData = MessageParser.parseGameSummary(gameSummary);
    }

    @Test
    public void testParseTableUpdatePlayerLeft() {

        final String tableUpdate = "table .4 foo state 3 foo . . . foo . 0 0 0 0 0 0 1 0 xskat $ 2 1 83 157 0 0 1 0 xskat:2 $ 0 0 0 0 0 0 1 0 . . 0 0 0 0 0 0 0 0 false 0";

        final StringTokenizer token = new StringTokenizer(tableUpdate);
        token.nextToken(); // table
        token.nextToken(); // .4
        final String creator = token.nextToken(); // foo
        token.nextToken(); // state
        final List<String> detailParams = new ArrayList<>();
        while (token.hasMoreTokens()) {
            detailParams.add(token.nextToken());
        }

        final TablePanelStatus status = MessageParser.getTableStatus(creator, detailParams);

        assertThat(status.getMaxPlayers()).isEqualTo(3);
        assertThat(status.getPlayerInformation()).hasSize(3);

        final PlayerStatus playerStatus = status.getPlayerInformation("xskat");
        assertNotNull(playerStatus);
        assertThat(playerStatus.getGamesPlayed()).isEqualTo(2);
        assertThat(playerStatus.getGamesWon()).isEqualTo(1);
        assertThat(playerStatus.getLastGameResult()).isEqualTo(83);
        assertThat(playerStatus.getTotalPoints()).isEqualTo(157);
        assertTrue(playerStatus.isPlayerLeft());
        assertFalse(playerStatus.isReadyToPlay());
        assertTrue(playerStatus.isTalkEnabled());
    }

    @Test
    public void testParseTableUpdatePlayerResign() {

        final String playerResign = "table .4 foo play 1 RE 124.1 173.9 177.8";

        final StringTokenizer token = new StringTokenizer(playerResign);
        token.nextToken(); // table
        token.nextToken(); // .4
        final String creator = token.nextToken(); // foo
        token.nextToken(); // play
        final List<String> detailParams = new ArrayList<>();
        while (token.hasMoreTokens()) {
            detailParams.add(token.nextToken());
        }

        final MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

        assertThat(moveInfo.getType()).isEqualTo(MoveType.RESIGN);
    }

    @Test
    public void testParseTableUpdatePlayerShowCards() {

        final String playerResign = "table .3 foo play 2 SC.HT.HA.SJ.SQ.SK.CJ 164.1 177.0 156.4";

        final StringTokenizer token = new StringTokenizer(playerResign);
        token.nextToken(); // table
        token.nextToken(); // .3
        final String creator = token.nextToken(); // foo
        token.nextToken(); // play
        final List<String> detailParams = new ArrayList<>();
        while (token.hasMoreTokens()) {
            detailParams.add(token.nextToken());
        }

        final MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

        assertThat(moveInfo.getType()).isEqualTo(MoveType.SHOW_CARDS);
        assertThat(moveInfo.getOuvertCards())
                .containsExactlyInAnyOrder(Card.HT, Card.HA, Card.SJ, Card.SQ, Card.SK, Card.CJ);
    }

    @Test
    public void testParseTableUpdateNullOuvertGame_JSkatUser() {

        final String ouvertGame = "table .0 foo play 1 NO.DJ.SA.D8.D9.DQ.H8.HT.HQ.C7.C8.CK.CA 237.6 225.8 237.7";
        final StringTokenizer token = new StringTokenizer(ouvertGame);
        token.nextToken(); // table
        token.nextToken(); // .1
        final String creator = token.nextToken(); // foo
        token.nextToken(); // play
        final List<String> detailParams = new ArrayList<>();
        while (token.hasMoreTokens()) {
            detailParams.add(token.nextToken());
        }

        final MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

        assertThat(moveInfo.getType()).isEqualTo(MoveType.GAME_ANNOUNCEMENT);

        final GameAnnouncement announcement = moveInfo.getGameAnnouncement();
        assertThat(announcement.getGameType()).isEqualTo(GameType.NULL);
        assertTrue(announcement.isOuvert());
        assertThat(announcement.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);

        assertThat(moveInfo.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);
    }

    @Test
    public void testParseTableUpdateNullOuvertGame_OtherPlayer() {

        final String ouvertGame = "table .0 foo play 1 NO.??.??.D8.D9.DQ.H8.HT.HQ.C7.C8.CK.CA 237.6 225.8 237.7";
        final StringTokenizer token = new StringTokenizer(ouvertGame);
        token.nextToken(); // table
        token.nextToken(); // .1
        final String creator = token.nextToken(); // foo
        token.nextToken(); // play
        final List<String> detailParams = new ArrayList<>();
        while (token.hasMoreTokens()) {
            detailParams.add(token.nextToken());
        }

        final MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

        assertThat(moveInfo.getType()).isEqualTo(MoveType.GAME_ANNOUNCEMENT);

        final GameAnnouncement announcement = moveInfo.getGameAnnouncement();
        assertThat(announcement.getGameType()).isEqualTo(GameType.NULL);
        assertTrue(announcement.isOuvert());
        assertThat(announcement.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);

        assertThat(moveInfo.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);
    }

    @Test
    public void testParseTableUpdateSuitHandOuvertGame_JSkatUser() {

        final String ouvertGame = "table .0 foo play 1 SHO.D8.D9.DQ.H8.HT.HQ.C7.C8.CK.CA 237.6 225.8 237.7";
        final StringTokenizer token = new StringTokenizer(ouvertGame);
        token.nextToken(); // table
        token.nextToken(); // .1
        final String creator = token.nextToken(); // foo
        token.nextToken(); // play
        final List<String> detailParams = new ArrayList<>();
        while (token.hasMoreTokens()) {
            detailParams.add(token.nextToken());
        }

        final MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

        assertThat(moveInfo.getType()).isEqualTo(MoveType.GAME_ANNOUNCEMENT);

        final GameAnnouncement announcement = moveInfo.getGameAnnouncement();
        assertThat(announcement.getGameType()).isEqualTo(GameType.SPADES);
        assertTrue(announcement.isHand());
        assertTrue(announcement.isOuvert());
        assertThat(announcement.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);

        assertThat(moveInfo.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);
    }

    @Test
    public void testParseTableUpdateSuitHandOuvertGame_OtherPlayer() {

        final String ouvertGame = "table .0 foo play 1 SHO.D8.D9.DQ.H8.HT.HQ.C7.C8.CK.CA 237.6 225.8 237.7";
        final StringTokenizer token = new StringTokenizer(ouvertGame);
        token.nextToken(); // table
        token.nextToken(); // .1
        final String creator = token.nextToken(); // foo
        token.nextToken(); // play
        final List<String> detailParams = new ArrayList<>();
        while (token.hasMoreTokens()) {
            detailParams.add(token.nextToken());
        }

        final MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

        assertThat(moveInfo.getType()).isEqualTo(MoveType.GAME_ANNOUNCEMENT);

        final GameAnnouncement announcement = moveInfo.getGameAnnouncement();
        assertThat(announcement.getGameType()).isEqualTo(GameType.SPADES);
        assertTrue(announcement.isHand());
        assertTrue(announcement.isOuvert());
        assertThat(announcement.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);

        assertThat(moveInfo.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);
    }

    @Test
    public void testParseTableUpdateGrandHandOuvertGame_JSkatUser() {

        final String ouvertGame = "table .0 foo play 1 GHO.D8.D9.DQ.H8.HT.HQ.C7.C8.CK.CA 237.6 225.8 237.7";
        final StringTokenizer token = new StringTokenizer(ouvertGame);
        token.nextToken(); // table
        token.nextToken(); // .1
        final String creator = token.nextToken(); // foo
        token.nextToken(); // play
        final List<String> detailParams = new ArrayList<>();
        while (token.hasMoreTokens()) {
            detailParams.add(token.nextToken());
        }

        final MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

        assertThat(moveInfo.getType()).isEqualTo(MoveType.GAME_ANNOUNCEMENT);

        final GameAnnouncement announcement = moveInfo.getGameAnnouncement();
        assertThat(announcement.getGameType()).isEqualTo(GameType.GRAND);
        assertTrue(announcement.isHand());
        assertTrue(announcement.isOuvert());
        assertThat(announcement.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);

        assertThat(moveInfo.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);
    }

    @Test
    public void testParseTableUpdateGrandHandOuvertGame_OtherPlayer() {

        final String ouvertGame = "table .0 foo play 1 GHO.D8.D9.DQ.H8.HT.HQ.C7.C8.CK.CA 237.6 225.8 237.7";
        final StringTokenizer token = new StringTokenizer(ouvertGame);
        token.nextToken(); // table
        token.nextToken(); // .1
        final String creator = token.nextToken(); // foo
        token.nextToken(); // play
        final List<String> detailParams = new ArrayList<>();
        while (token.hasMoreTokens()) {
            detailParams.add(token.nextToken());
        }

        final MoveInformation moveInfo = MessageParser.getMoveInformation(detailParams);

        assertThat(moveInfo.getType()).isEqualTo(MoveType.GAME_ANNOUNCEMENT);

        final GameAnnouncement announcement = moveInfo.getGameAnnouncement();
        assertThat(announcement.getGameType()).isEqualTo(GameType.GRAND);
        assertTrue(announcement.isHand());
        assertTrue(announcement.isOuvert());
        assertThat(announcement.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);

        assertThat(moveInfo.getOuvertCards())
                .containsExactlyInAnyOrder(
                        Card.D8, Card.D9, Card.DQ, Card.H8, Card.HT, Card.HQ, Card.C7, Card.C8, Card.CK, Card.CA);
    }
}
