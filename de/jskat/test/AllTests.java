/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.jskat.test.ai.nn.CardDeckSimulatorTest;
import de.jskat.test.util.CardDeckTest;
import de.jskat.test.util.CardTest;
import de.jskat.test.util.rule.BasicSkatRuleTest;
import de.jskat.test.util.rule.NullRuleTest;
import de.jskat.test.util.rule.SchneiderSchwarzRuleTest;

/**
 * All JUnit tests
 */
@RunWith(Suite.class)
@SuiteClasses({
	BasicSkatRuleTest.class,
	NullRuleTest.class,
	SchneiderSchwarzRuleTest.class,
	CardTest.class,
	CardDeckTest.class,
	CardDeckSimulatorTest.class
})

public class AllTests {
	// this is empty intentionally
}