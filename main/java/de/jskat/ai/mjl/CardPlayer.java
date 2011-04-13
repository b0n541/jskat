/**
 * JSkat - A skat program written in Java
 * Copyright by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.7.0-SNAPSHOT
 * Build date: 2011-04-13 21:42:39
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

/*

@ShortLicense@

Author: @MJL@

Released: @ReleaseDate@

*/

package de.jskat.ai.mjl;

import de.jskat.ai.PlayerKnowledge;
import de.jskat.util.Card;


/**
 * @author Markus J. Luzius <markus@luzius.de>
 *
 */
interface CardPlayer {

	/** Gets the next card, that the player wants to play
	 * @param trickInfo all necessary information about the trick
	 * @return the card to play
	 */
	Card playNextCard(PlayerKnowledge knowledge);
	
	void startGame(PlayerKnowledge knowledge);

}
