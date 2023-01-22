package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;

/**
 * Test player that calls Contra and Re everytime.
 */
public class ContraReCallingTestPlayer extends AIPlayerRND {
    @Override
    public boolean callContra() {
        return true;
    }

    @Override
    public boolean callRe() {
        return true;
    }

    @Override
    public int bidMore(int currentBid) {
        if (currentBid == 18) {
            return 20;
        }
        return 0;
    }

    @Override
    public boolean holdBid(int currentBid) {
        return currentBid == 18;
    }
}
