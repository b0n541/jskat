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
			
			rules = suitRules;
		}
		else if (gameType == SkatConstants.GameTypes.GRAND) {
			
			rules = grandRules;
		}
		else if (gameType == SkatConstants.GameTypes.NULL) {
			
			rules = nullRules;
		}
		else if (gameType == SkatConstants.GameTypes.RAMSCH) {
			
			rules = ramschRules;
		}
		
		return rules;
	}

	private static SkatRules suitRules = new SkatRulesSuit();
	private static SkatRules grandRules = new SkatRulesGrand();
	private static SkatRules nullRules = new SkatRulesNull();
	private static SkatRules ramschRules = new SkatRulesRamsch();
}
