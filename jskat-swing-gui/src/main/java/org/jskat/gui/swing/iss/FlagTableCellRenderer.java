package org.jskat.gui.swing.iss;

import org.jskat.gui.img.JSkatGraphicRepository;
import org.jskat.gui.img.JSkatGraphicRepository.Flag;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renders flag symbols for player list on ISS lobby
 */
public class FlagTableCellRenderer extends DefaultTableCellRenderer {


    private final static JSkatGraphicRepository bitmaps = JSkatGraphicRepository.INSTANCE;

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table,
                                                   final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int row, final int column) {

        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Cell doesn't contain a String!");
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
