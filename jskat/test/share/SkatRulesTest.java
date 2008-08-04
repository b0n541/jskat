/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test.share;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.SkatConstants.GameTypes;
import jskat.share.SkatConstants.Suits;
import jskat.share.rules.*;

/**
 * Runs all unit tests for skat rule classes
 * 
 * @see SkatRules
 * @see GrandRules
 * @see NullRules
 * @see RamschRules
 * @see SuitRules
 * @see SuitGrandRules
 */
public class SkatRulesTest {

	private Log log = LogFactory.getLog(SkatRulesTest.class);

	/**
	 * Setup for all tests
	 */
	@BeforeClass
	public static void setUp() {
		
		cardD7 = new Card(Suits.DIAMONDS, SkatConstants.Ranks.SEVEN);
		cardD8 = new Card(Suits.DIAMONDS, SkatConstants.Ranks.EIGHT);
		cardD9 = new Card(Suits.DIAMONDS, SkatConstants.Ranks.NINE);
		cardDQ = new Card(Suits.DIAMONDS, SkatConstants.Ranks.QUEEN);
		cardDJ = new Card(Suits.DIAMONDS, SkatConstants.Ranks.JACK);
		cardHQ = new Card(Suits.HEARTS, SkatConstants.Ranks.QUEEN);
		cardHJ = new Card(Suits.HEARTS, SkatConstants.Ranks.JACK);
		cardS7 = new Card(Suits.SPADES, SkatConstants.Ranks.SEVEN);
		cardS8 = new Card(Suits.SPADES, SkatConstants.Ranks.EIGHT);
		cardST = new Card(Suits.SPADES, SkatConstants.Ranks.TEN);
		cardSA = new Card(Suits.SPADES, SkatConstants.Ranks.ACE);
		cardSQ = new Card(Suits.SPADES, SkatConstants.Ranks.QUEEN);
		cardSK = new Card(Suits.SPADES, SkatConstants.Ranks.KING);
		cardSJ = new Card(Suits.SPADES, SkatConstants.Ranks.JACK);
		cardC7 = new Card(Suits.CLUBS, SkatConstants.Ranks.SEVEN);
		cardCQ = new Card(Suits.CLUBS, SkatConstants.Ranks.QUEEN);
		cardCK = new Card(Suits.CLUBS, SkatConstants.Ranks.KING);
		cardCJ = new Card(Suits.CLUBS, SkatConstants.Ranks.JACK);
		
		hand001 = new CardVector();
		hand001.add(cardCQ);
		hand001.add(cardHQ);
		hand001.add(cardHJ);
		hand001.add(cardD8);
		hand001.add(cardD7);

		hand002 = new CardVector();
		hand002.add(cardD7);
		hand002.add(cardD8);
		hand002.add(cardD9);
		hand002.add(cardDQ);
		
		hand003 = new CardVector();
		hand003.add(cardS7);
		hand003.add(cardS8);
		hand003.add(cardSQ);
		hand003.add(cardST);
		hand003.add(cardD7);
		hand003.add(cardD8);
		hand003.add(cardCQ);
		
		hand004 = new CardVector();
		hand004.add(cardCQ);
		hand004.add(cardCK);
	}
	
	/**
	 * Test 001 for method getGameResult()
	 * 
	 * @see SkatRules#getGameResult(jskat.data.SkatGameData)
	 */
//	@Test
//	public void getGameResult001() {
//		assertTrue(false);
//	}
	
