/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer and Markus J. Luzius
 *
 * Version: 0.9.0-SNAPSHOT
 * Build date: 2011-07-26
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
package org.jskat.data.iss;

/**
 * Chat message from ISS
 */
public class ChatMessage {

	private String chatName;
	private String message;

	/**
	 * Constructor
	 * 
	 * @param newChatName
	 *            Chat name
	 * @param messageText
	 *            Message text
	 */
	public ChatMessage(String newChatName, String messageText) {

		this.chatName = newChatName;
		this.message = messageText;
	}

	/**
	 * Gets the name of the chat
	 * 
	 * @return Chat name
	 */
	public String getChatName() {

		return this.chatName;
	}

	/**
	 * Gets the message text
	 * 
	 * @return Message text
	 */
	public String getMessage() {

		return this.message;
	}
}
