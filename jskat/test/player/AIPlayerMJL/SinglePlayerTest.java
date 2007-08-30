/*

@ShortLicense@

Authos: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import junit.framework.TestCase;
import jskat.player.AIPlayerMJL.GameInfo;
import jskat.player.AIPlayerMJL.SinglePlayer;
import jskat.player.AIPlayerMJL.TrickInfo;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.test.share.TestHelper;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class SinglePlayerTest extends TestCase {

	static Logger log = Logger.getLogger(SinglePlayerTest.class);
	
	/**
	 * Constructor for OpponentPlayerTest.
	 * @param arg0
	 */
	public SinglePlayerTest(String arg0) {
		super(arg0);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    //PropertyConfigurator.configure("C:\\Programme\\Tools\\dev\\eclipse\\workspace\\jskat\\lib\\log4j.properties");
		BasicConfigurator.configure();
	    
	    log.debug(".....");
	    log.info(".....");
	    
		junit.textui.TestRunner.run(SinglePlayerTest.class);
	}
	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		gi001 = new GameInfo(SkatConstants.SUIT, SkatConstants.SPADES, 0);

		cards001 = TestHelper.buildDeck("J-H,J-D,A-S,T-S,7-H,K-D,9-D,7-D");

		trick001 = new CardVector();
		trick001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.ACE));
		trick001.add(new Card(SkatConstants.DIAMONDS, SkatConstants.EIGHT));
}

	/**
	 * Tests for playing the next card
	 */
	public void testPlayNextCard() {
		SinglePlayer sp = new SinglePlayer(1);
		TrickInfo ti = new TrickInfo();
		ti.setGameInfo(gi001);
		ti.setTrick(trick001);		
		ti.setSinglePlayerPos(2);
		// should play king of diamonds (seven would be better though...) 
		assertEquals(5, sp.playNextCard(cards001, ti));

	}

	CardVector cards001;
	CardVector trick001;
	GameInfo gi001;
	Card played;
	Card initialCard;
	int gameType;
	int trump;
}
