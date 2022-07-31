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
    private PrintWriter output;
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
            this.socket = new Socket(options.getString(Option.ISS_ADDRESS),
                    options.getInteger(Option.ISS_PORT));

            this.output = new PrintWriter(this.socket.getOutputStream(), true);
            this.issOut = new StreamOutputChannel(this.output);
            this.issIn = new InputChannel(issControl, this,
                    this.socket.getInputStream());
            this.issIn.start();
            log.debug("Connection established...");

        } catch (java.net.UnknownHostException e) {
            log.error("Cannot open connection to ISS");
            issControl.showErrorMessage(strings
                    .getString("cant_connect_to_iss"));
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
            log.debug("closing connection");
            this.issIn.interrupt();
            log.debug("input channel closed");
            this.output.close();
            log.debug("output channel closed");
            this.socket.close();
            log.debug("socket closed");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.debug("ISS connector IOException");
            e.printStackTrace();
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
