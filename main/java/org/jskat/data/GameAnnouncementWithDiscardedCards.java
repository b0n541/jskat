/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20
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

/**
 * Same as {@link GameAnnouncement} but with discarded cards
 */
public class GameAnnouncementWithDiscardedCards extends GameAnnouncement {

	private CardList discardedCards;

	/**
	 * @see GameAnnouncement#initializeVariables()
	 */
	@Override
	public void initializeVariables() {

		super.initializeVariables();

		discardedCards = new CardList();
	}

	/**
	 * Sets the discarded cards
	 * 
	 * @param newDiscardedCards
	 *            Discarded cards
	 */
	public void setDiscardedCards(CardList newDiscardedCards) {

		if (newDiscardedCards.size() != 2) {
			throw new IllegalArgumentException(
					"More or less than two cards in discarded cards."); //$NON-NLS-1$
		}

		discardedCards.clear();
		discardedCards.addAll(newDiscardedCards);
	}

	/**
	 * Gets the discarded cards
	 * 
	 * @return Discarded cards
	 */
	public CardList getDiscardedCards() {

		return discardedCards;
	}
}
