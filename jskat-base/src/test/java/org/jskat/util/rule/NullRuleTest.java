package org.jskat.util.rule;


import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameContract;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
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
 * Tests for NullRule
 */
public class NullRuleTest extends AbstractJSkatTest {

    private static final CardList DISCARDED_CARDS = CardList.of(Card.CA, Card.SA);
    private SkatGameData data;
    private GameContract contract;

    private final CardList OUVERT_CARDS = CardList.of(
            Card.CJ, Card.SJ, Card.HJ, Card.DJ,
            Card.CA, Card.SA, Card.HA, Card.DA,
            Card.CT, Card.ST);
    private static final SkatRule nullRules = SkatRuleFactory.getSkatRules(GameType.NULL);

    @BeforeEach
    public void initialize() {
        data = new SkatGameData();
        data.setDeclarer(Player.FOREHAND);
        contract = new GameContract(GameType.NULL);
    }

    private void playWinningTricks() {
        data.addTrick(new Trick(0, Player.FOREHAND));
        data.addTrickCard(Card.C7);
        data.addTrickCard(Card.C8);
        data.addTrickCard(Card.C9);
        data.setTrickWinner(0, Player.REARHAND);
        data.addTrick(new Trick(1, Player.REARHAND));
        data.addTrickCard(Card.S8);
        data.addTrickCard(Card.S7);
        data.addTrickCard(Card.S9);
        data.setTrickWinner(1, Player.MIDDLEHAND);
    }

    /**
     * Checks @see NullRule#calcGameWon()
     */
    @Test
    public void calcGameWon() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        playWinningTricks();
        data.calcResult();
        assertTrue(data.getResult().isWon());
    }

    /**
     * Checks @see NullRule#calcGameWon()
     */
    @Test
    public void calcGameLost() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertFalse(data.getResult().isWon());
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWon() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(23);
    }

    @Test
    public void calcGameResultGameWonContra() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setContra(true);
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(46);
    }

    @Test
    public void calcGameResultGameWonContraRe() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setContra(true);
        data.setRe(true);
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(92);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonHand() {
        data.setAnnouncement(new GameAnnouncement(contract.withHand()));
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(35);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonOuvert() {
        data.setAnnouncement(new GameAnnouncement(contract.withOuvert(OUVERT_CARDS), DISCARDED_CARDS));
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(46);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonHandOuvert() {
        data.setAnnouncement(new GameAnnouncement(contract.withHand().withOuvert(OUVERT_CARDS)));
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(59);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameLost() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-46);
    }

    @Test
    public void calcGameResultGameLostContra() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setContra(true);
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-92);
    }

    @Test
    public void calcGameResultGameLostContraRe() {
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        data.setContra(true);
        data.setRe(true);
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-184);
    }

    private void playLoosingTrick() {
        data.addTrick(new Trick(2, Player.MIDDLEHAND));
        data.addTrickCard(Card.H7);
        data.addTrickCard(Card.H8);
        data.addTrickCard(Card.H9);
        data.setTrickWinner(2, Player.FOREHAND);
        data.getGameResult().setWon(nullRules.isGameWon(data));
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameLostHand() {
        data.setAnnouncement(new GameAnnouncement(contract.withHand()));
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(nullRules.calcGameResult(data)).isEqualTo(-70);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameLostOuvert() {
        data.setAnnouncement(new GameAnnouncement(contract.withOuvert(OUVERT_CARDS), DISCARDED_CARDS));
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(nullRules.calcGameResult(data)).isEqualTo(-92);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameLostHandOuvert() {
        data.setAnnouncement(new GameAnnouncement(contract.withHand().withOuvert(OUVERT_CARDS)));
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(nullRules.calcGameResult(data)).isEqualTo(-118);
    }

    /**
     * Test for overbidding in null games
     */
    @Test
    public void testOverbid() {
        data.addPlayerBid(Player.FOREHAND, 24);
        data.setAnnouncement(new GameAnnouncement(contract, DISCARDED_CARDS));
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-92);
    }
}
