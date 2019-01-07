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
package org.jskat.util.rule;

import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.GameType;
import org.jskat.util.Rank;
import org.jskat.util.Suit;

/**
 * Implementation of skat rules for Suit games
 */
public class SuitRule extends SuitGrandRule {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMultiplier(CardList cards, GameType gameType) {
		if (gameType == GameType.GRAND || gameType == GameType.RAMSCH
				|| gameType == GameType.NULL) {
			throw new IllegalArgumentException("Wrong ruleset - " + gameType);
		}
		int result = 1;
		if (cards.contains(Card.CJ)) {
			result++;
			if (cards.contains(Card.SJ)) {
				result++;
				if (cards.contains(Card.HJ)) {
					result++;
					if (cards.contains(Card.DJ)) {
						result++;
						for (Rank r : Rank.getRankList()) {
							if (cards.contains(Card.getCard(
									Suit.valueOf(gameType.toString()), r))) {
								result++;
							} else {
								break;
							}
						}
					}
				}
			}
		} else {
			result++;
			if (!cards.contains(Card.SJ)) {
				result++;
				if (!cards.contains(Card.HJ)) {
					result++;
					if (!cards.contains(Card.DJ)) {
						result++;
						for (Rank r : Rank.getRankList()) {
							if (!cards.contains(Card.getCard(
									Suit.valueOf(gameType.toString()), r))) {
								result++;
							} else {
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}
}
