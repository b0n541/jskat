/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import jskat.player.AIPlayerMJL.Helper;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.rules.SkatRules;
import jskat.share.rules.SkatRulesFactory;

public class HelperTest {

	@BeforeClass
	public static void setUp() {

		cards001 = new CardVector();
		cards001.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.JACK));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.JACK));
		cards001.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.ACE));
		cards001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.TEN));
		cards001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.KING));
		cards001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.QUEEN));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.NINE));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT));
		cards001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.SEVEN));
		cards001.sort(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.HEARTS);

		trick001 = new CardVector();
		trick001.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.ACE));
		trick001.add(new Card(SkatConstants.Suits.SPADES, SkatConstants.Ranks.SEVEN));

	}

	@Test
	public void isSinglePlayerWin() {
		//TODO Implement isSinglePlayerWin().
	}

	@Test
	public void isAbleToBeat() {
		//TODO Implement isAbleToBeat().
	}

	@Test
	public void isAbleToMatch001() {
		trump = SkatConstants.Suits.HEARTS;
		gameType = SkatConstants.GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertEquals(false, Helper.isAbleToMatch(rules, cards001, trump, trick001.getCard(0), gameType));
	}
	
	@Test
	public void isAbleToMatch002() {
		trump = SkatConstants.Suits.SPADES;
		gameType = SkatConstants.GameTypes.SUIT;
		rules = SkatRulesFactory.getSkatRules(gameType);
		assertEquals(true, Helper.isAbleToMatch(rules, cards001, trump, trick001.getCard(0), gameType));
	}

	@Test
	public void getHighestTrump() {
		//TODO Implement getHighestTrump().
	}

	@Test
	public void hasTrump() {
		//TODO Implement hasTrump().
	}

	private static CardVector cards001;
	CardVector cards002;
	private static CardVector trick001;
	Card played;
	Card initialCard;
	SkatConstants.GameTypes gameType;
	SkatConstants.Suits trump;
	SkatRules rules;
}
