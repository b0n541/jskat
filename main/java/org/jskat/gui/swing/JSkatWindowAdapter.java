package org.jskat.gui.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jskat.control.JSkatMaster;

public class JSkatWindowAdapter extends WindowAdapter {

	private JSkatMaster jskat;

	public JSkatWindowAdapter(JSkatMaster jskat) {
		this.jskat = jskat;
	}

	@Override
	public void windowClosing(final WindowEvent e) {
		jskat.exitJSkat();
	}
}
