/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jskat.ai.JSkatPlayer;
import de.jskat.data.GameAnnouncement;
import de.jskat.data.SkatGameData;
import de.jskat.data.Trick;
import de.jskat.data.SkatGameData.GameState;
import de.jskat.gui.JSkatView;
import de.jskat.util.CardDeck;
import de.jskat.util.CardList;
import de.jskat.util.Card;
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
	private JSkatPlayer foreHand;
	private JSkatPlayer middleHand;
	private JSkatPlayer hindHand;
	private Player trickForeHand;
	private JSkatPlayer[] player;
	private JSkatPlayer declarer;
	private String tableName;
	private JSkatView view;
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
	public SkatGame(String newTableName, JSkatPlayer newForeHand,
			JSkatPlayer newMiddleHand, JSkatPlayer newHindHand) {

		this.tableName = newTableName;
		this.foreHand = newForeHand;
		this.middleHand = newMiddleHand;
		this.hindHand = newHindHand;
		this.player = new JSkatPlayer[3];
		this.player[0] = this.foreHand;
		this.player[1] = this.middleHand;
		this.player[2] = this.hindHand;

		// inform all players about the starting of the new game
		this.player[0].newGame(Player.FORE_HAND);
		this.player[1].newGame(Player.MIDDLE_HAND);
		this.player[2].newGame(Player.HIND_HAND);

		this.data = new SkatGameData();
		setGameState(GameState.NEW_GAME);
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {

		this.view.clearTable(this.tableName);

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

		return this.declarer.lookIntoSkat();
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
				this.player[hand.getOrder()].takeCard(card);
				this.data.setDealtCard(hand, card);
				this.view.addCard(this.tableName, hand, card);
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
			if (!(this.player[Player.FORE_HAND.getOrder()].bidMore(18) > -1)) {

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
			int announcerBidValue = this.player[announcer.getOrder()]
					.bidMore(nextBidValue);

			if (announcerBidValue > -1) {

				log.debug("announcer holds " + announcerBidValue); //$NON-NLS-1$

				// announcing hand holds bid
				this.data.setBidValue(announcerBidValue);
				this.data.setPlayerBid(announcer, announcerBidValue);
				this.view.setBid(this.tableName, announcer, announcerBidValue);

				if (nextBidValue < announcerBidValue) {
					// increment bidOrderIndex accordingly
					while (SkatConstants.bidOrder[bidOrderIndex] < announcerBidValue) {

						bidOrderIndex++;
					}
				}

				if (this.player[hearer.getOrder()].holdBid(announcerBidValue)) {

					log.debug("hearer holds " + announcerBidValue); //$NON-NLS-1$

					// hearing hand holds bid
					this.data.setBidValue(announcerBidValue);
					this.data.setPlayerBid(hearer, announcerBidValue);
					this.view.setBid(this.tableName, hearer, announcerBidValue);

					// raise index to next bid in bid order
					bidOrderIndex++;

					if (bidOrderIndex == SkatConstants.bidOrder.length) {

						// TODO raise appropriate exception here
					}
				} else {

					log.debug("hearer passed at " + announcerBidValue); //$NON-NLS-1$

					// hearing hand passed
					hearerPassed = true;
				}
			} else {

				log.debug("announcer passed at " + nextBidValue); //$NON-NLS-1$

				// announcing hand passes
				announcerPassed = true;
			}
		}

		return bidOrderIndex;
	}

	private Player getBiddingWinner(Player announcer, Player hearer) {

		Player biddingWinner = null;

		if (this.data.getPlayerBid(announcer.getOrder()) == this.data
				.getBidValue()) {

			if (this.data.getPlayerBid(hearer.getOrder()) == this.data
					.getBidValue()) {

				biddingWinner = hearer;
			} else {

				biddingWinner = announcer;
			}
		} else if (this.data.getPlayerBid(hearer.getOrder()) == this.data
				.getBidValue()) {

			biddingWinner = hearer;
		}

		return biddingWinner;
	}

	private void discarding() {

		log.debug("Player looks into the skat..."); //$NON-NLS-1$
		log.debug("Skat before discarding: " + this.data.getSkat()); //$NON-NLS-1$

		// create a clone of the skat before sending it to the player
		// otherwise the player could change the skat after discarding
		this.declarer.takeSkat((CardList) this.data.getSkat().clone());

		// adjust cards in display too
		this.view.addCard(this.tableName, this.data.getDeclarer(), this.data
				.getSkat().get(0));
		this.view.addCard(this.tableName, this.data.getDeclarer(), this.data
				.getSkat().get(1));

		// ask player for the cards to be discarded
		// cloning is done to prevent the player
		// from manipulating the skat afterwards
		CardList discardedSkat = (CardList) this.declarer.discardSkat().clone();

		if (!checkDiscardedCards(discardedSkat)) {
			// TODO throw an appropriate exceptions

		}

		log.debug("Discarded cards: " + discardedSkat); //$NON-NLS-1$

		this.data.setDiscardedSkat(getPlayerID(this.declarer), discardedSkat);
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

			GameAnnouncement ann = (GameAnnouncement) this.declarer
					.announceGame().clone();

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

			if (i == 0) {
				// first trick
				this.trickForeHand = Player.FORE_HAND;
			} else {
				// all the other tricks
				// set last trick cards
				Trick lastTrick = this.data.getTricks().get(
						this.data.getTricks().size() - 1);
				this.view.setLastTrick(this.tableName, this.trickForeHand,
						lastTrick.getCard(Player.FORE_HAND), lastTrick
								.getCard(Player.MIDDLE_HAND), lastTrick
								.getCard(Player.HIND_HAND));
				this.view.clearTrickCards(this.tableName);
				// renew trickForeHand
				this.trickForeHand = this.data.getTrickWinner(this.data
						.getTricks().size() - 1);
			}

			this.view.setTrickForeHand(this.tableName, this.trickForeHand);

			Trick trick = new Trick(this.trickForeHand);

			// Ask players for their cards
			log.debug("fore hand plays"); //$NON-NLS-1$
			playCard(trick, this.trickForeHand);
			doSleep(this.maxSleep);

			log.debug("middle hand plays"); //$NON-NLS-1$
			playCard(trick, this.trickForeHand.getLeftNeighbor());
			doSleep(this.maxSleep);

			log.debug("hind hand plays"); //$NON-NLS-1$
			playCard(trick, this.trickForeHand.getRightNeighbor());

			doSleep(this.maxSleep);

			log.debug("Calculate trick winner"); //$NON-NLS-1$
			Player trickWinner = calculateTrickWinner(trick);
			trick.setTrickWinner(trickWinner);
			this.data.getTricks().add(trick);
			this.data.addPlayerPoints(trickWinner, trick.getCardValueSum());

			for (int j = 0; j < 3; j++) {
				// inform all players
				// cloning of trick information to prevent manipulation by
				// player
				try {
					this.player[j].showTrick((Trick) trick.clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			doSleep(this.maxSleep);

			log.debug("Trick cards: " + trick.getCardList()); //$NON-NLS-1$
			log
					.debug("Points: fore hand: " + this.data.getPlayerPoints(Player.FORE_HAND) + //$NON-NLS-1$
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
		JSkatPlayer skatPlayer = getPlayerObject(currPlayer);

		// ask player for the next card
		card = skatPlayer.playCard();

		log.debug(card + " " + this.data); //$NON-NLS-1$

		if (card == null) {
			// TODO throw appropriate exception
			log.error("Player is fooling!!! Did not play a card!"); //$NON-NLS-1$
		} else if (!playerHasCard(currPlayer, card)) {
			// TODO throw appropriate exception
			log.error("Player is fooling!!! Doesn't have card " + card + "!"); //$NON-NLS-1$//$NON-NLS-2$
		} else if (!this.rules.isCardAllowed(this.data.getGameType(), trick
				.getFirstCard(), this.data.getPlayerCards(currPlayer), card)) {
			// TODO throw appropriate exception
			log.error("Player is fooling!!! Card " + card + " is not allowed!"); //$NON-NLS-1$//$NON-NLS-2$
		} else {
			// card was on players hand and is valid
			trick.addCard(card);
			this.data.getPlayerCards(currPlayer).remove(card);
			/*
			 * this.view.removeCard(this.tableName, currPlayer, card);
			 * this.view.setTrickCard(this.tableName, currPlayer, card);
			 */
			this.view.playTrickCard(this.tableName, currPlayer, card);

			for (int i = 0; i < 3; i++) {
				// inform all players
				// cloning of card is not neccessary, because Card is immutable
				this.player[i].cardPlayed(currPlayer, card);
			}
		}

		log.debug("playing card " + card); //$NON-NLS-1$
	}

	private JSkatPlayer getPlayerObject(Player currPlayer) {

		JSkatPlayer result = null;

		switch (currPlayer) {
		case FORE_HAND:
			result = this.foreHand;
			break;
		case MIDDLE_HAND:
			result = this.middleHand;
			break;
		case HIND_HAND:
			result = this.hindHand;
			break;
		}

		return result;
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

	private Player getPlayerID(JSkatPlayer skatPlayer) {

		Player result = null;

		if (skatPlayer == this.foreHand) {

			result = Player.FORE_HAND;
		} else if (skatPlayer == this.middleHand) {

			result = Player.MIDDLE_HAND;
		} else if (skatPlayer == this.hindHand) {

			result = Player.HIND_HAND;
		}

		return result;
	}

	/**
	 * Calculates the trick winner
	 * 
	 * @param rules
	 *            Skat rules
	 * @param trick
	 *            Trick
	 * @return ID of the trick winner
	 */
	private Player calculateTrickWinner(Trick trick) {

		Player trickWinner = null;
		GameType gameType = this.data.getGameType();
		Card first = trick.getFirstCard();
		Card second = trick.getSecondCard();
		Card third = trick.getThirdCard();

		if (this.rules.isCardBeatsCard(gameType, first, second)) {

			if (this.rules.isCardBeatsCard(gameType, second, third)) {
				// trick winner is hind hand
				trickWinner = this.trickForeHand.getRightNeighbor();
			} else {
				// trick winner is middle hand
				trickWinner = this.trickForeHand.getLeftNeighbor();
			}
		} else {

			if (this.rules.isCardBeatsCard(gameType, first, third)) {
				// trick winner is hind hand
				trickWinner = this.trickForeHand.getRightNeighbor();
			} else {
				// trick winner is fore hand
				trickWinner = this.trickForeHand;
			}
		}

		// Check for preliminary ending of a null game
		if (gameType == GameType.NULL) {

			if (trickWinner == this.data.getDeclarer()) {
				// declarer has won a trick
				setGameState(GameState.PRELIMINARY_GAME_END);
			}
		}

		log.debug("Trick fore hand: " + this.trickForeHand); //$NON-NLS-1$
		log.debug("Trick winner: " + trickWinner); //$NON-NLS-1$

		return trickWinner;
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

		for (JSkatPlayer currPlayer : this.player) {
			// no cloning neccessary because all parameters are primitive data
			// types
			currPlayer.setGameResult(this.data.isGameWon(), this.data
					.getGameResult());
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
	public void setView(JSkatView newView) {

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
		for (JSkatPlayer currPlayer : this.player) {
			// no cloning neccessary, because all parameters are primitive data
			// types
			currPlayer.startGame(this.data.getDeclarer(), this.data
					.getGameType(), this.data.isHand(), this.data.isOuvert(),
					this.data.isSchneiderAnnounced(), this.data
							.isSchwarzAnnounced());
		}

		this.view.setGameAnnouncement(this.tableName, ann, this.data.isHand());

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

			} else if (newState == GameState.DISCARDING) {

				this.view.setSkat(this.tableName, this.data.getSkat());
				
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

		switch (singlePlayer) {
		case FORE_HAND:
			this.declarer = this.foreHand;
			break;
		case MIDDLE_HAND:
			this.declarer = this.middleHand;
			break;
		case HIND_HAND:
			this.declarer = this.hindHand;
			break;
		}
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
