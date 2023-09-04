package org.jskat.player;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.Trick;
import org.jskat.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerKnowledgeTest2 {

    private PlayerKnowledge knowledge;

    CardList playerCards = new CardList(Card.CJ, Card.SJ, Card.CA, Card.CT, Card.C9, Card.SA, Card.ST, Card.S9, Card.HA,
            Card.HT);
    CardList opponentCards = new CardList(Card.HJ, Card.DJ, Card.CK, Card.CQ, Card.SK, Card.SQ, Card.HK, Card.HQ,
            Card.D8, Card.D9, Card.DT, Card.C7, Card.C8, Card.S7, Card.S8, Card.H8, Card.H9, Card.DA, Card.DK, Card.DQ);
    CardList skatCards = new CardList(Card.H7, Card.D7);

    /**
     * Setting up all variables
     */
    @BeforeEach
    public void setUp() {
        knowledge = new PlayerKnowledge();
    }

    @Test
    public void beforeDealing() {
        knowledge.resetCurrentGameData();

        assertThat(knowledge.getTrumpCount()).isEqualTo(0);

        for (final Card card : Card.values()) {
            assertCardCouldBeEverywhere(card);
        }

        for (final Card card : opponentCards) {
            assertCardCouldBeEverywhere(card);
        }

        for (final Card card : skatCards) {
            assertCardCouldBeEverywhere(card);
        }

        assertTrue(knowledge.couldHaveSuit(Player.FOREHAND, Suit.CLUBS));
        assertTrue(knowledge.couldHaveSuit(Player.FOREHAND, Suit.SPADES));
        assertTrue(knowledge.couldHaveSuit(Player.FOREHAND, Suit.HEARTS));
        assertTrue(knowledge.couldHaveSuit(Player.FOREHAND, Suit.DIAMONDS));

        assertTrue(knowledge.couldHaveSuit(Player.MIDDLEHAND, Suit.CLUBS));
        assertTrue(knowledge.couldHaveSuit(Player.MIDDLEHAND, Suit.SPADES));
        assertTrue(knowledge.couldHaveSuit(Player.MIDDLEHAND, Suit.HEARTS));
        assertTrue(knowledge.couldHaveSuit(Player.MIDDLEHAND, Suit.DIAMONDS));

        assertTrue(knowledge.couldHaveSuit(Player.REARHAND, Suit.CLUBS));
        assertTrue(knowledge.couldHaveSuit(Player.REARHAND, Suit.SPADES));
        assertTrue(knowledge.couldHaveSuit(Player.REARHAND, Suit.HEARTS));
        assertTrue(knowledge.couldHaveSuit(Player.REARHAND, Suit.DIAMONDS));
    }

    @Test
    public void afterDealing() {
        knowledge.resetCurrentGameData();
        knowledge.setPlayerPosition(Player.FOREHAND);
        knowledge.addOwnCards(playerCards);

        gameAnnouncement(GameType.CLUBS, Player.FOREHAND);

        assertThat(knowledge.getTrumpCount()).isEqualTo(5);

        for (final Card card : playerCards) {
            assertTrue(knowledge.couldHaveCard(Player.FOREHAND, card));
            assertFalse(knowledge.couldHaveCard(Player.MIDDLEHAND, card));
            assertFalse(knowledge.couldHaveCard(Player.REARHAND, card));

            assertFalse(knowledge.couldLieInSkat(card));
        }

        for (final Card card : opponentCards) {
            assertFalse(knowledge.couldHaveCard(Player.FOREHAND, card));
            assertTrue(knowledge.couldHaveCard(Player.MIDDLEHAND, card));
            assertTrue(knowledge.couldHaveCard(Player.REARHAND, card));

            assertTrue(knowledge.couldLieInSkat(card));
        }

        for (final Card card : skatCards) {
            assertFalse(knowledge.couldHaveCard(Player.FOREHAND, card));
            assertTrue(knowledge.couldHaveCard(Player.MIDDLEHAND, card));
            assertTrue(knowledge.couldHaveCard(Player.REARHAND, card));

            assertTrue(knowledge.couldLieInSkat(card));
        }

        assertTrue(knowledge.couldOpponentsHaveTrump());

        assertTrue(knowledge.couldHaveSuit(Player.FOREHAND, Suit.CLUBS));
        assertTrue(knowledge.couldHaveSuit(Player.FOREHAND, Suit.SPADES));
        assertTrue(knowledge.couldHaveSuit(Player.FOREHAND, Suit.HEARTS));
        assertFalse(knowledge.couldHaveSuit(Player.FOREHAND, Suit.DIAMONDS));

        assertTrue(knowledge.couldHaveSuit(Player.MIDDLEHAND, Suit.CLUBS));
        assertTrue(knowledge.couldHaveSuit(Player.MIDDLEHAND, Suit.SPADES));
        assertTrue(knowledge.couldHaveSuit(Player.MIDDLEHAND, Suit.HEARTS));
        assertTrue(knowledge.couldHaveSuit(Player.MIDDLEHAND, Suit.DIAMONDS));

        assertTrue(knowledge.couldHaveSuit(Player.REARHAND, Suit.CLUBS));
        assertTrue(knowledge.couldHaveSuit(Player.REARHAND, Suit.SPADES));
        assertTrue(knowledge.couldHaveSuit(Player.REARHAND, Suit.HEARTS));
        assertTrue(knowledge.couldHaveSuit(Player.REARHAND, Suit.DIAMONDS));
    }

    @Test
    public void trickPlaying() {

        knowledge.resetCurrentGameData();
        knowledge.setPlayerPosition(Player.FOREHAND);
        knowledge.addOwnCards(playerCards);

        gameAnnouncement(GameType.CLUBS, Player.FOREHAND);

        // 1st trick
        knowledge.setNextTrick(0, Player.FOREHAND);

        assertTrue(knowledge.getCompletedTricks().isEmpty());

        knowledge.setCardPlayed(Player.FOREHAND, Card.CJ);

        assertCardCouldBeNowhere(Card.CJ);

        knowledge.setCardPlayed(Player.MIDDLEHAND, Card.D8);

        assertCardCouldBeNowhere(Card.D8);
        assertCouldNotHaveCardOfSuit(Player.MIDDLEHAND, Suit.CLUBS);
        assertCouldNotHaveCards(Player.MIDDLEHAND, Card.HJ, Card.DJ);

        knowledge.setCardPlayed(Player.REARHAND, Card.CK);

        assertCardCouldBeNowhere(Card.CK);

        Trick trick = new Trick(0, Player.FOREHAND);
        trick.addCard(Card.CJ);
        trick.addCard(Card.D8);
        trick.addCard(Card.CK);
        trick.setTrickWinner(Player.FOREHAND);
        knowledge.addCompletedTrick(trick);

        assertThat(knowledge.getCompletedTricks()).hasSize(1);

        // 2nd trick
        knowledge.setNextTrick(1, Player.FOREHAND);

        assertThat(knowledge.getCompletedTricks()).hasSize(1);

        knowledge.setCardPlayed(Player.FOREHAND, Card.SJ);

        assertCardCouldBeNowhere(Card.SJ);

        knowledge.setCardPlayed(Player.MIDDLEHAND, Card.D9);

        assertCardCouldBeNowhere(Card.D9);
        assertCouldHaveCards(Player.MIDDLEHAND, Card.SK, Card.SQ, Card.S8, Card.S7);

        knowledge.setCardPlayed(Player.REARHAND, Card.CQ);
        assertCouldHaveCards(Player.REARHAND, Card.SK, Card.SQ, Card.S8, Card.S7);

        assertCardCouldBeNowhere(Card.CQ);

        trick = new Trick(1, Player.FOREHAND);
        trick.addCard(Card.SJ);
        trick.addCard(Card.D9);
        trick.addCard(Card.CQ);
        trick.setTrickWinner(Player.FOREHAND);
        knowledge.addCompletedTrick(trick);

        assertThat(knowledge.getCompletedTricks()).hasSize(2);

        // 3rd trick
        knowledge.setNextTrick(2, Player.FOREHAND);

        assertThat(knowledge.getCompletedTricks()).hasSize(2);

        knowledge.setCardPlayed(Player.FOREHAND, Card.SA);

        assertCardCouldBeNowhere(Card.SA);

        knowledge.setCardPlayed(Player.MIDDLEHAND, Card.H8);

        assertCardCouldBeNowhere(Card.H8);
        assertCouldNotHaveCardOfSuit(Player.MIDDLEHAND, Suit.SPADES);

        knowledge.setCardPlayed(Player.REARHAND, Card.S8);
        assertCardCouldBeNowhere(Card.S8);

        trick = new Trick(2, Player.FOREHAND);
        trick.addCard(Card.SA);
        trick.addCard(Card.H8);
        trick.addCard(Card.S8);
        trick.setTrickWinner(Player.FOREHAND);
        knowledge.addCompletedTrick(trick);

        assertThat(knowledge.getCompletedTricks()).hasSize(3);

        // 4th trick
        knowledge.setNextTrick(3, Player.FOREHAND);

        assertThat(knowledge.getCompletedTricks()).hasSize(3);

        knowledge.setCardPlayed(Player.FOREHAND, Card.HA);

        assertCardCouldBeNowhere(Card.HA);

        knowledge.setCardPlayed(Player.MIDDLEHAND, Card.H9);

        assertCardCouldBeNowhere(Card.H9);

        knowledge.setCardPlayed(Player.REARHAND, Card.S9);

        assertCardCouldBeNowhere(Card.S9);
        assertCouldHaveCards(Player.REARHAND, Card.HJ, Card.DJ);
    }

    @Test
    public void declarerCards() {
        knowledge.resetCurrentGameData();

        assertThat(knowledge.getDeclarerPlayerCards()).isEmpty();

        knowledge.addDeclarerCards(CardList.of(Card.CJ, Card.SJ, Card.SA));

        assertThat(knowledge.getDeclarerPlayerCards()).containsExactlyInAnyOrder(Card.CJ, Card.SJ, Card.SA);

        // TODO implement and test removal of declarer cards during game play
    }

    private void assertCouldHaveCards(final Player player, final Card... cards) {
        for (final Card card : cards) {
            assertTrue(knowledge.couldHaveCard(player, card));
        }
    }

    private void assertCouldNotHaveCards(final Player player, final Card... cards) {
        for (final Card card : cards) {
            assertFalse(knowledge.couldHaveCard(player, card));
        }
    }

    private void assertCouldNotHaveCardOfSuit(final Player player, final Suit suit) {

        assertFalse(knowledge.couldHaveSuit(player, suit));
        for (final Card card : CardDeck.SUIT_CARDS.get(suit)) {
            assertFalse(knowledge.couldHaveCard(player, card));
        }
    }

    private void assertCardCouldBeNowhere(final Card card) {
        assertFalse(knowledge.couldHaveCard(Player.FOREHAND, card));
        assertFalse(knowledge.couldHaveCard(Player.MIDDLEHAND, card));
        assertFalse(knowledge.couldHaveCard(Player.REARHAND, card));
        assertFalse(knowledge.couldLieInSkat(card));
    }

    private void assertCardCouldBeEverywhere(final Card card) {
        assertTrue(knowledge.couldHaveCard(Player.FOREHAND, card));
        assertTrue(knowledge.couldHaveCard(Player.MIDDLEHAND, card));
        assertTrue(knowledge.couldHaveCard(Player.REARHAND, card));
        assertTrue(knowledge.couldLieInSkat(card));
    }

    private void gameAnnouncement(final GameType gameType, final Player declarer) {
        knowledge.setDeclarer(declarer);
        knowledge.setGame(GameAnnouncement.builder(gameType).build());
    }
}
