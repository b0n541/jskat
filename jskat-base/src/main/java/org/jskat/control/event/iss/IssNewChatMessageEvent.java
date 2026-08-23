package org.jskat.control.event.iss;

import org.jskat.control.iss.ChatMessageType;
import org.jskat.data.iss.ChatMessage;

/**
 * This event is created when new messages are added to a chat on the ISS.
 */
public record IssNewChatMessageEvent(ChatMessageType messageType, ChatMessage message) {
}
