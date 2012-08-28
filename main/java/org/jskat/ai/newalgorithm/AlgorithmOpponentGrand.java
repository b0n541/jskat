/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0
 * Copyright (C) 2012-08-28
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.newalgorithm;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jskat.ai.newalgorithm.exception.IllegalMethodException;
import org.jskat.util.*;

public class AlgorithmOpponentGrand extends AbstractAlgorithmAI {
	private static final Logger log = Logger.getLogger(AlgorithmOpponentGrand.class);

	AlgorithmOpponentGrand(final AlgorithmAI p, GameType pGameType) {
		super(p, pGameType);
		
		log.debug("Defining player <" + myPlayer.getPlayerName() + "> as " + this.getClass().getName());
	}

	protected Card startGame() {
		System.out.println("OpponentGrand starts Game: "+knowledge.getPlayerPosition());
		
		return playStartGameCard(
				knowledge.getOwnCards(),
				knowledge.getTrickCards(),
				oPlayedCards,
				oNotOpponentCards,
				oSituation,
				knowledge.getDeclarer());
	}

	protected Card playForehandCard() {
		System.out.println("OpponentGrand plays Forehand-Card: "+knowledge.getPlayerPosition());
		
		return playForehandCard(
				knowledge.getOwnCards(),
				knowledge.getTrickCards(),
				oPlayedCards,
				oNotOpponentCards,
				oSituation,
				knowledge.getDeclarer());
	}

	protected Card playMiddlehandCard() {
		System.out.println("OpponentGrand plays Middlehand-Card: "+knowledge.getPlayerPosition());
		
		return playMiddlehandCard(
				knowledge.getOwnCards(),
				knowledge.getTrickCards(),
				oPlayedCards,
				oNotOpponentCards,
				oSituation,
				knowledge.getDeclarer());
	}

	protected Card playRearhandCard() {
		System.out.println("OpponentGrand plays Rearhand-Card: "+knowledge.getPlayerPosition());
		
		return playRearhandCard(
				knowledge.getOwnCards(),
				knowledge.getTrickCards(),
				oPlayedCards,
				oNotOpponentCards,
				oSituation,
				knowledge.getDeclarer());
	}

	@Override
	public CardList discardSkat(BidEvaluator bidEvaluator) {
		throw new IllegalMethodException("AlgorithmOpponentGrand has nothing to discard!");
	}

	// static methods for creating JUnit-tests and test cardplaybehavior
	public static Card playStartGameCard(CardList pCards, CardList pTrickCards, CardList pPlayedCards, CardList pNotOpponentCards, Situation pSituation, Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		boolean tDeclarerInMiddle	= Player.FOREHAND.getLeftNeighbor() == pDeclarer;
		
		CardList tPossibleHighCard	= new CardList();
		CardList tPossibleLowCard	= new CardList();
		
		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}
	
	public static Card playForehandCard(CardList pCards, CardList pTrickCards, CardList pPlayedCards, CardList pNotOpponentCards, Situation pSituation, Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		boolean tDeclarerInMiddle	= Player.FOREHAND.getLeftNeighbor() == pDeclarer;
//		Card tForehandCard			= pTrickCards.get(0);
		
		CardList possibleCards		= new CardList();
		
		return getRandomAllowedCard(pCards, null, pSituation.getGameType());
	}
	
	public static Card playMiddlehandCard(CardList pCards, CardList pTrickCards, CardList pPlayedCards, CardList pNotOpponentCards, Situation pSituation, Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		boolean tDeclarerInForhand			= Player.MIDDLEHAND.getRightNeighbor() == pDeclarer;
		Card tForehandCard					= pTrickCards.get(0);
		Suit tSuit							= tForehandCard.getSuit();
		ArrayList<Suit> tDeclarerBlankSuits	= pSituation.getLeftPlayerBlankSuits();
		if(Player.MIDDLEHAND.getRightNeighbor() == pDeclarer)
			tDeclarerBlankSuits				= pSituation.getRightPlayerBlankSuits();
		
		CardList possibleCards				= new CardList();
		
		return getRandomAllowedCard(pCards, tForehandCard, pSituation.getGameType());
	}
	
	public static Card playRearhandCard(CardList pCards, CardList pTrickCards, CardList pPlayedCards, CardList pNotOpponentCards, Situation pSituation, Player pDeclarer) {
		pCards.sort(pSituation.getGameType());
		Card tForehandCard					= pTrickCards.get(0);
		Card tMiddlehandCard				= pTrickCards.get(1);
		CardList possibleCards				= new CardList();

		Card tCardToBeat	= tForehandCard;
		if(tMiddlehandCard.beats(pSituation.getGameType(), tCardToBeat))
			tCardToBeat		= tMiddlehandCard;
		Suit tSuit			= tCardToBeat.getSuit();

		ArrayList<Suit> tDeclarerBlankSuits	= pSituation.getLeftPlayerBlankSuits();
		if(Player.REARHAND.getRightNeighbor() == pDeclarer)
			tDeclarerBlankSuits				= pSituation.getRightPlayerBlankSuits();
		
		return getRandomAllowedCard(pCards, tForehandCard, pSituation.getGameType());
	}
}