/**
 * Copyright (C) 2016 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.player;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameSummary;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;

/**
 * Player interface for JSkat players
 */
public interface JSkatPlayer {

	/**
	 * This function can be used to do some clean up
	 */
	public void preparateForNewGame();

	/**
	 * This function can be used to do some finalizing calculations on the
	 * finished game
	 */
	public void finalizeGame();

	/**
	 * Informs the player about the start of a new game
	 *
	 * @param position
	 *            Initial sitting position of the player
	 */
	public void newGame(Player position);

	/**
	 * Notifies the player of the start of bidding for a new game
	 */
	public void setUpBidding();

	/**
	 * Asks the player whether it wants to bid higher or not
	 *
	 * @param nextBidValue
	 *            Next bid value
	 * @return A bid value equal or higher as the next bid value -1 if the
	 *         player passes
	 */
	public Integer bidMore(int nextBidValue);

	/**
	 * Asks the player whether it wants to hold a bid from the announcer
	 *
	 * @param currBidValue
	 *            Current bid value
	 * @return TRUE if the player holds the bid
	 */
	public Boolean holdBid(int currBidValue);

	/**
	 * Informs the player about a bid value that was announced or hold
	 *
	 * @param player
	 *            Player who announced or hold the bid
	 * @param bidValue
	 *            Bid value
	 */
	public void bidByPlayer(Player player, int bidValue);

	/**
	 * Takes cards from the dealer
	 *
	 * @param cards
	 *            New cards
	 */
	public void takeCards(CardList cards);

	/**
	 * @return <code>true</code>, if the player wants to play a grand hand in a
	 *         BockRamsch game
	 */
	public Boolean playGrandHand();

	/**
	 * Asks the player to call Contra as opponent player
	 *
	 * @return <code>true</code>, if the player wants to call Contra
	 */
	public Boolean callContra();

	/**
	 * Asks the player to call Re a declarer player, after an opponent did call
	 * Contra
	 *
	 * @return <code>true</code>, if the player wants to call Re
	 */
	public Boolean callRe();

	/**
	 * Checks whether the player wants to look into the skat
	 *
	 * @return TRUE if the player wants to look into the skat
	 */
	public Boolean pickUpSkat();

	/**
	 * Take the skat as a single player.<br>
	 * When the method is done, the skat CardList must still contain exactly two
	 * cards. Otherwise a SkatHandlingException might be thrown.
	 *
	 * @param skat
	 *            The skat Cards
	 */
	public void takeSkat(CardList skat);

	/**
	 * Asks the player for the game he wants to play
	 *
	 * @return Game announcement
	 */
	public GameAnnouncement announceGame();

	/**
	 * Start the game: inform player of game type, trumpf and special options
	 *
	 * @param singlePlayer
	 *            Single player position
	 * @param game
	 *            Game announcement containing all relevant information about
	 *            the new game
	 */
	// FIXME (jan 17.01.2011) change game announcement parameter to immutable
	// GameAnnouncement
	public void startGame(Player singlePlayer, GameAnnouncement game);

	/**
	 * Shows the cards of the single player to the opponents in ouvert games
	 *
	 * @param ouvertCards
	 *            Cards of the single player in an ouver game
	 */
	public void lookAtOuvertCards(CardList ouvertCards);

	/**
	 * Get next Card to play
	 *
	 * @return Card to be played
	 */
	public Card playCard();

	/**
	 * Informs the player about a card that was played
	 *
	 * @param player
	 *            Player who played the card
	 * @param card
	 *            Card that was played
	 */
	public void cardPlayed(Player player, Card card);

	/**
	 * Makes the current trick known to the players when it is initiated
	 *
	 * @param trickNo
	 *            Trick no in game
	 * @param trickForehand
	 *            Forehand for the trick
	 */
	public void newTrick(int trickNo, Player trickForehand);

	/**
	 * Makes the current trick known to the players when it is complete
	 *
	 * @param trick
	 *            Trick information
	 */
	public void showTrick(Trick trick);

	/**
	 * Gets the name of the skat player
	 *
	 * @return Player name
	 */
	public String getPlayerName();

	/**
	 * Sets the name of the skat player
	 *
	 * @param newPlayerName
	 *            Player name to be set
	 */
	public void setPlayerName(String newPlayerName);

	/**
	 * Checks whether the player is a human player
	 *
	 * @return TRUE if the player is a human player
	 */
	public Boolean isHumanPlayer();

	/**
	 * Checks whether the player is an AI player
	 *
	 * @return TRUE if the player is an AI player
	 */
	public Boolean isAIPlayer();

	/**
	 * Checks whether the player is the declaring player
	 *
	 * @return TRUE if the player is the declaring player
	 */
	public Boolean isDeclarer();

	/**
	 * Asks for the new skat cards during discarding
	 *
	 * @return CardList The new cards for the skat
	 */
	public CardList discardSkat();

	/**
	 * Informs the player about the game
	 *
	 * @param gameSummary
	 *            Game summary
	 */
	public void setGameSummary(GameSummary gameSummary);

	/**
	 * Holds all player states
	 */
	public enum PlayerState {
		/**
		 * Player waits
		 */
		WAITING,
		/**
		 * Player receives cards during dealing
		 */
		DEALING,
		/**
		 * Player takes part in bidding
		 */
		BIDDING,
		/**
		 * Player looks into skat and discards two cards
		 */
		DISCARDING,
		/**
		 * Player annouces a game
		 */
		GAME_ANNOUNCING,
		/**
		 * Player plays the tricks
		 */
		PLAYING
	}
}