/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share.rules;

import jskat.share.SkatConstants;

/**
 * Builds a SkatRules object according to the given game type
 * 
 * @author Jan Sch&auml;fer <j@nschaefer.net>
 */
public class SkatRulesFactory {

	/**
	 * Returns the right SkatRules object according to the game type
	 * 
	 * @param gameType Game type
	 * @return SkatRules object
	 */
	public static SkatRules getSkatRules(SkatConstants.GameTypes gameType) {
		
		SkatRules rules = null;
		
		if (gameType == SkatConstants.GameTypes.SUIT) {
			
			rules = new SkatRulesSuit();
		}
		else if (gameType == SkatConstants.GameTypes.GRAND) {
			
			rules = new SkatRulesGrand();
		}
		else if (gameType == SkatConstants.GameTypes.NULL) {
			
			rules = new SkatRulesNull();
		}
		else if (gameType == SkatConstants.GameTypes.RAMSCH) {
			
			rules = new SkatRulesRamsch();
		}
		
		return rules;
	}
}
