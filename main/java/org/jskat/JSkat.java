package org.jskat;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.jskat.control.JSkatMaster;
import org.jskat.data.JSkatOptions;
import org.jskat.gui.JSkatViewImpl;
import org.jskat.gui.LookAndFeelSetter;


/**
 * Main class for JSkat
 */
public class JSkat {

	private static Log log = LogFactory.getLog(JSkat.class);

	private static String VERSION = "0.10.0"; //$NON-NLS-1$

	/**
	 * Main method
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("org/jskat/config/log4j.properties")); //$NON-NLS-1$
		log.debug("Welcome to JSkat!"); //$NON-NLS-1$
		trySettingNimbusLookAndFeel();
		JSkatMaster jskat = JSkatMaster.instance();
		jskat.setView(new JSkatViewImpl());
		
		if(JSkatOptions.instance().isShowTipsAtStartUp()) {
			jskat.showWelcomeDialog();
		}

		if (JSkatOptions.instance().isCheckForNewVersionAtStartUp()) {
			jskat.checkJSkatVersion();
		}
	}

	/**
	 * Gets the version of JSkat
	 * 
	 * @return Version of JSkat
	 */
	public static String getVersion() {
		return VERSION;
	}

	private static void trySettingNimbusLookAndFeel() {
		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {

			if ("Nimbus".equals(laf.getName())) { //$NON-NLS-1$

				LookAndFeelSetter.setLookAndFeel();
			}
		}
	}
}
