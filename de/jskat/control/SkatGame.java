/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.IJSkatPlayer;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.SkatGameData.GameState;
import de.jskat.data.Trick;
import de.jskat.gui.IJSkatView;
import de.jskat.gui.human.HumanPlayer;
import de.jskat.util.Card;
import de.jskat.util.CardDeck;
import de.jskat.util.CardList;
import de.jskat.util.GameType;
import de.jskat.util.Player;
import de.jskat.util.SkatConstants;
import de.jskat.util.rule.BasicSkatRules;
import de.jskat.util.rule.SkatRuleFactory;

/**
 * Controls a skat game
 */
public class SkatGame extends JSkatThread {

	private static Log log = LogFactory.getLog(SkatGame.class);
	private int maxSleep = 100;
	private SkatGameData data;
	private CardDeck deck;
	private Map<Player, IJSkatPlayer> player;
	private String tableName;
	private IJSkatView view;
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
	 * @param newHindHand
	 *            Hind hand player
	 */
	public SkatGame(String newTableName, IJSkatPlayer newForeHand,
			IJSkatPlayer newMiddleHand, IJSkatPlayer newHindHand) {

		this.tableName = newTableName;
		this.player = new HashMap<Player, IJSkatPlayer>();
		this.player.put(Player.FORE_HAND, newForeHand);
		this.player.put(Player.MIDDLE_HAND, newMiddleHand);
		this.player.put(Player.HIND_HAND, newHindHand);

		// inform all players about the starting of the new game
		for (Player currPlayerPosition : player.keySet()) {
			player.get(currPlayerPosition).newGame(currPlayerPosition);
		}

		this.data = new SkatGameData();
		setGameState(GameState.NEW_GAME);
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {

		this.view.clearTable(this.tableName);
		this.view.setGameState(this.tableName, this.data.getGameState());

		do {

			log.debug("Game state " + this.data.getGameState()); //$NON-NLS-1$

			switch (this.data.getGameState()) {
			case NEW_GAME:
				setGameState(GameState.DEALING);
				break;
			case DEALING:
				dealCards();
				setGameState(GameState.BIDDING);
				break;
			case BIDDING:
				view.setActivePlayer(tableName, Player.MIDDLE_HAND);
				bidding();
				if (this.data.getGameType() == GameType.PASSED_IN) {

					setGameState(GameState.PRELIMINARY_GAME_END);
				} else {

					setGameState(GameState.LOOK_INTO_SKAT);
				}
				break;
			case LOOK_INTO_SKAT:
				if (lookIntoSkat()) {
					setGameState(GameState.DISCARDING);
				} else {
					this.data.setHand(true);
					setGameState(GameState.DECLARING);
				}
				break;
			case DISCARDING:
				discarding();
				setGameState(GameState.DECLARING);
				break;
			case DECLARING:
				announceGame();
				setGameState(GameState.TRICK_PLAYING);
				break;
			case TRICK_PLAYING:
				playTricks();
				setGameState(GameState.CALC_GAME_VALUE);
				break;
			case PRELIMINARY_GAME_END:
				setGameState(GameState.CALC_GAME_VALUE);
				break;
			case CALC_GAME_VALUE:
				calculateGameValue();
				setGameState(GameState.GAME_OVER);
				break;
			case GAME_OVER:
				break;
			}

			checkWaitCondition();
		} while (this.data.getGameState() != GameState.GAME_OVER);

		log.debug(this.data.getGameState());
	}

	private boolean lookIntoSkat() {

		return player.get(data.getDeclarer()).lookIntoSkat();
	}

	/**
	 * Deals the cards to the players and the skat
	 */
	public void dealCards() {

		if (this.deck == null) {
			// Skat game has no cards, yet
			this.deck = new CardDeck();
			log.debug("shuffling..."); //$NON-NLS-1$

			this.deck.shuffle();
			log.debug(this.deck);
		}

		doSleep(this.maxSleep);

		log.debug("dealing..."); //$NON-NLS-1$

		for (int i = 0; i < 3; i++) {
			// deal three rounds of cards
			switch (i) {
			case 0:
				// deal three cards
				dealCards(3);
				// and put two cards into the skat
				this.data
						.setSkatCards(this.deck.remove(0), this.deck.remove(0));
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
		for (Player currPlayer : Player.values()) {

			this.view.addCards(this.tableName, currPlayer,
					dealtCards.get(currPlayer));
		}

		doSleep(this.maxSleep);

		log.debug("Fore hand: " + this.data.getPlayerCards(Player.FORE_HAND)); //$NON-NLS-1$
		log.debug("Middle hand: " //$NON-NLS-1$
				+ this.data.getPlayerCards(Player.MIDDLE_HAND));
		log.debug("Hind hand: " + this.data.getPlayerCards(Player.HIND_HAND)); //$NON-NLS-1$
		log.debug("Skat: " + this.data.getSkat()); //$NON-NLS-1$
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
				Card card = this.deck.remove(0);
				// player can get original card object because Card is immutable
				player.get(hand).takeCard(card);
				this.data.setDealtCard(hand, card);
			}
		}
	}

	/**
	 * Controls the bidding of all players
	 */
	private void bidding() {

		int bidOrderIndex = 0;

		this.data.setBidValue(0);

		log.debug("ask middle and fore hand..."); //$NON-NLS-1$

		bidOrderIndex = twoPlayerBidding(Player.MIDDLE_HAND, Player.FORE_HAND,
				bidOrderIndex);

		log.debug("Bid value after first bidding: " //$NON-NLS-1$
				+ SkatConstants.bidOrder[bidOrderIndex]);

		Player firstWinner = getBiddingWinner(Player.MIDDLE_HAND,
				Player.FORE_HAND);

		log.debug("First bidding winner: " + firstWinner); //$NON-NLS-1$
		log.debug("ask hind hand and first winner..."); //$NON-NLS-1$

		if (this.data.getBidValue() > 0 && firstWinner == Player.MIDDLE_HAND) {
			// Increment bid order index for next bidding phase
			// if at least a bid was announced and middle hand has won the
			// bidding
			bidOrderIndex++;
		}

		bidOrderIndex = twoPlayerBidding(Player.HIND_HAND, firstWinner,
				bidOrderIndex);

		log.debug("Bid value after second bidding: " //$NON-NLS-1$
				+ SkatConstants.bidOrder[bidOrderIndex]);

		// get second winner
		Player secondWinner = getBiddingWinner(Player.HIND_HAND, firstWinner);

		if (secondWinner == Player.FORE_HAND && bidOrderIndex == 0) {

			log.debug("Check whether fore hand holds at least one bid"); //$NON-NLS-1$

			// check whether fore hand holds at least one bid
			if (!(player.get(Player.FORE_HAND).bidMore(18) > -1)) {

				log.debug("Fore hand passes too"); //$NON-NLS-1$
				secondWinner = null;
			} else {

				log.debug("Fore hand holds 18"); //$NON-NLS-1$
			}
		}

		if (secondWinner != null) {
			// there is a winner of the bidding
			setSinglePlayer(getBiddingWinner(Player.HIND_HAND, firstWinner));

			this.data.setBidValue(SkatConstants.bidOrder[bidOrderIndex]);

			log.debug("Player " + this.data.getDeclarer() //$NON-NLS-1$
					+ " wins the bidding."); //$NON-NLS-1$
		} else {
			// pass in
			GameAnnouncement ann = new GameAnnouncement();
			ann.setGameType(GameType.PASSED_IN);
			this.setGameAnnouncement(ann);
		}

		doSleep(this.maxSleep);
	}

	/**
	 * Controls the bidding between two players
	 * 
	 * @param announcer
	 *            Announcing player
	 * @param hearer
	 *            Hearing player
	 * @param startBidOrderIndex
	 *            Index of first bid value from bid order array
	 * @return
	 */
	private int twoPlayerBidding(Player announcer, Player hearer,
			int startBidOrderIndex) {

		int bidOrderIndex = startBidOrderIndex;
		boolean announcerPassed = false;
		boolean hearerPassed = false;

		while (!announcerPassed && !hearerPassed) {

			// get bid value
			int nextBidValue = SkatConstants.bidOrder[bidOrderIndex];
			this.view.setNextBidValue(this.tableName, nextBidValue);
			// ask player
			int announcerBidValue = player.get(announcer).bidMore(nextBidValue);

			if (announcerBidValue > -1) {

				log.debug("announcer holds " + announcerBidValue); //$NON-NLS-1$

				// announcing hand holds bid
				this.data.setBidValue(announcerBidValue);
				this.data.setPlayerBid(announcer, announcerBidValue);
				this.view.setBid(this.tableName, announcer, announcerBidValue,
						true);

				if (nextBidValue < announcerBidValue) {
					// increment bidOrderIndex accordingly
					while (SkatConstants.bidOrder[bidOrderIndex] < announcerBidValue) {

						bidOrderIndex++;
					}
				}

				if (player.get(hearer).holdBid(announcerBidValue)) {

					log.debug("hearer holds " + announcerBidValue); //$NON-NLS-1$

					// hearing hand holds bid
					this.data.setBidValue(announcerBidValue);
					this.data.setPlayerBid(hearer, announcerBidValue);
					this.view.setBid(this.tableName, hearer, announcerBidValue,
							false);

					// raise index to next bid in bid order
					bidOrderIndex++;

					if (bidOrderIndex == SkatConstants.bidOrder.length) {

						// TODO raise appropriate exception here
					}
				} else {

					log.debug("hearer passed at " + announcerBidValue); //$NON-NLS-1$

					// hearing hand passed
					hearerPassed = true;
					view.setPass(tableName, hearer);
				}
			} else {

				log.debug("announcer passed at " + nextBidValue); //$NON-NLS-1$

				// announcing hand passes
				announcerPassed = true;
				view.setPass(tableName, announcer);
			}
		}

		return bidOrderIndex;
	}

	private Player getBiddingWinner(Player announcer, Player hearer) {

		Player biddingWinner = null;

		if (this.data.getPlayerBid(announcer) == this.data.getBidValue()) {

			if (this.data.getPlayerBid(hearer) == this.data.getBidValue()) {

				biddingWinner = hearer;
			} else {

				biddingWinner = announcer;
			}
		} else if (this.data.getPlayerBid(hearer) == this.data.getBidValue()) {

			biddingWinner = hearer;
		}

		return biddingWinner;
	}

	private void discarding() {

		log.debug("Player looks into the skat..."); //$NON-NLS-1$
		log.debug("Skat before discarding: " + this.data.getSkat()); //$NON-NLS-1$

		IJSkatPlayer declarer = player.get(data.getDeclarer());

		// create a clone of the skat before sending it to the player
		// otherwise the player could change the skat after discarding
		declarer.takeSkat((CardList) this.data.getSkat().clone());

		// adjust cards in display too
		this.view.addCard(this.tableName, this.data.getDeclarer(), this.data
				.getSkat().get(0));
		this.view.addCard(this.tableName, this.data.getDeclarer(), this.data
				.getSkat().get(1));

		// ask player for the cards to be discarded
		// cloning is done to prevent the player
		// from manipulating the skat afterwards
		CardList discardedSkat = (CardList) declarer.discardSkat().clone();

		if (!checkDiscardedCards(discardedSkat)) {
			// TODO throw an appropriate exceptions

		}

		log.debug("Discarded cards: " + discardedSkat); //$NON-NLS-1$

		this.data.setDiscardedSkat(this.data.getDeclarer(), discardedSkat);
		this.view.removeCard(this.tableName, this.data.getDeclarer(),
				discardedSkat.get(0));
		this.view.removeCard(this.tableName, this.data.getDeclarer(),
				discardedSkat.get(1));
	}

	private boolean checkDiscardedCards(CardList discardedSkat) {

		// TODO move this to skat rules?
		boolean result = true;

		if (discardedSkat == null) {

			log.error("Player is fooling!!! Skat is empty!"); //$NON-NLS-1$
			result = false;
		} else if (discardedSkat.size() != 2) {

			log.error("Player is fooling!!! Skat doesn't have two cards!"); //$NON-NLS-1$
			result = false;
		}
		// TODO check for jacks in the discarded skat in ramsch games

		return result;
	}

	private void announceGame() {

		log.debug("declaring game..."); //$NON-NLS-1$

		// TODO check for valid game announcements
		try {
			GameAnnouncement ann = (GameAnnouncement) player
					.get(data.getDeclarer()).announceGame().clone();

			setGameAnnouncement(ann);
		} catch (NullPointerException e) {
			// player has not returned an object
			// TODO finish game immediately
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// player has not returned a game announcement
			// TODO finish game immediately
			e.printStackTrace();
		}

		doSleep(this.maxSleep);
	}

	private void playTricks() {

		this.view.clearTrickCards(this.tableName);

		for (int i = 0; i < 10; i++) {

			log.debug("Play trick " + (i + 1)); //$NON-NLS-1$
			doSleep(this.maxSleep);

			Player newTrickForeHand = null;
			if (i == 0) {
				// first trick
				newTrickForeHand = Player.FORE_HAND;
			} else {
				// all the other tricks
				Trick lastTrick = this.data.getTricks().get(
						this.data.getTricks().size() - 1);

				// set new trick fore hand
				newTrickForeHand = lastTrick.getTrickWinner();

				// remove all cards from current trick
				view.clearTrickCards(this.tableName);

				// set last trick cards
				view.setLastTrick(this.tableName, lastTrick.getForeHand(),
						lastTrick.getCard(Player.FORE_HAND),
						lastTrick.getCard(Player.MIDDLE_HAND),
						lastTrick.getCard(Player.HIND_HAND));
			}

			this.view.setTrickForeHand(this.tableName, newTrickForeHand);
			view.setActivePlayer(tableName, newTrickForeHand);

			Trick trick = new Trick(newTrickForeHand);
			data.addTrick(trick);

			// Ask players for their cards
			log.debug("fore hand plays"); //$NON-NLS-1$
			playCard(trick, newTrickForeHand);
			doSleep(this.maxSleep);

			log.debug("middle hand plays"); //$NON-NLS-1$
			view.setActivePlayer(tableName, newTrickForeHand.getLeftNeighbor());
			playCard(trick, newTrickForeHand.getLeftNeighbor());
			doSleep(this.maxSleep);

			log.debug("hind hand plays"); //$NON-NLS-1$
			view.setActivePlayer(tableName, newTrickForeHand.getRightNeighbor());
			playCard(trick, newTrickForeHand.getRightNeighbor());

			doSleep(this.maxSleep);

			log.debug("Calculate trick winner"); //$NON-NLS-1$
			Player trickWinner = rules.calculateTrickWinner(data.getGameType(),
					trick);
			trick.setTrickWinner(trickWinner);
			this.data.getTricks().add(trick);
			this.data.addPlayerPoints(trickWinner, trick.getCardValueSum());

			for (Player currPosition : Player.values()) {
				// inform all players
				// cloning of trick information to prevent manipulation by
				// player
				try {
					player.get(currPosition).showTrick((Trick) trick.clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// Check for preliminary ending of a null game
			if (GameType.NULL.equals(data.getGameType())) {

				if (trickWinner == this.data.getDeclarer()) {
					// declarer has won a trick
					setGameState(GameState.PRELIMINARY_GAME_END);
				}
			}

			doSleep(this.maxSleep);

			log.debug("Trick cards: " + trick.getCardList()); //$NON-NLS-1$
			log.debug("Points: fore hand: " + this.data.getPlayerPoints(Player.FORE_HAND) + //$NON-NLS-1$
					" middle hand: " //$NON-NLS-1$
					+ this.data.getPlayerPoints(Player.MIDDLE_HAND)
					+ " hind hand: " //$NON-NLS-1$
					+ this.data.getPlayerPoints(Player.HIND_HAND));

			if (isFinished()) {
				break;
			}

			checkWaitCondition();
		}

		if (this.data.getGameType() == GameType.RAMSCH) {
			// TODO give the card points of the skat to a player defined in
			// ramsch rules
		} else {
			// for all the other games, points to the declarer
			this.data.addPlayerPoints(this.data.getDeclarer(), this.data
					.getSkat().getCardValueSum());
		}

		// set schneider/schwarz/jungfrau/durchmarsch flags
		switch (this.data.getGameType()) {
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
		case GRAND:
			this.data.setSchneiderSchwarz();
			break;
		case RAMSCH:
			this.data.setJungfrauDurchmarsch();
			break;
		case NULL:
		case PASSED_IN:
			// do nothing
			break;
		}
	}

	private void playCard(Trick trick, Player currPlayer) {

		Card card = null;
		IJSkatPlayer skatPlayer = getPlayerObject(currPlayer);

		boolean isCardAccepted = false;
		while (!isCardAccepted) {
			// ask player for the next card
			card = skatPlayer.playCard();

			log.debug(card + " " + this.data); //$NON-NLS-1$

			if (card == null) {

				log.error("Player is fooling!!! Did not play a card!"); //$NON-NLS-1$

			} else if (!playerHasCard(currPlayer, card)) {

				log.error("Player is fooling!!! Doesn't have card " + card + "!"); //$NON-NLS-1$//$NON-NLS-2$

			} else if (!this.rules.isCardAllowed(this.data.getGameType(),
					trick.getFirstCard(), this.data.getPlayerCards(currPlayer),
					card)) {

				log.debug("card not allowed: " + card + " game type: "
						+ data.getGameType() + " first trick card: "
						+ trick.getFirstCard() + " player cards: "
						+ this.data.getPlayerCards(currPlayer));

				this.view.showMessage(JOptionPane.INFORMATION_MESSAGE,
						"Card " + card + " is not allowed!"); //$NON-NLS-1$ //$NON-NLS-2$

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
		view.playTrickCard(this.tableName, currPlayer, card);

		for (Player currPosition : Player.values()) {
			// inform all players
			// cloning of card is not neccessary, because Card is immutable
			player.get(currPosition).cardPlayed(currPlayer, card);
		}

		log.debug("playing card " + card); //$NON-NLS-1$
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

		log.debug("Player cards: " + this.data.getPlayerCards(skatPlayer)); //$NON-NLS-1$

		for (Card handCard : this.data.getPlayerCards(skatPlayer)) {

			if (handCard.equals(card)) {

				result = true;
			}
		}

		return result;
	}

	private boolean isFinished() {

		return this.data.getGameState() == GameState.PRELIMINARY_GAME_END
				|| this.data.getGameState() == GameState.GAME_OVER;
	}

	private void calculateGameValue() {

		log.debug("Calculate game value"); //$NON-NLS-1$

		this.data.calcResult();

		log.debug("game value=" + this.data.getResult() + ", bid value=" //$NON-NLS-1$ //$NON-NLS-2$
				+ this.data.getBidValue());

		if (this.data.isGameWon()
				&& this.data.getBidValue() > this.data.getGameResult()) {

			log.debug("Overbidding: Game is lost"); //$NON-NLS-1$
			// Game was overbidded
			// game is lost despite the winning of the single player
			this.data.setOverBidded(true);
		}

		log.debug("Final game result: lost:" + this.data.isGameLost() + //$NON-NLS-1$
				" game value: " + this.data.getResult()); //$NON-NLS-1$

		for (IJSkatPlayer currPlayer : player.values()) {
			// no cloning neccessary because all parameters are primitive data
			// types
			currPlayer.setGameResult(this.data.isGameWon(),
					this.data.getGameResult());
			currPlayer.finalizeGame();
		}

		doSleep(this.maxSleep);
	}

	private void doSleep(int milliseconds) {

		try {
			sleep(milliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets the view for the game
	 * 
	 * @param newView
	 */
	public void setView(IJSkatView newView) {

		this.view = newView;
	}

	/**
	 * Sets the cards from outside
	 * 
	 * @param newDeck
	 *            Card deck
	 */
	public void setCardDeck(CardDeck newDeck) {

		this.deck = newDeck;
	}

	/**
	 * Sets the game announcement from the outside
	 * 
	 * @param ann
	 *            Game announcement
	 */
	public void setGameAnnouncement(GameAnnouncement ann) {

		this.data.setAnnouncement(ann);
		this.rules = SkatRuleFactory.getSkatRules(this.data.getGameType());

		// inform all players
		for (IJSkatPlayer currPlayer : player.values()) {
			// no cloning neccessary, because all parameters are primitive data
			// types
			currPlayer.startGame(this.data.getDeclarer(),
					this.data.getGameType(), this.data.isHand(),
					this.data.isOuvert(), this.data.isSchneiderAnnounced(),
					this.data.isSchwarzAnnounced());
		}

		this.view.setGameAnnouncement(this.tableName, ann);

		log.debug(this.data.getAnnoucement());
	}

	/**
	 * Sets the game state from outside
	 * 
	 * @param newState
	 *            Game state
	 */
	public void setGameState(GameState newState) {

		this.data.setGameState(newState);

		if (this.view != null) {

			this.view.setGameState(this.tableName, newState);

			if (newState == GameState.NEW_GAME) {

				this.view.clearTable(this.tableName);

			} else if (newState == GameState.GAME_OVER) {

				this.view.addGameResult(this.tableName, this.data);
			}
		}
	}

	/**
	 * Sets the single player from outside
	 * 
	 * @param singlePlayer
	 *            Single player
	 */
	public void setSinglePlayer(Player singlePlayer) {

		this.data.setDeclarer(singlePlayer);
	}

	/**
	 * Gets whether a game was won or not
	 * 
	 * @return TRUE if the game was won
	 */
	public boolean isGameWon() {

		return this.data.isGameWon();
	}

	/**
	 * Gets the maximum sleep time
	 * 
	 * @return Maximum sleep time
	 */
	public int getMaxSleep() {

		return this.maxSleep;
	}

	/**
	 * Sets the maximum sleep time
	 * 
	 * @param newMaxSleep
	 *            Maximum sleep time
	 */
	public void setMaxSleep(int newMaxSleep) {

		this.maxSleep = newMaxSleep;
	}

	/**
	 * Gets the game result
	 * 
	 * @return Game result
	 */
	public int getGameResult() {

		return this.data.getGameResult();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return this.data.getGameState().toString();
	}
}
