/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
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
