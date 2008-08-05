package jskat.player.AIPlayerNN;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jskat.data.GameAnnouncement;
import jskat.player.AbstractJSkatPlayer;
import jskat.player.JSkatPlayer;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants.Ranks;
import jskat.share.SkatConstants.Suits;

public class AIPlayerNN extends AbstractJSkatPlayer implements JSkatPlayer {

	private Log log = LogFactory.getLog(AIPlayerNN.class);
	

	public GameAnnouncement announceGame() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean bidMore(int currBidValue) {
		// TODO Auto-generated method stub
		log.debug("ASDFJKLÖ");
		return false;
	}

	public void cardPlayed(Card card) {
		// TODO Auto-generated method stub

	}

	public void discloseOuvertCards(CardVector ouvertCards) {
		// TODO Auto-generated method stub

	}

	public boolean isAIPlayer() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isHumanPlayer() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean lookIntoSkat(boolean isRamsch) {
		// TODO Auto-generated method stub
		return false;
	}

	public Card playCard(CardVector trick) {
		// TODO Auto-generated method stub
		return null;
	}

	public Card removeCard(Suits suit, Ranks rank) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUpBidding(int initialForehandPlayer) {
		// TODO Auto-generated method stub

	}

	public void showTrick(CardVector trick, int trickWinner) {
		// TODO Auto-generated method stub

	}

	public void takeRamschSkat(CardVector skat, boolean jacksAllowed) {
		// TODO Auto-generated method stub

	}

	public void takeSkat(CardVector skat) {
		// TODO Auto-generated method stub

	}

}
