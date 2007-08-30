/*

@ShortLicense@

Authos: @MJL@

Released: @ReleaseDate@

*/

package jskat.test.player.AIPlayerMJL;

import junit.framework.TestCase;
import jskat.player.AIPlayerMJL.RamschSkatProcessor;
import jskat.share.Card;
import jskat.share.CardVector;
import jskat.share.SkatConstants;
import jskat.share.Tools;
import jskat.test.share.TestHelper;

/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
public class RamschSkatProcessorTest extends TestCase {
	/**
	 * Constructor for SkatProcessorTest.
	 * @param arg0
	 */
	public RamschSkatProcessorTest(String arg0) {
		super(arg0);
		Tools.checkLog();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(RamschSkatProcessorTest.class);
	}
	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		cards001 = TestHelper.buildDeck("C-J,D-J,C-T,C-8,S-Q,S-8,S-7,H-T,D-9,D-8");
		skat001 = TestHelper.buildDeck("C-K,C-7");
}

	/**
	 * Tests for processing the skat
	 */
	public void testProcessSkat() {
		RamschSkatProcessor rsp = new RamschSkatProcessor();
		System.out.println("-------------------------------------------------------------");
		System.out.println(this);
		System.out.println("-------------------------------------------------------------");

		System.out.println("Skat001 [before]:"+skat001);
		rsp.processSkat(cards001, skat001);
		assertTrue(skat001.size() == 2);
		System.out.println("Skat001 [after]: "+skat001);
		System.out.println("-------------------------------------------------------------");

	}

	CardVector cards001;
	CardVector skat001;
}
