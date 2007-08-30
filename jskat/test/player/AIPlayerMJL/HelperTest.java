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
		cards001.add(new Card(SkatConstants.SPADES, SkatConstants.JACK));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.JACK));
		cards001.add(new Card(SkatConstants.HEARTS, SkatConstants.ACE));
		cards001.add(new Card(SkatConstants.CLUBS, SkatConstants.TEN));
		cards001.add(new Card(SkatConstants.CLUBS, SkatConstants.KING));
		cards001.add(new Card(SkatConstants.CLUBS, SkatConstants.QUEEN));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.QUEEN));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.NINE));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.EIGHT));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.SEVEN));
		cards001.sort(SkatConstants.SUIT, SkatConstants.HEARTS);

		trick001 = new CardVector();
		trick001.add(new Card(SkatConstants.SPADES, SkatConstants.ACE));
		trick001.add(new Card(SkatConstants.SPADES, SkatConstants.SEVEN));

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
		trump = SkatConstants.HEARTS;
		gameType = SkatConstants.SUIT;
		assertEquals(false, Helper.isAbleToMatch(cards001, trump, trick001.getCard(0), gameType));
		System.out.println("----------------------------------------------------------");
		trump = SkatConstants.SPADES;
		gameType = SkatConstants.SUIT;
		assertEquals(true, Helper.isAbleToMatch(cards001, trump, trick001.getCard(0), gameType));
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
	int gameType;
	int trump;
}
