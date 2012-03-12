/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
 * Copyright (C) 2012-03-13
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.ai;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameSummary;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.BasicSkatRules;
import org.jskat.util.rule.SkatRuleFactory;

/**
 * Abstract JSkat player implementation
 */
public abstract class AbstractJSkatPlayer implements JSkatPlayer {

	private Log log = LogFactory.getLog(AbstractJSkatPlayer.class);

	/** Player name */
	protected String playerName;
	/** Player state */
	protected JSkatPlayer.PlayerState playerState;
	/** Player knowledge */
	protected PlayerKnowledge knowledge = new PlayerKnowledge();
	/** Skat rules for the current skat series */
	protected BasicSkatRules rules;
	/** Summary of the skat game */
	protected GameSummary gameSummary;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setPlayerName(String newPlayerName) {

		playerName = newPlayerName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPlayerName() {

		return playerName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setUpBidding() {

		setState(PlayerState.BIDDING);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void newGame(Player newPosition) {

		playerState = null;
		rules = null;
		gameSummary = null;
		knowledge.initializeVariables();
		knowledge.setPlayerPosition(newPosition);

		preparateForNewGame();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void takeCard(Card newCard) {

		knowledge.addCard(newCard);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean playGrandHand() {

		return false;
	}

	/**
	 * Sorts the card according a game type
	 * 
	 * @param sortGameType
	 *            Game type
	 */
	protected final void sortCards(GameType sortGameType) {

		knowledge.getMyCards().sort(sortGameType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void startGame(Player newDeclarer, GameAnnouncement game) {

		playerState = PlayerState.PLAYING;
		knowledge.setDeclarer(newDeclarer);
		knowledge.setGame(game);
		knowledge.getMyCards().sort(game.getGameType());

		rules = SkatRuleFactory.getSkatRules(game.getGameType());
		if (!GameType.PASSED_IN.equals(game.getGameType())) {
			log.debug("Starting game for " + getPlayerName() + ": " + game.getGameType() + " (rules=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ rules.getClass() + ")"); //$NON-NLS-1$
		}

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
	 * {@inheritDoc}
	 */
	@Override
	public final void takeSkat(CardList skatCards) {

		log.debug("Skat cards: " + skatCards); //$NON-NLS-1$

		knowledge.setSkat(skatCards);
		knowledge.getMyCards().addAll(skatCards);
	}

	/**
	 * Sets the state of the player
	 * 
	 * @param newState
	 *            State to be set
	 */
	protected final void setState(JSkatPlayer.PlayerState newState) {

		playerState = newState;
	}

	/**
	 * {@inheritDoc}
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

		log.debug("game type: " + knowledge.getGameType()); //$NON-NLS-1$
		log.debug("player cards (" + knowledge.getMyCards().size() + "): " + knowledge.getMyCards()); //$NON-NLS-1$ //$NON-NLS-2$
		log.debug("trick size: " + trick.size()); //$NON-NLS-1$

		for (Card card : knowledge.getMyCards()) {

			if (trick.size() > 0
					&& rules.isCardAllowed(knowledge.getGameType(), trick.get(0), knowledge.getMyCards(), card)) {

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
	 * {@inheritDoc}
	 */
	@Override
	public final void cardPlayed(Player player, Card card) {

		knowledge.setCardPlayed(player, card);

		if (player == knowledge.getPlayerPosition()) {
			// remove this card from counter
			knowledge.removeCard(card);
			knowledge.getMyCards().remove(card);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void newTrick(Trick trick) {
		knowledge.setCurrentTrick(trick);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void showTrick(Trick trick) {
		knowledge.addTrick(trick);
		knowledge.clearTrickCards();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isHumanPlayer() {

		return !isAIPlayer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isDeclarer() {

		boolean result = false;

		if (GameType.RAMSCH.equals(knowledge.getGameType())
				|| knowledge.getDeclarer().equals(knowledge.getPlayerPosition())) {

			result = true;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void lookAtOuvertCards(CardList ouvertCards) {

		knowledge.getSinglePlayerCards().addAll(ouvertCards);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameSummary(GameSummary gameSummary) {

		this.gameSummary = gameSummary;
	}

	/**
	 * Sets a new logger for the abstract skat player
	 * 
	 * @param newLogger
	 *            New logger
	 */
	public void setLogger(Log newLogger) {
		log = newLogger;
	}
}