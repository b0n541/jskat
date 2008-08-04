/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

*/

package jskat.data;

import java.util.Observable;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import jskat.control.JSkatMaster;
import jskat.gui.main.JSkatFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holds all data of JSkat
 * 
 * @author Jan Sch&auml;fer
 */
public class JSkatDataModel extends Observable {

	private Log log = LogFactory.getLog(JSkatDataModel.class);

	/**
	 * Creates a new instance of JSkatDataModel
	 * 
	 * @param mainClass
	 *            A reference to the Main class
	 * @param jskatOptions
	 * 			  Current options
	 */
	public JSkatDataModel(Object mainClass, JSkatOptions jskatOptions) {

		// Main class
		this.mainClass = mainClass;

		this.jskatOptions = jskatOptions;
		
		// set the Locale Variable
		JSkatOptions.Languages lang = jskatOptions.getLanguage();
		if (lang == JSkatOptions.Languages.GERMAN) {
			setLocale(new Locale("de", "DE"));
		}
		else if (lang == JSkatOptions.Languages.ENGLISH) {
			setLocale(new Locale("en", "GB"));
		}
		else {
			setLocale(new Locale("en", "GB"));
		}

		log.debug("Datamodel created.");
	}

	/**
	 * Returns the current ressource bundle
	 * 
	 * @return The current RessourceBundle
	 */
	public ResourceBundle getResourceBundle() {

		return jskatStrings;
	}

	/**
	 * Sets Locale variable
	 * 
	 * @param newLocale New Locale to be set
	 * @return Strings under the new Locale
	 */
	public ResourceBundle setLocale(Locale newLocale) {

		jskatLocale = newLocale;

		jskatStrings = PropertyResourceBundle.getBundle("jskat/i18n/i18n",
				jskatLocale);

		return jskatStrings;
	}

	/**
	 * Gets the Main class
	 * 
	 * @return the Main class
	 */
	public Object getMainClass() {

		return mainClass;
	}

	/**
	 * Set the main window
	 * 
	 * @param mainWindow
	 *            The main window
	 */
	public void setMainWindow(JSkatFrame mainWindow) {

		this.mainWindow = mainWindow;
	}

	/**
	 * Gets a reference to the main window
	 * 
	 * @return Main window
	 */
	public JSkatFrame getMainWindow() {

		return mainWindow;
	}

	/**
	 * @return Returns the jskatOptions.
	 */
	public JSkatOptions getJSkatOptions() {
		return jskatOptions;
	}

	/**
	 * @param jskatOptions
	 *            The jskatOptions to set.
	 */
	public void setJSkatOptions(JSkatOptions jskatOptions) {
		
		this.jskatOptions = jskatOptions;
		
		setChanged();
		notifyObservers(jskatOptions);
	}
	
	/**
	 * Sets the JSkatMaster
	 * 
	 * @param jskatMaster
	 */
	public void setJSkatMaster(JSkatMaster jskatMaster) {
		
		this.jskatMaster = jskatMaster;
	}

	/**
	 * Returns a reference to the JSkatMaster
	 * 
	 * @return jskatMaster
	 */
	public JSkatMaster getJSkatMaster() {
		
		return jskatMaster;
	}
	
	
	/** Holds a reference to the main class */
	private Object mainClass;

	/** Holds a reference to the main window */
	private JSkatFrame mainWindow;

	/** Holds a reference to the current Locale */
	private Locale jskatLocale;

	/** Holds a reference to the current ResourceBundle stings */
	private ResourceBundle jskatStrings;

	/** Holds a reference to the current set of JSkat options */
	private JSkatOptions jskatOptions;
	
	/** Holds a reference to the JSkatMaster */
	private JSkatMaster jskatMaster;
}
