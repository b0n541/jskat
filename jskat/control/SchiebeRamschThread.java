/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.control;

import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import jskat.data.GameAnnouncement;
import jskat.data.JSkatDataModel;
import jskat.data.SkatGameData;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.player.JSkatPlayer;

/**
 * Controls the Schieberramsch
 */
public class SchiebeRamschThread implements Runnable {

	private static final Logger log = Logger
			.getLogger(jskat.control.JSkatMaster.class);

	/**
	 * Initializes the SchiebeRamschThread
	 * 
	 * @param dataModel The JSkatDataModel
	 */
	public static void init(JSkatDataModel dataModel) {
		
		SchiebeRamschThread.dataModel = dataModel;
		jskatStrings = dataModel.getResourceBundle();
	}
	
	/** 
	 * Creates a new instance of SchieberRamschThread
	 *  
	 * @param game The current skat game 
	 */
	public SchiebeRamschThread(SkatGame game) {

		this.skatGame = game;
		isInterruptable = false;
	}

	/**
	 * Starts a new thread for pushing the cards around the table
	 */
	public void start() {

		log.debug("Setting up SchieberRamschThread...");

		this.players = skatGame.getSkatGameData().getPlayers();
		this.playerOrder = skatGame.getPlayerOrder();

		aThread = new Thread(this);
		aThread.start();
	}

	/**
	 * Is called if the user has looked into the skat
	 */
	public void notifyMe() {

		if (isInterruptable) {

			aThread.interrupt();
		}
	}

	/**
	 * Runs the card pushing thread
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		log.debug("SchieberRamschThread START");
		SkatGameData gameData = skatGame.getSkatGameData();
		CardVector skat = gameData.getSkat();

		// here comes the special version of showSkat.....
		for (int loop = 0; loop < 3; loop++) {
			
			int currPlayer = (playerOrder[0] + loop) % 3;

			log.debug("Current player is " + currPlayer);
			log.debug("Before:  "+ gameData.getPlayerCards(currPlayer) + 
					", Skat="+gameData.getSkat());
			
			// if (players[currPlayer] instanceof HumanPlayer) {
			if (players[currPlayer].isHumanPlayer()) {

				int choice = JOptionPane
						.showConfirmDialog(dataModel.getMainWindow(),
								jskatStrings.getString("look_into_skat"),
								jskatStrings.getString("look_into_skat"),
								JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.YES_OPTION) {

					gameData.setHand(false);
					skatGame.showSkat();
					waitMe();
					
					// it needs to be checked again, whether there are illegal Jacks in the skat
					// (there might have been some by dealing - the player cannot put them there)
					while (!skatGame.getSkatTableOptions().isSchieberRamschJacksInSkat()
							&& (skat.hasTrump(SkatConstants.GameTypes.RAMSCH, null))  ) {

						log.info("Human player has tried to put a Jack into skat although it's not allowed!");
						
						JOptionPane.showMessageDialog(dataModel.getMainWindow(),
								jskatStrings.getString("ramsch_no_jacks"),
								jskatStrings.getString("skat_rules"),
								JOptionPane.WARNING_MESSAGE);

						gameData.setHand(false);
						skatGame.showSkat();
						waitMe();
					}

				} else {

					gameData.geschoben();
				}

			} else {			// AI player

				if (players[currPlayer].lookIntoSkat(true)) {
					
					CardVector oldSkat = new CardVector();
					oldSkat.addNew(skat.getCard(0));
					oldSkat.addNew(skat.getCard(1));

					players[currPlayer].takeRamschSkat(skat, dataModel
							.getJSkatOptions().isSchieberRamschJacksInSkat());

					if (skat.size() != 2) {

						log.error("AIPlayer is cheating! Ramsch skat has more or less than 2 cards!");

					} else if (!skatGame.getSkatTableOptions()
							.isSchieberRamschJacksInSkat()
							&& (skat.getCard(0).getRank() == SkatConstants.Ranks.JACK
							|| skat.getCard(1).getRank() == SkatConstants.Ranks.JACK)) {

						log.error("AIPlayer has put a Jack into skat although it's not allowed!");
					}

					log.debug("Updating player hand and dealt cards...");
					gameData.updatePlayerCards(currPlayer, oldSkat);
					skatGame.updateDealtCards(currPlayer);

					if(skatGame.isJSkatPlayedByHuman()) {
						JOptionPane.showMessageDialog(dataModel.getMainWindow(),
								players[currPlayer].getPlayerName() + " "
										+ jskatStrings.getString("ramsch_skat"),
								jskatStrings.getString("game_announcement"),
								JOptionPane.INFORMATION_MESSAGE);
					}

				} else {

					gameData.geschoben();

					if(skatGame.isJSkatPlayedByHuman()) {
						JOptionPane.showMessageDialog(dataModel.getMainWindow(),
								players[currPlayer].getPlayerName() + " "
										+ jskatStrings.getString("skips"),
								jskatStrings.getString("game_announcement"),
								JOptionPane.INFORMATION_MESSAGE);
					}

				}

			}

			log.debug("Cards after:    "+ gameData.getPlayerCards(currPlayer) + 
					", Skat="+gameData.getSkat());

		}

		log.debug("SchieberRamschThread STOP\n\n");

		// Start a new game with Ramsch settings
		gameData.setSinglePlayer(skatGame.getPlayerOrder()[0]);

		GameAnnouncement newGame = new GameAnnouncement();
		newGame.setGameType(SkatConstants.GameTypes.RAMSCH);
		newGame.setTrump(null);

		skatGame.playing(newGame);
	}

	/**
	 * Thread waits until an exception occurs
	 */
	private synchronized void waitMe() {

		isInterruptable = true;

		try {

			wait();

		} catch (Exception ex) {

			log.debug("THREAD: got interrupt signal");
		}
	}

	private Thread aThread;

	private SkatGame skatGame;

	private static JSkatDataModel dataModel;

	private static ResourceBundle jskatStrings;

	private JSkatPlayer[] players;

	private int[] playerOrder;

	private boolean isInterruptable;
}