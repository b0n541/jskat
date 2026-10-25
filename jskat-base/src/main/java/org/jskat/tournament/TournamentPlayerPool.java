package org.jskat.tournament;

import org.jskat.ai.ml.AbstractMLPlayer;
import org.jskat.ai.rnd.AIPlayerRND;
import org.jskat.player.JSkatPlayer;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Run-scoped ownership. Each synchronized reservation acquires all three seats atomically. Native
 * construction runs outside the pool monitor: idle + leased + constructing never exceed 3*workers.
 */
public final class TournamentPlayerPool implements AutoCloseable {
    private record Entry(UUID competitor, JSkatPlayer player) {}

    private final Map<UUID, ResolvedTournamentCompetitor> roster = new HashMap<>();
    private final List<Entry> idle = new ArrayList<>();
    private final Set<GameLease> active = new HashSet<>();
    private final TournamentPlayerRegistry registry = new TournamentPlayerRegistry();
    private final int capacity;
    private final Function<ResolvedTournamentCompetitor, JSkatPlayer> create;
    private final Consumer<JSkatPlayer> dispose;
    private ExecutorService constructors;
    private int count, preparing;
    private boolean closed;
    private RuntimeException cleanupFailure;
    private final List<Entry> quarantine = new ArrayList<>();

    public TournamentPlayerPool(List<ResolvedTournamentCompetitor> competitors, int workers) {
        this(competitors, workers, null, null);
    }

    TournamentPlayerPool(List<ResolvedTournamentCompetitor> competitors, int workers,
            Function<ResolvedTournamentCompetitor, JSkatPlayer> create, Consumer<JSkatPlayer> dispose) {
        this.create = create == null ? registry::create : create;
        this.dispose = dispose == null ? registry::close : dispose;
        if (workers < 1) throw new IllegalArgumentException("Workers must be positive");
        capacity = Math.multiplyExact(3, workers);
        for (var c : competitors)
            if (roster.put(c.requested().id(), c) != null)
                throw new IllegalArgumentException("Duplicate competitor");
    }

    public synchronized int instanceCount() {
        return count;
    }

    public GameLease acquire(ScheduledTournamentGame game, CancellationToken cancellation) {
        var acquired = new ArrayList<Entry>(Collections.nCopies(3, null));
        var lease = new GameLease(acquired);
        int reserved = 0;
        synchronized (this) {
            for (var seat : game.seats())
                if (!roster.containsKey(seat.competitorId()))
                    throw new IllegalArgumentException("Unknown competitor: " + seat.competitorId());
            while (true) {
                ensureOpen();
                if (cancellation.isCancellationRequested())
                    throw new TournamentCancellationException("Player acquisition cancelled");
                if (count - idle.size() + 3 <= capacity) break;
                try { wait(50); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new TournamentCancellationException("Player acquisition interrupted");
                }
            }
            try {
                // Protect every reusable seat before choosing any eviction victim.
                for (int i = 0; i < game.seats().size(); i++) {
                    UUID competitor = game.seats().get(i).competitorId();
                    for (var candidate : idle)
                        if (candidate.competitor().equals(competitor)) {
                            acquired.set(i, candidate);
                            idle.remove(candidate);
                            break;
                        }
                }
                for (var entry : acquired) if (entry == null) {
                    if (count == capacity) destroy(idle.removeFirst());
                    count++;
                    reserved++;
                }
                preparing++;
            } catch (RuntimeException | Error failure) {
                count -= reserved;
                discardEntries(acquired, failure);
                notifyAll();
                throw failure;
            }
        }
        int unconstructed = reserved;
        int uncertainConstructions = 0;
        try {
            var missing = new ArrayList<Integer>();
            for (int i = 0; i < acquired.size(); i++) if (acquired.get(i) == null) missing.add(i);
            Throwable constructionFailure = null;
            for (var construction : construct(game, missing)) {
                if (construction.failure() == null) {
                    acquired.set(construction.seat(), construction.entry());
                    unconstructed--;
                } else {
                    if (construction.uncertainCleanup()) uncertainConstructions++;
                    if (constructionFailure == null) constructionFailure = construction.failure();
                    else if (constructionFailure != construction.failure())
                        constructionFailure.addSuppressed(construction.failure());
                }
            }
            if (constructionFailure instanceof RuntimeException failure) throw failure;
            if (constructionFailure instanceof Error failure) throw failure;
            if (Thread.currentThread().isInterrupted())
                throw new TournamentCancellationException("Player construction interrupted");
            for (int i = 0; i < acquired.size(); i++) {
                var entry = acquired.get(i);
                var seat = game.seats().get(i);
                if (entry.player() instanceof AIPlayerRND random) random.reseed(seat.seed());
                if (entry.player() instanceof AbstractMLPlayer ml)
                    ml.setFallbackSink((phase, error) -> lease.record(new TournamentInferenceFallback(
                            seat.competitorId(), game.gameId(), phase, error.toString())));
                entry.player().newGame(seat.seat());
            }
            synchronized (this) {
                ensureOpen();
                if (cancellation.isCancellationRequested())
                    throw new TournamentCancellationException("Player acquisition cancelled");
                active.add(lease);
            }
            return lease;
        } catch (RuntimeException | Error failure) {
            synchronized (this) {
                count -= unconstructed;
                if (uncertainConstructions > 0) {
                    count += uncertainConstructions; // Keep slots for unproven native cleanup.
                    cleanupFailure = new IllegalStateException("Player construction cleanup failed", failure);
                }
                discardEntries(acquired, failure);
            }
            throw failure;
        } finally {
            synchronized (this) { preparing--; notifyAll(); }
        }
    }

