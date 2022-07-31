package org.jskat.control.iss;

/**
 * All message types from ISS
 */
public enum MessageType {

    /**
     * Password message
     */
    PASSWORD("password:"),
    /**
     * Welcome message
     */
    WELCOME("Welcome"),
    /**
     * Version message
     */
    VERSION("Version"),
    /**
     * Clients update message
     */
    CLIENTS("clients"),
    /**
     * Tables update message
     */
    TABLES("tables"),
    /**
     * Table create message
     */
    CREATE("create"),
    /**
     * Table invite message
     */
    INVITE("invite"),
    /**
     * Table update message
     */
    TABLE("table"),
    /**
     * Table destroy message
     */
    DESTROY("destroy"),
    /**
     * Error message
     */
    ERROR("error"),
    /**
     * Chat message
     */
    TEXT("text"),
    /**
     * Lobby message
     */
    YELL("yell"),
    /**
     * UNKNOWN message
     */
    UNKNOWN("");

    private final String messageStart;

    MessageType(String startToken) {
        messageStart = startToken;
    }

    /**
     * Gets the message type by a string
     *
     * @param searchToken Search string
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
