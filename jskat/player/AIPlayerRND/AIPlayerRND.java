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
import jskat.share.rules.AbstractSkatRules;
import jskat.data.GameAnnouncement;
import jskat.player.AbstractJSkatPlayer;
import jskat.player.JSkatPlayer;

public class AIPlayerRND extends AbstractJSkatPlayer implements JSkatPlayer {

	private static final Logger log = Logger.getLogger(AIPlayerRND.class);

	/** Creates a new instance of SkatPlayer */
	public AIPlayerRND(int playerID) {

		super();
		log.debug("Constructing new AI player.");
		setPlayerID(playerID);
		setPlayerName("Nobody");
	}

	/** Creates a new instance of SkatPlayer */
	public AIPlayerRND() {

		super();
		log.debug("Constructing new AIPlayerRND");
	}

	/** Creates a new instance of SkatPlayer */
	public AIPlayerRND(int playerID, String playerName) {

		super();
		log.debug("Constructing new AIPlayerRND");
		setPlayerID(playerID);
		setPlayerName(playerName);
	}

	public void takeRamschSkat(CardVector skat, boolean jacksAllowed) {

	}

	public boolean lookIntoSkat(boolean isRamsch) {
		
		return rand.nextBoolean();
	}

	public GameAnnouncement announceGame() {

		GameAnnouncement newGame = new GameAnnouncement();

		newGame.setGameType(SkatConstants.GameTypes.SUIT);
		newGame.setTrump(cards.getMostFrequentSuitColor());
		newGame.setOuvert(rand.nextBoolean());

		return newGame;
	}

	public boolean bidMore(int currBidValue) {

		return rand.nextBoolean();
	}

	public Card playCard(CardVector trick) {

		int index = -1;

		CardVector possibleCards = new CardVector();
		
		if (trick.size() > 0) {
			
			log.debug("trick size: " + trick.size() + " initial card: " + trick.getCard(0));
			
			for (int i = 0; i < cards.size(); i++) {
				if (AbstractSkatRules.isCardAllowed(cards.getCard(i), cards, trick
						.getCard(0), currGameType, currTrump)) {
					
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

	public void showTrick(CardVector trick, int trickWinner) {
		// just ignore it
	}

	public boolean isAIPlayer() {

		return true;
	}

	public boolean isHumanPlayer() {

		return false;
	}
	
	Random rand = new Random();
}