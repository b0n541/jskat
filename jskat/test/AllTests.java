/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import jskat.test.share.CardTest;
import jskat.test.share.SkatRulesTest;
import jskat.test.player.AIPlayerMJL.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		CardTest.class,
		SkatRulesTest.class,
		HelperTest.class,
		BiddingTest.class,
		OpponentPlayerTest.class,
		SinglePlayerTest.class,
		SkatProcessorTest.class,
		AdvancedOpponentPlayerTest.class
		}
	)

public class AllTests {
}
