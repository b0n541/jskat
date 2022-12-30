package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;

/**
 * Test player that calls Contra everytime, but not Re.
 */
public class ContraCallingTestPlayer extends AIPlayerRND {
    @Override
    public boolean callContra() {
        return true;
    }

    @Override
    public boolean callRe() {
        return false;
    }
}
