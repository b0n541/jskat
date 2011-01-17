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
			if (playerHands.get(Player.FORE_HAND).size() < 10) {
				player = Player.FORE_HAND;
			} else if (playerHands.get(Player.MIDDLE_HAND).size() < 10) {
				player = Player.MIDDLE_HAND;
			} else if (playerHands.get(Player.HIND_HAND).size() < 10) {
				player = Player.HIND_HAND;
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
