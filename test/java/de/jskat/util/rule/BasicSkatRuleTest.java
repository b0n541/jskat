/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util.rule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;

/**
 * Test suite for all skat rule tests
 */
public class BasicSkatRuleTest {

	private CardList hand = new CardList();

	private static BasicSkatRules clubRules = SkatRuleFactory
			.getSkatRules(GameType.CLUBS);
	private static BasicSkatRules spadeRules = SkatRuleFactory
			.getSkatRules(GameType.SPADES);
	private static BasicSkatRules heartRules = SkatRuleFactory
			.getSkatRules(GameType.HEARTS);
	private static BasicSkatRules diamondRules = SkatRuleFactory
			.getSkatRules(GameType.DIAMONDS);
	private static BasicSkatRules grandRules = SkatRuleFactory
			.getSkatRules(GameType.GRAND);
	private static BasicSkatRules nullRules = SkatRuleFactory
			.getSkatRules(GameType.NULL);
	private static BasicSkatRules ramschRules = SkatRuleFactory
			.getSkatRules(GameType.RAMSCH);

	/**
	 * Initializes the logger
	 */
	@BeforeClass
	public static void setUpBeforeClass() {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$
	}

	/**
	 * Checks @see NullRules#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedNull001() {
		assertTrue(nullRules.isCardAllowed(GameType.NULL, Card.CA, this.hand,
				Card.CK));
	}

	/**
	 * Checks @see GrandRules#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedGrand001() {
		assertTrue(grandRules.isCardAllowed(GameType.GRAND, Card.CA, this.hand,
				Card.CK));
	}

	/**
	 * Checks @see SuitRules#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedSuit001() {
		assertTrue(clubRules.isCardAllowed(GameType.CLUBS, Card.CA, this.hand,
				Card.CK));
	}

	/**
	 * Checks @see SuitRules#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedSuit002() {

		this.hand.clear();
		this.hand.add(Card.C7);
		this.hand.add(Card.HK);
		this.hand.add(Card.HJ);

		assertFalse(diamondRules.isCardAllowed(GameType.DIAMONDS, Card.HT,
				this.hand, Card.HJ));
	}

	/**
	 * Checks @see SuitRules#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedSuit003() {

		this.hand.clear();
		this.hand.add(Card.C7);
		this.hand.add(Card.HK);
		this.hand.add(Card.HJ);

		assertTrue(diamondRules.isCardAllowed(GameType.DIAMONDS, Card.HT,
				this.hand, Card.HK));
	}

	/**
	 * Checks @see RamschRules#isCardAllowed(GameType, Card, CardList, Card)
	 */
	@Test
	public void isCardAllowedRamsch001() {
		assertTrue(ramschRules.isCardAllowed(GameType.RAMSCH, Card.CA,
				this.hand, Card.CK));
	}

	/**
	 * Checks @see NullRules#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardNull001() {
		assertTrue(nullRules.isCardBeatsCard(GameType.NULL, Card.C7, Card.C8));
	}

	/**
	 * Checks @see GrandRules#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardGrand001() {
		assertTrue(grandRules.isCardBeatsCard(GameType.GRAND, Card.C7, Card.C8));
	}

	/**
	 * Checks @see SuitRules#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardSuit001() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.C7, Card.C8));
	}

	/**
	 * Checks @see SuitRules#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardSuit002() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.C7, Card.HJ));
	}

	/**
	 * Checks @see SuitRules#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardSuit003() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.CA, Card.CJ));
	}

	/**
	 * Checks @see SuitRules#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardSuit004() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.SJ, Card.CJ));
	}

	/**
	 * Checks @see RamschRules#isCardBeatsCard(GameType, Card, Card)
	 */
	@Test
	public void isCardBeatsCardRamsch001() {
		assertTrue(ramschRules.isCardBeatsCard(GameType.RAMSCH, Card.C7,
				Card.C8));
	}
}
