/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0-SNAPSHOT
 * Build date: 2011-05-23 18:57:15
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
	CLUBS("C", "Clubs", 3),
	/**
	 * Spades or Grün
	 */
	SPADES("S", "Spades", 2),
	/**
	 * Hearts or Herz
	 */
	HEARTS("H", "Hearts", 1),
	/**
	 * Diamonds or Schellen
	 */
	DIAMONDS("D", "Diamonds", 0);

	private final String shortString;
	private final String longString;
	private final int sortOrder;
	
	private Suit(String s, String l, int order) {
		shortString = s;
		longString = l;
		sortOrder = order;
	}
	
	/**
	 * Gets a short string representation of the constant
	 * 
	 * @return Short string representation of the constant
	 */
	public String shortString() {
		return shortString;
	}

	/**
	 * Gets a long string representation of the constant
	 * 
	 * @return Long string representation of the constant
	 */
	public String longString() {
		return longString;
	}

	/**
	 * Gets the order of the constant
	 * 
	 * @return Order of the constant
	 */
	public int getSuitOrder() {
		return sortOrder;
	}
	
	/**
	 * Gets the suit of a card given as string
	 * 
	 * @param cardAsString Card as string
	 * @return Suit of card
	 */
	public static Suit getSuitFromString(String cardAsString) {
		
		Suit suit = null;
		
		if (cardAsString.length() == 2) {
			// parse only, if the string is two characters long
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

	public boolean equals(GameType t) {
		return this.name().equalsIgnoreCase(t.name());
	}
}
