# Context

## Glossary

### Active model release

The complete, validated ML model release that JSkat currently uses from
`~/.jskat/models`. It is selected only after every required file has passed the
release manifest's integrity checks.

### Staged model release

A candidate ML model release being downloaded and verified in a private
location. It cannot be used by an ML player and does not replace the Active
model release until activation completes.

### Resolved model set

The immutable description of validated model artifacts resolved from one model
snapshot for constructing an ML player. An ML player receives this set and has
no access to the model store, source release, or their lifecycle managers.

### Model release fingerprint

The stable content identity calculated from a model release's normalized
complete artifact inventory. Release labels, filesystem paths, source aliases,
download locations, and manifest formatting do not form part of this identity.

### Development model release

A complete, manifest-compatible ML model release produced locally for
development. It can be selected for a tournament without becoming the Active
model release.

### Tournament model snapshot

The private, verified copy of a resolved model release retained as an immutable
tournament input. Initial and resumed runs use the snapshot rather than the
source release from which it was created.

### Tournament competitor

A registered player implementation with a stable UUID, unique display name,
and the inputs that implementation requires. An ML Tournament competitor pairs
its implementation with one model release; a model-free Tournament competitor
has no model release. A tournament cannot contain duplicate competitors with
the same implementation and effective inputs.

### Tournament player implementation ID

A stable built-in identifier for a registered Tournament player implementation,
independent of its Java class name. Its registry entry defines construction,
required inputs, and deterministic seeding. For an ML implementation it also
defines the model artifacts and ONNX signatures the implementation accepts.

### Tournament build identity

The full Git commit hash for a clean JSkat build. A build from a dirty worktree
adds a deterministic digest of the relevant source tree so resume can require
an exact match.

### Tournament reproducibility

The ability to repeat a tournament from its retained inputs and purpose-specific
seeds using deterministically seeded registered player implementations. A logged
unseeded inference fallback makes the affected tournament non-reproducible.

### Tournament player instance

A seat-bound player object created from a Tournament competitor for one game.
Every instance owns its own game knowledge and mutable state and cannot share
that data with another instance, including an instance created from the same
AI player implementation and model release.

### Tournament score

The three-player Seeger–Fabian points credited to an occupied seat for a
completed game. A Tournament competitor's comparison-block total is the sum of
the Tournament scores from every seat instance it occupied.

### Comparison block

The six games that reuse one deal across every required seat assignment for one
competitor grouping. It contains the six mixed assignments when there are two
Tournament competitors and all six seat permutations of an unordered triple
when there are three or more.

### Tournament epoch

The complete duplicate schedule for one deal. With two Tournament competitors
it contains one Comparison block; with three or more it contains one Comparison
block for every unordered competitor triple.

### Tournament rating

The regularized batch Bradley–Terry estimate derived from Tournament score
orderings in complete Comparison blocks from complete Tournament epochs. It is
displayed on an Elo-style scale centered at 1500 with a 400-point scale and is
recomputed from retained observations rather than updated sequentially.

### Tournament game ID

The stable identity of one scheduled tournament game, determined by its
Tournament epoch, competitor grouping, and seat assignment. Retries and resumed
execution retain the same Tournament game ID, deal, and purpose-specific random
seeds.

### Headless Skat game core

The presentation-independent rules boundary that plays one scheduled Tournament
game from its retained deal and three isolated Tournament player instances.

### Tournament runner

The lifecycle that executes a retained Tournament schedule, commits completed
games, publishes progress and ratings, and reaches a terminal run state.

### Committed Tournament game

A legally completed Tournament game whose immutable result has been accepted by
the durable run store. Only committed games count toward progress, ratings, and
resume position.

### Tournament player lease

Exclusive use of one resettable Tournament player instance by one seat for the
duration of a game attempt. A game holds three distinct leases even when one
Tournament competitor occupies multiple seats.

### Tournament player pool

The run-scoped owner of reusable Tournament player instances. It leases healthy
instances exclusively to games and closes discarded instances and all retained
instances when the run ends.

### Skat game event

An ordered domain fact produced while one Skat game progresses, such as a phase
change, bid, card move, completed trick, or game completion.

### Tournament progress

An immutable observation of Tournament execution, including admitted games,
durable commits, failed attempts, completed epochs, cancellation, and terminal
outcome. It is distinct from the events within an individual Skat game.

### Admitted Tournament game

A scheduled Tournament game whose three player leases have been acquired and
whose final pre-start cancellation check has passed. Cancellation lets admitted
games finish but prevents any further admission.

### Context panel stack

The centre area of a Skat table. It presents the phase-specific game context and a lower area for state-appropriate actions and card information.

### Shared action area

The lower part of the context panel stack. It is the single location for command buttons applicable to the current local, ISS, or replay state.

### Shared context renderer

The presentation component that projects a Context Rendering State into phase content, lower-left card content, and lower-right actions. It owns lower-row placement; phase panels retain their event handling.

### Replay skat

The final two cards discarded into the skat, shown during trick-play replay. It is distinct from the dealt skat temporarily displayed to a declarer who picked it up.

### Dealt skat

The two original skat cards exposed in `DiscardPanel` when the declarer picks them up during a game or its replay.

### Replay completion

The replay state after the final move. Its shared action area offers Reset to Start and Continue Skat Series; Next Move is not present.
