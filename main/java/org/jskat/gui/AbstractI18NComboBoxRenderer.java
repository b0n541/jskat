/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0
 * Build date: 2011-10-09
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
package org.jskat.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import net.miginfocom.swing.MigLayout;

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

		setLayout(new MigLayout("fill")); //$NON-NLS-1$
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
