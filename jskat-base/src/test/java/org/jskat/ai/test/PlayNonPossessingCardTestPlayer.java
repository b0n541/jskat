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
package org.jskat.ai.test;

import java.util.Random;

import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;

/**
 * Test player throws an exception during card play.
 */
public class PlayNonPossessingCardTestPlayer extends AIPlayerRND {

	/**
	 * Random generator.
	 */
	private final Random random = new Random();

	@Override
	public final Card playCard() {
		final CardDeck unpossessedCards = new CardDeck();
		unpossessedCards.removeAll(knowledge.getOwnCards());

		return unpossessedCards.get(random.nextInt(unpossessedCards.size()));
	}
}
