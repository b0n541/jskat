package org.jskat.ai.test;

/**
 * Test player that only plays ramsch games
 */
public class RamschTestPlayer extends NoBiddingTestPlayer {

	private boolean playGrandHand = false;

	/**
	 * Sets whether the player should play grand hand
	 * 
	 * @param isPlayGrandHand
	 *            TRUE if the player should play grand hand
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
