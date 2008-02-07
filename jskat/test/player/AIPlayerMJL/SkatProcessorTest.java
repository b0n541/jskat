/*

@ShortLicense@

Authos: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import junit.framework.TestCase;
import jskat.player.AIPlayerMJL.SkatProcessor;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.test.share.TestHelper;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class SkatProcessorTest extends TestCase {
	/**
	 * Constructor for SkatProcessorTest.
	 * @param arg0
	 */
	public SkatProcessorTest(String arg0) {
		super(arg0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(SkatProcessorTest.class);
	}
	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		cards001 = new CardVector();
		cards001.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.JACK));
		cards001.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.SEVEN));
		cards001.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.QUEEN));
		cards001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.KING));
		cards001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.QUEEN));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.NINE));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.SEVEN));
		cards001.sort(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.HEARTS);

		skat001 = new CardVector();
		skat001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.TEN));
		skat001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.SEVEN));

		// Hand=J-H, J-D, A-H, K-H, Q-H, 9-H, 8-H, 9-D, 8-D, Q-S, Skat=10-S, 10-C
		cards002 = new CardVector();
		cards002.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK));
		cards002.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.JACK));
		cards002.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.ACE));
		cards002.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.KING));
		cards002.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.QUEEN));
		cards002.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.NINE));
		cards002.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.EIGHT));
		cards002.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.NINE));
		cards002.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT));
		cards002.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.QUEEN));
		
		skat002 = new CardVector();
		skat002.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.TEN));
		skat002.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.TEN));

		cards003 = new CardVector();
		cards003.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK));
		cards003.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.JACK));
		cards003.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.ACE));
		cards003.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.KING));
		cards003.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.QUEEN));
		cards003.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.NINE));
		cards003.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.EIGHT));
		cards003.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.NINE));
		cards003.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT));
		cards003.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.QUEEN));

		skat003 = new CardVector();
		skat003.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.EIGHT));
		skat003.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.TEN));

		cards004 = TestHelper.buildDeck("S-J,D-J,H-A,H-T,C-T");
}

	/**
	 * Tests for processing the skat
	 */
	public void testProcessSkat() {
		System.out.println("-------------------------------------------------------------");
		System.out.println(this);
		System.out.println("-------------------------------------------------------------");

		System.out.println("Skat001 [before]:"+skat001);
		assertEquals(SkatConstants.Suits.DIAMONDS, SkatProcessor.processSkat(cards001, skat001));
		System.out.println("Skat001 [after]: "+skat001);
		System.out.println("-------------------------------------------------------------");

		System.out.println("Skat002 [before]:"+skat002);
		assertEquals(SkatConstants.Suits.HEARTS, SkatProcessor.processSkat(cards002, skat002));
		System.out.println("Skat002 [after]: "+skat002);
		System.out.println("-------------------------------------------------------------");
		
		System.out.println("Skat003 [before]:"+skat003);
		assertEquals(SkatConstants.Suits.HEARTS, SkatProcessor.processSkat(cards003, skat003));
		System.out.println("Skat003 [after]: "+skat003);
	}

	CardVector cards001;
	CardVector skat001;
	CardVector cards002;
	CardVector skat002;
	CardVector cards003;
	CardVector skat003;
	CardVector cards004;
}
