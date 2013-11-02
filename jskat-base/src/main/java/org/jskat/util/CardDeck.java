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
package org.jskat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
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
	 * @param foreHandCards
	 *            Cards of fore hand
	 * @param middleHandCards
	 *            Cards of middle hand
	 * @param rearHandCards
	 *            Cards of rear hand
	 * @param skatCards
	 *            Cards of skat
	 */
	public CardDeck(List<Card> foreHandCards, List<Card> middleHandCards,
			List<Card> rearHandCards, List<Card> skatCards) {
		addAll(foreHandCards.subList(0, 3));
		addAll(middleHandCards.subList(0, 3));
		addAll(rearHandCards.subList(0, 3));
		addAll(skatCards);
		addAll(foreHandCards.subList(3, 7));
		addAll(middleHandCards.subList(3, 7));
		addAll(rearHandCards.subList(3, 7));
		addAll(foreHandCards.subList(7, 10));
		addAll(middleHandCards.subList(7, 10));
		addAll(rearHandCards.subList(7, 10));
	}

	/**
	 * Constructor
	 * 
	 * @param foreHandCards
	 *            Cards of fore hand
	 * @param middleHandCards
	 *            Cards of middle hand
	 * @param rearHandCards
	 *            Cards of rear hand
	 * @param skatCards
	 *            Cards of skat
	 */
	public CardDeck(String foreHandCards, String middleHandCards,
			String rearHandCards, String skatCards) {
		this(getCardsFromString(foreHandCards),
				getCardsFromString(middleHandCards),
				getCardsFromString(rearHandCards),
				getCardsFromString(skatCards));
	}

	/**
	 * Constructor
	 * 
	 * @param cards
	 *            Card distribution
	 */
	public CardDeck(final String cards) {

		addAll(getCardsFromString(cards));
	}

	private static List<Card> getCardsFromString(final String cards) {

		List<Card> result = new ArrayList<Card>();
		StringTokenizer token = new StringTokenizer(cards);

		while (token.hasMoreTokens()) {

			result.add(Card.getCardFromString(token.nextToken()));
		}

		return result;
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
