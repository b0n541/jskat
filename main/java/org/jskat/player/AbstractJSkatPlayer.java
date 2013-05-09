/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.0
 * Copyright (C) 2013-05-09
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.player;

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameSummary;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.rule.SkatRule;
import org.jskat.util.rule.SkatRuleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract JSkat player implementation
 */
public abstract class AbstractJSkatPlayer implements JSkatPlayer {

	private Logger log = LoggerFactory.getLogger(AbstractJSkatPlayer.class);

	/** Player name */
	protected String playerName;
	/** Player state */
	protected JSkatPlayer.PlayerState playerState;
	/** Player knowledge */
	protected PlayerKnowledge knowledge = new PlayerKnowledge();
	/** Skat rules for the current skat series */
	protected SkatRule rules;
	/** Summary of the skat game */
	protected GameSummary gameSummary;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setPlayerName(final String newPlayerName) {

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
	public final void newGame(final Player newPosition) {

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
	public final void takeCard(final Card newCard) {

		knowledge.addOwnCard(newCard);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean playGrandHand() {

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void startGame(final Player newDeclarer,
			final GameAnnouncement game) {

		playerState = PlayerState.PLAYING;
		knowledge.setDeclarer(newDeclarer);
		knowledge.setGame(game);

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
	public final void takeSkat(final CardList skatCards) {

		log.debug("Skat cards: " + skatCards); //$NON-NLS-1$

		knowledge.setSkat(skatCards);
		knowledge.addOwnCards(skatCards);
	}

	/**
	 * Sets the state of the player
	 * 
	 * @param newState
	 *            State to be set
	 */
	protected final void setState(final JSkatPlayer.PlayerState newState) {

		playerState = newState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void bidByPlayer(final Player player, final int bidValue) {

		knowledge.setHighestBid(player, bidValue);
	}

	/**
	 * Gets all playable cards
	 * 
	 * @param trick
	 * @return CardList with all playable cards
	 */
	protected final CardList getPlayableCards(final CardList trick) {

		boolean isCardAllowed = false;
		CardList result = new CardList();

		log.debug("game type: " + knowledge.getGameType()); //$NON-NLS-1$
		log.debug("player cards (" + knowledge.getOwnCards().size() + "): " + knowledge.getOwnCards()); //$NON-NLS-1$ //$NON-NLS-2$
		log.debug("trick size: " + trick.size()); //$NON-NLS-1$

		for (Card card : knowledge.getOwnCards()) {

			if (trick.size() > 0
					&& rules.isCardAllowed(knowledge.getGameType(),
							trick.get(0), knowledge.getOwnCards(), card)) {

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
	public final void cardPlayed(final Player player, final Card card) {

		knowledge.setCardPlayed(player, card);

		if (player == knowledge.getPlayerPosition()) {
			// remove this card from counter
			knowledge.removeOwnCard(card);
		} else {
			knowledge.removeCard(card);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void newTrick(final Trick trick) {
		knowledge.setCurrentTrick(trick);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void showTrick(final Trick trick) {
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
				|| knowledge.getDeclarer()
						.equals(knowledge.getPlayerPosition())) {

			result = true;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void lookAtOuvertCards(final CardList ouvertCards) {

		knowledge.getSinglePlayerCards().addAll(ouvertCards);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CardList discardSkat() {

		CardList result = new CardList();

		log.debug("Player cards before discarding: " + knowledge.getOwnCards()); //$NON-NLS-1$

		result.addAll(getCardsToDiscard());

		knowledge.removeOwnCards(result.getImmutableCopy());

		log.debug("Player cards after discarding: " + knowledge.getOwnCards()); //$NON-NLS-1$

		return result;
	}

	protected abstract CardList getCardsToDiscard();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGameSummary(final GameSummary gameSummary) {

		this.gameSummary = gameSummary;
	}

	/**
	 * Sets a new logger for the abstract skat player
	 * 
	 * @param newLogger
	 *            New logger
	 */
	public void setLogger(final Logger newLogger) {
		log = newLogger;
	}
}