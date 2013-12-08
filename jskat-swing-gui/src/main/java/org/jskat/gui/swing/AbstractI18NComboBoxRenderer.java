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
package org.jskat.gui.swing;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Abstract implementation of a combo box renderer with I18N functionality
 */
public abstract class AbstractI18NComboBoxRenderer extends JPanel implements
		ListCellRenderer {

	private static final long serialVersionUID = 1L;

	JLabel cellItemLabel;

	/**
	 * Constructor
	 */
	protected AbstractI18NComboBoxRenderer() {

		super();

		setLayout(LayoutFactory.getMigLayout("fill")); //$NON-NLS-1$
		cellItemLabel = new JLabel(" "); //$NON-NLS-1$
		add(cellItemLabel);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			@SuppressWarnings("unused") int index, boolean isSelected,
			@SuppressWarnings("unused") boolean cellHasFocus) {

		cellItemLabel.setFont(list.getFont());

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		cellItemLabel.setText(getValueText(value));

		return this;
	}

	public abstract String getValueText(Object value);
}
