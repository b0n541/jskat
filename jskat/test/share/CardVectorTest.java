/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

*/

package jskat.test.share;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;

/**
 * Unit tests for class CardVector<br />
 * @see jskat.share.CardVector
 */
public class CardVectorTest {

	/**
	 * Sets up the objects used in the tests
	 */
	@BeforeClass
	public static void setUp() {
	
		hand001 = new CardVector();
		hand001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.ACE));
		hand001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.TEN));
		hand001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.KING));
		hand001.add(new Card(SkatConstants.Suits.CLUBS, SkatConstants.Ranks.QUEEN));
		hand001.add(new Card(SkatConstants.Suits.HEARTS, SkatConstants.Ranks.JACK));
		hand001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.TEN));
		hand001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.EIGHT));
		hand001.add(new Card(SkatConstants.Suits.DIAMONDS, SkatConstants.Ranks.SEVEN));
	}

	/**
	 * Null game, suit CLUBS
	 */
	@Test
	public void hasSuit001() {
		
		assertTrue(hand001.hasSuit(SkatConstants.GameTypes.NULL, SkatConstants.Suits.CLUBS));
	}

	/**
	 * Null game, suit SPADES
	 */
	@Test
	public void hasSuit002() {
		
		assertFalse(hand001.hasSuit(SkatConstants.GameTypes.NULL, SkatConstants.Suits.SPADES));
	}

	/**
	 * Null game, suit HEARTS
	 */
	@Test
	public void hasSuit003() {
		
		assertTrue(hand001.hasSuit(SkatConstants.GameTypes.NULL, SkatConstants.Suits.HEARTS));
	}

	/**
	 * Null game, suit DIAMONDS
	 */
	@Test
	public void hasSuit004() {
		
		assertTrue(hand001.hasSuit(SkatConstants.GameTypes.NULL, SkatConstants.Suits.DIAMONDS));
	}
	
	/**
	 * Suit game, trump CLUBS, suit CLUBS
	 */
	@Test
	public void hasSuit201() {
		
		assertFalse(hand001.hasSuit(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.CLUBS, SkatConstants.Suits.CLUBS));
	}
	
	/**
	 * Suit game, trump CLUBS, suit SPADES
	 */
	@Test
	public void hasSuit202() {
		
		assertFalse(hand001.hasSuit(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.CLUBS, SkatConstants.Suits.SPADES));
	}
	
	/**
	 * Suit game, trump CLUBS, suit HEARTS
	 */
	@Test
	public void hasSuit203() {
		
		assertFalse(hand001.hasSuit(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.CLUBS, SkatConstants.Suits.HEARTS));
	}
	
	/**
	 * Suit game, trump CLUBS, suit DIAMONDS
	 */
	@Test
	public void hasSuit204() {
		
		assertTrue(hand001.hasSuit(SkatConstants.GameTypes.SUIT, SkatConstants.Suits.CLUBS, SkatConstants.Suits.DIAMONDS));
	}
	
	/**
	 * Grand game, suit CLUBS
	 */
	@Test
	public void hasSuit401() {
		
		assertTrue(hand001.hasSuit(SkatConstants.GameTypes.GRAND, SkatConstants.Suits.CLUBS));
	}
	
	/**
	 * Grand game, suit SPADES
	 */
	@Test
	public void hasSuit402() {
		
		assertFalse(hand001.hasSuit(SkatConstants.GameTypes.GRAND, SkatConstants.Suits.SPADES));
	}
	
	/**
	 * Grand game, suit HEARTS
	 */
	@Test
	public void hasSuit403() {
		
		assertFalse(hand001.hasSuit(SkatConstants.GameTypes.GRAND, SkatConstants.Suits.HEARTS));
	}
	
	/**
	 * Grand game, suit DIAMONDS
	 */
	@Test
	public void hasSuit404() {
		
		assertTrue(hand001.hasSuit(SkatConstants.GameTypes.GRAND, SkatConstants.Suits.DIAMONDS));
	}
	
	/**
	 * Ramsch game, suit CLUBS
	 */
	@Test
	public void hasSuit601() {
		
		assertTrue(hand001.hasSuit(SkatConstants.GameTypes.RAMSCH, SkatConstants.Suits.CLUBS));
	}
	
	/**
	 * Ramsch game, suit SPADES
	 */
	@Test
	public void hasSuit602() {
		
		assertFalse(hand001.hasSuit(SkatConstants.GameTypes.RAMSCH, SkatConstants.Suits.SPADES));
	}
	
	/**
	 * Ramsch game, suit HEARTS
	 */
	@Test
	public void hasSuit603() {
		
		assertFalse(hand001.hasSuit(SkatConstants.GameTypes.RAMSCH, SkatConstants.Suits.HEARTS));
	}
	
	/**
	 * Ramsch game, suit DIAMONDS
	 */
	@Test
	public void hasSuit604() {
		
		assertTrue(hand001.hasSuit(SkatConstants.GameTypes.RAMSCH, SkatConstants.Suits.DIAMONDS));
	}

	private static CardVector hand001;
}
