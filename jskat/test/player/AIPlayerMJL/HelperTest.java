/*

@ShortLicense@

Authos: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import junit.framework.TestCase;
import jskat.player.AIPlayerMJL.Helper;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.rules.SkatRules;
import jskat.share.rules.SkatRulesFactory;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class HelperTest extends TestCase {
	/**
	 * Constructor for HelperTest.
	 * @param arg0
	 */
	public HelperTest(String arg0) {
		super(arg0);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(HelperTest.class);
	}
	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		cards001 = new CardVector();
		cards001.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.JACK));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.JACK));
		cards001.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.ACE));
		cards001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.TEN));
		cards001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.KING));
		cards001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.QUEEN));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.NINE));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.SEVEN));
		cards001.sort(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.HEARTS);

		trick001 = new CardVector();
		trick001.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.ACE));
		trick001.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.SEVEN));

	}

	/**
	 * Test for isSinglePlayerWin/() method
	 */
	public void testIsSinglePlayerWin() {
		//TODO Implement isSinglePlayerWin().
	}

	/**
	 * Test for isAbleToBeat() method
	 */
	public void testIsAbleToBeat() {
		//TODO Implement isAbleToBeat().
	}

	/**
	 * Test for isAbleToMatch() method
	 */
	public void testIsAbleToMatch() {
		trump = SkatConstants.Suits.HEARTS;
		gameType = SkatConstants.GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertEquals(false, Helper.isAbleToMatch(rules, cards001, trump, trick001.getCard(0), gameType));
		System.out.println("----------------------------------------------------------");
		trump = SkatConstants.Suits.SPADES;
		gameType = SkatConstants.GameTypes.SUIT;
		assertEquals(true, Helper.isAbleToMatch(rules, cards001, trump, trick001.getCard(0), gameType));
	}

	/**
	 * Test for getHighestTrump() method
	 */
	public void testGetHighestTrump() {
		//TODO Implement getHighestTrump().
	}

	/**
	 * Test for hasTrump() method
	 */
	public void testHasTrump() {
		//TODO Implement hasTrump().
	}

	CardVector cards001;
	CardVector cards002;
	CardVector trick001;
	Card played;
	Card initialCard;
	SkatConstants.GameTypes gameType;
	SkatConstants.Suits trump;
	SkatRules rules;
}
