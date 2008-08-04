/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import jskat.player.AIPlayerMJL.GameInfo;
import jskat.player.AIPlayerMJL.SinglePlayer;
import jskat.player.AIPlayerMJL.TrickInfo;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.Tools;
import jskat.share.rules.SkatRules;
import jskat.share.rules.SkatRulesFactory;
import jskat.test.share.TestHelper;

public class SinglePlayerTest {

	private Log log = LogFactory.getLog(SinglePlayerTest.class);

	@BeforeClass
	public static void setUp() {

		gi001 = new GameInfo(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.SPADES, 0);

		cards001 = TestHelper.buildDeck("J-H,J-D,A-S,T-S,7-H,K-D,9-D,7-D");

		trick001 = new CardVector();
		trick001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.ACE));
		trick001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT));
	}

	@Test
	public void playNextCard() {
		
		rules = SkatRulesFactory.getSkatRules(gi001.getGameType());
		SinglePlayer sp = new SinglePlayer(1, rules);
		TrickInfo ti = new TrickInfo();
		ti.setGameInfo(gi001);
		ti.setTrick(trick001);		
		ti.setSinglePlayerPos(2);
		// should play king of diamonds (seven would be better though...) 
		assertEquals(5, sp.playNextCard(cards001, ti));
	}

	private static CardVector cards001;
	private static CardVector trick001;
	private static GameInfo gi001;
	Card played;
	Card initialCard;
	int gameType;
	int trump;
	SkatRules rules;
}
