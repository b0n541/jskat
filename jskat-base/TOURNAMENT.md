# Tournament contracts, version 1

The shared Java API lives in `org.jskat.tournament` in `jskat-base`. No type in
this package initializes JavaFX, an event bus or the ordinary model lifecycle.
The bundled schema is `org/jskat/tournament/configuration-v1.schema.json`.
`TournamentConfigurationReader` is the authoritative shared validator for both
entry points, including normalized uniqueness and arithmetic constraints that
JSON Schema cannot express. Validation is side-effect free and never creates
output directories. Issues sort by JSON Pointer, code, then message, with Java
string lexical comparison. JSON Pointer escaping follows RFC 6901.

## Requested and resolved configuration

`TournamentConfiguration` preserves the original JSON, its absolute file path,
the requested seed's presence, and each configured path's original spelling.
`ConfiguredPath.absolute` resolves against the configuration file's directory,
normalizes dot segments and Unicode NFC, and never expands shell expressions.
Names are stripped and NFC-normalized before measuring Unicode code points and
checking case-sensitive uniqueness. Installed release labels identify direct
children: `.`/`..`, path separators and NUL are rejected. No I/O or model
resolution occurs during structural validation.

UUIDs use canonical lowercase RFC variant spelling with version 1 through 8.
All roster sorting uses the lexical lowercase UUID string, **not** Java
`UUID.compareTo`, whose signed ordering differs. The int-indexed retained
schedule representation addresses at most `Integer.MAX_VALUE` total games.
Counts use checked signed-long multiplication before that representation check;
no schedule allocation happens during validation. This is a representation
constraint, not a schema-level epoch cap.

New/resume/verify and stale-lock authorization belong to `TournamentInvocation`.
Workers, verbosity and stall warnings belong to `TournamentSessionOptions`.
Neither forms part of configuration identity. `ResolvedTournamentConfiguration`
adds one effective root seed, exact build identity and the resolved roster.
Model-backed entries retain fingerprint, complete inventory, concrete class,
release label and normalized snapshot location. Their original source stays in
the requested competitor. `TournamentInputResolver.verify` must use retained
snapshots exclusively. Resolution/probing errors use the same issue/result
contract; implementations aggregate discoverable roster failures.

## Canonical bytes and fingerprints

`CanonicalJson` version 1 emits compact UTF-8 JSON without BOM, whitespace or a
trailing newline. All strings and keys normalize to NFC; keys sort in Java
string lexical order **after** normalization. Key collisions after normalization
and unpaired Unicode surrogates are invalid. Arrays keep their supplied order.
Numbers are exact integral values or finite decimal values normalized by
stripping trailing zeros and writing plain decimal notation; negative zero is
`0`. NaN and infinities are invalid. Jackson's decimal node should be used when
reading exact decimal values; binary floating inputs retain their represented
Java decimal value. Required semantic array sorting happens before encoding.

The SHA-256 digest is always lowercase hexadecimal. Each identity wraps the
content with its explicit `encoding` field:

- `jskat-request-v1`: `configuration` has schema/ruleset, epochs, normalized
  absolute output and UUID-string-sorted competitors. Names are normalized.
  Model paths are normalized absolute paths. Explicit seeds use canonical
  unsigned decimal strings; an omitted seed is omitted. Original JSON/path
  spelling is provenance and does not participate.
- `jskat-resolved-v1`: requested digest, effective unsigned seed, complete build
  identity, game count, UUID-string-sorted resolved roster. Each model includes
  its release label, fingerprint, snapshot and filename-sorted inventory.
- `jskat-artifacts-v1`: `artifacts` contains filename-sorted objects with `name`,
  `size` (byte count), `sha256`. Duplicate normalized filenames are invalid.
  Labels, provenance and paths do not participate in the release fingerprint.
- `jskat-seed-v1`: `rootSeed` is unsigned decimal, `purpose` is a nonblank string,
  `identifiers` is an ordered string array. Hash the envelope and interpret the
  first eight bytes as a big-endian 64-bit value. Java `long` retains its bits;
  use `Long.toUnsignedString` when serializing. Purpose labels include `deck`,
  `player-instance`, and `bootstrap`; the scheduler owns stable identifiers and
  a separately versioned shuffle algorithm. Never seed from worker/attempt IDs.

The requested fixture in `src/test/resources/org/jskat/tournament` and literal
artifact/seed vectors in `TournamentIdentityTest` were calculated independently
with Python's `json.dumps(sort_keys=True,separators=(',',':'),ensure_ascii=False)`
and `hashlib.sha256`. Their Unicode samples are NFC before Python serialization.
Unsigned zero/max boundaries, combining Unicode, lexical UUID ordering, decimal
normalization and the largest addressable two-player run have fixtures.

## Packaged build identity

Gradle generates `org/jskat/tournament/build-identity.properties` every resource
build. Runtime `TournamentBuildIdentity.current()` reads that resource and
rejects missing/unsupported metadata; it never invokes Git or substitutes an
application version. A clean identity is the complete Git hash. A dirty identity
is `<commit>+dirty:<sourceDigest>`. Both staged and unstaged changes and relevant
non-ignored untracked files contribute to dirtiness. Relevant deleted files also
mark the build dirty.

