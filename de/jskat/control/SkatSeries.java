/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.JSkatPlayer;
import de.jskat.data.SkatSeriesData;
import de.jskat.data.SkatSeriesData.SeriesStates;
import de.jskat.gui.JSkatView;
import de.jskat.gui.human.HumanPlayer;
import de.jskat.util.Player;

/**
 * Controls a series of skat games
 */
public class SkatSeries extends JSkatThread {

	private static Log log = LogFactory.getLog(SkatSeries.class);

	private int maxSleep = 0;
	private SkatSeriesData data;
	private int roundsToGo = 0;
	private List<JSkatPlayer> player;
	private List<Player> viewPositions;
	private SkatGame currSkatGame;

	private JSkatView view;

	/**
	 * Constructor
	 * 
	 * @param tableName
	 *            Table name
	 */
	public SkatSeries(String tableName) {

		this.data = new SkatSeriesData();
		this.data.setState(SeriesStates.WAITING);
		this.data.setTableName(tableName);
		this.player = new ArrayList<JSkatPlayer>();
		this.viewPositions = new ArrayList<Player>();
		this.viewPositions.add(Player.FORE_HAND);
		this.viewPositions.add(Player.MIDDLE_HAND);
		this.viewPositions.add(Player.HIND_HAND);
	}

	/**
	 * Sets the skat players
	 * 
	 * @param newPlayer
	 *            New skat series player
	 */
	public void setPlayer(List<JSkatPlayer> newPlayer) {

		for (JSkatPlayer currPlayer : newPlayer) {

			this.player.add(currPlayer);
		}

		log.debug("Player order: " + this.player); //$NON-NLS-1$
	}

	/**
	 * Checks whether a series is running
	 * 
	 * @return TRUE if the series is running
	 */
	public boolean isRunning() {

		return this.data.getState() == SeriesStates.RUNNING;
	}

	/**
	 * Starts the series
	 * 
	 * @param rounds
	 *            Number of rounds to be played
	 */
	public void startSeries(int rounds) {

		this.roundsToGo = rounds;
		this.data.setState(SeriesStates.RUNNING);
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {

		int roundsPlayed = 0;

		while (this.roundsToGo > 0) {

			log.debug("Playing round " + (roundsPlayed + 1)); //$NON-NLS-1$

			for (int j = 0; j < 3; j++) {

				if (j > 0 || roundsPlayed > 0) {
					// change player positions after first game
					JSkatPlayer helper = this.player.get(2);
					this.player.set(2, this.player.get(0));
					this.player.set(0, this.player.get(1));
					this.player.set(1, helper);
					// change view positions too
					Player helper2 = this.viewPositions.get(2);
					this.viewPositions.set(2, this.viewPositions.get(1));
					this.viewPositions.set(1, this.viewPositions.get(0));
					this.viewPositions.set(0, helper2);
				}

				this.currSkatGame = new SkatGame(this.data.getTableName(),
						this.player.get(0), this.player.get(1), this.player
								.get(2));
				this.view.setPositions(this.data.getTableName(),
						this.viewPositions.get(0), this.viewPositions.get(1),
						this.viewPositions.get(2));
				this.currSkatGame.setView(this.view);

				log.debug("Playing game " + (j + 1)); //$NON-NLS-1$

				this.data.addGame(this.currSkatGame);
				this.currSkatGame.start();
				try {
					this.currSkatGame.join();

					log.debug("Game ended: join"); //$NON-NLS-1$

					sleep(this.maxSleep);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (isHumanPlayerInvolved()) {
					// wait for human to start next game
					startWaiting();
				}

				checkWaitCondition();
			}

			this.roundsToGo--;
			roundsPlayed++;

			checkWaitCondition();
		}

		this.data.setState(SeriesStates.SERIES_FINISHED);
		log.debug(this.data.getState());
	}

	private boolean isHumanPlayerInvolved() {

		boolean result = false;

		for (JSkatPlayer currPlayer : this.player) {

			if (currPlayer instanceof HumanPlayer) {

				result = true;
			}
		}

		return result;
	}

	/**
	 * Gets the state of the series
	 * 
	 * @return State of the series
	 */
	public SeriesStates getSeriesState() {

		return this.data.getState();
	}

	/**
	 * Gets the ID of the current game
	 * 
	 * @return ID of the current game
	 */
	public int getCurrentGameID() {

		return this.data.getCurrentGameID();
	}

	/**
	 * Pauses the current game
	 */
	public void pauseSkatGame() {

		synchronized (this.currSkatGame) {

			this.currSkatGame.startWaiting();
		}
	}

	/**
	 * Resumes the current game
	 */
	public void resumeSkatGame() {

		synchronized (this.currSkatGame) {

			this.currSkatGame.stopWaiting();
			this.currSkatGame.notify();
		}
	}

	/**
	 * Checks whether the current skat game is paused
	 * 
	 * @return TRUE if the current skat game is paused
	 */
	public boolean isSkatGameWaiting() {

		return this.currSkatGame.isWaiting();
	}

	/**
	 * Sets the view for the series
	 * 
	 * @param newView
	 *            View
	 */
	public void setView(JSkatView newView) {

		this.view = newView;
	}
}
