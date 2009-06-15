/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat.data;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import de.jskat.control.SkatTable;

/**
 * Holds all application data
 */
public class JSkatApplicationData {

	private JSkatOptions options;
	private Map<String, SkatTable> localSkatTables;
	private Map<String, SkatTable> remoteSkatTables;
	private String activeTable;

	/**
	 * Contructor
	 */
	public JSkatApplicationData(JSkatOptions jskatOptions) {
		
		this.options = jskatOptions;
		this.localSkatTables = new HashMap<String, SkatTable>();
		this.remoteSkatTables = new HashMap<String, SkatTable>();
	}

	/**
	 * Adds a new local skat table
	 * 
	 * @param newSkatTable New local table
	 */
	synchronized public void addLocalSkatTable(SkatTable newSkatTable) {
		
		this.localSkatTables.put(newSkatTable.getName(), newSkatTable);
	}
	
	/**
	 * Returns a reference to a skat table
	 * 
	 * @param tableName Table name
	 * @return Skat table
	 */
	public SkatTable getSkatTable(String tableName) {
		
		return this.localSkatTables.get(tableName);
	}
	
	/**
	 * Adds a new remote skat table
	 *  
	 * @param newRemoteSkatTable New remote table
	 */
	public void addRemoteSkatTables(SkatTable newRemoteSkatTable) {
		// TODO implement it correctly
		this.remoteSkatTables.put("Blubb", newRemoteSkatTable);
	}

	/**
	 * Gets all options for JSkat
	 * 
	 * @return All options for JSkat
	 */
	public JSkatOptions getOptions() {
		
		return this.options;
	}
	
	/**
	 * Gets current options for a new table
	 * 
	 * @return Current options for a new table
	 */
	public SkatTableOptions getTableOptions() {
		
		return this.options.getSkatTableOptions();
	}
	
	/**
	 * Sets the active table
	 * 
	 * @param newActiveTable New active table
	 */
	public void setActiveTable(String newActiveTable) {
		
		this.activeTable = newActiveTable;
	}

	/**
	 * Gets the active table
	 * 
	 * @return Active table
	 */
	public String getActiveTable() {
		
		return this.activeTable;
	}

	public ResourceBundle getResourceBundle() {
		// TODO Auto-generated method stub
		return null;
	}
}
