/*

@ShortLicense@

Authors: @JS@
         @MJL@

Released: @ReleaseDate@

 */

package de.jskat;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;

/**
 * Abstract class for all JSkat unit tests
 */
public class AbstractJSkatTest {

	/**
	 * Creates the logger
	 */
	@BeforeClass
	public static void createLogger() {

		PropertyConfigurator.configure(ClassLoader.getSystemResource("de/jskat/config/log4j.properties")); //$NON-NLS-1$
	}
}
