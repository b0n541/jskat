package org.jskat.ai.ml;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for MLPlayer to verify hand game decision-making.
 */
public class MLPlayerTest extends AbstractJSkatTest {

    /**
     * Test that MLPlayer correctly identifies a perfect grand hand
     * and chooses to play it as a hand game (not picking up the skat).
     *
     * Perfect grand hand: All 4 jacks + high trump cards + high-value cards.
     * Expected: Should NOT pick up skat (mlShouldPickupSkat = false)
     */
    @Test
    public void testPerfectGrandHandShouldNotPickupSkat() {
        MLPlayer player = new MLPlayer();

        // Invincible grand hand: All 4 jacks + top two cards of three suits
        CardList perfectGrandHand = new CardList(
            Card.CJ, Card.SJ, Card.HJ, Card.DJ,  // All 4 jacks (trump in Grand)
            Card.CA, Card.CT,                    // Clubs: Ace + Ten
            Card.SA, Card.ST,                    // Spades: Ace + Ten
            Card.HA, Card.HT                     // Hearts: Ace + Ten
        );

        // Setup game state
        player.newGame(Player.FOREHAND);
        player.takeCards(perfectGrandHand);
        player.setGameState(SkatGameData.GameState.BIDDING);
        player.setUpBidding();

        // Trigger bid calculation by calling bidMore
        // This will internally call calculateMLMaxBid()
        player.bidMore(18);

        // Verify that the player chooses NOT to pick up the skat
        boolean shouldPickupSkat = player.pickUpSkat();

        assertFalse(shouldPickupSkat,
            "Perfect grand hand should be played as hand game (not picking up skat). " +
            "Hand: " + perfectGrandHand);
    }

    /**
     * Test that MLPlayer correctly identifies a perfect suit hand
     * and chooses to play it as a hand game (not picking up the skat).
     *
     * Perfect clubs hand: All 4 jacks + strong clubs suit.
     * Expected: Should NOT pick up skat (mlShouldPickupSkat = false)
     */
    @Test
    public void testPerfectSuitHandShouldNotPickupSkat() {
        MLPlayer player = new MLPlayer();

        // Perfect clubs hand: All 4 jacks + strong clubs
        // With all 4 jacks (With 4) + good clubs suit
        CardList perfectClubsHand = new CardList(
            Card.CJ, Card.SJ, Card.HJ, Card.DJ,  // All 4 jacks
            Card.CA, Card.CT, Card.CK,            // Clubs: A, T, K (very strong)
            Card.SA, Card.HA, Card.DA             // Side aces for control
        );

        // Setup game state
        player.newGame(Player.FOREHAND);
        player.takeCards(perfectClubsHand);
        player.setGameState(SkatGameData.GameState.BIDDING);
        player.setUpBidding();

        // Trigger bid calculation
        player.bidMore(18);

        // Verify that the player chooses NOT to pick up the skat
        boolean shouldPickupSkat = player.pickUpSkat();

        assertFalse(shouldPickupSkat,
            "Perfect clubs hand should be played as hand game (not picking up skat). " +
            "Hand: " + perfectClubsHand);
    }

    /**
     * Test that MLPlayer correctly identifies a perfect null hand
     * and chooses to play it as a hand game (not picking up the skat).
     *
     * Perfect null hand: All low cards (7-9) with no aces, tens, or face cards.
     * Expected: Should NOT pick up skat (mlShouldPickupSkat = false)
     */
    @Test
    public void testPerfectNullHandShouldNotPickupSkat() {
        MLPlayer player = new MLPlayer();

        // Perfect null hand: All low cards (7, 8, 9) - unbeatable in Null
        // No aces, tens, jacks, queens, or kings
        CardList perfectNullHand = new CardList(
            Card.C7, Card.C8, Card.C9,  // Clubs: 7, 8, 9
            Card.S7, Card.S8,            // Spades: 7, 8
            Card.H7, Card.H8,            // Hearts: 7, 8
            Card.D7, Card.D8, Card.D9   // Diamonds: 7, 8, 9
        );

        // Setup game state
        player.newGame(Player.FOREHAND);
        player.takeCards(perfectNullHand);
        player.setGameState(SkatGameData.GameState.BIDDING);
        player.setUpBidding();

        // Trigger bid calculation
        player.bidMore(18);

        // Verify that the player chooses NOT to pick up the skat
        boolean shouldPickupSkat = player.pickUpSkat();

        assertFalse(shouldPickupSkat,
            "Perfect null hand should be played as hand game (not picking up skat). " +
            "Hand: " + perfectNullHand);
    }

    /**
     * Test with the standard "perfect distribution" forehand position.
     * This is the canonical perfect hand in Skat.
     */
    @Test
    public void testStandardPerfectDistributionShouldNotPickupSkat() {
        MLPlayer player = new MLPlayer();

        // Invincible grand hand (Modified from CardDeck.getPerfectDistribution to be truly unbeatable)
        CardList standardPerfectHand = new CardList(
            Card.CJ, Card.SJ, Card.HJ, Card.DJ,  // All 4 jacks
            Card.CA, Card.CT,                    // Clubs
            Card.SA, Card.ST,                    // Spades
            Card.HA, Card.HT                     // Hearts
        );

        // Setup game state
        player.newGame(Player.FOREHAND);
        player.takeCards(standardPerfectHand);
        player.setGameState(SkatGameData.GameState.BIDDING);
        player.setUpBidding();

        // Trigger bid calculation
        player.bidMore(18);

        // Verify that the player chooses NOT to pick up the skat
        boolean shouldPickupSkat = player.pickUpSkat();

        assertFalse(shouldPickupSkat,
            "Standard perfect hand should be played as hand game (not picking up skat). " +
            "Hand: " + standardPerfectHand);
    }
}
