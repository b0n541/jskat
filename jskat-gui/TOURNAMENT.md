# Developer tournament window

Start JSkat with `--developer-mode`, then select **Extras → Tournament…**.
Ordinary launches create neither the menu entry nor tournament tabs/controllers.
The application entry point routes headless tournament arguments before JavaFX;
see `app/TOURNAMENT.md` for headless usage.

## Requested inputs and validation

New run provides native roster, player implementation, model source, output,
whole-epoch and optional unsigned-seed controls. Generated UUIDs survive edits,
reordering and JSON import/export. The ruleset is fixed to `standard-skat-v1`;
the checked schedule count is derived from roster size and epochs. Random versus
Algorithmic needs no model installation and performs no automatic source discovery.

The displayed configuration-file base is stable. Imports preserve their original
base. Exporting elsewhere writes absolute paths where necessary to preserve
meaning; it does not silently change the editor's base. Unknown/duplicate/invalid
imported fields remain shared reader failures and are never silently discarded.
Only structurally valid imports populate the editor.

Select a model implementation or choose **Discover models…** to inspect Active
and Installed candidates. Discovery checks the full manifest inventory and the
selected implementation's signatures. Invalid candidates remain visible with
reasons and cannot be selected; an invalid source already present in an imported
configuration retains its identity for precise validation. Development sources
have a directory picker. Tournament discovery never installs or activates models.

Validate uses the shared blocking service's read-only preflight in a background
thread. All ordered issues retain severity, code, JSON Pointer and message; fields
show matching issues, and selecting an issue focuses its control. Details include
shared build and resolved implementation/model identities. Validation creates no
output. Source probes prevent starting another tournament until they finish;
new source validation/discovery is disabled while a runner is active. Start repeats
checks under service ownership and freezes the requested inputs during execution.
Workers, verbosity and positive stall-warning seconds are session controls and
can change on explicit Resume without changing configuration identity.

## Retained runs, reports and execution

Open run creates an independent read-only inspection tab. It preserves the
retained configuration's original path context, shows tolerant diagnostics and
captures one coherent report generation. Invalid evidence remains inspectable;
Start and unsafe Resume are disabled. Verify checks compatibility and retained
snapshots through the shared service, without resolving original source paths.
Verified details include the build, effective seed, competitor UUIDs, concrete
classes, fingerprints and snapshot paths. A completed run offers verification;
failed final publication can explicitly Resume even if all games are committed.
Stale ownership recovery requires its own confirmation and shared-service
revalidation of the free OS lock and retained integrity. There are no repair,
overwrite, migration or forced takeover actions.

The native leaderboard preserves the shared report's ordering, numeric ratings,
intervals and reasons uncertainty is unavailable. Partial results are marked
provisional; only complete epochs contribute to ratings. Progress distinguishes
committed games, session admissions, failed attempts, complete epochs and partial
epoch commits, and shows monotonic elapsed time, throughput, advisory ETA and
running attempt/stall details. Inference fallback leaves a persistent warning.
Summary JSON, leaderboard CSV and game-score CSV come from one captured report
generation; audit and run-directory actions expose sessions, failed attempts,
lock history, fallback causes and diagnostics. Report/audit reads and all runner,
probe, verification and publication work execute outside the JavaFX thread.

## Ownership and closing

One application-owned controller retains the active requested configuration and
runner through preparation, admitted-game draining, publication and final cleanup.
Monitors subscribe to immutable observations. `TournamentProgress.Kind` controls
coalescing and urgency, typed service recovery status controls the recovery action,
and the controller retains operation failure separately from its diagnostic text.
Changing readable wording never changes these decisions. Routine observations coalesce;
lifecycle/failure/stall observations are queued promptly and older sequence values
cannot replace newer progress. Disposed/broken listeners cannot affect cleanup.

Closing an active monitor offers **Cancel tournament and close tab** and
**Continue running**. Approval requests cooperative cancellation and immediately
detaches the tab; Continue and Escape retain it. Approval during cancellation or
finalization only detaches monitoring. Closing an unrelated inspection does not
cancel the runner. Extras reopens the same process-owned run, including a Resume
started from an inspection tab, and a second invocation cannot start while the
first owns preparation or cleanup.

Window close and menu/Welcome Exit commands use the same application exit path.
During execution, **Stay** keeps running; **Cancel tournament and wait** consumes
the close request, requests cancellation and exits only after background cleanup
returns. JavaFX never waits for that work. Admitted callbacks are not interrupted
or forcibly terminated. Short-lived background inspection/probe threads also
retain process lifetime while their native resources close.

## Verification

Tournament GUI tests exercise actual JavaFX controls with a real display. They
cover native import/path/UUID behavior, ordered field/list issue parity,
background responsiveness and probe ownership, disabled invalid candidates,
menu gating, cross-tab resume/context/close/reopen behavior, cancellation and
finalization/exit waiting, observer isolation, numeric leaderboard presentation,
and actual shared-service model-free execution/completed verification plus
snapshot verification after the original development source disappears.

For durable integration scenarios, set `JSKAT_TOURNAMENT_EXT4_TEST_ROOT` to a
supported ext4 directory, as for the base service tests. The model snapshot and
implementation-specific discovery scenarios use the installed v1.4.0 ONNX fixture
when present. No pixel golden files are required; the themed native New and
published-report views were rendered and inspected during implementation.
