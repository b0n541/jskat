# Tournament inputs and player ownership

`TournamentInputResolver.validate(requested, cancellation)` is full read-only source
preflight: source manifests, complete nine-file inventory, sizes and SHA-256,
implementation-specific tensor signatures, effective-input duplicates and a
construction/close probe per otherwise-valid competitor. Entry points use this
before creating output. Independent roster issues retain configuration array JSON
Pointers and sort through `ValidationResult`. Model-free validation never reads
or creates the model store.

`DefaultTournamentInputResolver` reads Active as a direct-child installed alias;
Installed and Development labels have no compatibility allowlist. Installed
`1.4.0` resolves an exact child first and otherwise the conventional `v1.4.0`
release directory, preserving the configured provenance. `discover()`
returns invalid candidates with reasons; Active and its installed directory are
one discovery candidate. Signatures accept FLOAT/INT64/BOOL tensor types, exact
names, ranks and nonbatch dimensions. Batch is dynamic or one; scalar win
probabilities accept `[N]` or `[N,1]` as the inference wrappers support both.

`prepare` copies manifests and all nine artifacts into a private staging
directory, rehashes the copy, and atomically publishes a fingerprint-named
snapshot. No Active marker, installed release or source is mutated. Duplicate
snapshots share immutable file content, never player objects. If a roster fails,
newly published snapshots are removed and no resolved configuration is returned.
Snapshot locations are absolute paths under the caller's snapshot directory.
`verify(retained, cancellation)` uses only those locations and retained
inventories/fingerprints; it checks signatures but never constructs a player or
consults any original source. Completed-run verification therefore needs no
live player instances.

`TournamentPlayerPool(roster, workers).acquire(game, cancellation)` reserves all
three seats together. The returned `GameLease.players()` is ordered Forehand,
Middlehand, Rearhand; each instance has exclusive mutable knowledge. The pool
sets each retained seed and diagnostic sink, then calls `newGame(seat)` exactly
once. The headless core must accept those already reset players. Random resets
its owned RNG; Algorithmic clears its evaluator and strategy. Player knowledge
uses enum-ordered card sets so seeded card indices and deterministic ties do
not depend on identity hash ordering across JVMs.

Capacity is `3 * workers`, counting idle, leased and construction reservations.
Reservations, creation and idle eviction are synchronized. An idle instance
closes before its slot is reused. Cleanup uncertainty poisons admission without
freeing its capacity; terminal cleanup retries quarantined instances and reports
the failure. `GameLease.close()` returns healthy players;
`GameLease.discard()` closes all three after any failed attempt. Runner shutdown
drains leases before `pool.close()` (which waits if leases remain outstanding).

Tournament ML construction explicitly selects `ONNXConstructionPolicy.TOURNAMENT`:
one intra-operation and one inter-operation thread per session, three sessions
for ML and four for ML-Pro. Each player owns all its sessions. SessionOptions
close after creation; singleton OrtEnvironment is never player-owned. Every
partial constructor failure closes preceding sessions, and close attempts all
sessions even after a failure. Ordinary constructors retain default threading
and ML-Pro's optional-model behavior.

A lease binds each effective inference fallback to competitor UUID, stable game
ID, actual game phase enum name and error. `GameLease.fallbacks()` returns an
immutable copy. Sinks are replaced before reuse and cleared on release. The
existing fallback randomness remains unseeded. Comparison-only dense logging in
ML-Pro is excluded because it does not change a decision. The game core Supplier overload observes `lease::fallbacks` only after all
callbacks; the List overload remains available for retained audit replay. The runner includes
these records in the result and retains permanent non-reproducibility status.

Tests cover model-free/read-only preflight, roster aggregation, real installed
ML/ML-Pro aliases/signatures and snapshot/source independence, tampering,
canonical RNG card choice, three independent seats, overlapping lease capacity
and cancellation, constructor partial failure, continued cleanup after a close
failure, and inference attribution after reuse. Native calls are mocked only at
the external ONNX API boundary for failure injection. Actual installed release
integration tests skip explicitly when the local fixture is absent; no download
is performed.

The integration fixture plays 24 retained shuffled deals with actual registered
Random and Algorithmic leases, replays every audit through the verifier, and
repeats each deal after pool reuse with identical events/outcomes/scores. This
identified two legacy Algorithmic issues: unknown contract type during standard
DISCARDING incorrectly entered Ramsch/global options, and some strategy proposals
violated follow-suit. Standard discard now uses its declarer strategy and the
player's existing legal-card fallback also handles invalid proposals. The core
still rejects every illegal player output; it does not repair player actions.
