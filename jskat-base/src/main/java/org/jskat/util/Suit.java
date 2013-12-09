/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	 * @param cardAsString
	 *            Card as string
	 * @return Suit of card
	 */
	public static Suit getSuitFromString(String cardAsString) {

		Suit suit = null;

		if (cardAsString.length() == 2) {
			// parse only, if the string is two characters long
			if (cardAsString.startsWith("C")) { //$NON-NLS-1$

				suit = CLUBS;
			} else if (cardAsString.startsWith("S")) { //$NON-NLS-1$

				suit = SPADES;
			} else if (cardAsString.startsWith("H")) { //$NON-NLS-1$

				suit = HEARTS;
			} else if (cardAsString.startsWith("D")) { //$NON-NLS-1$

				suit = DIAMONDS;
			}
		}

		return suit;
	}
}
