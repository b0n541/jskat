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

import org.jskat.data.GameAnnouncement;
import org.jskat.data.GameAnnouncement.GameAnnouncementFactory;
import org.jskat.data.SkatGameData;
import org.jskat.util.Player;

/**
 * Event for game announcement.
 */
public final class GameAnnouncementEvent extends AbstractPlayerMoveEvent {

	public final GameAnnouncement announcement;

	public GameAnnouncementEvent(Player player, GameAnnouncement announcement) {
		super(player);
		this.announcement = announcement;
	}

	@Override
	public void processForward(SkatGameData data) {
		data.setAnnouncement(announcement);
	}

	@Override
	public void processBackward(SkatGameData data) {
		data.setAnnouncement(GameAnnouncementFactory.getEmptyAnnouncement());
	}

	@Override
	protected String getMoveDetails() {
		return announcement.toString();
	}
}
