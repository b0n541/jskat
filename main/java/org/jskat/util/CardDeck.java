package org.jskat.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.StringTokenizer;

/**
 * Represents a complete card deck for Skat
 */
public class CardDeck extends CardList {

	private static final long serialVersionUID = 1L;

	private final static int MAX_CARDS = 32;

	/**
	 * Creates a new instance of CardDeck
	 */
	public CardDeck() {

		super();

		// Creates cards for every suit and value
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
