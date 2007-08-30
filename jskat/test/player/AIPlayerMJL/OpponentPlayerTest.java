/*

@ShortLicense@

Authos: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import jskat.player.AIPlayerMJL.GameInfo;
import jskat.player.AIPlayerMJL.OpponentPlayer;
import jskat.player.AIPlayerMJL.TrickInfo;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.Tools;
import jskat.test.share.TestHelper;
import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 * @deprecated
 */
public class OpponentPlayerTest extends TestCase {
	/**
	 * Constructor for OpponentPlayerTest.
	 * @param arg0
	 */
	public OpponentPlayerTest(String arg0) {
		super(arg0);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(OpponentPlayerTest.class);
	}
	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		Tools.checkLog();

		gi001 = new GameInfo(SkatConstants.SUIT, SkatConstants.HEARTS, 0);

		cards001 = new CardVector();
		cards001.add(new Card(SkatConstants.SPADES, SkatConstants.JACK));
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

		trick001 = new CardVector();
		trick001.add(new Card(SkatConstants.SPADES, SkatConstants.ACE));
		trick001.add(new Card(SkatConstants.SPADES, SkatConstants.SEVEN));

		trick002 = new CardVector();
		trick002.add(new Card(SkatConstants.CLUBS, SkatConstants.ACE));
		trick002.add(new Card(SkatConstants.CLUBS, SkatConstants.SEVEN));

		trick003 = new CardVector();
		trick003.add(new Card(SkatConstants.CLUBS, SkatConstants.NINE));
		trick003.add(new Card(SkatConstants.CLUBS, SkatConstants.SEVEN));

		// Hand [J-H, J-D, A-S, 10-S, 7-H, K-D, 9-D, 7-D] with trick [A-D, J-C].
		cards002 = TestHelper.buildDeck("J-H,J-D,A-S,T-S,7-H,K-D,9-D,7-D");
		System.out.println("generated hand: "+cards002);
		cards002 = new CardVector();
		cards002.add(new Card(SkatConstants.HEARTS, SkatConstants.JACK));
		cards002.add(new Card(SkatConstants.DIAMONDS, SkatConstants.JACK));
		cards002.add(new Card(SkatConstants.SPADES, SkatConstants.ACE));
		cards002.add(new Card(SkatConstants.SPADES, SkatConstants.TEN));
		cards002.add(new Card(SkatConstants.HEARTS, SkatConstants.SEVEN));
		cards002.add(new Card(SkatConstants.DIAMONDS, SkatConstants.KING));
		cards002.add(new Card(SkatConstants.DIAMONDS, SkatConstants.NINE));
		cards002.add(new Card(SkatConstants.DIAMONDS, SkatConstants.SEVEN));

		trick004 = new CardVector();
		trick004.add(new Card(SkatConstants.DIAMONDS, SkatConstants.ACE));
		trick004.add(new Card(SkatConstants.CLUBS, SkatConstants.JACK));

		trick005 = new CardVector();
		trick005.add(new Card(SkatConstants.CLUBS, SkatConstants.JACK));
		trick005.add(new Card(SkatConstants.DIAMONDS, SkatConstants.ACE));

		cards003 = TestHelper.buildDeck("J-C,9-H,T-C,Q-C,7-C,T-S,8-S,7-S,8-D");
		trick006 = TestHelper.buildDeck("J-D,7-H");

		// K-D, Q-D, K-H, 9-H, 8-H, K-C, 9-C
		cards004 = TestHelper.buildDeck("K-D,Q-D,K-H,9-H,8-H,K-C,9-C");
		trick007 = TestHelper.buildDeck("A-H,7-H");
	
		// J-D, K-S, 7-H, 9-C, 8-C, 10-D
		cards008 = TestHelper.buildDeck("J-D,K-S,7-H,9-C,8-C,T-D");
		trick008 = TestHelper.buildDeck("J-H,J-C");
	}

	/**
	 * Tests for playing the next card
	 */
	public void testGetNextCard() {
		OpponentPlayer op = new OpponentPlayer(1);
		TrickInfo ti = new TrickInfo();
		ti.setGameInfo(gi001);
		ti.setTrick(trick001);		
		ti.setSinglePlayerPos(1);
		// should play ten of clubs ("schmieren") 
		assertEquals(3, op.playNextCard(cards001, ti));

		ti.setSinglePlayerPos(0);
		// should play ten of clubs ("trumpfen") 
		assertEquals(2, op.playNextCard(cards001, ti));

		ti.setTrick(trick002);
		ti.setSinglePlayerPos(1);
		// should play ten of clubs 
		assertEquals(3, op.playNextCard(cards001, ti));

		ti.setSinglePlayerPos(0);
		// should play queen of clubs 
		assertEquals(5, op.playNextCard(cards001, ti));

		ti.setTrick(trick001);
		ti.getGameInfo().setTrump(SkatConstants.SPADES);
		// should play jack of diamonds
		// -- plays jack of spades right now (to be improved)
		assertEquals(0, op.playNextCard(cards001, ti));

		ti.setTrick(trick003);
		ti.setSinglePlayerPos(1);
		// should play ten of clubs 
		assertEquals(3, op.playNextCard(cards001, ti));

		ti.setSinglePlayerPos(0);
		// should play ten of clubs 
		assertEquals(3, op.playNextCard(cards001, ti));

		ti.setTrick(trick004);
		ti.setSinglePlayerPos(0);
		// should play king of diamonds 
		assertEquals(5, op.playNextCard(cards002, ti));

		ti.setSinglePlayerPos(1);
		// should play seven of diamonds 
		assertEquals(7, op.playNextCard(cards002, ti));

		ti.setTrick(trick005);
		ti.setSinglePlayerPos(0);
		// should play jack of diamonds 
		assertEquals(1, op.playNextCard(cards001, ti));

		ti.getGameInfo().setTrump(SkatConstants.DIAMONDS);
		// should play seven of diamonds 
		assertEquals(9, op.playNextCard(cards001, ti));
		
		// logilfe:
		//Processing hand [J-S, J-D, 10-H, 7-H, K-S, 9-S, 7-S, 10-C] with trick [A-S]. Trump is x.
		//player 0: K-S
		// --> should have played 7-S instead!!! 

		// Problem with Jacks being played as first card
		gi001.setTrump(SkatConstants.HEARTS);
		ti.setGameInfo(gi001);
		ti.setTrick(trick006);
		ti.setSinglePlayerPos(0);
		// should play jack of diamonds 
		assertEquals(0, op.playNextCard(cards003, ti));

		// and again....
		gi001.setTrump(SkatConstants.SPADES);
		ti.setGameInfo(gi001);
		ti.setTrick(trick008);
		ti.setSinglePlayerPos(0);
		// should play king of spades 
		assertEquals(1, op.playNextCard(cards008, ti));

		
		// Problem with giving a value card
		gi001.setTrump(SkatConstants.SPADES);
		ti.setGameInfo(gi001);
		ti.setTrick(trick007);
		ti.setSinglePlayerPos(1);
		// should play king of hearts 
		assertEquals(2, op.playNextCard(cards004, ti));
	
	}

	CardVector cards001;
	CardVector cards002;
	CardVector cards003;
	CardVector cards004;
	CardVector cards008;
	CardVector trick001;
	CardVector trick002;
	CardVector trick003;
	CardVector trick004;
	CardVector trick005;
	CardVector trick006;
	CardVector trick007;
	CardVector trick008;
	GameInfo gi001;
	Card played;
	Card initialCard;
	int gameType;
	int trump;

	static Logger log = Logger.getLogger(OpponentPlayerTest.class);
}
