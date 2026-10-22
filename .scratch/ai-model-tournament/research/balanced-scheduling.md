# Balanced and reproducible Skat tournament scheduling

## Question

What scheduling and deal-reuse design gives two or more tournament competitors fair exposure to opponents, seats, declarer and defender roles, and card distributions while preserving a reproducible seed and practical runtime?

## Recommendation

Use a **complete duplicate epoch** as the scoring unit.

For each epoch:

1. Generate one 32-card deck order from the tournament seed and epoch number.
2. With exactly two competitors, create independent player instances for the six mixed seat assignments `AAB`, `ABA`, `BAA`, `ABB`, `BAB`, and `BBA`.
3. With three or more competitors, enumerate every unordered group of three competitors and play that same deck under all six assignments of each group to `FOREHAND`, `MIDDLEHAND`, and `REARHAND`.
4. Start each game with fresh per-game player state and deterministic, independently derived random streams.
5. Count an epoch in the official leaderboard only when every scheduled game in it has completed. Persist individual results immediately so an interrupted epoch can resume exactly; a partial leaderboard must be labelled provisional.

This is the strongest simple design for the expected small roster of model releases. It exactly balances opponents, positions, and cards instead of hoping those factors average out. The number of games per epoch is

```text
n = 2: 6
n ≥ 3: 6 × C(n, 3) = n × (n - 1) × (n - 2)
```

where `n` is the competitor count. The UI and CLI should show this cost before starting and accept a fixed number of epochs. Do not stop early because a rating appears settled; a user may interrupt, but only completed epochs belong in the final comparison.

## Why this is the right unit

