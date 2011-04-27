/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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

/*

@ShortLicense@

Authors: @JS@

Released: @ReleaseDate@

 */

package de.jskat.ai.nn;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.jskat.util.Card;
import de.jskat.util.CardDeck;
import de.jskat.util.CardList;
import de.jskat.util.Player;

/**
 * Simulates possible card decks according to the player knowledge
 */
public class CardDeckSimulator {

	private static Random rand = new Random();

	/**
	 * Simulates a card distribution
	 * 
	 * @param playerPosition
	 *            Player position
	 * @param playerHand
	 *            Cards on players hand
	 * @return Simulated card distribution
	 */
	public static CardDeck simulateUnknownCards(Player playerPosition,
			CardList playerHand) {

		// prepare result
		Map<Player, CardList> playerHands = new HashMap<Player, CardList>();
		CardList skat = new CardList();

		for (Player player : Player.values()) {
			// set empty card list
			playerHands.put(player, new CardList());
		}
		playerHands.get(playerPosition).addAll(playerHand);

		// get unknown cards
		CardDeck unknownCards = new CardDeck();
		for (Card card : playerHand) {

			unknownCards.remove(card);
		}
		unknownCards.shuffle();

		// set unknown cards
		for (Card card : unknownCards) {

			Player player = null;
			if (playerHands.get(Player.FOREHAND).size() < 10) {
				player = Player.FOREHAND;
			} else if (playerHands.get(Player.MIDDLEHAND).size() < 10) {
				player = Player.MIDDLEHAND;
			} else if (playerHands.get(Player.REARHAND).size() < 10) {
				player = Player.REARHAND;
			}

			if (player != null) {
				playerHands.get(player).add(card);
			} else {
				// player hands are filled -> put card into skat
				skat.add(card);
			}
		}

		return createCardDeck(playerHands, skat);
	}

	private static CardDeck createCardDeck(Map<Player, CardList> playerHands,
			CardList skat) {

		CardList cards = new CardList();

		// Simulate card dealing
		for (int i = 0; i < 3; i++) {
			// FIXME (jan 17.01.2011) code duplication with SkatGame#dealCards()
			// deal three rounds of cards
			switch (i) {
			case 0:
				// deal three cards
				dealCards(cards, playerHands, 3);
				// and put two cards into the skat
				cards.add(skat.get(0));
				cards.add(skat.get(1));
				break;
			case 1:
				// deal four cards
				dealCards(cards, playerHands, 4);
				break;
			case 2:
				// deal three cards
				dealCards(cards, playerHands, 3);
				break;
			}
		}

		return new CardDeck(cards);
	}

	private static void dealCards(CardList result,
			Map<Player, CardList> playerHands, int cardCount) {

		for (Player player : Player.values()) {
			for (int i = 0; i < cardCount; i++) {
				result.add(playerHands.get(player).remove(0));
			}
		}
	}
}
