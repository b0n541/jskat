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
 * All message types from ISS
 */
public enum MessageType {

	/**
	 * Password message
	 */
	PASSWORD("password:"), //$NON-NLS-1$
	/**
	 * Welcome message
	 */
	WELCOME("Welcome"), //$NON-NLS-1$
	/**
	 * Version message
	 */
	VERSION("Version"), //$NON-NLS-1$
	/**
	 * Clients update message
	 */
	CLIENTS("clients"), //$NON-NLS-1$
	/**
	 * Tables update message
	 */
	TABLES("tables"), //$NON-NLS-1$
	/**
	 * Table create message
	 */
	CREATE("create"), //$NON-NLS-1$
	/**
	 * Table invite message
	 */
	INVITE("invite"), //$NON-NLS-1$
	/**
	 * Table update message
	 */
	TABLE("table"), //$NON-NLS-1$
	/**
	 * Table destroy message
	 */
	DESTROY("destroy"), //$NON-NLS-1$
	/**
	 * Error message
	 */
	ERROR("error"), //$NON-NLS-1$
	/**
	 * Chat message
	 */
	TEXT("text"), //$NON-NLS-1$
	/**
	 * Lobby message
	 */
	YELL("yell"), //$NON-NLS-1$
	/**
	 * UNKNOWN message
	 */
	UNKNOWN(""); //$NON-NLS-1$

	private String messageStart;

	private MessageType(String startToken) {
		messageStart = startToken;
	}

	/**
	 * Gets the message type by a string
	 * 
	 * @param searchToken
	 *            Search string
	 * @return Message type or {@link #UNKNOWN}
	 */
	public static MessageType getByString(String searchToken) {

		for (MessageType type : MessageType.values()) {
			if (type.messageStart.equals(searchToken)) {
				return type;
			}
		}

		return UNKNOWN;
	}
}
