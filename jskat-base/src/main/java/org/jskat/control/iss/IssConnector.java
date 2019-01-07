/**
 * Copyright (C) 2019 Jan Schäfer (jansch@users.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	 * @param issControl
	 *            ISS controller
	 * @return TRUE if the connection was successful
	 */
	boolean establishConnection(final IssController issControl);

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
