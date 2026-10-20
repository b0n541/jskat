package org.jskat.tournament;

@FunctionalInterface
public interface CancellationToken {
    boolean isCancellationRequested();

    CancellationToken NONE = () -> false;
}
