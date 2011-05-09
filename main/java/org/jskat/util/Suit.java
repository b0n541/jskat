/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jskat.util;

/**
 * Contains all suits
 */
public enum Suit {
	/**
	 * Club or Eichel
	 */
	CLUBS {
		@Override
		public String shortString() { return "C"; } //$NON-NLS-1$
		@Override
		public String longString() { return "Clubs"; } //$NON-NLS-1$
		@Override
		public int getSuitOrder() { return 3; }
	},
	/**
	 * Spades or Grün
	 */
	SPADES {
		@Override
		public String shortString() { return "S"; } //$NON-NLS-1$
		@Override
		public String longString() { return "Spades"; } //$NON-NLS-1$
		@Override
		public int getSuitOrder() { return 2; }
	},
	/**
	 * Hearts or Herz
	 */
	HEARTS {
		@Override
		public String shortString() { return "H"; } //$NON-NLS-1$
		@Override
		public String longString() { return "Hearts"; } //$NON-NLS-1$
		@Override
		public int getSuitOrder() { return 1; }
	},
	/**
	 * Diamonds or Schellen
	 */
	DIAMONDS {
		@Override
		public String shortString() { return "D"; } //$NON-NLS-1$
		@Override
		public String longString() { return "Diamonds"; } //$NON-NLS-1$
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
		
		if (cardAsString.length() == 2) {
			// parse only, iff the string is two characters long
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
		}
		
		return suit;
	}
}
