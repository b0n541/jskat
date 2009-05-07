/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.Trick;
import de.jskat.util.CardList;
import de.jskat.util.Card;
import de.jskat.util.GameType;
import de.jskat.util.Player;
import de.jskat.util.SkatConstants;
import de.jskat.util.rule.BasicSkatRules;
import de.jskat.util.rule.SkatRuleFactory;

/**
 * The JSkat Player implementation
 */
public abstract class AbstractJSkatPlayer implements JSkatPlayer {

	private static Log log = LogFactory.getLog(AbstractJSkatPlayer.class);

	/** ID of the single player (declaring player) */
	protected Player singlePlayer;
	/** Player name */
	protected String playerName;
	/** Player state */
	protected JSkatPlayer.PlayerStates playerState;
	/** Player knowledge */
	protected PlayerKnowledge knowledge = new PlayerKnowledge();
	/** Player cards */
	protected CardList cards = new CardList();
	/** Skat cards */
	protected CardList skat = new CardList();
	/** Cards of the single player */
	protected CardList singlePlayerCards = new CardList();
	/** Game type of the current game */
	protected GameType gameType;
	/** Flag for hand game */
	protected boolean handGame;
	/** Flag for ouvert game */
	protected boolean ouvertGame;
	/** Flag for schneider announced */
	protected boolean schneiderAnnounced;
	/** Flag for schwarz announced */
	protected boolean schwarzAnnounced;
	/** Skat rules for the current skat series */
	protected BasicSkatRules rules;
	/** Result of the skat game */
	protected boolean gameWon;
	/** Game value */
	protected int gameValue;

	/**
	 * @see JSkatPlayer#setPlayerName(java.lang.String)
	 */
	public final void setPlayerName(String newPlayerName) {

		this.playerName = newPlayerName;
	}

	/**
	 * @see JSkatPlayer#getPlayerName()
	 */
	public final String getPlayerName() {

		return this.playerName;
	}

	/**
	 * @see JSkatPlayer#setUpBidding()
	 */
	public final void setUpBidding() {

		setState(PlayerStates.BIDDING);
	}

	/**
	 * @see JSkatPlayer#newGame(Player)
	 */
	public final void newGame(Player newPosition) {

		this.cards.clear();
		this.skat.clear();
		this.gameType = null;
		this.handGame = false;
		this.ouvertGame = false;
		this.playerState = null;
		this.singlePlayer = null;
		this.rules = null;
		this.schneiderAnnounced = false;
		this.schwarzAnnounced = false;
		this.singlePlayer = null;
		this.gameWon = false;
		this.gameValue = 0;
		this.knowledge.initializeVariables();
		this.knowledge.setPlayerPosition(newPosition);
		
		preparateForNewGame();
	}

	/**
	 * @see JSkatPlayer#takeCard(Card)
	 */
	public final void takeCard(Card newCard) {

		this.cards.add(newCard);
		this.knowledge.addCard(newCard);
	}

	/**
	 * Sorts the card according a game type
	 * 
	 * @param sortGameType Game type
	 */
	protected final void sortCards(GameType sortGameType) {

		this.cards.sort(sortGameType);
	}

	/**
	 * @see JSkatPlayer#startGame(Player, GameType, boolean, boolean, boolean, boolean)
	 */
	public final void startGame(Player newSinglePlayer, GameType newGameType, 
							boolean newHandGame, boolean newOuvertGame,
							boolean newSchneiderAnnounced, boolean newSchwarzAnnounced) {

		this.playerState = PlayerStates.PLAYING;
		this.singlePlayer = newSinglePlayer;
		this.gameType = newGameType;
		this.handGame = newHandGame;
		this.ouvertGame = newOuvertGame;
		this.schneiderAnnounced = newSchneiderAnnounced;
		this.schwarzAnnounced = newSchwarzAnnounced;
		this.gameWon = false;
		this.gameValue = 0;

		this.rules = SkatRuleFactory.getSkatRules(this.gameType);
	}

	/**
	 * @see JSkatPlayer#takeSkat(CardList)
	 */
	public final void takeSkat(CardList skatCards) {
		
		log.debug("Skat cards: " + skatCards); //$NON-NLS-1$
		
		this.skat = skatCards;
		this.cards.addAll(skatCards);
	}
	
	/**
	 * Sets the state of the player
	 * 
	 * @param newState State to be set
	 */
	protected final void setState(JSkatPlayer.PlayerStates newState) {

		this.playerState = newState;
	}
	
	/**
	 * @see JSkatPlayer#bidByPlayer(Player, int)
	 */
	public final void bidByPlayer(Player player, int bidValue) {
		
		this.knowledge.setHighestBid(player, bidValue);
	}

	/**
	 * Gets all playable cards
	 * 
	 * @param trick
	 * @return CardList with all playable cards
	 */
	protected final CardList getPlayableCards(CardList trick) {
		
		boolean isCardAllowed = false;
		CardList result = new CardList();
		
		log.debug("game type: " + this.gameType); //$NON-NLS-1$
		log.debug("player cards (" + this.cards.size() + "): " + this.cards); //$NON-NLS-1$ //$NON-NLS-2$
		log.debug("trick size: " + trick.size()); //$NON-NLS-1$
			
		for (Card card : this.cards) {
			
			if (trick.size() > 0 &&
				this.rules.isCardAllowed(this.gameType, trick.get(0), 
												this.cards, card)) {
				
				log.debug("initial card: " + trick.get(0)); //$NON-NLS-1$
				isCardAllowed = true;
			}
			else if (trick.size() == 0) {
				
				isCardAllowed = true;
			}
			else {
				
				isCardAllowed = false;
			}
			
			if (isCardAllowed) {
				
				result.add(card);
			}
		}
		
		return result;
	}
	
	/**
	 * @see JSkatPlayer#cardPlayed(Player, Card)
	 */
	public final void cardPlayed(Player player, Card card) {
		
		this.knowledge.setCardPlayed(player, card);
		
		if (player == this.knowledge.getPlayerPosition()) {
			// remove this card from counter
			this.knowledge.removeCard(card);
			this.cards.remove(card);
		}
	}

	/**
	 * @see JSkatPlayer#showTrick(Trick)
	 */
	public final void showTrick(Trick trick) {

		this.knowledge.addTrick(trick);
		this.knowledge.clearTrickCards();
	}

	/**
	 * @see JSkatPlayer#isHumanPlayer()
	 */
	public final boolean isHumanPlayer() {

		return !isAIPlayer();
	}

	/**
	 * @see JSkatPlayer#isDeclarer()
	 */
	public final boolean isDeclarer() {
		
		boolean result = false;
		
		if (this.singlePlayer == this.knowledge.getPlayerPosition()) {
			
			result = true;
		}
		
		return result;
	}
	
	/**
	 * @see JSkatPlayer#getOuvertCards(CardList)
	 */
	public final void getOuvertCards(CardList ouvertCards) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see JSkatPlayer#setGameResult(boolean, int)
	 */
	@Override
	public void setGameResult(boolean pGameWon, int pGameValue) {
		
		this.gameWon = pGameWon;
		this.gameValue = pGameValue;
	}
}