package org.jskat.tournament;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.*;

class TournamentRatingsTest {
    private final UUID a = UUID.fromString("11111111-1111-4111-8111-111111111111"),
            b = UUID.fromString("ffffffff-ffff-4fff-8fff-ffffffffffff");

    @Test
    void priorAndEloScaleMatchIndependentScalarRoot() {
        var r =
                TournamentRatings.fit(
                        List.of(a, b),
                        List.of(new TournamentRatings.Observation(0, "block", a, b, 1)));
        // Independent bisection of d/400^2 = ln(10)/400 * (1-logistic(2*d*ln(10)/400)).
        assertThat(r.get(a)).isCloseTo(1645.410817318144, within(0.000001));
        assertThat(r.get(b)).isCloseTo(1354.589182681856, within(0.000001));
    }

    @Test
    void wholeEpochBootstrapAndOrderingMatchIndependentTwoEpochFixture() {
        var observations =
                List.of(
                        new TournamentRatings.Observation(0, "one", a, b, 1),
                        new TournamentRatings.Observation(1, "two", a, b, 0));
        var intervals =
                TournamentRatings.intervals(List.of(a, b), observations, List.of(0L, 1L), 123)
                        .orElseThrow();
        assertThat(
                        TournamentRatings.intervals(
                                        List.of(b, a),
                                        List.of(observations.get(1), observations.get(0)),
                                        List.of(1L, 0L),
                                        123)
                                .orElseThrow())
                .isEqualTo(intervals);
        assertThat(intervals.get(a).get(0)).isCloseTo(1311.4192815451158, within(1e-6));
        assertThat(intervals.get(a).get(1)).isCloseTo(1688.5807184548842, within(1e-6));
        assertThat(
                        TournamentRatings.fit(
                                List.of(b, a), List.of(observations.get(1), observations.get(0))))
                .isEqualTo(Map.of(a, 1500.0, b, 1500.0));
        assertThat(
                        TournamentRatings.intervals(
                                List.of(a, b), List.of(observations.get(0)), List.of(0L), 123))
                .isEmpty();
    }

    @Test
    void independentFiveEpochOracleFreezesBootstrapRngAndPercentiles() {
        double[] outcomes = {1, 0, .5, 1, 1};
        var obs = new ArrayList<TournamentRatings.Observation>();
        for (int i = 0; i < 5; i++)
            obs.add(new TournamentRatings.Observation(i, "b" + i, a, b, outcomes[i]));
        var interval =
                TournamentRatings.intervals(List.of(a, b), obs, List.of(0L, 1L, 2L, 3L, 4L), 123)
                        .orElseThrow()
                        .get(a);
        assertThat(interval.get(0)).isCloseTo(1432.39522383201, within(1e-7));
        assertThat(interval.get(1)).isCloseTo(1748.694005372399, within(1e-7));
    }
}
