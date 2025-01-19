package org.jskat.gui.swing.iss;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.NumberFormat;

public class PlayerStrengthTableCellRenderer extends DefaultTableCellRenderer {


    PlayerStrengthTableCellRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public void setValue(Object playerStrength) {
        Object result = playerStrength;
        if ((playerStrength != null) && (playerStrength instanceof Number)) {
            Number numberValue = (Number) playerStrength;
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(1);
            formatter.setMaximumFractionDigits(1);
            result = formatter.format(numberValue.doubleValue());
        }
        super.setValue(result);
    }
}
