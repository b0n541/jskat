package org.jskat.control.event.iss;

import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.iss.ChatMessage;

/**
 * This event is created when new messages are added to a chat on the ISS.
 */
/**
 * Event raised when a chat message arrives from the Internet Skat Server.
 *
 * @param messageType category of the incoming chat message
 * @param message chat message contents and sender
 */
public record IssNewChatMessageEvent(ChatMessageType messageType, ChatMessage message) {
}
