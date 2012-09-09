/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary.GameSummaryFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.RamschRule;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data class for a Skat game
 */
public class SkatGameData {

	private static Logger log = LoggerFactory.getLogger(SkatGameData.class);

	/**
	 * All possible game states
	 */
	public enum GameState {

		/**
		 * New game started
		 */
		GAME_START,
		/**
		 * Dealing phase
		 */
		DEALING,
		/**
		 * Bidding phase
		 */
		BIDDING,
		/**
		 * Grand hand announcement instead of an ramsch game
		 */
		RAMSCH_GRAND_HAND_ANNOUNCING,
		/**
		 * Schieberamsch
		 */
		SCHIEBERAMSCH,
		/**
		 * Look into skat or play hand game phase
		 */
		PICKING_UP_SKAT,
		/**
		 * Discarding phase
		 */
		DISCARDING,
		/**
		 * Declaring phase
		 */
		DECLARING,
		/**
		 * Trick playing phase
		 */
		TRICK_PLAYING,
		/**
		 * Preliminary game end
		 */
		PRELIMINARY_GAME_END,
		/**
		 * Game value calculation phase
		 */
		CALCULATING_GAME_VALUE,
		/**
		 * Game over
		 */
		GAME_OVER;
	}

	private GameState gameState;
	/**
	 * Flag for the Skat rules
	 */
	private boolean ispaRules = true;

	/**
	 * Skat rules according the game type
	 */
	private SkatRule rules;

	/**
	 * The game announcement made by the declarer
	 */
	private GameAnnouncement announcement;

	/**
	 * Declarer player
	 */
	private Player declarer;

	/**
	 * Dealer of the cards
	 */
	private Player dealer;

	/**
	 * Active player to make the next move
	 */
	private Player activePlayer;

	/**
	 * Points the player made during the game
	 */
	private Map<Player, Integer> playerPoints;

	/**
	 * Game result
	 */
	private SkatGameResult result;

	/**
	 * Highest bid value made during bidding
	 */
	private int highestBidValue = -1;

	/**
	 * Player names
	 */
	private Map<Player, String> playerNames;

	/**
	 * Bids the players made during bidding
	 */
	private Map<Player, Integer> playerBids;

	/**
	 * Passes the players made during bidding
	 */
	private Map<Player, Boolean> playerPasses;

	/**
	 * Flag for a geschoben game (the skat was handed over from player to player
	 * at the beginning of a ramsch game)
	 */
	private int geschoben = 0;

	/**
	 * Tricks made in the game
	 */
	private List<Trick> tricks;

	/**
	 * Cards on player hands
	 */
	private Map<Player, CardList> playerHands;

	/**
	 * Cards in the skat
	 */
	private CardList skat;

	/**
	 * Holds all cards dealt to the players
	 */
	private Map<Player, CardList> dealtCards;

	/**
	 * Holds all cards dealt to skat
	 */
	private CardList dealtSkat;

	private boolean declarerPickedUpSkat;

	/**
	 * Creates a new instance of a Skat game data
	 */
	public SkatGameData() {

		intializeVariables();

		log.debug("Game created"); //$NON-NLS-1$
	}

	private void intializeVariables() {

		announcement = GameAnnouncement.getFactory().getEmptyAnnouncement();
		result = new SkatGameResult();

		playerNames = new HashMap<Player, String>();
		playerHands = new HashMap<Player, CardList>();
		dealtCards = new HashMap<Player, CardList>();
		playerPoints = new HashMap<Player, Integer>();
		playerBids = new HashMap<Player, Integer>();
		playerPasses = new HashMap<Player, Boolean>();

		for (final Player player : Player.values()) {
			playerNames.put(player, ""); //$NON-NLS-1$
			playerHands.put(player, new CardList());
			dealtCards.put(player, new CardList());
			playerPoints.put(player, Integer.valueOf(0));
			playerBids.put(player, Integer.valueOf(0));
			playerPasses.put(player, Boolean.FALSE);
		}

		dealtSkat = new CardList();
		skat = new CardList();

		tricks = new ArrayList<Trick>();
	}

