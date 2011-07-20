/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.8.0
 * Build date: 2011-07-20 21:16:11
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
/**
 * 
 */
package org.jskat.ai.algorithmic;

import org.jskat.util.Card;
import org.jskat.util.CardList;

/**
 * @author Markus J. Luzius <br>
 * created: 08.07.2011 17:22:49
 *
 */
public interface IAlgorithmicAIPlayer {

	/** Prompts the ai player instance to play a card
	 * @return the card to play
	 */
	Card playCard();

	/** Asks the ai player instance to discard two skat cards
	 * @param bidEvaluator
	 * @return the discarded skat cards
	 */
	CardList discardSkat(BidEvaluator bidEvaluator);

}
