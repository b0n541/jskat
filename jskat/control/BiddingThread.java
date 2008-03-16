/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.control;

import org.apache.log4j.Logger;

import jskat.data.JSkatDataModel;
import jskat.data.BidStatus;
import jskat.share.SkatConstants;
import jskat.share.SkatConstants.Player;
import jskat.player.JSkatPlayer;
import jskat.player.HumanPlayer;

/**
 * Controls the bidding
 * Implements Singleton pattern
 */
public class BiddingThread implements Runnable {

	static Logger log = Logger.getLogger(jskat.control.BiddingThread.class);

	/**
	 * Gets an instance of BiddingThread
	 * 
	 * @param dataModel Data model
	 * @param game Game
	 * @param playerOrder Order of players
	 * @return Instance of BiddingThread
	 */
	public static BiddingThread getInstance(JSkatDataModel dataModel, SkatGame game, int[] playerOrder) {
		
		if(myself==null) {
			
			myself = new BiddingThread(dataModel, game, playerOrder);
		}
		else {
			
			myself.resetBiddingThread(dataModel, game, playerOrder);
		}
		return myself;
	}
	
	/**
	 * Constructor is private because of Singleton pattern
	 * 
	 * @param dataModel The JSkatDataModel
	 * @param game Current game
	 * @param playerOrder Player order
	 */
	private BiddingThread(JSkatDataModel dataModel, SkatGame game, int[] playerOrder) {
		this.dataModel = dataModel;
		this.skatGame = game;
		this.players = skatGame.getSkatGameData().getPlayers();
		this.playerOrder = playerOrder;
		bidStatus = new BidStatus();
		isInterruptable = false;
	}
	
	private void resetBiddingThread(JSkatDataModel dataModel, SkatGame game, int[] playerOrder) {
		this.dataModel = dataModel;
		this.skatGame = game;
		this.players = skatGame.getSkatGameData().getPlayers();
		this.playerOrder = playerOrder;
		bidStatus = new BidStatus();
		isInterruptable = false;
	}
	

	/**
	 * Starts a new thread for bidding
	 */
	public void start() {
		log.debug("Starting bidding thread: "+this);
		aThread = new Thread(this);
		log.debug("BiddingThread: "+aThread.getId());
		aThread.start();
	}

	/**
	 * Is called if a user places a higher bid
	 * 
	 * @param userBidsMore 
	 */
	public void notifyMe(boolean userBidsMore) {

		if (isInterruptable) {

			bidStatus.setHandDoesBid(currentBidHand, userBidsMore);

			aThread.interrupt();
		}
	}

	/**
	 * Gets the current bid status
	 * 
	 * @return bid status
	 */
	public BidStatus getBidStatus() {

		return bidStatus;
	}

