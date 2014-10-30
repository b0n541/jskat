package org.jskat.control.event.iss;

/**
 * This event is created when the connection to ISS was successful.
 */
public class IssConnectSuccessEvent {

	public final String login;
	
	public IssConnectSuccessEvent(String login) {
		this.login = login;
	}
}
