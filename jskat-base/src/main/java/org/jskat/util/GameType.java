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

/**
 * Contains all game types
 */
public enum GameType {
	/**
	 * Suit game with Clubs as trump
	 */
	CLUBS {
		@Override
		public Suit getTrumpSuit() {
			return Suit.CLUBS;
		}
	},
	/**
	 * Suit game with Spades as trump
	 */
	SPADES {
		@Override
		public Suit getTrumpSuit() {
			return Suit.SPADES;
		}
	},
	/**
	 * Suit game with Hearts as trump
	 */
	HEARTS {
		@Override
		public Suit getTrumpSuit() {
			return Suit.HEARTS;
		}
	},
	/**
	 * Suit game with Diamonds as trump
	 */
	DIAMONDS {
		@Override
		public Suit getTrumpSuit() {
			return Suit.DIAMONDS;
		}
	},
	/**
	 * Grand game
	 */
	GRAND {
		@Override
		public Suit getTrumpSuit() {
			return null;
		}
	},
	/**
	 * Null game
	 */
	NULL {
		@Override
		public Suit getTrumpSuit() {
			return null;
		}
	},
	/**
	 * Ramsch game, this is not playable under ISPA rules
	 */
	RAMSCH {
		@Override
		public Suit getTrumpSuit() {
			return null;
		}
	},
	/**
	 * Passed in game, no player did a bid
	 */
	PASSED_IN {
		@Override
		public Suit getTrumpSuit() {
			return null;
		}
	};

	/**
	 * Gets the trump suit
	 * 
	 * @return Trump suit, null if there is no trump suit
	 */
	public abstract Suit getTrumpSuit();
}
