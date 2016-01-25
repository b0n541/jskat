/**
 * This file is part of JSkat.
 *
 * JSkat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JSkat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSkat.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jskat;

import org.apache.log4j.PropertyConfigurator;
import org.jskat.data.DesktopSavePathResolver;
import org.jskat.data.JSkatOptions;
import org.junit.BeforeClass;

/**
 * Abstract class for all JSkat unit tests
 * 
 * FIXME: this is a duplicate from jskat-base, find a way to use that one
 * instead of copying.
 */
public abstract class AbstractJSkatTest {

	/**
	 * Creates the logger
	 */
	@BeforeClass
	public static void createLogger() {
		PropertyConfigurator.configure(ClassLoader.getSystemResource("org/jskat/config/log4j.properties")); //$NON-NLS-1$
		JSkatOptions options = JSkatOptions.instance(new DesktopSavePathResolver());
		options.resetToDefault();
	}
}
