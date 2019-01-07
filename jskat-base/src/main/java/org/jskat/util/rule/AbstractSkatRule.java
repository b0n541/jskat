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
package org.jskat.util.rule;

import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of skat rules
 */
public abstract class AbstractSkatRule implements SkatRule {

	private static Logger log = LoggerFactory.getLogger(AbstractSkatRule.class);

	/**
	 * Checks whether a game was overbid
	 * 
	 * @param gameData
	 *            Game data
	 * @return TRUE if the game was overbid
	 */
	@Override
	public boolean isOverbid(final SkatGameData gameData) {
		return gameData.getMaxBidValue() > getGameValueForWonGame(gameData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Player calculateTrickWinner(final GameType gameType,
			final Trick trick) {

		Player trickWinner = null;
		Card first = trick.getFirstCard();
		Card second = trick.getSecondCard();
		Card third = trick.getThirdCard();
		Player trickForeHand = trick.getForeHand();

		if (isCardBeatsCard(gameType, first, second)) {

			if (isCardBeatsCard(gameType, second, third)) {
				// trick winner is rear hand
				trickWinner = trickForeHand.getRightNeighbor();
			} else {
				// trick winner is middle hand
				trickWinner = trickForeHand.getLeftNeighbor();
			}
		} else {

			if (isCardBeatsCard(gameType, first, third)) {
				// trick winner is rear hand
				trickWinner = trickForeHand.getRightNeighbor();
			} else {
				// trick winner is fore hand
				trickWinner = trickForeHand;
			}
		}

		log.debug("Trick fore hand: " + trickForeHand); //$NON-NLS-1$
		log.debug("Trick winner: " + trickWinner); //$NON-NLS-1$

		return trickWinner;
	}
}