	/**
	 * Test 001 for method isCardAllowed()
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed001() {
		
		// queen of clubs allowed on eight of hearts: NO (hearts are trump)
		played = hand001.getCard(hand001.getIndexOf(Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(Suits.HEARTS, SkatConstants.Ranks.EIGHT);
		gameType = GameTypes.SUIT;
		trump = Suits.HEARTS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
	
	/**
	 * Test 002 for method isCardAllowed()
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed002() {
		// queen of clubs allowed on eight of spades: YES (can't match)
		played = hand001.getCard(hand001.getIndexOf(Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(Suits.SPADES, SkatConstants.Ranks.EIGHT);
		gameType = GameTypes.SUIT;
		trump = Suits.HEARTS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
	
	/**
	 * Test 003 for method isCardAllowed()
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed003() {
		// queen of clubs allowed on eight of clubs: YES (matching suit)
		played = hand001.getCard(hand001.getIndexOf(Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(Suits.CLUBS, SkatConstants.Ranks.EIGHT);
		gameType = GameTypes.SUIT;
		trump = Suits.DIAMONDS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
	
	/**
	 * Test 004 for method isCardAllowed()
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed004() {
		// queen of clubs allowed on eight of spades: NO (has other trump - the Jack!)
		// Test for Bug # 
		played = hand001.getCard(hand001.getIndexOf(Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(Suits.SPADES, SkatConstants.Ranks.EIGHT);
		gameType = GameTypes.SUIT;
		trump = Suits.SPADES;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
	
	/**
	 * Test 005 for method isCardAllowed()
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed005() {
		// queen of clubs allowed on eight of spades: YES (can't match)
		played = hand001.getCard(hand001.getIndexOf(Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(Suits.SPADES, SkatConstants.Ranks.EIGHT);
		gameType = GameTypes.SUIT;
		trump = Suits.DIAMONDS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
		
	/**
	 * Test 006 for method isCardAllowed()
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed006() {
		// Problem was in playing ramsch - 8 of Clubs was not allowed ("Player is evil")
		hand001 = TestHelper.buildDeck("J-H,J-C,Q-S,T-C,8-C");
		played = new Card(Suits.CLUBS, SkatConstants.Ranks.EIGHT);
		initialCard = new Card(Suits.DIAMONDS, SkatConstants.Ranks.EIGHT);
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, hand001, initialCard, null));
	}

	/**
	 * Test 201 for method isCardAllowed() <br />
	 * Suit game, Spades trump, D7 allowed on C7 and hand {D-7, D-8, D-9, D-Q}
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed201() {
		
		log.debug("====> isCardAllowed201 <====");
		gameType = GameTypes.SUIT;
		trump = Suits.SPADES;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(cardD7, hand002, cardC7, trump));
	}

	/**
	 * Test 202 for method isCardAllowed() <br />
	 * Suit game, Spades trump, D7 allowed on C7 and hand {D-7, D-8, D-9, D-Q}
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed202() {
		
		log.debug("====> isCardAllowed202 <====");
		gameType = GameTypes.SUIT;
		trump = Suits.CLUBS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(cardD7, hand002, cardC7, trump));
	}

	/**
	 * Test 203 for method isCardAllowed() <br />
	 * Suit game, Clubs trump, S7 not allowed on C7 with hand {S-7, S-8, S-Q, S-T, D-7, D-8, C-Q}
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed203() {
		
		log.debug("====> isCardAllowed203 <====");
		gameType = GameTypes.SUIT;
		trump = Suits.CLUBS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardAllowed(cardS7, hand003, cardC7, trump));
	}

	/**
	 * Test 204 for method isCardAllowed() <br />
	 * Suit game, Spades trump, CQ allowed on SA with hand {C-Q, C-K}
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed204() {
		
		log.debug("====> isCardAllowed204 <====");
		gameType = GameTypes.SUIT;
		trump = Suits.SPADES;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(cardCQ, hand004, cardSA, trump));
	}
	
	/**
	 * Test 205 for method isCardAllowed() <br />
	 * Suit game, Diamonds trump, DJ not allowed on D8 with hand {J-H,J-D,A-D,Q-S,T-C,8-C} 
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed205() {
		
		log.debug("====> isCardAllowed205 <====");
		hand001 = TestHelper.buildDeck("J-H,J-D,A-D,Q-S,T-C,8-C");
		played = cardDJ;
		initialCard = cardD8;
		gameType = GameTypes.SUIT;
		trump = Suits.HEARTS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardAllowed(played, hand001, initialCard, trump));
	}

	/**
	 * Test 206 for method isCardAllowed() <br />
	 * Suit game, Diamonds trump, DJ allowed on D8 with hand {J-H,J-D,A-H,Q-S,T-C,8-C} 
	 * 
	 * @see SkatRules#isCardAllowed(Card, CardVector, Card, Suits)
	 */
	@Test
	public void isCardAllowed206() {
		
		log.debug("====> isCardAllowed206 <====");
		hand001 = TestHelper.buildDeck("J-H,J-D,A-H,Q-S,T-C,8-C");
		played = cardDJ;
		initialCard = cardD8;
		gameType = GameTypes.SUIT;
		trump = Suits.HEARTS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
	
	/**
	 * Test 100 for method isTrump() <br />
	 * Suit game
	 * 
	 * @see SuitTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump100() {
		
		log.debug("====> isTrump100 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		trump = Suits.CLUBS;
		assertFalse(((TrumpDecorator)rules).isTrump(cardD7, trump));
	}
	
	/**
	 * Test 101 for method isTrump() <br />
	 * Suit game
	 * 
	 * @see SuitTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump101() {
		
		log.debug("====> isTrump101 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		trump = Suits.CLUBS;
		assertTrue(((TrumpDecorator)rules).isTrump(cardDJ, trump));
	}
	
	/**
	 * Test 102 for method isTrump() <br />
	 * Suit game
	 * 
	 * @see SuitTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump102() {
		
		log.debug("====> isTrump102 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		trump = Suits.CLUBS;
		assertTrue(((TrumpDecorator)rules).isTrump(cardC7, trump));
	}
	
	/**
	 * Test 103 for method isTrump() <br />
	 * Suit game
	 * 
	 * @see SuitTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump103() {
		
		log.debug("====> isTrump103 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		trump = Suits.CLUBS;
		assertTrue(((TrumpDecorator)rules).isTrump(cardCJ, trump));
	}
	
	/**
	 * Test 104 for method isTrump() <br />
	 * Suit game
	 * 
	 * @see SuitTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump104() {
		
		log.debug("====> isTrump104 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		trump = Suits.CLUBS;
		assertTrue(((TrumpDecorator)rules).isTrump(cardHJ, trump));
	}

	/**
	 * Test 200 for method isTrump() <br />
	 * Grand game
	 * 
	 * @see GrandRamschTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump200() {
		
		log.debug("====> isTrump200 <====");
		gameType = GameTypes.GRAND;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(((TrumpDecorator)rules).isTrump(cardHJ, null));
	}

	/**
	 * Test 201 for method isTrump() <br />
	 * Grand game
	 * 
	 * @see GrandRamschTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump201() {
		
		log.debug("====> isTrump201 <====");
		gameType = GameTypes.GRAND;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(((TrumpDecorator)rules).isTrump(cardCJ, null));
	}

	/**
	 * Test 202 for method isTrump() <br />
	 * Grand game
	 * 
	 * @see GrandRamschTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump202() {
		
		log.debug("====> isTrump202 <====");
		gameType = GameTypes.GRAND;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(((TrumpDecorator)rules).isTrump(cardSA, null));
	}

	/**
	 * Test 203 for method isTrump() <br />
	 * Grand game
	 * 
	 * @see GrandRamschTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump203() {
		
		log.debug("====> isTrump203 <====");
		gameType = GameTypes.GRAND;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(((TrumpDecorator)rules).isTrump(cardC7, null));
	}

	/**
	 * Test 300 for method isTrump() <br />
	 * Ramsch game
	 * 
	 * @see GrandRamschTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump300() {
		
		log.debug("====> isTrump300 <====");
		gameType = GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(((TrumpDecorator)rules).isTrump(cardHJ, null));
	}

	/**
	 * Test 301 for method isTrump() <br />
	 * Ramsch game
	 * 
	 * @see GrandRamschTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump301() {
		
		log.debug("====> isTrump301 <====");
		gameType = GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(((TrumpDecorator)rules).isTrump(cardCJ, null));
	}

	/**
	 * Test 302 for method isTrump() <br />
	 * Ramsch game
	 * 
	 * @see GrandRamschTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump302() {
		
		log.debug("====> isTrump302 <====");
		gameType = GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(((TrumpDecorator)rules).isTrump(cardSA, null));
	}

	/**
	 * Test 303 for method isTrump() <br />
	 * Ramsch game
	 * 
	 * @see GrandRamschTrumpRules#isTrump(Card, Suits)
	 */
	@Test
	public void isTrump303() {
		
		log.debug("====> isTrump303 <====");
		gameType = GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(((TrumpDecorator)rules).isTrump(cardC7, null));
	}

	/**
	 * Test 100 for method isCardBeatsCard() <br />
	 * Null game
	 * 
	 * @see NullRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard100() {
		
		log.debug("====> isCardBeatsCard100 <====");
		gameType = GameTypes.NULL;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardST, cardS8, null));
	}

	/**
	 * Test 101 for method isCardBeatsCard() <br />
	 * Null game
	 * 
	 * @see NullRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard101() {
		
		log.debug("====> isCardBeatsCard101 <====");
		gameType = GameTypes.NULL;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardBeatsCard(cardS8, cardST, null));
	}

	/**
	 * Test 102 for method isCardBeatsCard() <br />
	 * Null game
	 * 
	 * @see NullRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard102() {
		
		log.debug("====> isCardBeatsCard102 <====");
		gameType = GameTypes.NULL;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardBeatsCard(cardHJ, cardS8, null));
	}

	/**
	 * Test 103 for method isCardBeatsCard() <br />
	 * Null game
	 * 
	 * @see NullRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard103() {
		
		log.debug("====> isCardBeatsCard103 <====");
		gameType = GameTypes.NULL;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardBeatsCard(cardHJ, cardDJ, null));
	}

	/**
	 * Test 104 for method isCardBeatsCard() <br />
	 * Null game
	 * 
	 * @see NullRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard104() {
		
		log.debug("====> isCardBeatsCard104 <====");
		gameType = GameTypes.NULL;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardSQ, cardST, null));
	}

	/**
	 * Test 200 for method isCardBeatsCard() <br />
	 * Suit game
	 * 
	 * @see SuitRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard200() {
		
		log.debug("====> isCardBeatsCard200 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardBeatsCard(cardSQ, cardST, Suits.CLUBS));
	}

	/**
	 * Test 201 for method isCardBeatsCard() <br />
	 * Suit game
	 * 
	 * @see SuitRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard201() {
		
		log.debug("====> isCardBeatsCard201 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardST, cardSQ, Suits.CLUBS));
	}

	/**
	 * Test 202 for method isCardBeatsCard() <br />
	 * Suit game
	 * 
	 * @see SuitRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard202() {
		
		log.debug("====> isCardBeatsCard202 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardBeatsCard(cardSQ, cardST, Suits.SPADES));
	}

	/**
	 * Test 203 for method isCardBeatsCard() <br />
	 * Suit game
	 * 
	 * @see SuitRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard203() {
		
		log.debug("====> isCardBeatsCard203 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardST, cardSQ, Suits.SPADES));
	}

	/**
	 * Test 204 for method isCardBeatsCard() <br />
	 * Suit game
	 * 
	 * @see SuitRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard204() {
		
		log.debug("====> isCardBeatsCard204 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardSQ, cardCK, Suits.SPADES));
	}

	/**
	 * Test 205 for method isCardBeatsCard() <br />
	 * Suit game
	 * 
	 * @see SuitRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard205() {
		
		log.debug("====> isCardBeatsCard205 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardHJ, cardDJ, Suits.SPADES));
	}

	/**
	 * Test 206 for method isCardBeatsCard() <br />
	 * Suit game
	 * 
	 * @see SuitRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard206() {
		
		log.debug("====> isCardBeatsCard206 <====");
		gameType = GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardSJ, cardST, Suits.SPADES));
	}

	/**
	 * Test 300 for method isCardBeatsCard() <br />
	 * Grand game
	 * 
	 * @see GrandRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard300() {
		
		log.debug("====> isCardBeatsCard300 <====");
		gameType = GameTypes.GRAND;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardBeatsCard(cardSQ, cardST, null));
	}

	/**
	 * Test 301 for method isCardBeatsCard() <br />
	 * Grand game
	 * 
	 * @see GrandRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard301() {
		
		log.debug("====> isCardBeatsCard301 <====");
		gameType = GameTypes.GRAND;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardST, cardSQ, null));
	}

	/**
	 * Test 302 for method isCardBeatsCard() <br />
	 * Grand game
	 * 
	 * @see GrandRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard302() {
		
		log.debug("====> isCardBeatsCard302 <====");
		gameType = GameTypes.GRAND;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardBeatsCard(cardSQ, cardCK, null));
	}

	/**
	 * Test 303 for method isCardBeatsCard() <br />
	 * Grand game
	 * 
	 * @see GrandRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard303() {
		
		log.debug("====> isCardBeatsCard303 <====");
		gameType = GameTypes.GRAND;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardHJ, cardDJ, null));
	}

	/**
	 * Test 304 for method isCardBeatsCard() <br />
	 * Grand game
	 * 
	 * @see GrandRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard304() {
		
		log.debug("====> isCardBeatsCard304 <====");
		gameType = GameTypes.GRAND;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardSJ, cardST, null));
	}

	/**
	 * Test 400 for method isCardBeatsCard() <br />
	 * Ramsch game
	 * 
	 * @see RamschRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard400() {
		
		log.debug("====> isCardBeatsCard400 <====");
		gameType = GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardSQ, cardST, null));
	}

	/**
	 * Test 401 for method isCardBeatsCard() <br />
	 * Ramsch game
	 * 
	 * @see RamschRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard401() {
		
		log.debug("====> isCardBeatsCard401 <====");
		gameType = GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardSK, cardST, null));
	}

	/**
	 * Test 402 for method isCardBeatsCard() <br />
	 * Ramsch game
	 * 
	 * @see RamschRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard402() {
		
		log.debug("====> isCardBeatsCard402 <====");
		gameType = GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardSJ, cardST, null));
	}

	/**
	 * Test 403 for method isCardBeatsCard() <br />
	 * Ramsch game
	 * 
	 * @see RamschRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard403() {
		
		log.debug("====> isCardBeatsCard403 <====");
		gameType = GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardBeatsCard(cardSJ, cardHJ, null));
	}

	/**
	 * Test 404 for method isCardBeatsCard() <br />
	 * Ramsch game
	 * 
	 * @see RamschRules#isCardBeatsCard(Card, Card, Suits)
	 */
	@Test
	public void isCardBeatsCard404() {
		
		log.debug("====> isCardBeatsCard404 <====");
		gameType = GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardBeatsCard(cardSQ, cardD8, null));
	}

	private static CardVector hand001;
	private static CardVector hand002;
	private static CardVector hand003;
	private static CardVector hand004;
	private static Card played;
	private static Card initialCard;
	private static Card cardD7;
	private static Card cardD8;
	private static Card cardD9;
	private static Card cardDQ;
	private static Card cardDJ;
	private static Card cardHQ;
	private static Card cardHJ;
	private static Card cardS7;
	private static Card cardS8;
	private static Card cardSJ;
	private static Card cardSQ;
	private static Card cardSK;
	private static Card cardST;
	private static Card cardSA;
	private static Card cardCQ;
	private static Card cardCK;
	private static Card cardCJ;
	private static Card cardC7;
	private static GameTypes gameType;
	private static Suits trump;
	private static SkatRules rules;
}
