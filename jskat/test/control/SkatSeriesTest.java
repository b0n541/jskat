/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/
package jskat.test.control;

import jskat.control.SkatSeries;
import jskat.data.JSkatDataModel;
import jskat.data.JSkatOptions;
import jskat.player.JSkatPlayer;
import jskat.player.AIPlayerRND.AIPlayerRND;
import jskat.share.Tools;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for class SkatSeries<br />
 * @see jskat.control.SkatSeries
 */

public class SkatSeriesTest {

	/**
	 * Setup for all tests
	 */
	@BeforeClass
	public static void setUp() {
		
		Tools.checkLog();
	}
	
	/**
	 * Tests the play of a skat series
	 */
	@Test
	public void startPlaying() {
		
		JSkatOptions jskatOptions = JSkatOptions.instance();
		JSkatDataModel dataModel = new JSkatDataModel(null, jskatOptions);
		JSkatPlayer[] player = {new AIPlayerRND(), new AIPlayerRND(), new AIPlayerRND()};
		SkatSeries series = new SkatSeries(dataModel, player, 0);
		
		series.startPlaying();
	}
}
