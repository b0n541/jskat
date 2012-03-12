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
package org.jskat.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jskat.util.CardList;
import org.jskat.util.GameType;

/**
 * Game announcement
 * 
 * An object of this class is returned by an AI player for game announcement
 */
public class GameAnnouncement {

	static final Log log = LogFactory.getLog(GameAnnouncement.class);
	/**
	 * Game type
	 */
	GameType gameType;
	/**
	 * Discarded cards
	 */
	CardList discardedCards = new CardList();
	/**
	 * Ouvert announcement
	 */
	Boolean ouvert = Boolean.FALSE;
	/**
	 * Hand announcement
	 */
	Boolean hand = Boolean.FALSE;
	/**
	 * Schneider announcement
	 */
	Boolean schneider = Boolean.FALSE;
	/**
	 * Schwarz announcement
	 */
	Boolean schwarz = Boolean.FALSE;

	/**
	 * Constructor
	 */
	GameAnnouncement() {
	}

	/**
	 * Gets the factory for a {@link GameAnnouncement}
	 * 
	 * @return Factory
	 */
	public static GameAnnouncementFactory getFactory() {
		return new GameAnnouncementFactory();
	}

	/**
	 * Factory for a {@link GameAnnouncement}
	 */
	public final static class GameAnnouncementFactory {

		private GameAnnouncement tmpAnnouncement;

		GameAnnouncementFactory() {
			tmpAnnouncement = new GameAnnouncement();
		}

		/**
		 * Gets the {@link GameAnnouncement}
		 * 
		 * @return Game announcement
		 */
		public final GameAnnouncement getAnnouncement() {
			GameAnnouncement result = null;
			if (validate()) {
				result = tmpAnnouncement;
				tmpAnnouncement = new GameAnnouncement();
			} else {
				throw new RuntimeException("Game announcement not valid."); //$NON-NLS-1$
			}
			return result;
		}

		/**
		 * Sets the {@link GameType}
		 * 
		 * @param gameType
		 *            Game type
		 */
		public final void setGameType(GameType gameType) {
			tmpAnnouncement.gameType = gameType;
		}

		/**
		 * Sets the discarded cards
		 * 
		 * @param discardedCards
		 *            Discarded cards
		 */
		public final void setDiscardedCards(CardList discardedCards) {
			tmpAnnouncement.discardedCards.addAll(discardedCards);
		}

		/**
		 * Sets the flag for a hand game
		 * 
		 * @param isHand
		 *            TRUE, if a hand game was announced
		 */
		public final void setHand(Boolean isHand) {
			tmpAnnouncement.hand = isHand;
		}

		/**
		 * Sets the flag for an ouvert game
		 * 
		 * @param isOuvert
		 *            TRUE, if an ouvert game was announced
		 */
		public final void setOuvert(Boolean isOuvert) {
			tmpAnnouncement.ouvert = isOuvert;
		}

		/**
		 * Sets the flag for a schneider game
		 * 
		 * @param isSchneider
		 *            TRUE, if schneider was announced
		 */
		public final void setSchneider(Boolean isSchneider) {
			tmpAnnouncement.schneider = isSchneider;
		}

		/**
		 * Sets the flag for a schwarz game
		 * 
		 * @param isSchwarz
		 *            TRUE, if a schwarz was announced
		 */
		public final void setSchwarz(Boolean isSchwarz) {
			tmpAnnouncement.schwarz = isSchwarz;
		}

		private boolean validate() {
			if (tmpAnnouncement.gameType == null) {
				log.debug("gameType is null"); //$NON-NLS-1$
				return false;
			} else if (tmpAnnouncement.isHand() && tmpAnnouncement.discardedCards.size() > 0) {
				log.debug("hand=" + tmpAnnouncement.isHand() + ", size=" + tmpAnnouncement.discardedCards.size()); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			return true;
		}
	}

	/**
	 * Gets the game type
	 * 
	 * @return Game type
	 */
	public final GameType getGameType() {

		return gameType;
	}

	/**
	 * Gets the discarded cards
	 * 
	 * @return Discarded cards
	 */
	public final CardList getDiscardedCards() {
		CardList result = new CardList();
		result.addAll(discardedCards);
		return result;
	}

	/**
	 * Checks whether schneider was announced or not
	 * 
	 * @return TRUE if schneider was announced
	 */
	public final boolean isSchneider() {

		return schneider.booleanValue();
	}

	/**
	 * Checks whether schwarz was announced or not
	 * 
	 * @return TRUE if schwarz was announced
	 */
	public final boolean isSchwarz() {

		return schwarz.booleanValue();
	}

	/**
	 * Checks whether an ouvert game was announced or not
	 * 
	 * @return TRUE if an ouvert game was announced
	 */
	public final boolean isOuvert() {

		return ouvert.booleanValue();
	}

	/**
	 * Checks whether a hand game was announced or not
	 * 
	 * @return TRUE if a hand game was announced
	 */
	public final boolean isHand() {

		return hand.booleanValue();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();

		result.append("Game announcement: ").append(gameType); //$NON-NLS-1$

		if (hand.booleanValue()) {

			result.append(" hand"); //$NON-NLS-1$
		}

		if (ouvert.booleanValue()) {

			result.append(" ouvert"); //$NON-NLS-1$
		}

		if (schneider.booleanValue()) {

			result.append(" schneider"); //$NON-NLS-1$
		}

		if (schwarz.booleanValue()) {

			result.append(" schwarz"); //$NON-NLS-1$
		}

		return result.toString();
	}
}
