package org.jskat.tournament;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

import org.slf4j.*;

/** Filters only explicitly scoped tournament threads; ordinary games retain their logger policy. */
final class TournamentLogging implements AutoCloseable {
    private static final ThreadLocal<Boolean> VERBOSE = new ThreadLocal<>();

    static {
        if (LoggerFactory.getILoggerFactory() instanceof LoggerContext context) {
            var filter =
                    new TurboFilter() {
                        @Override
                        public FilterReply decide(
                                Marker marker,
                                ch.qos.logback.classic.Logger logger,
                                Level level,
                                String format,
                                Object[] args,
                                Throwable error) {
                            return Boolean.FALSE.equals(VERBOSE.get())
                                            && level.toInt() < Level.WARN_INT
                                    ? FilterReply.DENY
                                    : FilterReply.NEUTRAL;
                        }
                    };
            filter.start();
            context.addTurboFilter(filter);
        }
    }

    private final Boolean previous = VERBOSE.get();

    TournamentLogging(boolean verbose) {
        VERBOSE.set(verbose);
    }

    public void close() {
        if (previous == null) VERBOSE.remove();
        else VERBOSE.set(previous);
    }
}
