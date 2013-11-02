/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
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

import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Game announcement
 * 
 * An object of this class is returned by a player for game announcement
 */
public class GameAnnouncement {

	private static Logger log = LoggerFactory.getLogger(GameAnnouncement.class);
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
		 * Gets an empty {@link GameAnnouncement}
		 * 
		 * @return Empty {@link GameAnnouncement}
		 */
		public final static GameAnnouncement getEmptyAnnouncement() {
			GameAnnouncement result = new GameAnnouncement();
			result.hand = true;
			return result;
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
			}
			return result;
		}

		/**
		 * Sets the {@link GameType}
		 * 
		 * @param gameType
		 *            Game type
		 */
		public final void setGameType(final GameType gameType) {
			tmpAnnouncement.gameType = gameType;
		}

		/**
		 * Sets the discarded cards
		 * 
		 * @param discardedCards
		 *            Discarded cards
		 */
		public final void setDiscardedCards(final CardList discardedCards) {
			tmpAnnouncement.discardedCards.addAll(discardedCards);
		}

		/**
		 * Sets the flag for a hand game
		 * 
		 * @param isHand
		 *            TRUE, if a hand game was announced
		 */
		public final void setHand(final Boolean isHand) {
			tmpAnnouncement.hand = isHand;
		}

		/**
		 * Sets the flag for an ouvert game
		 * 
		 * @param isOuvert
		 *            TRUE, if an ouvert game was announced
		 */
		public final void setOuvert(final Boolean isOuvert) {
			tmpAnnouncement.ouvert = isOuvert;
		}

		/**
		 * Sets the flag for a schneider game
		 * 
		 * @param isSchneider
		 *            TRUE, if schneider was announced
		 */
		public final void setSchneider(final Boolean isSchneider) {
			tmpAnnouncement.schneider = isSchneider;
		}

		/**
		 * Sets the flag for a schwarz game
		 * 
		 * @param isSchwarz
		 *            TRUE, if a schwarz was announced
		 */
		public final void setSchwarz(final Boolean isSchwarz) {
			tmpAnnouncement.schwarz = isSchwarz;
		}

		private boolean validate() {

			boolean result = true;

			if (tmpAnnouncement.gameType == null) {
				result = false;
			} else if (tmpAnnouncement.discardedCards.size() != 0
					&& tmpAnnouncement.discardedCards.size() != 2) {
				result = false;
			} else if (tmpAnnouncement.gameType == GameType.RAMSCH
					|| tmpAnnouncement.gameType == GameType.PASSED_IN) {
				if (tmpAnnouncement.hand || tmpAnnouncement.ouvert
						|| tmpAnnouncement.isSchneider()
						|| tmpAnnouncement.isSchwarz()
						|| tmpAnnouncement.discardedCards.size() != 0) {
					result = false;
				}
			} else if (tmpAnnouncement.gameType == GameType.NULL) {
				if (tmpAnnouncement.hand
						&& tmpAnnouncement.discardedCards.size() != 0) {
					result = false;
				} else if (tmpAnnouncement.isSchneider()
						|| tmpAnnouncement.isSchwarz()) {
					result = false;
				}
			} else if (tmpAnnouncement.gameType == GameType.CLUBS
					|| tmpAnnouncement.gameType == GameType.SPADES
					|| tmpAnnouncement.gameType == GameType.HEARTS
					|| tmpAnnouncement.gameType == GameType.DIAMONDS
					|| tmpAnnouncement.gameType == GameType.GRAND) {

				if (tmpAnnouncement.hand
						&& tmpAnnouncement.discardedCards.size() != 0) {
					result = false;
				} else if (!tmpAnnouncement.hand
						&& tmpAnnouncement.discardedCards.size() != 0
						&& tmpAnnouncement.discardedCards.size() != 2) {
					result = false;
				} else if (tmpAnnouncement.ouvert
						&& (!tmpAnnouncement.hand || !tmpAnnouncement.schneider || !tmpAnnouncement.schwarz)) {
					result = false;
				} else if (tmpAnnouncement.schwarz
						&& !tmpAnnouncement.schneider) {
					result = false;
				} else if ((tmpAnnouncement.schwarz || tmpAnnouncement.schneider)
						&& !tmpAnnouncement.hand) {
					result = false;
				}
			}

			if (!result) {
				log.error("Invalid " + tmpAnnouncement); //$NON-NLS-1$
			}
			return result;
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
		return discardedCards.getImmutableCopy();
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

		final StringBuffer result = new StringBuffer();

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

		if (discardedCards.size() > 0) {

			result.append(" discarded " + discardedCards); //$NON-NLS-1$
		}

		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (discardedCards == null ? 0 : discardedCards.hashCode());
		result = prime * result + (gameType == null ? 0 : gameType.hashCode());
		result = prime * result + (hand == null ? 0 : hand.hashCode());
		result = prime * result + (ouvert == null ? 0 : ouvert.hashCode());
		result = prime * result
				+ (schneider == null ? 0 : schneider.hashCode());
		result = prime * result + (schwarz == null ? 0 : schwarz.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GameAnnouncement other = (GameAnnouncement) obj;
		if (discardedCards == null) {
			if (other.discardedCards != null) {
				return false;
			}
		} else if (!discardedCards.equals(other.discardedCards)) {
			return false;
		}
		if (gameType != other.gameType) {
			return false;
		}
		if (hand == null) {
			if (other.hand != null) {
				return false;
			}
		} else if (!hand.equals(other.hand)) {
			return false;
		}
		if (ouvert == null) {
			if (other.ouvert != null) {
				return false;
			}
		} else if (!ouvert.equals(other.ouvert)) {
			return false;
		}
		if (schneider == null) {
			if (other.schneider != null) {
				return false;
			}
		} else if (!schneider.equals(other.schneider)) {
			return false;
		}
		if (schwarz == null) {
			if (other.schwarz != null) {
				return false;
			}
		} else if (!schwarz.equals(other.schwarz)) {
			return false;
		}
		return true;
	}
}
