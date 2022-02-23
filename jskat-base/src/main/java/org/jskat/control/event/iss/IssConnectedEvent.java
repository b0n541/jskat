package org.jskat.control.event.iss;

/**
 * This event is created when the connection to ISS was successful.
 */
public class IssConnectedEvent {

    public final String login;

    public IssConnectedEvent(String login) {
        this.login = login;
    }
}
