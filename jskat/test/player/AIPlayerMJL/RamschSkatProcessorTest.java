/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import jskat.player.AIPlayerMJL.RamschSkatProcessor;
import jskat.share.CardVector;
import jskat.share.Tools;
import jskat.test.share.TestHelper;

public class RamschSkatProcessorTest {

	@BeforeClass
	public static void setUp() {

		Tools.checkLog();
		
		cards001 = TestHelper.buildDeck("C-J,D-J,C-T,C-8,S-Q,S-8,S-7,H-T,D-9,D-8");
		skat001 = TestHelper.buildDeck("C-K,C-7");
	}

	@Test
	public void testProcessSkat() {
		RamschSkatProcessor rsp = new RamschSkatProcessor();
		log.debug("-------------------------------------------------------------");
		log.debug(this);
		log.debug("-------------------------------------------------------------");

		log.debug("Skat001 [before]:"+skat001);
		rsp.processSkat(cards001, skat001);
		assertTrue(skat001.size() == 2);
		log.debug("Skat001 [after]: "+skat001);
		log.debug("-------------------------------------------------------------");
	}

	private static final Logger log = Logger.getLogger(RamschSkatProcessorTest.class);

	private static CardVector cards001;
	private static CardVector skat001;
}
