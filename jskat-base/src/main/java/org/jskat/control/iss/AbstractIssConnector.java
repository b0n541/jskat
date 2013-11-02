/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.13.0
 * Copyright (C) 2013-11-02
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
package org.jskat.control.iss;

import org.jskat.data.JSkatOptions;
import org.jskat.util.JSkatResourceBundle;

/**
 * Abstract implementation of {@link IssConnector}
 */
abstract class AbstractIssConnector implements IssConnector {

	protected static final JSkatResourceBundle strings = JSkatResourceBundle
			.instance();
	protected static final JSkatOptions options = JSkatOptions.instance();

	protected String loginName;
	protected String password;

	/**
	 * Sets login credentials
	 * 
	 * @param newLoginName
	 *            Login name
	 * @param newPassword
	 *            Password
	 */
	@Override
	public void setConnectionData(final String newLoginName,
			final String newPassword) {
		loginName = newLoginName;
		password = newPassword;
	}

}
