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
package org.jskat.ai.newalgorithm;

import java.util.ArrayList;

import org.jskat.ai.newalgorithm.exception.IllegalMethodException;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Player;
import org.jskat.util.Suit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmOpponentNull extends AbstractAlgorithmAI {

	private static final Logger log = LoggerFactory.getLogger(AlgorithmOpponentNull.class);

	AlgorithmOpponentNull(final AlgorithmAI p, final GameType pGameType) {
		super(p, pGameType);

		log.debug("Defining player <" + myPlayer.getPlayerName() + "> as "
				+ this.getClass().getName());
	}

	@Override
	protected Card startGame() {
		log.debug("OpponentNull starts Game: " + knowledge.getPlayerPosition());

		return playStartGameCard(knowledge.getOwnCards(),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation, knowledge.getDeclarer());
	}

	@Override
	protected Card playForehandCard() {
		log.debug("OpponentNull plays Forehand-Card: "
				+ knowledge.getPlayerPosition());

		return playForehandCard(knowledge.getOwnCards(),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation, knowledge.getDeclarer());
	}

	@Override
	protected Card playMiddlehandCard() {
		log.debug("OpponentNull plays Middlehand-Card: "
				+ knowledge.getPlayerPosition());

		return playMiddlehandCard(
				myPlayer.getPlayableCards(knowledge.getTrickCards()),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation, knowledge.getDeclarer());
	}

	@Override
	protected Card playRearhandCard() {
		log.debug("OpponentNull plays Rearhand-Card: "
				+ knowledge.getPlayerPosition());

		return playRearhandCard(
				myPlayer.getPlayableCards(knowledge.getTrickCards()),
				knowledge.getTrickCards(), oPlayedCards, oNotOpponentCards,
				oSituation, knowledge.getDeclarer());
	}

	@Override
	public CardList discardSkat(final BidEvaluator bidEvaluator) {
		throw new IllegalMethodException(
				"AlgorithmOpponentNull has nothing to discard!");
	}

	// static methods for creating JUnit-tests and test cardplaybehavior
	public static Card playStartGameCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation, final Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		final boolean tDeclarerInMiddle = Player.FOREHAND.getLeftNeighbor() == pDeclarer;

		final CardList tPossibleHighCard = new CardList();
		final CardList tPossibleLowCard = new CardList();

		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}

	public static Card playForehandCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation, final Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		final boolean tDeclarerInMiddle = Player.FOREHAND.getLeftNeighbor() == pDeclarer;

		final CardList possibleCards = new CardList();

		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}

	public static Card playMiddlehandCard(final CardList pCards,
			final CardList pTrickCards, final CardList pPlayedCards,
			final CardList pNotOpponentCards, final Situation pSituation, final Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		final boolean tDeclarerInForhand = Player.MIDDLEHAND.getRightNeighbor() == pDeclarer;
		final Card tForehandCard = pTrickCards.get(0);
		final Suit tSuit = tForehandCard.getSuit();
		ArrayList<Suit> tDeclarerBlankSuits = pSituation
				.getLeftPlayerBlankSuits();
		if (Player.MIDDLEHAND.getRightNeighbor() == pDeclarer) {
			tDeclarerBlankSuits = pSituation.getRightPlayerBlankSuits();
		}

		final CardList possibleCards = new CardList();

		return getRandomAllowedCard(pCards, tForehandCard,
				pSituation.getGameType());
	}

	public static Card playRearhandCard(final CardList pCards, final CardList pTrickCards,
			final CardList pPlayedCards, final CardList pNotOpponentCards,
			final Situation pSituation, final Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		final Card tForehandCard = pTrickCards.get(0);
		final Card tMiddlehandCard = pTrickCards.get(1);
		final CardList possibleCards = new CardList();

		Card tCardToBeat = tForehandCard;
		if (tMiddlehandCard.beats(pSituation.getGameType(), tCardToBeat)) {
			tCardToBeat = tMiddlehandCard;
		}
		final Suit tSuit = tCardToBeat.getSuit();

		ArrayList<Suit> tDeclarerBlankSuits = pSituation
				.getLeftPlayerBlankSuits();
		if (Player.REARHAND.getRightNeighbor() == pDeclarer) {
			tDeclarerBlankSuits = pSituation.getRightPlayerBlankSuits();
		}

		return getRandomAllowedCard(pCards, tForehandCard,
				pSituation.getGameType());
	}
}