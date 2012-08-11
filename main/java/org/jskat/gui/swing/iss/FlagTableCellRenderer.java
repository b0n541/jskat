package org.jskat.gui.swing.iss;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Flag;
import org.jskat.util.JSkatResourceBundle;

/**
 * Renders flag symbols for player list on ISS lobby
 */
public class FlagTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private final static JSkatGraphicRepository bitmaps = JSkatGraphicRepository
			.instance();
	private final static JSkatResourceBundle strings = JSkatResourceBundle
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

		JPanel result = new JPanel();
		result.setBackground(defaultComponent.getBackground());

		for (char languageChar : ((String) value).toCharArray()) {
			Flag flag = parseFlag(languageChar);
			if (flag != null) {
				ImageIcon imageIcon = new ImageIcon(getFlagSymbol(flag));
				JLabel flagLabel = new JLabel(imageIcon);
				flagLabel.setToolTipText(flag.getLanguageForFlag());
				result.add(flagLabel);
			}
		}

		return result;
	}

	private static Image getFlagSymbol(final Flag flag) {
		return bitmaps.getFlagImage(flag);
	}

	private static Flag parseFlag(final char languageChar) {
		return Flag.valueOf(languageChar);
	}
}
