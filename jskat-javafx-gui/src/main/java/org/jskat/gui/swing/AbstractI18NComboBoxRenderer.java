package org.jskat.gui.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract implementation of a combo box renderer with I18N functionality
 */
public abstract class AbstractI18NComboBoxRenderer extends JPanel implements ListCellRenderer {
    JLabel cellItemLabel;

    /**
     * Constructor
     */
    protected AbstractI18NComboBoxRenderer() {

        super();

        setLayout(LayoutFactory.getMigLayout("fill"));
        cellItemLabel = new JLabel(" ");
        add(cellItemLabel);
    }

    @Override
    public Component getListCellRendererComponent(final JList list,
                                                  final Object value,
                                                  @SuppressWarnings("unused") final int index,
                                                  final boolean isSelected,
                                                  @SuppressWarnings("unused") final boolean cellHasFocus) {

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
