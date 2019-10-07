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

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects
				.hash(gameNo, gameVariant, leftPlayerPosition, rightPlayerPosition, userPosition);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;			
		}
		if (obj == null) {
			return false;			
		}
		if (getClass() != obj.getClass()) {
			return false;			
		}
		final GameStartEvent other = (GameStartEvent) obj;
		
		return Objects.equals(gameNo, other.gameNo) &&
				Objects.equals(gameVariant, other.gameVariant) &&
				Objects.equals(leftPlayerPosition, other.leftPlayerPosition) &&
				Objects.equals(rightPlayerPosition, other.rightPlayerPosition) &&
				Objects.equals(userPosition, other.userPosition);
	}
}
