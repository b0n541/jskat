/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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
package org.jskat.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Represents a complete card deck for Skat
 */
public class CardDeck extends CardList {

	private static final long serialVersionUID = 1L;

	private Random rand;

	private final int MAX_CARDS = 32;

	/**
	 * Creates a new instance of CardDeck
	 */
	public CardDeck() {

		super();

		// Creates cards for every suit and value
		for (Card card : Card.values()) {

			add(card);
		}

		this.rand = new Random();
	}

	/**
	 * Constructor
	 * 
	 * @param cards
	 *            Card distribution
	 */
	public CardDeck(String cards) {

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
	public CardDeck(CardList cards) {

		this.addAll(cards);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(Card card) {

		boolean result = false;

		if (size() < this.MAX_CARDS) {

			result = super.add(card);
		}

		return result;
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
		Collections.shuffle(this);
	}
}
