/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package jskat.player;

import org.apache.log4j.Logger;

import jskat.share.CardVector;
import jskat.share.Card;
import jskat.share.SkatConstants;
import jskat.share.rules.SkatRules;
import jskat.share.rules.SkatRulesFactory;

/**
 * The JSkat Player implementation
 */
public abstract class AbstractJSkatPlayer implements JSkatPlayer {

	static Logger log = Logger.getLogger(AbstractJSkatPlayer.class);

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
		this.currGameType = gameType;
		this.currTrump = trump;
		this.handGame = handGame;
		this.ouvertGame = ouvertGame;

		this.rules = SkatRulesFactory.getSkatRules(gameType);
		
		// TODO reimplement sorting
		// cards.sortWithTrump((currGameType==SkatConstants.NULL ?
		// SkatConstants.NULL_SORT : SkatConstants.SUIT_SORT), currTrump);
	}

	protected void setState(JSkatPlayer.PlayerStates newState) {

		playerState = newState;
	}
	
	/** Variables */
	protected int playerID;
	protected int singlePlayer;
	protected int forehandPlayer;
	protected String playerName;
	protected JSkatPlayer.PlayerStates playerState;
	protected CardVector cards = new CardVector();
	protected CardVector skat = new CardVector();
	protected CardVector singlePlayerCards = new CardVector();
	protected SkatConstants.GameTypes currGameType;
	protected SkatConstants.Suits currTrump;
	protected boolean handGame;
	protected boolean ouvertGame;
	protected SkatRules rules;
}