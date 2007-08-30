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
import junit.framework.TestCase;

/**
 * @author markusl
 *
 */
public class CardTest extends TestCase {
	/**
	 * Constructor for SkatRulesTest.
	 * @param arg0
	 */
	public CardTest(String arg0) {
		super(arg0);
	}

    public static void main(String[] args) {
		junit.textui.TestRunner.run(CardTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testBeats() {
    	
        Card card001 = new Card(SkatConstants.DIAMONDS, SkatConstants.JACK);
        Card card002 = new Card(SkatConstants.DIAMONDS, SkatConstants.EIGHT);
        GameAnnouncement gameAnn = new GameAnnouncement();
        gameAnn.setGameType(SkatConstants.RAMSCH);
        gameAnn.setTrump(-1);
        assertTrue(card001.beats(card002, gameAnn, card001));
        assertFalse(card002.beats(card001, gameAnn, card001));

        card001 = new Card(SkatConstants.SPADES, SkatConstants.NINE);
        card002 = new Card(SkatConstants.CLUBS, SkatConstants.JACK);
        assertTrue(card002.beats(card001, gameAnn, card001));
        assertFalse(card001.beats(card002, gameAnn, card001));

    }
    
}
