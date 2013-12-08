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
package org.jskat.gui.swing.table;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jskat.gui.action.main.StartSkatSeriesAction;
import org.jskat.gui.swing.LayoutFactory;

class StartContextPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private StartSkatSeriesAction action;

	public StartContextPanel(StartSkatSeriesAction newAction) {

		this.action = newAction;
		initPanel();
	}

	public void initPanel() {

		this.setLayout(LayoutFactory.getMigLayout("fill", "fill", "fill")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		JPanel panel = new JPanel(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
		panel.add(new JButton(this.action), "center"); //$NON-NLS-1$
		panel.setOpaque(false);
		this.add(panel, "center"); //$NON-NLS-1$

		setOpaque(false);
	}
}
