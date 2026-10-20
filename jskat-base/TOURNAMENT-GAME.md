# Shared one-game execution

`control.SkatGameEngine` owns the one existing game state machine. Its immutable
`RulesPolicy` and synchronous `Presentation` port replace direct options, event
bus, view, and delay dependencies. `control.SkatGame` reads the ordinary options
once, owns presentation pacing and delayed player wrappers, and translates the
port into the existing local table notifications. Authoritative moves apply
synchronously using `SkatGameData.apply`, independently of bus subscribers.
Ordinary replay/ISS subscribers retain `adjustDataOn` as an adapter to that method.

`TournamentGameCore.play` accepts the retained schedule, three distinct leased
player identities in seat order, resolved input digest, attempt/session IDs, and
fallback observations. The player pool seeds and invokes `newGame(seat)` exactly
once before exposing the leases; the core does not reset them again. The core
uses the standard policy with no Ramsch, contra, table registration, graphical
objects or waits. It never shuffles the retained deck. All player notifications
and actions are guarded, including phase notifications and finalization. A
`GameFailure` preserves the original callback cause and an unscored
`TournamentFailedAttempt` with seat/callback context and the audit produced so far.
Only after finalization and GAME_OVER notifications succeed are scores and the
completion event frozen. Resource return/cleanup belongs to the caller after
this boundary.

Actions are validated before state mutation: supported nondecreasing bids,
owned distinct discards, standard contracts consistent with pickup/hand status
and ouvert cards, owned cards following the shared rule's suit/trump predicate.
A Null game may legally end after the declarer's first won trick. The scoring
uses existing game values, adding 50 for a winning declarer or subtracting 50
for a losing declarer; each defender receives 40 on a loss and zero on a win.
All-pass scores zero for every seat. Mutable callback arguments are copied away
from authoritative deal, contract, skat, trick and score state.

## Audit format, version 1

Events are immutable canonical JSON payloads with contiguous zero-based sequence
numbers. This vocabulary is part of the version-1 durable game format:

- `phase`, `active`, `declarer`: `{value: <enum name>}`.
- `CardDealEvent`: seat-name arrays of card enum names plus `skat`.
- `BidEvent`, `HoldBidEvent`, `PassBidEvent`: `seat`, `bid` (offered next bid for pass).
- `PickUpSkatEvent`, `DiscardSkatEvent`: `seat`, `cards` in retained action order.
- `GameAnnouncementEvent`: optional `seat` (absent for all-pass), `gameType`,
  `hand`, `ouvert`, `schneider`, `schwarz`, and `ouvertCards`.
- `TrickCardPlayedEvent`: `seat`, `card`.
- `trick`: immediately after every third accepted card, including Null early
  completion and the tenth trick: zero-based `index`, `winner`, `points`.
- `score`: three seat-name fields containing signed Seeger–Fabian scores.
- `complete`: `{ruleset: "standard-skat-v1"}` after all callbacks succeed.

`TournamentGameCore.verifyResult(schedule, result)` is the pure semantic verifier
for the store. It constructs transcript players from the frozen actions and
re-executes them through the same engine/rules, then compares the complete event
stream, legal outcome, card points/tricks and scores. It never constructs a
competitor, runs inference, calls original player callbacks or uses filesystem,
options, buses or views. Missing/reordered/extra events, changed decks, illegal
actions and forged observations/scores fail with `IllegalArgumentException`.
The store additionally verifies resolved-input linkage and schedule/record
identity, checksums, seeds and session/attempt envelopes.
