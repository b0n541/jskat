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
import de.jskat.data.JSkatOptions;
import de.jskat.gui.JSkatViewImpl;
import de.jskat.gui.LookAndFeelSetter;
import de.jskat.gui.img.JSkatGraphicRepository;

/**
 * Main class for JSkat
 */
public class JSkat {

	private static Log log = LogFactory.getLog(JSkat.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$

		log.debug("Welcome to JSkat!");

		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {

			if ("Nimbus".equals(laf.getName())) {
				
				LookAndFeelSetter.setLookAndFeel();
			}
		}
		
		JSkatOptions jskatOptions = JSkatOptions.instance();

		// Preparing all graphics
		JSkatGraphicRepository jskatBitmaps = new JSkatGraphicRepository(
				jskatOptions);

		JSkatMaster jskat = new JSkatMaster(jskatOptions);

		jskat.setView(new JSkatViewImpl(jskat, jskatBitmaps));
	}
}
