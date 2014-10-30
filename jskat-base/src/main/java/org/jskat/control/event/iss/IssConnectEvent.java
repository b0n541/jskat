package org.jskat.control.event.iss;

import org.jskat.data.iss.LoginCredentials;

/**
 * This event is created when the user wants to connect to the ISS.
 */
public class IssConnectEvent {

	public final LoginCredentials loginCredentials;

	public IssConnectEvent(final LoginCredentials loginCredentials) {
		this.loginCredentials = loginCredentials;
	}
}
