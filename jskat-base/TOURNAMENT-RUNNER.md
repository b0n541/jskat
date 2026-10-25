# Blocking tournament service

`TournamentService` is the shared base-module boundary for CLI and JavaFX.
`read` parses shared JSON; `validateNew` performs read-only output, build and
whole-roster source/probe preflight. `preflightNew` exposes those same ordered
issues plus the packaged build identity and each successfully probed competitor's
UUID, concrete implementation class, release label and fingerprint. Model-free
entries have null label/fingerprint. These are current source observations;
Start repeats preflight and creates retained snapshots under run ownership.
Preflight never creates output, snapshots, owner/session records or report files.

`open` tolerantly inspects retained evidence and one coherent immutable report
generation without requiring the writer lock. `verify` acquires the existing
lock for strict read-only integrity/build verification. Neither starts players.
`run` blocks through preparation, execution, report publication and full cleanup.
Its result distinguishes configuration rejection from COMPLETED, CANCELLED and
FAILED. `RunResult.recovery()` is `EXPLICIT_AUTHORIZATION_REQUIRED` only after
unreleased owner metadata blocks resume with a free OS lock; other failures use
`NOT_REQUIRED`. The store exposes that refusal as
`TournamentRecoveryRequiredException`, an `IOException` subtype. Tolerant inspection
messages never grant recovery authority. An existing output path is not silently reused. RESUME validates retained
snapshots without consulting original model sources; a certified completed run
returns read-only, and publication-only recovery creates no pool or game workers.

One static process gate is acquired before preparation and released after every
owned resource has been closed. Independent service instances share that gate.
Observers receive serialized immutable snapshots on the calling coordinator;
they must marshal promptly to JavaFX or another presentation thread. Observer
exceptions are isolated. Terminal observation and return follow gate release.

## Execution ownership and admission

The coordinator retains at most `workers` outstanding tasks, with a bounded
executor queue. Pending games are submitted continuously across epoch boundaries.
Workers acquire
all three leases in canonical admission order, check stop/cancellation at the
final admission boundary and immediately invoke the shared game core. Pool
capacity counts idle, leased and constructing instances against `3 * workers`.
Worker failure closes admission before releasing resources, so a newly freed
slot cannot admit another game while its failure awaits the coordinator.

Already admitted games drain cooperatively; cancellation never interrupts a
callback or forces termination. A pre-freeze failure is retained without scores
and discards all seats. A legal frozen result survives a later lease-return
failure and is committed before FAILED termination. There is no automatic retry.
Explicit Resume reuses pending game inputs with new attempt/session UUIDs.

Only acknowledged durable commits increase visible game counts. Uncertain
publication remains FAILED; resume verifies any final record before deciding
whether the game is pending. Complete epoch counts come from captured retained
sets and incremental pending-epoch counters, including out-of-order completion and
partial-epoch resume. Epoch completion requests background reporting without an
admission barrier. One owned report task calculates and publishes an immutable
snapshot; further requests coalesce into one pending update. The calculator caches
schedule indexes and immutable per-game contributions for that session, while all
statistical fits retain their original algorithms and sample counts. Generation
files are staged outside the game-commit monitor, with authentication and the
monotonic current-pointer swap protected under it. The coordinator continues
cancellation/progress/stall observation and handles report failures.

## Progress and termination

`TournamentProgress.kind()` classifies observations as `ROUTINE`, `NOTICE`,
`STALL_WARNING`, or `FAILURE`. Adapters use these typed categories and lifecycle
state for coalescing/urgency; readable message wording carries no control semantics.
Compatibility constructors default to `ROUTINE`. `TournamentProgress` includes
`TournamentMetrics`: monotonic elapsed/attempt
times, sequence, session throughput, optional ETA, running attempts, latest
coherent summary and generation. Admissions/failures are session counts;
committed games and complete epochs include retained history. Throughput uses
new acknowledged commits only. Routine notifications are limited to one per
second; lifecycle, cause-bearing failures, publication and stall warnings are
immediate. Each attempt warns once at its positive configured threshold without
interrupting gameplay. Permanent fallback evidence includes failed attempts and
survives explicit resume. Full events stay in durable game records.

Quiet operational logging applies only to explicitly scoped tournament threads;
ordinary interactive logger policy is preserved. Verbose mode changes logging,
not inputs or gameplay. Session history records workers, verbosity, stall warning,
action, recovery authorization and build.

Finalization stops admission, drains workers, closes players and their bounded
loader, drains background reporting, publishes a current-set termination report, records session end, closes report workers and releases store/OS-lock
ownership. Authoritative execution, storage, publication or cleanup failure wins
over completed games; all acknowledged scheduled games win over late
cancellation. Best-effort diagnostic publication errors cannot skip remaining
cleanup. Failure to acknowledge lock release returns FAILED; diagnostics are
written only while the lock is still provably owned. Storage cannot guarantee a
new durable diagnostic after ownership is already lost.

Runner acceptance tests use the real durable store under the explicitly selected
ext4 root `JSKAT_TOURNAMENT_EXT4_TEST_ROOT`. They cover worker-count/resume outcome
identity, cancellation/draining, preparation/finalization gate ownership,
pre-/post-freeze faults, uncertain commit acknowledgment, report barriers,
observer isolation, monotonic stalls, publication-only recovery and permanent
failed-attempt fallback history. Model fixture tests also exercise read-only
preflight identities against actual installed ONNX releases when available.
