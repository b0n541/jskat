/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.test.util.rule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jskat.util.CardDeck;
import de.jskat.util.CardList;
import de.jskat.util.Card;
import de.jskat.util.GameType;
import de.jskat.util.rule.BasicSkatRules;
import de.jskat.util.rule.SkatRuleFactory;

/**
 * Test suite for all skat rule tests
 */
public class BasicSkatRuleTest {

	private static CardDeck deck = new CardDeck();
	private static CardList hand = new CardList();

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

	@BeforeClass
	public static void setUpBeforeClass() {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$
	}

	@AfterClass
	public static void tearDownAfterClass() {
		// TODO implement it
	}

	@Before
	public void setUp() {
		// TODO implement it
	}

	@After
	public void tearDown() {
		// TODO implement it
	}

	@Test
	public void isCardAllowedNull001() {
		assertTrue(nullRules.isCardAllowed(GameType.NULL, Card.CA, hand, Card.CK));
	}

	@Test
	public void isCardAllowedGrand001() {
		assertTrue(grandRules.isCardAllowed(GameType.GRAND, Card.CA, hand, Card.CK));
	}

	@Test
	public void isCardAllowedSuit001() {
		assertTrue(clubRules.isCardAllowed(GameType.CLUBS, Card.CA, hand, Card.CK));
	}
	
	@Test
	public void isCardAllowedSuit002() {
		
		this.hand.clear();
		this.hand.add(Card.C7);
		this.hand.add(Card.HK);
		this.hand.add(Card.HJ);
		
		assertFalse(diamondRules.isCardAllowed(GameType.DIAMONDS, Card.HT, hand, Card.HJ));
	}

	@Test
	public void isCardAllowedSuit003() {
		
		this.hand.clear();
		this.hand.add(Card.C7);
		this.hand.add(Card.HK);
		this.hand.add(Card.HJ);
		
		assertTrue(diamondRules.isCardAllowed(GameType.DIAMONDS, Card.HT, hand, Card.HK));
	}

	@Test
	public void isCardAllowedRamsch001() {
		assertTrue(ramschRules.isCardAllowed(GameType.RAMSCH, Card.CA, hand, Card.CK));
	}

	@Test
	public void isCardBeatsCardNull001() {
		assertTrue(nullRules.isCardBeatsCard(GameType.NULL, Card.C7, Card.C8));
	}

	@Test
	public void isCardBeatsCardGrand001() {
		assertTrue(grandRules.isCardBeatsCard(GameType.GRAND, Card.C7, Card.C8));
	}

	@Test
	public void isCardBeatsCardSuit001() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.C7, Card.C8));
	}

	@Test
	public void isCardBeatsCardSuit002() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.C7, Card.HJ));
	}

	@Test
	public void isCardBeatsCardSuit003() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.CA, Card.CJ));
	}

	@Test
	public void isCardBeatsCardSuit004() {
		assertTrue(clubRules.isCardBeatsCard(GameType.CLUBS, Card.SJ, Card.CJ));
	}

	@Test
	public void isCardBeatsCardRamsch001() {
		assertTrue(ramschRules.isCardBeatsCard(GameType.RAMSCH, Card.C7, Card.C8));
	}
}
