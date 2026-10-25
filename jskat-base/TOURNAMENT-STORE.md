# Tournament scheduling and retained-run format 1

The public boundaries are `TournamentSchedules`, `TournamentRecords` and
`DurableTournamentRunStore`. `create` returns an executable store only after
source preflight, locked preparation, snapshot verification, complete input
revalidation and the durable ready marker. `resume` requires the exact requested
configuration and build and verifies snapshots without opening original sources.
`inspect(Path)` is a tolerant, read-only diagnostic observation; it grants no
execution authority. `verify` takes a requested configuration or an output path,
acquires the existing OS lock and performs strict read-only verification. It
writes no session, owner, recovery, report or player state.

## Canonical scheduling

Roster UUIDs sort by lowercase UUID **string**. Epochs start at zero. With two
competitors A < B, the fixed assignment order is AAB, ABA, BAA, ABB, BAB, BBA.
With larger rosters, unordered triples iterate lexically and each triple's six
seat permutations iterate lexically. Seats are FOREHAND, MIDDLEHAND, REARHAND.

The block identity is the SHA-256 of canonical
`{encoding:"jskat-schedule-v1",epoch:<integer>,group:[<UUID strings>]}`.
The game identity adds the zero-based `assignment` integer to that tuple. Neither
identity includes a retry, worker, session, attempt or seed. Player seeds use
`deriveSeed(root,"player-instance",gameId,"seat:"+seatIndex)`. Deck seeds use
`deriveSeed(root,"deck","epoch:"+epoch)`. Every game in an epoch retains the
same 32 cards in dealing order.

The deck RNG is `splitmix64-fisher-yates-v1`: the initial card order is Java
`Card.values()` (clubs, spades, hearts, diamonds; A,T,K,Q,J,9,8,7). SplitMix64
increments by `0x9e3779b97f4a7c15`, mixes with
`0xbf58476d1ce4e5b9` and `0x94d049bb133111eb`, and uses unsigned shifts
30,27,31. Fisher–Yates iterates i=31 down to 1, drawing modulo i+1 from the
nonnegative `nextLong() >>> 1`, rejecting signed overflow of
`bits - remainder + bound - 1`. Two-/three-competitor exact schedule fixtures
were generated independently with Python json/hashlib and explicit 64-bit
arithmetic. Resume regenerates every tuple/deck/seed and compares canonical
bytes. Configuration validation rejects arithmetic/representation overflow
before schedule allocation.

## Layout and records

```
run/
  .lock
  lock-owner.json
  ready.json
  inputs/
    original-config.json
    provenance.json
    requested.json
    resolved.json
    schedule.json
    models/<fingerprint>/models.json
    models/<fingerprint>/<artifact files>
  games/<game-id>.json
  failed-attempts/<attempt-uuid>.json
  sessions/<session-uuid>.start.json
  sessions/<session-uuid>.end.json
  diagnostics/<uuid>.json
  lock-history/<uuid>.json
  reports/<generation-uuid>/{summary.json,leaderboard.csv,game-scores.csv,manifest.json}
  reports/current.json
```

Every JSON record except original configuration and snapshot manifests uses
exactly `schemaVersion` (integer 1), `recordType`, `runId`, `payload` and
`payloadSha256`. The checksum covers version-one canonical payload UTF-8;
inventory hashes cover entire file bytes. Strict decoding rejects invalid UTF-8,
duplicate/unknown fields, missing values, unsupported versions, wrong run/type,
nonlowercase/checksum-invalid digests and lossy integer coercion. Typed domain
records close successful/failed result schemas. The committed game payload has
exactly `schedule` and `result`, binding every retained seat, deck and seed to the
resolved input digest. `TournamentGameCore.verifyResult` replays the entire audit
through the shared rules engine before both publication and resume; checksums
alone do not establish a legal game. Fallbacks must refer to that game, an
occupied model competitor and an actual retained game phase.

`requested` and `resolved` contain the canonical identity objects documented in
TOURNAMENT.md. `provenance` contains `configurationFile`, retaining the original
relative-path base. `schedule` contains `scheduleVersion`, `deckVersion`, `games`.
`ready` contains `runFormat`, `algorithms`, `inventory`. Algorithms explicitly
name canonical encoding, artifact fingerprint, seed derivation, schedule, RNG,
game audit, scoring and rating/export format versions. The inventory is a
lexically sorted array of `{path,size,sha256}` for every retained input file,
including snapshot manifest bytes. It never includes mutable game/history/report
files. Snapshots and inputs must stay at the exact retained absolute output.

Session starts contain `sessionId`, `startedAt`, `metadata`; ends contain
`sessionId`, `outcome`, `endedAt`, `metadata`. Start/end are separate immutable
records, allowing a crash without an end. Attempts require a retained start;
attempt UUIDs are unique across both successes and failures. Failed-attempt
records have no score. Diagnostics contain `recordedAt`, `detail`. Recovery
audit contains `previousBase64`, `previousSha256`, `recoveredAt`, preserving even
corrupt previous owner metadata byte for byte. All times use ISO-8601 instants.
Owner metadata retains owner UUID, PID, hostname, process start if available,
acquisition/release instants and release state; it is diagnostic, never authority
to break a live OS lock.

Authoritative paths require regular files/directories without symlinks. Unknown
authoritative entries refuse. Reserved same-directory `.tmp-<uuid>-<filename>`
files and report `.tmp-<uuid>` generation directories are interrupted-write
evidence, never committed authority, and remain available for inspection. An
initialization without valid readiness cannot be resumed or reused as new.

