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
package org.jskat.gui.action;

/**
 * Action event for JSkat<br />
 * Swings action event is not used to enable other platforms like Android
 */
public class JSkatActionEvent {

	private String actionCommand = null;
	private Object actionSource = null;

	/**
	 * Constructor
	 * 
	 * @param actionCommand
	 *            Action command
	 * @param actionSource
	 *            Action source
	 */
	public JSkatActionEvent(final String actionCommand,
			final Object actionSource) {
		this.actionCommand = actionCommand;
		this.actionSource = actionSource;
	}

	/**
	 * Constructor
	 * 
	 * @param jskatAction
	 *            JSkat action
	 * @param actionSource
	 *            Action source
	 */
	public JSkatActionEvent(final JSkatAction jskatAction,
			final Object actionSource) {
		this.actionCommand = jskatAction.toString();
		this.actionSource = actionSource;
	}

	/**
	 * Constructor
	 * 
	 * @param jskatAction
	 *            JSkat action
	 */
	public JSkatActionEvent(final JSkatAction jskatAction) {
		this.actionCommand = jskatAction.toString();
	}

	/**
	 * Gets the action command
	 * 
	 * @return Action command
	 */
	public String getActionCommand() {
		return this.actionCommand;
	}

	/**
	 * Gets the action source
	 * 
	 * @return Action source
	 */
	public Object getSource() {
		return this.actionSource;
	}

	@Override
	public String toString() {
		return "Action: " + actionCommand + " source: " + actionSource; //$NON-NLS-1$//$NON-NLS-2$
	}
}
