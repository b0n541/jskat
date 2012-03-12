package org.jskat.util;

/**
 * Contains all player positions in a trick
 */
public enum Player {
	/**
	 * First player
	 */
	FOREHAND {
		@Override
		public int getOrder() { return 0; }
		@Override
		public Player getRightNeighbor() { return REARHAND; }
		@Override
		public Player getLeftNeighbor() { return MIDDLEHAND; }
	},
	/**
	 * Second player
	 */
	MIDDLEHAND { 
		@Override
		public int getOrder() { return 1; } 
		@Override
		public Player getRightNeighbor() { return FOREHAND; }
		@Override
		public Player getLeftNeighbor() { return REARHAND; }
	},
	/**
	 * Third player
	 */
	REARHAND { 
		@Override
		public int getOrder() { return 2; } 
		@Override
		public Player getRightNeighbor() { return MIDDLEHAND; }
		@Override
		public Player getLeftNeighbor() { return FOREHAND; }
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
