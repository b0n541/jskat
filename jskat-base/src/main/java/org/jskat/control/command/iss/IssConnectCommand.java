package org.jskat.control.command.iss;

import org.jskat.data.iss.LoginCredentials;

/**
 * This command is created when the user wants to connect to the ISS.
 */
public class IssConnectCommand {

    /**
     * Credentials used to establish the connection.
     */
    public final LoginCredentials loginCredentials;

    /**
     * Performs iss connect command.
     * @param loginCredentials login credentials value.
     */
    public IssConnectCommand(final LoginCredentials loginCredentials) {
        this.loginCredentials = loginCredentials;
    }
}
