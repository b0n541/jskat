/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version 0.11.0-SNAPSHOT
 * Copyright (C) 2012-03-13
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

	private static String VERSION = "0.11.0"; //$NON-NLS-1$

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
