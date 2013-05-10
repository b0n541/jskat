/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.1
 * Copyright (C) 2013-05-10
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.ai.mjl;

import org.apache.log4j.Logger;
import org.jskat.player.PlayerKnowledge;
import org.jskat.util.CardList;

/**
 * @author Markus J. Luzius <br>
 * created: 24.01.2011 18:20:09
 *
 */
public abstract class AbstractCardPlayer implements CardPlayer {
	private static final Logger log = Logger.getLogger(AbstractCardPlayer.class);
	
	protected CardList cards = null;

	protected AbstractCardPlayer(CardList cards) {
		this.cards = cards;
	}
	
	public void startGame(PlayerKnowledge knowledge) {
		log.debug("Starting game...");
		cards.sort(knowledge.getGame().getGameType());
	}


}