The official [International Skat Order, November 2022](https://dskv.de/app/uploads/sites/18/2020/08/ISkO-2022-1.pdf) establishes the asymmetries that the schedule must control:

- one game has three active players, one declarer, and two defenders (ISkO 1.1.1 and 3.4.1);
- cards are dealt into three ten-card hands plus the two-card Skat in the fixed `3-Skat-4-3` sequence (ISkO 3.2.6);
- Forehand, Middlehand, and Rearhand have different bidding order, and the declarer is determined by bidding (ISkO 3.3.1–3.3.5).

Seat assignment therefore affects more than who leads. It affects bidding information and the opportunity to become declarer.

Duplicate play is an established variance-reduction method for evaluating game-playing agents. The organizers' report on the [Annual Computer Poker Competition](https://ojs.aaai.org/aimagazine/index.php/aimagazine/article/view/2474/2362) describes resetting agents, switching their seats, and replaying the same cards; it says the multiplayer process is analogous. Davidson, Archibald, and Bowling's original paper, [*Baseline: Practical Control Variates for Agent Evaluation in Zero-Sum Domains*](https://poker.cs.ualberta.ca/publications/AAMAS13-baseline.pdf), defines full duplicate play as replaying each hand once for every player configuration and identifies factorial growth in the number of players at a table as its cost. For Skat that factor is fixed at only `3! = 6` because each game always has three active seats.

## Exact balance provided by one epoch

### Exactly two competitors

For competitors `A` and `B`, use six independent player configurations:

| Forehand | Middlehand | Rearhand |
|---|---|---|
| A | A | B |
| A | B | A |
| B | A | A |
| A | B | B |
| B | A | B |
| B | B | A |

Each table entry represents a separate Tournament player instance. Two entries
with the same competitor identity use the same AI implementation and model
release, but each instance owns separate game knowledge, mutable state, and a
deterministic random stream. Instances cannot exchange knowledge or other
per-game data.

Across the six-game cell, each competitor occupies every seat three times,
contributes nine seat participations, appears as the duplicated competitor in
three games, and appears as the single competitor in three games. Both
competitors therefore see the same cards and the same mixture of single and
duplicated participation. The completed cell produces one direct comparison
block between `A` and `B`.

### Three or more competitors

Let competitors have stable IDs, sorted canonically. For each unordered triple `{A, B, C}`, enumerate these six seat assignments:

| Forehand | Middlehand | Rearhand |
|---|---|---|
| A | B | C |
| A | C | B |
| B | A | C |
| C | A | B |
| B | C | A |
| C | B | A |

For every epoch this gives:

- **Cards and seats:** within each opponent triple, each competitor receives each exact ten-card hand twice, once for each ordering of the other two competitors. Each competitor occupies every seat equally often.
- **Opponent exposure:** every pair meets with every possible third competitor. For a fixed third competitor, the pair occurs once in each of the six distinct ordered seat pairs.
- **Equal volume:** each competitor plays `6 × C(n - 1, 2)` games, each pair meets in `6 × (n - 2)` games, and each competitor occupies each seat `2 × C(n - 1, 2)` times.
- **Common card population:** because the epoch's deck is reused across all triples, every competitor is evaluated on exactly the same card distribution against every opponent pair.

The whole six-game cell must share one immutable `dealId`. Its results should also carry `epoch`, competitor triple, permutation, and seat assignment so downstream rating and uncertainty calculations can recognize that the games are deliberately correlated observations from one deal.

## Declarer and defender exposure

Do not force declarer assignments. Under standard Skat the highest bidder becomes declarer (ISkO 3.3.5), so assigning the role externally would evaluate a different game and hide differences in bidding quality.

The six permutations instead equalize each competitor's opportunity. With two competitors, each competitor receives each exact hand in each seat three times: once as the single competitor and twice as the duplicated competitor. With three or more competitors, the same competitor sees the same hand in the same seat twice within each triple, with the two opponents exchanged. Actual declarer and defender counts are outcomes of the models. Record and report at least:

- games, bids won, and passed hands by competitor and seat;
- declarer games, wins, losses, and game value;
- defender games and wins, including the other defender's identity;
- all-pass games.

An all-pass game consumes its scheduled permutation and is not redealt. ISkO 3.2.19 explicitly says an all-pass deal is a valid game, and ISkO 3.3.7 advances to the next deal. Redrawing until somebody declares would select for stronger hands and bias the model comparison.

## Reproducibility contract

A seed alone is insufficient unless every source of randomness and its consumption order are controlled. The tournament report should therefore store:

- the user-visible root seed;
- scheduler and deal-generator algorithm names and version numbers;
- the canonical ordered competitor identities and model artifact hashes;
- ruleset, epoch count, and the complete generated schedule;
- the exact 32-card order, or an equivalent lossless four-packet representation, for every `dealId`;
- a deterministic seed for each competitor instance in each game;
- completion state and result for every stable game ID.

Derive independent sub-seeds from immutable identifiers, for example:

```text
SHA-256(rootSeed || purpose || epoch || lineupIds || permutation || competitorId || seat)
```

Use distinct purpose labels such as `deal`, `schedule-order`, and `player`. Never consume one shared mutable RNG from parallel workers: task order, retries, or resume would then change later games. Generate the complete schedule before execution, and sort persisted results by stable game ID rather than completion order.

Use a named, versioned shuffle implementation. A straightforward choice is a locally specified Fisher–Yates shuffle driven by a reproducible generator. Oracle specifies that two [`java.util.Random`](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/Random.html) instances given the same seed and method-call sequence produce identical results across Java implementations, and [`Collections.shuffle`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collections.html#shuffle(java.util.List,java.util.random.RandomGenerator)) accepts an explicit generator. Persisting the deck order remains the authoritative replay mechanism and protects old reports if the generator changes later.

Create a separate Tournament player instance for every occupied seat and reset all mutable player state between games so a competitor cannot remember a previously seen duplicate deal. This is required even when two seats use the same Tournament competitor: each instance has independent knowledge and cannot share per-game data with the other instance. Immutable model weights and inference sessions may be cached for performance only when doing so cannot expose mutable gameplay state. Bidding/play state and player RNG must remain per-instance and per-game. This follows the ACPC duplicate protocol, which resets bots before replaying the same cards with changed seats.

## Runtime and interruption

The exact schedule grows cubically with roster size, but its unit costs are transparent:

| Competitors | Games per epoch |
|---:|---:|
| 2 | 6 |
| 3 | 6 |
| 4 | 24 |
| 5 | 60 |
| 6 | 120 |
| 8 | 336 |
| 10 | 720 |
| 20 | 6,840 |

For model-release comparisons, expose `epochs` as the primary work control and show `epochs × games per epoch` plus an estimated duration in both interfaces. Keep the epoch count fixed in the saved configuration so reruns on faster or slower hardware still execute the same experiment. Execute independent games in parallel only after deterministic schedule generation and per-game seed derivation.

Checkpoint after every game. On resume, verify the tournament configuration and model hashes, then run only missing stable game IDs with their original deck and seeds. If a game fails, retry that exact game; do not replace its deal. A completed six-game cell may be used for provisional diagnostics, but the official cross-competitor leaderboard should advance only at complete-epoch boundaries, where all exposure counts are exactly equal.

If future tournaments regularly contain enough competitors that one full epoch is too expensive, add a separately named approximate mode based on balanced three-player block designs. It must report per-player and per-pair exposure deltas and cannot claim the exact opponent-third balance of the complete schedule. That complexity is unnecessary for the first model-release tournament.

## Current code implications

The current game path cannot meet this contract without a deal-injection and RNG-injection seam:

- [`CardDeck.shuffle()`](../../../jskat-base/src/main/java/org/jskat/util/CardDeck.java#L163) calls `Collections.shuffle(cards)` with the default randomness source.
- [`SkatGame.dealCards()`](../../../jskat-base/src/main/java/org/jskat/control/SkatGame.java#L277) always asks the deck to shuffle before dealing, though its `3-2-4-3` logic already matches ISkO.
- [`AbstractMLPlayer`](../../../jskat-base/src/main/java/org/jskat/ai/ml/AbstractMLPlayer.java#L34) constructs an unseeded `Random` used for fallback decisions.

The tournament engine therefore needs to supply an exact prepared deal and deterministic per-player RNG state independently of the ordinary interactive-game path. This is a planning conclusion; no implementation code was changed by this research ticket.

## Sources

- Deutscher Skatverband and International Skat Players Association, [*Internationale Skatordnung / Skatwettspielordnung*, November 2022](https://dskv.de/app/uploads/sites/18/2020/08/ISkO-2022-1.pdf).
- Nolan Bard, John Hawkin, Jonathan Rubin, and Martin Zinkevich, [*The Annual Computer Poker Competition*](https://ojs.aaai.org/aimagazine/index.php/aimagazine/article/view/2474/2362), *AI Magazine* 34(2), 2013.
- Joshua Davidson, Christopher Archibald, and Michael Bowling, [*Baseline: Practical Control Variates for Agent Evaluation in Zero-Sum Domains*](https://poker.cs.ualberta.ca/publications/AAMAS13-baseline.pdf), AAMAS 2013.
- Oracle, [`java.util.Random` API specification](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/Random.html) and [`java.util.Collections` API specification](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collections.html).
