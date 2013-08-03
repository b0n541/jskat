/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-10
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.test;

import java.util.Random;

import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * Test player throws an excpetion during card play
 */
public class PlayNotAllowedCardTestPlayer extends AIPlayerRND {

	private final static Random random = new Random();

	@Override
	public Card playCard() {
		CardList notAllowedCards = new CardList(knowledge.getOwnCards());
		notAllowedCards.removeAll(getPlayableCards(knowledge.getTrickCards()));

		// sometimes all cards are allowed
		if (notAllowedCards.size() > 0) {
			return notAllowedCards.get(random.nextInt(notAllowedCards.size()));
		} else {
			return super.playCard();
		}
	}
}
