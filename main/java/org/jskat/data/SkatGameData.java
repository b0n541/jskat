/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.10.0-SNAPSHOT
 * Build date: 2011-10-09
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
package org.jskat.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary.GameSummaryFactory;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.BasicSkatRules;
import org.jskat.util.rule.RamschRules;
import org.jskat.util.rule.SkatRuleFactory;

/**
 * Data class for a Skat game
 */
public class SkatGameData {

	private static Log log = LogFactory.getLog(SkatGameData.class);

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
	private BasicSkatRules rules;

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

		announcement = new GameAnnouncement();
		result = new SkatGameResult();

		playerNames = new HashMap<Player, String>();
		playerHands = new HashMap<Player, CardList>();
		dealtCards = new HashMap<Player, CardList>();
		playerPoints = new HashMap<Player, Integer>();
		playerBids = new HashMap<Player, Integer>();
		playerPasses = new HashMap<Player, Boolean>();

		for (Player player : Player.values()) {
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
	public void setDeclarer(Player singlePlayer) {

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
	public void setBidValue(int value) {

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
	public int getScore(Player player) {

		return playerPoints.get(player).intValue();
	}

	/**
	 * Overwrites the declarer score
	 * 
	 * @param newScore
	 *            New score
	 */
	public void setDeclarerScore(int newScore) {

		playerPoints.put(declarer, Integer.valueOf(newScore));
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
				result.setWon(rules.calcGameWon(this));
			}
			result.setGameValue(rules.calcGameResult(this));
		}

		if (GameType.CLUBS.equals(announcement.gameType) || GameType.SPADES.equals(announcement.gameType)
				|| GameType.HEARTS.equals(announcement.gameType) || GameType.DIAMONDS.equals(announcement.gameType)
				|| GameType.GRAND.equals(announcement.gameType)) {

			result.setFinalDeclarerPoints(getDeclarerScore());
			result.setFinalOpponentPoints(getOpponentScore());
			result.setMultiplier(rules.getMultiplier(this));
			result.setPlayWithJacks(rules.isPlayWithJacks(this));
		}

		if (result.isWon() && getBidValue() > result.getGameValue()) {

			log.debug("Overbidding: Game is lost"); //$NON-NLS-1$
			// Game was overbidded
			// game is lost despite the winning of the single player
			result.setOverBidded(true);
			result.setWon(false);
			result.setGameValue(result.getGameValue() * -2);
		}
	}

	/**
	 * Calculates final results of a ramsch game
	 */
	public void finishRamschGame() {

		Player ramschLoser;
		getGameResult().setWon(false);

		// FIXME this is rule logic --> remove it from data object!!!
		if (playerPoints.get(Player.FOREHAND).intValue() > playerPoints.get(Player.MIDDLEHAND).intValue()) {

			if (playerPoints.get(Player.FOREHAND).intValue() > playerPoints.get(Player.REARHAND).intValue()) {
				ramschLoser = Player.FOREHAND;
			} else {
				if (playerPoints.get(Player.FOREHAND).intValue() == playerPoints.get(Player.REARHAND).intValue()) {
					ramschLoser = Player.MIDDLEHAND;
					getGameResult().setWon(true);
				} else {
					ramschLoser = Player.REARHAND;
				}
			}

		} else {

			if (playerPoints.get(Player.MIDDLEHAND).intValue() > playerPoints.get(Player.REARHAND).intValue()) {
				ramschLoser = Player.MIDDLEHAND;
			} else {
				if (playerPoints.get(Player.MIDDLEHAND).intValue() == playerPoints.get(Player.REARHAND).intValue()) {
					ramschLoser = Player.FOREHAND;
					getGameResult().setWon(true);
				} else {
					ramschLoser = Player.REARHAND;
				}
			}
		}

		setDeclarer(ramschLoser);

		if (isDurchmarsch()) {
			getGameResult().setWon(true);
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
	public void setResult(SkatGameResult newResult) {

		result = newResult;
	}

	/**
	 * Adds a trick
	 * 
	 * @param newTrick
	 *            New trick
	 */
	public void addTrick(Trick newTrick) {

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
	public void setTrickCard(Player player, Card card) {

		log.debug(this + ".setTrickCard(" + player + ", " //$NON-NLS-1$ //$NON-NLS-2$
				+ card + ")"); //$NON-NLS-1$

		Trick currentTrick = getCurrentTrick();
		Player trickForeHand = currentTrick.getForeHand();

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

		if (getTricks().size() == 0) {
			throw new IllegalStateException("No tricks played in the game so far."); //$NON-NLS-1$
		}

		return tricks.get(tricks.size() - 1);
	}

	/**
	 * Sets the trick winner
	 * 
	 * @param trickNumber
	 *            The number of the trick in a game
	 * @param winner
	 *            The player ID of the winner of the trick
	 */
	public void setTrickWinner(int trickNumber, Player winner) {

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
	public Player getTrickWinner(int trickNumber) {

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
			multiplier = (1 << geschoben);
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
	public CardList getPlayerCards(Player player) {

		return playerHands.get(player);
	}

	/**
	 * Gets a reference to the skat for the game
	 * 
	 * @return skat The cards of the skat
	 */
	public CardList getSkat() {

		return skat;
	}

	/**
	 * Sets a new skat after discarding
	 * 
	 * @param player
	 *            Player ID of the discarding player
	 * @param newSkat
	 *            CardList of the new skat
	 */
	public void setDiscardedSkat(Player player, CardList newSkat) {

		CardList hand = playerHands.get(player);

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

		return dealtSkat;
	}

	/**
	 * Sets a dealt card
	 * 
	 * @param player
	 *            Player that got the Card
	 * @param card
	 *            Card that was dealt
	 */
	public void setDealtCard(Player player, Card card) {

		// remember the dealt cards
		dealtCards.get(player).add(card);

		// current cards are hold in playerHands and skat
		playerHands.get(player).add(card);
	}

	/**
	 * Sets cards for the skat
	 * 
	 * @param card0
	 *            First card
	 * @param card1
	 *            Second card
	 */
	public void setDealtSkatCards(Card card0, Card card1) {

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
	public void addPlayerPoints(Player player, int points) {

		playerPoints.put(player, Integer.valueOf(playerPoints.get(player).intValue() + points));
	}

	/**
	 * Gets the points of a player
	 * 
	 * @param player
	 *            Player
	 * @return Points of the player
	 */
	public int getPlayerPoints(Player player) {

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
	public void setPlayerBid(Player player, int bidValue) {

		playerBids.put(player, Integer.valueOf(bidValue));
	}

	/**
	 * Gets the highest bid value for a player
	 * 
	 * @param player
	 *            Player
	 * @return Highest bid value so far
	 */
	public int getPlayerBid(Player player) {

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
	public void setPlayerPass(Player player, boolean isPassing) {

		playerPasses.put(player, Boolean.valueOf(isPassing));
	}

	/**
	 * Gets the value for a player pass
	 * 
	 * @param player
	 *            Player
	 * @return TRUE, if the player passes
	 */
	public boolean isPlayerPass(Player player) {

		return playerPasses.get(player).booleanValue();
	}

	/**
	 * Gets the number of passes so far
	 * 
	 * @return Number of passes
	 */
	public int getNumberOfPasses() {

		int numberOfPasses = 0;

		for (Player currPlayer : Player.values()) {
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
	public void setAnnouncement(GameAnnouncement announcement) {

		GameAnnouncementFactory factory = GameAnnouncement.getFactory();
		factory.setGameType(announcement.getGameType());
		if (announcement.getGameType() != GameType.RAMSCH) {
			if (!declarerPickedUpSkat) {
				factory.setHand(Boolean.TRUE);
			}
			factory.setOuvert(Boolean.valueOf(announcement.isOuvert()));
			factory.setSchneider(Boolean.valueOf(announcement.isSchneider()));
			factory.setSchwarz(Boolean.valueOf(announcement.isSchwarz()));
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
	public void setIspaRules(boolean isIspaRules) {
		ispaRules = isIspaRules;
	}

	/**
	 * Sets the game state
	 * 
	 * @param newState
	 *            New game state
	 */
	public void setGameState(GameState newState) {

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
		// FIXME this is rule logic --> move to SuitGrandRules
		int declarerPoints = getPlayerPoints(declarer);

		if (declarerPoints >= 89 || declarerPoints <= 30) {

			result.setSchneider(true);
		}

		if (declarerPoints == 120 || declarerPoints == 0) {

			result.setSchwarz(true);
		}
	}

	public void setJungfrauDurchmarsch() {
		// FIXME this is rule logic --> move to RamschRules
		for (Player currPlayer : Player.values()) {
			result.setJungfrau(((RamschRules) rules).isJungfrau(currPlayer, this));
			result.setDurchmarsch(((RamschRules) rules).isDurchmarsch(currPlayer, this));
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
	public void setDealer(Player newDealer) {
		dealer = newDealer;
	}

	/**
	 * Gets the player name
	 * 
	 * @param player
	 *            Player
	 * @return Player name
	 */
	public String getPlayerName(Player player) {
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
	public void setPlayerName(Player player, String playerName) {
		playerNames.put(player, playerName);
	}

	/**
	 * Gets whether the game was passed or nor
	 * 
	 * @return TRUE if the game was lost
	 */
	public boolean isGamePassed() {
		return playerPasses.get(Player.FOREHAND).booleanValue() && playerPasses.get(Player.MIDDLEHAND).booleanValue()
				&& playerPasses.get(Player.FOREHAND).booleanValue();
	}

	/**
	 * Sets whether the declarer picked up the skat
	 * 
	 * @param isDeclarerPickedUpSkat
	 *            TRUE, if the declarer picked up the skat
	 */
	public void setDeclarerPickedUpSkat(boolean isDeclarerPickedUpSkat) {
		this.declarerPickedUpSkat = isDeclarerPickedUpSkat;
	}

	/**
	 * Gets a summary of the game
	 * 
	 * @return Game summary
	 */
	public GameSummary getGameSummary() {
		GameSummaryFactory factory = GameSummary.getFactory();

		factory.setGameType(getGameType());
		factory.setHand(Boolean.valueOf(isHand()));
		factory.setOuvert(Boolean.valueOf(isOuvert()));
		factory.setSchneider(Boolean.valueOf(isSchneider()));
		factory.setSchwarz(Boolean.valueOf(isSchwarz()));

		factory.setForeHand(getPlayerName(Player.FOREHAND));
		factory.setMiddleHand(getPlayerName(Player.MIDDLEHAND));
		factory.setRearHand(getPlayerName(Player.REARHAND));
		factory.setDeclarer(getDeclarer());

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
		Trick lastTrick = getLastTrick();

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
	public void setActivePlayer(Player activePlayer) {
		this.activePlayer = activePlayer;
	}
}
