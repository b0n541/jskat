# Ratings and reports, version 1

`TournamentReports.calculate(configuration, schedule, committedSet, historyJson,
metadata)` returns immutable canonical JSON and CSV text with `exports()` providing
fresh byte arrays. `publish(store, metadata)` captures exactly one committed set,
calculates, and calls the store's generation publisher. `read(store)` resolves a
single generation and returns all three exports. The runner may instead perform
capture/calculate/publish explicitly. Lifecycle metadata (including outcome and
session UUID) is copied into summary `lifecycle`; pass the same metadata to the
store publication. `historyJson` comes from `store.historyJson()` and retains
sessions, failed attempts, diagnostics, and recovery history. A caller must supply
verified committed games: calculate is a derivation boundary, not a second legal
game verifier. The store owns verification and stale-set rejection.

Only epochs with every scheduled game enter block totals and pair observations.
Every occupied seat is added once, including both duplicated seats. A block yields
one observation per unordered competitor pair, in UUID lexical order: higher total
1, lower 0, equal .5. Epoch/block order is canonical, irrespective of completion.
Partial games remain in per-game CSV, counts and descriptive statistics only.
`provisional` means scheduled games remain; successful completion certification
continues to belong to durable store/session verification.

## Numerical definitions

`jskat-bt-map-newton-v2` minimizes logistic negative log likelihood plus independent
Normal(0,400) penalties on deviations from 1500. Internally
`x=(rating-1500)*ln(10)/400` has prior variance `ln(10)^2`. The Newton Hessian is a
positive definite weighted graph Laplacian plus prior precision; Cholesky solves
it without a reference-player constraint. Initial x is zero. Convergence requires
gradient infinity norm <1e-10 or Newton correction infinity norm <1e-12, within
100 Newton iterations. The latter bounds the correction in Elo units to less than
1.74e-10 and handles the gradient's floating-point floor. Backtracking tries
alpha=2^-k for k=0..49; Armijo coefficient 1e-4 and objective-roundoff allowance
max(1e-14, 16 ulps of the current objective). Win and loss softplus terms are summed
directly, avoiding cancellation for one-sided results. Stable softplus/logistic
forms avoid unnecessary overflow. Java
StrictMath supplies transcendentals and square root. Final deviations are centered
by subtracting their mean. Nonfinite arithmetic, failed factorization, line search
or convergence throws an explicit exception; no alternative estimator is used.
Observations aggregate into canonical pair counts, with exact integer/half counts.

Version 2 fixes version 1's rounding stall near the optimum: subtracting the win
term from the softplus likelihood could reject an accurate full Newton step,
then repeatedly accept a backtracked step too small to change x. The estimator,
prior, bootstrap draws and percentile convention are unchanged. Reports retain
the new optimizer version and both stopping tolerances; exact build matching for
resume still applies to runs created with the earlier build.

`jskat-epoch-splitmix64-percentile7-v1` derives its retained seed with
`deriveSeed(root,"bootstrap","ratings-v1")`. It sorts distinct epoch IDs, draws
exactly E whole epochs with replacement per replicate, and refits all 5,000
replicates. SplitMix64 is the scheduler's documented integer mixing algorithm;
bounded draws use `(nextLong() >>> 1) % E`, rejecting signed overflow of
`bits-remainder+E-1`. Replicates and draws iterate from zero in increasing order.
Percentiles sort the 5,000 ratings; q=.025/.975 uses index `(N-1)*q` and linear
interpolation between adjacent values (type 7). No replicate is dropped. Fewer
than two epochs yields null intervals with `fewer-than-two-complete-epochs`.

`jskat-loeo-ten-bins-v1` refits once excluding each complete epoch and predicts
all its canonical pair observations. Natural-log binary cross entropy includes
ties as fractional outcomes; mean loss denominator is the total number of held
out pair observations. Calibration uses ten equal probability bins [i/10,(i+1)/10),
with the last including 1. Each observation is counted once, oriented to the
lexically first UUID; mean prediction/outcome divide by that bin's count. Empty
bins use null means. Fewer than two epochs yields unavailable diagnostics, null
loss, zero observations, and an empty calibration array.

Independent Python scalar bisection and unsigned-integer bootstrap oracle lives at
`src/test/resources/org/jskat/tournament/ratings-reference-v1.py`. Literal tests
cover prior/scale, opposite epochs, bootstrap endpoints and held-out loss/bins.
Additional public-boundary tests cover all-pass ties, duplicate seats, partial
exclusion, canonical completion order, real registered games and ext4 generations.

## Export interpretation

`jskat-rating-export-v1` summary retains requested/resolved identities, canonical
configurations and original configuration/path provenance, the fixed committed
checksum map and digest, included epochs, every block total and pair observation,
all numerical versions and parameters, dedicated bootstrap seed, history and
lifecycle, fallbacks, leaderboard, pair coverage and probabilities, and statistics.
No complete epochs means `hasRatedEvidence:false`, null rating/probabilities, and
blank CSV rating cells. The prior's internal 1500 is never presented as evidence.

Rows rank descending unrounded rating, tie exactly equal ratings with competition
ranks (1,1,3), then UUID lexical order. No evidence is a tie. Canonical CSV uses
UTF-8, LF, a fixed header, and RFC-style quoting of every data field (quotes double).
Leaderboard includes IDs, names, implementation, point/interval/provisional/evidence;
game CSV includes stable game/epoch/block/seat/competitor, score, role, win status,
all-pass and contract. An all-pass role has no win observation.

`blockTieRate` divides blocks containing any tied pair by complete rated blocks;
`blockPairTieRate` divides tied pair observations by all pair observations. Both
publish their denominators; pairwise rows publish exact wins/losses/ties/coverage.
These probabilities are Bradley–Terry ordinal pair probabilities; there is no
separate draw model.

Descriptive statistics include all committed seats, explicitly including partial
epochs. Seat appearances, signed score totals/means, all-pass appearances, card
points/tricks, declarer and defender appearances/wins/losses/win rates/scores are
reported per competitor. All-pass is excluded from role denominators; unavailable
ratios are null. `contracts` counts non-all-pass seat appearances by game type;
`declarerContracts` counts contracts declared by that competitor only. Contract
flags count seat appearances. Bidding counts accepted Bid/Hold/Pass audit actions
and maximum accepted bid, with each player mapped by its scheduled seat. It does
not infer bids from scores or failed attempts.

Ratings discard score margins, are relative to this roster, can conceal partner
and nontransitive effects, and do not produce role ratings. Wide uncertainty is
compatible with a final fixed-size run and never triggers early stopping.
