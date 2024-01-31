package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;

/**
 * Test player that only plays Ramsch games
 */
public class RamschTestPlayer extends AIPlayerRND {

    private boolean playGrandHand = false;
    private boolean pickUpSkat = true;

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
     * @param playGrandHand TRUE if the player should play grand hand
     */
    public void setPlayGrandHand(final boolean playGrandHand) {
        this.playGrandHand = playGrandHand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playGrandHand() {
        return playGrandHand;
    }

    /**
     * Sets whether the player should pick up skat
     *
     * @param pickUpSkat TRUE if the player should pick up skat
     */
    public void setPickUpSkat(final boolean pickUpSkat) {
        this.pickUpSkat = pickUpSkat;
    }

    @Override
    public boolean pickUpSkat() {
        return pickUpSkat;
    }
}
