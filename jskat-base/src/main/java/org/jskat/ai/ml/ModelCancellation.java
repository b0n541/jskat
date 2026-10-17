package org.jskat.ai.ml;

/** Cancellation token for an explicit model installation. */
@FunctionalInterface
public interface ModelCancellation {

    boolean isCancelled();

    static ModelCancellation never() {
        return () -> false;
    }
}
