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
package org.jskat.ai.test;

import org.jskat.ai.rnd.AIPlayerRND;

/**
 * Test player that only plays ramsch games
 */
public class RamschTestPlayer extends AIPlayerRND {

	private boolean playGrandHand = false;

	@Override
	public Integer bidMore(final int nextBidValue) {
		return -1;
	}

	@Override
	public Boolean holdBid(final int currBidValue) {
		return false;
	}

	/**
	 * Sets whether the player should play grand hand
	 * 
	 * @param isPlayGrandHand
	 *            TRUE if the player should play grand hand
	 */
	public void setPlayGrandHand(boolean isPlayGrandHand) {
		playGrandHand = isPlayGrandHand;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean playGrandHand() {
		return playGrandHand;
	}
}
