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
package org.jskat.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jskat.control.command.table.ShowCardsCommand;
import org.jskat.control.event.skatgame.BidEvent;
import org.jskat.control.event.skatgame.CardDealEvent;
import org.jskat.control.event.skatgame.ContraEvent;
import org.jskat.control.event.skatgame.GameAnnouncementEvent;
import org.jskat.control.event.skatgame.GameFinishEvent;
import org.jskat.control.event.skatgame.HoldBidEvent;
import org.jskat.control.event.skatgame.PassBidEvent;
import org.jskat.control.event.skatgame.ReEvent;
import org.jskat.control.event.skatgame.SkatGameEvent;
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
public class SkatGame {

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
	 *            Game variant
	 * @param newForeHand
	 *            Fore hand player
	 * @param newMiddleHand
	 *            Middle hand player
	 * @param newRearHand
	 *            Rear hand player
	 */
	public SkatGame(final String newTableName, final GameVariant variant, final JSkatPlayer newForeHand,
			final JSkatPlayer newMiddleHand, final JSkatPlayer newRearHand) {

		tableName = newTableName;
		data = new SkatGameData();
		JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).register(data);

		this.variant = variant;

		player = new HashMap<Player, JSkatPlayer>();
		player.put(Player.FOREHAND, newForeHand);
		player.put(Player.MIDDLEHAND, newMiddleHand);
		player.put(Player.REARHAND, newRearHand);

		// inform all players about the starting of the new game
		for (final Player pos : player.keySet()) {
			getPlayerInstance(pos).newGame(pos);
		}

