/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control;

import java.util.HashMap;
import java.util.Map;

import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.skatgame.BidEvent;
import org.jskat.control.event.skatgame.ContraEvent;
import org.jskat.control.event.skatgame.GameAnnouncementEvent;
import org.jskat.control.event.skatgame.HoldBidEvent;
import org.jskat.control.event.skatgame.PassBidEvent;
import org.jskat.control.event.skatgame.ReEvent;
import org.jskat.control.event.skatgame.TrickCardPlayedEvent;
import org.jskat.control.event.table.ActivePlayerChangedEvent;
import org.jskat.control.event.table.TableGameMoveEvent;
import org.jskat.control.event.table.TrickCompletedEvent;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.GameSummary;
import org.jskat.data.JSkatOptions;
import org.jskat.data.SkatGameData;
import org.jskat.data.SkatGameData.GameState;
import org.jskat.data.SkatGameResult;
import org.jskat.data.SkatTableOptions.ContraCallingTime;
import org.jskat.data.SkatTableOptions.RamschSkatOwner;
import org.jskat.data.Trick;
import org.jskat.gui.JSkatView;
import org.jskat.player.JSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.GameVariant;
import org.jskat.util.JSkatResourceBundle;
import org.jskat.util.Player;
import org.jskat.util.SkatConstants;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls a skat game.
 */
public class SkatGame extends JSkatThread {

	private Logger log = LoggerFactory.getLogger(SkatGame.class);
	private int maxSleep;
	private final SkatGameData data;
	private final GameVariant variant;
	private CardDeck deck;
	private final Map<Player, JSkatPlayer> player;
	private Player activePlayer;
	private final String tableName;
	private JSkatView view;
	private SkatRule rules;

	private final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;

	/**
	 * Constructor
	 *
	 * @param newTableName
	 *            Table name
	 * @param variant
	 *            game variant
	 * @param newForeHand
	 *            Fore hand player
	 * @param newMiddleHand
	 *            Middle hand player
	 * @param newRearHand
	 *            Rear hand player
	 */
	public SkatGame(final String newTableName, final GameVariant variant,
			final JSkatPlayer newForeHand, final JSkatPlayer newMiddleHand,
			final JSkatPlayer newRearHand) {
		this.tableName = newTableName;
		setName("SkatGame on table " + this.tableName); //$NON-NLS-1$
		this.variant = variant;
		this.player = new HashMap<Player, JSkatPlayer>();
		this.player.put(Player.FOREHAND, newForeHand);
		this.player.put(Player.MIDDLEHAND, newMiddleHand);
		this.player.put(Player.REARHAND, newRearHand);

		// inform all players about the starting of the new game
		for (final Player pos : this.player.keySet()) {
			getPlayerInstance(pos).newGame(pos);
		}

		this.data = new SkatGameData();

		setGameState(GameState.GAME_START);
	}

	/**
	 * @see Thread#run()
	 */
	// FIXME jan 11.07.2013: this method is too long, break it down to smaller
	// methods or implement it in another way
	@Override
	public void run() {

		JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).register(data);

		this.view.clearTable(this.tableName);
		this.view.setGameState(this.tableName, this.data.getGameState());

