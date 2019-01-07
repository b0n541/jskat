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
package org.jskat.control.event.skatgame;

import org.jskat.data.SkatGameData;
import org.jskat.util.GameVariant;
import org.jskat.util.Player;

/**
 * Event for game start.
 */
public final class GameStartEvent implements SkatGameEvent {

	public final Integer gameNo;
	public final GameVariant gameVariant;
	public final Player leftPlayerPosition;
	public final Player rightPlayerPosition;
	public final Player userPosition;

	public GameStartEvent(Integer gameNo, GameVariant gameVariant,
			Player leftPlayerPosition, Player rightPlayerPosition,
			Player userPosition) {
		this.gameNo = gameNo;
		this.gameVariant = gameVariant;
		this.leftPlayerPosition = leftPlayerPosition;
		this.rightPlayerPosition = rightPlayerPosition;
		this.userPosition = userPosition;
	}

	@Override
	public void processForward(SkatGameData data) {
	}

	@Override
	public void processBackward(SkatGameData data) {
	}
}