		setGameState(GameState.GAME_START);
	}

	/**
	 * Runs the skat game.
	 *
	 * @return Result of the skat game
	 */
	public SkatGameResult run() {
		// FIXME jan 11.07.2013: this method is too long, break it down to smaller
		// methods or implement it in another way
		view.setGameState(tableName, data.getGameState());

		do {
			log.debug("Game state: " + data.getGameState());

			switch (data.getGameState()) {
			case GAME_START:
				setGameState(GameState.DEALING);
				break;
			case DEALING:
				dealCards();
				setGameState(GameState.BIDDING);
				break;
			case BIDDING:
				setActivePlayer(Player.MIDDLEHAND);

				if (variant == GameVariant.FORCED_RAMSCH) {
					// ramsch games are enforced
					final GameAnnouncementFactory gaf = GameAnnouncement.getFactory();
					gaf.setGameType(GameType.RAMSCH);
					setGameAnnouncement(gaf.getAnnouncement());
				} else {
					// "normal" game (i.e. no ramsch)
					bidding();
				}

				if (GameType.PASSED_IN.equals(data.getGameType())) {
					setGameState(GameState.PRELIMINARY_GAME_END);
				} else if (GameType.RAMSCH.equals(data.getGameType())) {
					setGameState(GameState.RAMSCH_GRAND_HAND_ANNOUNCING);
				} else {
					view.setDeclarer(tableName, data.getDeclarer());
					setGameState(GameState.PICKING_UP_SKAT);
				}
				break;
			case RAMSCH_GRAND_HAND_ANNOUNCING:
				final boolean grandHandAnnounced = grandHand();

				if (grandHandAnnounced) {
					log.debug(data.getDeclarer() + " is playing grand hand"); //$NON-NLS-1$
					final GameAnnouncementFactory gaf = GameAnnouncement.getFactory();
					gaf.setGameType(GameType.GRAND);
					gaf.setHand(Boolean.TRUE);
					setGameAnnouncement(gaf.getAnnouncement());
					setGameState(GameState.TRICK_PLAYING);
					log.debug("grand hand game started"); //$NON-NLS-1$
					break;
				} else {
					if (JSkatOptions.instance().isSchieberamsch(true)) {
						log.debug("no grand hand - initiating schieberamsch"); //$NON-NLS-1$
						setGameState(GameState.SCHIEBERAMSCH);
					} else {
						log.debug("no grand hand and no schieberamsch - play ramsch"); //$NON-NLS-1$
						setGameState(GameState.TRICK_PLAYING);
					}
				}
				break;
			case SCHIEBERAMSCH:
				schieberamsch();
				final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
				factory.setGameType(GameType.RAMSCH);
				setGameAnnouncement(factory.getAnnouncement());
				setGameState(GameState.TRICK_PLAYING);
				break;
			case PICKING_UP_SKAT:
				setActivePlayer(data.getDeclarer());
				if (pickUpSkat()) {
					setGameState(GameState.DISCARDING);
				} else {
					setGameState(GameState.DECLARING);
				}
				break;
			case DISCARDING:
				setActivePlayer(data.getDeclarer());
				discarding();
				if (!GameState.PRELIMINARY_GAME_END.equals(data.getGameState())) {
					setGameState(GameState.DECLARING);
				}
				break;
			case DECLARING:
				announceGame();
				if (isContraPlayEnabled(ContraCallingTime.AFTER_GAME_ANNOUNCEMENT, 0)) {
					setGameState(GameState.CONTRA);
				} else {
					setGameState(GameState.TRICK_PLAYING);
				}
				break;
			case CONTRA:
				for (final Player player : Player.getOrderedList()) {
					if (isContraEnabledForPlayer(player, ContraCallingTime.AFTER_GAME_ANNOUNCEMENT, 0)) {
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

		} while (data.getGameState() != GameState.GAME_OVER);

		JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName).unregister(data);

		log.debug(data.getGameState().name());
		log.debug("Game moves:");
		for (final SkatGameEvent event : data.getGameMoves()) {
			log.debug(event.toString());
		}

		return getGameResult();
	}

	private void contraRe() {
		if (getActivePlayerInstance().callContra()) {
			JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, new ContraEvent(activePlayer)));
			final Player activePlayerBeforeContraRe = activePlayer;
			setActivePlayer(data.getDeclarer());
			if (getActivePlayerInstance().callRe()) {
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, new ReEvent(activePlayer)));
			}
			setActivePlayer(activePlayerBeforeContraRe);
		}
	}

	private boolean grandHand() {
		boolean grandHandAnnounced = false;

		for (final Player currPlayer : Player.getOrderedList()) {
			setActivePlayer(currPlayer);
			if (!grandHandAnnounced && playGrandHand()) {
				log.debug("Player " + activePlayer + " is playing grand hand.");
				setDeclarer(activePlayer);
				grandHandAnnounced = true;
			} else {
				log.debug("Player " + activePlayer + " doesn't want to play grand hand.");
			}
		}
		return grandHandAnnounced;
	}

	private void schieberamsch() {
		for (final Player currPlayer : Player.getOrderedList()) {
			setActivePlayer(currPlayer);
			if (!pickUpSkat()) {
				log.debug("Player " + currPlayer + " does schieben."); //$NON-NLS-1$
				data.addGeschoben();
				view.setGeschoben(tableName, activePlayer);
			} else {
				log.debug("Player " + currPlayer + " wants to look into skat.");
				view.setSkat(tableName, data.getSkat());
				discarding();
			}
		}
	}

	private void setActivePlayer(final Player newPlayer) {
		activePlayer = newPlayer;
		JSkatEventBus.INSTANCE.post(new ActivePlayerChangedEvent(tableName, activePlayer));
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

		final Map<Player, CardList> dealtCards = new HashMap<>();
		for (final Player player : Player.values()) {
			dealtCards.put(player, new CardList());
		}

		if (deck == null) {
			// Skat game has no cards, yet
			deck = new CardDeck();

			log.debug("shuffling..."); //$NON-NLS-1$
			deck.shuffle();

			log.debug(deck.toString());
		}

		doSleep(maxSleep);

		log.debug("dealing..."); //$NON-NLS-1$

		// deal three rounds of cards
		// deal three cards
		dealCards(3, dealtCards);
		// and put two cards into the skat
		final CardList skat = new CardList(deck.remove(0), deck.remove(0));
		// deal four cards
		dealCards(4, dealtCards);
		// deal three cards
		dealCards(3, dealtCards);

		JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, new CardDealEvent(dealtCards, skat)));

		doSleep(maxSleep);

		log.debug("Fore hand: " + data.getPlayerCards(Player.FOREHAND)); //$NON-NLS-1$
		log.debug("Middle hand: " //$NON-NLS-1$
				+ data.getPlayerCards(Player.MIDDLEHAND));
		log.debug("Rear hand: " + data.getPlayerCards(Player.REARHAND)); //$NON-NLS-1$
		log.debug("Skat: " + data.getSkat()); //$NON-NLS-1$
	}

	/**
	 * Deals a given number of cards to the players
	 *
	 * @param cardCount
	 *            Number of cards to be dealt to a player
	 */
	private void dealCards(final int cardCount, final Map<Player, CardList> dealtCards) {

		for (final Player hand : Player.getOrderedList()) {
			final CardList cards = new CardList();
			for (int j = 0; j < cardCount; j++) {
				// deal amount of cards
				cards.add(deck.remove(0));
			}
			// player can get original card object because Card is immutable
			getPlayerInstance(hand).takeCards(cards);
			dealtCards.get(hand).addAll(cards);
		}
	}

	/**
	 * Controls the bidding of all players
	 */
	private void bidding() {

		int bidValue = 0;

		log.debug("ask middle and fore hand..."); //$NON-NLS-1$

		bidValue = twoPlayerBidding(Player.MIDDLEHAND, Player.FOREHAND, bidValue);

		log.debug("Bid value after first bidding: " //$NON-NLS-1$
				+ bidValue);

		final Player firstWinner = getBiddingWinner(Player.MIDDLEHAND, Player.FOREHAND);

		log.debug("First bidding winner: " + firstWinner); //$NON-NLS-1$
		log.debug("ask rear hand and first winner..."); //$NON-NLS-1$

		bidValue = twoPlayerBidding(Player.REARHAND, firstWinner, bidValue);

		log.debug("Bid value after second bidding: " //$NON-NLS-1$
				+ bidValue);

		// get second winner
		Player secondWinner = getBiddingWinner(Player.REARHAND, firstWinner);

		if (secondWinner == Player.FOREHAND && bidValue == 0) {

			log.debug("Check whether fore hand holds at least one bid"); //$NON-NLS-1$

			setActivePlayer(Player.FOREHAND);

			// check whether fore hand holds at least one bid
			if (getPlayerInstance(Player.FOREHAND).bidMore(18) > -1) {

				log.debug("Fore hand holds 18"); //$NON-NLS-1$
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, new BidEvent(secondWinner, 18)));
			} else {

				log.debug("Fore hand passes too"); //$NON-NLS-1$
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, new PassBidEvent(Player.FOREHAND)));
				secondWinner = null;
			}
		}

		if (secondWinner != null) {
			// there is a winner of the bidding
			setDeclarer(secondWinner);
			setActivePlayer(secondWinner);

			log.debug("Player " + data.getDeclarer() //$NON-NLS-1$
					+ " wins the bidding."); //$NON-NLS-1$
		} else {
			// FIXME (jansch 02.01.2012) use cloned rule options here (see
			// MantisBT: 0000037)
			final JSkatOptions options = JSkatOptions.instance();

			if (options.isPlayRamsch() && options.isRamschEventNoBid()) {
				log.debug("Playing ramsch due to no bid"); //$NON-NLS-1$
				final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
				factory.setGameType(GameType.RAMSCH);
				setGameAnnouncement(factory.getAnnouncement());
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName,
						new GameAnnouncementEvent(data.getDeclarer(), data.getAnnoucement())));
				setActivePlayer(Player.FOREHAND);
				// do not call "setGameAnnouncement(..)" here!
			} else {
				// pass in
				final GameAnnouncementFactory factory = GameAnnouncement.getFactory();
				factory.setGameType(GameType.PASSED_IN);
				setGameAnnouncement(factory.getAnnouncement());
			}
		}

		doSleep(maxSleep);
	}

	private void informPlayersAboutBid(final Player bidPlayer, final int bidValue) {
		// inform all players about the last bid
		for (final JSkatPlayer playerInstance : player.values()) {
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
	private int twoPlayerBidding(final Player announcer, final Player hearer, final int startBidValue) {

		int currBidValue = startBidValue;
		boolean announcerPassed = false;
		boolean hearerPassed = false;

		while (!announcerPassed && !hearerPassed) {

			// get bid value
			final int nextBidValue = SkatConstants.getNextBidValue(currBidValue);
			view.setBidValueToMake(tableName, nextBidValue);
			// ask player
			setActivePlayer(announcer);
			final int announcerBidValue = getPlayerInstance(announcer).bidMore(nextBidValue);

			if (announcerBidValue > -1 && SkatConstants.bidOrder.contains(Integer.valueOf(announcerBidValue))) {

				log.debug("announcer bids " + announcerBidValue); //$NON-NLS-1$

				// announcing hand holds bid
				currBidValue = announcerBidValue;

				data.addPlayerBid(announcer, announcerBidValue);
				informPlayersAboutBid(announcer, announcerBidValue);
				JSkatEventBus.INSTANCE
						.post(new TableGameMoveEvent(tableName, new BidEvent(announcer, announcerBidValue)));

				setActivePlayer(hearer);
				if (getPlayerInstance(hearer).holdBid(currBidValue)) {

					log.debug("hearer holds " + currBidValue); //$NON-NLS-1$

					// hearing hand holds bid
					data.addPlayerBid(hearer, announcerBidValue);
					informPlayersAboutBid(hearer, announcerBidValue);
					JSkatEventBus.INSTANCE
							.post(new TableGameMoveEvent(tableName, new HoldBidEvent(hearer, announcerBidValue)));

				} else {

					log.debug("hearer passed at " + announcerBidValue); //$NON-NLS-1$

					// hearing hand passed
					hearerPassed = true;
					data.setPlayerPass(hearer, true);
					JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, new PassBidEvent(hearer)));
				}
			} else {

				log.debug("announcer passed at " + nextBidValue); //$NON-NLS-1$

				// announcing hand passes
				announcerPassed = true;
				data.setPlayerPass(announcer, true);
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, new PassBidEvent(announcer)));
			}
		}

		return currBidValue;
	}

	private Player getBiddingWinner(final Player announcer, final Player hearer) {

		Player biddingWinner = null;

		if (data.isPlayerPass(announcer)) {
			biddingWinner = hearer;
		} else if (data.isPlayerPass(hearer)) {
			biddingWinner = announcer;
		}

		return biddingWinner;
	}

	private void discarding() {

		final JSkatPlayer activePlayerInstance = getActivePlayerInstance();

		view.setSkat(tableName, data.getSkat());

		log.debug("Player " + activePlayer + " looks into the skat..."); //$NON-NLS-1$ //$NON-NLS-2$
		log.debug("Skat before discarding: " + data.getSkat()); //$NON-NLS-1$

		final CardList skatBefore = new CardList(data.getSkat());

		// create a clone of the skat before sending it to the player
		// otherwise the player could change the skat after discarding
		activePlayerInstance.takeSkat(skatBefore);
		data.addSkatToPlayer(activePlayer);

		// ask player for the cards to be discarded
		// cloning is done to prevent the player
		// from manipulating the skat afterwards
		final CardList discardedSkat = new CardList();
		discardedSkat.addAll(activePlayerInstance.discardSkat());

		if (!checkDiscardedCards(activePlayer, discardedSkat)) {
			view.showAIPlayedSchwarzMessageDiscarding(activePlayerInstance.getPlayerName(), discardedSkat);
			endGameBecauseOfSchwarzPlaying(activePlayer);
		} else {
			log.debug("Discarded cards: " + discardedSkat); //$NON-NLS-1$

			data.setDiscardedSkat(activePlayer, discardedSkat);
			if (!activePlayerInstance.isHumanPlayer()) {
				// human player has changed the cards in the GUI already
				view.setDiscardedSkat(tableName, activePlayer, skatBefore, discardedSkat);
			}
		}
	}

	private boolean checkDiscardedCards(final Player player, final CardList discardedSkat) {

		// TODO move this to skat rules?
		boolean result = true;

		if (discardedSkat == null) {

			log.error("Player is fooling!!! Skat is empty!"); //$NON-NLS-1$
			result = false;
		} else if (discardedSkat.size() != 2) {

			log.error("Player is fooling!!! Skat doesn't have two cards!"); //$NON-NLS-1$
			result = false;
		} else if (discardedSkat.get(0) == discardedSkat.get(1)) {
			log.error("Player is fooling!!! Skat cards are identical!"); //$NON-NLS-1$
			result = false;
		} else if (!playerHasCard(player, discardedSkat.get(0)) || !playerHasCard(player, discardedSkat.get(1))) {
			log.error("Player is fooling!!! Player doesn't have had discarded card! Dis"); //$NON-NLS-1$
			result = false;
		}
		// TODO check for jacks in the discarded skat in ramsch games

		return result;
	}

	private void announceGame() {

		log.debug("declaring game..."); //$NON-NLS-1$

		// TODO check for valid game announcements
		final GameAnnouncement ann = getPlayerInstance(data.getDeclarer()).announceGame();
		if (ann != null) {
			setGameAnnouncement(ann);
		} else {
			view.showErrorMessage(strings.getString("invalid_game_announcement_title"), //$NON-NLS-1$
					strings.getString("invalid_game_announcement_message", ann)); //$NON-NLS-1$
		}

		doSleep(maxSleep);
	}

	private void playTricks() {

		for (int trickNo = 0; trickNo < 10; trickNo++) {

			log.debug("=============== Play trick " + (trickNo + 1) + " ==============="); //$NON-NLS-1$ //$NON-NLS-2$
			doSleep(maxSleep);

			final Player trickForehand = getTrickForeHand(trickNo);
			setActivePlayer(trickForehand);

			informPlayersAboutNewTrick(trickNo, trickForehand);

			// Ask players for their cards
			log.debug("fore hand plays"); //$NON-NLS-1$
			if (isContraEnabledForPlayer(activePlayer, ContraCallingTime.BEFORE_FIRST_CARD, trickNo)) {
				setGameState(GameState.CONTRA);
				contraRe();
			}
			setGameState(GameState.TRICK_PLAYING);

			playCard(trickForehand, null, activePlayer);

			if (data.isGameFinished()) {
				break;
			}

			doSleep(maxSleep);

			log.debug("middle hand plays"); //$NON-NLS-1$
			setActivePlayer(activePlayer.getLeftNeighbor());
			if (isContraEnabledForPlayer(activePlayer, ContraCallingTime.BEFORE_FIRST_CARD, trickNo)) {
				setGameState(GameState.CONTRA);
				contraRe();
			}
			setGameState(GameState.TRICK_PLAYING);

			playCard(trickForehand, data.getCurrentTrick().getFirstCard(), activePlayer);

			if (data.isGameFinished()) {
				break;
			}

			doSleep(maxSleep);

			log.debug("rear hand plays"); //$NON-NLS-1$
			setActivePlayer(activePlayer.getLeftNeighbor());
			if (isContraEnabledForPlayer(activePlayer, ContraCallingTime.BEFORE_FIRST_CARD, trickNo)) {
				setGameState(GameState.CONTRA);
				contraRe();
			}
			setGameState(GameState.TRICK_PLAYING);

			playCard(trickForehand, data.getCurrentTrick().getFirstCard(), activePlayer);

			if (data.isGameFinished()) {
				break;
			}

			doSleep(maxSleep);

			final Trick lastTrick = data.getLastCompletedTrick();
			data.addPlayerPoints(lastTrick.getTrickWinner(), lastTrick.getValue());

			informPlayersAboutCompletedTrick(lastTrick);

			// Check for preliminary ending of a null game
			if (GameType.NULL.equals(data.getGameType())) {

				if (lastTrick.getTrickWinner() == data.getDeclarer()) {
					// declarer has won a trick
					setGameState(GameState.PRELIMINARY_GAME_END);
				}
			}

			log.debug("Trick cards: " + lastTrick.getCardList()); //$NON-NLS-1$
			logPlayerPoints();

			if (getActivePlayerInstance().isAIPlayer()) {
				doSleep(JSkatOptions.instance().getWaitTimeAfterTrick() * 1000);
			}

			if (data.isGameFinished()) {
				break;
			}
		}

		addSkatPointsToPlayerPoints();

		// set schneider/schwarz/jungfrau/durchmarsch flags
		switch (data.getGameType()) {
		case CLUBS:
		case SPADES:
		case HEARTS:
		case DIAMONDS:
		case GRAND:
			data.setSchneiderSchwarz();
			break;
		case RAMSCH:
			data.setJungfrauDurchmarsch();
			break;
		case NULL:
		case PASSED_IN:
			// do nothing
			break;
		}
	}

	private Boolean isContraPlayEnabled(final ContraCallingTime gameTime, final int trickNo) {
		final JSkatOptions options = JSkatOptions.instance();
		if (!GameVariant.FORCED_RAMSCH.equals(variant) && options.isPlayContra(true)
				&& options.getContraCallingTime() == gameTime && isGameWithDeclarer()) {
			if (ContraCallingTime.AFTER_GAME_ANNOUNCEMENT == gameTime) {
				return true;
			} else if (ContraCallingTime.BEFORE_FIRST_CARD == gameTime && trickNo == 0) {
				return true;
			}
		}
		return false;
	}

	private Boolean isGameWithDeclarer() {
		final GameType gameType = data.getGameType();
		if (gameType == GameType.CLUBS || gameType == GameType.SPADES || gameType == GameType.HEARTS
				|| gameType == GameType.DIAMONDS || gameType == GameType.GRAND || gameType == GameType.NULL) {
			return true;
		}
		return false;
	}

	private Boolean isContraEnabledForPlayer(final Player player, final ContraCallingTime gameTime, final int trickNo) {
		if (isContraPlayEnabled(gameTime, trickNo) && isNoContraCalledYet() && isPlayerOpponent(player)
				&& isPlayerBidHighEnoughForContra(player)) {
			return true;
		}

		return false;
	}

	private boolean isPlayerBidHighEnoughForContra(final Player player) {
		final JSkatOptions options = JSkatOptions.instance();
		if (options.isContraAfterBid18() && data.getMaxPlayerBid(player) > 0) {
			return true;
		}
		return true;
	}

	private boolean isPlayerOpponent(final Player player) {
		return player != data.getDeclarer();
	}

	private boolean isNoContraCalledYet() {
		return !data.isContra();
	}

	private void informPlayersAboutCompletedTrick(final Trick trick) {
		for (final Player currPosition : Player.getOrderedList()) {
			getPlayerInstance(currPosition).showTrick((Trick) trick.clone());
		}
	}

	private void informPlayersAboutNewTrick(final int trickNo, final Player trickForehand) {
		for (final Player currPosition : Player.getOrderedList()) {
			getPlayerInstance(currPosition).newTrick(trickNo, trickForehand);
		}
	}

	private Player getTrickForeHand(final int currentTrickNo) {
		Player trickForeHand = null;
		if (currentTrickNo == 0) {
			// first trick
			trickForeHand = Player.FOREHAND;
		} else {
			// get last trick winner as fore hand of next trick
			trickForeHand = data.getTrickWinner(currentTrickNo - 1);
		}
		return trickForeHand;
	}

	private void logPlayerPoints() {
		log.debug("Points: forehand: " + data.getPlayerPoints(Player.FOREHAND) + //$NON-NLS-1$
				" middlehand: " //$NON-NLS-1$
				+ data.getPlayerPoints(Player.MIDDLEHAND) + " rearhand: " //$NON-NLS-1$
				+ data.getPlayerPoints(Player.REARHAND));
	}

	private void addSkatPointsToPlayerPoints() {
		log.debug("Skat: " + data.getSkat());
		if (data.getGameType() == GameType.RAMSCH) {
			addSkatPointsToPlayerPointsInRamschGames();
		} else {
			// for all the other games, points to the declarer
			data.addPlayerPoints(data.getDeclarer(), data.getSkat().getTotalValue());
		}
		logPlayerPoints();
	}

	private void addSkatPointsToPlayerPointsInRamschGames() {
		if (JSkatOptions.instance().getRamschSkatOwner() == RamschSkatOwner.LAST_TRICK) {
			try {
				final Player lastTrickWinner = data.getLastTrickWinner();
				if (lastTrickWinner != null) {
					log.debug("Skat cards (" + data.getSkat().getTotalValue() + " points) are added to player @ " //$NON-NLS-1$ //$NON-NLS-2$
							+ lastTrickWinner + " (= last trick)"); //$NON-NLS-1$
					data.addPlayerPoints(lastTrickWinner, data.getSkat().getTotalValue());
				}
			} catch (final IllegalArgumentException exception) {
				// IllegalArgumentException can be thrown if a game was ended
				// preliminary by a player playing Schwarz
				log.warn("Skat cards cannot be added to winner of final trick - trick winner is unknown"); //$NON-NLS-1$
			}
		} else if (JSkatOptions.instance().getRamschSkatOwner() == RamschSkatOwner.LOSER) {
			int maxPoints = -1;
			Player looser = null;
			for (final Player player : Player.values()) {
				final int playerPoints = data.getPlayerPoints(player);
				if (playerPoints > maxPoints) {
					maxPoints = playerPoints;
					looser = player;
				}
			}
			data.addPlayerPoints(looser, data.getSkat().getTotalValue());
		}
	}

	private void playCard(final Player trickForeHand, final Card firstTrickCard, final Player currPlayer) {

		Card playedCard = null;
		final JSkatPlayer skatPlayer = getActivePlayerInstance();

		boolean cardAccepted = false;
		boolean aiPlayerPlayedSchwarz = false;

		while (!cardAccepted && !aiPlayerPlayedSchwarz) {

			try {
				// ask player for the next card
				playedCard = skatPlayer.playCard();
			} catch (final Exception exp) {
				log.error("Exception thrown by player " + skatPlayer + " playing " + currPlayer + ": " + exp); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (!skatPlayer.isHumanPlayer()) {
					aiPlayerPlayedSchwarz = true;
				}
			}

			log.debug(playedCard + " " + data); //$NON-NLS-1$

			if (isCardSchwarzPlay(skatPlayer, currPlayer, firstTrickCard, playedCard)) {
				if (skatPlayer.isHumanPlayer()) {
					view.showCardNotAllowedMessage(playedCard);
				} else {
					view.showAIPlayedSchwarzMessageCardPlay(skatPlayer.getPlayerName(), playedCard);
					aiPlayerPlayedSchwarz = true;
				}
			} else {

				cardAccepted = true;
			}
		}

		if (playedCard != null) {
			// TODO: code duplication with SkatGameReplayer.oneStepForward()
			if (data.getCurrentTrick() != null && data.getCurrentTrick().getFirstCard() == null) {
				JSkatEventBus.TABLE_EVENT_BUSSES.get(tableName)
						.post(new TrickCompletedEvent(data.getLastCompletedTrick()));
			}

			JSkatEventBus.INSTANCE
					.post(new TableGameMoveEvent(tableName, new TrickCardPlayedEvent(currPlayer, playedCard)));

			for (final JSkatPlayer playerInstance : player.values()) {
				// inform all players
				// cloning of card is not neccessary, because Card is immutable
				playerInstance.cardPlayed(currPlayer, playedCard);
			}

			log.debug("playing card " + playedCard); //$NON-NLS-1$
		}

		if (aiPlayerPlayedSchwarz) {
			// end game immediately
			endGameBecauseOfSchwarzPlaying(currPlayer);
		}
	}

	private void endGameBecauseOfSchwarzPlaying(final Player currentPlayer) {
		data.getResult().setSchwarz(true);
		if (data.getDeclarer().equals(currentPlayer)) {
			// declarer played schwarz
			data.getResult().setWon(false);
		} else {
			// opponent played schwarz
			data.getResult().setWon(true);
		}
		data.setGameState(GameState.PRELIMINARY_GAME_END);
	}

	private boolean isCardSchwarzPlay(final JSkatPlayer skatPlayer, final Player position, final Card firstTrickCard,
			final Card playedCard) {
		boolean isSchwarz = false;
		if (playedCard == null) {

			log.error("Player is fooling!!! Did not play a card!"); //$NON-NLS-1$
			isSchwarz = true;

		} else if (!playerHasCard(position, playedCard)) {

			log.error("Player (" + skatPlayer + ") is fooling!!! Doesn't have card " + playedCard + "!"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			isSchwarz = true;

		} else if (!rules.isCardAllowed(data.getGameType(), firstTrickCard, data.getPlayerCards(position),
				playedCard)) {

			log.error(
					"Player " + skatPlayer.getClass().toString() + " card not allowed: " + playedCard + " game type: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							+ data.getGameType() + " first trick card: " //$NON-NLS-1$
							+ firstTrickCard + " player cards: " //$NON-NLS-1$
							+ data.getPlayerCards(position));
			isSchwarz = true;
		}

		return isSchwarz;
	}

	private JSkatPlayer getActivePlayerInstance() {
		return player.get(activePlayer);
	}

	private JSkatPlayer getPlayerInstance(final Player position) {
		return player.get(position);
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

		log.debug("Player " + player + " has card: player cards: " + data.getPlayerCards(player) //$NON-NLS-1$
				+ " card to check: " + card);

		for (final Card handCard : data.getPlayerCards(player)) {

			if (handCard.equals(card)) {

				result = true;
			}
		}

		return result;
	}

	private void calculateGameValue() {

		log.debug("Calculate game value"); //$NON-NLS-1$

		// FIXME (jan 07.12.2010) don't let a data class calculate it's values
		data.calcResult();

		log.debug("game value=" + data.getResult() + ", bid value=" //$NON-NLS-1$ //$NON-NLS-2$
				+ data.getMaxBidValue());

		log.debug("Final game result: lost:" + data.isGameLost() + //$NON-NLS-1$
				" game value: " + data.getResult()); //$NON-NLS-1$

		log.debug("Final result: " + data.getDeclarerScore() + "/" + data.getOpponentScore());

		for (final JSkatPlayer playerInstance : player.values()) {
			playerInstance.setGameSummary(data.getGameSummary());
			playerInstance.finalizeGame();
		}

		doSleep(maxSleep);
	}

	private void doSleep(final int milliseconds) {

		if (milliseconds > 0) {
			try {
				Thread.sleep(milliseconds);
			} catch (final InterruptedException e) {
				log.warn("sleep was interrupted..."); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Sets the view for the game
	 *
	 * @param newView
	 *            View
	 *
	 * @deprecated don't rely on setting a view anymore as we want to use event
	 *             busses now.
	 */
	@Deprecated
	public void setView(final JSkatView newView) {

		view = newView;
	}

	/**
	 * Sets a new logger for the skat game
	 *
	 * @param newLogger
	 *            New logger
	 */
	public void setLogger(final Logger newLogger) {
		log = newLogger;
	}

	/**
	 * Sets the cards from outside
	 *
	 * @param newDeck
	 *            Card deck
	 */
	public void setCardDeck(final CardDeck newDeck) {

		deck = newDeck;
	}

	/**
	 * Sets the game announcement from the outside
	 *
	 * @param ann
	 *            Game announcement
	 */
	public void setGameAnnouncement(final GameAnnouncement ann) {

		data.setAnnouncement(ann);
		rules = SkatRuleFactory.getSkatRules(data.getGameType());
		JSkatEventBus.INSTANCE
				.post(new TableGameMoveEvent(tableName, new GameAnnouncementEvent(data.getDeclarer(), ann)));

		// inform all players
		for (final JSkatPlayer playerInstance : player.values()) {
			playerInstance.startGame(data.getDeclarer(), ann);
		}

		log.debug(".setGameAnnouncement(): " + data.getAnnoucement() + " by " + data.getDeclarer() //$NON-NLS-1$ //$NON-NLS-2$
				+ ", rules=" + rules); //$NON-NLS-1$
	}

	/**
	 * Sets the game state from outside
	 *
	 * @param newState
	 *            Game state
	 */
	public void setGameState(final GameState newState) {

		data.setGameState(newState);

		if (view != null) {

			view.setGameState(tableName, newState);

			if (newState == GameState.GAME_OVER) {

				// FIXME: merge this event with the command
				JSkatEventBus.INSTANCE.post(new TableGameMoveEvent(tableName, new GameFinishEvent(getGameSummary())));

				JSkatEventBus.INSTANCE.post(new ShowCardsCommand(tableName, data.getCardsAfterDiscard()));
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

		data.setDeclarer(declarer);
		view.setDeclarer(tableName, declarer);
	}

	/**
	 * Gets the single player
	 *
	 * @return Single player
	 */
	public Player getDeclarer() {
		return data.getDeclarer();
	}

	/**
	 * Gets whether a game was won or not
	 *
	 * @return TRUE if the game was won
	 */
	public boolean isGameWon() {

		return data.isGameWon();
	}

	/**
	 * Gets the maximum sleep time
	 *
	 * @return Maximum sleep time
	 */
	public int getMaxSleep() {

		return maxSleep;
	}

	/**
	 * Sets the maximum sleep time
	 *
	 * @param newMaxSleep
	 *            Maximum sleep time
	 */
	public void setMaxSleep(final int newMaxSleep) {

		maxSleep = newMaxSleep;
	}

	/**
	 * Gets the game result
	 *
	 * @return Game result
	 */
	public SkatGameResult getGameResult() {

		return data.getGameResult();
	}

	/**
	 * Gets the game moves.
	 *
	 * @return List of game moves
	 */
	public List<SkatGameEvent> getGameMoves() {
		return data.getGameMoves();
	}

	/**
	 * Gets a summary of the game
	 *
	 * @return Game summary
	 */
	public GameSummary getGameSummary() {
		return data.getGameSummary();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		return data.getGameState().toString();
	}

	/**
	 * Gets the game announcement
	 *
	 * @return Game announcement
	 */
	public GameAnnouncement getGameAnnouncement() {
		return data.getAnnoucement();
	}

	/**
	 * Gets the game state
	 *
	 * @return Game state
	 */
	public GameState getGameState() {
		return data.getGameState();
	}
}
