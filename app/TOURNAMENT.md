# Headless tournament command

Run the installed `JSkat` launcher without a display or installed models:

```
JSkat --tournament config.json [--resume] [--recover-stale-lock]
      [--workers 1] [--verbose] [--stall-warning-seconds 60]
JSkat --tournament-help
```

The configuration is the shared schema in `jskat-base/TOURNAMENT.md`.
For example, Random versus Algorithmic requires no model installation:

```json
{"schemaVersion":1,"ruleset":"standard-skat-v1","epochs":1,"seed":"42",
 "output":"runs/example","competitors":[
 {"id":"550e8400-e29b-41d4-a716-446655440000","name":"Random","implementation":"random"},
 {"id":"ca207ce9-3ca4-4ce1-8443-81e081b8344e","name":"Algorithmic","implementation":"algorithmic"}]}
```

Relative configuration paths use the working directory. Relative output and
Development model paths use the configuration file directory. No environment,
shell or tilde expansion is performed by the command. Tournament dispatch occurs
before JavaFX and normal model startup. Ordinary GUI options, including
`--developer-mode`, continue to work on ordinary GUI launches.

Options may occur in any order. Each option may occur once. Values must be
separate arguments; `--name=value`, unknown options, positional arguments,
GUI options mixed into tournament commands and missing values are rejected.
`--tournament-help` must be the only argument. All other controls require
`--tournament <path>`.

Workers defaults to 1 and accepts ASCII decimal integers from 1 through
715827882 (the representable three-seat capacity bound). Verbose defaults to off.
Stall warnings default to 60 seconds and accept positive ASCII decimal whole
seconds through 9223372036854775807. They warn without interrupting games.
These session options may change on resume and are recorded in session history;
they do not change tournament identity, seeds, outcomes or retained audit data.

New runs require fresh output. Resume is always explicit and verifies retained
inputs rather than consulting original model sources. Successful completed runs
are verified read-only with no players. `--recover-stale-lock` requires
`--resume`; it authorizes audited stale owner metadata recovery only when the OS
lock is free. It cannot break a live lock.

Exit statuses are 0 for COMPLETED (including completed resume), 2 for syntax or
configuration rejection, 1 for execution/storage/integrity/publication/cleanup
failure, and 130 for a returned CANCELLED result. Failures and cancellation keep
valid committed games for explicit resume.

On the supported Linux JVM, SIGINT exits 130 and SIGTERM exits 143, as selected
by the JVM signal handler. A standard shutdown hook requests cooperative
cancellation and waits for all admitted games, publication and resource cleanup.
The command prints the actual final service disposition even when the JVM signal
exit status differs. A late signal can still print COMPLETED; authoritative
failure still prints FAILED. No hook calls System.exit. Uncatchable process
termination requires normal retained-run recovery on the next invocation.

Stdout contains plain progress, latest coherent ratings, final status, summary,
report generation and normalized output directory. Stderr contains the complete
ordered shared validation issues (severity, code, JSON Pointer and message),
usage errors and diagnostics. Routine progress is coalesced to at most once per
second, including rapid epoch publications. Lifecycle changes, failures and
stall warnings are immediate. Progress counts only durable commits; ETA is
advisory. Games run continuously across epoch boundaries; a dedicated reporting
thread calculates and publishes immutable snapshots when epochs complete. Pending
updates coalesce while reporting is busy, so displayed ratings can lag committed
progress. Finalization waits for a report covering every committed game. Missing evidence or intervals
are shown as unavailable. Fallback leaves a reproducibility warning. Verbose
adds attempt identities and operational logs; full events remain in retained
game records in both modes.
