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
