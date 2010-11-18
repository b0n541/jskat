/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;
import de.jskat.util.Suit;
import de.jskat.util.rule.BasicSkatRules;
import de.jskat.util.rule.SkatRuleFactory;

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
		NEW_GAME,
		/**
		 * Dealing phase
		 */
		DEALING,
		/**
		 * Bidding phase
		 */
		BIDDING,
		/**
		 * Look into skat or play hand game phase
		 */
		LOOK_INTO_SKAT,
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
		CALC_GAME_VALUE,
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
	 * Points the player made during the game
	 */
	private Map<Player, Integer> playerPoints;

	/**
	 * Game result
	 */
	private int result = 0;

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
	 * Flag for won games
	 */
	private boolean won = false;

	/**
	 * Flag for an over bidded game
	 */
	private boolean overBidded = false;

	/**
	 * Flag for the club jack on declarers hand
	 */
	private boolean clubJack = false;

	/**
	 * Flag for the spade jack on declarers hand
	 */
	private boolean spadeJack = false;

	/**
	 * Flag for the heart jack on declarers hand
	 */
	private boolean heartJack = false;

	/**
	 * Flag for the diamond jack on declarers hand
	 */
	private boolean diamondJack = false;

	/**
	 * Flag for a schneider game
	 */
	private boolean schneider = false;

	/**
	 * Flag for a schwarz game
	 */
	private boolean schwarz = false;

	/**
	 * Flag for a durchmarsch game (one player made all tricks in a ramsch game)
	 */
	private boolean durchmarsch = false;

	/**
	 * Flag for a jungfrau game (one player made no tricks in a ramsch game)
	 */
	private boolean jungfrau = false;

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
	 * Holds all cards dealt to the players and to the skat
	 */
	private Map<Player, CardList> dealtCards;

	/**
	 * Creates a new instance of a Skat game data
	 */
	public SkatGameData() {

		intializeVariables();

		log.debug("Game created"); //$NON-NLS-1$
	}

	private void intializeVariables() {

		this.announcement = new GameAnnouncement();
		this.result = -1;

		this.playerNames = new HashMap<Player, String>();
		this.playerHands = new HashMap<Player, CardList>();
		this.dealtCards = new HashMap<Player, CardList>();
		this.playerPoints = new HashMap<Player, Integer>();
		this.playerBids = new HashMap<Player, Integer>();

		for (Player player : Player.values()) {
			this.playerNames.put(player, ""); //$NON-NLS-1$
			this.playerHands.put(player, new CardList());
			this.dealtCards.put(player, new CardList());
			this.playerPoints.put(player, Integer.valueOf(0));
			this.playerBids.put(player, Integer.valueOf(0));
		}

		this.skat = new CardList();

		this.tricks = new ArrayList<Trick>();
	}

	/**
	 * Returns the result of the game
	 * 
	 * @return Value of the game
	 */
	public int getGameResult() {

		if (this.result == 0 && getGameType() != GameType.PASSED_IN) {

			log.warn("Game result hasn't been calculated yet!"); //$NON-NLS-1$
			calcResult();
		}

		return this.result;
	}

	/**
	 * Returns the single player of the game
	 * 
	 * @return Player ID of the single player
	 */
	public Player getDeclarer() {

		return this.declarer;
	}

	/**
	 * Set the single player of the game
	 * 
	 * @param singlePlayer
	 *            Player ID of the single player
	 */
	public void setDeclarer(Player singlePlayer) {

		log.debug("Current single Player " + singlePlayer); //$NON-NLS-1$

		this.declarer = singlePlayer;
	}

	/**
	 * Returns the highest bid value of the game
	 * 
	 * @return Highest bid value
	 */
	public int getBidValue() {

		return this.highestBidValue;
	}

	/**
	 * Sets the highest bid value of the game
	 * 
	 * @param value
	 *            Highest bid value
	 */
	public void setBidValue(int value) {

		this.highestBidValue = value;
		log.debug("setBidValue(" + value + ")"); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Gets whether the game was lost or not
	 * 
	 * @return TRUE if the game was lost
	 */
	public boolean isGameLost() {

		return !this.won;
	}

	/**
	 * Gets whether a game was won or not
	 * 
	 * @return TRUE if the game was won
	 */
	public boolean isGameWon() {

		return this.won;
	}

	/**
	 * Sets whether the game was won or not
	 * 
	 * @param gameWon
	 *            TRUE if the game was won
	 */
	public void setGameWon(boolean gameWon) {

		this.won = gameWon;

		log.debug("setGameLost(): Game lost = " + this.won); //$NON-NLS-1$
	}

	/**
	 * Checks whether the single player overbidded
	 * 
	 * @return TRUE if the single player overbidded
	 */
	public boolean getOverBidded() {

		// TODO This should not be possible when a Ramsch game is played
		// maybe throw an exception instead?
		if (getGameType() == GameType.RAMSCH) {
			log.warn("Overbidding cannot happen in Ramsch games: gameType=" //$NON-NLS-1$
					+ getGameType());
		}
		return this.overBidded;
	}

	/**
	 * Sets the flag for overbidding
	 * 
	 * @param newOverBidded
	 *            TRUE if the single player overbidded
	 */
	public void setOverBidded(boolean newOverBidded) {

		this.overBidded = newOverBidded;

		if (this.overBidded) {
			// game was overbidded
			this.won = false;
			this.result = this.result * -2;
		}
	}

	/**
	 * Checks whether the single player had the club jack
	 * 
	 * @return TRUE if the single player had the club jack
	 */
	public boolean getClubJack() {

		return this.clubJack;
	}

	/**
	 * Sets the flag for the club jack
	 * 
	 * @param hadClubJack
	 *            TRUE if the single player had the club jack
	 */
	public void setClubJack(boolean hadClubJack) {

		this.clubJack = hadClubJack;
	}

	/**
	 * Checks whether the single player had the spade jack
	 * 
	 * @return TRUE if the single player had the spade jack
	 */
	public boolean getSpadeJack() {

		return this.spadeJack;
	}

	/**
	 * Sets the flag for the spade jack
	 * 
	 * @param hadSpadeJack
	 *            TRUE if the single player had the spade jack
	 */
	public void setSpadeJack(boolean hadSpadeJack) {

		this.spadeJack = hadSpadeJack;
	}

	/**
	 * Checks whether the single player had the heart jack
	 * 
	 * @return TRUE if the single player had the heart jack
	 */
	public boolean getHeartJack() {

		return this.heartJack;
	}

	/**
	 * Sets the flag for the heart jack
	 * 
	 * @param hadHeartJack
	 *            TRUE if the single player had the heart jack
	 */
	public void setHeartJack(boolean hadHeartJack) {

		this.heartJack = hadHeartJack;
	}

	/**
	 * Checks whether the single player had the diamond jack
	 * 
	 * @return TRUE if the single player had the diamond jack
	 */
	public boolean getDiamondJack() {

		return this.diamondJack;
	}

	/**
	 * Sets the flag for the diamond jack
	 * 
	 * @param hadDiamondJack
	 *            TRUE if the single player had the diamond jack
	 */
	public void setDiamondJack(boolean hadDiamondJack) {

		this.diamondJack = hadDiamondJack;
	}

	/**
	 * Checks whether the single player has played a hand game
	 * 
	 * @return TRUE, if the single player has played a hand game
	 */
	public boolean isHand() {

		return this.announcement.isHand();
	}

	/**
	 * Sets whether the single player has played a hand game
	 * 
	 * @param isHand
	 *            TRUE, if the single player has played a hand game
	 */
	public void setHand(boolean isHand) {

		this.announcement.setHand(isHand);
	}

	/**
	 * Checks whether the single player has played an ouvert
	 * 
	 * @return TRUE if the single player has played an ouvert game
	 */
	public boolean isOuvert() {

		return this.announcement.isOuvert();
	}

	/**
	 * Checks whether one party played schneider
	 * 
	 * @return TRUE if the single player or the opponents played schneider
	 */
	public boolean isSchneider() {

		return this.schneider;
	}

	/**
	 * Sets the flag for schneider
	 * 
	 * @param isSchneider
	 *            TRUE if the single player or the opponents played schneider
	 */
	public void setSchneider(boolean isSchneider) {

		this.schneider = isSchneider;
	}

	/**
	 * Checks whether schneider was announced
	 * 
	 * @return TRUE if Schneider was announced
	 */
	public boolean isSchneiderAnnounced() {

		return this.announcement.isSchneider();
	}

	/**
	 * Checks whether schwarz was played
	 * 
	 * @return TRUE if the player or the opponents played schwarz
	 */
	public boolean isSchwarz() {

		return this.schwarz;
	}

	/**
	 * Sets the flag for schwarz
	 * 
	 * @param isSchwarz
	 *            TRUE if the player or the opponents played schwarz
	 */
	public void setSchwarz(boolean isSchwarz) {

		this.schwarz = isSchwarz;
	}

	/**
	 * Checks whether schwarz was announced
	 * 
	 * @return TRUE if schwarz was announced
	 */
	public boolean isSchwarzAnnounced() {

		return this.announcement.isSchwarz();
	}

	/**
	 * Checks whether contra was announced
	 * 
	 * @return TRUE if contra was announced
	 */
	public boolean isContra() {

		return this.announcement.isContra();
	}

	/**
	 * Checks whether re was announced
	 * 
	 * @return TRUE if re was announced
	 */
	public boolean isRe() {

		return this.announcement.isRe();
	}

	/**
	 * Checks whether bock was announced
	 * 
	 * @return TRUE if bock was announced
	 */
	public boolean isBock() {

		return this.announcement.isBock();
	}

	/**
	 * Checks whether a durchmarsch was done in a ramsch game or not
	 * 
	 * @return TRUE if someone did a durchmarsch in a ramsch game
	 */
	public boolean isDurchmarsch() {

		return this.durchmarsch;
	}

	/**
	 * Sets the flag for durchmarsch in a ramsch game
	 * 
	 * @param isDurchmarsch
	 *            TRUE if someone did a durchmarsch in a ramsch game
	 */
	public void setDurchmarsch(boolean isDurchmarsch) {

		this.durchmarsch = isDurchmarsch;
	}

	/**
	 * Checks whether someone was jungfrau in a ramsch game
	 * 
	 * @return TRUE if someone was jungfrau in a ramsch game
	 */
	public boolean isJungfrau() {

		return this.jungfrau;
	}

	/**
	 * Sets the flag for jungfrau in a ramsch game
	 * 
	 * @param isJungfrau
	 *            TRUE if someone was jungfrau in a ramsch game
	 */
	public void setJungfrau(boolean isJungfrau) {

		this.jungfrau = isJungfrau;
	}

	/**
	 * Adds the value of a trick to the points of a player
	 * 
	 * @param player
	 *            The ID of the player
	 * @param trickValue
	 *            The value of the trick
	 */
	public void addToPlayerPoints(Player player, int trickValue) {

		this.playerPoints.put(player, new Integer(trickValue));
	}

	/**
	 * Gets the score of a player
	 * 
	 * @param player
	 *            The ID of a player
	 * @return The score of a player
	 */
	public int getScore(Player player) {

		return this.playerPoints.get(player).intValue();
	}

	/**
	 * Gets the score of the single player
	 * 
	 * @return The score of the single player
	 */
	public int getDeclarerScore() {

		int score = 0;

		if (this.declarer != null) {

			score = getScore(this.declarer);
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

		if (this.declarer != null) {

			score = 120 - getScore(this.declarer);
		}

		return score;
	}

	/**
	 * Calculates the result of a game
	 */
	public void calcResult() {

		if (this.getGameType() == GameType.PASSED_IN) {

			this.won = false;
			this.result = 0;
		} else {

			this.won = this.rules.calcGameWon(this);
			this.result = this.rules.calcGameResult(this);
		}
	}

	/**
	 * Calculates final results of a ramsch game
	 */
	public void finishRamschGame() {

		Player ramschLoser;

		// TODO what happens if two or more players have the same points?
		// FIXME this is rule logic --> remove it from data object!!!
		if (this.playerPoints.get(Player.FORE_HAND) > this.playerPoints
				.get(Player.MIDDLE_HAND)) {

			if (this.playerPoints.get(Player.FORE_HAND) > this.playerPoints
					.get(Player.HIND_HAND)) {
				ramschLoser = Player.FORE_HAND;
			} else {
				ramschLoser = Player.HIND_HAND;
			}

		} else {

			if (this.playerPoints.get(Player.MIDDLE_HAND) > this.playerPoints
					.get(Player.HIND_HAND)) {
				ramschLoser = Player.MIDDLE_HAND;
			} else {
				ramschLoser = Player.HIND_HAND;
			}
		}

		setDeclarer(ramschLoser);

		if (isDurchmarsch()) {
			setGameWon(true);
		} else {
			setGameWon(false);
		}
	}

	/**
	 * Gets the result of a game
	 * 
	 * @return The result of a game
	 */
	public int getResult() {

		return this.result;
	}

	/**
	 * Adds a trick
	 * 
	 * @param foreHand
	 *            The player ID of the forehand in the trick
	 */
	public void addTrick(Player foreHand) {

		this.tricks.add(new Trick(foreHand));
	}

	/**
	 * Sets a trick card
	 * 
	 * @param trickNumber
	 *            The number of the trick in a game
	 * @param player
	 *            The player of the card in the trick
	 * @param card
	 *            The ID of the card that was played
	 */
	public void setTrickCard(int trickNumber, Player player, Card card) {

		log.debug(this + ".setTrickCard(" + trickNumber + ", " + player + ", " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ card + ")"); //$NON-NLS-1$

		switch (player) {

		case FORE_HAND:
			this.tricks.get(trickNumber).setFirstCard(card);
			break;
		case MIDDLE_HAND:
			this.tricks.get(trickNumber).setSecondCard(card);
			break;
		case HIND_HAND:
			this.tricks.get(trickNumber).setThirdCard(card);
			break;
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
	public void setTrickWinner(int trickNumber, Player winner) {

		log.debug("setTrickWinner(" + trickNumber + ", " + winner + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		this.tricks.get(trickNumber).setTrickWinner(winner);
	}

	/**
	 * Gets the winner of the trick
	 * 
	 * @param trickNumber
	 *            The number of the trick in a game
	 * @return The player ID of the trick winner
	 */
	public Player getTrickWinner(int trickNumber) {

		return this.tricks.get(trickNumber).getTrickWinner();

	}

	/**
	 * Gets all tricks
	 * 
	 * @return ArrayList of tricks
	 */
	public List<Trick> getTricks() {

		return this.tricks;
	}

	/**
	 * Gets the number of geschoben
	 * 
	 * @return Returns the number of geschoben
	 */
	public int getGeschoben() {

		return this.geschoben;
	}

	/**
	 * Gets the geschoben multiplier
	 * 
	 * @return Returns the geschoben multiplier
	 */
	public int getGeschobenMultiplier() {

		log.debug("geschoben=" + this.geschoben + ", 2^" + this.geschoben + "=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ (1 << this.geschoben));

		int multiplier = 0;

		if (this.geschoben < 0) {

			multiplier = this.geschoben;

		} else {

			// TODO: need to know what this is doing
			multiplier = (1 << this.geschoben);
		}

		return multiplier;
	}

	/**
	 * Raises the value of geschoben by 1
	 * 
	 */
	public void geschoben() {

		this.geschoben++;
	}

	/**
	 * Get the player cards
	 * 
	 * @param player
	 *            Player
	 * @return CardList of Cards from the player
	 */
	public CardList getPlayerCards(Player player) {

		return this.playerHands.get(player);
	}

	/**
	 * Gets a reference to the skat for the game
	 * 
	 * @return skat The cards of the skat
	 */
	public CardList getSkat() {

		return this.skat;
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

		CardList hand = this.playerHands.get(player);

		// add old skat to player's hand
		hand.add(this.skat.get(0));
		hand.add(this.skat.get(1));

		// clear skat
		this.skat.clear();

		// add new cards to skat
		hand.remove(newSkat.get(0));
		this.skat.add(newSkat.get(0));
		hand.remove(newSkat.get(1));
		this.skat.add(newSkat.get(1));
	}

	/**
	 * Gets the dealt cards
	 * 
	 * @return The dealt cards
	 */
	public Map<Player, CardList> getDealtCards() {

		return this.dealtCards;
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
		this.dealtCards.get(player).add(card);

		// current cards are hold in playerHands and skat
		this.playerHands.get(player).add(card);
	}

	/**
	 * Sets cards for the skat
	 * 
	 * @param card0
	 *            First card
	 * @param card1
	 *            Second card
	 */
	public void setSkatCards(Card card0, Card card1) {

		this.skat.add(card0);
		this.skat.add(card1);
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

		this.playerPoints.put(
				player,
				Integer.valueOf(this.playerPoints.get(player).intValue()
						+ points));
	}

	/**
	 * Gets the points of a player
	 * 
	 * @param player
	 *            Player
	 * @return Points of the player
	 */
	public int getPlayerPoints(Player player) {

		return this.playerPoints.get(player).intValue();
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

		this.playerBids.put(player, Integer.valueOf(bidValue));
	}

	/**
	 * Gets the highest bid value for a player
	 * 
	 * @param player
	 *            Player
	 * @return Highest bid value so far
	 */
	public int getPlayerBid(Player player) {

		return this.playerBids.get(player).intValue();
	}

	/**
	 * Sets the game announcement
	 * 
	 * @param newAnnouncement
	 *            The game announcement
	 */
	public void setAnnouncement(GameAnnouncement newAnnouncement) {

		this.announcement.setGameType(newAnnouncement.getGameType());
		this.announcement.setOuvert(newAnnouncement.isOuvert());
		this.announcement.setSchneider(newAnnouncement.isSchneider());
		this.announcement.setSchwarz(newAnnouncement.isSchwarz());

		this.rules = SkatRuleFactory.getSkatRules(getGameType());

		if (getGameType() == GameType.PASSED_IN) {

			this.gameState = GameState.GAME_OVER;
			this.calcResult();
		}
	}

	/**
	 * Gets the game type
	 * 
	 * @return Game type
	 */
	public GameType getGameType() {

		GameType gameType = null;

		if (this.announcement != null) {
			gameType = this.announcement.getGameType();
		}

		return gameType;
	}

	/**
	 * Gets the game announcement
	 * 
	 * @return The game announcement
	 */
	public GameAnnouncement getAnnoucement() {

		return this.announcement;
	}

	/**
	 * Checks whether the game was played under ISPA rules or not
	 * 
	 * @return TRUE when the game was played under ISPA rules
	 */
	public boolean isIspaRules() {

		return this.ispaRules;
	}

	/**
	 * Sets the flag for ISPA rules
	 * 
	 * @param isIspaRules
	 *            TRUE when the game was played under ISPA rules
	 */
	public void setIspaRules(boolean isIspaRules) {
		this.ispaRules = isIspaRules;
	}

	/**
	 * Calculates the counts of jacks
	 */
	public void calcJackInformation() {

		// FIXME (jansch 09.11.2010) this is code for skat rules
		CardList declarerCards = (CardList) this.playerHands.get(this.declarer)
				.clone();

		declarerCards.add(this.skat.get(0));
		declarerCards.add(this.skat.get(1));

		if (declarerCards.hasJack(Suit.CLUBS)) {

			setClubJack(true);
		}
		if (declarerCards.hasJack(Suit.SPADES)) {

			setSpadeJack(true);
		}
		if (declarerCards.hasJack(Suit.HEARTS)) {

			setHeartJack(true);
		}
		if (declarerCards.hasJack(Suit.DIAMONDS)) {

			this.setDiamondJack(true);
		}
	}

	/**
	 * Sets the game state
	 * 
	 * @param newState
	 *            New game state
	 */
	public void setGameState(GameState newState) {

		this.gameState = newState;
	}

	/**
	 * Gets the game state
	 * 
	 * @return The game state
	 */
	public GameState getGameState() {

		return this.gameState;
	}

	/**
	 * Sets the schneider and schwarz flag according the player points
	 */
	public void setSchneiderSchwarz() {
		// FIXME this is rule logic --> remove it
		int declarerPoints = getPlayerPoints(this.declarer);

		if (declarerPoints >= 89 || declarerPoints <= 30) {

			setSchneider(true);
		}

		if (declarerPoints == 120 || declarerPoints == 0) {

			setSchwarz(true);
		}
	}

	public void setJungfrauDurchmarsch() {
		// FIXME this is rule logic --> remove it
		for (Player currPlayer : Player.values()) {

			int playerPoints = getPlayerPoints(currPlayer);

			if (playerPoints == 0) {

				setJungfrau(true);
			}
			if (playerPoints == 120) {

				setDurchmarsch(true);
			}
		}
	}

	/**
	 * Gets the dealing player
	 * 
	 * @return Dealing player
	 */
	public Player getDealer() {
		return this.dealer;
	}

	/**
	 * Sets the dealer player
	 * 
	 * @param newDealer
	 *            Dealing player
	 */
	public void setDealer(Player newDealer) {
		this.dealer = newDealer;
	}

	/**
	 * Gets the player name
	 * 
	 * @param player
	 *            Player
	 * @return Player name
	 */
	public String getPlayerName(Player player) {
		return this.playerNames.get(player);
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
		this.playerNames.put(player, playerName);
	}
}
