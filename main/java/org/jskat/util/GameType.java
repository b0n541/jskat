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
 * Contains all game types
 */
public enum GameType {
	/**
	 * Suit game with Clubs as trump
	 */
	CLUBS { 
		@Override
		public Suit getTrumpSuit() { return Suit.CLUBS; }
	}, 
	/**
	 * Suit game with Spades as trump
	 */
	SPADES { 
		@Override
		public Suit getTrumpSuit() { return Suit.SPADES; }
	}, 
	/**
	 * Suit game with Hearts as trump
	 */
	HEARTS { 
		@Override
		public Suit getTrumpSuit() { return Suit.HEARTS; }
	}, 
	/**
	 * Suit game with Diamonds as trump
	 */
	DIAMONDS { 
		@Override
		public Suit getTrumpSuit() { return Suit.DIAMONDS; }
	}, 
	/**
	 * Grand game
	 */
	GRAND { 
		@Override
		public Suit getTrumpSuit() { return null; }
	}, 
	/**
	 * Null game
	 */
	NULL { 
		@Override
		public Suit getTrumpSuit() { return null; }
	}, 
	/**
	 * Ramsch game, this is not playable under ISPA rules
	 */
	RAMSCH {
		@Override
		public Suit getTrumpSuit() { return null; }
	},
	/**
	 * Passed in game, no player did a bid
	 */
	PASSED_IN { 
		@Override
		public Suit getTrumpSuit() { return null; }
	};
	
	/**
	 * Gets the trump suit
	 * 
	 * @return Trump suit, null if there is no trump suit
	 */
	public abstract Suit getTrumpSuit();

	public boolean equals(Suit s) {
		return s.name().equalsIgnoreCase(this.name());
	}
	
	public Suit asSuit() {
		try {
			return Suit.valueOf(this.name());
		} catch (Exception e) {
			return null;
		}
	}
	
}
