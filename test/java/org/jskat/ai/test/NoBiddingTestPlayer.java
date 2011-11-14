package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;

/**
 * Test player that does not bid, it makes random moves during other game phases
 */
public class NoBiddingTestPlayer extends AIPlayerRND {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int bidMore(@SuppressWarnings("unused") int nextBidValue) {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean holdBid(@SuppressWarnings("unused") int currBidValue) {
		return false;
	}
}
