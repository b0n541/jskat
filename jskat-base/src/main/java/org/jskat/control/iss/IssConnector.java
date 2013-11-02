/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius, Daniel Loreck and Sascha Laurien
 *
 * Version 0.14.0-SNAPSHOT
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

/**
 * Interface for connectors to ISS
 */
interface IssConnector {

	/**
	 * Sets login credentials
	 * 
	 * @param newLoginName
	 *            Login name
	 * @param newPassword
	 *            Password
	 */
	void setConnectionData(final String newLoginName, final String newPassword);

	/**
	 * Establishes a connection with ISS
	 * 
	 * @return TRUE if the connection was successful
	 */
	boolean establishConnection(IssController issControl);

	/**
	 * Gets the output channel
	 * 
	 * @return Output channel
	 */
	OutputChannel getOutputChannel();

	/**
	 * Closes the connection to ISS
	 */
	void closeConnection();

	/**
	 * Checks whether there is an open connection
	 * 
	 * @return TRUE if there is an open connection
	 */
	boolean isConnected();
}
