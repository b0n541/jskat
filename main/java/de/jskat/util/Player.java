/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util;

/**
 * Contains all player positions in a trick
 */
public enum Player {
	/**
	 * First player
	 */
	FORE_HAND {
		@Override
		public int getOrder() { return 0; }
		@Override
		public Player getRightNeighbor() { return HIND_HAND; }
		@Override
		public Player getLeftNeighbor() { return MIDDLE_HAND; }
	},
	/**
	 * Second player
	 */
	MIDDLE_HAND { 
		@Override
		public int getOrder() { return 1; } 
		@Override
		public Player getRightNeighbor() { return FORE_HAND; }
		@Override
		public Player getLeftNeighbor() { return HIND_HAND; }
	},
	/**
	 * Third player
	 */
	HIND_HAND { 
		@Override
		public int getOrder() { return 2; } 
		@Override
		public Player getRightNeighbor() { return MIDDLE_HAND; }
		@Override
		public Player getLeftNeighbor() { return FORE_HAND; }
	};
	
	/**
	 * Gets order of a player
	 * 
	 * @return Order of the player
	 */
	public abstract int getOrder();
	
	/**
	 * Gets the player right from the player
	 * 
	 * @return Player right from the player
	 */
	public abstract Player getRightNeighbor();
	
	/**
	 * Gets the player left from the player
	 * 
	 * @return Player left from the player
	 */
	public abstract Player getLeftNeighbor();
}
