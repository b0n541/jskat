/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
 * Copyright (C) 2013-11-02
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.human;

import org.jskat.data.GameAnnouncement;
import org.jskat.gui.action.JSkatAction;
import org.jskat.gui.action.JSkatActionEvent;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Human player
 */
public class SwingHumanPlayer extends AbstractHumanJSkatPlayer {

	private static Logger log = LoggerFactory.getLogger(SwingHumanPlayer.class);

	private Idler idler = new Idler();

	private boolean holdBid;
	private int bidValue;
	private GameAnnouncementStep gameAnnouncementStep;
	private boolean playGrandHand;
	private boolean pickUpSkat;
	private CardList discardSkat;
	private GameAnnouncement gameAnnouncement;
	private Card nextCard;

	private enum GameAnnouncementStep {
		BEFORE_ANNOUNCEMENT, LOOKED_INTO_SKAT, DISCARDED_SKAT, PLAYS_HAND, DONE_GAME_ANNOUNCEMENT
	}

	/**
	 * @see JSkatPlayer#announceGame()
	 */
	@Override
	public GameAnnouncement announceGame() {

		log.debug("Waiting for human game announcing..."); //$NON-NLS-1$

		waitForUserInput();

		gameAnnouncementStep = GameAnnouncementStep.DONE_GAME_ANNOUNCEMENT;

		return this.gameAnnouncement;
	}

	/**
	 * @see JSkatPlayer#bidMore(int)
	 */
	@Override
	public Integer bidMore(final int nextBidValue) {

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
	 * @see JSkatPlayer#discardSkat()
	 */
	@Override
	public CardList getCardsToDiscard() {

		log.debug("Waiting for human discarding..."); //$NON-NLS-1$

		waitForUserInput();

		return this.discardSkat;
	}

	/**
	 * @see JSkatPlayer#preparateForNewGame()
	 */
	@Override
	public void preparateForNewGame() {

		resetPlayer();
	}

	/**
	 * @see JSkatPlayer#finalizeGame()
	 */
	@Override
	public void finalizeGame() {
		// TODO implement it
	}

	/**
	 * @see JSkatPlayer#holdBid(int)
	 */
	@Override
	public Boolean holdBid(final int currBidValue) {

		log.debug("Waiting for human holding bid..."); //$NON-NLS-1$

		waitForUserInput();

		return this.holdBid;
	}

	/**
	 * @see JSkatPlayer#pickUpSkat()
	 */
	@Override
	public Boolean playGrandHand() {

		log.debug("Waiting for human to decide if playing a grand hand..."); //$NON-NLS-1$

		waitForUserInput();

		return this.playGrandHand;
	}

	/**
	 * @see JSkatPlayer#pickUpSkat()
	 */
	@Override
	public Boolean pickUpSkat() {

		log.debug("Waiting for human looking into skat..."); //$NON-NLS-1$

		waitForUserInput();

		return this.pickUpSkat;
	}

	/**
	 * @see JSkatPlayer#playCard()
	 */
	@Override
	public Card playCard() {

		log.debug("Waiting for human playing next card..."); //$NON-NLS-1$

		waitForUserInput();

		return this.nextCard;
	}

	@Override
	public void actionPerformed(final JSkatActionEvent e) {
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
		} else if (JSkatAction.PLAY_GRAND_HAND.toString().equals(command)) {
			// player wants to play a grand hand
			this.playGrandHand = true;
		} else if (JSkatAction.PICK_UP_SKAT.toString().equals(command)) {

			// player wants to pick up the skat
			this.pickUpSkat = true;
			gameAnnouncementStep = GameAnnouncementStep.LOOKED_INTO_SKAT;

		} else if (JSkatAction.SCHIEBEN.toString().equals(command)) {

			if (source instanceof CardList) {
				CardList cards = (CardList) source;
				if (cards.size() == 2) {
					setDiscardedSkatCards((CardList) source);
				}
			}
		} else if (JSkatAction.ANNOUNCE_GAME.toString().equals(command)) {

			if (source instanceof GameAnnouncement) {
				// player did game announcement
				gameAnnouncement = (GameAnnouncement) source;

				if (gameAnnouncement.isHand()) {
					gameAnnouncementStep = GameAnnouncementStep.PLAYS_HAND;
				} else {
					setDiscardedSkatCards(gameAnnouncement.getDiscardedCards());
					gameAnnouncementStep = GameAnnouncementStep.DISCARDED_SKAT;
				}
			} else {

				log.warn("Wrong source for " + command); //$NON-NLS-1$
				interrupt = false;
			}
		} else if (JSkatAction.PLAY_CARD.toString().equals(command)
				&& source instanceof Card) {

			this.nextCard = (Card) source;

		} else {

			log.warn("Unknown action event occured: " + command + " from " + source); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (interrupt) {

			this.idler.interrupt();
		}
	}

	/**
	 * Starts waiting for user input
	 */
	public void waitForUserInput() {

		this.idler = new Idler();
		this.idler.setMonitor(this);

		if (!isPlayerHasAlreadyPlayed()) {

			this.idler.start();
			try {
				this.idler.join();
			} catch (InterruptedException e) {
				log.warn("wait for user input was interrupted");
			}
		}
	}

	private boolean isPlayerHasAlreadyPlayed() {

		log.debug("Game announcement step: " + gameAnnouncementStep); //$NON-NLS-1$

		boolean result = false;

		if (GameAnnouncementStep.DISCARDED_SKAT.equals(gameAnnouncementStep)
				|| GameAnnouncementStep.PLAYS_HAND.equals(gameAnnouncementStep)) {
			result = true;
		}

		return result;
	}

	private void setDiscardedSkatCards(final CardList discardedCards) {
		discardSkat = discardedCards;
	}

	/*-------------------------------------------------------------------
	 * Inner class
	 *-------------------------------------------------------------------*/

	/**
	 * Protected class implementing the waiting thread for user input
	 */
	protected static class Idler extends Thread {

		/**
		 * Sets the monitoring object
		 * 
		 * @param newMonitor
		 *            Monitor
		 */
		public void setMonitor(final Object newMonitor) {

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
						stopWaiting();
					}
				}
			}
		}

		private boolean doWait = true;
		private Object monitor = null;
	}

	/**
	 * @see org.jskat.player.AbstractJSkatPlayer#startGame()
	 */
	@Override
	public void startGame() {
		// TODO is there something todo?
	}

	private void resetPlayer() {
		bidValue = 0;
		holdBid = false;
		playGrandHand = false;
		gameAnnouncementStep = GameAnnouncementStep.BEFORE_ANNOUNCEMENT;
		pickUpSkat = false;
		discardSkat = null;
		gameAnnouncement = null;
		nextCard = null;
	}

	@Override
	public Boolean callContra() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean callRe() {
		// TODO Auto-generated method stub
		return false;
	}
}
