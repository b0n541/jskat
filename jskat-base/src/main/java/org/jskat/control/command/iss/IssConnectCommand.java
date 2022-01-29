package org.jskat.control.command.iss;

import org.jskat.data.iss.LoginCredentials;

/**
 * This command is created when the user wants to connect to the ISS.
 */
public class IssConnectCommand {

    public final LoginCredentials loginCredentials;

    public IssConnectCommand(final LoginCredentials loginCredentials) {
        this.loginCredentials = loginCredentials;
    }
}
