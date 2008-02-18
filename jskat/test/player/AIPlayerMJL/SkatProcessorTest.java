/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import jskat.player.AIPlayerMJL.SkatProcessor;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.Tools;
import jskat.test.share.TestHelper;

public class SkatProcessorTest {

	static Logger log = Logger.getLogger(SkatProcessorTest.class);

	@BeforeClass
	public static void setUp() {

		Tools.checkLog();
		
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

	@Test
	public void processSkat001() {
		log.debug("-------------------------------------------------------------");
		log.debug(this);
		log.debug("-------------------------------------------------------------");

		log.debug("Skat001 [before]:"+skat001);
		assertEquals(SkatConstants.Suits.DIAMONDS, SkatProcessor.processSkat(cards001, skat001));
		log.debug("Skat001 [after]: "+skat001);
		log.debug("-------------------------------------------------------------");
	}

	@Test
	public void processSkat002() {
		System.out.println("Skat002 [before]:"+skat002);
		assertEquals(SkatConstants.Suits.HEARTS, SkatProcessor.processSkat(cards002, skat002));
		System.out.println("Skat002 [after]: "+skat002);
		System.out.println("-------------------------------------------------------------");
	}

	@Test
	public void processSkat003() {
		System.out.println("Skat003 [before]:"+skat003);
		assertEquals(SkatConstants.Suits.HEARTS, SkatProcessor.processSkat(cards003, skat003));
		System.out.println("Skat003 [after]: "+skat003);
	}

	private static CardVector cards001;
	private static CardVector skat001;
	private static CardVector cards002;
	private static CardVector skat002;
	private static CardVector cards003;
	private static CardVector skat003;
	private static CardVector cards004;
}
