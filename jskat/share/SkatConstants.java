/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package jskat.share;

/**
 * Skat constants
 */

public final class SkatConstants {

	public final static String HUMANPLAYER = "HumanPlayer";

	// Codes of the different cards (suit)
	// TODO is this really needed?
	public final static int SUIT_GRAND = -1;

	public enum Suits {
		CLUBS {
			String shortString() {
				return "C";
			}

			String longString() {
				return "Clubs";
			}
			
			int getSuitOrder() {
				return 3;
			}
		},
		SPADES {
			String shortString() {
				return "S";
			}

			String longString() {
				return "Spades";
			}
			
			int getSuitOrder() {
				return 2;
			}
		},
		HEARTS {
			String shortString() {
				return "H";
			}

			String longString() {
				return "Hearts";
			}
			
			int getSuitOrder() {
				return 1;
			}
		},
		DIAMONDS {
			String shortString() {
				return "D";
			}

			String longString() {
				return "Diamonds";
			}
			
			int getSuitOrder() {
				return 0;
			}
		};

		/**
		 * Gets a short string representation of the constant
		 * 
		 * @return Short string representation of the constant
		 */
		abstract String shortString();

		/**
		 * Gets a long string representation of the constant
		 * 
		 * @return Long string representation of the constant
		 */
		abstract String longString();

		/**
		 * Gets the order of the constant
		 * 
		 * @return Order of the constant
		 */
		abstract int getSuitOrder();
	};

	// Codes of the different cards (value)
	public enum Ranks {
		SEVEN {
			String shortString() {
				return "7";
			}

			String longString() {
				return "Seven";
			}
			
			int getSuitGrandOrder() {
				return 0;
			}

			int getNullOrder() {
				return 0;
			}
			
			int getRamschOrder() {
				return 0;
			}
		},
		EIGHT {
			String shortString() {
				return "8";
			}

			String longString() {
				return "Eight";
			}
			
			int getSuitGrandOrder() {
				return 1;
			}

			int getNullOrder() {
				return 1;
			}
			
			int getRamschOrder() {
				return 1;
			}
		},
		NINE {
			String shortString() {
				return "9";
			}

			String longString() {
				return "Nine";
			}
			
			int getSuitGrandOrder() {
				return 2;
			}

			int getNullOrder() {
				return 2;
			}
			
			int getRamschOrder() {
				return 2;
			}
		},
		QUEEN {
			String shortString() {
				return "Q";
			}

			String longString() {
				return "Queen";
			}
			
			int getSuitGrandOrder() {
				return 3;
			}

			int getNullOrder() {
				return 5;
			}
			
			int getRamschOrder() {
				return 4;
			}
		},
		KING {
			String shortString() {
				return "K";
			}

			String longString() {
				return "King";
			}
			
			int getSuitGrandOrder() {
				return 4;
			}

			int getNullOrder() {
				return 6;
			}
			
			int getRamschOrder() {
				return 5;
			}
		},
		TEN {
			String shortString() {
				return "T";
			}

			String longString() {
				return "Ten";
			}
			
			int getSuitGrandOrder() {
				return 5;
			}

			int getNullOrder() {
				return 3;
			}
			
			int getRamschOrder() {
				return 3;
			}
		},
		ACE {
			String shortString() {
				return "A";
			}

			String longString() {
				return "Ace";
			}
			
			int getSuitGrandOrder() {
				return 6;
			}

			int getNullOrder() {
				return 7;
			}
			
			int getRamschOrder() {
				return 6;
			}
		},
		JACK {
			String shortString() {
				return "J";
			}

			String longString() {
				return "Jack";
			}
			
			int getSuitGrandOrder() {
				return 7;
			}

			int getNullOrder() {
				return 4;
			}
			
			int getRamschOrder() {
				return 7;
			}
		};

		/**
		 * Gets a short string representation of the constant
		 * 
		 * @return Short string representation of the constant
		 */
		abstract String shortString();

		/**
		 * Gets a long string representation of the constant
		 * 
		 * @return Long string representation of the constant
		 */
		abstract String longString();

		/**
		 * Gets the order of the constant in suit and grand games
		 * 
		 * @return Order in suit and grand games
		 */
		abstract int getSuitGrandOrder();

		/**
		 * Gets the order of the constant in null games
		 * 
		 * @return Order in null games
		 */
		abstract int getNullOrder();

		/**
		 * Gets the order of the constant in ramsch games
		 * 
		 * @return Order in ramsch games
		 */
		abstract int getRamschOrder();
	};

	// Codes for fore-, middle- and backhand
	public final static int FORE_HAND = 0;
	public final static int MIDDLE_HAND = 1;
	public final static int BACK_HAND = 2;

	// Codes of the different Skat game types
	public enum GameTypes {
		PASSED_IN, SUIT, GRAND, NULL, RAMSCH,
		// TODO is RAMSCHGRAND needed?
		RAMSCHGRAND
	};

	// Values for bidding order
	public final static int[] bidOrder = new int[] { 18, 20, 22, 23, 24, 27,
			30, 33, 35, 36, 40, 44, 45, 46, 48, 50, 54, 55, 59, 60, 63, 66, 70,
			72, 77, 80, 81, 84, 88, 90, 96, 99, 100, 108, 110, 117, 120, 121,
			126, 130, 132, 135, 140, 143, 144, 150, 153, 154, 156, 160, 162,
			165, 168, 170, 176, 180, 187, 192, 198, 204, 216, 240, 264 };

	// Values for the different cards for win and loss calculation
	public final static int SEVEN_VAL = 0;
	public final static int EIGHT_VAL = 0;
	public final static int NINE_VAL = 0;
	public final static int JACK_VAL = 2;
	public final static int QUEEN_VAL = 3;
	public final static int KING_VAL = 4;
	public final static int TEN_VAL = 10;
	public final static int ACE_VAL = 11;

	// Values for the different Skat games for win and loss calculation
	public final static int CLUBS_VAL = 12;
	public final static int SPADES_VAL = 11;
	public final static int HEARTS_VAL = 10;
	public final static int DIAMONDS_VAL = 9;
	public final static int GRAND_VAL = 24;
	public final static int NULL_VAL = 23;
	public final static int NULLHAND_VAL = 35;
	public final static int NULLOUVERT_VAL = 46;
	public final static int NULLHANDOUVERT_VAL = 59;
	public final static int RAMSCH_VAL = 0;

	// Codes for game value increases
	public final static int NORMAL = 0;
	public final static int HAND = 1;
	public final static int OUVERT = 2;
	public final static int SCHNEIDER = 3;
	public final static int SCHNEIDER_ANNOUNCED = 4;
	public final static int SCHWARZ = 5;
	public final static int SCHWARZ_ANNOUNCED = 6;
	public final static int CONTRA = 7;
	public final static int RE = 8;
	public final static int BOCK = 9;
}
