package org.jskat.ai.alex;

import java.util.Random;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;

public class PlayerAlex {
	private final Random random = new Random();

	// TODO play not random
	private Card openRound(final CardList cards) {
		Card randomCard = cards.get(random.nextInt(cards.size()));
		return randomCard;
	}

	// find cards that are at least stronger than trick cards else play random
	private Card continueRound(final CardList myCards, final GameType gameType, final CardList trickCards) {
		CardList possibleCards = new CardList(myCards);
		for (Card myCard: myCards) {
			for (Card trickCard: trickCards) {
				if (trickCard.beats(gameType, myCard)) {
					possibleCards.remove(myCard);
				}
			}
		}
		if (possibleCards.size() > 0) {
			Card randomBeatingCard = possibleCards.get(random.nextInt(possibleCards.size()));
			return randomBeatingCard;
		}

		// TODO play worst card
		Card randomCard = myCards.get(random.nextInt(myCards.size()));
		return randomCard;
	}

	public Card playCard(final CardList possibleCards, final GameType gameType,final CardList trickCards) {
		if (trickCards.size() == 0) {
			return openRound(possibleCards);
		}

		return continueRound(possibleCards, gameType, trickCards);
	}
}
