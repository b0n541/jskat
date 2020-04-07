/**
 * Copyright (C) 2020 Jan Schäfer (jansch@users.sourceforge.net)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.util.rule;


import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
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
    private GameAnnouncementFactory factory;

    private static final SkatRule nullRules = SkatRuleFactory.getSkatRules(GameType.NULL);

    @BeforeEach
    public void initialize() {

        data = new SkatGameData();
        factory = GameAnnouncement.getFactory();
        factory.setGameType(GameType.NULL);
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

        data.setAnnouncement(factory.getAnnouncement());
        playWinningTricks();
        data.calcResult();
        assertTrue(data.getResult().isWon());
    }

    /**
     * Checks @see NullRule#calcGameWon()
     */
    @Test
    public void calcGameLost() {

        data.setAnnouncement(factory.getAnnouncement());
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

        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(23);
    }

    @Test
    public void calcGameResultGameWonContra() {

        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
        data.setContra(true);
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(46);
    }

    @Test
    public void calcGameResultGameWonContraRe() {

        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
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

        factory.setHand(true);
        data.setAnnouncement(factory.getAnnouncement());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(35);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonOuvert() {

        factory.setHand(false);
        factory.setOuvert(Boolean.TRUE);
        data.setAnnouncement(factory.getAnnouncement());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(46);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameWonHandOuvert() {
        factory.setHand(true);
        factory.setOuvert(true);
        data.setAnnouncement(factory.getAnnouncement());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(59);
    }

    /**
     * Checks @see NullRule#calcGameResult()
     */
    @Test
    public void calcGameResultGameLost() {

        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-46);
    }

    @Test
    public void calcGameResultGameLostContra() {

        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
        data.setContra(true);
        playWinningTricks();
        playLoosingTrick();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-92);
    }

    @Test
    public void calcGameResultGameLostContraRe() {

        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
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
        factory.setHand(true);
        data.setAnnouncement(factory.getAnnouncement());
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

        factory.setHand(false);
        factory.setOuvert(Boolean.TRUE);
        data.setAnnouncement(factory.getAnnouncement());
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
        factory.setHand(true);
        factory.setOuvert(true);
        data.setAnnouncement(factory.getAnnouncement());
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
        factory.setHand(false);
        data.setAnnouncement(factory.getAnnouncement());
        playWinningTricks();
        data.calcResult();
        assertThat(data.getResult().getGameValue()).isEqualTo(-46);
    }
}
