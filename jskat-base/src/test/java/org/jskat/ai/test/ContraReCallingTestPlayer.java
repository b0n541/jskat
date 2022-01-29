package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;

/**
 * Test player that calls Contra and Re everytime.
 */
public class ContraReCallingTestPlayer extends AIPlayerRND {
    @Override
    public Boolean callContra() {
        return true;
    }

    @Override
    public Boolean callRe() {
        return true;
    }

    @Override
    public Integer bidMore(int currentBid) {
        if (currentBid == 18) {
            return 20;
        }
        return -1;
    }

    @Override
    public Boolean holdBid(int currentBid) {
        if (currentBid == 18) {
            return true;
        }
        return false;
    }
}
