
package org.jskat.ai.test;

import java.util.ArrayList;
import java.util.List;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.util.Card;
import org.jskat.util.CardList;

public class UnitTestPlayer extends AbstractAIPlayer {

	private int trickNo = 0;
	private List<Card> cardsToPlay = new ArrayList<Card>();

	@Override
	public void preparateForNewGame() {
	}

	@Override
	public void finalizeGame() {
	}

	@Override
	public Integer bidMore(int nextBidValue) {
		return 0;
	}

	@Override
	public Boolean holdBid(int currBidValue) {
		return false;
	}

	@Override
	public Boolean pickUpSkat() {
		return false;
	}

	@Override
	public GameAnnouncement announceGame() {
		return null;
	}

	public void setCardsToPlay(List<Card> cardsToPlay) {
		this.cardsToPlay.addAll(cardsToPlay);
	}

	@Override
	public Card playCard() {
		return cardsToPlay.get(trickNo++);
	}

	@Override
	public void startGame() {
	}

	@Override
	protected CardList getCardsToDiscard() {
		return null;
	}

	@Override
	public Boolean callContra() {
		return false;
	}

	@Override
	public Boolean callRe() {
		return false;
	}

	@Override
	public Boolean playGrandHand() {
		return false;
	}
}
