/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
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
package org.jskat.ai.test;

import java.util.Random;

import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.util.Card;
import org.jskat.util.CardDeck;

/**
 * Test player throws an excpetion during card play
 */
public class PlayNonPossessingCardTestPlayer extends AIPlayerRND {

	private final static Random random = new Random();

	@Override
	public Card playCard() {
		CardDeck unpossessedCards = new CardDeck();
		unpossessedCards.removeAll(knowledge.getOwnCards());

		return unpossessedCards.get(random.nextInt(unpossessedCards.size()));
	}
}
