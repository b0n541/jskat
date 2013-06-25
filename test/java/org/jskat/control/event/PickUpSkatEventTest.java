package org.jskat.control.event;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jskat.AbstractJSkatTest;
import org.jskat.data.SkatGameData;
import org.jskat.util.Player;
import org.junit.Before;
import org.junit.Test;

public class PickUpSkatEventTest extends AbstractJSkatTest {

	private SkatGameData data;
	private PickUpSkatEvent event;

	@Before
	public void setUp() {
		data = new SkatGameData();
		event = new PickUpSkatEvent(Player.FOREHAND);
	}

	@Test
	public void SkatGameDataAfterEvent() {

		event.processForward(data);

		assertThat(data.isHand(), is(false));
	}

	@Test
	public void SkatGameDataBeforeEvent() {

		event.processForward(data);
		event.processBackward(data);

		assertThat(data.isHand(), is(true));
	}
}
