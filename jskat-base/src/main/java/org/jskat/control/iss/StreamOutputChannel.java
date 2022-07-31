package org.jskat.control.iss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

class StreamOutputChannel implements OutputChannel {

    private static final Logger log = LoggerFactory
            .getLogger(StreamOutputChannel.class);

    private final PrintWriter output;

    /**
     * Constructor
     *
     * @param newOutput Input stream from ISS
     */
    StreamOutputChannel(final PrintWriter newOutput) {

        this.output = newOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(final String message) {
        log.debug("ISS <--|    " + message);
        this.output.println(message);
    }
}
