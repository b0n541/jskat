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

import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for game start.
 */
public final class GameStartEvent implements Event {

	private final Player dealer;

	public GameStartEvent(Player dealer) {
		this.dealer = dealer;
	}

	@Override
	public final void processForward(SkatGameData data) {
		data.setDealer(dealer);
	}

	@Override
	public final void processBackward(SkatGameData data) {
		data.setDealer(null);
	}
}
