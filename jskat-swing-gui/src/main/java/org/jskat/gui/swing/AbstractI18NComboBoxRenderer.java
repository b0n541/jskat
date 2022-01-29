package org.jskat.gui.swing;

import javax.swing.*;
import java.awt.*;

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
