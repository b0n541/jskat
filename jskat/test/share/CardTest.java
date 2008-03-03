/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test.share;

import jskat.share.Card;
import jskat.share.SkatConstants;
import jskat.share.Tools;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class Card<br />
 * @see jskat.share.Card
 */
public class CardTest {

	/**
	 * Set up for the object used in the tests
	 */
	@BeforeClass
	public static void setUp() {
		
		Tools.checkLog();
		
		cardCJ = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.JACK);
		cardSJ = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.JACK);
		cardS9 = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.NINE);
		cardHJ = new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK);
		cardDJ = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.JACK);
		cardDA = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.ACE);
		cardDT = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.TEN);
		cardDK = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.KING);
		cardDQ = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.QUEEN);
		cardD9 = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.NINE);
		cardD8 = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT);
		cardD7 = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.SEVEN);
	}

	/**
	 * Null game, D9 beats D8, initial D7
	 */
	@Test
	public void beats001() {
		
		assertTrue(cardD9.beats(cardD8, SkatConstants.GameTypes.NULL, cardD7));
	}
	
	/**
	 * Null game, D9 beats D8, initial DT
	 */
	@Test
	public void beats002() {
		
		assertTrue(cardD9.beats(cardD8, SkatConstants.GameTypes.NULL, cardDT));
	}

	/**
	 * Null game, DJ beats DT, initial D7
	 */
	@Test
	public void beats003() {
		
		assertTrue(cardDJ.beats(cardDT, SkatConstants.GameTypes.NULL, cardD7));
	}
	
	/**
	 * Null game, DQ beats DT, initial DT
	 */
	@Test
	public void beats004() {
		
		assertTrue(cardDQ.beats(cardDT, SkatConstants.GameTypes.NULL, cardDT));
	}

	/**
	 * Null game, DK doesn't beat DA, initial DT
	 */
	@Test
	public void beats005() {
		
		assertFalse(cardDK.beats(cardDA, SkatConstants.GameTypes.NULL, cardDT));
	}
	
	/**
	 * Null game, D9 beats CJ, initial D7
	 */
	@Test
	public void beats006() {
		
		assertTrue(cardD9.beats(cardCJ, SkatConstants.GameTypes.NULL, cardD7));
	}
	
	/**
	 * Null game, D9 doesn't beat CJ, initial CJ
	 */
	@Test
	public void beats007() {
		
		assertFalse(cardD9.beats(cardCJ, SkatConstants.GameTypes.NULL, cardCJ));
	}
	
	/**
	 * Ramsch game, DJ beats D8, initial DJ
	 */
	@Test 
	public void beats601() {
    	
        assertTrue(cardDJ.beats(cardD8, SkatConstants.GameTypes.RAMSCH, cardDJ));
	}
	
	/**
	 * Ramsch game, D8 doesn't beat DJ, initial D8
	 */
	@Test
	public void beats602() {
		
        assertFalse(cardD8.beats(cardDJ, SkatConstants.GameTypes.RAMSCH, null, cardD8));
	}
	
	/**
	 * Ramsch game, CJ beats S9, initial CJ
	 */
	@Test
	public void beats603() {
		
        assertTrue(cardCJ.beats(cardS9, SkatConstants.GameTypes.RAMSCH, null, cardCJ));
	}
	
	/**
	 * Ramsch game, S9 doesn't beat CJ, initial CJ
	 */
	@Test
	public void beats604() {
		
        assertFalse(cardS9.beats(cardCJ, SkatConstants.GameTypes.RAMSCH, null, cardCJ));
    }
	
	/**
	 * Ramsch game, CJ beats SJ, initial CJ
	 */
	@Test
	public void beats605() {
		
        assertTrue(cardCJ.beats(cardSJ, SkatConstants.GameTypes.RAMSCH, null, cardCJ));
	}

	/**
	 * Ramsch game, DJ doesn't beat SJ, initial CJ
	 */
	@Test
	public void beats606() {
		
        assertFalse(cardDJ.beats(cardSJ, SkatConstants.GameTypes.RAMSCH, null, cardCJ));
	}

	/**
	 * Ramsch game, DT doesn't beat SJ, initial CJ
	 */
	@Test
	public void beats607() {
		
        assertFalse(cardDT.beats(cardSJ, SkatConstants.GameTypes.RAMSCH, null, cardCJ));
	}

	/**
	 * Ramsch game, SJ beats DT, initial CJ
	 */
	@Test
	public void beats608() {
		
        assertTrue(cardSJ.beats(cardDT, SkatConstants.GameTypes.RAMSCH, null, cardCJ));
	}

	/**
	 * Ramsch game, SJ beats HJ, initial CJ
	 */
	@Test
	public void beats609() {
		
        assertTrue(cardSJ.beats(cardHJ, SkatConstants.GameTypes.RAMSCH, null, cardCJ));
	}

	private static Card cardCJ;
	private static Card cardSJ;
	private static Card cardHJ;
	private static Card cardS9;
	private static Card cardDJ;
	private static Card cardDA;
	private static Card cardDT;
	private static Card cardDK;
	private static Card cardDQ;
	private static Card cardD9;
	private static Card cardD8;
	private static Card cardD7;
}
