/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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
package org.jskat.ai.nn;

import java.util.HashMap;
import java.util.Map;

import org.jskat.ai.IJSkatPlayer;
import org.jskat.control.JSkatThread;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.Trick;
import org.jskat.gui.human.HumanPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.BasicSkatRules;
import org.jskat.util.rule.SkatRuleFactory;

/**
 * Controls a skat game
 */
// FIXME (jansch 02.08.2011) this is nearly the same than SkatGame class
// try to make them using the same source
public class SimpleSkatGame extends JSkatThread {

	private SkatGameData data;
	private CardDeck deck;
	private Map<Player, IJSkatPlayer> player;
	private BasicSkatRules rules;

	/**
	 * Constructor
	 * 
	 * @param newTableName
	 *            Table name
	 * @param newForeHand
	 *            Fore hand player
	 * @param newMiddleHand
	 *            Middle hand player
	 * @param newRearHand
	 *            Hind hand player
	 */
	public SimpleSkatGame(IJSkatPlayer newForeHand, IJSkatPlayer newMiddleHand,
			IJSkatPlayer newRearHand) {

		player = new HashMap<Player, IJSkatPlayer>();
		player.put(Player.FOREHAND, newForeHand);
		player.put(Player.MIDDLEHAND, newMiddleHand);
		player.put(Player.REARHAND, newRearHand);

		// inform all players about the starting of the new game
		for (Player currPlayerPosition : player.keySet()) {
			player.get(currPlayerPosition).newGame(currPlayerPosition);
		}

		data = new SkatGameData();
		setGameState(GameState.GAME_START);
		data.setDeclarerPickedUpSkat(true);
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {

		playTricks();
	}

	private void playTricks() {

		for (int trickNo = 0; trickNo < 10; trickNo++) {

			Player newTrickForeHand = null;
			if (trickNo == 0) {
				// first trick
				newTrickForeHand = Player.FOREHAND;
			} else {
				// all the other tricks
				Trick lastTrick = data.getTricks().get(
						data.getTricks().size() - 1);

				// set new trick fore hand
				newTrickForeHand = lastTrick.getTrickWinner();
			}

			Trick trick = new Trick(trickNo, newTrickForeHand);
			data.addTrick(trick);
			for (Player currPosition : Player.values()) {
				// inform all players
				// cloning of trick information to prevent manipulation by
				// player
				try {
					player.get(currPosition).newTrick((Trick) trick.clone());
				} catch (CloneNotSupportedException e) {
					player.get(currPosition).newTrick(trick);
				}
			}

			// Ask players for their cards
			playCard(trick, newTrickForeHand, newTrickForeHand);
			playCard(trick, newTrickForeHand,
					newTrickForeHand.getLeftNeighbor());
			playCard(trick, newTrickForeHand,
					newTrickForeHand.getRightNeighbor());
			Player trickWinner = rules.calculateTrickWinner(data.getGameType(),
					trick);
			trick.setTrickWinner(trickWinner);
			data.addPlayerPoints(trickWinner, trick.getCardValueSum());

			for (Player currPosition : Player.values()) {
				// inform all players
				// cloning of trick information to prevent manipulation by
				// player
				try {
					player.get(currPosition).showTrick((Trick) trick.clone());
				} catch (CloneNotSupportedException e) {
					player.get(currPosition).showTrick(trick);
				}
			}

			// Check for preliminary ending of a null game
			if (GameType.NULL.equals(data.getGameType())) {

				if (trickWinner == data.getDeclarer()) {
					// declarer has won a trick
					setGameState(GameState.PRELIMINARY_GAME_END);
				}
			}

			if (isFinished()) {
				break;
			}
		}

		if (data.getGameType() == GameType.RAMSCH) {
			// TODO give the card points of the skat to a player defined in
			// ramsch rules
		} else {
			// for all the other games, points to the declarer
			data.addPlayerPoints(data.getDeclarer(), data.getSkat()
					.getCardValueSum());
		}

		// set schneider/schwarz/jungfrau/durchmarsch flags
		switch (data.getGameType()) {
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
		case GRAND:
			data.setSchneiderSchwarz();
			break;
		case RAMSCH:
			data.setJungfrauDurchmarsch();
			break;
		case NULL:
		case PASSED_IN:
			// do nothing
			break;
		}
	}

	private void playCard(Trick trick, Player trickForeHand, Player currPlayer) {

		Card card = null;
		IJSkatPlayer skatPlayer = getPlayerObject(currPlayer);

		boolean isCardAccepted = false;
		while (!isCardAccepted) {
			// ask player for the next card
			card = skatPlayer.playCard();

			if (card == null) {

			} else if (!playerHasCard(currPlayer, card)) {

			} else if (!rules
					.isCardAllowed(data.getGameType(), trick.getFirstCard(),
							data.getPlayerCards(currPlayer), card)) {

				if (!(skatPlayer instanceof HumanPlayer)) {
					// TODO create option for switching playing schwarz on/off
					isCardAccepted = true;
				}

			} else {

				isCardAccepted = true;
			}
		}

		// card was on players hand and is valid
		data.getPlayerCards(currPlayer).remove(card);
		data.setTrickCard(currPlayer, card);

		if (trick.getTrickNumberInGame() > 0
				&& currPlayer.equals(trickForeHand)) {
			// remove all cards from current trick panel first

			Trick lastTrick = data.getTricks().get(data.getTricks().size() - 2);
		}

		for (Player currPosition : Player.values()) {
			// inform all players
			// cloning of card is not neccessary, because Card is immutable
			player.get(currPosition).cardPlayed(currPlayer, card);
		}
	}

	private IJSkatPlayer getPlayerObject(Player currPlayer) {

		return player.get(currPlayer);
	}

	/**
	 * Checks whether a player has the card on it's hand or not
	 * 
	 * @param card
	 *            Card to check
	 * @return TRUE if the card is on player's hand
	 */
	private boolean playerHasCard(Player skatPlayer, Card card) {

		boolean result = false;

		for (Card handCard : data.getPlayerCards(skatPlayer)) {

			if (handCard.equals(card)) {

				result = true;
			}
		}

		return result;
	}

	private boolean isFinished() {

		return data.getGameState() == GameState.PRELIMINARY_GAME_END
				|| data.getGameState() == GameState.GAME_OVER;
	}

	/**
	 * Sets the cards from outside
	 * 
	 * @param newDeck
	 *            Card deck
	 */
	public void setCardDeck(CardDeck newDeck) {

		deck = newDeck;
	}

	/**
	 * Sets the game announcement from the outside
	 * 
	 * @param ann
	 *            Game announcement
	 */
	public void setGameAnnouncement(GameAnnouncement ann) {

		data.setAnnouncement(ann);
		rules = SkatRuleFactory.getSkatRules(data.getGameType());

		// inform all players
		for (IJSkatPlayer currPlayer : player.values()) {

			currPlayer.startGame(data.getDeclarer(), ann);
		}
	}

	/**
	 * Sets the game state from outside
	 * 
	 * @param newState
	 *            Game state
	 */
	public void setGameState(GameState newState) {

		data.setGameState(newState);
	}

	/**
	 * Sets the single player from outside
	 * 
	 * @param singlePlayer
	 *            Single player
	 */
	public void setSinglePlayer(Player singlePlayer) {

		data.setDeclarer(singlePlayer);
	}

	/**
	 * Gets whether a game was won or not
	 * 
	 * @return TRUE if the game was won
	 */
	public boolean isGameWon() {

		return data.isGameWon();
	}

	/**
	 * Gets the game result
	 * 
	 * @return Game result
	 */
	public int getGameResult() {

		return data.getGameResult().getGameValue();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return data.getGameState().toString();
	}

	/**
	 * Deals the cards to the players and the skat
	 */
	public void dealCards() {

		for (int i = 0; i < 3; i++) {
			// deal three rounds of cards
			switch (i) {
			case 0:
				// deal three cards
				dealCards(3);
				// and put two cards into the skat
				data.setDealtSkatCards(deck.remove(0), deck.remove(0));
				break;
			case 1:
				// deal four cards
				dealCards(4);
				break;
			case 2:
				// deal three cards
				dealCards(3);
				break;
			}
		}

		// show cards in the view
		Map<Player, CardList> dealtCards = data.getDealtCards();
	}

	/**
	 * Deals the cards to the players
	 * 
	 * @param deck
	 *            Card deck
	 * @param cardCount
	 *            Number of cards to be dealt to a player
	 */
	private void dealCards(int cardCount) {

		for (Player hand : Player.values()) {
			// for all players
			for (int j = 0; j < cardCount; j++) {
				// deal amount of cards
				Card card = deck.remove(0);
				// player can get original card object because Card is immutable
				player.get(hand).takeCard(card);
				data.setDealtCard(hand, card);
			}
		}
	}

	public void calculateGameValue() {

		// FIXME (jan 07.12.2010) don't let a data class calculate it's values
		data.calcResult();

		for (IJSkatPlayer currPlayer : player.values()) {
			currPlayer.setGameResult(data.getGameResult().clone());
			currPlayer.finalizeGame();
		}
	}
}
