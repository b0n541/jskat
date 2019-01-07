/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
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
package org.jskat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jskat.control.event.skatgame.SkatGameEvent;
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

import com.google.common.eventbus.Subscribe;

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
		 * Contra calling
		 */
		CONTRA,
		/**
		 * Re calling
		 */
		RE,
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
	private Boolean ispaRules = true;

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
	 * Points the player made during the game
	 */
	private final Map<Player, Integer> playerPoints = new HashMap<Player, Integer>();

	/**
	 * Game result
	 */
	private SkatGameResult result;

	/**
	 * Player names
	 */
	private final Map<Player, String> playerNames = new HashMap<Player, String>();

	/**
	 * Bids the players made during bidding
	 */
	private final Map<Player, List<Integer>> playerBids = new HashMap<Player, List<Integer>>();

	/**
	 * Passes the players made during bidding
	 */
	private final Map<Player, Boolean> playerPasses = new HashMap<Player, Boolean>();

	/**
	 * Flag for a geschoben game (the skat was handed over from player to player at
	 * the beginning of a ramsch game)
	 */
	private int geschoben = 0;

	/**
	 * Tricks made in the game
	 */
	private final List<Trick> tricks = new ArrayList<Trick>();

	/**
	 * Cards on player hands
	 */
	private final Map<Player, CardList> playerHands = new HashMap<Player, CardList>();

	/**
	 * Cards in the skat
	 */
	private final CardList skat = new CardList();

	/**
	 * Holds all cards dealt to the players
	 */
	private final Map<Player, CardList> dealtCards = new HashMap<Player, CardList>();

	/**
	 * Holds all cards dealt to skat
	 */
	private final CardList dealtSkat = new CardList();

	private Boolean skatPickedUp = false;

	private final Set<Player> ramschLoosers = new HashSet<Player>();

	private final List<SkatGameEvent> gameMoves = new ArrayList<>();

	/**
	 * Creates a new instance of a Skat game data
	 */
	public SkatGameData() {

		intializeVariables();

		log.debug("Game data created"); //$NON-NLS-1$
	}

	@Subscribe
	public void adjustDataOn(final SkatGameEvent event) {
		event.processForward(this);
		gameMoves.add(event);
	}

	private void intializeVariables() {

		GameAnnouncement.getFactory();
		announcement = GameAnnouncementFactory.getEmptyAnnouncement();
		result = new SkatGameResult();

		for (final Player player : Player.values()) {
			playerNames.put(player, ""); //$NON-NLS-1$
			playerHands.put(player, new CardList());
			dealtCards.put(player, new CardList());
			playerPoints.put(player, 0);
			playerBids.put(player, new ArrayList<Integer>());
			playerPasses.put(player, Boolean.FALSE);
		}
	}

	/**
	 * Returns all game moves.
	 *
	 * @return Game moves
	 */
	public List<SkatGameEvent> getGameMoves() {
		return Collections.unmodifiableList(gameMoves);
	}

	/**
	 * Returns the result of the game
	 *
	 * @return Value of the game
	 */
	public SkatGameResult getGameResult() {

		if (result.getGameValue() == -1
				&& getGameType() != GameType.PASSED_IN) {

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
	public Integer getMaxBidValue() {

		Integer result = 0;

		for (final List<Integer> bids : playerBids.values()) {
			final int maxBid = bids.size() > 0 ? bids.get(bids.size() - 1) : 0;
			if (maxBid > result) {
				result = maxBid;
			}
		}

		return result;
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

		if (newScore < 31 || newScore > 89) {
			result.setSchneider(true);
		} else {
			result.setSchneider(false);
		}

		if (newScore == 0 || newScore == 120) {
			result.setSchwarz(true);
		} else {
			result.setSchwarz(false);
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
				|| GameType.GRAND.equals(announcement.gameType)
				|| GameType.NULL.equals(announcement.gameType)) {

			result.setFinalDeclarerPoints(getDeclarerScore());
			result.setFinalOpponentPoints(getOpponentScore());
		}

		if (GameType.CLUBS.equals(announcement.gameType)
				|| GameType.SPADES.equals(announcement.gameType)
				|| GameType.HEARTS.equals(announcement.gameType)
				|| GameType.DIAMONDS.equals(announcement.gameType)
				|| GameType.GRAND.equals(announcement.gameType)) {

			result.setMultiplier(rules.getMultiplier(this));
			result.setPlayWithJacks(rules.isPlayWithJacks(this));
		}

		if (GameType.RAMSCH.equals(announcement.gameType)) {
			finishRamschGame();
		}
	}

	/**
	 * Calculates final results of a ramsch game
	 */
	public void finishRamschGame() {

		result.setWon(false);

		// FIXME this is rule logic --> remove it from data object!!!
		Integer highestPoints = Integer.MIN_VALUE;
		for (final Player player : Player.values()) {
			if (highestPoints < playerPoints.get(player)) {
				highestPoints = playerPoints.get(player);
			}
		}
		for (final Player player : Player.values()) {
			if (playerPoints.get(player).equals(highestPoints)) {
				ramschLoosers.add(player);
			}
		}

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
	 * Removes the last trick.
	 */
	public void removeLastTrick() {
		tricks.remove(tricks.size() - 1);
	}

	/**
	 * Adds a trick card.
	 *
	 * @param card
	 *            Card to be added
	 */
	public void addTrickCard(final Card card) {

		getCurrentTrick().addCard(card);
	}

	/**
	 * Removes a trick card.
	 *
	 * @param card
	 *            Card to be removed
	 */
	public void removeTrickCard(final Card card) {
		getCurrentTrick().removeCard(card);
	}

	/**
	 * Gets the current trick
	 *
	 * @return Current trick
	 */
	public Trick getCurrentTrick() {
		if (tricks.size() > 0) {
			return tricks.get(tricks.size() - 1);
		}
		return null;
	}

	/**
	 * Gets the last completed trick.
	 *
	 * @return Last completed trick, NULL otherwise
	 */
	public Trick getLastCompletedTrick() {

		if (getTricks().size() < 2) {
			throw new IllegalStateException(
					"No tricks finished in the game so far."); //$NON-NLS-1$
		}

		if (tricks.get(tricks.size() - 1).isTrickFinished()) {
			// after the game is finished take the last trick
			return tricks.get(tricks.size() - 1);
		} else {
			// during the game the last trick is not finished
			return tricks.get(tricks.size() - 2);
		}
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

		return Collections.unmodifiableList(tricks);
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

		playerHands.get(player).removeAll(newSkat);

		skat.clear();
		skat.addAll(newSkat);
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
	 * Sets a dealt cards
	 *
	 * @param player
	 *            Player that got the Card
	 * @param cards
	 *            Cards that was dealt
	 */
	public void addDealtCards(final Player player, final CardList cards) {
		dealtCards.get(player).addAll(cards);
		playerHands.get(player).addAll(cards);
	}

	/**
	 * Sets cards for the skat
	 *
	 * @param cards
	 *            Skat cards
	 */
	public void setDealtSkatCards(final CardList cards) {

		dealtSkat.clear();
		dealtSkat.addAll(cards);
		skat.clear();
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

		playerPoints.put(player, playerPoints.get(player) + points);
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
	 * Sets the max bid value for a player
	 *
	 * @param player
	 *            Player
	 * @param bidValue
	 *            Max bid value so far
	 */
	public void addPlayerBid(final Player player, final int bidValue) {

		playerBids.get(player).add(bidValue);
	}

	public void removeLastPlayerBid(final Player player) {
		final int lastIndex = playerBids.get(player).size() - 1;
		playerBids.get(player).remove(lastIndex);
	}

	/**
	 * Gets the highest bid value for a player
	 *
	 * @param player
	 *            Player
	 * @return Highest bid value so far
	 */
	public int getMaxPlayerBid(final Player player) {
		final List<Integer> bids = playerBids.get(player);
		return bids.size() > 0 ? bids.get(bids.size() - 1) : 0;
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
			// if (!declarerPickedUpSkat) {
			// factory.setHand(Boolean.TRUE);
			// }
			factory.setHand(announcement.isHand());
			factory.setOuvert(announcement.isOuvert());
			factory.setSchneider(announcement.isSchneider());
			factory.setSchwarz(announcement.isSchwarz());
			factory.setDiscardedCards(announcement.discardedCards);
		}
		this.announcement = factory.getAnnouncement();

		if (this.announcement == null) {
			this.announcement = GameAnnouncementFactory.getEmptyAnnouncement();
			rules = null;
		} else {
			rules = SkatRuleFactory.getSkatRules(getGameType());
		}

		if (GameType.PASSED_IN.equals(getGameType())) {
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

		if (announcement == null) {
			throw new IllegalStateException("No game announcement available!");
		}

		return announcement.getGameType();
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
	 * Checks whether the game has ended or not.
	 *
	 * @return TRUE, if the game has ended
	 */
	public boolean isGameFinished() {

		return GameState.PRELIMINARY_GAME_END.equals(gameState)
				|| GameState.GAME_OVER.equals(gameState);
	}

	/**
	 * Sets the schneider and schwarz flag according the player points
	 */
	public void setSchneiderSchwarz() {
		// FIXME this is rule logic --> move to SuitGrandRule
		final int declarerPoints = getPlayerPoints(declarer);

		if (declarerPoints >= 90 || declarerPoints <= 30) {

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
			} else if (RamschRule.isJungfrau(currPlayer, this)) {
				result.setJungfrau(true);
			}
		}
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
		factory.setContra(isContra());
		factory.setRe(isRe());

		factory.setForeHand(getPlayerName(Player.FOREHAND));
		factory.setMiddleHand(getPlayerName(Player.MIDDLEHAND));
		factory.setRearHand(getPlayerName(Player.REARHAND));
		factory.setDeclarer(getDeclarer());

		factory.setTricks(getTricks());

		factory.setPlayerPoints(playerPoints);

		if (announcement.gameType == GameType.RAMSCH) {
			for (final Player looser : ramschLoosers) {
				factory.addRamschLooser(looser);
			}
		}

		factory.setGameResult(getResult());

		return factory.getSummary();
	}

	/**
	 * Gets the last trick winner
	 *
	 * @return Last trick winner
	 */
	public Player getLastTrickWinner() {
		return getLastCompletedTrick().getTrickWinner();
	}

	/**
	 * Checks whether one player made no trick
	 *
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
	 * Removes a card from a players hand.
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

	/**
	 * Gets the looses for a ramsch game
	 *
	 * @return Set of loosing players
	 */
	public Set<Player> getRamschLoosers() {
		if (announcement == null
				|| !GameType.RAMSCH.equals(announcement.gameType)) {
			throw new IllegalStateException(
					"This game data object is not from a ramsch game!");
		}
		return Collections.unmodifiableSet(ramschLoosers);
	}

	/**
	 * Removes dealt cards for a player
	 *
	 * @param player
	 *            Player
	 * @param cards
	 *            Cards
	 */
	public void removeDealtCards(final Player player, final CardList cards) {
		dealtCards.get(player).removeAll(cards);
	}

	/**
	 * Removes cards from dealt skat
	 *
	 * @param cards
	 *            Cards
	 */
	public void removeDealtSkatCards(final CardList cards) {
		dealtSkat.removeAll(cards);
		skat.removeAll(cards);
	}

	/**
	 * Adds all cards from the skat to a players hand
	 *
	 * @param player
	 *            Player
	 */
	public void addSkatToPlayer(final Player player) {
		playerHands.get(player).addAll(skat);
		skat.clear();
		announcement.hand = false;
	}

	/**
	 * Removes all cards from the former skat from a players hand
	 *
	 * @param player
	 *            Player
	 */
	public void removeSkatFromPlayer(final Player player) {
		playerHands.get(player).removeAll(dealtSkat);
		skat.addAll(dealtSkat);
		announcement.hand = true;
	}

	/**
	 * Sets the cards in the skat.
	 *
	 * @param cards
	 *            Cards
	 */
	public void setSkatCards(final CardList cards) {
		skat.clear();
		skat.addAll(cards);
	}

	/**
	 * Adds cards to a players hand.
	 *
	 * @param player
	 *            Player
	 * @param cards
	 *            Cards
	 */
	public void addPlayerCards(final Player player, final CardList cards) {
		playerHands.get(player).addAll(cards);
	}

	/**
	 * Adds a card to a players hand.
	 *
	 * @param player
	 *            Player
	 * @param card
	 *            Card
	 */
	public void addPlayerCard(final Player player, final Card card) {
		playerHands.get(player).add(card);
	}

	/**
	 * Sets Contra information.
	 *
	 * @param isContra
	 *            <code>true</code>, if Contra was called.
	 */
	public void setContra(final boolean isContra) {
		announcement.contra = isContra;
	}

	/**
	 * Checks, whether Contra was called.
	 *
	 * @return <code>true</code>, if Contra was called.
	 */
	public Boolean isContra() {
		return announcement.contra;
	}

	/**
	 * Sets Re information.
	 *
	 * @param isRe
	 *            <code>true</code>, if Re was called.
	 */
	public void setRe(final boolean isRe) {
		announcement.re = isRe;
	}

	/**
	 * Checks, whether Re was called.
	 *
	 * @return <code>true</code>, if Re was called.
	 */
	public Boolean isRe() {
		return announcement.re;
	}

	/**
	 * Sets the information that the winner of the bidding picked up the skat.
	 *
	 * @param skatPickedUp
	 *            TRUE if the winner of the bidding picked up the skat
	 */
	public void setSkatPickUp(final boolean skatPickedUp) {
		this.skatPickedUp = skatPickedUp;
	}

	/**
	 * Checks whether the skat was picked up.
	 *
	 * @return TRUE if the skat was picked up
	 */
	public Boolean isSkatPickedUp() {
		return skatPickedUp;
	}
}
