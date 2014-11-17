/**
 * Copyright (C) 2003 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jskat.control.event;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.TrickCardPlayedEvent;
import org.jskat.data.SkatGameData;
import org.jskat.data.Trick;
import org.jskat.util.Card;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class TrickCardPlayedEventTest extends AbstractJSkatTest {

	private SkatGameData data;
	private List<TrickCardPlayedEvent> eventList;

	@Before
	public void setUp() {
		data = new SkatGameData();
		data.addTrick(new Trick(0, Player.FOREHAND));
		data.addPlayerCard(Player.FOREHAND, Card.CJ);
		data.addPlayerCard(Player.MIDDLEHAND, Card.SJ);
		data.addPlayerCard(Player.REARHAND, Card.HJ);

		eventList = new ArrayList<>();
		eventList.add(new TrickCardPlayedEvent(Player.FOREHAND, Card.CJ));
		eventList.add(new TrickCardPlayedEvent(Player.MIDDLEHAND, Card.SJ));
		eventList.add(new TrickCardPlayedEvent(Player.REARHAND, Card.HJ));
	}

	@Test
	public void skatGameDataAfterEvent() {

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.FOREHAND), hasItem(Card.CJ));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND), hasItem(Card.SJ));
		assertThat(data.getPlayerCards(Player.REARHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.REARHAND), hasItem(Card.HJ));
		assertNull(data.getCurrentTrick().getFirstCard());
		assertNull(data.getCurrentTrick().getSecondCard());
		assertNull(data.getCurrentTrick().getThirdCard());

		eventList.get(0).processForward(data);

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND), hasItem(Card.SJ));
		assertThat(data.getPlayerCards(Player.REARHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.REARHAND), hasItem(Card.HJ));
		assertThat(data.getCurrentTrick().getFirstCard(), is(Card.CJ));
		assertNull(data.getCurrentTrick().getSecondCard());
		assertNull(data.getCurrentTrick().getThirdCard());

		eventList.get(1).processForward(data);

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.REARHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.REARHAND), hasItem(Card.HJ));
		assertThat(data.getCurrentTrick().getFirstCard(), is(Card.CJ));
		assertThat(data.getCurrentTrick().getSecondCard(), is(Card.SJ));
		assertNull(data.getCurrentTrick().getThirdCard());

		eventList.get(2).processForward(data);

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.REARHAND).size(), is(0));
		assertThat(data.getCurrentTrick().getFirstCard(), is(Card.CJ));
		assertThat(data.getCurrentTrick().getSecondCard(), is(Card.SJ));
		assertThat(data.getCurrentTrick().getThirdCard(), is(Card.HJ));
	}

	@Test
	public void skatGameDataBeforeEvent() {

		eventList.get(0).processForward(data);
		eventList.get(1).processForward(data);
		eventList.get(2).processForward(data);

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.REARHAND).size(), is(0));
		assertThat(data.getCurrentTrick().getFirstCard(), is(Card.CJ));
		assertThat(data.getCurrentTrick().getSecondCard(), is(Card.SJ));
		assertThat(data.getCurrentTrick().getThirdCard(), is(Card.HJ));

		eventList.get(2).processBackward(data);

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.REARHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.REARHAND), hasItem(Card.HJ));
		assertThat(data.getCurrentTrick().getFirstCard(), is(Card.CJ));
		assertThat(data.getCurrentTrick().getSecondCard(), is(Card.SJ));
		assertNull(data.getCurrentTrick().getThirdCard());

		eventList.get(1).processBackward(data);

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(0));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND), hasItem(Card.SJ));
		assertThat(data.getPlayerCards(Player.REARHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.REARHAND), hasItem(Card.HJ));
		assertThat(data.getCurrentTrick().getFirstCard(), is(Card.CJ));
		assertNull(data.getCurrentTrick().getSecondCard());
		assertNull(data.getCurrentTrick().getThirdCard());

		eventList.get(0).processBackward(data);

		assertThat(data.getPlayerCards(Player.FOREHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.FOREHAND), hasItem(Card.CJ));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.MIDDLEHAND), hasItem(Card.SJ));
		assertThat(data.getPlayerCards(Player.REARHAND).size(), is(1));
		assertThat(data.getPlayerCards(Player.REARHAND), hasItem(Card.HJ));
		assertNull(data.getCurrentTrick().getFirstCard());
		assertNull(data.getCurrentTrick().getSecondCard());
		assertNull(data.getCurrentTrick().getThirdCard());
	}
}
