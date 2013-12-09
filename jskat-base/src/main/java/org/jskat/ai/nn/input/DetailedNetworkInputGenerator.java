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
import org.jskat.util.Rank;
import org.jskat.util.Suit;

class DetailedNetworkInputGenerator implements NetworkInputGenerator {

	final static int INPUT_LENGTH = 1089;
	private final static int PLAYER_LENGTH = 363;

	public static double HAS_CARD = 1.0d;
	public static double COULD_HAVE_CARD = 0.5d;
	public static double DOESNT_HAVE_CARD = 0.0d;
	public static double PLAYED_CARD = -0.5d;
	public static double PLAYED_CARD_IN_TRICK = -1.0d;

	public static double ACTIVE = 1.0d;
	public static double INACTIVE = 0.0d;

	/**
	 * Creates the net input attributes
	 * 
	 * @param cardToPlay
	 *            Card to be played
	 * @return Net input attributes
	 */
	@Override
	public double[] getNetInputs(ImmutablePlayerKnowledge knowledge, Card cardToPlay) {

		double[] netInputs = new double[INPUT_LENGTH];

		// set game declarer
		setDeclarerInputs(knowledge, netInputs, PLAYER_LENGTH);

		// set information for all played cards
		final int TRICK_LENGTH = 33;
		final int CARD_OFFSET = 1;
		setTrickInputs(knowledge, netInputs, PLAYER_LENGTH, TRICK_LENGTH,
				CARD_OFFSET);

		// set information for all unplayed cards
		Player leftOpponent = knowledge.getPlayerPosition().getLeftNeighbor();
		Player rightOpponent = knowledge.getPlayerPosition().getRightNeighbor();
		final int KNOWN_CARDS_OFFSET = 331;
		for (Card card : new CardDeck()) {
			setKnownCards(knowledge, netInputs, leftOpponent, rightOpponent,
					card, PLAYER_LENGTH, KNOWN_CARDS_OFFSET);
		}

		// set information of card to be played
		if (cardToPlay != null) {
			int trickStartIndex = knowledge.getCurrentTrick()
					.getTrickNumberInGame() * TRICK_LENGTH + 1;
			setCardInputs(knowledge.getGameType(), netInputs, PLAYER_LENGTH,
					knowledge.getPlayerPosition(), trickStartIndex,
					CARD_OFFSET, knowledge.getPlayerPosition(), cardToPlay,
					PLAYED_CARD_IN_TRICK);
		}

		return netInputs;
	}

	private static void setTrickInputs(ImmutablePlayerKnowledge knowledge,
			double[] inputs, int playerLength, int trickLength, int cardOffset) {

		List<Trick> trickList = new ArrayList<Trick>();
		trickList.addAll(knowledge.getCompletedTricks());
		trickList.add(knowledge.getCurrentTrick());
		for (Trick trick : trickList) {
			Player position = knowledge.getPlayerPosition();
			Player trickForeHand = trick.getForeHand();

			int trickStartIndex = trick.getTrickNumberInGame() * trickLength
					+ 1;
			int index = -1;
			if (position.getLeftNeighbor() == trickForeHand) {
				index = trickStartIndex;
			} else if (position == trickForeHand) {
				index = trickStartIndex + playerLength;
			} else if (position.getRightNeighbor() == trickForeHand) {
				index = trickStartIndex + 2 * playerLength;
			}
			inputs[index] = ACTIVE;

			double activationValue = 0.0;
			if (trick.getTrickNumberInGame() < trickList.size()) {
				activationValue = PLAYED_CARD;
			} else {
				activationValue = PLAYED_CARD_IN_TRICK;
			}
			Player trickPlayer = trick.getForeHand();
			for (Card card : trick.getCardList()) {
				setCardInputs(knowledge.getGameType(), inputs, playerLength,
						position, trickStartIndex, cardOffset, trickPlayer,
						card, activationValue);
				trickPlayer = trickPlayer.getLeftNeighbor();
			}
		}
	}

	private static void setCardInputs(GameType gameType, double[] inputs,
			int playerLength, Player position, int trickStartIndex,
			int cardOffset, Player trickPlayer, Card card,
			double activationValue) {
		int cardIndex = getNetInputIndex(gameType, card);

		int index = -1;
		if (position.getLeftNeighbor() == trickPlayer) {
			index = cardIndex + trickStartIndex + cardOffset;
		} else if (position == trickPlayer) {
			index = cardIndex + trickStartIndex + cardOffset + playerLength;
		} else if (position.getRightNeighbor() == trickPlayer) {
			index = cardIndex + trickStartIndex + cardOffset + 2 * playerLength;
		}

		inputs[index] = activationValue;
	}

