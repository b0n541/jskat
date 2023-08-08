package org.jskat.ai.sascha.solo;

import org.jskat.ai.newalgorithm.AlgorithmAI;
import org.jskat.ai.sascha.AbstractPlayer;
import org.jskat.data.Trick;
import org.jskat.player.ImmutablePlayerKnowledge;
import org.jskat.util.Card;

public class NullPlayer extends AbstractPlayer {

    public NullPlayer(AlgorithmAI p, ImmutablePlayerKnowledge k) {
        super(p, k);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected Card foreHand() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'foreHand'");
    }

    @Override
    protected Card midHand(Card firstCard) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'midHand'");
    }

    @Override
    protected Card rearHand(Card firstCard, Card secondCard) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rearHand'");
    }

    @Override
    protected void beforeCard() {
    }

    @Override
    protected void afterTrick(Trick t) {

    }

}