## Filesystem support and publication

The supported production profile is the default Linux local-file provider on
ext4, with reliable cooperative exclusive Java/NIO OS locks, same-filesystem
atomic rename, file `force(true)` and readable directory-channel `force(true)`.
Unknown, network, FUSE, overlay and tmpfs stores refuse rather than downgrade.
Filesystem type identifies the supported profile; it does not independently
prove device power-loss honesty. A completed API call is the software durability
boundary under the platform/filesystem/device contract.

`.lock` is permanent: no writer unlinks, replaces or renames its inode. A free
OS lock is necessary before inspecting/recovering stale ownership metadata;
`tryLock` failure always means busy, even when recovery is requested. An
unreleased/corrupt owner requires explicit recovery. Recovery first durably
publishes the exact previous bytes and audit, then replaces owner metadata.
Orderly close durably records release before unlocking; errors fail the
invocation and close still releases resources, aggregating failures.

All immutable publication is synchronized under the retained OS lock: write a
same-directory CREATE_NEW temporary file, force it, reopen/validate its exact
bytes, recheck destination absence with NOFOLLOW attributes, perform ATOMIC_MOVE,
force the parent directory, then acknowledge. No copy/non-atomic fallback exists.
Missing parent directories are created one at a time with parent-directory
fsync. Snapshot files and all containing directories are forced before ready.

Java ATOMIC_MOVE does **not** itself promise no replacement. No-clobber comes
from the permanent exclusive lock plus serialized absence checks: all cooperating
writers must use this protocol. This is not protection against a hostile outside
writer replacing filesystem entries. Existing immutable destinations always
fail, even when bytes are equal. Only owner and current-report pointers are
explicitly mutable. An error after rename is an uncertain acknowledgment and
fails the invocation; resume validates any final file before deciding to skip
or replay. Temporary files never advance durable progress.

`TournamentPublicationIO` is the filesystem syscall boundary for acceptance
fault injection; production uses the strict LOCAL implementation. It permits
crash-window tests without weakening production filesystem checks.

## Reports and lifecycle observations

`captureCommittedSet()` returns a fixed canonical `CommittedSet`: run ID, verified
games, payload checksums, complete epochs and their digest. Rating/report writers
must use this exact observation. `publishReports(set,exports,metadata)` refuses a
foreign/stale set and requires exactly summary JSON, leaderboard CSV and game
scores CSV. The runner's `publishReportSnapshot` also accepts an earlier immutable
observation while games continue: every game, checksum, complete epoch and digest
must match the authoritative subset, and publication cannot move backwards within
an ownership period. On resume the subset must include all already retained games.
The final report still uses the strict current-set publication path. Publication
forces a private complete generation, publishes the directory,
then atomically replaces and forces `current.json`. The manifest contains exact
game checksums/epochs, metadata and export hashes. A reader resolves the pointer
once and reads all exports through that generation. Old/orphan generations stay
available. Export arrays are defensively copied.

Game admission proceeds across all pending epochs without an epoch barrier.
Epoch completion requests background reporting. There is at most one in-flight
report and one coalesced request for the latest snapshot; intermediate epochs
can be skipped for display. Only complete epochs contribute to ratings, including
when epochs finish out of order. Progress counts completed epochs independently
of the latest displayed report. Calculation and durable report publication run on
the dedicated reporting thread; synchronized store operations still serialize
report publication with game commits. Failures stop further admission and fail
the run. Finalization drains reporting and publishes a terminal current-set report
before ending the session or releasing the store.

Terminal report metadata must carry `outcome:"COMPLETED"` and `sessionId` for
successful completion certification. `verify(...).successfullyCompleted` also
requires every legal game, a consistent complete generation, the referenced
successful session end, no later mutation/session/diagnostic evidence, and a
durably released owner. A missing terminal record, stale owner or missing/corrupt
export does not certify completion. Missing/corrupt derived exports are reported
as publication recovery; verified games remain intact. `pendingGames()` then
permits publication-only resume with no player construction. `historyJson()`
returns a copied canonical observation with `sessions`, `failed-attempts`,
`diagnostics`, `lock-history` arrays of `{file,payload}` for UI/report writers.

## Filesystem evidence and limits

On 2026-10-20, actual `/home/jan/git/jskat` uses
`/dev/mapper/system-root` ext4; `/tmp` uses tmpfs. Acceptance tests explicitly
selected the former through `JSKAT_TOURNAMENT_EXT4_TEST_ROOT`. They exercised
file/directory force, atomic rename, an actual second Java process denied a live
lock (and acquiring it after release), exact audited stale recovery, failure
before ready-file force/rename, after rename/directory force, uncertain game
acknowledgment, both report-pointer rename sides, immutable duplicate rejection,
temporary evidence preservation and read-only completed verification. The tmpfs
fixture refused production execution. These are process/syscall-failure tests,
not destructive power-cut tests or a claim about dishonest hardware caches.

Run durability acceptance on a local ext4 directory:

```
JSKAT_TOURNAMENT_EXT4_TEST_ROOT=/path/on/ext4 ./gradlew :jskat-base:test --tests 'org.jskat.tournament.*'
```

The ext4 integration fixtures explicitly skip when no ext4 test root is selected;
format/schedule/unit tests remain portable. Acceptance run directories retain
failure evidence under `jskat-tournament-store-tests` in that root and can be
removed after reviewing the results.
