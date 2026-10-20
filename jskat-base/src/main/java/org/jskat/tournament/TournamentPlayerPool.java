package org.jskat.tournament;

import org.jskat.ai.ml.AbstractMLPlayer;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.player.JSkatPlayer;

import java.util.*;
import java.util.concurrent.CancellationException;

/**
 * Run-scoped ownership. Each synchronized reservation acquires all three seats atomically. Native
 * construction and eviction are serialized: idle + leased + constructing never exceed 3*workers.
 */
public final class TournamentPlayerPool implements AutoCloseable {
    private record Entry(UUID competitor, JSkatPlayer player) {}

    private final Map<UUID, ResolvedTournamentCompetitor> roster = new HashMap<>();
    private final List<Entry> idle = new ArrayList<>();
    private final Set<GameLease> active = new HashSet<>();
    private final TournamentPlayerRegistry registry = new TournamentPlayerRegistry();
    private final int capacity;
    private int count;
    private boolean closed;
    private RuntimeException cleanupFailure;
    private final List<Entry> quarantine = new ArrayList<>();

    public TournamentPlayerPool(List<ResolvedTournamentCompetitor> competitors, int workers) {
        if (workers < 1) throw new IllegalArgumentException("Workers must be positive");
        capacity = Math.multiplyExact(3, workers);
        for (var c : competitors)
            if (roster.put(c.requested().id(), c) != null)
                throw new IllegalArgumentException("Duplicate competitor");
    }

    public synchronized int instanceCount() {
        return count;
    }

    public synchronized GameLease acquire(
            ScheduledTournamentGame game, CancellationToken cancellation) {
        for (var seat : game.seats())
            if (!roster.containsKey(seat.competitorId()))
                throw new IllegalArgumentException("Unknown competitor: " + seat.competitorId());
        while (true) {
            ensureOpen();
            if (cancellation.isCancellationRequested())
                throw new CancellationException("Player acquisition cancelled");
            // Three slots must fit independently of reuse. Other games retain exactly three.
            if (count - idle.size() + 3 <= capacity) break;
            try {
                wait(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CancellationException("Player acquisition interrupted");
            }
        }
        var acquired = new ArrayList<Entry>();
        var lease = new GameLease(acquired);
        try {
            for (var seat : game.seats()) {
                Entry entry = null;
                for (var candidate : idle)
                    if (candidate.competitor().equals(seat.competitorId())) {
                        entry = candidate;
                        break;
                    }
                if (entry != null) idle.remove(entry);
                else {
                    if (count == capacity) {
                        Entry evicted = idle.removeFirst();
                        destroy(evicted);
                    }
                    // Claim capacity before native construction begins, including failure cleanup.
                    count++;
                    try {
                        entry =
                                new Entry(
                                        seat.competitorId(),
                                        registry.create(roster.get(seat.competitorId())));
                    } catch (RuntimeException e) {
                        if (hasSuppressed(e)) cleanupFailure = e;
                        else count--;
                        throw e;
                    }
                }
                acquired.add(entry);
                if (entry.player() instanceof AIPlayerRND random) random.reseed(seat.seed());
                if (entry.player() instanceof AbstractMLPlayer ml)
                    ml.setFallbackSink(
                            (phase, error) ->
                                    lease.record(
                                            new TournamentInferenceFallback(
                                                    seat.competitorId(),
                                                    game.gameId(),
                                                    phase,
                                                    error.toString())));
                entry.player().newGame(seat.seat());
            }
            active.add(lease);
            return lease;
        } catch (RuntimeException e) {
            for (var entry : acquired)
                try {
                    destroy(entry);
                } catch (RuntimeException cleanup) {
                    e.addSuppressed(cleanup);
                }
            notifyAll();
            throw e;
        }
    }

    private static boolean hasSuppressed(Throwable failure) {
        return failure != null
                && (failure.getSuppressed().length > 0 || hasSuppressed(failure.getCause()));
    }

    private void ensureOpen() {
        if (cleanupFailure != null)
            throw new IllegalStateException(
                    "Player cleanup failed; pool cannot admit games", cleanupFailure);
        if (closed) throw new IllegalStateException("Player pool closed");
    }

    private void destroy(Entry entry) {
        try {
            registry.close(entry.player());
            count--;
        } catch (RuntimeException e) {
            cleanupFailure = e;
            if (!quarantine.contains(entry)) quarantine.add(entry);
            throw e;
        }
    }

    public final class GameLease implements AutoCloseable {
        private final List<Entry> entries;
        private final List<TournamentInferenceFallback> diagnostics = new ArrayList<>();
        private boolean returned;

        private GameLease(List<Entry> entries) {
            this.entries = entries;
        }

        public List<JSkatPlayer> players() {
            return entries.stream().map(Entry::player).toList();
        }

        private synchronized void record(TournamentInferenceFallback fallback) {
            diagnostics.add(fallback);
        }

        public synchronized List<TournamentInferenceFallback> fallbacks() {
            return List.copyOf(diagnostics);
        }

        /** Failed attempts discard all seats, even those whose callback did not fail. */
        public void discard() {
            release(false);
        }

        @Override
        public void close() {
            release(true);
        }

        private void release(boolean healthy) {
            synchronized (TournamentPlayerPool.this) {
                if (returned) return;
                returned = true;
                active.remove(this);
                RuntimeException failure = null;
                for (var entry : entries) {
                    if (entry.player() instanceof AbstractMLPlayer ml)
                        ml.setFallbackSink((phase, error) -> {});
                    if (healthy && !closed && cleanupFailure == null) idle.add(entry);
                    else
                        try {
                            destroy(entry);
                        } catch (RuntimeException e) {
                            if (failure == null) failure = e;
                            else failure.addSuppressed(e);
                        }
                }
                TournamentPlayerPool.this.notifyAll();
                if (failure != null) throw failure;
            }
        }
    }

    /**
     * Called after the runner drains admitted games; waits safely if a caller is still releasing.
     */
    @Override
    public synchronized void close() {
        closed = true;
        notifyAll();
        boolean interrupted = false;
        while (!active.isEmpty())
            try {
                wait();
            } catch (InterruptedException e) {
                interrupted = true;
            }
        RuntimeException failure = cleanupFailure;
        var remaining = new ArrayList<>(idle);
        remaining.addAll(quarantine);
        quarantine.clear();
        for (var entry : remaining)
            try {
                destroy(entry);
            } catch (RuntimeException e) {
                if (failure == null) failure = e;
                else if (failure != e) failure.addSuppressed(e);
            }
        idle.clear();
        if (interrupted) Thread.currentThread().interrupt();
        if (failure != null) throw failure;
    }
}
