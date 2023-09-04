package org.jskat.util.rule;


import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
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

    private SkatGameData data;
    private GameAnnouncement.Builder builder;

    private final CardList OUVERT_CARDS = CardList.of(
            Card.CJ, Card.SJ, Card.HJ, Card.DJ,
            Card.CA, Card.SA, Card.HA, Card.DA,
            Card.CT, Card.ST);
    private static final SkatRule nullRules = SkatRuleFactory.getSkatRules(GameType.NULL);

    @BeforeEach
    public void initialize() {
        data = new SkatGameData();
        builder = GameAnnouncement.builder(GameType.NULL);
        data.setDeclarer(Player.FOREHAND);
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
        data.setAnnouncement(builder.build());
        playWinningTricks();
        data.calcResult();
        assertTrue(data.getResult().isWon());
    }

    /**
     * Checks @see NullRule#calcGameWon()
     */
    @Test
    public void calcGameLost() {
        data.setAnnouncement(builder.build());
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
        data.setAnnouncement(builder.build());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(23);
    }

    @Test
    public void calcGameResultGameWonContra() {
        data.setAnnouncement(builder.build());
        data.setContra(true);
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(46);
    }

    @Test
    public void calcGameResultGameWonContraRe() {
        data.setAnnouncement(builder.build());
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
        data.setAnnouncement(builder.hand().build());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(35);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonOuvert() {
        data.setAnnouncement(builder.ouvert(OUVERT_CARDS).build());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(46);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonHandOuvert() {
        data.setAnnouncement(builder.hand().ouvert(OUVERT_CARDS).build());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(59);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameLost() {
        data.setAnnouncement(builder.build());
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-46);
    }

    @Test
    public void calcGameResultGameLostContra() {
        data.setAnnouncement(builder.build());
        data.setContra(true);
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-92);
    }

    @Test
    public void calcGameResultGameLostContraRe() {
        data.setAnnouncement(builder.build());
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
        data.setAnnouncement(builder.hand().build());
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
        data.setAnnouncement(builder.ouvert(OUVERT_CARDS).build());
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
        data.setAnnouncement(builder.hand().ouvert(OUVERT_CARDS).build());
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
        data.setAnnouncement(builder.build());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-92);
    }
}
