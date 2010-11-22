/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.data;

import de.jskat.util.CardList;

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