	/**
	 * Runs the bidding thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		log.debug("BiddingThread ("+Thread.currentThread().getId()+") START");

		int currBidValue = 0;
		int currBidOrderIndex = -1;

		bidStatus.resetBidStatus(playerOrder[0]);

		log
				.debug("Informing players of new bidding process. Game forehand = player "
						+ playerOrder[0]);
		for (int i = 0; i < 3; i++) {
			players[i].setUpBidding(playerOrder[0]);
		}

		while (bidStatus.getHandDoesBid(Player.FORE_HAND)
				&& bidStatus.getHandDoesBid(Player.MIDDLE_HAND)) {

			// get next valid bid value
			currBidOrderIndex++;
			currBidValue = SkatConstants.bidOrder[currBidOrderIndex];
			log.debug("currBidValue=" + currBidValue);

			// ask Middlehand Player
			bidStatus.setHandDoesBid(Player.MIDDLE_HAND, askForBidValue(
					Player.MIDDLE_HAND, currBidValue, false));

			// ask Forehand Player (only if middlehand holds the bid)
			if (bidStatus.getHandDoesBid(Player.MIDDLE_HAND)) {

				log.debug("Middlehand: " + currBidValue);

				bidStatus.setBidValue(Player.MIDDLE_HAND, currBidValue);

				bidStatus.setHandDoesBid(Player.FORE_HAND,
						askForBidValue(Player.FORE_HAND, currBidValue,
								true));

				if (bidStatus.getHandDoesBid(Player.FORE_HAND)) {

					log.debug("Forehand: Yes");

					bidStatus
							.setBidValue(Player.FORE_HAND, currBidValue);
				}
				else {
					log.debug("Forehand: Passe");
				}
			} else {
				log.debug("Middlehand: Passe");
			}

		}

		if (bidStatus.getHandDoesBid(Player.MIDDLE_HAND)) {

			while (bidStatus.getHandDoesBid(Player.MIDDLE_HAND)
					&& bidStatus.getHandDoesBid(Player.BACK_HAND)) {

				// get next valid bid value
				currBidOrderIndex++;
				currBidValue = SkatConstants.bidOrder[currBidOrderIndex];

				// ask Hindhand Player
				bidStatus.setHandDoesBid(Player.BACK_HAND,
						askForBidValue(Player.BACK_HAND, currBidValue,
								false));

				// ask Middlehand Player
				if (bidStatus.getHandDoesBid(Player.BACK_HAND)) {

					log.debug("Hindhand: " + currBidValue);

					bidStatus
							.setBidValue(Player.BACK_HAND, currBidValue);

					bidStatus.setHandDoesBid(Player.MIDDLE_HAND,
							askForBidValue(Player.MIDDLE_HAND,
									currBidValue, true));

					if (bidStatus.getHandDoesBid(Player.MIDDLE_HAND)) {

						log.debug("Middlehand: Yes");

						bidStatus.setBidValue(Player.MIDDLE_HAND,
								currBidValue);
					}
					else {
						log.debug("Middlehand: Passe");
					}
				}
				else {
					log.debug("Backhand: Passe");
				}
			}
		} else {

			// Forehand player was not asked the current bid value
			// that's why the same current bid value is used here
			log.debug("Middlehand is out.");

			while (bidStatus.getHandDoesBid(Player.FORE_HAND)
					&& bidStatus.getHandDoesBid(Player.BACK_HAND)) {

				// ask Middlehand Player
				bidStatus.setHandDoesBid(Player.BACK_HAND,
						askForBidValue(Player.BACK_HAND, currBidValue,
								false));

				// ask Forehand Player
				if (bidStatus.getHandDoesBid(Player.BACK_HAND)) {

					log.debug("Hindhand: " + currBidValue);

					bidStatus
							.setBidValue(Player.BACK_HAND, currBidValue);

					bidStatus.setHandDoesBid(Player.FORE_HAND,
							askForBidValue(Player.FORE_HAND,
									currBidValue, true));

					if (bidStatus.getHandDoesBid(Player.FORE_HAND)) {

						log.debug("Forehand: Yes");

						bidStatus.setBidValue(Player.FORE_HAND,
								currBidValue);
					}
					else {
						log.debug("Forehand: Passe");
					}
				} else if (currBidValue == SkatConstants.bidOrder[0]) {
					log.debug("Hindhand: No on 18");
					// here forehand should be asked again...??
					bidStatus.setHandDoesBid(Player.FORE_HAND,
							askForBidValue(Player.FORE_HAND,
									currBidValue, true));

					if (bidStatus.getHandDoesBid(Player.FORE_HAND)) {

						log.debug("Forehand: 18 I have");

						bidStatus.setBidValue(Player.FORE_HAND,
								currBidValue);
					}
					else {
						log.debug("Forehand: Passe");
					}
				}

				// get next valid bid value
				currBidOrderIndex++;
				currBidValue = SkatConstants.bidOrder[currBidOrderIndex];
			}
		}

		log.debug("Next bid value " + currBidValue);
		log.debug("Forehand bids "
				+ bidStatus.getBidValue(Player.FORE_HAND)
				+ ", still in: "
				+ bidStatus.getHandDoesBid(Player.FORE_HAND));
		log.debug("Middlehand bids "
				+ bidStatus.getBidValue(Player.MIDDLE_HAND)
				+ ", still in: "
				+ bidStatus.getHandDoesBid(Player.MIDDLE_HAND));
		log.debug("Hindhand bids "
				+ bidStatus.getBidValue(Player.BACK_HAND)
				+ ", still in: "
				+ bidStatus.getHandDoesBid(Player.BACK_HAND));

		if (bidStatus.getHandDoesBid(Player.FORE_HAND)) {

			skatGame.getSkatGameData().setSinglePlayer(
					playerOrder[0]);
		}

		if (bidStatus.getHandDoesBid(Player.MIDDLE_HAND)) {

			skatGame.getSkatGameData().setSinglePlayer(
					playerOrder[1]);
		}

		if (bidStatus.getHandDoesBid(Player.BACK_HAND)) {

			skatGame.getSkatGameData().setSinglePlayer(
					playerOrder[2]);
		}

		if (skatGame.getSkatGameData().getSinglePlayer() >= 0) {

			log.debug("Player "
					+ players[skatGame.getSkatGameData().getSinglePlayer()]
							.getPlayerName() + " wins the bidding.");
		}

		skatGame.getSkatGameData().setBidValue(
		bidStatus.getHighestBidValue());

		log.debug("BiddingThread ("+Thread.currentThread().getId()+") DONE");

		// TODO (2007/09/11 mjl) This should be done via GameState, not explicitly in BiddingThread
		skatGame.showSkat();

		log.debug("BiddingThread ("+Thread.currentThread().getId()+") TERMINATED");
}

	private boolean askForBidValue(Player currentPlayer, int currBidValue,
			boolean wasAsked) {

		log.debug("askForBidValue(): player " + currentPlayer);

		currentBidHand = currentPlayer;

		boolean bidMore = false;

		if (players[playerOrder[currentPlayer.getOrder()]] instanceof HumanPlayer) {

			bidStatus.setButtonsEnabled(true);

			bidStatus.setWasAsked(currentBidHand);

			log.debug("Human Player was asked!");
			waitMe();

			return bidStatus.getHandDoesBid(currentPlayer);

		} else {

			bidStatus.setButtonsEnabled(true);

			waitMe(dataModel.getJSkatOptions().getTrickRemoveDelayTime() + 1);

			// ask the AI Player whether it wants to do the bid value or not
			try {

				log.debug("Asking AIPlayer to bid more");
				bidMore = ((JSkatPlayer) players[playerOrder[currentPlayer.getOrder()]])
						.bidMore(currBidValue);

			} catch (java.lang.ClassCastException e) {

				log.error("Error: Non-conform AIPlayer");
			}
		}

		return bidMore;
	}

	private synchronized void waitMe() {

		isInterruptable = true;

		try {

			wait();

		} catch (Exception ex) {

			log.debug("THREAD ("+Thread.currentThread().getId()+"): got interrupt signal");
		}
	}

	private synchronized void waitMe(int milliSecs) {

		isInterruptable = false;

		try {

			wait(milliSecs);

		} catch (Exception ex) {

			log.debug("THREAD ("+Thread.currentThread().getId()+"): got message");
		}
	}

	private Thread aThread;

	//private SkatTable skatTable;

	private JSkatDataModel dataModel;
	
	private SkatGame skatGame;

	private BidStatus bidStatus;

	private Player currentBidHand;

	private JSkatPlayer[] players;

	private int[] playerOrder;

	private boolean isInterruptable;
	
	private static BiddingThread myself = null; 

}