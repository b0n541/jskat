package org.jskat.player;


import org.jskat.AbstractJSkatTest;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
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
 * Test cases for class PlayerKnowledge
 */
public class PlayerKnowledgeTest extends AbstractJSkatTest {

    PlayerKnowledge knowledge;
    CardList playerCards;
    CardList otherCards;

    /**
     * Setting up all variables
     */
    @BeforeEach
    public void setUp() {

        knowledge = new PlayerKnowledge();

        knowledge.setPlayerPosition(Player.MIDDLEHAND);

        // Cards for the player
        playerCards = CardList.of(Card.CA, Card.CQ, Card.C8, Card.ST, Card.SQ, Card.DT, Card.DK, Card.D7, Card.HJ, Card.HA);

        // other cards
        otherCards = new CardList();
        for (final Card card : Card.values()) {
            if (!playerCards.contains(card)) {
                otherCards.add(card);
            }
        }
    }

    /**
     * Test the player knowledge after fresh initialization
     */
    @Test
    public void testInitialization() {

        assertTrue(knowledge.getOwnCards().isEmpty());
        assertThat(knowledge.getTrumpCount()).isEqualTo(0);

        for (final Card card : playerCards) {
            assertTrue(knowledge.couldHaveCard(Player.FOREHAND, card));
            assertTrue(knowledge.couldHaveCard(Player.MIDDLEHAND, card));
            assertTrue(knowledge.couldHaveCard(Player.REARHAND, card));
            assertFalse(knowledge.hasCard(Player.FOREHAND, card));
            assertFalse(knowledge.hasCard(Player.MIDDLEHAND, card));
            assertFalse(knowledge.hasCard(Player.REARHAND, card));
            assertTrue(knowledge.couldLieInSkat(card));
        }
        for (final Card card : otherCards) {
            assertTrue(knowledge.couldHaveCard(Player.FOREHAND, card));
            assertTrue(knowledge.couldHaveCard(Player.MIDDLEHAND, card));
            assertTrue(knowledge.couldHaveCard(Player.REARHAND, card));
            assertFalse(knowledge.hasCard(Player.FOREHAND, card));
            assertFalse(knowledge.hasCard(Player.REARHAND, card));
            assertFalse(knowledge.hasCard(Player.MIDDLEHAND, card));
            assertTrue(knowledge.couldLieInSkat(card));
        }
    }

    /**
     * Test player knowledge after dealing
     */
    @Test
    public void testDealing() {

        dealPlayerCards();

        assertThat(knowledge.getOwnCards()).hasSize(10);
        assertThat(knowledge.getTrumpCount()).isEqualTo(0);

        for (final Card card : playerCards) {
            assertFalse(knowledge.couldHaveCard(Player.FOREHAND, card));
            assertTrue(knowledge.couldHaveCard(Player.MIDDLEHAND, card));
            assertFalse(knowledge.couldHaveCard(Player.REARHAND, card));
            assertFalse(knowledge.hasCard(Player.FOREHAND, card));
            assertTrue(knowledge.hasCard(Player.MIDDLEHAND, card));
            assertFalse(knowledge.hasCard(Player.REARHAND, card));
            assertFalse(knowledge.couldLieInSkat(card));
        }
        for (final Card card : otherCards) {
            assertCouldHaveCard(Player.FOREHAND, card);
            assertCouldNotHaveCard(Player.MIDDLEHAND, card);
            assertCouldHaveCard(Player.REARHAND, card);
            assertHasNotCard(Player.FOREHAND, card);
            assertHasNotCard(Player.MIDDLEHAND, card);
            assertHasNotCard(Player.REARHAND, card);
            assertTrue(knowledge.couldLieInSkat(card));
        }
    }

