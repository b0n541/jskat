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
    	
        Card card001 = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.JACK);
        Card card002 = new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT);
        GameAnnouncement gameAnn = new GameAnnouncement();
        gameAnn.setGameType(SkatConstants.GameTypes.RAMSCH);
        gameAnn.setTrump(null);
        assertTrue(card001.beats(card002, gameAnn, card001));
        assertFalse(card002.beats(card001, gameAnn, card001));

        card001 = new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.NINE);
        card002 = new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.JACK);
        assertTrue(card002.beats(card001, gameAnn, card001));
        assertFalse(card001.beats(card002, gameAnn, card001));

    }
    
}
