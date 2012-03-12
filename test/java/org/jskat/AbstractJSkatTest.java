package org.jskat;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;

/**
 * Abstract class for all JSkat unit tests
 */
public abstract class AbstractJSkatTest {

	/**
	 * Creates the logger
	 */
	@BeforeClass
	public static void createLogger() {

		PropertyConfigurator.configure(ClassLoader
				.getSystemResource("org/jskat/config/log4j.properties")); //$NON-NLS-1$
	}
}