    @Test
    public void testCardPlay_SuitGame() {

        dealPlayerCards();

        final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
        factory.setGameType(GameType.CLUBS);
        knowledge.setGame(factory.getAnnouncement());
        knowledge.setDeclarer(Player.FOREHAND);

        knowledge.setNextTrick(0, Player.FOREHAND);

        final Card foreHandCard = Card.SA;
        final Card middleHandCard = Card.SQ;
        final Card rearHandCard = Card.H7;

        // Fore hand plays
        assertCouldHaveCard(Player.FOREHAND, Card.SA);
        assertCouldNotHaveCard(Player.FOREHAND, Card.ST);
        assertCouldHaveCard(Player.FOREHAND, Card.SK);
        assertCouldNotHaveCard(Player.FOREHAND, Card.SQ);
        assertCouldHaveCard(Player.FOREHAND, Card.S9);
        assertCouldHaveCard(Player.FOREHAND, Card.S8);
        assertCouldHaveCard(Player.FOREHAND, Card.S7);

        knowledge.setCardPlayed(Player.FOREHAND, foreHandCard);

        assertCouldNotHaveCard(Player.FOREHAND, Card.SA);
        assertCouldNotHaveCard(Player.FOREHAND, Card.ST);
        assertCouldHaveCard(Player.FOREHAND, Card.SK);
        assertCouldNotHaveCard(Player.FOREHAND, Card.SQ);
        assertCouldHaveCard(Player.FOREHAND, Card.S9);
        assertCouldHaveCard(Player.FOREHAND, Card.S8);
        assertCouldHaveCard(Player.FOREHAND, Card.S7);

        assertCouldNotHaveCard(Player.MIDDLEHAND, foreHandCard);
        assertCouldNotHaveCard(Player.REARHAND, foreHandCard);

        // Middle hand plays
        assertCouldNotHaveCard(Player.MIDDLEHAND, Card.SA);
        assertCouldHaveCard(Player.MIDDLEHAND, Card.ST);
        assertCouldNotHaveCard(Player.MIDDLEHAND, Card.SK);
        assertCouldHaveCard(Player.MIDDLEHAND, Card.SQ);
        assertCouldNotHaveCard(Player.MIDDLEHAND, Card.S9);
        assertCouldNotHaveCard(Player.MIDDLEHAND, Card.S8);
        assertCouldNotHaveCard(Player.MIDDLEHAND, Card.S7);

        knowledge.setCardPlayed(Player.MIDDLEHAND, middleHandCard);

        assertCouldNotHaveCard(Player.FOREHAND, middleHandCard);
        assertCouldNotHaveCard(Player.MIDDLEHAND, middleHandCard);
        assertCouldNotHaveCard(Player.REARHAND, middleHandCard);

        // Rear hand plays
        assertCouldHaveCard(Player.REARHAND, Card.CJ);
        assertCouldHaveCard(Player.REARHAND, Card.SJ);
        assertCouldNotHaveCard(Player.REARHAND, Card.HJ);
        assertCouldHaveCard(Player.REARHAND, Card.DJ);
        assertCouldNotHaveCard(Player.REARHAND, Card.SA);
        assertCouldNotHaveCard(Player.REARHAND, Card.ST);
        assertCouldHaveCard(Player.REARHAND, Card.SK);
        assertCouldNotHaveCard(Player.REARHAND, Card.SQ);
        assertCouldHaveCard(Player.REARHAND, Card.S9);
        assertCouldHaveCard(Player.REARHAND, Card.S8);
        assertCouldHaveCard(Player.REARHAND, Card.S7);

        knowledge.setCardPlayed(Player.REARHAND, rearHandCard);

        assertCouldNotHaveCard(Player.FOREHAND, rearHandCard);
        assertCouldNotHaveCard(Player.MIDDLEHAND, rearHandCard);
        assertCouldNotHaveCard(Player.REARHAND, rearHandCard);

        // Rear hand has not followed suit
        assertCouldHaveCard(Player.REARHAND, Card.CJ);
        assertCouldHaveCard(Player.REARHAND, Card.SJ);
        assertCouldNotHaveCard(Player.REARHAND, Card.HJ);
        assertCouldHaveCard(Player.REARHAND, Card.DJ);
        assertCouldNotHaveCard(Player.REARHAND, Card.SA);
        assertCouldNotHaveCard(Player.REARHAND, Card.ST);
        assertCouldNotHaveCard(Player.REARHAND, Card.SK);
        assertCouldNotHaveCard(Player.REARHAND, Card.SQ);
        assertCouldNotHaveCard(Player.REARHAND, Card.S9);
        assertCouldNotHaveCard(Player.REARHAND, Card.S8);
        assertCouldNotHaveCard(Player.REARHAND, Card.S7);
    }

    private void dealPlayerCards() {

        // set up player cards
        knowledge.setPlayerPosition(Player.MIDDLEHAND);
        knowledge.addOwnCards(playerCards);
    }

    private void assertCouldHaveCard(final Player player, final Card card) {
        assertTrue(knowledge.couldHaveCard(player, card));
    }

    private void assertCouldNotHaveCard(final Player player, final Card card) {
        assertFalse(knowledge.couldHaveCard(player, card));
    }

    private void assertHasCard(final Player player, final Card card) {
        assertTrue(knowledge.hasCard(player, card));
    }

    private void assertHasNotCard(final Player player, final Card card) {
        assertFalse(knowledge.hasCard(player, card));
    }

    private void assertCouldLieInSkat(final Card card) {
        assertTrue(knowledge.couldLieInSkat(card));
    }

    private void assertCouldNotLieInSkat(final Card card) {
        assertFalse(knowledge.couldLieInSkat(card));
    }

    /**
     * Tests setting of tricks
     */
    @Test
    public void testSetTrick() {

        knowledge.setNextTrick(0, Player.FOREHAND);

        assertThat(knowledge.getCompletedTricks()).hasSize(0);

        knowledge.addCompletedTrick(new Trick(0, Player.FOREHAND));

        assertThat(knowledge.getCompletedTricks()).hasSize(1);
    }
}
