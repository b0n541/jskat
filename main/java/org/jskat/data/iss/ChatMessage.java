/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.13.0-SNAPSHOT
 * Copyright (C) 2013-05-09
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
