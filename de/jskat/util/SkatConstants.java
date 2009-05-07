/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.util;


/**
 * Skat constants
 */
public final class SkatConstants {

	/**
	 * All possible bid values ordered from the lowest to the highest bid value
	 */
	public final static int[] bidOrder = new int[] { 18, 20, 22, 23, 24, 27,
			30, 33, 35, 36, 40, 44, 45, 46, 48, 50, 54, 55, 59, 60, 63, 66, 70,
			72, 77, 80, 81, 84, 88, 90, 96, 99, 100, 108, 110, 117, 120, 121,
			126, 130, 132, 135, 140, 143, 144, 150, 153, 154, 156, 160, 162,
			165, 168, 170, 176, 180, 187, 192, 198, 204, 216, 240, 264 };

	/**
	 * Returns the multiplier for the game
	 * 
	 * @param gameType Game type
	 * @param hand TRUE if game is a hand game
	 * @param ouvert TRUE if game is an ouvert game
	 * @return Multiplier
	 */
	public final static int getGameBaseValue(GameType gameType, boolean hand, boolean ouvert) {
		
		int multiplier = 0;
		
		switch(gameType) {
		case PASSED_IN:
			break;
		case CLUBS:
			multiplier = 12;
			break;
		case SPADES:
			multiplier = 11;
			break;
		case HEARTS:
			multiplier = 10;
			break;
		case DIAMONDS:
			multiplier = 9;
			break;
		case NULL:
			if (hand) {
				if (ouvert) {
					multiplier = 59;
				}
				else {
					multiplier = 35;
				}
			}
			else {
				if (ouvert) {
					multiplier = 46;
				}
				else {
					multiplier = 23;
				}
			}
			break;
		case GRAND:
			multiplier = 24;
			break;
		case RAMSCH:
			multiplier = 1;
			break;
		}
		
		return multiplier;
	}
}
