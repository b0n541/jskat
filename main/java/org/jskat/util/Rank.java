/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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

import java.util.Arrays;
import java.util.List;

/**
 * Contains all ranks
 */
public enum Rank {
	/**
	 * Seven
	 */
	SEVEN {
		@Override
		public String shortString() {
			return "7";} //$NON-NLS-1$

		@Override
		public String longString() {
			return "Seven";} //$NON-NLS-1$

		@Override
		public int getSuitGrandOrder() {
			return 0;
		}

		@Override
		public int getNullOrder() {
			return 0;
		}

		@Override
		public int getRamschOrder() {
			return 0;
		}

		@Override
		public int getPoints() {
			return 0;
		}
	},
	/**
	 * Eight
	 */
	EIGHT {
		@Override
		public String shortString() {
			return "8";} //$NON-NLS-1$

		@Override
		public String longString() {
			return "Eight";} //$NON-NLS-1$

		@Override
		public int getSuitGrandOrder() {
			return 1;
		}

		@Override
		public int getNullOrder() {
			return 1;
		}

		@Override
		public int getRamschOrder() {
			return 1;
		}

		@Override
		public int getPoints() {
			return 0;
		}
	},
	/**
	 * Nine
	 */
	NINE {
		@Override
		public String shortString() {
			return "9";} //$NON-NLS-1$

		@Override
		public String longString() {
			return "Nine";} //$NON-NLS-1$

		@Override
		public int getSuitGrandOrder() {
			return 2;
		}

		@Override
		public int getNullOrder() {
			return 2;
		}

		@Override
		public int getRamschOrder() {
			return 2;
		}

		@Override
		public int getPoints() {
			return 0;
		}
	},
	/**
	 * Queen or Ober
	 */
	QUEEN {
		@Override
		public String shortString() {
			return "Q";} //$NON-NLS-1$

		@Override
		public String longString() {
			return "Queen";} //$NON-NLS-1$

		@Override
		public int getSuitGrandOrder() {
			return 3;
		}

		@Override
		public int getNullOrder() {
			return 5;
		}

		@Override
		public int getRamschOrder() {
			return 4;
		}

		@Override
		public int getPoints() {
			return 3;
		}
	},
	/**
	 * King or König
	 */
	KING {
		@Override
		public String shortString() {
			return "K";} //$NON-NLS-1$

		@Override
		public String longString() {
			return "King";} //$NON-NLS-1$

		@Override
		public int getSuitGrandOrder() {
			return 4;
		}

		@Override
		public int getNullOrder() {
			return 6;
		}

		@Override
		public int getRamschOrder() {
			return 5;
		}

		@Override
		public int getPoints() {
			return 4;
		}
	},
	/**
	 * Ten
	 */
	TEN {
		@Override
		public String shortString() {
			return "T";} //$NON-NLS-1$

		@Override
		public String longString() {
			return "Ten";} //$NON-NLS-1$

		@Override
		public int getSuitGrandOrder() {
			return 5;
		}

		@Override
		public int getNullOrder() {
			return 3;
		}

		@Override
		public int getRamschOrder() {
			return 3;
		}

		@Override
		public int getPoints() {
			return 10;
		}
	},
	/**
	 * Ace or Daus
	 */
	ACE {
		@Override
		public String shortString() {
			return "A";} //$NON-NLS-1$

		@Override
		public String longString() {
			return "Ace";} //$NON-NLS-1$

		@Override
		public int getSuitGrandOrder() {
			return 6;
		}

		@Override
		public int getNullOrder() {
			return 7;
		}

		@Override
		public int getRamschOrder() {
			return 6;
		}

		@Override
		public int getPoints() {
			return 11;
		}
	},
	/**
	 * Jack or Unter
	 */
	JACK {
		@Override
		public String shortString() {
			return "J";} //$NON-NLS-1$

		@Override
		public String longString() {
			return "Jack";} //$NON-NLS-1$

		@Override
		public int getSuitGrandOrder() {
			return 7;
		}

		@Override
		public int getNullOrder() {
			return 4;
		}

		@Override
		public int getRamschOrder() {
			return 7;
		}

		@Override
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

	/**
	 * Gets the rank of a card given as string
	 * 
	 * @param cardAsString
	 *            Card as string
	 * @return Rank of card
	 */
	public static Rank getRankFromString(String cardAsString) {

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
	 * @return an array containing the ranks without the jack, starting with
	 *         ace, ending with 7
	 */
	public static List<Rank> getRankList() {

		return Arrays.asList(Rank.ACE, Rank.TEN, Rank.KING, Rank.QUEEN,
				Rank.NINE, Rank.EIGHT, Rank.SEVEN);
	}
}
