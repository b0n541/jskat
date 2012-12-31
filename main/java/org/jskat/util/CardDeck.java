/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
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
package org.jskat.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.StringTokenizer;

/**
 * Represents a complete card deck for Skat
 */
public class CardDeck extends CardList {

	private final int MAX_CARDS = 32;

	/**
	 * Creates a new instance of CardDeck
	 */
	public CardDeck() {

		super();

		// Adds a card for every suit and value
		for (Card card : Card.values()) {

			add(card);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param cards
	 *            Card distribution
	 */
	public CardDeck(final String cards) {

		StringTokenizer token = new StringTokenizer(cards);

		while (token.hasMoreTokens()) {

			add(Card.getCardFromString(token.nextToken()));
		}
	}

	/**
	 * Constructor
	 * 
	 * @param cards
	 *            Card distribution
	 */
	public CardDeck(final CardList cards) {

		addAll(cards);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(final Card card) {

		if (size() == MAX_CARDS) {
			throw new IllegalStateException("Card deck is already filled with "
					+ MAX_CARDS + " cards.");
		}
		if (contains(card)) {
			throw new IllegalArgumentException("Card " + card
					+ " is already contained in card deck.");
		}

		return super.add(card);
	}

	/**
	 * Gets a complete card deck
	 * 
	 * @return A complete card deck
	 */
	public static EnumSet<Card> getAllCards() {

		EnumSet<Card> allCards = EnumSet.allOf(Card.class);

		return allCards;
	}

	/**
	 * Shuffles the CardDeck
	 */
	public void shuffle() {
		// Simple random shuffling
		Collections.shuffle(cards);
	}
}
