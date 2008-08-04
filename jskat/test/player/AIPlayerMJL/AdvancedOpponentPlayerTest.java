/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.test.share.TestHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
/**
 * AdvancedOpponentPlayerTest.java
 * Created 24.05.2007 15:07:12
 * @author Markus J. Luzius
 *
 */
public class AdvancedOpponentPlayerTest {

	private Log log = LogFactory.getLog(AdvancedOpponentPlayerTest.class);

	private OpponentPlayerTestCase optc;
	private Card result;

//	OpponentPlayerTestCase(int singlePlayerPos, CardVector cards, CardVector trick, int gameType, int trump) {

	@Parameters
	public static Collection data() {
		return Arrays.asList(
			new Object[][] {
				{new OpponentPlayerTestCase(
						0, 
						TestHelper.buildDeck("J-H,J-D,A-S,T-S,7-H,K-D,9-D,7-D"), 
						TestHelper.buildDeck("A-D,J-C"), 
						SkatConstants.GameTypes.SUIT, 
						SkatConstants.Suits.SPADES)
					, new Card("D-K")},
				{new OpponentPlayerTestCase(
						0, 
						TestHelper.buildDeck("J-S,J-D,9-S,8-S,T-H,K-D,Q-D,9-D"), 
						TestHelper.buildDeck("J-C,9-H"), 
						SkatConstants.GameTypes.SUIT, 
						SkatConstants.Suits.HEARTS)
					, new Card("D-J")},
				{new OpponentPlayerTestCase(
						0, 
						TestHelper.buildDeck("Q-C,A-S,Q-S,7-S"), 
						TestHelper.buildDeck("8-H"), 
						SkatConstants.GameTypes.SUIT, 
						SkatConstants.Suits.HEARTS)
					, new Card("S-Q")},
				{new OpponentPlayerTestCase(
						2, 
						TestHelper.buildDeck("J-H,J-D,A-C,9-C,8-S,T-H,9-H"), 
						TestHelper.buildDeck(""), 
						SkatConstants.GameTypes.SUIT, 
						SkatConstants.Suits.CLUBS)
					, new Card("S-8")},
				{new OpponentPlayerTestCase(
						2, 
						TestHelper.buildDeck("J-S,J-D,T-C,9-S,8-S,T-H,K-D,Q-D,9-D,7-D"), 
						new CardVector(), 
						SkatConstants.GameTypes.SUIT, 
						SkatConstants.Suits.HEARTS)
					, new Card("S-9")}
				}
			);
	}
	
	public AdvancedOpponentPlayerTest(OpponentPlayerTestCase optc, Card expectedCard) {
		this.optc = optc;
		result = expectedCard;
	}
	
	/**
	 * Test method for {@link jskat.player.AIPlayerMJL.OpponentPlayer#playNextCard(jskat.share.CardVector, jskat.player.AIPlayerMJL.TrickInfo)}.
	 */
	@Test
	public void playNextCard() {
		Card actual = optc.getCards().getCard(optc.getOpponentPlayer().playNextCard(optc.getCards(), optc.getTrickInfo()));
		assertEquals(result, actual);
	}

}
