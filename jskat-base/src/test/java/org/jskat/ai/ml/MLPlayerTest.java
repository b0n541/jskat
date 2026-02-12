package org.jskat.ai.ml;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for MLPlayer to verify hand game decision-making.
 */
public class MLPlayerTest extends AbstractJSkatTest {

    private MLPlayer player;

    @BeforeEach
    void setUp() {
        player = new MLPlayer();
    }

    @AfterEach
    void tearDown() {
        player.close();
    }

    /**
     * Sets up the player with the given hand in forehand position and triggers bid calculation.
     *
     * @return whether the player wants to pick up the skat
     */
    private boolean shouldPickUpSkatWith(CardList hand) {
        player.newGame(Player.FOREHAND);
        player.takeCards(hand);
        player.setGameState(SkatGameData.GameState.BIDDING);
        player.setUpBidding();
        player.bidMore(18);
        return player.pickUpSkat();
    }

    /**
     * Test that MLPlayer correctly identifies a perfect grand hand
     * and chooses to play it as a hand game (not picking up the skat).
     */
    @Test
    public void testPerfectGrandHandShouldNotPickupSkat() {
        // Invincible grand hand: All 4 jacks + top two cards of three suits
        CardList hand = new CardList(
            Card.CJ, Card.SJ, Card.HJ, Card.DJ,
            Card.CA, Card.CT,
            Card.SA, Card.ST,
            Card.HA, Card.HT
        );

        assertFalse(shouldPickUpSkatWith(hand),
            "Perfect grand hand should be played as hand game. Hand: " + hand);
    }

    /**
     * Test that MLPlayer correctly identifies a perfect suit hand
     * and chooses to play it as a hand game (not picking up the skat).
     */
    @Test
    public void testPerfectSuitHandShouldNotPickupSkat() {
        // Perfect clubs hand: All 4 jacks + strong clubs + side aces
        CardList hand = new CardList(
            Card.CJ, Card.SJ, Card.HJ, Card.DJ,
            Card.CA, Card.CT, Card.CK,
            Card.SA, Card.HA, Card.DA
        );

        assertFalse(shouldPickUpSkatWith(hand),
            "Perfect clubs hand should be played as hand game. Hand: " + hand);
    }

    /**
     * Test that MLPlayer correctly identifies a perfect null hand
     * and chooses to play it as a hand game (not picking up the skat).
     */
    @Test
    public void testPerfectNullHandShouldNotPickupSkat() {
        // Perfect null hand: All low cards (7, 8, 9) - unbeatable in Null
        CardList hand = new CardList(
            Card.C7, Card.C8, Card.C9,
            Card.S7, Card.S8,
            Card.H7, Card.H8,
            Card.D7, Card.D8, Card.D9
        );

        assertFalse(shouldPickUpSkatWith(hand),
            "Perfect null hand should be played as hand game. Hand: " + hand);
    }

    /**
     * Test with the standard "perfect distribution" forehand position.
     * This is the canonical perfect hand in Skat.
     */
    @Test
    public void testStandardPerfectDistributionShouldNotPickupSkat() {
        // Invincible grand hand
        CardList hand = new CardList(
            Card.CJ, Card.SJ, Card.HJ, Card.DJ,
            Card.CA, Card.CT,
            Card.SA, Card.ST,
            Card.HA, Card.HT
        );

        assertFalse(shouldPickUpSkatWith(hand),
            "Standard perfect hand should be played as hand game. Hand: " + hand);
    }
}
