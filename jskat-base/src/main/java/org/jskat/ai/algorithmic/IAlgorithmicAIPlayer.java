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
package org.jskat.ai.algorithmic;

import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * @author Markus J. Luzius <br>
 *         created: 08.07.2011 17:22:49
 * 
 */
public interface IAlgorithmicAIPlayer {

	/**
	 * Prompts the ai player instance to play a card
	 * 
	 * @return the card to play
	 */
	Card playCard();

	/**
	 * Asks the ai player instance to discard two skat cards
	 * 
	 * @param bidEvaluator
	 *            Bid evaluator
	 * @return the discarded skat cards
	 */
	CardList discardSkat(BidEvaluator bidEvaluator);

}
