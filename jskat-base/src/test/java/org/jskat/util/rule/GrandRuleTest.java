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

/**
 * Tests for Grand rules
 */
public class GrandRuleTest extends AbstractJSkatTest {

    private static final CardList DISCARDED_CARDS = CardList.of(Card.D7, Card.H7);
    private GameContract contract;

    @BeforeEach
    public void initialize() {
        contract = new GameContract(GameType.GRAND);
    }

    /**
     * Checks @see GrandRule#calcGameWon()
     */
    @Test
    public void calcGameWon() {
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarerScore(61);
        data.calcResult();
        assertThat(data.getResult().isWon()).isTrue();
    }

    @Test
    public void calcGameWonSchneiderAnnounced() {
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract.withSchneider()));
        data.setDeclarerScore(90);
        data.calcResult();
        assertThat(data.getResult().isWon()).isTrue();
    }

    @Test
    public void calcGameLostSchneiderAnnounced() {
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract.withSchneider()));
        data.setDeclarerScore(89);
        data.calcResult();
        assertThat(data.getResult().isWon()).isFalse();
    }

    @Test
    public void calcGameWonSchwarzAnnounced() {
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract.withSchwarz()));
        data.setDeclarerScore(120);
        data.calcResult();
        assertThat(data.getResult().isWon()).isTrue();
    }

    @Test
    public void calcGameLostSchwarzAnnounced() {
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract.withSchwarz()));
        data.setDeclarerScore(119);
        data.calcResult();
        assertThat(data.getResult().isWon()).isFalse();
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameLost() {
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarerScore(60);
        data.calcResult();
        assertThat(data.isGameLost()).isTrue();
    }

    @Test
    public void calcGameResultGameLostClubJack() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarerScore(60);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-96);
    }

    @Test
    public void calcGameResultGameLostClubJackContra() {
        final var data = new SkatGameData();
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setContra(true);
        data.setDeclarerScore(60);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-192);
    }

    @Test
    public void calcGameResultGameLostClubJackContraRe() {
        final var data = new SkatGameData();
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
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
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarerScore(61);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(48);
    }

    @Test
    public void calcGameResultGameWonClubJackContra() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarer(Player.FOREHAND);
        data.addSkatToPlayer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setContra(true);
        data.setDeclarerScore(61);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(96);
    }

    @Test
    public void calcGameResultGameWonClubJackContraRe() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.addSkatToPlayer(Player.FOREHAND);
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setContra(true);
        data.setRe(true);
        data.setDeclarerScore(61);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(192);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonHandWithClubAndSpadeJack() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ, Card.DJ));
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarerScore(61);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(72);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonWithClubAndHeartJack() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarerScore(61);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(48);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonHandWithClubJack() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract.withHand()));
        data.setDeclarerScore(61);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(72);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchneider() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarerScore(90);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(72);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonClubJackSchneiderSchwarz() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarerScore(120);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(96);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonSchneiderAnnouncedClubJack() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract.withSchneider()));
        data.setDeclarerScore(90);
        data.getGameResult().setSchneider(true);
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(120);
    }

    /**
     * Checks @see GrandRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonSchwarzAnnouncedClubJack() {
        final var data = new SkatGameData();
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.HJ));
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract.withSchwarz()));
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
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
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
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
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
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
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
        final var data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setDeclarerScore(120);
        data.addDealtCards(Player.FOREHAND, new CardList(Card.CJ, Card.SJ, Card.HJ, Card.DJ));
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(168);
    }
}
