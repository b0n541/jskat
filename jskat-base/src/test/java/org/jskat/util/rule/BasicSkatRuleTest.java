
package org.jskat.util.rule;


import org.jskat.AbstractJSkatTest;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for all skat rule tests
 */
public class BasicSkatRuleTest extends AbstractJSkatTest {

    private final CardList hand = new CardList();

    private static final SkatRule clubRules = SkatRuleFactory.getSkatRules(GameType.CLUBS);
    private static final SkatRule spadeRules = SkatRuleFactory.getSkatRules(GameType.SPADES);
    private static final SkatRule heartRules = SkatRuleFactory.getSkatRules(GameType.HEARTS);
    private static final SkatRule diamondRules = SkatRuleFactory.getSkatRules(GameType.DIAMONDS);
    private static final SkatRule grandRules = SkatRuleFactory.getSkatRules(GameType.GRAND);
    private static final SkatRule nullRules = SkatRuleFactory.getSkatRules(GameType.NULL);
    private static final SkatRule ramschRules = SkatRuleFactory.getSkatRules(GameType.RAMSCH);

    /**
     * Checks @see NullRule#isCardAllowed(GameType, Card, CardList, Card)
     */
    @Test
    public void isCardAllowedNull001() {
        assertTrue(nullRules.isCardAllowed(GameType.NULL, Card.CA, this.hand, Card.CK));
    }

    /**
     * Checks @see GrandRule#isCardAllowed(GameType, Card, CardList, Card)
     */
    @Test
    public void isCardAllowedGrand001() {
        assertTrue(grandRules.isCardAllowed(GameType.GRAND, Card.CA, this.hand, Card.CK));
    }

    /**
     * Checks @see SuitRule#isCardAllowed(GameType, Card, CardList, Card)
     */
    @Test
    public void isCardAllowedSuit001() {
        assertTrue(clubRules.isCardAllowed(GameType.CLUBS, Card.CA, this.hand, Card.CK));
    }

    /**
     * Checks @see SuitRule#isCardAllowed(GameType, Card, CardList, Card)
     */
    @Test
    public void isCardAllowedSuit002() {

        this.hand.clear();
        this.hand.add(Card.C7);
        this.hand.add(Card.HK);
        this.hand.add(Card.HJ);

        assertFalse(diamondRules.isCardAllowed(GameType.DIAMONDS, Card.HT, this.hand, Card.HJ));
    }

    /**
     * Checks @see SuitRule#isCardAllowed(GameType, Card, CardList, Card)
     */
    @Test
    public void isCardAllowedSuit003() {

        this.hand.clear();
        this.hand.add(Card.C7);
        this.hand.add(Card.HK);
        this.hand.add(Card.HJ);

        assertTrue(diamondRules.isCardAllowed(GameType.DIAMONDS, Card.HT, this.hand, Card.HK));
    }

    /**
     * Checks @see RamschRule#isCardAllowed(GameType, Card, CardList, Card)
     */
    @Test
    public void isCardAllowedRamsch001() {
        assertTrue(ramschRules.isCardAllowed(GameType.RAMSCH, Card.CA, this.hand, Card.CK));
    }

    /**
     * Checks @see NullRule#isCardBeatsCard(GameType, Card, Card)
     */
    @Test
    public void isCardBeatsCardNull001() {
        assertTrue(nullRules.isCardBeatsCard(GameType.NULL, Card.C7, Card.C8));
    }

    /**
     * Checks @see GrandRule#isCardBeatsCard(GameType, Card, Card)
     */
    @Test
    public void isCardBeatsCardGrand001() {
        assertTrue(grandRules.isCardBeatsCard(GameType.GRAND, Card.C7, Card.C8));
    }

    /**
     * Checks @see SuitRule#isCardBeatsCard(GameType, Card, Card)
     */
    @Test
    public void isCardBeatsCardSuit001() {
        assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.C7, Card.C8));
    }

    /**
     * Checks @see SuitRule#isCardBeatsCard(GameType, Card, Card)
     */
    @Test
    public void isCardBeatsCardSuit002() {
        assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.C7, Card.HJ));
    }

    /**
     * Checks @see SuitRule#isCardBeatsCard(GameType, Card, Card)
     */
    @Test
    public void isCardBeatsCardSuit003() {
        assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.CA, Card.CJ));
    }

    /**
     * Checks @see SuitRule#isCardBeatsCard(GameType, Card, Card)
     */
    @Test
    public void isCardBeatsCardSuit004() {
        assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.SJ, Card.CJ));
    }

    /**
     * Checks @see RamschRule#isCardBeatsCard(GameType, Card, Card)
     */
    @Test
    public void isCardBeatsCardRamsch001() {
        assertTrue(ramschRules.isCardBeatsCard(GameType.RAMSCH, Card.C7, Card.C8));
    }
}
