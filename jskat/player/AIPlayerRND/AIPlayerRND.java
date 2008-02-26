/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.player.AIPlayerRND;

import java.util.Random;

import org.apache.log4j.Logger;

import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.SkatConstants.Ranks;
import jskat.share.SkatConstants.Suits;
import jskat.data.GameAnnouncement;
import jskat.player.AbstractJSkatPlayer;
import jskat.player.JSkatPlayer;

/**
 * Random player for testing
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class AIPlayerRND extends AbstractJSkatPlayer implements JSkatPlayer {

	private static final Logger log = Logger.getLogger(AIPlayerRND.class);

	/** 
	 * Creates a new instance of AIPlayerRND
	 *  
	 * @param playerID Player ID for the player 
	 */
	public AIPlayerRND(int playerID) {

		super();
		log.debug("Constructing new AI player.");
		setPlayerID(playerID);
		setPlayerName("Nobody");
	}

	/** 
	 * Creates a new instance of AIPlayerRND 
	 */
	public AIPlayerRND() {

		super();
		log.debug("Constructing new AIPlayerRND");
	}

	/** 
	 * Creates a new instance of AIPlayerRND
	 * 
	 * @param playerID Player ID for the player 
	 * @param playerName Player's name
	 */
	public AIPlayerRND(int playerID, String playerName) {

		super();
		log.debug("Constructing new AIPlayerRND");
		setPlayerID(playerID);
		setPlayerName(playerName);
	}

	/**
	 * @see jskat.player.JSkatPlayer#takeRamschSkat(jskat.share.CardVector, boolean)
	 */
	public void takeRamschSkat(CardVector skat, boolean jacksAllowed) {
		// TODO implement it
	}

	/**
	 * @see jskat.player.JSkatPlayer#lookIntoSkat(boolean)
	 */
	public boolean lookIntoSkat(boolean isRamsch) {
		
		return rand.nextBoolean();
	}

	/**
	 * @see jskat.player.JSkatPlayer#announceGame()
	 */
	public GameAnnouncement announceGame() {

		GameAnnouncement newGame = new GameAnnouncement();

		newGame.setGameType(SkatConstants.GameTypes.SUIT);
		newGame.setTrump(cards.getMostFrequentSuit());
		newGame.setOuvert(rand.nextBoolean());

		return newGame;
	}

	/**
	 * @see jskat.player.JSkatPlayer#bidMore(int)
	 */
	public boolean bidMore(int currBidValue) {

		return rand.nextBoolean();
	}

	/**
	 * @see jskat.player.JSkatPlayer#playCard(jskat.share.CardVector)
	 */
	public Card playCard(CardVector trick) {

		int index = -1;

		CardVector possibleCards = new CardVector();
		
		if (trick.size() > 0) {
			
			log.debug("trick size: " + trick.size() + " initial card: " + trick.getCard(0));
			
			for (int i = 0; i < cards.size(); i++) {
				if (rules.isCardAllowed(cards.getCard(i), cards, trick
						.getCard(0), currTrump)) {
					
					possibleCards.add(cards.getCard(i));
				}
			}
		} else {
			
			possibleCards = cards;
		}

		log.debug("found " + possibleCards.size() + " possible cards");

		int rand = new Double(Math.random() * possibleCards.size()).intValue();

		log.debug("choosing card " + rand);
		index = cards.getIndexOf(possibleCards.getCard(rand).getSuit(),
				possibleCards.getCard(rand).getRank());

		log.debug("as player " + playerID + ": " + cards.getCard(index));

		return cards.remove(index);
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#showTrick(jskat.share.CardVector, int)
	 */
	@Override
	public void showTrick(CardVector trick, int trickWinner) {
		// just ignore it
	}

	/**
	 * @see jskat.player.JSkatPlayer#isAIPlayer()
	 */
	public boolean isAIPlayer() {

		return true;
	}

	/**
	 * @see jskat.player.JSkatPlayer#isHumanPlayer()
	 */
	public boolean isHumanPlayer() {

		return false;
	}
	
	Random rand = new Random();

	/**
	 * @see jskat.player.AbstractJSkatPlayer#cardPlayed(jskat.share.Card)
	 */
	@Override
	public void cardPlayed(Card card) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#discloseOuvertCards(jskat.share.CardVector)
	 */
	@Override
	public void discloseOuvertCards(CardVector ouvertCards) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#setUpBidding(int)
	 */
	@Override
	public void setUpBidding(int initialForehandPlayer) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.AbstractJSkatPlayer#takeSkat(jskat.share.CardVector)
	 */
	@Override
	public void takeSkat(CardVector skat) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see jskat.player.JSkatPlayer#removeCard(jskat.share.SkatConstants.Suits, jskat.share.SkatConstants.Ranks)
	 */
	public Card removeCard(Suits suit, Ranks rank) {
		// TODO Auto-generated method stub
		return null;
	}
}