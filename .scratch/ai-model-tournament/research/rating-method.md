# Elo-style rating for three-player, role-asymmetric Skat

## Question

Which Elo-style rating method can turn outcomes from three-player,
role-asymmetric Skat games into defensible ratings for Tournament competitors,
and what limitations, parameters, or additional uncertainty measures must the
specification disclose?

A Tournament competitor is one ML player implementation paired with one model
release. The first leaderboard needs one overall rating, with declarer and
defender results shown as descriptive breakdowns.

## Recommendation

Use an **Elo-scaled, regularized Bradley-Terry model fitted in batch to balanced
comparison blocks**.

Do not update the rating from individual Skat games. One game compares a single
declarer with two cooperating defenders, and bidding helps determine who takes
which role. A direct Elo update has no native representation for that structure.
Classic TrueSkill can infer individual skills from team results, but it assumes
that a team's performance is the sum of its members' performances. Microsoft
also states that classic TrueSkill does not handle handicapped games, because it
assumes equally skilled teams have equal chances. Those assumptions do not
describe the structurally unequal declarer and defender sides of Skat. See the
[original TrueSkill paper](https://www.microsoft.com/en-us/research/wp-content/uploads/2006/01/TR-2006-80.pdf)
and Microsoft's [official TrueSkill description and FAQ](https://www.microsoft.com/en-us/research/project/trueskill-ranking-system/).

Instead, make the rated observation a completed comparison block:

1. For one lineup of three competitors, reuse a declared set of shuffled deals
   across all competitor-to-seat permutations. For every deal, each competitor
   must receive each seat's hand equally often over the block. Bidding remains
   part of the competitor's behavior; the runner must not force equal declarer
   counts.
2. Compute each competitor's block total with the Tournament's fixed Skat
   scoring formula.
3. Convert the three totals into the three pairwise block results. A higher total
   is a win (`1`), a lower total is a loss (`0`), and equal totals are a tie
   (`0.5`).
4. Refit all ratings from all completed blocks in one batch whenever a report is
   produced or a run is resumed. Never carry forward game-by-game `K`-factor
   mutations as the canonical result.

This construction rates the whole competitor: bidding, declarer play, and
defender play all affect its block total. The declarer and defender breakdowns
remain diagnostics and do not become separate ratings in the first version.

## Rating model

For competitors `i` and `j`, fit the Bradley-Terry probability

```text
P(i ranks above j in a balanced block)
  = 1 / (1 + 10 ^ ((R_j - R_i) / 400))
```

The original Bradley-Terry model assigns strengths from repeated paired
comparisons; its win probability is the first competitor's positive strength
divided by the sum of both strengths. See Bradley and Terry,
[“Rank Analysis of Incomplete Block Designs: I. The Method of Paired
Comparisons”](https://doi.org/10.2307/2334029). FIDE's official Elo regulations
likewise map a rating difference to an expected score and use observed scores of
`1`, `0.5`, and `0`; this supports the familiar 400-point display scale, although
FIDE Elo itself is a two-player online update rule. See the
[FIDE Rating Regulations, sections 8.1–8.3](https://handbook.fide.com/chapter/B022024).

Fit the model as a regularized logistic regression over the pairwise block
results. Constrain the competitor mean to 1500 and use a zero-centered Gaussian
prior (equivalently, an L2 penalty) on rating deviations. A concrete default is
`R_i - 1500 ~ Normal(0, 400)`; the report must record the configured prior
width. The prior keeps estimates finite when a competitor is undefeated or the
comparison graph is sparse. Bayesian inference and extensions for group
comparisons are established for generalized Bradley-Terry models; the authors'
reference package also includes a home-advantage extension. See Caron and
Doucet, [“Efficient Bayesian Inference for Generalized Bradley-Terry
Models”](https://www.stats.ox.ac.uk/~doucet/caron_doucet_bayesianbradleyterry.pdf)
and their [BayesBT reference implementation](https://www.stats.ox.ac.uk/~caron/code/bayesbt/).

Treating a tied pair as `0.5` is an Elo-compatible composite-likelihood
convention, not a generative model of tie frequency. The specification should
require a Davidson/Plackett-Luce tie extension if ties are common enough to
affect rankings; that change would introduce another parameter and a report
format version.

Sorting by the fitted `R_i` gives the first leaderboard. Show, alongside each
point estimate:

- a 95% interval from the block bootstrap described below;
- completed blocks and raw games, overall and by role;
- pairwise probability estimates against every other competitor;
- a `provisional` marker until the configured coverage and precision criteria
  are met.

## Why not the obvious alternatives?

### Per-game Elo or Glicko

FIDE Elo is defined around a rating difference between two players and a
two-player result. Glicko adds a rating deviation, which is useful evidence that
a leaderboard should expose uncertainty, but it remains a paired-game model.
Glickman's own description recommends reporting a rating interval rather than a
point alone. See [“The Glicko System”](https://www.glicko.net/glicko/glicko.pdf).

Splitting one Skat game into “declarer versus defender A” and “declarer versus
defender B” would count one shared result twice, treat the cooperating defenders
as independent opponents, and make the rating depend on role frequency. It is
therefore unsuitable as the canonical observation.

### Per-game TrueSkill

TrueSkill is the closest off the shelf alternative: it generalizes Elo to any
number of competitors, supports teams and draws, and tracks a mean and
uncertainty. Its published model defines team performance as the sum of member
performances, however, and Microsoft describes that additive model as an
assumption. Applying it directly as one declarer team versus a two-member
defender team would encode the wrong team-size relationship unless JSkat first
designed and validated role weights or a declarer handicap. That is additional
modeling work, not a safe default.

TrueSkill over already balanced block rankings would be defensible, but the
Tournament compares immutable artifacts and can retain every result. A batch
Bradley-Terry fit is simpler, independent of event ingestion order, and has no
skill-drift parameter to tune.

### Plackett-Luce over three-way block ranks

A Plackett-Luce model is a coherent direct likelihood for multi-competitor
rankings. The primary inference work covers complete and partial rankings and
notes that sparse rankings need regularization. See Guiver and Snelson,
[“Bayesian Inference for Plackett-Luce Ranking
Models”](https://www.microsoft.com/en-us/research/wp-content/uploads/2016/02/plackett.pdf).

It is a sound later refinement. For the first version, pairwise Bradley-Terry
rows are easier to implement and explain. Because the three rows from one block
are correlated, their model-based standard errors must not be presented as if
they came from independent observations.

## Required uncertainty treatment

Use a **cluster bootstrap whose resampling unit is the complete comparison
block**. Every game derived from reused cards or a shared deal seed must stay in
the same resampled unit. Refit the regularized model in each replicate and use
the empirical 2.5th and 97.5th percentiles for each rating. The bootstrap was
introduced as a method for estimating sampling distributions from resampled
observations by Efron,
[“Bootstrap Methods: Another Look at the
Jackknife”](https://doi.org/10.1214/aos/1176344552); keeping the dependent games
together is the necessary application-specific unit here.

The configuration and report must record the bootstrap replicate count and RNG
seed. A practical deterministic default is 5,000 replicates. Regularization
keeps bootstrap fits finite even when a resample contains an undefeated
competitor, but the report should flag a replicate or final comparison graph
that is disconnected.

Also report predictive checks at block level: at minimum held-out or
leave-one-block-out log loss and a calibration table comparing predicted
pairwise probabilities with observed frequencies. An Elo difference should not
be interpreted as a probability until the model is adequately calibrated on
these Tournament results.

## Parameters and contracts the specification must freeze

The durable Tournament configuration or versioned rating-algorithm definition
must state:

- the comparison-block construction, including deal reuse, seat permutations,
  treatment of incomplete blocks, and minimum block coverage between
  competitors;
- the exact per-game scoring formula used to form block totals, including
  all-pass, illegal, aborted, timeout, forfeit, and tied-block handling;
- rating center (`1500`), scale (`400`), regularization/prior family and width,
  optimizer or posterior estimator, convergence tolerance, and algorithm
  version;
- whether the leaderboard uses a posterior mean, MAP estimate, median, or
  another point estimate (use the regularized MAP estimate for version 1);
- the bootstrap unit, replicate count, seed, interval definition, and behavior
  when a replicate has poor comparison coverage;
- the predeclared stopping rule and the threshold at which a rating ceases to be
  provisional; use completed whole blocks and a precision target rather than an
  unsupported universal number of games;
- the immutable competitor identity: ML implementation, model-release identity,
  and integrity hash;
- the complete game rules, Tournament scoring rule, schedule seed, and rating
  algorithm/report schema versions.

## Declarer and defender breakdowns

Keep the accepted single overall rating. For each competitor, provide
descriptive counts and rates separately for declarer and defender play, such as
games, wins, losses, contract mix, and points under the fixed scoring formula.
Also show uncertainty on win rates, preferably with intervals calculated over
whole blocks rather than games.

These breakdowns should explain an overall result, not create a second path into
the rating calculation. Conditioning the overall rating on contract or role can
remove behavior produced by the competitor's own bidding policy and would
therefore change what “whole Skat player” means.

## Limitations to disclose in every report

- **Relative scope:** ratings compare this competitor pool under this ruleset,
  scoring formula, schedule, and model version. Values from different runs are
  not comparable unless those elements and anchor competitors are identical.
- **One-dimensional summary:** bidding, declarer play, and partnership defense
  can offset one another in a single number. Non-transitive matchups and
  partner-specific synergy violate the scalar-skill assumption.
- **Ordinal information:** converting block totals to wins/ties/losses discards
  victory margins. This is deliberate for robustness, but close and decisive
  blocks receive equal rating weight.
- **Dependence:** repeated deals and the three pairwise rows from one block are
  correlated. Game-level confidence intervals will be too narrow; only
  block-aware uncertainty is suitable.
- **Schedule sensitivity:** weak card/seat balance, disconnected opponent groups,
  incomplete blocks, or adaptive stopping can move the ratings. Reports must
  expose these conditions.
- **Finite evidence:** a rating point estimate does not establish superiority.
  Intervals, pairwise probabilities, sample coverage, and calibration must be
  visible with it.
- **Static target:** the model assumes each competitor is immutable during a
  run. Any change to implementation or model artifacts creates a new competitor;
  there is no TrueSkill-style dynamics parameter in version 1.

## Decision summary

The first Tournament should rate the ordered outcomes of reproducible,
card-and-seat-balanced comparison blocks with a regularized, batch
Bradley-Terry model displayed on the conventional 1500/400 Elo scale. It should
publish 95% block-bootstrap intervals and block-level calibration, while keeping
declarer and defender statistics descriptive. Direct per-game Elo, Glicko, or
unmodified TrueSkill should not be used for the canonical leaderboard because
none represents Skat's one-versus-two role structure without extra assumptions.
