/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util.rule;

import de.jskat.data.SkatGameData;

/**
 * Implementation of skat rules for Grand games
 */
public class GrandRules extends SuitGrandRules {

	/**
	 * @see SuitGrandRules#getBaseMultiplier(SkatGameData)
	 */
	@Override
	protected int getBaseMultiplier(SkatGameData gameData) {
		
		return this.getJackMultiplier(gameData);
	}
}
