/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import de.jskat.control.JSkatMaster;
import de.jskat.gui.JSkatViewImpl;
import de.jskat.gui.LookAndFeelSetter;

/**
 * Main class for JSkat
 */
public class JSkat {

	private static Log log = LogFactory.getLog(JSkat.class);

	/**
	 * Main method
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$

		log.debug("Welcome to JSkat!"); //$NON-NLS-1$

		trySettingNimbusLookAndFeel();

		// // get the options
		// JSkatOptions jskatOptions = JSkatOptions.instance();
		// // get the i18n strings
		// JSkatResourceBundle jskatStrings = JSkatResourceBundle.instance();
		// // get all graphics
		// JSkatGraphicRepository jskatBitmaps =
		// JSkatGraphicRepository.instance();
		// get master controller
		JSkatMaster jskat = JSkatMaster.instance();

		jskat.setView(new JSkatViewImpl());
	}

	private static void trySettingNimbusLookAndFeel() {
		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {

			if ("Nimbus".equals(laf.getName())) { //$NON-NLS-1$

				LookAndFeelSetter.setLookAndFeel();
			}
		}
	}
}
