/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test.share;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.rules.SkatRules;
import jskat.share.rules.SkatRulesFactory;

public class SkatRulesTest {

	@Before
	public void setUp() {
		
		cards001 = new CardVector();
		cards001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		cards001.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.QUEEN));
		cards001.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.SEVEN));
		cards001.sort(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.HEARTS);
	}
	
	@Test
	public void getResult() {
		assertTrue(false);
	}
	
	@Test
	public void isCardAllowed001() {
		
		// queen of clubs allowed on eight of hearts: NO (hearts are trump)
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.HEARTS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardAllowed(played, cards001, initialCard, trump));
	}
	
	@Test
	public void isCardAllowed002() {
		// queen of clubs allowed on eight of spades: YES (can't match)
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.HEARTS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, cards001, initialCard, trump));
	}
	
	@Test
	public void isCardAllowed003() {
		// queen of clubs allowed on eight of clubs: YES (matching suit)
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.DIAMONDS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, cards001, initialCard, trump));
	}
	
	@Test
	public void isCardAllowed004() {
		// queen of clubs allowed on eight of spades: NO (has other trump - the Jack!)
		// Test for Bug # 
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.SPADES;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertFalse(rules.isCardAllowed(played, cards001, initialCard, trump));
	}
	
	@Test
	public void isCardAllowed005() {
		// queen of clubs allowed on eight of spades: YES (can't match)
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		initialCard = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.SUIT;
		trump = SkatConstants.Suits.DIAMONDS;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, cards001, initialCard, trump));
	}
		
	@Test
	public void isCardAllowed006() {
		// Problem was in playing ramsch - 8 of Clubs was not allowed ("Player is evil")
		cards001 = TestHelper.buildDeck("J-H,J-D,Q-S,T-C,8-C");
		played = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.EIGHT);
		initialCard = new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.EIGHT);
		gameType = SkatConstants.GameTypes.RAMSCH;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertTrue(rules.isCardAllowed(played, cards001, initialCard, null));
	}

	private CardVector cards001;
	private Card played;
	private Card initialCard;
	private SkatConstants.GameTypes gameType;
	private SkatConstants.Suits trump;
	private SkatRules rules;
}
