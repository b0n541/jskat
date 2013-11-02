/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
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
package org.jskat.control.event;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class DiscardSkatEventTest extends AbstractJSkatTest {

	private SkatGameData data;

	private PickUpSkatEvent pickUpSkatEvent;
	private DiscardSkatEvent event;

	private CardList dealtPlayerCards;
	private CardList dealtSkatCards;
	private CardList discardedCards;
	private CardList newPlayerCards;

	@Before
	public void setUp() {

		dealtPlayerCards = new CardList(Card.DA, Card.DT, Card.DK, Card.DQ,
				Card.DJ, Card.D9, Card.D8, Card.D7, Card.HA, Card.HT);
		dealtSkatCards = new CardList(Card.CJ, Card.SJ);
		discardedCards = new CardList(Card.DA, Card.HA);
		newPlayerCards = new CardList(Card.DT, Card.DK, Card.DQ, Card.DJ,
				Card.D9, Card.D8, Card.D7, Card.HT, Card.CJ, Card.SJ);

		data = new SkatGameData();
		data.setDealtSkatCards(dealtSkatCards);
		data.addDealtCards(Player.FOREHAND, dealtPlayerCards);

		pickUpSkatEvent = new PickUpSkatEvent(Player.FOREHAND);
		pickUpSkatEvent.processForward(data);

		event = new DiscardSkatEvent(Player.FOREHAND, discardedCards);
	}

	@Test
	public void SkatGameDataAfterEvent() {

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(12));
		assertThat(data.getSkat().size(), is(0));

		event.processForward(data);

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(10));
		for (Card card : newPlayerCards) {
			assertTrue(data.getPlayerCards(Player.FOREHAND).contains(card));
		}
		assertThat(data.getSkat().size(), is(2));
		assertThat(data.getSkat(), hasItems(Card.DA, Card.HA));
	}

	@Test
	public void SkatGameDataBeforeEvent() {

		event.processForward(data);
		event.processBackward(data);

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(12));
		for (Card card : dealtPlayerCards) {
			assertTrue(data.getPlayerCards(Player.FOREHAND).contains(card));
		}
		assertThat(data.getSkat().size(), is(0));
	}
}
