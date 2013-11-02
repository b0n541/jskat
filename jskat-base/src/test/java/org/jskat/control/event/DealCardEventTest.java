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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.SkatGameData;
import org.jskat.util.Card;
import org.jskat.util.CardList;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class DealCardEventTest extends AbstractJSkatTest {

	private SkatGameData data;
	private DealCardEvent event;

	@Before
	public void setUp() {

		data = new SkatGameData();

		Map<Player, CardList> playerCards = new HashMap<Player, CardList>();
		playerCards
				.put(Player.FOREHAND,
						new CardList(Arrays.asList(Card.CJ, Card.SJ, Card.HJ,
								Card.DJ)));
		playerCards
				.put(Player.MIDDLEHAND,
						new CardList(Arrays.asList(Card.CA, Card.SA, Card.HA,
								Card.DA)));
		playerCards
				.put(Player.REARHAND,
						new CardList(Arrays.asList(Card.CT, Card.ST, Card.HT,
								Card.DT)));

		CardList skat = new CardList(Card.D7, Card.D8);

		event = new DealCardEvent(playerCards, skat);
	}

	@Test
	public void SkatGameDataAfterEvent() {

		event.processForward(data);

		checkPlayerCards(Player.FOREHAND, Card.CJ, Card.SJ, Card.HJ, Card.DJ);
		checkPlayerCards(Player.MIDDLEHAND, Card.CA, Card.SA, Card.HA, Card.DA);
		checkPlayerCards(Player.REARHAND, Card.CT, Card.ST, Card.HT, Card.DT);

		checkCardList(data.getDealtSkat(), Card.D7, Card.D8);
		checkCardList(data.getSkat(), Card.D7, Card.D8);
	}

	private void checkPlayerCards(Player player, Card... cards) {
		CardList cardList = data.getDealtCards().get(player);
		checkCardList(cardList, cards);
	}

	private void checkCardList(CardList cardList, Card... cards) {
		assertThat(cardList.size(), is(equalTo(cards.length)));
		assertThat(cardList, hasItems(cards));
	}

	@Test
	public void SkatGameDataBeforeEvent() {

		event.processForward(data);
		event.processBackward(data);

		for (Player player : Player.values()) {
			checkCardList(data.getDealtCards().get(player));
		}
		checkCardList(data.getDealtSkat());
		checkCardList(data.getSkat());
	}
}
