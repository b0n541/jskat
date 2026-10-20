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

    /**
     * ONNX runtime environment shared by the loaded session.
     */
    protected final OrtEnvironment env;
    /**
     * Loaded model inference session.
     */
    protected final OrtSession session;
    private final ONNXConstructionPolicy policy;
    private boolean closed;

    /**
     * Loads an ONNX model from the given path.
     *
     * @param modelPath Path to the .onnx model file
     * @param modelName Human-readable model name for logging
     * @throws OrtException If model loading fails
     * @throws IOException  If file cannot be read
     */
    protected AbstractONNXWrapper(String modelPath, String modelName) throws OrtException, IOException {
        this(modelPath, modelName, ONNXConstructionPolicy.INTERACTIVE);
    }

    protected AbstractONNXWrapper(String modelPath, String modelName, ONNXConstructionPolicy policy) throws OrtException, IOException {
        this.policy = policy;
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
        OrtSession created = null;
        try (var options = new OrtSession.SessionOptions()) {
            if (policy == ONNXConstructionPolicy.TOURNAMENT) {
                options.setIntraOpNumThreads(1);
                options.setInterOpNumThreads(1);
            }
            created = env.createSession(modelPath, options);
        } catch (OrtException | RuntimeException failure) {
            if (created != null) try { created.close(); } catch (OrtException cleanup) { failure.addSuppressed(cleanup); }
            throw failure;
        }
        this.session = created;
        logger.info("{} loaded successfully", modelName);
    }

    @Override
    public synchronized void close() {
        if (closed) return;
        if (session != null) {
            try {
                session.close();
                closed = true;
            } catch (OrtException e) {
                if (policy == ONNXConstructionPolicy.TOURNAMENT) throw new IllegalStateException("Error closing ONNX session", e);
                logger.error("Error closing ONNX session", e);
            }
        }
    }
}
