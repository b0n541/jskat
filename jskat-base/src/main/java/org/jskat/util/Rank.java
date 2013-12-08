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

import java.util.Arrays;
import java.util.List;

/**
 * Contains all ranks.
 */
public enum Rank {
	/**
	 * Seven
	 */
	SEVEN {
		@Override
		public String shortString() {
			return "7";
		}

		@Override
		public String longString() {
			return "Seven";
		}

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
			return "8";
		}

		@Override
		public String longString() {
			return "Eight";
		}

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
			return "9";
		}

		@Override
		public String longString() {
			return "Nine";
		}

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
			return "Q";
		}

		@Override
		public String longString() {
			return "Queen";
		}

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
			return 3;
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
			return "K";
		}

		@Override
		public String longString() {
			return "King";
		}

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
			return 4;
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
			return "T";
		}

		@Override
		public String longString() {
			return "Ten";
		}

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
			return 5;
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
			return "A";
		}

		@Override
		public String longString() {
			return "Ace";
		}

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
			return "J";
		}

		@Override
		public String longString() {
			return "Jack";
		}

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
	 * Gets a short string representation of the constant.
	 * 
	 * @return Short string representation of the constant
	 */
	public abstract String shortString();

	/**
	 * Gets a long string representation of the constant.
	 * 
	 * @return Long string representation of the constant
	 */
	public abstract String longString();

	/**
	 * Gets the order of the constant in suit and grand games.
	 * 
	 * @return Order in suit and grand games
	 */
	public abstract int getSuitGrandOrder();

	/**
	 * Gets the order of the constant in null games.
	 * 
	 * @return Order in null games
	 */
	public abstract int getNullOrder();

	/**
	 * Gets the order of the constant in ramsch games.
	 * 
	 * @return Order in ramsch games
	 */
	public abstract int getRamschOrder();

	/**
	 * Gets the points of the card for game value calculation.
	 * 
	 * @return Points of the card
	 */
	public abstract int getPoints();

	/**
	 * Gets the rank of a card given as string.
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

	/**
	 * converts the rank of a card to a specific int value (7=1, 8=2, 9=4, ...
	 * A=64, J=128)
	 * 
	 * @return an int representation of the card's rank
	 */
	@Deprecated
	public int toBinaryFlag() {
		return (int) Math.pow(2, this.ordinal());
	}

	public int toNullBinaryFlag() {
		return (int) Math.pow(2, this.getNullOrder());
	}

	public int toSuitBinaryFlag() {
		return (int) Math.pow(2, this.getSuitGrandOrder());
	}

	public int toGrandBinaryFlag() {
		return toSuitBinaryFlag();
	}

	public int toRamschBinaryFlag() {
		return (int) Math.pow(2, this.getRamschOrder());
	}
}