The exact `jskat-build-inputs-v1` inventory is Git's cached/non-ignored-untracked
file list, de-duplicated and sorted, restricted to:

- production source/resources under `jskat-base/src/main`, `jskat-gui/src/main`,
  `app/src/main`, and convention plugins under `build-logic/src/main`;
- all `*.gradle.kts` files, `gradle/libs.versions.toml`;
- `gradle/wrapper/*`, `gradlew`, `gradlew.bat`.

Tests, docs, scratch, generated outputs and ignored inputs do not contribute.
For files that still exist, the UTF-8 digest input begins
`jskat-build-inputs-v1\n`, then each entry is:

```text
<UTF-8 path byte length>:<relative Unix path>\n
<file byte length>:<lowercase file SHA-256>\n
```

The SHA-256 of these bytes is the dirty source digest. The source digest is
omitted for a clean relevant inventory. Packaging requires a full Git commit;
a source archive must supply actual Git metadata rather than invent identity.
Resume must compare the complete identity exactly.

## Execution and durable boundaries

`ScheduledTournamentGame` retains a zero-based epoch, stable game/block IDs,
three ordered seat assignments with per-seat seeds, all 32 cards in dealing
order, and its deck seed. A game result cross-references this schedule by game ID
and binds the resolved input digest. A result freezes only after all callbacks
including `finalizeGame` succeed. It includes typed contract declarations,
declarer/all-pass outcome, signed value, card-point and trick observations,
per-seat Seeger–Fabian scores, complete ordered immutable events and fallbacks.
Events start at sequence zero without gaps and must cover phases, bids,
declarations/discards, card plays/tricks, scoring and completion. Payload JSON is
immutable canonical text rather than mutable game-engine state. A failed attempt
has separate diagnostics/events and deliberately has no score field.

`TournamentRunStore.commit` acknowledges only durable authoritative publication;
store implementations validate input linkage, legal outcomes and scores against
the retained schedule/rules before committing. Store ownership includes an
exclusive lock and close releases it. `TournamentProgress` distinguishes durable
commits from admissions and unsuccessful attempts. Cancellation is a thread-safe
cooperative signal; the runner remains responsible for the admission boundary,
letting admitted games finish and observing the signal before new admission.

## Operating guide

Start with the complete model-free configuration and launcher commands in
[the CLI manual](../app/TOURNAMENT.md), or use the native editor documented in
[the JavaFX manual](../jskat-gui/TOURNAMENT.md). `ml` and `ml-pro` require a
model variant; replace a model-free implementation with, for example:

```json
{"id":"ca207ce9-3ca4-4ce1-8443-81e081b8344e","name":"Candidate",
 "implementation":"ml-pro","model":{"source":"development","path":"../models/candidate"}}
```

Other source variants are `{"source":"active"}` and
`{"source":"installed","version":"v1.4.0"}`. Installed versions are exact
directory labels under `~/.jskat/models/releases`, including any `v` prefix.
Each model implementation can appear with distinct effective fingerprints;
Random and Algorithmic can each appear only once. A roster may compare the same
release with different ML implementations. See [inputs and native ownership](TOURNAMENT-INPUTS.md)
for inventories, signatures and source discovery.

A run always uses standard three-player Skat: no Ramsch or configurable rule
variants. All-pass consumes its scheduled game with three zero scores. Winning
declarers receive signed game value +50; losing declarers receive signed game
value −50, and each defender receives 40. Defenders receive zero on declarer
wins. Add every occupied seat to its competitor's block total, including both
seats when one competitor occupies two. Compare totals ordinally; score margins
do not affect the rating. [The shared game core](TOURNAMENT-GAME.md) and
[ratings manual](TOURNAMENT-RATINGS.md) specify legal results, observations,
relative ratings, coverage, whole-deal uncertainty and predictive diagnostics.

Retain the entire output directory. `summary.json`, `leaderboard.csv` and
`game-scores.csv` are a single captured report generation; audit records retain
complete successful events, unscored failed attempts, sessions and fallback
causes. [The store manual](TOURNAMENT-STORE.md) gives the exact layout, format
versions, atomic publication boundaries and recovery procedure. Resume uses the
exact original requested configuration and complete packaged build identity,
including any dirty-source digest. Only session workers, verbosity and stall
warning duration may change. Original model sources may disappear after readiness;
retained snapshots must remain intact. Preserve the matching executable/build.

Resume never repairs authoritative records or initializes an incomplete run.
There is no format migration, salvage, overwrite, forced lock takeover, automatic
retry, hard deadline or hard callback termination. Explicit stale recovery requires
a free OS lock and retains the previous owner bytes. Cancellation drains admitted
games, computes and publishes final observations, closes resources and releases
ownership before returning. A hung player callback can delay that indefinitely;
a monotonic stall warning does not end the attempt. See [runner lifecycle](TOURNAMENT-RUNNER.md)
for failure precedence and the bounded worker/player ownership contract.

[Acceptance evidence](TOURNAMENT-ACCEPTANCE.md) records executed scenarios and
limits, including the selected Linux/ext4 software durability boundary.
