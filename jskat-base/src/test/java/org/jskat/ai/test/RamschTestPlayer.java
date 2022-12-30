package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;

/**
 * Test player that only plays Ramsch games
 */
public class RamschTestPlayer extends AIPlayerRND {

    private boolean playGrandHand = false;

    @Override
    public int bidMore(final int nextBidValue) {
        return 0;
    }

    @Override
    public boolean holdBid(final int currBidValue) {
        return false;
    }

    /**
     * Sets whether the player should play grand hand
     *
     * @param isPlayGrandHand TRUE if the player should play grand hand
     */
    public void setPlayGrandHand(boolean isPlayGrandHand) {
        playGrandHand = isPlayGrandHand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playGrandHand() {
        return playGrandHand;
    }
}
