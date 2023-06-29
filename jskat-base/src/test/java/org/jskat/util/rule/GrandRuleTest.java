package org.jskat.util.rule;


import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
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
 * Tests for Grand rules
 */
public class GrandRuleTest extends AbstractJSkatTest {

    private GameAnnouncementFactory factory;

    @BeforeEach
    public void initialize() {

        factory = GameAnnouncement.getFactory();
        factory.setGameType(GameType.GRAND);
    }

    /**
     * Checks @see GrandRule#calcGameWon()
     */
    @Test
    public void calcGameWon() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(61);
        data.calcResult();
        assertTrue(data.getResult().isWon());
    }

    @Test
    public void calcGameWonSchneiderAnnounced() {
        factory.setHand(Boolean.TRUE);
        factory.setSchneider(Boolean.TRUE);
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(90);
        data.calcResult();
        assertTrue(data.getResult().isWon());
    }

    @Test
    public void calcGameLostSchneiderAnnounced() {
        factory.setHand(Boolean.TRUE);
        factory.setSchneider(Boolean.TRUE);
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(89);
        data.calcResult();
        assertFalse(data.getResult().isWon());
    }

    @Test
    public void calcGameWonSchwarzAnnounced() {
        factory.setHand(Boolean.TRUE);
        factory.setSchneider(Boolean.TRUE);
        factory.setSchwarz(Boolean.TRUE);
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(120);
        data.calcResult();
        assertTrue(data.getResult().isWon());
    }

    @Test
    public void calcGameLostSchwarzAnnounced() {
        factory.setHand(Boolean.TRUE);
        factory.setSchneider(Boolean.TRUE);
        factory.setSchwarz(Boolean.TRUE);
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(119);
        data.calcResult();
        assertFalse(data.getResult().isWon());
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameLost() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(60);
        data.calcResult();
        assertFalse(data.getResult().isWon());
    }

    @Test
    public void calcGameResultGameLostClubJack() {
        final SkatGameData data = new SkatGameData();
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(60);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-96);
    }

    @Test
    public void calcGameResultGameLostClubJackContra() {
        final SkatGameData data = new SkatGameData();
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setContra(true);
        data.setDeclarerScore(60);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-192);
    }

    @Test
    public void calcGameResultGameLostClubJackContraRe() {
        final SkatGameData data = new SkatGameData();
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setContra(true);
        data.setRe(true);
        data.setDeclarerScore(60);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-384);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJack() {
        final SkatGameData data = new SkatGameData();
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(61);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(48);
    }

    @Test
    public void calcGameResultGameWonClubJackContra() {
        final SkatGameData data = new SkatGameData();
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setContra(true);
        data.setDeclarerScore(61);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(96);
    }

    @Test
    public void calcGameResultGameWonClubJackContraRe() {
        final SkatGameData data = new SkatGameData();
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setContra(true);
        data.setRe(true);
        data.setDeclarerScore(61);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(192);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubAndSpadeJack() {
        final SkatGameData data = new SkatGameData();
        factory.setHand(false);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(61);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ, Card.DJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(72);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubAndHeartJack() {
        final SkatGameData data = new SkatGameData();
        factory.setHand(false);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(61);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(48);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackHand() {
        final SkatGameData data = new SkatGameData();
        factory.setHand(true);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(61);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(72);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchneider() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarerScore(90);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(72);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchneiderSchwarz() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(factory.getAnnouncement());
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarerScore(120);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(96);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchneiderAndAnnounced() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        factory.setHand(true);
        factory.setSchneider(true);
        data.setAnnouncement(factory.getAnnouncement());
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarerScore(90);
        data.getGameResult().setSchneider(true);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(120);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchwarzAndAnnounced() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        factory.setHand(true);
        factory.setSchneider(true);
        factory.setSchwarz(true);
        data.setAnnouncement(factory.getAnnouncement());
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarerScore(120);
        data.getGameResult().setSchneider(true);
        data.getGameResult().setSchwarz(true);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(168);
    }

    /**
     * Tests a grand game without 2
     */
    @Test
    public void testGrandWithout2And89PointsForDeclarer() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        factory.setHand(false);
        factory.setSchneider(Boolean.FALSE);
        factory.setSchwarz(Boolean.FALSE);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(89);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.HJ, Card.DJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(72);
    }

    /**
     * Test grand with four
     */
    @Test
    public void testGrandWithFour() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(61);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ, Card.HJ, Card.DJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(120);
    }

    /**
     * Test grand with four schneider
     */
    @Test
    public void testGrandWithFourSchneider() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(90);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ, Card.HJ, Card.DJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(144);
    }

    /**
     * Test grand with four schneider and schwarz
     */
    @Test
    public void testGrandWithFourSchneiderSchwarz() {
        final SkatGameData data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
        data.setDeclarerScore(120);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ, Card.HJ, Card.DJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(168);
    }
}
