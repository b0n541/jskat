package org.jskat.ai.test;

import org.jskat.ai.AbstractAIPlayer;
import org.jskat.data.GameAnnouncement;
import org.jskat.util.Card;
import org.jskat.util.CardList;

import java.util.ArrayList;
import java.util.List;

public class UnitTestPlayer extends AbstractAIPlayer {

    private int trickNo = 0;
    private final List<Card> cardsToPlay = new ArrayList<>();

    @Override
    public void prepareForNewGame() {
    }

    @Override
    public void finalizeGame() {
    }

    @Override
    public int bidMore(int nextBidValue) {
        return 0;
    }

    @Override
    public boolean holdBid(int currBidValue) {
        return false;
    }

    @Override
    public boolean pickUpSkat() {
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
    public boolean callContra() {
        return false;
    }

    @Override
    public boolean callRe() {
        return false;
    }

    @Override
    public boolean playGrandHand() {
        return false;
    }
}
