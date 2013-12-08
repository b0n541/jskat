/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat.gui.action.human;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.action.JSkatAction;

/**
 * Implements the action for playing a card
 */
public class PlayCardAction extends AbstractHumanJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractJSkatAction#AbstractJSkatAction()
	 */
	public PlayCardAction() {

		setActionCommand(JSkatAction.PLAY_CARD);
	}
}
