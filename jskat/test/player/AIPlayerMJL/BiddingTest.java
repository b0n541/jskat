/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import jskat.player.AIPlayerMJL.Bidding;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.Tools;

public class BiddingTest {

	@BeforeClass
	public static void setUp() {

		Tools.checkLog();
		
		cards001 = new CardVector();
		cards001.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK));
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
	}

	@Test
	public void getMaxBid() {
		
		Bidding bid = new Bidding(cards001);
		assertEquals(27, bid.getMaxBid());
	}

	private static CardVector cards001;
}
