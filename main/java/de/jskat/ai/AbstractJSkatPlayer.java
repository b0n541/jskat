/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.ai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.data.GameAnnouncement;
import de.jskat.data.Trick;
import de.jskat.util.Card;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;
import de.jskat.util.rule.BasicSkatRules;
import de.jskat.util.rule.SkatRuleFactory;

/**
 * The JSkat Player implementation
 */
public abstract class AbstractJSkatPlayer implements IJSkatPlayer {

	private static Log log = LogFactory.getLog(AbstractJSkatPlayer.class);

	/** Player name */
	protected String playerName;
	/** Player state */
	protected IJSkatPlayer.PlayerStates playerState;
	/** Player knowledge */
	protected PlayerKnowledge knowledge = new PlayerKnowledge();
	/** Player cards */
	protected CardList cards = new CardList();
	/** Skat cards */
	protected CardList skat = new CardList();
	/** Cards of the single player */
	protected CardList singlePlayerCards = new CardList();
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
	 * @see IJSkatPlayer#setPlayerName(java.lang.String)
	 */
	public final void setPlayerName(String newPlayerName) {

		playerName = newPlayerName;
	}

	/**
	 * @see IJSkatPlayer#getPlayerName()
	 */
	public final String getPlayerName() {

		return playerName;
	}

	/**
	 * @see IJSkatPlayer#setUpBidding()
	 */
	public final void setUpBidding() {

		setState(PlayerStates.BIDDING);
	}

	/**
	 * @see IJSkatPlayer#newGame(Player)
	 */
	public final void newGame(Player newPosition) {

		cards.clear();
		skat.clear();
		handGame = false;
		ouvertGame = false;
		playerState = null;
		rules = null;
		schneiderAnnounced = false;
		schwarzAnnounced = false;
		gameWon = false;
		gameValue = 0;
		knowledge.initializeVariables();
		knowledge.setPlayerPosition(newPosition);

		preparateForNewGame();
	}

	/**
	 * @see IJSkatPlayer#takeCard(Card)
	 */
	public final void takeCard(Card newCard) {

		cards.add(newCard);
		knowledge.addCard(newCard);
	}

	/**
	 * Sorts the card according a game type
	 * 
	 * @param sortGameType
	 *            Game type
	 */
	protected final void sortCards(GameType sortGameType) {

		cards.sort(sortGameType);
	}

	/**
	 * @see IJSkatPlayer#startGame(Player, GameType, boolean, boolean, boolean,
	 *      boolean)
	 */
	public final void startGame(Player newDeclarer, GameType newGameType, boolean newHandGame, boolean newOuvertGame,
			boolean newSchneiderAnnounced, boolean newSchwarzAnnounced) {

		playerState = PlayerStates.PLAYING;
		knowledge.setDeclarer(newDeclarer);
		GameAnnouncement announcement = new GameAnnouncement();
		announcement.setGameType(newGameType);
		announcement.setHand(newHandGame);
		announcement.setOuvert(newOuvertGame);
		announcement.setSchneider(newSchneiderAnnounced);
		announcement.setSchwarz(newSchwarzAnnounced);
		knowledge.setGame(announcement);
		handGame = newHandGame;
		ouvertGame = newOuvertGame;
		schneiderAnnounced = newSchneiderAnnounced;
		schwarzAnnounced = newSchwarzAnnounced;
		gameWon = false;
		gameValue = 0;

		rules = SkatRuleFactory.getSkatRules(newGameType);

		startGame();
	}

	/**
	 * does certain startGame operations
	 * 
	 * A method that is called by the abstract player to allow individual
	 * players to implement certain start-up operations
	 */
	public abstract void startGame();

	/**
	 * @see IJSkatPlayer#takeSkat(CardList)
	 */
	public final void takeSkat(CardList skatCards) {

		log.debug("Skat cards: " + skatCards); //$NON-NLS-1$

		skat = skatCards;
		cards.addAll(skatCards);
	}

	/**
	 * Sets the state of the player
	 * 
	 * @param newState
	 *            State to be set
	 */
	protected final void setState(IJSkatPlayer.PlayerStates newState) {

		playerState = newState;
	}

	/**
	 * @see IJSkatPlayer#bidByPlayer(Player, int)
	 */
	@Override
	public final void bidByPlayer(Player player, int bidValue) {

		knowledge.setHighestBid(player, bidValue);
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

		log.debug("game type: " + knowledge.getGame().getGameType()); //$NON-NLS-1$
		log.debug("player cards (" + cards.size() + "): " + cards); //$NON-NLS-1$ //$NON-NLS-2$
		log.debug("trick size: " + trick.size()); //$NON-NLS-1$

		for (Card card : cards) {

			if (trick.size() > 0 && rules.isCardAllowed(knowledge.getGame().getGameType(), trick.get(0), cards, card)) {

				log.debug("initial card: " + trick.get(0)); //$NON-NLS-1$
				isCardAllowed = true;
			} else if (trick.size() == 0) {

				isCardAllowed = true;
			} else {

				isCardAllowed = false;
			}

			if (isCardAllowed) {

				result.add(card);
			}
		}

		return result;
	}

	/**
	 * @see IJSkatPlayer#cardPlayed(Player, Card)
	 */
	public final void cardPlayed(Player player, Card card) {

		knowledge.setCardPlayed(player, card);

		if (player == knowledge.getPlayerPosition()) {
			// remove this card from counter
			knowledge.removeCard(card);
			cards.remove(card);
		}
	}

	/**
	 * @see IJSkatPlayer#showTrick(Trick)
	 */
	public final void showTrick(Trick trick) {

		knowledge.addTrick(trick);
		knowledge.clearTrickCards();
	}

	/**
	 * @see IJSkatPlayer#isHumanPlayer()
	 */
	public final boolean isHumanPlayer() {

		return !isAIPlayer();
	}

	/**
	 * @see IJSkatPlayer#isDeclarer()
	 */
	public final boolean isDeclarer() {

		boolean result = false;

		if (knowledge.getDeclarer().equals(knowledge.getPlayerPosition())) {

			result = true;
		}

		return result;
	}

	/**
	 * @see IJSkatPlayer#lookAtOuvertCards(CardList)
	 */
	public final void lookAtOuvertCards(CardList ouvertCards) {

		singlePlayerCards.addAll(ouvertCards);
	}

	/**
	 * @see IJSkatPlayer#setGameResult(boolean, int)
	 */
	@Override
	public void setGameResult(boolean pGameWon, int pGameValue) {

		gameWon = pGameWon;
		gameValue = pGameValue;
	}
}