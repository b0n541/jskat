package org.jskat.ai.ddss;

import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.data.GameAnnouncement;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An AI player implementation using the Double Dummy Skat Solver approach.
 */
public class DoubleDummySkatSolver extends AIPlayerRND {

	private final static Logger LOG = LoggerFactory.getLogger(DoubleDummySkatSolver.class);

	@Override
	public Card playCard() {

		LOG.debug("Playable cards: " + getPlayableCards(knowledge.getTrickCards()));

		for (Card card : Card.values()) {

			StringBuilder outstandingCardsLog = new StringBuilder();

			if (knowledge.isCardOutstanding(card)) {
				outstandingCardsLog.append("Outstanding card: " + card + " could lie at ");
				if (knowledge.couldHaveCard(Player.FOREHAND, card)) {
					outstandingCardsLog.append("FOREHAND ");
				}
				if (knowledge.couldHaveCard(Player.MIDDLEHAND, card)) {
					outstandingCardsLog.append("MIDDLEHAND ");
				}
				if (knowledge.couldHaveCard(Player.REARHAND, card)) {
					outstandingCardsLog.append("REARHAND");
				}
				LOG.debug(outstandingCardsLog.toString());
			}
		}

		return super.playCard();
	}

	@Override
	public void preparateForNewGame() {
		super.preparateForNewGame();
	}

	@Override
	public void finalizeGame() {
		super.finalizeGame();
	}

	@Override
	public Integer bidMore(int nextBidValue) {
		return super.bidMore(nextBidValue);
	}

	@Override
	public Boolean holdBid(int currBidValue) {
		return super.holdBid(currBidValue);
	}

	@Override
	public Boolean playGrandHand() {
		return super.playGrandHand();
	}

	@Override
	public Boolean callContra() {
		return super.callContra();
	}

	@Override
	public Boolean callRe() {
		return super.callRe();
	}

	@Override
	public Boolean pickUpSkat() {
		return super.pickUpSkat();
	}

	@Override
	public GameAnnouncement announceGame() {
		return super.announceGame();
	}

	@Override
	public void startGame() {
		super.startGame();
	}

	@Override
	public CardList getCardsToDiscard() {
		return super.getCardsToDiscard();
	}
}
