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
package org.jskat.gui.swing.iss;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Flag;

/**
 * Renders flag symbols for player list on ISS lobby
 */
public class FlagTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private final static JSkatGraphicRepository bitmaps = JSkatGraphicRepository
			.instance();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table,
			final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {

		if (!(value instanceof String)) {
			throw new IllegalArgumentException("Cell doesn't contain a String!"); //$NON-NLS-1$
		}

		Component defaultComponent = super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);

		JLabel flagLabel = new JLabel();
		for (char languageChar : ((String) value).toCharArray()) {
			Flag flag = parseFlag(languageChar);
			if (flag != null) {
				ImageIcon imageIcon = new ImageIcon(getFlagSymbol(flag));
				flagLabel = new JLabel(imageIcon);
				flagLabel.setToolTipText(flag.getLanguageForFlag());
			}
		}

		return flagLabel;
	}

	private static Image getFlagSymbol(final Flag flag) {
		return bitmaps.getFlagImage(flag);
	}

	private static Flag parseFlag(final char languageChar) {
		return Flag.valueOf(languageChar);
	}
}
