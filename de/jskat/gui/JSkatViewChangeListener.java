package de.jskat.gui;

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jskat.control.JSkatMaster;

/**
 * Listens on GUI changes and propagates them to the JSkatMaster
 */
public class JSkatViewChangeListener implements ChangeListener {

	/**
	 * Constructor
	 * 
	 * @param controller JSkatMaster
	 */
	public JSkatViewChangeListener(JSkatMaster controller) {
		
		this.jskat = controller;
	}
	
	/**
	 * @see ChangeListener#stateChanged(ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		
		if (e.getSource() instanceof JTabbedPane) {
		
			JTabbedPane tabs = (JTabbedPane) e.getSource();
			Component tab = tabs.getSelectedComponent();
			
			if (tab instanceof JSkatTabPanel) {
			
				String tableName = ((JSkatTabPanel) tab).getName();
				this.jskat.setActiveTable(tableName);
			}
		}
	}

	private JSkatMaster jskat;
}