	private static void setDeclarerInputs(ImmutablePlayerKnowledge knowledge,
			double[] inputs, int NEURON_OFFSET) {
		if (!GameType.RAMSCH.equals(knowledge.getGameType())) {
			// in Ramsch games there is no declarer
			Player position = knowledge.getPlayerPosition();
			Player declarer = knowledge.getDeclarer();

			int index = -1;
			if (position.getLeftNeighbor() == declarer) {
				index = 0;
			} else if (position == declarer) {
				index = NEURON_OFFSET;
			} else if (position.getRightNeighbor() == declarer) {
				index = 2 * NEURON_OFFSET;
			}

			inputs[index] = ACTIVE;
		}
	}

	private static void setKnownCards(ImmutablePlayerKnowledge knowledge,
			double[] inputs, Player leftOpponent, Player rightOpponent,
			Card card, int playerLength, int knownCardsOffset) {

		GameType gameType = knowledge.getGameAnnouncement().getGameType();
		int netInputIndexForCard = getNetInputIndex(gameType, card);

		// inputs for left opponent
		if (knowledge.couldHaveCard(leftOpponent, card)) {
			if (knowledge.couldHaveCard(rightOpponent, card)) {
				inputs[netInputIndexForCard + knownCardsOffset] = COULD_HAVE_CARD;
			} else {
				inputs[netInputIndexForCard + knownCardsOffset] = HAS_CARD;
			}
		}

		// inputs for player
		if (knowledge.getOwnCards().contains(card)) {
			inputs[netInputIndexForCard + knownCardsOffset + playerLength] = HAS_CARD;
		}

		// inputs for right opponent
		if (knowledge.couldHaveCard(rightOpponent, card)) {
			if (knowledge.couldHaveCard(leftOpponent, card)) {
				inputs[netInputIndexForCard + knownCardsOffset + 2
						* playerLength] = COULD_HAVE_CARD;
			} else {
				inputs[netInputIndexForCard + knownCardsOffset + 2
						* playerLength] = HAS_CARD;
			}
		}
	}

	/**
	 * Returns the index for a card according a game type
	 * 
	 * @param gameType
	 *            Game type
	 * @return Index of card for the given game type
	 */
	static int getNetInputIndex(final GameType gameType, final Card card) {

		int result = 0;

		// if (gameType == GameType.NULL) {
		//
		// result = getNetInputIndexNullGame(card);
		// } else {
		//
		// result = getNetInputIndexSuitGrandRamschGame(gameType, card);
		// }
		result = getNetInputIndexNullGame(card);

		return result;
	}

	private static int getNetInputIndexSuitGrandRamschGame(
			final GameType gameType, final Card card) {

		int result = -1;

		if (card.getRank() == Rank.JACK) {

			result = getNetInputIndexJack(card.getSuit());
		} else {

			if (gameType == GameType.GRAND) {

				result = getNetInputIndexGrandGame(card);
			} else if (gameType == GameType.RAMSCH) {

				result = getNetInputIndexRamschGame(card);
			} else {

				// result = getNetInputIndexSuitGame(gameType, card);
				result = getNetInputIndexGrandGame(card);
			}
		}

		return result;
	}

	private static int getNetInputIndexJack(final Suit jackSuit) {

		int result = -1;

		switch (jackSuit) {
		case CLUBS:
			result = 0;
			break;
		case SPADES:
			result = 1;
			break;
		case HEARTS:
			result = 2;
			break;
		case DIAMONDS:
			result = 3;
			break;
		}

		return result;
	}

	private static int getNetInputIndexNullGame(final Card card) {
		// TODO better order cards after frequency or points
		// normal null ordering
		return card.getSuit().getSuitOrder() * 8 + card.getNullOrder();
	}

	private static int getNetInputIndexGrandGame(final Card card) {
		// TODO better order cards after frequency or points
		// normal suit ordering after all jacks
		return 4 + card.getSuit().getSuitOrder() * 7 + card.getSuitGrandOrder();
	}

	private static int getNetInputIndexRamschGame(final Card card) {
		// TODO better order cards after frequency or points
		// ramsch ordering after all jacks
		return 4 + card.getSuit().getSuitOrder() * 7 + card.getRamschOrder();
	}

	private static int getNetInputIndexSuitGame(final GameType gameType,
			final Card card) {

		int result = -1;
		Suit trump = gameType.getTrumpSuit();

		if (card.getSuit() == trump) {
			// trump cards after all jacks
			result = 4 + card.getSuitGrandOrder();
		} else {
			// TODO better order cards after frequency or points
			// normal suit ordering after all trump cards
			if (card.getSuit().getSuitOrder() > trump.getSuitOrder()) {

				result = 4 + 7 + (card.getSuit().getSuitOrder() - 1) * 7
						+ card.getSuitGrandOrder();
			} else {

				result = 4 + 7 + card.getSuit().getSuitOrder() * 7
						+ card.getSuitGrandOrder();
			}
		}

		return result;
	}
}
