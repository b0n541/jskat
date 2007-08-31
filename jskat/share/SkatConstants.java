/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.share;

/**
 * Skat constants 
 *
 * @author  Jan Schäfer <jan.schaefer@b0n541.net>
 */

public final class SkatConstants {
    
    public final static String HUMANPLAYER = "HumanPlayer";
    
    // Codes of the different cards (suit)
    public final static int SUIT_GRAND = -1;
    public final static int CLUBS      = 0;
    public final static int SPADES     = 1;
    public final static int HEARTS     = 2;
    public final static int DIAMONDS   = 3;
    
    // Codes of the different cards (value)
    public final static int SEVEN = 0;
    public final static int EIGHT = 1;
    public final static int NINE  = 2;
    public final static int QUEEN = 3;
    public final static int KING  = 4;
    public final static int TEN   = 5;
    public final static int ACE   = 6;
    public final static int JACK  = 7;
    
    // Codes for comparing the cards in a suit or grand game
    public final static int SEVEN_SUIT_GRAND_VAL = 0;
    public final static int EIGHT_SUIT_GRAND_VAL = 1;
    public final static int NINE_SUIT_GRAND_VAL  = 2;
    public final static int QUEEN_SUIT_GRAND_VAL = 3;
    public final static int KING_SUIT_GRAND_VAL  = 4;
    public final static int TEN_SUIT_GRAND_VAL   = 5;
    public final static int ACE_SUIT_GRAND_VAL   = 6;
    public final static int JACK_SUIT_GRAND_VAL  = -1;
    
    // Codes for comparing the cards in a null game
    public final static int SEVEN_NULL_VAL = 0;
    public final static int EIGHT_NULL_VAL = 1;
    public final static int NINE_NULL_VAL  = 2;
    public final static int TEN_NULL_VAL   = 3;
    public final static int JACK_NULL_VAL  = 4;
    public final static int QUEEN_NULL_VAL = 5;
    public final static int KING_NULL_VAL  = 6;
    public final static int ACE_NULL_VAL   = 7;
    
    // Codes for comparing the cards in a ramsch game
    public final static int SEVEN_RAMSCH_VAL = 0;
    public final static int EIGHT_RAMSCH_VAL = 1;
    public final static int NINE_RAMSCH_VAL  = 2;
    public final static int QUEEN_RAMSCH_VAL = 3;
    public final static int KING_RAMSCH_VAL  = 4;
    public final static int TEN_RAMSCH_VAL   = 5;
    public final static int ACE_RAMSCH_VAL   = 6;
    public final static int JACK_RAMSCH_VAL  = -1;
    
    // Codes for fore-, middle- and backhand
    public final static int FORE_HAND   = 0;
    public final static int MIDDLE_HAND = 1;
    public final static int BACK_HAND   = 2;
    
    // Codes of the different Skat game types
	public final static int PASSED_IN   = -1;
    public final static int SUIT        =  0;
    public final static int GRAND       =  1;
    public final static int NULL        =  2;
    public final static int RAMSCH      =  3;
    public final static int RAMSCHGRAND =  4;
    
    // Values for bidding order
    public final static int[] bidOrder = new int[] { 18,  20,  22,  23,  24,
    27,  30,  33,  35,  36,
    40,  44,  45,  46,  48,
    50,  54,  55,  59,  60,
    63,  66,  70,  72,  77,
    80,  81,  84,  88,  90,
    96,  99, 100, 108, 110,
    117, 120, 121, 126, 130,
    132, 135, 140, 143, 144,
    150, 153, 154, 156, 160, 
    162, 165, 168, 170, 176,
    180, 187, 192, 198, 204,
    216, 240, 264};
    
    // Values for the different cards for win and loss calculation
    public final static int SEVEN_VAL =  0;
    public final static int EIGHT_VAL =  0;
    public final static int NINE_VAL  =  0;
    public final static int JACK_VAL  =  2;
    public final static int QUEEN_VAL =  3;
    public final static int KING_VAL  =  4;
    public final static int TEN_VAL   = 10;
    public final static int ACE_VAL   = 11;
    
    // Values for the different Skat games for win and loss calculation
    public final static int CLUBS_VAL          = 12;
    public final static int SPADES_VAL         = 11;
    public final static int HEARTS_VAL         = 10;
    public final static int DIAMONDS_VAL       =  9;
    public final static int GRAND_VAL          = 24;
    public final static int NULL_VAL           = 23;
    public final static int NULLHAND_VAL       = 35;
    public final static int NULLOUVERT_VAL     = 46;
    public final static int NULLHANDOUVERT_VAL = 59;
    public final static int RAMSCH_VAL         =  0;
    
    // Codes for game value increases
    public final static int NORMAL      = 0;
    public final static int HAND        = 1;
    public final static int SCHNEIDER   = 2;
    public final static int SCHWARZ     = 3;
    public final static int OUVERT      = 4;
    
    public static String getGameType(int i) {
    	switch(i) {
	    	case PASSED_IN:   return "Passed in";
	    	case SUIT:        return "Suit game";
	    	case GRAND:       return "Grand";
	    	case NULL:        return "Null";
	    	case RAMSCH:      return "Ramsch";
	    	case RAMSCHGRAND: return "Grand Hand (Ramsch)";
    	}
    	return "unknown";
    }
    
    public static String getSuit(int i) {
    	switch(i) {
	    	case CLUBS:      return "Clubs";
	    	case HEARTS:     return "Hearts";
	    	case DIAMONDS:   return "Diamonds";
	    	case SPADES:     return "Spades";
	    	case SUIT_GRAND: return "Grand";
    	}
    	return "unknown";
    }

}
