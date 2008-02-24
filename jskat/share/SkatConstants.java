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

	/**
	 * String for a human player without a name
	 */
	public final static String HUMANPLAYER = "HumanPlayer";

	/**
	 * Contains all suits
	 */
	public enum Suits {
		/**
		 * Club or Eichel
		 */
		CLUBS {
			public String shortString() {
				return "C";
			}

			public String longString() {
				return "Clubs";
			}
			
			public int getSuitOrder() {
				return 3;
			}
		},
		/**
		 * Spades or Grün
		 */
		SPADES {
			public String shortString() {
				return "S";
			}

			public String longString() {
				return "Spades";
			}
			
			public int getSuitOrder() {
				return 2;
			}
		},
		/**
		 * Hearts or Herz
		 */
		HEARTS {
			public String shortString() {
				return "H";
			}

			public String longString() {
				return "Hearts";
			}
			
			public int getSuitOrder() {
				return 1;
			}
		},
		/**
		 * Diamonds or Schellen
		 */
		DIAMONDS {
			public String shortString() {
				return "D";
			}

			public String longString() {
				return "Diamonds";
			}
			
			public int getSuitOrder() {
				return 0;
			}
		};

		/**
		 * Gets a short string representation of the constant
		 * 
		 * @return Short string representation of the constant
		 */
		public abstract String shortString();

		/**
		 * Gets a long string representation of the constant
		 * 
		 * @return Long string representation of the constant
		 */
		public abstract String longString();

		/**
		 * Gets the order of the constant
		 * 
		 * @return Order of the constant
		 */
		public abstract int getSuitOrder();
	};

	/**
	 * Contains all ranks
	 */
	public enum Ranks {
		/**
		 * Seven
		 */
		SEVEN {
			public String shortString() {
				return "7";
			}

			public String longString() {
				return "Seven";
			}
			
			public int getSuitGrandOrder() {
				return 0;
			}

			public int getNullOrder() {
				return 0;
			}
			
			public int getRamschOrder() {
				return 0;
			}
			
			public int getPoints() {
				
				return 0;
			}
		},
		/**
		 * Eight
		 */
		EIGHT {
			public String shortString() {
				return "8";
			}

			public String longString() {
				return "Eight";
			}
			
			public int getSuitGrandOrder() {
				return 1;
			}

			public int getNullOrder() {
				return 1;
			}
			
			public int getRamschOrder() {
				return 1;
			}
			
			public int getPoints() {
				
				return 0;
			}
		},
		/**
		 * Nine
		 */
		NINE {
			public String shortString() {
				return "9";
			}

			public String longString() {
				return "Nine";
			}
			
			public int getSuitGrandOrder() {
				return 2;
			}

			public int getNullOrder() {
				return 2;
			}
			
			public int getRamschOrder() {
				return 2;
			}
			
			public int getPoints() {
				
				return 0;
			}
		},
		/**
		 * Queen or Ober
		 */
		QUEEN {
			public String shortString() {
				return "Q";
			}

			public String longString() {
				return "Queen";
			}
			
			public int getSuitGrandOrder() {
				return 3;
			}

			public int getNullOrder() {
				return 5;
			}
			
			public int getRamschOrder() {
				return 4;
			}
			
			public int getPoints() {
				
				return 3;
			}
		},
		/**
		 * King or König
		 */
		KING {
			public String shortString() {
				return "K";
			}

			public String longString() {
				return "King";
			}
			
			public int getSuitGrandOrder() {
				return 4;
			}

			public int getNullOrder() {
				return 6;
			}
			
			public int getRamschOrder() {
				return 5;
			}
			
			public int getPoints() {
				
				return 4;
			}
		},
		/**
		 * Ten
		 */
		TEN {
			public String shortString() {
				return "T";
			}

			public String longString() {
				return "Ten";
			}
			
			public int getSuitGrandOrder() {
				return 5;
			}

			public int getNullOrder() {
				return 3;
			}
			
			public int getRamschOrder() {
				return 3;
			}
			
			public int getPoints() {
				
				return 10;
			}
		},
		/**
		 * Ace or Daus
		 */
		ACE {
			public String shortString() {
				return "A";
			}

			public String longString() {
				return "Ace";
			}
			
			public int getSuitGrandOrder() {
				return 6;
			}

			public int getNullOrder() {
				return 7;
			}
			
			public int getRamschOrder() {
				return 6;
			}
			
			public int getPoints() {
				
				return 11;
			}
		},
		/**
		 * Jack or Unter
		 */
		JACK {
			public String shortString() {
				return "J";
			}

			public String longString() {
				return "Jack";
			}
			
			public int getSuitGrandOrder() {
				return 7;
			}

			public int getNullOrder() {
				return 4;
			}
			
			public int getRamschOrder() {
				return 7;
			}
			
			public int getPoints() {
				
				return 2;
			}
		};

		/**
		 * Gets a short string representation of the constant
		 * 
		 * @return Short string representation of the constant
		 */
		public abstract String shortString();

		/**
		 * Gets a long string representation of the constant
		 * 
		 * @return Long string representation of the constant
		 */
		public abstract String longString();

		/**
		 * Gets the order of the constant in suit and grand games
		 * 
		 * @return Order in suit and grand games
		 */
		public abstract int getSuitGrandOrder();

		/**
		 * Gets the order of the constant in null games
		 * 
		 * @return Order in null games
		 */
		public abstract int getNullOrder();

		/**
		 * Gets the order of the constant in ramsch games
		 * 
		 * @return Order in ramsch games
		 */
		public abstract int getRamschOrder();
		
		/**
		 * Gets the points of the card for game value calculation
		 * 
		 * @return Points of the card
		 */
		public abstract int getPoints();
	};

	/**
	 * Contains all player positions in a trick
	 */
	public enum Player {
		/**
		 * First player
		 */
		FORE_HAND { public int getOrder() { return 0; } },
		/**
		 * Second player
		 */
		MIDDLE_HAND { public int getOrder() { return 1; } },
		/**
		 * Third player
		 */
		BACK_HAND { public int getOrder() { return 2; } };
		
		/**
		 * Gets order of a player
		 * 
		 * @return Order of the player
		 */
		public abstract int getOrder();
	};

	/**
	 * Contains all game types
	 */
	public enum GameTypes {
		/**
		 * Passed in game, no player did a bid
		 */
		PASSED_IN, 
		/**
		 * Suit game with one suit as trump
		 */
		SUIT, 
		/**
		 * Grand game
		 */
		GRAND, 
		/**
		 * Null game
		 */
		NULL, 
		/**
		 * Ramsch game, this is not playable under ISPA rules
		 */
		RAMSCH,
		// TODO is RAMSCHGRAND needed?
		/**
		 * Grand game under a ramsch event, this is not playable under ISPA rules
		 */
		RAMSCHGRAND
	};

	/**
	 * All possible bid values ordered from the lowest to the highest bid value
	 */
	public final static int[] bidOrder = new int[] { 18, 20, 22, 23, 24, 27,
			30, 33, 35, 36, 40, 44, 45, 46, 48, 50, 54, 55, 59, 60, 63, 66, 70,
			72, 77, 80, 81, 84, 88, 90, 96, 99, 100, 108, 110, 117, 120, 121,
			126, 130, 132, 135, 140, 143, 144, 150, 153, 154, 156, 160, 162,
			165, 168, 170, 176, 180, 187, 192, 198, 204, 216, 240, 264 };

	/**
	 * Multiplier for a suit game with clubs as trump
	 */
	public final static int CLUBS_VAL = 12;
	/**
	 * Multiplier for a suit game with spades as trump
	 */
	public final static int SPADES_VAL = 11;
	/**
	 * Multiplier for a suit game with hearts as trump
	 */
	public final static int HEARTS_VAL = 10;
	/**
	 * Multiplier for a suit game with diamonds as trump
	 */
	public final static int DIAMONDS_VAL = 9;
	/**
	 * Multiplier for a grand game
	 */
	public final static int GRAND_VAL = 24;
	/**
	 * Value for a null game
	 */
	public final static int NULL_VAL = 23;
	/**
	 * Value for a null hand game
	 */
	public final static int NULLHAND_VAL = 35;
	/**
	 * Value for a null ouvert game
	 */
	public final static int NULLOUVERT_VAL = 46;
	/**
	 * Value for a null hand ouvert game
	 */
	public final static int NULLHANDOUVERT_VAL = 59;
	/**
	 * Value for a ramsch game
	 */
	public final static int RAMSCH_VAL = 0;
}
