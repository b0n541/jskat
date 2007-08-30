/*

@ShortLicense@

Authos: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import junit.framework.TestCase;
import jskat.player.AIPlayerMJL.Bidding;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class BiddingTest extends TestCase {
	/**
	 * Constructor for BiddingTest.
	 * @param arg0
	 */
	public BiddingTest(String arg0) {
		super(arg0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(BiddingTest.class);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		cards001 = new CardVector();
		cards001.add(new Card(SkatConstants.HEARTS, SkatConstants.JACK));
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

	}

	/**
	 * Test for bidding values
	 */
	public void testGetMaxBid() {
		Bidding bid = new Bidding(cards001);
		assertEquals(27, bid.getMaxBid());
	}

	CardVector cards001;
}
