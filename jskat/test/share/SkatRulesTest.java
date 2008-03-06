/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test.share;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.Tools;
import jskat.share.rules.SkatRules;
import jskat.share.rules.SkatRulesFactory;

/**
 * Runs all unit tests for skat rule classes
 * 
 * @see jskat.share.rules.SkatRules
 * @see jskat.share.rules.SkatRulesGrand
 * @see jskat.share.rules.SkatRulesNull
 * @see jskat.share.rules.SkatRulesRamsch
 * @see jskat.share.rules.SkatRulesSuit
 * @see jskat.share.rules.SkatRulesSuitGrand
 */
public class SkatRulesTest {

	static Logger log = Logger.getLogger(jskat.test.share.SkatRulesTest.class);

	/**
	 * Setup for all tests
	 */
	@Before
	public void setUp() {
		
		Tools.checkLog();
		
		cardD7 = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.SEVEN);
		cardD8 = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT);
		cardD9 = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.NINE);
		cardDQ = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.QUEEN);
		cardDA = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.ACE);
		cardHQ = new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.QUEEN);
		cardHJ = new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK);
		cardS7 = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.SEVEN);
		cardS8 = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.EIGHT);
		cardST = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.TEN);
		cardSA = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.ACE);
		cardSQ = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.QUEEN);
		cardC7 = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.SEVEN);
		cardCQ = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN);
		cardCK = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.KING);
		
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
	 * @see jskat.share.rules.SkatRules#getGameResult(jskat.data.SkatGameData)
	 */
	@Test
	public void getGameResult001() {
		assertTrue(false);
	}
	
	/**
	 * Test 001 for method isCardAllowed()
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed001() {
		
		// queen of clubs allowed on eight of hearts: NO (hearts are trump)
		played = hand001.getCard(hand001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.HEARTS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
	
	/**
	 * Test 002 for method isCardAllowed()
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed002() {
		// queen of clubs allowed on eight of spades: YES (can't match)
		played = hand001.getCard(hand001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.HEARTS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
	
	/**
	 * Test 003 for method isCardAllowed()
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed003() {
		// queen of clubs allowed on eight of clubs: YES (matching suit)
		played = hand001.getCard(hand001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.DIAMONDS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
	
	/**
	 * Test 004 for method isCardAllowed()
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed004() {
		// queen of clubs allowed on eight of spades: NO (has other trump - the Jack!)
		// Test for Bug # 
		played = hand001.getCard(hand001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.SPADES;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
	
	/**
	 * Test 005 for method isCardAllowed()
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed005() {
		// queen of clubs allowed on eight of spades: YES (can't match)
		played = hand001.getCard(hand001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.DIAMONDS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, hand001, initialCard, trump));
	}
		
	/**
	 * Test 006 for method isCardAllowed()
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed006() {
		// Problem was in playing ramsch - 8 of Clubs was not allowed ("Player is evil")
		hand001 = TestHelper.buildDeck("J-H,J-D,Q-S,T-C,8-C");
		played = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.EIGHT);
		initialCard = new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, hand001, initialCard, null));
	}

	/**
	 * Test 201 for method isCardAllowed() <br />
	 * Suit game, Spades trump, D7 allowed on C7 and hand {D-7, D-8, D-9, D-Q}
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed201() {
		
		log.debug("====> isCardAllowed201 <====");
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.SPADES;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(cardD7, hand002, cardC7, trump));
	}

	/**
	 * Test 202 for method isCardAllowed() <br />
	 * Suit game, Spades trump, D7 allowed on C7 and hand {D-7, D-8, D-9, D-Q}
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed202() {
		
		log.debug("====> isCardAllowed202 <====");
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.CLUBS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(cardD7, hand002, cardC7, trump));
	}

	/**
	 * Test 203 for method isCardAllowed() <br />
	 * Suit game, Clubs trump, S7 not allowed on C7 with hand {S-7, S-8, S-Q, S-T, D-7, D-8, C-Q}
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed203() {
		
		log.debug("====> isCardAllowed203 <====");
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.CLUBS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardAllowed(cardS7, hand003, cardC7, trump));
	}

	/**
	 * Test 204 for method isCardAllowed() <br />
	 * Suit game, Spades trump, CQ allowed on SA with hand {C-Q, C-K}
	 * 
	 * @see jskat.share.rules.SkatRules#isCardAllowed(Card, CardVector, Card, jskat.share.SkatConstants.Suits)
	 */
	@Test
	public void isCardAllowed204() {
		
		log.debug("====> isCardAllowed204 <====");
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.SPADES;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(cardCQ, hand004, cardSA, trump));
	}
	
	private CardVector hand001;
	private CardVector hand002;
	private CardVector hand003;
	private CardVector hand004;
	private Card played;
	private Card initialCard;
	private Card cardD7;
	private Card cardD8;
	private Card cardD9;
	private Card cardDQ;
	private Card cardDA;
	private Card cardHQ;
	private Card cardHJ;
	private Card cardS7;
	private Card cardS8;
	private Card cardSQ;
	private Card cardST;
	private Card cardSA;
	private Card cardC7;
	private Card cardCQ;
	private Card cardCK;
	private SkatConstants.GameTypes gameType;
	private SkatConstants.Suits trump;
	private SkatRules rules;
}
