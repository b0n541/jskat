package org.jskat.gui.swing;

import org.jskat.control.JSkatMaster;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JSkatWindowAdapter extends WindowAdapter {
    @Override
    public void windowClosing(final WindowEvent e) {
        JSkatMaster.INSTANCE.exitJSkat();
    }
}
