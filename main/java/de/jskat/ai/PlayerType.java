/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai;

/**
 * All implemented player types
 */
public enum PlayerType {

	/**
	 * Random player
	 */
	RANDOM,
	/**
	 * Algorithmic player
	 */
	ALGORITHMIC,
	/**
	 * Neural network player
	 */
	NEURAL_NETWORK,
	/**
	 * Human player
	 */
	HUMAN;
	
	/**
	 * Gets a player type from a string
	 * 
	 * @param playerString Player type as string
	 * @return Player type or NULL if the player type does not exists
	 */
	public static PlayerType getPlayerTypeFromString(String playerString) {
		
		PlayerType result = null;
		
		for (PlayerType currPlayerType : PlayerType.values()) {
			
			if (currPlayerType.toString().equals(playerString)) {
				
				result = currPlayerType;
			}
		}
		
		return result;
	}
}
