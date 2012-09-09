package org.jskat.player;

import java.util.ArrayList;
import java.util.List;

import org.jskat.data.GameAnnouncement;
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
