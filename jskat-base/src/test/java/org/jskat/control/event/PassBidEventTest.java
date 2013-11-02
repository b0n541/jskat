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
package org.jskat.control.event;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.SkatGameData;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class PassBidEventTest extends AbstractJSkatTest {

	private SkatGameData data;
	private PassBidEvent event;
	private PassBidEvent event2;
	private PassBidEvent event3;

	@Before
	public void setUp() {
		data = new SkatGameData();
		event = new PassBidEvent(Player.FOREHAND);
		event2 = new PassBidEvent(Player.MIDDLEHAND);
		event3 = new PassBidEvent(Player.REARHAND);
	}

	@Test
	public void SkatGameDataAfterEvent() {

		event.processForward(data);

		checkMaxBid(0);
		checkPlayerBid(Player.FOREHAND, 0);
		assertThat(data.isPlayerPass(Player.FOREHAND), is(true));
		checkPlayerBid(Player.MIDDLEHAND, 0);
		assertThat(data.isPlayerPass(Player.MIDDLEHAND), is(false));
		checkPlayerBid(Player.REARHAND, 0);
		assertThat(data.isPlayerPass(Player.REARHAND), is(false));

		event2.processForward(data);

		checkMaxBid(0);
		checkPlayerBid(Player.FOREHAND, 0);
		assertThat(data.isPlayerPass(Player.FOREHAND), is(true));
		checkPlayerBid(Player.MIDDLEHAND, 0);
		assertThat(data.isPlayerPass(Player.MIDDLEHAND), is(true));
		checkPlayerBid(Player.REARHAND, 0);
		assertThat(data.isPlayerPass(Player.REARHAND), is(false));

		event3.processForward(data);

		checkMaxBid(0);
		checkPlayerBid(Player.FOREHAND, 0);
		assertThat(data.isPlayerPass(Player.FOREHAND), is(true));
		checkPlayerBid(Player.MIDDLEHAND, 0);
		assertThat(data.isPlayerPass(Player.MIDDLEHAND), is(true));
		checkPlayerBid(Player.REARHAND, 0);
		assertThat(data.isPlayerPass(Player.REARHAND), is(true));
	}

	private void checkMaxBid(Integer bid) {
		assertThat(data.getMaxBidValue(), is(equalTo(bid)));
	}

	private void checkPlayerBid(Player player, Integer bid) {
		assertThat(data.getMaxPlayerBid(player), is(equalTo(bid)));
	}

	@Test
	public void SkatGameDataBeforeEvent() {

		event.processForward(data);
		event2.processForward(data);
		event3.processForward(data);
		event3.processBackward(data);

		checkMaxBid(0);
		checkPlayerBid(Player.FOREHAND, 0);
		assertThat(data.isPlayerPass(Player.FOREHAND), is(true));
		checkPlayerBid(Player.MIDDLEHAND, 0);
		assertThat(data.isPlayerPass(Player.MIDDLEHAND), is(true));
		checkPlayerBid(Player.REARHAND, 0);
		assertThat(data.isPlayerPass(Player.REARHAND), is(false));

		event2.processBackward(data);

		checkMaxBid(0);
		checkPlayerBid(Player.FOREHAND, 0);
		assertThat(data.isPlayerPass(Player.FOREHAND), is(true));
		checkPlayerBid(Player.MIDDLEHAND, 0);
		assertThat(data.isPlayerPass(Player.MIDDLEHAND), is(false));
		checkPlayerBid(Player.REARHAND, 0);
		assertThat(data.isPlayerPass(Player.REARHAND), is(false));

		event.processBackward(data);

		checkMaxBid(0);
		checkPlayerBid(Player.FOREHAND, 0);
		assertThat(data.isPlayerPass(Player.FOREHAND), is(false));
		checkPlayerBid(Player.MIDDLEHAND, 0);
		assertThat(data.isPlayerPass(Player.MIDDLEHAND), is(false));
		checkPlayerBid(Player.REARHAND, 0);
		assertThat(data.isPlayerPass(Player.REARHAND), is(false));
	}
}
