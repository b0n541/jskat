package org.jskat.util.rule;


import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameContract;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for suit games
 */
public class SuitRuleTest extends AbstractJSkatTest {

    private static final CardList DISCARDED_CARDS = CardList.of(Card.C7, Card.S7);
    private GameContract contract;

    private static final SkatRule clubsRules = SkatRuleFactory.getSkatRules(GameType.CLUBS);

    @BeforeEach
    public void initialize() {
        contract = new GameContract(GameType.CLUBS);
    }

    /**
     * Checks @see SuitGrandRule#calcGameWon()
     */
    @Test
    public void calcGameWon() {
        final SkatGameData data = new SkatGameData();
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(61);

        assertTrue(clubsRules.isGameWon(data));
    }

    @Test
    public void calcGameWonSchneiderAnnounced() {
        final SkatGameData data = new SkatGameData();
        data.setAnnouncement(new GameAnnouncement(contract.withHand().withSchneider()));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(90);

        assertTrue(clubsRules.isGameWon(data));
    }

    @Test
    public void calcGameLostSchneiderAnnounced() {
        final SkatGameData data = new SkatGameData();
        data.setAnnouncement(new GameAnnouncement(contract.withHand().withSchneider()));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(89);

        assertFalse(clubsRules.isGameWon(data));
    }

    @Test
    public void calcGameWonSchwarzAnnounced() {
        final SkatGameData data = new SkatGameData();
        data.setAnnouncement(new GameAnnouncement(contract.withHand().withSchneider().withSchwarz()));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(120);

        assertTrue(clubsRules.isGameWon(data));
    }

    @Test
    public void calcGameLostSchwarzAnnounced() {
        final SkatGameData data = new SkatGameData();
        data.setAnnouncement(new GameAnnouncement(contract.withHand().withSchneider().withSchwarz()));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(119);

        assertFalse(clubsRules.isGameWon(data));
    }

    /**
     * Checks @see SuitGrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonWithoutJacks() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CA));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(61);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(60);
        assertThat(clubsRules.calcGameResult(data)).isEqualTo(60);
    }

    /**
     * Checks @see SuitGrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJack() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ, Card.DJ, Card.CA));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(61);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(24);
    }

    @Test
    public void calcGameResultGameWonClubJackContra() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ, Card.DJ, Card.CA));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setContra(true);
        data.setDeclarerScore(61);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(48);
    }

    @Test
    public void calcGameResultGameWonClubJackContraRe() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ, Card.DJ, Card.CA));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setContra(true);
        data.setRe(true);
        data.setDeclarerScore(61);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(96);
    }

    @Test
    public void calcGameResultGameLostClubJack() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ, Card.DJ, Card.CA));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(60);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(-48);
    }

    @Test
    public void calcGameResultGameLostClubJackContra() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ, Card.DJ, Card.CA));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setContra(true);
        data.setDeclarerScore(60);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(-96);
    }

    @Test
    public void calcGameResultGameLostClubJackContraRe() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ, Card.DJ, Card.CA));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setContra(true);
        data.setRe(true);
        data.setDeclarerScore(60);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(-192);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchneider() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(90);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(36);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchneiderSchwarz() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(120);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(48);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchneiderAndAnnounced() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setAnnouncement(new GameAnnouncement(contract.withHand().withSchneider()));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(90);
        data.getGameResult().setSchneider(true);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(60);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchwarzAndAnnounced() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setAnnouncement(new GameAnnouncement(contract.withHand().withSchneider().withSchwarz()));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(120);
        data.getGameResult().setSchneider(true);
        data.getGameResult().setSchwarz(true);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(84);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubSpadeHeartJack() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ, Card.HJ, Card.CA));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(61);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(48);
        assertThat(clubsRules.calcGameResult(data)).isEqualTo(48);
    }

    /**
     * Checks @see SuitGrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubSpadeHeartDiamondJack() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CT));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(61);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(60);
        assertThat(clubsRules.calcGameResult(data)).isEqualTo(60);
    }

    /**
     * Checks @see SuitGrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonMoreTops() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ, Card.HJ, Card.DJ, Card.CA, Card.CT, Card.CQ));
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarer(Player.FOREHAND);
        data.setDeclarerScore(61);

        data.calcResult();

        assertThat(data.getResult().getGameValue()).isEqualTo(84);
        assertThat(clubsRules.calcGameResult(data)).isEqualTo(84);
    }

    /**
     * Test for overbidding<br />
     * Middle hand wins the bidding with 22<br/>
     * Picks up skat<br />
     * Announces Diamonds without one (has SJ, HJ, DJ)<br />
     * Middle hand makes 89 points<br />
     * Game is lost because of overbidding
     */
    @Test
    public void testOverbid() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.MIDDLEHAND, new CardList(Card.SJ, Card.HJ, Card.DJ));
        data.addPlayerBid(Player.MIDDLEHAND, 22);
        data.setAnnouncement(new GameAnnouncement(new GameContract(GameType.DIAMONDS), DISCARDED_CARDS));
        data.setDeclarer(Player.MIDDLEHAND);
        data.setDeclarerScore(89);

        data.calcResult();

        assertThat(data.getResult().isWon()).isFalse();
        assertThat(data.getResult().getGameValue()).isEqualTo(-54);
        assertThat(data.getResult().getOverBid()).isTrue();
    }

    /**
     * Test for overbidding with schneider<br />
     * Same as before but this time the declarer played the other player
     * schneider, game is won
     */
    @Test
    public void testOverbidWithSchneider() {
        final SkatGameData data = new SkatGameData();
        data.addDealtCards(Player.MIDDLEHAND, new CardList(Card.SJ, Card.HJ, Card.DJ));
        data.addPlayerBid(Player.MIDDLEHAND, 22);
        data.setAnnouncement(new GameAnnouncement(new GameContract(GameType.DIAMONDS), DISCARDED_CARDS));
        data.setDeclarer(Player.MIDDLEHAND);
        data.setDeclarerScore(90);
        data.getResult().setSchneider(true);

        data.calcResult();

        assertTrue(data.getResult().isWon());
        assertThat(data.getResult().getGameValue()).isEqualTo(27);
    }

    @Test
    public void testGetMultiplier() {
        final var rule = new SuitRule();

        assertThat(rule.getMatadors(
                CardList.of(Card.SJ, Card.HJ, Card.DJ, Card.DT, Card.D8, Card.SQ, Card.S9, Card.HA, Card.HK, Card.HQ),
                GameType.DIAMONDS))
                .isEqualTo(1);
        assertThat(rule.getMatadors(
                CardList.of(Card.HJ, Card.DJ, Card.DT, Card.D8, Card.SQ, Card.S9, Card.HA, Card.HK, Card.HQ, Card.H7),
                GameType.DIAMONDS))
                .isEqualTo(2);
    }
}
