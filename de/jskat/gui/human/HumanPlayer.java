/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.gui.human;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.AbstractJSkatPlayer;
import de.jskat.ai.IJSkatPlayer;
import de.jskat.data.GameAnnouncement;
import de.jskat.gui.action.JSkatAction;
import de.jskat.util.Card;
import de.jskat.util.CardList;

/**
 * Human player
 */
public class HumanPlayer extends AbstractJSkatPlayer implements ActionListener {

	private static Log log = LogFactory.getLog(HumanPlayer.class);

	private Idler idler = new Idler();

	private boolean holdBid;
	private int bidValue;
	private boolean lookIntoSkat;
	private CardList discardSkat;
	private GameAnnouncement gameAnnouncement;
	private Card nextCard;

	/**
	 * @see IJSkatPlayer#isAIPlayer()
	 */
	@Override
	public boolean isAIPlayer() {

		return false;
	}

	/**
	 * @see IJSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {

		log.debug("Waiting for human game announcing..."); //$NON-NLS-1$

		waitForUserInput();

		return this.gameAnnouncement;
	}

	/**
	 * @see IJSkatPlayer#bidMore(int)
	 */
	@Override
	public int bidMore(int nextBidValue) {

		log.debug("Waiting for human next bid value..."); //$NON-NLS-1$

		waitForUserInput();

		if (this.holdBid) {

			this.bidValue = nextBidValue;
		} else {

			this.bidValue = -1;
		}

		return this.bidValue;
	}

	/**
	 * @see IJSkatPlayer#discardSkat()
	 */
	@Override
	public CardList discardSkat() {

		log.debug("Waiting for human discarding..."); //$NON-NLS-1$

		waitForUserInput();

		return this.discardSkat;
	}

	/**
	 * @see IJSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {

		this.holdBid = false;
		this.bidValue = 0;
		this.lookIntoSkat = false;
		this.discardSkat = null;
		this.gameAnnouncement = null;
		this.nextCard = null;
	}

	/**
	 * @see IJSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {
		// TODO implement it
	}

	/**
	 * @see IJSkatPlayer#holdBid(int)
	 */
	@Override
	public boolean holdBid(int currBidValue) {

		log.debug("Waiting for human holding bid..."); //$NON-NLS-1$

		waitForUserInput();

		return this.holdBid;
	}

	/**
	 * @see IJSkatPlayer#lookIntoSkat()
	 */
	@Override
	public boolean lookIntoSkat() {

		log.debug("Waiting for human looking into skat..."); //$NON-NLS-1$

		waitForUserInput();

		return this.lookIntoSkat;
	}

	/**
	 * @see IJSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {

		log.debug("Waiting for human playing next card..."); //$NON-NLS-1$

		waitForUserInput();

		return this.nextCard;
	}

	/**
	 * Starts waiting for user input
	 */
	public void waitForUserInput() {

		this.idler = new Idler();
		this.idler.setMonitor(this);

		this.idler.start();
		try {
			this.idler.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		String command = e.getActionCommand();
		boolean interrupt = true;

		if (JSkatAction.PASS_BID.toString().equals(command)) {
			// player passed
			this.holdBid = false;
		} else if (JSkatAction.MAKE_BID.toString().equals(command)) {
			// player hold bid
			this.holdBid = true;
		} else if (JSkatAction.HOLD_BID.toString().equals(command)) {
			// player hold bid
			this.holdBid = true;
		} else if (JSkatAction.LOOK_INTO_SKAT.toString().equals(command)) {
			// player wants to look into the skat
			this.lookIntoSkat = true;
		} else if (JSkatAction.PLAY_HAND_GAME.toString().equals(command)) {
			// player wants to play a hand game
			this.lookIntoSkat = false;
		} else if (JSkatAction.DISCARD_CARDS.toString().equals(command)) {

			if (source instanceof CardList) {
				// player discarded cards
				this.discardSkat = (CardList) source;

				this.cards.remove(this.discardSkat.get(0));
				this.cards.remove(this.discardSkat.get(1));
			} else {

				log.error("Wrong source for " + command); //$NON-NLS-1$
				interrupt = false;
			}
		} else if (JSkatAction.ANNOUNCE_GAME.toString().equals(command)) {

			if (source instanceof JButton) {
				log.debug("ONLY JBUTTON"); //$NON-NLS-1$
				interrupt = false;
			} else {
				// player did game announcement
				this.gameAnnouncement = (GameAnnouncement) source;
			}
		} else if (JSkatAction.PLAY_CARD.toString().equals(command)
				&& source instanceof Card) {

			this.nextCard = (Card) source;

		} else {

			log.error("Unknown action event occured: " + command + " from " + source); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (interrupt) {

			this.idler.interrupt();
		}
	}

	/*-------------------------------------------------------------------
	 * Inner class
	 *-------------------------------------------------------------------*/

	/**
	 * Protected class implementing the waiting thread for user input
	 */
	protected class Idler extends Thread {

		/**
		 * Sets the monitoring object
		 * 
		 * @param newMonitor
		 *            Monitor
		 */
		public void setMonitor(Object newMonitor) {

			this.monitor = newMonitor;
		}

		/**
		 * Stops the waiting
		 */
		public void stopWaiting() {

			this.doWait = false;
		}

		/**
		 * @see Thread#run()
		 */
		@Override
		public void run() {

			synchronized (this.monitor) {

				while (this.doWait) {
					try {
						this.monitor.wait();
					} catch (InterruptedException e) {
						this.doWait = false;
					}
				}
			}
		}

		private boolean doWait = true;
		private Object monitor = null;
	}

	/**
	 * @see de.jskat.ai.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		// TODO implement it
	}
}
