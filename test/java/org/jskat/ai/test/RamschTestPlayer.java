/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.10.0-SNAPSHOT
 * Copyright (C) 2012-03-13
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

/**
 * Test player that only plays ramsch games
 */
public class RamschTestPlayer extends NoBiddingTestPlayer {

	private boolean playGrandHand = false;

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
	public boolean playGrandHand() {
		return playGrandHand;
	}
}
