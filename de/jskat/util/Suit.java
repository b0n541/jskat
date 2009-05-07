package de.jskat.util;

/**
 * Contains all suits
 */
public enum Suit {
	/**
	 * Club or Eichel
	 */
	CLUBS {
		@Override
		public String shortString() { return "C"; }
		@Override
		public String longString() { return "Clubs"; }
		@Override
		public int getSuitOrder() { return 3; }
	},
	/**
	 * Spades or Grün
	 */
	SPADES {
		@Override
		public String shortString() { return "S"; }
		@Override
		public String longString() { return "Spades"; }
		@Override
		public int getSuitOrder() { return 2; }
	},
	/**
	 * Hearts or Herz
	 */
	HEARTS {
		@Override
		public String shortString() { return "H"; }
		@Override
		public String longString() { return "Hearts"; }
		@Override
		public int getSuitOrder() { return 1; }
	},
	/**
	 * Diamonds or Schellen
	 */
	DIAMONDS {
		@Override
		public String shortString() { return "D"; }
		@Override
		public String longString() { return "Diamonds"; }
		@Override
		public int getSuitOrder() { return 0; }
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
	
	/**
	 * Gets the suit of a card given as string
	 * 
	 * @param cardAsString Card as string
	 * @return Suit of card
	 */
	public static Suit getSuitFromString(String cardAsString) {
		
		Suit suit = null;
		
		if (cardAsString.startsWith("C")) { //$NON-NLS-1$
			
			suit = CLUBS;
		}
		else if (cardAsString.startsWith("S")) { //$NON-NLS-1$
			
			suit = SPADES;
		}
		else if (cardAsString.startsWith("H")) { //$NON-NLS-1$
			
			suit = HEARTS;
		}
		else if (cardAsString.startsWith("D")) { //$NON-NLS-1$
			
			suit = DIAMONDS;
		}
		
		return suit;
	}
}
