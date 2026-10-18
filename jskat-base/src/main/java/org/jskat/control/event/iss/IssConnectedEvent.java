package org.jskat.control.event.iss;

/**
 * This event is created when the connection to ISS was successful.
 */
/**
 * Event raised after connecting to the Internet Skat Server.
 *
 * @param userName name of the connected user
 */
public record IssConnectedEvent(String userName) {
}
