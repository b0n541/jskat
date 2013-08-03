/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-10
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
package org.jskat.ai.test;

import java.util.ArrayList;
import java.util.List;

import org.jskat.data.GameAnnouncement;
import org.jskat.player.AbstractJSkatPlayer;
import org.jskat.util.Card;
import org.jskat.util.CardList;

public class UnitTestPlayer extends AbstractJSkatPlayer {

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
	public int bidMore(int nextBidValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean holdBid(int currBidValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pickUpSkat() {
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
		return cardsToPlay.get(knowledge.getCurrentTrick().getTrickNumberInGame());
	}

	@Override
	public boolean isAIPlayer() {
		// TODO Auto-generated method stub
		return false;
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

}
