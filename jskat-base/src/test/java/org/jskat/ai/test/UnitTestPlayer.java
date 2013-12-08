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
package org.jskat.ai.test;

import java.util.ArrayList;
import java.util.List;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.util.Card;
import org.jskat.util.CardList;

public class UnitTestPlayer extends AbstractAIPlayer {

	List<Card> cardsToPlay = new ArrayList<Card>();

	@Override
	public void preparateForNewGame() {
		// TODO Auto-generated method stub
	}

	@Override
	public void finalizeGame() {
		// TODO Auto-generated method stub
	}

	@Override
	public Integer bidMore(int nextBidValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean holdBid(int currBidValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean pickUpSkat() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GameAnnouncement announceGame() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCardsToPlay(List<Card> cardsToPlay) {
		this.cardsToPlay.addAll(cardsToPlay);
	}

	@Override
	public Card playCard() {
		return cardsToPlay.get(knowledge.getCurrentTrick()
				.getTrickNumberInGame());
	}

	@Override
	public void startGame() {
		// TODO Auto-generated method stub
	}

	@Override
	protected CardList getCardsToDiscard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean callContra() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean callRe() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean playGrandHand() {
		// TODO Auto-generated method stub
		return null;
	}
}
