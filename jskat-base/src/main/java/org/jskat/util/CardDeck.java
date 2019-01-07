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
package org.jskat.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Represents a complete card deck for Skat
 */
public class CardDeck extends CardList {

	private final int MAX_CARDS = 32;

	public final static Map<Suit, List<Card>> SUIT_CARDS;
	public final static Map<Rank, List<Card>> RANK_CARDS;

	static {
		Map<Suit, List<Card>> suitCards = new HashMap<>();
		suitCards.put(Suit.CLUBS,
				Collections.unmodifiableList(
						Arrays.asList(Card.CJ, Card.CA, Card.CT, Card.CK,
								Card.CQ, Card.C9, Card.C8, Card.C7)));
		suitCards.put(Suit.SPADES,
				Collections.unmodifiableList(
						Arrays.asList(Card.SJ, Card.SA, Card.ST, Card.SK,
								Card.SQ, Card.S9, Card.S8, Card.S7)));
		suitCards.put(Suit.HEARTS,
				Collections.unmodifiableList(
						Arrays.asList(Card.HJ, Card.HA, Card.HT, Card.HK,
								Card.HQ, Card.H9, Card.H8, Card.H7)));
		suitCards.put(Suit.DIAMONDS,
				Collections.unmodifiableList(
						Arrays.asList(Card.DJ, Card.DA, Card.DT, Card.DK,
								Card.DQ, Card.D9, Card.D8, Card.D7)));
		SUIT_CARDS = Collections.unmodifiableMap(suitCards);

		Map<Rank, List<Card>> rankCards = new HashMap<>();
		rankCards.put(Rank.JACK,
				Collections.unmodifiableList(
						Arrays.asList(Card.CJ, Card.SJ, Card.HJ, Card.DJ)));
		rankCards.put(Rank.ACE,
				Collections.unmodifiableList(
						Arrays.asList(Card.CA, Card.SA, Card.HA, Card.DA)));
		rankCards.put(Rank.TEN,
				Collections.unmodifiableList(
						Arrays.asList(Card.CT, Card.ST, Card.HT, Card.DT)));
		rankCards.put(Rank.KING,
				Collections.unmodifiableList(
						Arrays.asList(Card.CK, Card.SK, Card.HK, Card.DK)));
		rankCards.put(Rank.QUEEN,
				Collections.unmodifiableList(
						Arrays.asList(Card.CQ, Card.SQ, Card.HQ, Card.DQ)));
		rankCards.put(Rank.NINE,
				Collections.unmodifiableList(
						Arrays.asList(Card.C9, Card.S9, Card.H9, Card.D9)));
		rankCards.put(Rank.EIGHT,
				Collections.unmodifiableList(
						Arrays.asList(Card.C8, Card.S8, Card.H8, Card.D8)));
		rankCards.put(Rank.SEVEN,
				Collections.unmodifiableList(
						Arrays.asList(Card.C7, Card.S7, Card.H7, Card.D7)));
		RANK_CARDS = Collections.unmodifiableMap(rankCards);
	}

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
	 * Get the perfect card distribution where forehand wins all types of games.
	 * 
	 * @return Perfect card distribution
	 */
	public static CardDeck getPerfectDistribution() {
		return new CardDeck(
				"CJ SJ HJ CK CQ SK C7 C8 S7 H7 D7 DJ CA CT C9 SQ HA HK HQ S8 H8 H9 HT SA ST S9 D8 D9 DT DA DK DQ");
	}

	/**
	 * Shuffles the CardDeck
	 */
	public void shuffle() {
		// Simple random shuffling
		Collections.shuffle(cards);
	}
}
