package org.jskat.ai.mcts;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NodeStatisticsTest {

    @Test
    public void initialUctValue() {

        final NodeStatistics statistics = new NodeStatistics();

        assertThat(statistics.getUctValue(0L), is(Double.MAX_VALUE));
    }

    @Test
    public void addReward() {

        final NodeStatistics statistics = new NodeStatistics();

        statistics.addReward(1.0);

        assertThat(statistics.getUctValue(1L), is(1.0));

        statistics.addReward(1.0);

        assertThat(statistics.getUctValue(2L), is(1.8325546111576978));
    }

    @Test
    public void addNoReward() {

        final NodeStatistics statistics = new NodeStatistics();

        statistics.addReward(0.0);

        assertThat(statistics.getUctValue(1L), is(0.0));

        statistics.addReward(1.0);

        assertThat(statistics.getUctValue(2L), is(1.3325546111576978));
    }

    @Test
    public void addNoRewardOtherOrder() {

        final NodeStatistics statistics = new NodeStatistics();

        statistics.addReward(1.0);

        assertThat(statistics.getUctValue(1L), is(1.0));

        statistics.addReward(0.0);

        assertThat(statistics.getUctValue(2L), is(1.3325546111576978));
    }
}
