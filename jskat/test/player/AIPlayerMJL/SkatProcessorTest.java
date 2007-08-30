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
		cards001.add(new Card(SkatConstants.HEARTS, SkatConstants.JACK));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.JACK));
		cards001.add(new Card(SkatConstants.SPADES, SkatConstants.SEVEN));
		cards001.add(new Card(SkatConstants.SPADES, SkatConstants.QUEEN));
		cards001.add(new Card(SkatConstants.CLUBS, SkatConstants.KING));
		cards001.add(new Card(SkatConstants.CLUBS, SkatConstants.QUEEN));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.QUEEN));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.NINE));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.EIGHT));
		cards001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.SEVEN));
		cards001.sort(SkatConstants.SUIT, SkatConstants.HEARTS);

		skat001 = new CardVector();
		skat001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.TEN));
		skat001.add(new Card(SkatConstants.CLUBS, SkatConstants.SEVEN));

		// Hand=J-H, J-D, A-H, K-H, Q-H, 9-H, 8-H, 9-D, 8-D, Q-S, Skat=10-S, 10-C
		cards002 = new CardVector();
		cards002.add(new Card(SkatConstants.HEARTS, SkatConstants.JACK));
		cards002.add(new Card(SkatConstants.DIAMONDS, SkatConstants.JACK));
		cards002.add(new Card(SkatConstants.HEARTS, SkatConstants.ACE));
		cards002.add(new Card(SkatConstants.HEARTS, SkatConstants.KING));
		cards002.add(new Card(SkatConstants.HEARTS, SkatConstants.QUEEN));
		cards002.add(new Card(SkatConstants.HEARTS, SkatConstants.NINE));
		cards002.add(new Card(SkatConstants.HEARTS, SkatConstants.EIGHT));
		cards002.add(new Card(SkatConstants.DIAMONDS, SkatConstants.NINE));
		cards002.add(new Card(SkatConstants.DIAMONDS, SkatConstants.EIGHT));
		cards002.add(new Card(SkatConstants.SPADES, SkatConstants.QUEEN));
		
		skat002 = new CardVector();
		skat002.add(new Card(SkatConstants.SPADES, SkatConstants.TEN));
		skat002.add(new Card(SkatConstants.CLUBS, SkatConstants.TEN));

		cards003 = new CardVector();
		cards003.add(new Card(SkatConstants.HEARTS, SkatConstants.JACK));
		cards003.add(new Card(SkatConstants.DIAMONDS, SkatConstants.JACK));
		cards003.add(new Card(SkatConstants.HEARTS, SkatConstants.ACE));
		cards003.add(new Card(SkatConstants.HEARTS, SkatConstants.KING));
		cards003.add(new Card(SkatConstants.HEARTS, SkatConstants.QUEEN));
		cards003.add(new Card(SkatConstants.HEARTS, SkatConstants.NINE));
		cards003.add(new Card(SkatConstants.HEARTS, SkatConstants.EIGHT));
		cards003.add(new Card(SkatConstants.DIAMONDS, SkatConstants.NINE));
		cards003.add(new Card(SkatConstants.DIAMONDS, SkatConstants.EIGHT));
		cards003.add(new Card(SkatConstants.SPADES, SkatConstants.QUEEN));

		skat003 = new CardVector();
		skat003.add(new Card(SkatConstants.CLUBS, SkatConstants.EIGHT));
		skat003.add(new Card(SkatConstants.CLUBS, SkatConstants.TEN));

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
		assertEquals(SkatConstants.DIAMONDS, SkatProcessor.processSkat(cards001, skat001));
		System.out.println("Skat001 [after]: "+skat001);
		System.out.println("-------------------------------------------------------------");

		System.out.println("Skat002 [before]:"+skat002);
		assertEquals(SkatConstants.HEARTS, SkatProcessor.processSkat(cards002, skat002));
		System.out.println("Skat002 [after]: "+skat002);
		System.out.println("-------------------------------------------------------------");
		
		System.out.println("Skat003 [before]:"+skat003);
		assertEquals(SkatConstants.HEARTS, SkatProcessor.processSkat(cards003, skat003));
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
