package org.jskat.control.iss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reads data from ISS until an interrupt signal occurs
 * <p>
 * Idea is taken from the book Java Threads by Scott Oaks and Henry Wong
 */
class InputChannel {

    private static final Logger log = LoggerFactory.getLogger(InputChannel.class);

    private final MessageHandler messageHandler;

    private final InputStream stream;
    private final BufferedReader reader;
    private volatile boolean done = false; // Use volatile for proper thread visibility
    private final InputReaderThread inputReaderThread; // The single thread for reading input

    /**
     * Constructor
     *
     * @param controller
     * @param is         Input stream
     */
    InputChannel(final IssController controller, final InputStream is) {

        this.stream = is;
        this.reader = new BufferedReader(new InputStreamReader(this.stream));
        this.messageHandler = new MessageHandler(controller);
        this.messageHandler.start();
        this.inputReaderThread = new InputReaderThread();
        this.inputReaderThread.start();
    }

    /**
     * Helper class for reading incoming information from the stream.
     */
    class InputReaderThread extends Thread {

        InputReaderThread() {
            setName("ISS-InputReader"); // Give a name for easier debugging
        }

        @Override
        public void run() {
            String line;
            while (!InputChannel.this.done) { // Check done flag
                try {
                    line = InputChannel.this.reader.readLine();

                    if (line == null) {
                        InputChannel.this.done = true;
                    } else {
                        log.info("ISS    |--> " + line);
                        InputChannel.this.messageHandler.addMessage(line);
                    }

                } catch (IOException ioe) {
                    if (isInterrupted() || InputChannel.this.done) { // Check if interrupted intentionally or done flag is set
                        log.debug("InputReaderThread: InputReaderThread interrupted or done flag set, closing stream.");
                    } else {
                        log.error("InputReaderThread: IO exception in InputReaderThread --> lost connection to ISS", ioe);
                    }
                    InputChannel.this.done = true;
                }
            }
        }
    }

    /**
     * Closes the input channel, signaling its internal threads to stop and waiting for their termination.
     * The underlying stream and reader are expected to be closed by the caller (e.g., StreamConnector).
     */
    public void close() {
        this.done = true;
        if (messageHandler != null) {
            messageHandler.stopHandling();
        }

        if (inputReaderThread != null) {
            inputReaderThread.interrupt();
            try {
                inputReaderThread.join(5000);
                if (inputReaderThread.isAlive()) {
                    log.warn("InputReaderThread thread did not terminate within 5 seconds.");
                } else {
                    log.debug("InputReaderThread thread terminated.");
                }
            } catch (InterruptedException e) {
                log.error("Interrupted while waiting for InputReaderThread to terminate.", e);
                Thread.currentThread().interrupt();
            }
        }

        if (messageHandler != null && Thread.currentThread() != messageHandler) {
            messageHandler.interrupt();
            try {
                messageHandler.join(5000);
                if (messageHandler.isAlive()) {
                    log.warn("MessageHandler thread did not terminate within 5 seconds.");
                } else {
                    log.debug("MessageHandler thread terminated.");
                }
            } catch (InterruptedException e) {
                log.error("Interrupted while waiting for MessageHandler to terminate.", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
