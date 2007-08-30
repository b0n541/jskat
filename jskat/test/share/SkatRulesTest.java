/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test.share;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.SkatRules;
import junit.framework.TestCase;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class SkatRulesTest extends TestCase {
	/**
	 * Constructor for SkatRulesTest.
	 * @param arg0
	 */
	public SkatRulesTest(String arg0) {
		super(arg0);
	}
	public static void main(String[] args) {
		junit.textui.TestRunner.run(SkatRulesTest.class);
	}
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		cards001 = new CardVector();
		cards001.add(new Card(SkatConstants.CLUBS, SkatConstants.QUEEN));
		cards001.add(new Card(SkatConstants.HEARTS, SkatConstants.QUEEN));
		cards001.add(new Card(SkatConstants.HEARTS, SkatConstants.JACK));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.EIGHT));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.SEVEN));
		cards001.sort(SkatConstants.SUIT, SkatConstants.HEARTS);
	}
	public void testGetResult() {
		//TODO Implement getResult().
	}
	public void testIsCardAllowed() {
		// queen of clubs allowed on eight of hearts: NO (hearts are trump)
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.CLUBS, SkatConstants.QUEEN));
		initialCard = new Card(SkatConstants.HEARTS, SkatConstants.EIGHT);
		gameType = SkatConstants.SUIT;
		trump = SkatConstants.HEARTS;
		assertFalse(SkatRules.isCardAllowed(played, cards001, initialCard, gameType, trump));

		// queen of clubs allowed on eight of spades: YES (can't match)
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.CLUBS, SkatConstants.QUEEN));
		initialCard = new Card(SkatConstants.SPADES, SkatConstants.EIGHT);
		gameType = SkatConstants.SUIT;
		trump = SkatConstants.HEARTS;
		assertTrue(SkatRules.isCardAllowed(played, cards001, initialCard, gameType, trump));

		// queen of clubs allowed on eight of clubs: YES (matching suit)
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.CLUBS, SkatConstants.QUEEN));
		initialCard = new Card(SkatConstants.CLUBS, SkatConstants.EIGHT);
		gameType = SkatConstants.SUIT;
		trump = SkatConstants.DIAMONDS;
		assertTrue(SkatRules.isCardAllowed(played, cards001, initialCard, gameType, trump));

		// queen of clubs allowed on eight of spades: NO (has other trump - the Jack!)
		// Test for Bug # 
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.CLUBS, SkatConstants.QUEEN));
		initialCard = new Card(SkatConstants.SPADES, SkatConstants.EIGHT);
		gameType = SkatConstants.SUIT;
		trump = SkatConstants.SPADES;
		assertFalse(SkatRules.isCardAllowed(played, cards001, initialCard, gameType, trump));

		// queen of clubs allowed on eight of spades: YES (can't match)
		played = cards001.getCard(cards001.getIndexOf(SkatConstants.CLUBS, SkatConstants.QUEEN));
		initialCard = new Card(SkatConstants.SPADES, SkatConstants.EIGHT);
		gameType = SkatConstants.SUIT;
		trump = SkatConstants.DIAMONDS;
		assertTrue(SkatRules.isCardAllowed(played, cards001, initialCard, gameType, trump));

		
		// Problem was in playing ramsch - 8 of Clubs was not allowed ("Player is evil")
		cards001 = TestHelper.buildDeck("J-H,J-D,Q-S,T-C,8-C");
		played = new Card(SkatConstants.CLUBS, SkatConstants.EIGHT);
		initialCard = new Card(SkatConstants.HEARTS, SkatConstants.EIGHT);
		gameType = SkatConstants.RAMSCH;
		trump = -1;
		assertTrue(SkatRules.isCardAllowed(played, cards001, initialCard, gameType, trump));
		
	}

	CardVector cards001;
	Card played;
	Card initialCard;
	int gameType;
	int trump;
}
