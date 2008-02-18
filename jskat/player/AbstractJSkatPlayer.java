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
 * 
 * @author Jan Schäfer
 */
public abstract class AbstractJSkatPlayer implements JSkatPlayer {

	static Logger log = Logger.getLogger(AbstractJSkatPlayer.class);

	/** Creates a new instance of SkatPlayer */
	public AbstractJSkatPlayer() {

		this.playerID = -1;
		this.playerName = "AI Player";
		this.playerState = JSkatPlayer.PlayerStates.WAITING;
		this.cards = new CardVector();
		this.skat = new CardVector();
	}

	/** Returns the ID of the SkatPlayer */
	public final int getPlayerID() {

		return playerID;
	}

	/** Returns the ID of the SkatPlayer */
	public final void setPlayerID(int newPlayerID) {
		playerID = newPlayerID;
	}

	/** Sets the name of the SkatPlayer */
	public final void setPlayerName(String newPlayerName) {

		playerName = newPlayerName;
	}

	/** Returns the name of the SkatPlayer */
	public final String getPlayerName() {

		return playerName;
	}

	/** Take a card from someone */
	public final void takeCard(Card newCard) {

		cards.add(newCard);

		// TODO: Do sorting only on demand
		if (cards.size() == 10)
			sortCards(SkatConstants.GameTypes.SUIT);
	}

	protected final void sortCards(SkatConstants.GameTypes gameType) {

		cards.sort(gameType);
	}

	/** Notifies the player of the start of bidding for a new game */
	public void setUpBidding(int initialForehandPlayer) {
	}

	/** Take the skat */
	public void takeSkat(CardVector skat) {
	}

	/** Start the game: inform player of game type, trumpf and special options */
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

	/**
	 * Shows the cards of the single player to the opponents in ouvert games
	 * 
	 * @param ouvertCards
	 *            The cards of the single player
	 */
	public void discloseOuvertCards(CardVector ouvertCards) {

		singlePlayerCards = ouvertCards;
	}

	/**
	 * Makes the current trick known to the players when it is complete
	 * 
	 * @param trick
	 *            The CardVector of the current trick
	 * @param trickWinner
	 *            The trick winner
	 */
	public abstract void showTrick(CardVector trick, int trickWinner);

	/**
	 * Returns a reference to the CardVector of the SkatPlayer
	 * 
	 * This won't be needed anymore when bidding and game announcing is fully
	 * implemented (therefore defined as deprecated)
	 * 
	 * @deprecated
	 */
	public CardVector getCardVector() {

		return cards;
	}

	protected void setState(JSkatPlayer.PlayerStates newState) {

		playerState = newState;
	}

	/**
	 * Get the selected card from the player's hand
	 * 
	 * @see jskat.player.JSkatPlayer#removeCard(int)
	 */
	public final Card removeCard(int index) {
		return cards.remove(index);
	}

	/**
	 * Get the selected card from the player's hand
	 * 
	 * This method should not be used anymore, player has to remove cards for
	 * their own after playing them out
	 * 
	 * @deprecated
	 * @see jskat.player.JSkatPlayer#removeCard(jskat.share.Card)
	 */
	public final Card removeCard(Card card) {

		return cards.remove(card.getSuit(), card.getRank());
	}

	/**
	 * Get the selected card from the player's hand
	 * 
	 * This method should not be used anymore, player has to remove cards for
	 * their own after playing them out
	 * 
	 * @deprecated
	 * @see jskat.player.JSkatPlayer#removeCard(int, int)
	 */
	public Card removeCard(SkatConstants.Suits suit, SkatConstants.Ranks rank) {

		return cards.remove(suit, rank);
	}

	/**
	 * Get the selected card from the player's hand
	 * 
	 * This method should not be used anymore
	 * 
	 * @deprecated
	 */
	// FIXME (mjl) getCard should not remove a card (inconsistent
	// implementation)
	public final Card getCard(SkatConstants.Suits suit, SkatConstants.Ranks rank) {

		int i = 0;

		while (cards.getCard(i).getSuit() != suit
				|| cards.getCard(i).getRank() != rank) {

			i++;
		}

		Card removedCard = cards.remove(i);

		// --[DEBUG]--//
		log.debug("Player " + playerID + ": " + removedCard.toString());
		// --[DEBUG]--//

		return removedCard;
	}

	/**
	 * Only for satisfying the JSkatPlayer interface
	 * 
	 * @deprecated
	 */
	public int playNextCard(CardVector trick) {

		return -1;
	}

	public void cardPlayed(Card card) {

		cards.remove(card);
	}

	public final void newGame() {

		cards.clear();
		skat.clear();
	}

	/** Variables */
	protected int playerID;
	protected int singlePlayer;
	protected int forehandPlayer;
	protected String playerName;
	protected JSkatPlayer.PlayerStates playerState;
	protected CardVector cards;
	protected CardVector skat;
	protected CardVector singlePlayerCards;
	protected SkatConstants.GameTypes currGameType;
	protected SkatConstants.Suits currTrump;
	protected boolean handGame;
	protected boolean ouvertGame;
	protected SkatRules rules;
}