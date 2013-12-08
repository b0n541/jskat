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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jskat.gui.action.AbstractJSkatAction;
import org.jskat.gui.action.JSkatActionEvent;
import org.jskat.player.JSkatPlayer;

/**
 * Abstract implementation of an human player action for JSkat<br />
 * When the action is performed the GUI player implementation of
 * {@link JSkatPlayer} is triggered
 */
public abstract class AbstractHumanJSkatAction extends AbstractJSkatAction {

	private static final long serialVersionUID = 1L;

	/**
	 * @see AbstractAction#actionPerformed(ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		jskat.triggerHuman(new JSkatActionEvent(e.getActionCommand(), e
				.getSource()));
	}
}
