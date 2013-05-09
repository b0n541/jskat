/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.0
 * Copyright (C) 2013-05-09
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
package org.jskat.gui.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jskat.control.JSkatMaster;

public class JSkatWindowAdapter extends WindowAdapter {

	private JSkatMaster jskat;

	public JSkatWindowAdapter(JSkatMaster jskat) {
		this.jskat = jskat;
	}

	@Override
	public void windowClosing(final WindowEvent e) {
		jskat.exitJSkat();
	}
}
