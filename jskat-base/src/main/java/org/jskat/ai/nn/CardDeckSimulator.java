/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.ai.nn;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.CardList;
import org.jskat.util.Player;

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
		return simulateUnknownCards(playerPosition, playerHand, new CardList());
	}

	/**
	 * Simulates a card distribution
	 * 
	 * @param playerPosition
	 *            Player position
	 * @param playerHand
	 *            Cards on players hand
	 * @param knownSkat
	 *            Cards in the skat
	 * @return Simulated card distribution
	 */
	public static CardDeck simulateUnknownCards(Player playerPosition,
			CardList playerHand, CardList knownSkat) {

		// prepare result
		Map<Player, CardList> playerHands = new HashMap<Player, CardList>();
		CardList skat = new CardList(knownSkat);

		for (Player player : Player.values()) {
			// set empty card list
			playerHands.put(player, new CardList());
		}
		playerHands.get(playerPosition).addAll(playerHand);

		// get unknown cards
		CardDeck unknownCards = new CardDeck();
		unknownCards.removeAll(playerHand);
		unknownCards.removeAll(knownSkat);

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
			// FIXME (jan 17.01.2011) code duplication with
			// SimpleSkatGame#dealCards()
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
