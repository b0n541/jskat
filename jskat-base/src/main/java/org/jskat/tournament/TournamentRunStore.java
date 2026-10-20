package org.jskat.tournament;

import java.io.IOException;
import java.util.*;

/** Exclusive-lock owner. Acknowledged commit means authoritative durable publication. */
public interface TournamentRunStore extends AutoCloseable {
    UUID runId();

    ResolvedTournamentConfiguration configuration();

    List<ScheduledTournamentGame> schedule();

    List<TournamentGameResult> committedGames() throws IOException;

    void commit(TournamentGameResult result) throws IOException;

    void recordFailure(TournamentFailedAttempt failure) throws IOException;

    void close() throws IOException;
}
