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
package org.jskat.gui.human;

import org.jskat.gui.action.JSkatActionEvent;
import org.jskat.player.AbstractJSkatPlayer;

/**
 * Abstract implementation of a human player for JSkat
 */
public abstract class AbstractHumanJSkatPlayer extends AbstractJSkatPlayer {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAIPlayer() {
		return false;
	}

	/**
	 * Informs the human player about an action that was performed
	 * 
	 * @param e
	 *            Action
	 */
	public abstract void actionPerformed(final JSkatActionEvent e);
}
