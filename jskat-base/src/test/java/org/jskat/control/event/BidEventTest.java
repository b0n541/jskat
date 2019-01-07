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
package org.jskat.control.event;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jskat.AbstractJSkatTest;
import org.jskat.control.event.skatgame.AbstractBidEvent;
import org.jskat.control.event.skatgame.BidEvent;
import org.jskat.data.SkatGameData;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class BidEventTest extends AbstractJSkatTest {

	private SkatGameData data;
	private AbstractBidEvent event;
	private AbstractBidEvent event2;
	private AbstractBidEvent event3;

	@Before
	public void setUp() {
		data = new SkatGameData();
		event = new BidEvent(Player.FOREHAND, 18);
		event2 = new BidEvent(Player.MIDDLEHAND, 18);
		event3 = new BidEvent(Player.FOREHAND, 20);
	}

	@Test
	public void skatGameDataAfterEvent() {

		event.processForward(data);

		checkMaxBid(18);
		checkPlayerBid(Player.FOREHAND, 18);
		checkPlayerBid(Player.MIDDLEHAND, 0);
		checkPlayerBid(Player.REARHAND, 0);

		event2.processForward(data);

		checkMaxBid(18);
		checkPlayerBid(Player.FOREHAND, 18);
		checkPlayerBid(Player.MIDDLEHAND, 18);
		checkPlayerBid(Player.REARHAND, 0);

		event3.processForward(data);

		checkMaxBid(20);
		checkPlayerBid(Player.FOREHAND, 20);
		checkPlayerBid(Player.MIDDLEHAND, 18);
		checkPlayerBid(Player.REARHAND, 0);
	}

	private void checkMaxBid(Integer bid) {
		assertThat(data.getMaxBidValue(), is(equalTo(bid)));
	}

	private void checkPlayerBid(Player player, Integer bid) {
		assertThat(data.getMaxPlayerBid(player), is(equalTo(bid)));
	}

	@Test
	public void skatGameDataBeforeEvent() {

		event.processForward(data);
		event2.processForward(data);
		event3.processForward(data);
		event3.processBackward(data);

		checkMaxBid(18);
		checkPlayerBid(Player.FOREHAND, 18);
		checkPlayerBid(Player.MIDDLEHAND, 18);
		checkPlayerBid(Player.REARHAND, 0);

		event2.processBackward(data);

		checkMaxBid(18);
		checkPlayerBid(Player.FOREHAND, 18);
		checkPlayerBid(Player.MIDDLEHAND, 0);
		checkPlayerBid(Player.REARHAND, 0);

		event.processBackward(data);

		checkMaxBid(0);
		checkPlayerBid(Player.FOREHAND, 0);
		checkPlayerBid(Player.MIDDLEHAND, 0);
		checkPlayerBid(Player.REARHAND, 0);
	}
}
