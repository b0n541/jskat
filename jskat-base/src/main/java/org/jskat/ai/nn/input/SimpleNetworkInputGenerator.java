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
package org.jskat.ai.nn.input;

import java.util.ArrayList;
import java.util.List;

import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;
import org.jskat.util.GameType;
import org.jskat.util.Player;

/**
 * Creates input signals for neural networks<br />
 * The signals are divided into three parts<br />
 * Opponent 1|Neural network player|Opponent 2<br />
 * Every player part ist divided into another parts<br />
 * Played cards|Unplayed cards|Other information flags
 */
public class SimpleNetworkInputGenerator implements NetworkInputGenerator {

	final static int INPUT_LENGTH = 195;
	final static int PLAYER_INPUT_LENGTH = 65;
	final static int CARD_DECK_INPUT_LENGTH = 32;

	final static double ACTIVE = 1.0d;
	final static double INACTIVE = 0.0d;

	final static double HAS_CARD = 1.0d;
	final static double COULD_HAVE_CARD = 1.0d;

	@Override
	public double[] getNetInputs(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {
		double[] netInputs = new double[INPUT_LENGTH];

		for (int i = 0; i < INPUT_LENGTH; i++) {
			netInputs[i] = INACTIVE;
		}

		// set played cards (32 neurons per player)
		setPlayedCardsInput(netInputs, knowledge);

		// set unplayed cards (32 neurons per player)
		setUnplayedCardsInput(netInputs, knowledge);

		// set card to be played (see played cards)
		setCardToBePlayedInput(netInputs, cardToPlay);

		// set game declarer (1 neuron per player)
		setDeclarerInput(netInputs, knowledge);

		return netInputs;
	}

	private void setDeclarerInput(double[] netInputs, ImmutablePlayerKnowledge knowledge) {
		if (!GameType.RAMSCH.equals(knowledge.getGameType())) {
			// in Ramsch games there is no declarer
			Player position = knowledge.getPlayerPosition();
			Player declarer = knowledge.getDeclarer();

			int index = -1;
			if (position.getLeftNeighbor() == declarer) {
				index = 2 * CARD_DECK_INPUT_LENGTH;
			} else if (position == declarer) {
				index = PLAYER_INPUT_LENGTH + 2 * CARD_DECK_INPUT_LENGTH;
			} else if (position.getRightNeighbor() == declarer) {
				index = 2 * PLAYER_INPUT_LENGTH + 2 * CARD_DECK_INPUT_LENGTH;
			}

			netInputs[index] = ACTIVE;
		}
	}

	private static void setPlayedCardsInput(double[] netInputs,
			ImmutablePlayerKnowledge knowledge) {

		List<Trick> trickList = new ArrayList<Trick>();
		trickList.addAll(knowledge.getCompletedTricks());
		trickList.add(knowledge.getCurrentTrick());
		for (Trick trick : trickList) {
			Player position = knowledge.getPlayerPosition();

			Player trickPlayer = trick.getForeHand();
			for (Card card : trick.getCardList()) {
				setPlayedCardsInput(netInputs, knowledge.getGameType(),
						position, trickPlayer, card, ACTIVE);
				trickPlayer = trickPlayer.getLeftNeighbor();
			}
		}
	}

	private static void setPlayedCardsInput(double[] netInputs,
			GameType gameType, Player position, Player trickPlayer, Card card,
			double activationValue) {

		int cardIndex = getNetInputIndex(card);

		// using offset of 1 because of declarer flag
		int index = -1;
		if (position.getLeftNeighbor() == trickPlayer) {
			index = cardIndex;
		} else if (position == trickPlayer) {
			index = PLAYER_INPUT_LENGTH + cardIndex;
		} else if (position.getRightNeighbor() == trickPlayer) {
			index = 2 * PLAYER_INPUT_LENGTH + cardIndex;
		}

		netInputs[index] = activationValue;
	}

	private void setUnplayedCardsInput(double[] netInputs,
			ImmutablePlayerKnowledge knowledge) {
		for (Card card : new CardDeck()) {
			setUnplayedCardsInput(netInputs, knowledge, card);
		}
	}

	private void setUnplayedCardsInput(double[] netInputs,
			ImmutablePlayerKnowledge knowledge, Card card) {

		Player leftOpponent = knowledge.getPlayerPosition().getLeftNeighbor();
		Player rightOpponent = knowledge.getPlayerPosition().getRightNeighbor();
		int index = getNetInputIndex(card);

		// inputs for left opponent
		if (knowledge.couldHaveCard(leftOpponent, card)) {
			netInputs[CARD_DECK_INPUT_LENGTH + index] = COULD_HAVE_CARD;
		} else if (knowledge.hasCard(leftOpponent, card)) {
			netInputs[CARD_DECK_INPUT_LENGTH + index] = HAS_CARD;
		}

		// inputs for player
		if (knowledge.hasCard(knowledge.getPlayerPosition(), card)) {
			netInputs[PLAYER_INPUT_LENGTH + CARD_DECK_INPUT_LENGTH + index] = HAS_CARD;
		}

		// inputs for right opponent
		if (knowledge.couldHaveCard(rightOpponent, card)) {
			netInputs[2 * PLAYER_INPUT_LENGTH + CARD_DECK_INPUT_LENGTH + index] = COULD_HAVE_CARD;
		} else if (knowledge.hasCard(rightOpponent, card)) {
			netInputs[2 * PLAYER_INPUT_LENGTH + CARD_DECK_INPUT_LENGTH + index] = HAS_CARD;
		}
	}

	private void setCardToBePlayedInput(double[] netInputs, Card cardToPlay) {

		// Card to be played is set into played cards inputs
		int index = PLAYER_INPUT_LENGTH + getNetInputIndex(cardToPlay);
		netInputs[index] = ACTIVE;
	}

	private static int getNetInputIndex(final Card card) {

		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}
}
