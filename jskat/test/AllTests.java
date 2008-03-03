/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test;

import jskat.test.data.SkatGameDataTest;
//import jskat.test.player.AIPlayerMJL.AdvancedOpponentPlayerTest;
//import jskat.test.player.AIPlayerMJL.BiddingTest;
//import jskat.test.player.AIPlayerMJL.HelperTest;
//import jskat.test.player.AIPlayerMJL.SinglePlayerTest;
//import jskat.test.player.AIPlayerMJL.SkatProcessorTest;
import jskat.test.share.CardTest;
import jskat.test.share.CardVectorTest;
import jskat.test.share.SkatRulesTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		// JSkat classes
		SkatGameDataTest.class,
		CardTest.class,
		CardVectorTest.class,
		SkatRulesTest.class//,
		// PlayerMJL classes
//		HelperTest.class,
//		BiddingTest.class,
//		SinglePlayerTest.class,
//		SkatProcessorTest.class,
//		AdvancedOpponentPlayerTest.class
		}
	)

public class AllTests {
}
