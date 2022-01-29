package org.jskat.control.iss;


/**
 * Interface for connectors to ISS
 */
interface IssConnector {

    /**
     * Sets login credentials
     *
     * @param newLoginName Login name
     * @param newPassword  Password
     */
    void setConnectionData(final String newLoginName, final String newPassword);

    /**
     * Establishes a connection with ISS
     *
     * @param issControl ISS controller
     * @return TRUE if the connection was successful
     */
    boolean establishConnection(final IssController issControl);

    /**
     * Gets the output channel
     *
     * @return Output channel
     */
    OutputChannel getOutputChannel();

    /**
     * Closes the connection to ISS
     */
    void closeConnection();

    /**
     * Checks whether there is an open connection
     *
     * @return TRUE if there is an open connection
     */
    boolean isConnected();
}
