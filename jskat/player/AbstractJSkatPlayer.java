/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package jskat.player;

import jskat.share.CardVector;
import jskat.share.Card;
import jskat.share.SkatConstants;
import jskat.share.rules.SkatRules;
import jskat.share.rules.SkatRulesFactory;

/**
 * The JSkat Player implementation
 */
public abstract class AbstractJSkatPlayer implements JSkatPlayer {

	/**
	 * @see jskat.player.JSkatPlayer#getPlayerID()
	 */
	public final int getPlayerID() {

		return playerID;
	}

	/**
	 * @see jskat.player.JSkatPlayer#setPlayerID(int)
	 */
	public final void setPlayerID(int newPlayerID) {
		playerID = newPlayerID;
	}

	/**
	 * @see jskat.player.JSkatPlayer#setPlayerName(java.lang.String)
	 */
	public final void setPlayerName(String newPlayerName) {

		playerName = newPlayerName;
	}

	/**
	 * @see jskat.player.JSkatPlayer#getPlayerName()
	 */
	public final String getPlayerName() {

		return playerName;
	}

	/**
	 * @see jskat.player.JSkatPlayer#newGame()
	 */
	public final void newGame() {

		cards.clear();
		skat.clear();
	}

	/**
	 * @see jskat.player.JSkatPlayer#takeCard(jskat.share.Card)
	 */
	public final void takeCard(Card newCard) {

		cards.add(newCard);

		// TODO: Do sorting only on demand
		if (cards.size() == 10) {
			sortCards(SkatConstants.GameTypes.SUIT);
		}
	}

	/**
	 * Sorts the card according a game type
	 * 
	 * @param gameType Game type
	 */
	protected final void sortCards(SkatConstants.GameTypes gameType) {

		cards.sort(gameType);
	}

	/**
	 * @see jskat.player.JSkatPlayer#startGame(int, int, jskat.share.SkatConstants.GameTypes, jskat.share.SkatConstants.Suits, boolean, boolean)
	 */
	public void startGame(int singlePlayer, int forehandPlayer,
			SkatConstants.GameTypes gameType, SkatConstants.Suits trump,
			boolean handGame, boolean ouvertGame) {

		this.playerState = JSkatPlayer.PlayerStates.PLAYING;
		this.singlePlayer = singlePlayer;
		this.forehandPlayer = forehandPlayer;
		this.gameType = gameType;
		this.trump = trump;
		this.handGame = handGame;
		this.ouvertGame = ouvertGame;

		this.rules = SkatRulesFactory.getSkatRules(gameType);
		
		// TODO reimplement sorting
		// cards.sortWithTrump((currGameType==SkatConstants.NULL ?
		// SkatConstants.NULL_SORT : SkatConstants.SUIT_SORT), currTrump);
	}

	/**
	 * Sets the state of the player
	 * 
	 * @param newState State to be set
	 */
	protected void setState(JSkatPlayer.PlayerStates newState) {

		playerState = newState;
	}
	
	/* Variables */
	/** Player ID */
	protected int playerID;
	/** ID of the single player (declaring player) */
	protected int singlePlayer;
	/** ID of the forehand player */
	protected int forehandPlayer;
	/** Player name */
	protected String playerName;
	/** Player state */
	protected JSkatPlayer.PlayerStates playerState;
	/** Player cards */
	protected CardVector cards = new CardVector();
	/** Skat cards */
	protected CardVector skat = new CardVector();
	/** Cards of the single player */
	protected CardVector singlePlayerCards = new CardVector();
	/** Game type of the current game */
	protected SkatConstants.GameTypes gameType;
	/** Trump of the current game */
	protected SkatConstants.Suits trump;
	/** Flag for hand game */
	protected boolean handGame;
	/** Flag for ouvert game */
	protected boolean ouvertGame;
	/** Skat rules for the current skat series */
	protected SkatRules rules;
}