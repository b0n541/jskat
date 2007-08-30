/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share;

/**
 * States for JSkat
 * 
 * @author Jan Schäfer <jan.schaefer@b0n541.net>
 */
public final class JSkatStates {

    // Main states for JSkat
	public static int BEFORE_INITIALISATION = 0;
	public static int INITIALISATION_COMPLETE = 1;
	public static int SKATSERIES_STARTED = 2;
	public static int DEALING = 3;
	public static int BIDDING = 4;
	public static int BIDDING_FINISHED = 5;
	public static int DOING_SCHIEBERRAMSCH = 6;
	public static int SHOWING_SKAT = 7;
	public static int PLAYING = 8;
	public static int CHECKING_FOR_WINNER = 9;
	public static int GAME_OVER = 10;
}