	/**
	 * Returns the result of the game
	 * 
	 * @return Value of the game
	 */
	public SkatGameResult getGameResult() {

		if (result.getGameValue() == -1 && getGameType() != GameType.PASSED_IN) {

			log.warn("Game result hasn't been calculated yet!"); //$NON-NLS-1$
			calcResult();
		}

		return result;
	}

	/**
	 * Returns the single player of the game
	 * 
	 * @return Player ID of the single player
	 */
	public Player getDeclarer() {

		return declarer;
	}

	/**
	 * Set the single player of the game
	 * 
	 * @param singlePlayer
	 *            Player ID of the single player
	 */
	public void setDeclarer(final Player singlePlayer) {

		log.debug("Current single Player " + singlePlayer); //$NON-NLS-1$

		declarer = singlePlayer;
	}

	/**
	 * Returns the highest bid value of the game
	 * 
	 * @return Highest bid value
	 */
	public int getBidValue() {

		return highestBidValue;
	}

	/**
	 * Sets the highest bid value of the game
	 * 
	 * @param value
	 *            Highest bid value
	 */
	public void setBidValue(final int value) {

		highestBidValue = value;
		log.debug("setBidValue(" + value + ")"); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Gets whether the game was lost or not
	 * 
	 * @return TRUE if the game was lost
	 */
	public boolean isGameLost() {

		return !result.isWon();
	}

	/**
	 * Gets whether a game was won or not
	 * 
	 * @return TRUE if the game was won
	 */
	public boolean isGameWon() {

		return result.isWon();
	}

	/**
	 * Checks whether the single player overbidded
	 * 
	 * @return TRUE if the single player overbidded
	 */
	public boolean isOverBidded() {

		// TODO This should not be possible when a Ramsch game is played
		// maybe throw an exception instead?
		if (getGameType() == GameType.RAMSCH) {
			log.warn("Overbidding cannot happen in Ramsch games: gameType=" //$NON-NLS-1$
					+ getGameType());
		}
		return result.isOverBidded();
	}

	/**
	 * Checks whether the single player has played a hand game
	 * 
	 * @return TRUE, if the single player has played a hand game
	 */
	public boolean isHand() {

		return announcement.isHand();
	}

	/**
	 * Checks whether the single player has played an ouvert
	 * 
	 * @return TRUE if the single player has played an ouvert game
	 */
	public boolean isOuvert() {

		return announcement.isOuvert();
	}

	/**
	 * Checks whether one party played schneider
	 * 
	 * @return TRUE if the single player or the opponents played schneider
	 */
	public boolean isSchneider() {

		return result.isSchneider();
	}

	/**
	 * Checks whether schneider was announced
	 * 
	 * @return TRUE if Schneider was announced
	 */
	public boolean isSchneiderAnnounced() {

		return announcement.isSchneider();
	}

	/**
	 * Checks whether schwarz was played
	 * 
	 * @return TRUE if the player or the opponents played schwarz
	 */
	public boolean isSchwarz() {

		return result.isSchwarz();
	}

	/**
	 * Checks whether schwarz was announced
	 * 
	 * @return TRUE if schwarz was announced
	 */
	public boolean isSchwarzAnnounced() {

		return announcement.isSchwarz();
	}

	/**
	 * Checks whether a durchmarsch was done in a ramsch game or not
	 * 
	 * @return TRUE if someone did a durchmarsch in a ramsch game
	 */
	public boolean isDurchmarsch() {

		return result.isDurchmarsch();
	}

	/**
	 * Checks whether someone was jungfrau in a ramsch game
	 * 
	 * @return TRUE if someone was jungfrau in a ramsch game
	 */
	public boolean isJungfrau() {

		return result.isJungfrau();
	}

	/**
	 * Gets the score of a player
	 * 
	 * @param player
	 *            The ID of a player
	 * @return The score of a player
	 */
	public int getScore(final Player player) {

		return playerPoints.get(player).intValue();
	}

	/**
	 * Overwrites the declarer score
	 * 
	 * @param newScore
	 *            New score
	 */
	public void setDeclarerScore(final int newScore) {

		playerPoints.put(declarer, Integer.valueOf(newScore));
		if (newScore > 89) {
			result.setSchneider(true);
		}
		if (newScore == 120) {
			result.setSchwarz(true);
		}
	}

	/**
	 * Gets the score of the single player
	 * 
	 * @return The score of the single player
	 */
	public int getDeclarerScore() {

		int score = 0;

		if (declarer != null) {

			score = getScore(declarer);
		}

		return score;
	}

	/**
	 * Gets the score of the opponent players
	 * 
	 * @return Score The score of the opponent players
	 */
	public int getOpponentScore() {

		int score = 0;

		if (declarer != null) {

			score = 120 - getScore(declarer);
		}

		return score;
	}

	/**
	 * Calculates the result of a game
	 */
	public void calcResult() {

		if (getGameType() == GameType.PASSED_IN) {

			result.setWon(false);
			result.setGameValue(0);
		} else {

			if (!result.isWon()) {
				// game could be won already, because of playing schwarz of an
				// opponent
				result.setWon(rules.isGameWon(this));
			}
			result.setGameValue(rules.calcGameResult(this));

			if (rules.isOverbid(this)) {
				result.setOverBidded(true);
			}
		}

		if (GameType.CLUBS.equals(announcement.gameType)
				|| GameType.SPADES.equals(announcement.gameType)
				|| GameType.HEARTS.equals(announcement.gameType)
				|| GameType.DIAMONDS.equals(announcement.gameType)
				|| GameType.GRAND.equals(announcement.gameType)) {

			result.setFinalDeclarerPoints(getDeclarerScore());
			result.setFinalOpponentPoints(getOpponentScore());
			result.setMultiplier(rules.getMultiplier(this));
			result.setPlayWithJacks(rules.isPlayWithJacks(this));
		} else if (GameType.RAMSCH.equals(announcement.gameType)) {
			finishRamschGame();
		}
	}

	/**
	 * Calculates final results of a ramsch game
	 */
	public void finishRamschGame() {

		Player ramschLoser;
		result.setWon(false);

		// FIXME this is rule logic --> remove it from data object!!!
		if (playerPoints.get(Player.FOREHAND).intValue() > playerPoints.get(
				Player.MIDDLEHAND).intValue()) {

			if (playerPoints.get(Player.FOREHAND).intValue() > playerPoints
					.get(Player.REARHAND).intValue()) {
				ramschLoser = Player.FOREHAND;
			} else {
				if (playerPoints.get(Player.FOREHAND).intValue() == playerPoints
						.get(Player.REARHAND).intValue()) {
					ramschLoser = Player.MIDDLEHAND;
					result.setWon(true);
				} else {
					ramschLoser = Player.REARHAND;
				}
			}

		} else {

			if (playerPoints.get(Player.MIDDLEHAND).intValue() > playerPoints
					.get(Player.REARHAND).intValue()) {
				ramschLoser = Player.MIDDLEHAND;
			} else {
				if (playerPoints.get(Player.MIDDLEHAND).intValue() == playerPoints
						.get(Player.REARHAND).intValue()) {
					ramschLoser = Player.FOREHAND;
					result.setWon(true);
				} else {
					ramschLoser = Player.REARHAND;
				}
			}
		}
		setDeclarer(ramschLoser);

		result.setFinalDeclarerPoints(playerPoints.get(ramschLoser));
		result.setFinalOpponentPoints(playerPoints.get(ramschLoser
				.getLeftNeighbor())
				+ playerPoints.get(ramschLoser.getRightNeighbor()));

		if (isDurchmarsch()) {
			result.setWon(true);
		}

	}

	/**
	 * Gets the result of a game
	 * 
	 * @return The result of a game
	 */
	public SkatGameResult getResult() {

		return result;
	}

	/**
	 * Sets the game result
	 * 
	 * @param newResult
	 *            Game result
	 */
	public void setResult(final SkatGameResult newResult) {

		result = newResult;
	}

	/**
	 * Adds a trick
	 * 
	 * @param newTrick
	 *            New trick
	 */
	public void addTrick(final Trick newTrick) {

		tricks.add(newTrick);
	}

	/**
	 * Sets a trick card
	 * 
	 * @param player
	 *            The player of the card in the trick
	 * @param card
	 *            The ID of the card that was played
	 */
	public void setTrickCard(final Player player, final Card card) {

		log.debug(this + ".setTrickCard(" + player + ", " //$NON-NLS-1$ //$NON-NLS-2$
				+ card + ")"); //$NON-NLS-1$

		final Trick currentTrick = getCurrentTrick();
		final Player trickForeHand = currentTrick.getForeHand();

		if (trickForeHand.equals(player)) {
			currentTrick.setFirstCard(card);
		} else if (trickForeHand.getLeftNeighbor().equals(player)) {
			currentTrick.setSecondCard(card);
		} else if (trickForeHand.getRightNeighbor().equals(player)) {
			currentTrick.setThirdCard(card);
		}
	}

	/**
	 * Gets the current trick
	 * 
	 * @return Current trick
	 */
	public Trick getCurrentTrick() {

		return tricks.get(tricks.size() - 1);
	}

	/**
	 * Gets the last trick
	 * 
	 * @return Last trick, if at least two tricks are available<br>
	 *         NULL otherwise
	 */
	public Trick getLastTrick() {

		if (getTricks().size() < 2) {
			throw new IllegalStateException(
					"No tricks finished in the game so far."); //$NON-NLS-1$
		}

		return tricks.get(tricks.size() - 2);
	}

	/**
	 * Sets the trick winner
	 * 
	 * @param trickNumber
	 *            The number of the trick in a game
	 * @param winner
	 *            The player ID of the winner of the trick
	 */
	public void setTrickWinner(final int trickNumber, final Player winner) {

		log.debug("setTrickWinner(" + trickNumber + ", " + winner + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		tricks.get(trickNumber).setTrickWinner(winner);
	}

	/**
	 * Gets the winner of the trick
	 * 
	 * @param trickNumber
	 *            The number of the trick in a game
	 * @return The player ID of the trick winner
	 */
	public Player getTrickWinner(final int trickNumber) {

		return tricks.get(trickNumber).getTrickWinner();
	}

	/**
	 * Gets all tricks
	 * 
	 * @return ArrayList of tricks
	 */
	public List<Trick> getTricks() {

		return tricks;
	}

	/**
	 * Gets the number of geschoben
	 * 
	 * @return Returns the number of geschoben
	 */
	public int getGeschoben() {

		return geschoben;
	}

	/**
	 * Gets the geschoben multiplier
	 * 
	 * @return Returns the geschoben multiplier
	 */
	public int getGeschobenMultiplier() {

		log.debug("geschoben=" + geschoben + ", 2^" + geschoben + "=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ (1 << geschoben));

		int multiplier = 0;

		if (geschoben < 0) {

			multiplier = geschoben;

		} else {

			// TODO: need to know what this is doing
			multiplier = 1 << geschoben;
		}

		return multiplier;
	}

	/**
	 * Raises the value of geschoben by 1
	 * 
	 */
	public void addGeschoben() {

		geschoben++;
	}

	/**
	 * Get the player cards
	 * 
	 * @param player
	 *            Player
	 * @return CardList of Cards from the player
	 */
	public CardList getPlayerCards(final Player player) {

		return playerHands.get(player).getImmutableCopy();
	}

	/**
	 * Gets a reference to the skat for the game
	 * 
	 * @return skat The cards of the skat
	 */
	public CardList getSkat() {
		return skat.getImmutableCopy();
	}

	/**
	 * Sets a new skat after discarding
	 * 
	 * @param player
	 *            Player ID of the discarding player
	 * @param newSkat
	 *            CardList of the new skat
	 */
	public void setDiscardedSkat(final Player player, final CardList newSkat) {

		final CardList hand = playerHands.get(player);

		// add old skat to player's hand
		hand.add(skat.get(0));
		hand.add(skat.get(1));

		// clear skat
		skat.clear();

		// add new cards to skat
		hand.remove(newSkat.get(0));
		skat.add(newSkat.get(0));
		hand.remove(newSkat.get(1));
		skat.add(newSkat.get(1));
	}

	/**
	 * Gets the dealt cards
	 * 
	 * @return The dealt cards
	 */
	public Map<Player, CardList> getDealtCards() {

		return dealtCards;
	}

	/**
	 * Gets the dealt skat
	 * 
	 * @return Dealt skat
	 */
	public CardList getDealtSkat() {

		return dealtSkat.getImmutableCopy();
	}

	/**
	 * Sets a dealt card
	 * 
	 * @param player
	 *            Player that got the Card
	 * @param card
	 *            Card that was dealt
	 */
	public void setDealtCard(final Player player, final Card card) {
		// remember the dealt cards
		dealtCards.get(player).add(card);
		// current cards are hold in playerHands and skat
		playerHands.get(player).add(card);
	}

	/**
	 * Sets a dealt cards
	 * 
	 * @param player
	 *            Player that got the Card
	 * @param cards
	 *            Cards that was dealt
	 */
	public void setDealtCards(final Player player, final CardList cards) {
		for (final Card card : cards) {
			setDealtCard(player, card);
		}
	}

	/**
	 * Sets cards for the skat
	 * 
	 * @param card0
	 *            First card
	 * @param card1
	 *            Second card
	 */
	public void setDealtSkatCards(final Card card0, final Card card1) {

		dealtSkat.add(card0);
		dealtSkat.add(card1);

		skat.addAll(dealtSkat);
	}

	/**
	 * Adds points to player points
	 * 
	 * @param player
	 *            Player to whom the points should be added
	 * @param points
	 *            Points to be added
	 */
	public void addPlayerPoints(final Player player, final int points) {

		playerPoints.put(player,
				playerPoints.get(player) + points);
	}

	/**
	 * Gets the points of a player
	 * 
	 * @param player
	 *            Player
	 * @return Points of the player
	 */
	public int getPlayerPoints(final Player player) {

		return playerPoints.get(player).intValue();
	}

	/**
	 * Gets the fore hand player for the current trick
	 * 
	 * @return Fore hand player for the current trick
	 */
	public Player getTrickForeHand() {

		return getCurrentTrick().getForeHand();
	}

	/**
	 * Sets the highest bid value for a player
	 * 
	 * @param player
	 *            Player
	 * @param bidValue
	 *            Highest bid value so far
	 */
	public void setPlayerBid(final Player player, final int bidValue) {

		playerBids.put(player, Integer.valueOf(bidValue));
	}

	/**
	 * Gets the highest bid value for a player
	 * 
	 * @param player
	 *            Player
	 * @return Highest bid value so far
	 */
	public int getPlayerBid(final Player player) {

		return playerBids.get(player).intValue();
	}

	/**
	 * Sets the value for a player pass
	 * 
	 * @param player
	 *            Player
	 * @param isPassing
	 *            TRUE, if the player passes
	 */
	public void setPlayerPass(final Player player, final boolean isPassing) {

		playerPasses.put(player, Boolean.valueOf(isPassing));
	}

	/**
	 * Gets the value for a player pass
	 * 
	 * @param player
	 *            Player
	 * @return TRUE, if the player passes
	 */
	public boolean isPlayerPass(final Player player) {

		return playerPasses.get(player).booleanValue();
	}

	/**
	 * Gets the number of passes so far
	 * 
	 * @return Number of passes
	 */
	public int getNumberOfPasses() {

		int numberOfPasses = 0;

		for (final Player currPlayer : Player.values()) {
			if (isPlayerPass(currPlayer)) {
				numberOfPasses++;
			}
		}

		return numberOfPasses;
	}

	/**
	 * Sets the game announcement
	 * 
	 * @param announcement
	 *            The game announcement
	 */
	public void setAnnouncement(final GameAnnouncement announcement) {

		final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(announcement.getGameType());
		if (announcement.getGameType() != GameType.RAMSCH) {
			if (!declarerPickedUpSkat) {
				factory.setHand(Boolean.TRUE);
			}
			factory.setHand(announcement.isHand());
			factory.setOuvert(announcement.isOuvert());
			factory.setSchneider(announcement.isSchneider());
			factory.setSchwarz(announcement.isSchwarz());
			factory.setDiscardedCards(announcement.discardedCards);
		}
		this.announcement = factory.getAnnouncement();

		rules = SkatRuleFactory.getSkatRules(getGameType());

		if (getGameType() == GameType.PASSED_IN) {

			gameState = GameState.GAME_OVER;
			calcResult();
		}
	}

	/**
	 * Gets the game type
	 * 
	 * @return Game type
	 */
	public GameType getGameType() {

		GameType gameType = null;

		if (announcement != null) {
			gameType = announcement.getGameType();
		}

		return gameType;
	}

	/**
	 * Gets the game announcement
	 * 
	 * @return The game announcement
	 */
	public GameAnnouncement getAnnoucement() {

		return announcement;
	}

	/**
	 * Checks whether the game was played under ISPA rules or not
	 * 
	 * @return TRUE when the game was played under ISPA rules
	 */
	public boolean isIspaRules() {

		return ispaRules;
	}

	/**
	 * Sets the flag for ISPA rules
	 * 
	 * @param isIspaRules
	 *            TRUE when the game was played under ISPA rules
	 */
	public void setIspaRules(final boolean isIspaRules) {
		ispaRules = isIspaRules;
	}

	/**
	 * Sets the game state
	 * 
	 * @param newState
	 *            New game state
	 */
	public void setGameState(final GameState newState) {

		gameState = newState;
	}

	/**
	 * Gets the game state
	 * 
	 * @return The game state
	 */
	public GameState getGameState() {

		return gameState;
	}

	/**
	 * Sets the schneider and schwarz flag according the player points
	 */
	public void setSchneiderSchwarz() {
		// FIXME this is rule logic --> move to SuitGrandRule
		final int declarerPoints = getPlayerPoints(declarer);

		if (declarerPoints >= 89 || declarerPoints <= 30) {

			result.setSchneider(true);
		}

		if (declarerPoints == 120 || declarerPoints == 0) {

			result.setSchwarz(true);
		}
	}

	public void setJungfrauDurchmarsch() {
		// FIXME this is rule logic --> move to RamschRule
		for (final Player currPlayer : Player.values()) {
			if (RamschRule.isDurchmarsch(currPlayer, this)) {
				result.setDurchmarsch(true);
			} else {
				if (RamschRule.isJungfrau(currPlayer, this)) {
					result.setJungfrau(true);
				}
			}
		}
	}

	/**
	 * Gets the dealing player
	 * 
	 * @return Dealing player
	 */
	public Player getDealer() {
		return dealer;
	}

	/**
	 * Sets the dealer player
	 * 
	 * @param newDealer
	 *            Dealing player
	 */
	public void setDealer(final Player newDealer) {
		dealer = newDealer;
	}

	/**
	 * Gets the player name
	 * 
	 * @param player
	 *            Player
	 * @return Player name
	 */
	public String getPlayerName(final Player player) {
		return playerNames.get(player);
	}

	/**
	 * Sets the player name
	 * 
	 * @param player
	 *            Player
	 * @param playerName
	 *            Player name
	 */
	public void setPlayerName(final Player player, final String playerName) {
		playerNames.put(player, playerName);
	}

	/**
	 * Gets whether the game was passed or nor
	 * 
	 * @return TRUE if the game was lost
	 */
	public boolean isGamePassed() {
		return playerPasses.get(Player.FOREHAND).booleanValue()
				&& playerPasses.get(Player.MIDDLEHAND).booleanValue()
				&& playerPasses.get(Player.FOREHAND).booleanValue();
	}

	/**
	 * Sets whether the declarer picked up the skat
	 * 
	 * @param isDeclarerPickedUpSkat
	 *            TRUE, if the declarer picked up the skat
	 */
	public void setDeclarerPickedUpSkat(final boolean isDeclarerPickedUpSkat) {
		this.declarerPickedUpSkat = isDeclarerPickedUpSkat;
		if (isDeclarerPickedUpSkat) {
			announcement.hand = false;
		}
	}

	/**
	 * Gets a summary of the game
	 * 
	 * @return Game summary
	 */
	public GameSummary getGameSummary() {
		final GameSummaryFactory factory = GameSummary.getFactory();

		factory.setGameType(getGameType());
		factory.setHand(isHand());
		factory.setOuvert(isOuvert());
		factory.setSchneider(isSchneider());
		factory.setSchwarz(isSchwarz());

		factory.setForeHand(getPlayerName(Player.FOREHAND));
		factory.setMiddleHand(getPlayerName(Player.MIDDLEHAND));
		factory.setRearHand(getPlayerName(Player.REARHAND));
		factory.setDeclarer(getDeclarer());

		factory.setTricks(getTricks());

		factory.setPlayerPoints(playerPoints);

		factory.setGameResult(getResult());

		return factory.getSummary();
	}

	/**
	 * Gets the last trick winner
	 * 
	 * @return Last trick winner
	 */
	public Player getLastTrickWinner() {
		// get last trick
		final Trick lastTrick = getLastTrick();

		// get trick winner
		return lastTrick.getTrickWinner();
	}

	/**
	 * Gets the active player
	 * 
	 * @return Active player
	 */
	public Player getActivePlayer() {
		return activePlayer;
	}

	/**
	 * Sets the active player
	 * 
	 * @param activePlayer
	 *            Active player
	 */
	public void setActivePlayer(final Player activePlayer) {
		this.activePlayer = activePlayer;
	}

	/**
	 * Checks whether one player made no trick
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if a player made no trick
	 */
	public boolean isPlayerMadeNoTrick() {

		return isPlayerMadeNoTrick(Player.FOREHAND)
				|| isPlayerMadeNoTrick(Player.MIDDLEHAND)
				|| isPlayerMadeNoTrick(Player.REARHAND);
	}

	/**
	 * Checks whether a certain player made no trick
	 * 
	 * @param gameData
	 *            Game data
	 * @param player
	 *            Player to check
	 * @return TRUE if the player made not trick
	 */
	public boolean isPlayerMadeNoTrick(final Player player) {

		final Set<Player> trickWinners = new HashSet<Player>();

		for (int i = 0; i < getTricks().size(); i++) {
			trickWinners.add(getTrickWinner(i));
		}

		return !trickWinners.contains(player);
	}

	/**
	 * Removes a card from a players hand
	 * 
	 * @param player
	 *            Player
	 * @param card
	 *            Card
	 */
	public void removePlayerCard(final Player player, final Card card) {
		playerHands.get(player).remove(card);
	}

	/**
	 * Gets the cards of a player after discarding
	 * 
	 * @param player
	 *            Player
	 * @return Cards after discarding
	 */
	public Map<Player, CardList> getCardsAfterDiscard() {

		final Map<Player, CardList> result = new HashMap<Player, CardList>();

		for (final Player player : Player.values()) {
			final CardList cards = new CardList();

			if (player.equals(getDeclarer())) {
				cards.addAll(getDealtCards().get(player));
				cards.addAll(getDealtSkat());
				cards.removeAll(getSkat());
			} else {
				cards.addAll(getDealtCards().get(player));
			}

			result.put(player, cards);
		}
		return result;
	}
}
