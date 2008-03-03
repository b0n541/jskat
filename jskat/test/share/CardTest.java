/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test.share;

import jskat.data.GameAnnouncement;
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
		
		cardDJ = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.JACK);
		cardD8 = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT);
		cardS9 = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.NINE);
		cardCJ = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.JACK);

        gameAnnRamsch = new GameAnnouncement();
        gameAnnRamsch.setGameType(SkatConstants.GameTypes.RAMSCH);
	}

	@Test 
	public void beats001() {
    	
        gameAnnRamsch.setTrump(null);
        assertTrue(cardDJ.beats(cardD8, gameAnnRamsch, cardDJ));
	}
	
	@Test
	public void beats002() {
		
        assertFalse(cardD8.beats(cardDJ, gameAnnRamsch, cardDJ));
	}
	
	@Test
	public void beats003() {
		
        assertTrue(cardCJ.beats(cardS9, gameAnnRamsch, cardS9));
	}
	
	@Test
	public void beats004() {
		
        assertFalse(cardS9.beats(cardCJ, gameAnnRamsch, cardS9));
    }
	
	private static Card cardDJ;
	private static Card cardD8;
	private static Card cardS9;
	private static Card cardCJ;
	private static GameAnnouncement gameAnnRamsch;
}