    private record Construction(int seat, Entry entry, Throwable failure, boolean uncertainCleanup) {}

    private Construction constructSeat(ScheduledTournamentGame game, int seat) {
        UUID id = game.seats().get(seat).competitorId();
        try { return new Construction(seat, new Entry(id, create.apply(roster.get(id))), null, false); }
        catch (RuntimeException | Error failure) { return new Construction(seat, null, failure, hasSuppressed(failure)); }
    }

    private List<Construction> construct(ScheduledTournamentGame game, List<Integer> missing) {
        if (missing.isEmpty()) return List.of();
        int models = 0;
        for (int seat : missing)
            if (roster.get(game.seats().get(seat).competitorId()).requested().implementation().modelRequired()) models++;
        var result = new ArrayList<Construction>(missing.size());
        if (models < 2) {
            for (int seat : missing) {
                var construction = constructSeat(game, seat);
                result.add(construction);
                if (construction.failure() != null) break;
            }
            return result;
        }
        ExecutorService executor;
        synchronized (this) {
            if (constructors == null) constructors = Executors.newFixedThreadPool(3,
                    r -> new Thread(r, "tournament-player-loader"));
            executor = constructors;
        }
        var futures = new ArrayList<Future<Construction>>(missing.size());
        for (int seat : missing) {
            try { futures.add(executor.submit(() -> constructSeat(game, seat))); }
            catch (RuntimeException | Error failure) {
                result.add(new Construction(seat, null, failure, hasSuppressed(failure)));
                break;
            }
        }
        boolean interrupted = false;
        try {
            // Native construction is not interruptible: join every reserved slot before cleanup.
            for (int i = 0; i < futures.size(); i++) while (true) {
                try { result.add(futures.get(i).get()); break; }
                catch (InterruptedException e) { interrupted = true; }
                catch (ExecutionException e) {
                    result.add(new Construction(missing.get(i), null, e.getCause(), hasSuppressed(e.getCause())));
                    break;
                }
            }
        } finally { if (interrupted) Thread.currentThread().interrupt(); }
        return result;
    }

    private void discardEntries(List<Entry> entries, Throwable failure) {
        for (var entry : entries) if (entry != null)
            try { destroy(entry); }
            catch (RuntimeException cleanup) { failure.addSuppressed(cleanup); }
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
            dispose.accept(entry.player());
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
        while (!active.isEmpty() || preparing > 0)
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
        if (constructors != null) constructors.close();
        if (interrupted) Thread.currentThread().interrupt();
        if (failure != null) throw failure;
    }
}
