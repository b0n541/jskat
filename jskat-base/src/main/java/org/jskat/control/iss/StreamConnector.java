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

    private static Logger log = LoggerFactory.getLogger(StreamConnector.class);

    private static JSkatResourceBundle strings = JSkatResourceBundle.INSTANCE;
    private static JSkatOptions options = JSkatOptions.instance();

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

        log.debug("StreamConnector.establishConnection()"); //$NON-NLS-1$

        try {
            this.socket = new Socket(options.getString(Option.ISS_ADDRESS),
                    options.getInteger(Option.ISS_PORT));

            this.output = new PrintWriter(this.socket.getOutputStream(), true);
            this.issOut = new StreamOutputChannel(this.output);
            this.issIn = new InputChannel(issControl, this,
                    this.socket.getInputStream());
            this.issIn.start();
            log.debug("Connection established..."); //$NON-NLS-1$

        } catch (java.net.UnknownHostException e) {
            log.error("Cannot open connection to ISS"); //$NON-NLS-1$
            issControl.showErrorMessage(strings
                    .getString("cant_connect_to_iss")); //$NON-NLS-1$
            return false;
        } catch (java.io.IOException e) {
            log.error("IOException: " + e.toString()); //$NON-NLS-1$
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
            log.debug("closing connection"); //$NON-NLS-1$
            this.issIn.interrupt();
            log.debug("input channel closed"); //$NON-NLS-1$
            this.output.close();
            log.debug("output channel closed"); //$NON-NLS-1$
            this.socket.close();
            log.debug("socket closed"); //$NON-NLS-1$
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.debug("ISS connector IOException"); //$NON-NLS-1$
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
