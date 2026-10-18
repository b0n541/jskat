package org.jskat.ai.ml;

/**
 * Cancellation token for an explicit model installation.
 */
@FunctionalInterface
public interface ModelCancellation {

    /**
     * Returns or indicates cancelled.
     * @return is cancelled result.
     */
    boolean isCancelled();

    /**
     * Performs never.
     * @return never result.
     */
    static ModelCancellation never() {
        return () -> false;
    }
}
