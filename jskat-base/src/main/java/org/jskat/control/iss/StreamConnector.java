package org.jskat.control.iss;

import org.jskat.data.JSkatOptions;
import org.jskat.data.JSkatOptions.Option;
import org.jskat.util.JSkatResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * StreamConnector to International Skat Server ISS
 */
class StreamConnector extends AbstractIssConnector {

    private static final Logger log = LoggerFactory.getLogger(StreamConnector.class);

    private static final JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;
    private static final JSkatOptions options = JSkatOptions.instance();

    private Socket socket;
    private PrintWriter writer;
    private InputChannel issIn;
    private StreamOutputChannel issOut;

    /**
     * Establishes a connection with ISS
     *
     * @return TRUE if the connection was successful
     */
    @Override
    public boolean establishConnection(final IssController issControl) {

        log.debug("StreamConnector.establishConnection()");

        try {
            socket = new Socket(options.getString(Option.ISS_ADDRESS), options.getInteger(Option.ISS_PORT));
            writer = new PrintWriter(socket.getOutputStream(), true);
            issOut = new StreamOutputChannel(writer);
            issIn = new InputChannel(issControl, socket.getInputStream());

            log.debug("Connection established...");

        } catch (java.net.UnknownHostException e) {
            log.error("Cannot open connection to ISS");
            issControl.showErrorMessage(strings.getString("cantConnectToIss"));
            return false;
        } catch (java.io.IOException e) {
            log.error("IOException: " + e);
            return false;
        }

        return true;
    }

    @Override
    public OutputChannel getOutputChannel() {
        return this.issOut;
    }

    /**
     * Closes the connection to ISS
     */
    @Override
    public void closeConnection() {

        try {
            // Close the socket first to unblock any blocking read operations on its input stream
            if (socket != null) {
                socket.close();
                log.debug("socket closed");
            }
            // Now signal the input channel to stop and clean up its threads
            if (this.issIn != null) {
                this.issIn.close();
                log.debug("input channel closed");
            }
        } catch (IOException e) {
            log.error("Error while closing ISS connection", e);
        }
    }

    /**
     * Checks whether there is an open connection
     *
     * @return TRUE if there is an open connection
     */
    @Override
    public boolean isConnected() {
        return this.socket != null && !this.socket.isClosed();
    }
}