		do {
			this.log.debug("SkatGame.do --- Game state: " + this.data.getGameState()); //$NON-NLS-1$

			switch (this.data.getGameState()) {
			case GAME_START:
				setGameState(GameState.DEALING);
				break;
			case DEALING:
				dealCards();
				setGameState(GameState.BIDDING);
				break;
			case BIDDING:
				setActivePlayer(Player.MIDDLEHAND);

				if (this.variant == GameVariant.FORCED_RAMSCH) {
					// ramsch games are enforced
					final GameAnnouncementFactory gaf = GameAnnouncement
							.getFactory();
					gaf.setGameType(GameType.RAMSCH);
					setGameAnnouncement(gaf.getAnnouncement());
				} else {
					// "normal" game (i.e. no ramsch)
					bidding();
				}

				if (GameType.PASSED_IN.equals(this.data.getGameType())) {
					setGameState(GameState.PRELIMINARY_GAME_END);
				} else if (GameType.RAMSCH.equals(this.data.getGameType())) {
					setGameState(GameState.RAMSCH_GRAND_HAND_ANNOUNCING);
				} else {
					this.view.setDeclarer(this.tableName, this.data.getDeclarer());
					setGameState(GameState.PICKING_UP_SKAT);
				}
				break;
			case RAMSCH_GRAND_HAND_ANNOUNCING:
				boolean grandHandAnnounced = grandHand();

				if (grandHandAnnounced) {
					this.log.debug(this.data.getDeclarer() + " is playing grand hand"); //$NON-NLS-1$
					final GameAnnouncementFactory gaf = GameAnnouncement
							.getFactory();
					gaf.setGameType(GameType.GRAND);
					gaf.setHand(Boolean.TRUE);
					setGameAnnouncement(gaf.getAnnouncement());
					setGameState(GameState.TRICK_PLAYING);
					this.log.debug("grand hand game started"); //$NON-NLS-1$
					break;
				} else {
					if (JSkatOptions.instance().isSchieberamsch(true)) {
						this.log.debug("no grand hand - initiating schieberamsch"); //$NON-NLS-1$
						setGameState(GameState.SCHIEBERAMSCH);
					} else {
						this.log.debug("no grand hand and no schieberamsch - play ramsch"); //$NON-NLS-1$
						setGameState(GameState.TRICK_PLAYING);
					}
				}
				break;
			case SCHIEBERAMSCH:
				schieberamsch();
				GameAnnouncementFactory factory = GameAnnouncement.getFactory();
				factory.setGameType(GameType.RAMSCH);
				setGameAnnouncement(factory.getAnnouncement());
				setGameState(GameState.TRICK_PLAYING);
				break;
			case PICKING_UP_SKAT:
				setActivePlayer(this.data.getDeclarer());
				if (pickUpSkat()) {
					setGameState(GameState.DISCARDING);
				} else {
					setGameState(GameState.DECLARING);
				}
				break;
			case DISCARDING:
				setActivePlayer(this.data.getDeclarer());
				discarding();
				if (!GameState.PRELIMINARY_GAME_END.equals(this.data.getGameState())) {
					setGameState(GameState.DECLARING);
				}
				break;
			case DECLARING:
				announceGame();
				if (isContraPlayEnabled(ContraCallingTime.AFTER_GAME_ANNOUNCEMENT)) {
					setGameState(GameState.CONTRA);
				} else {
					setGameState(GameState.TRICK_PLAYING);
				}
				break;
			case CONTRA:
				for (Player player : Player.getOrderedList()) {
					if (isContraEnabledForPlayer(player,
							ContraCallingTime.AFTER_GAME_ANNOUNCEMENT)) {
						setActivePlayer(player);
						contraRe();
					}
				}
				setGameState(GameState.TRICK_PLAYING);
				break;
			case TRICK_PLAYING:
				playTricks();
				setGameState(GameState.CALCULATING_GAME_VALUE);
				break;
			case PRELIMINARY_GAME_END:
				setGameState(GameState.CALCULATING_GAME_VALUE);
				break;
			case CALCULATING_GAME_VALUE:
				calculateGameValue();
				setGameState(GameState.GAME_OVER);
				break;
			case GAME_OVER:
				break;
			}

			checkWaitCondition();
		} while (this.data.getGameState() != GameState.GAME_OVER && !isTerminated());

		JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).unregister(data);

		this.log.debug(this.data.getGameState().name());
	}

	private void contraRe() {
		if (getActivePlayerInstance().callContra()) {
			JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName,
					new ContraEvent(activePlayer)));
			setGameState(GameState.RE);
			Player activePlayerBeforeContraRe = activePlayer;
			setActivePlayer(this.data.getDeclarer());
			if (getActivePlayerInstance().callRe()) {
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName,
						new ReEvent(activePlayer)));
			}
			setActivePlayer(activePlayerBeforeContraRe);
		}
	}

	private boolean grandHand() {
		boolean grandHandAnnounced = false;

		for (final Player currPlayer : Player.getOrderedList()) {
			setActivePlayer(currPlayer);
			if (!grandHandAnnounced && playGrandHand()) {
				this.log.debug("Player " + this.activePlayer + " is playing grand hand.");
				setDeclarer(this.activePlayer);
				grandHandAnnounced = true;
			} else {
				this.log.debug("Player " + this.activePlayer
						+ " doesn't want to play grand hand.");
			}
		}
		return grandHandAnnounced;
	}

	private void schieberamsch() {
		for (final Player currPlayer : Player.getOrderedList()) {
			setActivePlayer(currPlayer);
			if (!pickUpSkat()) {
				this.log.debug("Player " + currPlayer + " does schieben."); //$NON-NLS-1$
				this.data.addGeschoben();
				this.view.setGeschoben(this.tableName, this.activePlayer);
			} else {
				this.log.debug("Player " + currPlayer + " wants to look into skat.");
				this.view.setSkat(this.tableName, this.data.getSkat());
				discarding();
			}
		}
	}

	private void setActivePlayer(final Player newPlayer) {
		this.activePlayer = newPlayer;
		JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(
				this.tableName, this.activePlayer));
	}

	private boolean playGrandHand() {
		return getActivePlayerInstance().playGrandHand();
	}

	private boolean pickUpSkat() {
		return getActivePlayerInstance().pickUpSkat();
	}

	/**
	 * Deals the cards to the players and the skat
	 */
	public void dealCards() {

		if (this.deck == null) {
			// Skat game has no cards, yet
			this.deck = new CardDeck();

			this.log.debug("shuffling..."); //$NON-NLS-1$
			this.deck.shuffle();

			this.log.debug(this.deck.toString());
		}

		doSleep(this.maxSleep);

		this.log.debug("dealing..."); //$NON-NLS-1$

		// deal three rounds of cards
		// deal three cards
		dealCards(3);
		// and put two cards into the skat
		CardList skat = new CardList(this.deck.remove(0), this.deck.remove(0));
		this.data.setDealtSkatCards(skat);
		// deal four cards
		dealCards(4);
		// deal three cards
		dealCards(3);

		// show cards in the view
		final Map<Player, CardList> dealtCards = this.data.getDealtCards();
		for (final Player currPlayer : Player.getOrderedList()) {

			this.view.addCards(this.tableName, currPlayer, dealtCards.get(currPlayer));
		}

		doSleep(this.maxSleep);

		this.log.debug("Fore hand: " + this.data.getPlayerCards(Player.FOREHAND)); //$NON-NLS-1$
		this.log.debug("Middle hand: " //$NON-NLS-1$
				+ this.data.getPlayerCards(Player.MIDDLEHAND));
		this.log.debug("Rear hand: " + this.data.getPlayerCards(Player.REARHAND)); //$NON-NLS-1$
		this.log.debug("Skat: " + this.data.getSkat()); //$NON-NLS-1$
	}

	/**
	 * Deals a given number of cards to the players
	 *
	 * @param cardCount
	 *            Number of cards to be dealt to a player
	 */
	private void dealCards(final int cardCount) {

		for (final Player hand : Player.getOrderedList()) {
			CardList cards = new CardList();
			for (int j = 0; j < cardCount; j++) {
				// deal amount of cards
				cards.add(this.deck.remove(0));
			}
			// player can get original card object because Card is immutable
			getPlayerInstance(hand).takeCards(cards);
			this.data.addDealtCards(hand, cards);
		}
	}

	/**
	 * Controls the bidding of all players
	 */
	private void bidding() {

		int bidValue = 0;

		this.log.debug("ask middle and fore hand..."); //$NON-NLS-1$

		bidValue = twoPlayerBidding(Player.MIDDLEHAND, Player.FOREHAND,
				bidValue);

		this.log.debug("Bid value after first bidding: " //$NON-NLS-1$
				+ bidValue);

		final Player firstWinner = getBiddingWinner(Player.MIDDLEHAND,
				Player.FOREHAND);

		this.log.debug("First bidding winner: " + firstWinner); //$NON-NLS-1$
		this.log.debug("ask rear hand and first winner..."); //$NON-NLS-1$

		bidValue = twoPlayerBidding(Player.REARHAND, firstWinner, bidValue);

		this.log.debug("Bid value after second bidding: " //$NON-NLS-1$
				+ bidValue);

		// get second winner
		Player secondWinner = getBiddingWinner(Player.REARHAND, firstWinner);

		if (secondWinner == Player.FOREHAND && bidValue == 0) {

			this.log.debug("Check whether fore hand holds at least one bid"); //$NON-NLS-1$

			setActivePlayer(Player.FOREHAND);

			// check whether fore hand holds at least one bid
			if (getPlayerInstance(Player.FOREHAND).bidMore(18) > -1) {

				this.log.debug("Fore hand holds 18"); //$NON-NLS-1$
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(
						this.tableName, new BidEvent(secondWinner, 18)));
			} else {

				this.log.debug("Fore hand passes too"); //$NON-NLS-1$
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(
						this.tableName, new PassBidEvent(Player.FOREHAND)));
				secondWinner = null;
			}
		}

		if (secondWinner != null) {
			// there is a winner of the bidding
			setDeclarer(secondWinner);
			setActivePlayer(secondWinner);

			this.log.debug("Player " + this.data.getDeclarer() //$NON-NLS-1$
					+ " wins the bidding."); //$NON-NLS-1$
		} else {
			// FIXME (jansch 02.01.2012) use cloned rule options here (see
			// MantisBT: 0000037)
			final JSkatOptions options = JSkatOptions.instance();

			if (options.isPlayRamsch() && options.isRamschEventNoBid()) {
				this.log.debug("Playing ramsch due to no bid"); //$NON-NLS-1$
				final GameAnnouncementFactory factory = GameAnnouncement
						.getFactory();
				factory.setGameType(GameType.RAMSCH);
				setGameAnnouncement(factory.getAnnouncement());
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName,
						new GameAnnouncementEvent(data.getDeclarer(), data
								.getAnnoucement())));
				setActivePlayer(Player.FOREHAND);
				// do not call "setGameAnnouncement(..)" here!
			} else {
				// pass in
				final GameAnnouncementFactory factory = GameAnnouncement
						.getFactory();
				factory.setGameType(GameType.PASSED_IN);
				setGameAnnouncement(factory.getAnnouncement());
			}
		}

		doSleep(this.maxSleep);
	}

	private void informPlayersAboutBid(final Player bidPlayer,
			final int bidValue) {
		// inform all players about the last bid
		for (final JSkatPlayer playerInstance : this.player.values()) {
			playerInstance.bidByPlayer(bidPlayer, bidValue);
		}
	}

	/**
	 * Controls the bidding between two players
	 *
	 * @param announcer
	 *            Announcing player
	 * @param hearer
	 *            Hearing player
	 * @param startBidValue
	 *            Bid value to start from
	 * @return the final bid value
	 */
	private int twoPlayerBidding(final Player announcer, final Player hearer,
			final int startBidValue) {

		int currBidValue = startBidValue;
		boolean announcerPassed = false;
		boolean hearerPassed = false;

		while (!announcerPassed && !hearerPassed) {

			// get bid value
			final int nextBidValue = SkatConstants
					.getNextBidValue(currBidValue);
			this.view.setBidValueToMake(this.tableName, nextBidValue);
			// ask player
			setActivePlayer(announcer);
			final int announcerBidValue = getPlayerInstance(announcer).bidMore(
					nextBidValue);

			if (announcerBidValue > -1
					&& SkatConstants.bidOrder.contains(Integer
							.valueOf(announcerBidValue))) {

				this.log.debug("announcer bids " + announcerBidValue); //$NON-NLS-1$

				// announcing hand holds bid
				currBidValue = announcerBidValue;

				this.data.addPlayerBid(announcer, announcerBidValue);
				informPlayersAboutBid(announcer, announcerBidValue);
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(
						this.tableName, new BidEvent(announcer,
								announcerBidValue)));

				setActivePlayer(hearer);
				if (getPlayerInstance(hearer).holdBid(currBidValue)) {

					this.log.debug("hearer holds " + currBidValue); //$NON-NLS-1$

					// hearing hand holds bid
					this.data.addPlayerBid(hearer, announcerBidValue);
					informPlayersAboutBid(hearer, announcerBidValue);
					JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(
							this.tableName, new HoldBidEvent(hearer,
									announcerBidValue)));

				} else {

					this.log.debug("hearer passed at " + announcerBidValue); //$NON-NLS-1$

					// hearing hand passed
					hearerPassed = true;
					this.data.setPlayerPass(hearer, true);
					JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(
							this.tableName, new PassBidEvent(hearer)));
				}
			} else {

				this.log.debug("announcer passed at " + nextBidValue); //$NON-NLS-1$

				// announcing hand passes
				announcerPassed = true;
				this.data.setPlayerPass(announcer, true);
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(
						this.tableName, new PassBidEvent(announcer)));
			}
		}

		return currBidValue;
	}

	private Player getBiddingWinner(final Player announcer, final Player hearer) {

		Player biddingWinner = null;

		if (this.data.isPlayerPass(announcer)) {
			biddingWinner = hearer;
		} else if (this.data.isPlayerPass(hearer)) {
			biddingWinner = announcer;
		}

		return biddingWinner;
	}

	private void discarding() {

		final JSkatPlayer activePlayerInstance = getActivePlayerInstance();

		this.view.setSkat(this.tableName, this.data.getSkat());

		this.log.debug("Player " + this.activePlayer + " looks into the skat..."); //$NON-NLS-1$ //$NON-NLS-2$
		this.log.debug("Skat before discarding: " + this.data.getSkat()); //$NON-NLS-1$

		final CardList skatBefore = new CardList(this.data.getSkat());

		// create a clone of the skat before sending it to the player
		// otherwise the player could change the skat after discarding
		activePlayerInstance.takeSkat(skatBefore);
		this.data.addSkatToPlayer(this.activePlayer);

		// ask player for the cards to be discarded
		// cloning is done to prevent the player
		// from manipulating the skat afterwards
		final CardList discardedSkat = new CardList();
		discardedSkat.addAll(activePlayerInstance.discardSkat());

		if (!checkDiscardedCards(this.activePlayer, discardedSkat)) {
			this.view.showAIPlayedSchwarzMessageDiscarding(
					activePlayerInstance.getPlayerName(), discardedSkat);
			endGameBecauseOfSchwarzPlaying(this.activePlayer);
		} else {
			this.log.debug("Discarded cards: " + discardedSkat); //$NON-NLS-1$

			this.data.setDiscardedSkat(this.activePlayer, discardedSkat);
			if (!activePlayerInstance.isHumanPlayer()) {
				// human player has changed the cards in the GUI already
				this.view.setDiscardedSkat(this.tableName, this.activePlayer, skatBefore,
						discardedSkat);
			}
		}
	}

	private boolean checkDiscardedCards(Player player, CardList discardedSkat) {

		// TODO move this to skat rules?
		boolean result = true;

		if (discardedSkat == null) {

			this.log.error("Player is fooling!!! Skat is empty!"); //$NON-NLS-1$
			result = false;
		} else if (discardedSkat.size() != 2) {

			this.log.error("Player is fooling!!! Skat doesn't have two cards!"); //$NON-NLS-1$
			result = false;
		} else if (discardedSkat.get(0) == discardedSkat.get(1)) {
			this.log.error("Player is fooling!!! Skat cards are identical!"); //$NON-NLS-1$
			result = false;
		} else if (!playerHasCard(player, discardedSkat.get(0))
				|| !playerHasCard(player, discardedSkat.get(1))) {
			this.log.error("Player is fooling!!! Player doesn't have had discarded card! Dis"); //$NON-NLS-1$
			result = false;
		}
		// TODO check for jacks in the discarded skat in ramsch games

		return result;
	}

	private void announceGame() {

		this.log.debug("declaring game..."); //$NON-NLS-1$

		// TODO check for valid game announcements
		final GameAnnouncement ann = getPlayerInstance(this.data.getDeclarer())
				.announceGame();
		if (ann != null) {
			setGameAnnouncement(ann);
		} else {
			this.view.showErrorMessage(
					this.strings.getString("invalid_game_announcement_title"), //$NON-NLS-1$
					this.strings.getString("invalid_game_announcement_message", ann)); //$NON-NLS-1$
		}

		doSleep(this.maxSleep);
	}

	private void playTricks() {

		Player trickWinner = null;

		for (int trickNo = 0; trickNo < 10; trickNo++) {

			this.log.debug("=============== Play trick " + (trickNo + 1) + " ==============="); //$NON-NLS-1$ //$NON-NLS-2$
			doSleep(this.maxSleep);

			Player trickForehand = getTrickForeHand(trickNo);
			setActivePlayer(trickForehand);

			this.view.setTrickNumber(this.tableName, trickNo + 1);

			final Trick trick = new Trick(trickNo, this.activePlayer);
			this.data.addTrick(trick);
			informPlayersAboutNewTrick(trick);

			// Ask players for their cards
			this.log.debug("fore hand plays"); //$NON-NLS-1$
			if (isContraEnabledForPlayer(this.activePlayer,
					ContraCallingTime.BEFORE_FIRST_CARD)) {
				setGameState(GameState.CONTRA);
				contraRe();
			}
			setGameState(GameState.TRICK_PLAYING);

			playCard(trick, trickForehand, this.activePlayer);

			if (isFinished()) {
				break;
			}

			doSleep(this.maxSleep);

			this.log.debug("middle hand plays"); //$NON-NLS-1$
			setActivePlayer(this.activePlayer.getLeftNeighbor());
			if (isContraEnabledForPlayer(this.activePlayer,
					ContraCallingTime.BEFORE_FIRST_CARD)) {
				setGameState(GameState.CONTRA);
				contraRe();
			}
			setGameState(GameState.TRICK_PLAYING);

			playCard(trick, trickForehand, this.activePlayer);

			if (isFinished()) {
				break;
			}

			doSleep(this.maxSleep);

			this.log.debug("rear hand plays"); //$NON-NLS-1$
			setActivePlayer(this.activePlayer.getLeftNeighbor());
			if (isContraEnabledForPlayer(this.activePlayer,
					ContraCallingTime.BEFORE_FIRST_CARD)) {
				setGameState(GameState.CONTRA);
				contraRe();
			}
			setGameState(GameState.TRICK_PLAYING);

			playCard(trick, trickForehand, this.activePlayer);

			if (isFinished()) {
				break;
			}

			doSleep(this.maxSleep);

			this.log.debug("Calculate trick winner"); //$NON-NLS-1$
			trickWinner = this.rules.calculateTrickWinner(this.data.getGameType(), trick);
			trick.setTrickWinner(trickWinner);
			this.data.addPlayerPoints(trickWinner, trick.getValue());

			informPlayersAboutCompletedTrick(trick);

			// Check for preliminary ending of a null game
			if (GameType.NULL.equals(this.data.getGameType())) {

				if (trickWinner == this.data.getDeclarer()) {
					// declarer has won a trick
					setGameState(GameState.PRELIMINARY_GAME_END);
				}
			}

			this.log.debug("Trick cards: " + trick.getCardList()); //$NON-NLS-1$
			logPlayerPoints();

			doSleep(this.maxSleep);

			if (isFinished()) {
				break;
			}

			checkWaitCondition();
		}

		addSkatPointsToPlayerPoints(trickWinner);

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

	private Boolean isPlayContra(ContraCallingTime contraCallingTime,
			Player player) {
		JSkatOptions options = JSkatOptions.instance();
		if (isContraPlayEnabled(contraCallingTime)) {
			if (ContraCallingTime.AFTER_GAME_ANNOUNCEMENT == contraCallingTime) {
				return true;
			} else if (ContraCallingTime.BEFORE_FIRST_CARD == contraCallingTime
					&& this.data.getCurrentTrick().getTrickNumberInGame() == 0) {
				return true;
			}
		}

		return false;
	}

	private Boolean isContraPlayEnabled(ContraCallingTime gameTime) {
		JSkatOptions options = JSkatOptions.instance();
		if (!GameVariant.FORCED_RAMSCH.equals(this.variant)
				&& options.isPlayContra(true)
				&& options.getContraCallingTime() == gameTime
				&& isGameWithDeclarer()) {
			if (ContraCallingTime.AFTER_GAME_ANNOUNCEMENT == gameTime) {
				return true;
			} else if (ContraCallingTime.BEFORE_FIRST_CARD == gameTime
					&& this.data.getCurrentTrick().getTrickNumberInGame() == 0) {
				return true;
			}
		}
		return false;
	}

	private Boolean isGameWithDeclarer() {
		GameType gameType = this.data.getGameType();
		if (gameType == GameType.CLUBS || gameType == GameType.SPADES
				|| gameType == GameType.HEARTS || gameType == GameType.DIAMONDS
				|| gameType == GameType.GRAND || gameType == GameType.NULL) {
			return true;
		}
		return false;
	}

	private Boolean isContraEnabledForPlayer(Player player,
			ContraCallingTime gameTime) {
		if (isContraPlayEnabled(gameTime) && isNoContraCalledYet()
				&& isPlayerOpponent(player)
				&& isPlayerBidHighEnoughForContra(player)) {
			return true;
		}

		return false;
	}

	private boolean isPlayerBidHighEnoughForContra(Player player) {
		JSkatOptions options = JSkatOptions.instance();
		if (options.isContraAfterBid18() && this.data.getMaxPlayerBid(player) > 0) {
			return true;
		}
		return true;
	}

	private boolean isPlayerOpponent(Player player) {
		return player != this.data.getDeclarer();
	}

	private boolean isNoContraCalledYet() {
		return !this.data.isContra();
	}

	private void informPlayersAboutCompletedTrick(final Trick trick) {
		for (Player currPosition : Player.getOrderedList()) {
			getPlayerInstance(currPosition).showTrick((Trick) trick.clone());
		}
	}

	private void informPlayersAboutNewTrick(final Trick trick) {
		for (Player currPosition : Player.getOrderedList()) {
			getPlayerInstance(currPosition).newTrick((Trick) trick.clone());
		}
	}

	private Player getTrickForeHand(int currentTrickNo) {
		Player trickForeHand = null;
		if (currentTrickNo == 0) {
			// first trick
			trickForeHand = Player.FOREHAND;
		} else {
			// get last trick winner as fore hand of next trick
			trickForeHand = this.data.getTrickWinner(currentTrickNo - 1);
		}
		return trickForeHand;
	}

	private void logPlayerPoints() {
		this.log.debug("Points: forehand: " + this.data.getPlayerPoints(Player.FOREHAND) + //$NON-NLS-1$
				" middlehand: " //$NON-NLS-1$
				+ this.data.getPlayerPoints(Player.MIDDLEHAND) + " rearhand: " //$NON-NLS-1$
				+ this.data.getPlayerPoints(Player.REARHAND));
	}

	private void addSkatPointsToPlayerPoints(Player lastTrickWinner) {
		this.log.debug("Skat: " + this.data.getSkat());
		if (this.data.getGameType() == GameType.RAMSCH) {
			if (JSkatOptions.instance().getRamschSkatOwner() == RamschSkatOwner.LAST_TRICK) {
				if (lastTrickWinner != null) {
					this.log.debug("Skat cards (" + this.data.getSkat().getTotalValue() + " points) are added to player @ " //$NON-NLS-1$ //$NON-NLS-2$
							+ lastTrickWinner + " (= last trick)"); //$NON-NLS-1$
					this.data.addPlayerPoints(lastTrickWinner, this.data.getSkat()
							.getTotalValue());
				} else {
					this.log.warn("Skat cards cannot be added to winner of final trick - trick winner is unknown"); //$NON-NLS-1$
				}
			}
		} else {
			// for all the other games, points to the declarer
			this.data.addPlayerPoints(this.data.getDeclarer(), this.data.getSkat()
					.getTotalValue());
		}
		logPlayerPoints();
	}

	private void playCard(Trick trick, Player trickForeHand, Player currPlayer) {

		Card card = null;
		final JSkatPlayer skatPlayer = getActivePlayerInstance();

		boolean cardAccepted = false;
		boolean aiPlayerPlayedSchwarz = false;

		while (!cardAccepted && !aiPlayerPlayedSchwarz) {

			try {
				// ask player for the next card
				card = skatPlayer.playCard();
			} catch (final Exception exp) {
				this.log.error("Exception thrown by player " + skatPlayer + " playing " + currPlayer + ": " + exp); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (!skatPlayer.isHumanPlayer()) {
					aiPlayerPlayedSchwarz = true;
				}
			}

			this.log.debug(card + " " + this.data); //$NON-NLS-1$

			if (isCardSchwarzPlay(skatPlayer, currPlayer, trick, card)) {
				if (skatPlayer.isHumanPlayer()) {
					this.view.showCardNotAllowedMessage(card);
				} else {
					this.view.showAIPlayedSchwarzMessageCardPlay(
							skatPlayer.getPlayerName(), card);
					aiPlayerPlayedSchwarz = true;
				}
			} else {

				cardAccepted = true;
			}
		}

		if (card != null) {
			if (trick.getTrickNumberInGame() > 0
					&& trick.getFirstCard() == null) {

				JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).post(
						new TrickCompletedEvent(data.getLastTrick()));
			}

			JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(this.tableName,
					new TrickCardPlayedEvent(currPlayer, card)));

			for (final JSkatPlayer playerInstance : this.player.values()) {
				// inform all players
				// cloning of card is not neccessary, because Card is immutable
				playerInstance.cardPlayed(currPlayer, card);
			}

			this.log.debug("playing card " + card); //$NON-NLS-1$
		}

		if (aiPlayerPlayedSchwarz) {
			// end game immediately
			endGameBecauseOfSchwarzPlaying(currPlayer);
		}
	}

	private void endGameBecauseOfSchwarzPlaying(Player currentPlayer) {
		this.data.getResult().setSchwarz(true);
		if (this.data.getDeclarer().equals(currentPlayer)) {
			// declarer played schwarz
			this.data.getResult().setWon(false);
		} else {
			// opponent played schwarz
			this.data.getResult().setWon(true);
		}
		this.data.setGameState(GameState.PRELIMINARY_GAME_END);
	}

	private boolean isCardSchwarzPlay(JSkatPlayer skatPlayer, Player position,
			Trick trick, Card card) {
		boolean isSchwarz = false;
		if (card == null) {

			this.log.error("Player is fooling!!! Did not play a card!"); //$NON-NLS-1$
			isSchwarz = true;

		} else if (!playerHasCard(position, card)) {

			this.log.error("Player (" + skatPlayer + ") is fooling!!! Doesn't have card " + card + "!"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			isSchwarz = true;

		} else if (!this.rules.isCardAllowed(this.data.getGameType(),
				trick.getFirstCard(), this.data.getPlayerCards(position), card)) {

			this.log.error("Player " + skatPlayer.getClass().toString() + " card not allowed: " + card + " game type: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ this.data.getGameType() + " first trick card: " //$NON-NLS-1$
					+ trick.getFirstCard() + " player cards: " //$NON-NLS-1$
					+ this.data.getPlayerCards(position));
			isSchwarz = true;
		}

		return isSchwarz;
	}

	private JSkatPlayer getActivePlayerInstance() {
		return this.player.get(this.activePlayer);
	}

	private JSkatPlayer getPlayerInstance(Player position) {
		return this.player.get(position);
	}

	/**
	 * Checks whether a player has the card on it's hand or not
	 *
	 * @param card
	 *            Card to check
	 * @return TRUE if the card is on player's hand
	 */
	private boolean playerHasCard(final Player player, final Card card) {

		boolean result = false;

		this.log.debug("Player " + player + " has card: player cards: " + this.data.getPlayerCards(player) + " card to check: " + card); //$NON-NLS-1$

		for (final Card handCard : this.data.getPlayerCards(player)) {

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

		this.log.debug("Calculate game value"); //$NON-NLS-1$

		// FIXME (jan 07.12.2010) don't let a data class calculate it's values
		this.data.calcResult();

		this.log.debug("game value=" + this.data.getResult() + ", bid value=" //$NON-NLS-1$ //$NON-NLS-2$
				+ this.data.getMaxBidValue());

		this.log.debug("Final game result: lost:" + this.data.isGameLost() + //$NON-NLS-1$
				" game value: " + this.data.getResult()); //$NON-NLS-1$

		this.log.debug("Final result: " + this.data.getDeclarerScore() + "/"
				+ this.data.getOpponentScore());

		for (final JSkatPlayer playerInstance : this.player.values()) {
			playerInstance.setGameSummary(this.data.getGameSummary());
			playerInstance.finalizeGame();
		}

		doSleep(this.maxSleep);
	}

	private void doSleep(final int milliseconds) {

		try {
			sleep(milliseconds);
		} catch (final InterruptedException e) {
			this.log.warn("sleep was interrupted..."); //$NON-NLS-1$
		}
	}

	/**
	 * Sets the view for the game
	 *
	 * @param newView
	 *            View
	 */
	public void setView(final JSkatView newView) {

		this.view = newView;
	}

	/**
	 * Sets a new logger for the skat game
	 *
	 * @param newLogger
	 *            New logger
	 */
	public void setLogger(final Logger newLogger) {
		this.log = newLogger;
	}

	/**
	 * Sets the cards from outside
	 *
	 * @param newDeck
	 *            Card deck
	 */
	public void setCardDeck(final CardDeck newDeck) {

		this.deck = newDeck;
	}

	/**
	 * Sets the game announcement from the outside
	 *
	 * @param ann
	 *            Game announcement
	 */
	public void setGameAnnouncement(final GameAnnouncement ann) {

		this.data.setAnnouncement(ann);
		this.rules = SkatRuleFactory.getSkatRules(this.data.getGameType());
		JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName,
				new GameAnnouncementEvent(data.getDeclarer(), ann)));

		// inform all players
		for (final JSkatPlayer playerInstance : this.player.values()) {
			playerInstance.startGame(this.data.getDeclarer(), ann);
		}

		this.log.debug(".setGameAnnouncement(): " + this.data.getAnnoucement() + " by " + this.data.getDeclarer() + ", rules=" + this.rules); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Sets the game state from outside
	 *
	 * @param newState
	 *            Game state
	 */
	public void setGameState(final GameState newState) {

		this.data.setGameState(newState);

		if (this.view != null) {

			this.view.setGameState(this.tableName, newState);

			if (newState == GameState.GAME_START) {

				this.view.clearTable(this.tableName);

			} else if (newState == GameState.GAME_OVER) {

				this.view.addGameResult(this.tableName, getGameSummary());

				JSkatEventBus.INSTANCE.post(new ShowCardsCommand(tableName,
						data.getCardsAfterDiscard()));
			}
		}
	}

	/**
	 * Sets the single player from outside
	 *
	 * @param declarer
	 *            Declarer
	 */
	public void setDeclarer(final Player declarer) {

		this.data.setDeclarer(declarer);
		this.view.setDeclarer(this.tableName, declarer);
	}

	/**
	 * Gets the single player
	 *
	 * @return Single player
	 */
	public Player getDeclarer() {
		return this.data.getDeclarer();
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
	public void setMaxSleep(final int newMaxSleep) {

		this.maxSleep = newMaxSleep;
	}

	/**
	 * Gets the game result
	 *
	 * @return Game result
	 */
	public SkatGameResult getGameResult() {

		return this.data.getGameResult();
	}

	/**
	 * Gets a summary of the game
	 *
	 * @return Game summary
	 */
	public GameSummary getGameSummary() {
		return this.data.getGameSummary();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return this.data.getGameState().toString();
	}

	/**
	 * Gets the game announcement
	 *
	 * @return Game announcement
	 */
	public GameAnnouncement getGameAnnouncement() {
		return this.data.getAnnoucement();
	}

	/**
	 * Gets the game state
	 *
	 * @return Game state
	 */
	public GameState getGameState() {
		return this.data.getGameState();
	}
}
