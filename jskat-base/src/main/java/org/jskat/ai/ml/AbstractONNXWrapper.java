package org.jskat.ai.ml;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Base class for ONNX model wrappers providing common model loading and lifecycle management.
 */
public abstract class AbstractONNXWrapper implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractONNXWrapper.class);

    protected final OrtEnvironment env;
    protected final OrtSession session;

    /**
     * Loads an ONNX model from the given path.
     *
     * @param modelPath Path to the .onnx model file
     * @param modelName Human-readable model name for logging
     * @throws OrtException If model loading fails
     * @throws IOException  If file cannot be read
     */
    protected AbstractONNXWrapper(String modelPath, String modelName) throws OrtException, IOException {
        this.env = OrtEnvironment.getEnvironment();

        Path path = Paths.get(modelPath);
        if (!Files.exists(path)) {
            throw new IOException(String.format(
                    "%s model not found: %s\n" +
                    "  Absolute path tried: %s\n" +
                    "  Current working directory: %s",
                    modelName, modelPath,
                    path.toAbsolutePath(),
                    System.getProperty("user.dir")));
        }

        logger.info("Loading {} from {}", modelName, modelPath);
        this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
        logger.info("{} loaded successfully", modelName);
    }

    @Override
    public void close() {
        if (session != null) {
            try {
                session.close();
            } catch (OrtException e) {
                logger.error("Error closing ONNX session", e);
            }
        }
    }
}
