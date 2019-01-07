/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.util;

import java.util.Arrays;
import java.util.List;

/**
 * Contains all ranks.
 */
public enum Rank {
	/**
	 * Seven
	 */
	SEVEN("7", "Seven", 0, 0, 0, 0),
	/**
	 * Eight
	 */
	EIGHT("8", "Eight", 1, 1, 1, 0),
	/**
	 * Nine
	 */
	NINE("9", "Nine", 2, 2, 2, 0),
	/**
	 * Queen or Ober
	 */
	QUEEN("Q", "Queen", 3, 5, 3, 3),
	/**
	 * King or König
	 */
	KING("K", "King", 4, 6, 4, 4),
	/**
	 * Ten
	 */
	TEN("T", "Ten", 5, 3, 5, 10),
	/**
	 * Ace or Daus
	 */
	ACE("A", "Ace", 6, 7, 6, 11),
	/**
	 * Jack or Unter
	 */
	JACK("J", "Jack", 7, 4, 7, 2);

	private String shortString;
	private String longString;
	private int suitGrandOrder;
	private int nullOrder;
	private int ramschOrder;
	private int points;

	/**
	 * Constructor
	 */
	private Rank(final String shortString, final String longString, final int suitGrandOrder, final int nullOrder,
			final int ramschOrder, final int points) {
		this.shortString = shortString;
		this.longString = longString;
		this.suitGrandOrder = suitGrandOrder;
		this.nullOrder = nullOrder;
		this.ramschOrder = ramschOrder;
		this.points = points;
	}

	/**
	 * Gets a short string representation of the rank.
	 *
	 * @return Short string representation of the rank
	 */
	public String getShortString() {
		return shortString;
	}

	/**
	 * Gets a long string representation of the rank.
	 *
	 * @return Long string representation of the rank
	 */
	public String getLongString() {
		return longString;
	}

	/**
	 * Gets the order of the rank in suit and grand games.
	 *
	 * @return Order in suit and grand games
	 */
	public int getSuitGrandOrder() {
		return suitGrandOrder;
	}

	/**
	 * Gets the order of the rank in null games.
	 *
	 * @return Order in null games
	 */
	public int getNullOrder() {
		return nullOrder;
	}

	/**
	 * Gets the order of the rank in ramsch games.
	 *
	 * @return Order in ramsch games
	 */
	public int getRamschOrder() {
		return ramschOrder;
	}

	/**
	 * Gets the points of the rank for game value calculation.
	 *
	 * @return Points of the card
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Gets the rank of a card given as string.
	 *
	 * @param cardAsString
	 *            Card as string
	 * @return Rank of card
	 */
	public static Rank getRankFromString(final String cardAsString) {

		Rank rank = null;

		if (cardAsString.length() == 2) {
			// parse only, iff the string is two characters long
			if (cardAsString.endsWith("A")) { //$NON-NLS-1$

				rank = ACE;
			} else if (cardAsString.endsWith("T")) { //$NON-NLS-1$

				rank = TEN;
			} else if (cardAsString.endsWith("K")) { //$NON-NLS-1$

				rank = KING;
			} else if (cardAsString.endsWith("Q")) { //$NON-NLS-1$

				rank = QUEEN;
			} else if (cardAsString.endsWith("J")) { //$NON-NLS-1$

				rank = JACK;
			} else if (cardAsString.endsWith("9")) { //$NON-NLS-1$

				rank = NINE;
			} else if (cardAsString.endsWith("8")) { //$NON-NLS-1$

				rank = EIGHT;
			} else if (cardAsString.endsWith("7")) { //$NON-NLS-1$

				rank = SEVEN;
			}
		}

		return rank;
	}

	/**
	 * Builds an array of the ranks (e.g. to compute the multipliers)
	 *
	 * @return an array containing the ranks without the jack, starting with ace,
	 *         ending with 7
	 */
	public static List<Rank> getRankList() {

		return Arrays.asList(Rank.ACE, Rank.TEN, Rank.KING, Rank.QUEEN,
				Rank.NINE, Rank.EIGHT, Rank.SEVEN);
	}

	/**
	 * converts the rank of a card to a specific int value (7=1, 8=2, 9=4, ... A=64,
	 * J=128)
	 *
	 * @return an int representation of the card's rank
	 */
	@Deprecated
	public int toBinaryFlag() {
		return (int) Math.pow(2, ordinal());
	}

	public int toNullBinaryFlag() {
		return (int) Math.pow(2, getNullOrder());
	}

	public int toSuitBinaryFlag() {
		return (int) Math.pow(2, getSuitGrandOrder());
	}

	public int toGrandBinaryFlag() {
		return toSuitBinaryFlag();
	}

	public int toRamschBinaryFlag() {
		return (int) Math.pow(2, getRamschOrder());
	}
}
